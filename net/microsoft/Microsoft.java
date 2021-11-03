package net.microsoft;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import it.amadeus.client.accounts.AdrianAltManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.Session;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
public class Microsoft {
    private static final String msTokenUrl = "https://login.live.com/oauth20_token.srf";

    private static final String authXbl = "https://user.auth.xboxlive.com/user/authenticate";

    private static final String authXsts = "https://xsts.auth.xboxlive.com/xsts/authorize";

    private static final String minecraftAuth = "https://api.minecraftservices.com/authentication/login_with_xbox";

    private static final String minecraftProfile = "https://api.minecraftservices.com/minecraft/profile";

    private static final String clientId = "a9e195ea-5ac3-4777-836a-465f0c928c36";

    private static final String redirectDict = "relogin";

    private static final String redirect = "http://localhost:26669/relogin";

    private static final String msAuthUrl = (new UrlBuilder("https://login.live.com/oauth20_authorize.srf"))
            .addParameter("client_id", "a9e195ea-5ac3-4777-836a-465f0c928c36")
            .addParameter("response_type", "code")
            .addParameter("redirect_uri", "http://localhost:26669/relogin")
            .addParameter("scope", "XboxLive.signin%20offline_access")
            .build();

    private final RequestConfig config = RequestConfig.custom().setConnectTimeout(30000).setSocketTimeout(30000).setConnectionRequestTimeout(30000).build();

    private final CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(this.config).build();

    private boolean isCancelled = false;

    private String errorMsg = null;

    public static void logout() {
        try {
            Desktop.getDesktop().browse(new URI(msAuthUrl));
        } catch (IOException|java.net.URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void login(GuiScreen guiScreen) {
        AdrianAltManager altManager = (AdrianAltManager)guiScreen;
        try {
            String authorizeCode = callIfNotCancelled(this::authorizeUser);
            if (authorizeCode == null) {
                altManager.setStatus("Authorization error on login");
                return;
            }
            altManager.setStatus("Getting token from Microsoft");
            MsToken token = callIfNotCancelled(this::getMsToken, authorizeCode);
            altManager.setStatus("Getting Xbox Live token");
            XblToken xblToken = callIfNotCancelled(this::getXblToken, token.accessToken);
            altManager.setStatus("Logging into Xbox Live");
            XstsToken xstsToken = callIfNotCancelled(this::getXstsToken, xblToken);
            altManager.setStatus("Getting your Minecraft token");
            MinecraftToken profile = callIfNotCancelled(() -> getMinecraftToken(xstsToken, xblToken));
            altManager.setStatus("Loading your profile");
            MinecraftProfile mcProfile = callIfNotCancelled(this::getMinecraftProfile, profile);
            altManager.setStatus("Logging you into Minecraft");
            if (mcProfile != null) {
                (Minecraft.getMinecraft()).session = new Session(mcProfile.name, mcProfile.id, mcProfile.token.accessToken, Session.Type.MOJANG.name());
                altManager.setStatus("Successfully logged in as: " + mcProfile.name);
            }
            if (mcProfile != null) {
                (Minecraft.getMinecraft()).session = new Session(mcProfile.name, mcProfile.id, mcProfile.token.accessToken, Session.Type.MOJANG.name());
                System.out.println("Successfully logged in as: " + mcProfile.name);
            }
            return;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                this.client.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private <T, R> R callIfNotCancelled(Function<T, R> function, T value) {
        if (this.isCancelled)
            return null;
        if (this.errorMsg != null)
            return null;
        return function.apply(value);
    }

    private <T> T callIfNotCancelled(Supplier<T> runnable) {
        if (this.isCancelled)
            return null;
        if (this.errorMsg != null)
            return null;
        return runnable.get();
    }

    public void cancelLogin() {
        this.isCancelled = true;
    }

    private String authorizeUser() {
        try {
            CountDownLatch latch = new CountDownLatch(1);
            HttpServer server = HttpServer.create(new InetSocketAddress(26669), 0);
            AtomicReference<String> msCode = new AtomicReference<>(null);
            server.createContext("/relogin", httpExchange -> {
                String code = httpExchange.getRequestURI().getQuery();
                if (code != null)
                    msCode.set(code.substring(code.indexOf('=') + 1));
                String response = "You can now close your browser.";
                httpExchange.sendResponseHeaders(200, response.length());
                OutputStream stream = httpExchange.getResponseBody();
                stream.write(response.getBytes());
                stream.close();
                latch.countDown();
                server.stop(2);
            });
            server.setExecutor(null);
            server.start();
            Desktop.getDesktop().browse(new URI(msAuthUrl));
            latch.await();
            return msCode.get();
        } catch (Exception e) {
            this.errorMsg = ExceptionUtils.getStackTrace(e);
            return null;
        }
    }

    private MsToken getMsToken(String authorizeCode) {
        try {
            HttpPost post = new HttpPost("https://login.live.com/oauth20_token.srf");
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("client_id", "a9e195ea-5ac3-4777-836a-465f0c928c36"));
            params.add(new BasicNameValuePair("scope", "xboxlive.signin"));
            params.add(new BasicNameValuePair("code", authorizeCode));
            params.add(new BasicNameValuePair("grant_type", "authorization_code"));
            params.add(new BasicNameValuePair("redirect_uri", "http://localhost:26669/relogin"));
            post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            post.setHeader("Accept", "application/x-www-form-urlencoded");
            post.setHeader("Content-type", "application/x-www-form-urlencoded");
            CloseableHttpResponse closeableHttpResponse = this.client.execute(post);
            if (closeableHttpResponse.getEntity() == null)
                throw new RuntimeException("No entity!");
            JsonObject obj = parseObject(EntityUtils.toString(closeableHttpResponse.getEntity()));
            return new MsToken(obj.get("access_token").getAsString(), obj.get("refresh_token").getAsString());
        } catch (Exception e) {
            this.errorMsg = ExceptionUtils.getStackTrace(e);
            return null;
        }
    }

    private XblToken getXblToken(String accessToken) {
        try {
            HttpPost post = new HttpPost("https://user.auth.xboxlive.com/user/authenticate");
            JsonObject obj = new JsonObject();
            JsonObject props = new JsonObject();
            props.addProperty("AuthMethod", "RPS");
            props.addProperty("SiteName", "user.auth.xboxlive.com");
            props.addProperty("RpsTicket", "d=" + accessToken);
            obj.add("Properties", props);
            obj.addProperty("RelyingParty", "http://auth.xboxlive.com");
            obj.addProperty("TokenType", "JWT");
            StringEntity requestEntity = new StringEntity(obj.toString(), ContentType.APPLICATION_JSON);
            post.setEntity(requestEntity);
            CloseableHttpResponse closeableHttpResponse = this.client.execute(post);
            if (closeableHttpResponse.getEntity() == null)
                throw new RuntimeException("No entity!");
            JsonObject responseObj = parseObject(closeableHttpResponse);
            return new XblToken(responseObj.get("Token").getAsString(), responseObj.get("DisplayClaims").getAsJsonObject().get("xui").getAsJsonArray().get(0).getAsJsonObject().get("uhs").getAsString());
        } catch (Exception e) {
            this.errorMsg = ExceptionUtils.getStackTrace(e);
            return null;
        }
    }

    private XstsToken getXstsToken(XblToken xblToken) {
        try {
            HttpPost post = new HttpPost("https://xsts.auth.xboxlive.com/xsts/authorize");
            JsonObject obj = new JsonObject();
            JsonObject props = new JsonObject();
            JsonArray token = new JsonArray();
            JsonElement asd = new JsonParser().parse(xblToken.token);
            token.add(asd);
            props.addProperty("SandboxId", "RETAIL");
            props.add("UserTokens", token);
            obj.add("Properties", props);
            obj.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
            obj.addProperty("TokenType", "JWT");
            StringEntity entity = new StringEntity(obj.toString(), ContentType.APPLICATION_JSON);
            post.setEntity(entity);
            CloseableHttpResponse closeableHttpResponse = this.client.execute(post);
            return new XstsToken(parseObject(closeableHttpResponse).get("Token").getAsString());
        } catch (Exception e) {
            this.errorMsg = ExceptionUtils.getStackTrace(e);
            return null;
        }
    }

    private MinecraftToken getMinecraftToken(XstsToken xstsToken, XblToken xblToken) {
        try {
            HttpPost post = new HttpPost("https://api.minecraftservices.com/authentication/login_with_xbox");
            JsonObject obj = new JsonObject();
            obj.addProperty("identityToken", "XBL3.0 x=" + xblToken.ush + ";" + xstsToken.token);
            StringEntity entity = new StringEntity(obj.toString(), ContentType.APPLICATION_JSON);
            post.setEntity(entity);
            CloseableHttpResponse closeableHttpResponse = this.client.execute(post);
            JsonObject responseObj = parseObject(closeableHttpResponse);
            return new MinecraftToken(responseObj.get("access_token").getAsString());
        } catch (Exception e) {
            this.errorMsg = ExceptionUtils.getStackTrace(e);
            return null;
        }
    }

    private MinecraftProfile getMinecraftProfile(MinecraftToken minecraftToken) {
        try {
            HttpGet get = new HttpGet("https://api.minecraftservices.com/minecraft/profile");
            get.setHeader("Authorization", "Bearer " + minecraftToken.accessToken);
            CloseableHttpResponse closeableHttpResponse = this.client.execute(get);
            JsonObject obj = parseObject(closeableHttpResponse);
            return new MinecraftProfile(obj.get("name").getAsString(), obj.get("id").getAsString(), minecraftToken);
        } catch (Exception e) {
            this.errorMsg = ExceptionUtils.getStackTrace(e);
            return null;
        }
    }

    private JsonObject parseObject(HttpResponse entity) throws IOException {
        return parseObject(EntityUtils.toString(entity.getEntity()));
    }

    private JsonObject parseObject(String str) {
        return (new JsonParser()).parse(str).getAsJsonObject();
    }

    private static class MsToken {
        public String accessToken;

        public String refreshToken;

        public MsToken(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }

    private static class XblToken {
        public String token;

        public String ush;

        public XblToken(String token, String ush) {
            this.token = token;
            this.ush = ush;
        }
    }

    private static class XstsToken {
        public String token;

        public XstsToken(String token) {
            this.token = token;
        }
    }

    public static class MinecraftToken {
        public String accessToken;

        public MinecraftToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }

    private static class UrlBuilder {
        private final String url;

        private final Map<String, Object> parameters = new HashMap<>();

        public UrlBuilder(String url) {
            this.url = url;
        }

        public UrlBuilder addParameter(String key, Object value) {
            this.parameters.put(key, value);
            return this;
        }

        public String build() {
            StringBuilder builder = new StringBuilder();
            builder.append(this.url);
            if (!this.parameters.isEmpty())
                builder.append("?");
            for (Map.Entry<String, Object> entry : this.parameters.entrySet())
                builder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            builder.setLength(builder.length() - 1);
            return builder.toString();
        }
    }

    public static class MinecraftProfile {
        public String name;

        public String id;

        public Microsoft.MinecraftToken token;

        public MinecraftProfile(String name, String id, Microsoft.MinecraftToken token) {
            this.name = name;
            this.id = id;
            this.token = token;
        }
    }
}