package it.amadeus.client.mods;

import com.sun.net.httpserver.HttpServer;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.Overlay;
import it.amadeus.client.event.events.Update;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.FileUtil;
import it.amadeus.client.utilities.TimerUtil;
import lombok.SneakyThrows;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.apache.hc.core5.http.ParseException;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Spotify extends Module {

    public static final String NAME = "Spotify";

    private static final String CLIENT_ID = "9a05b87f5f3c4a5a8ab6394237d28697";

    private static final URI REDIRECT_URI = SpotifyHttpManager.makeUri("http://localhost:42069");

    private static final String CHALLENGE = "w6iZIj99vHGtEx_NVl9u3sthTN646vvkiP8OMCGfPmo";

    private static final String CODE_VERIFIER = "NlJx4kD4opk4HY7zBM6WfUHxX7HoF8A2TUhOIPGA74w";

    private static final SpotifyApi api = SpotifyApi.builder().setClientId("9a05b87f5f3c4a5a8ab6394237d28697").setRedirectUri(REDIRECT_URI).build();

    private static final AuthorizationCodeUriRequest authorizationCodeUriRequest = api.authorizationCodePKCEUri("w6iZIj99vHGtEx_NVl9u3sthTN646vvkiP8OMCGfPmo").scope("user-read-playback-state user-read-currently-playing user-modify-playback-state streaming user-read-private").build();

    protected static String previousSong;
    protected static Track currentlyPlaying;
    protected static ResourceLocation coverImage;
    protected static BufferedImage coverImageBuffer;
    protected static Boolean playing;
    private final TimerUtil timeHelper = new TimerUtil();
    private float widht;

    private static void update() {
        (new Thread("Spotify Updater Thread") {
            @SneakyThrows
            public void run() {
                try {
                    CurrentlyPlayingContext context = Spotify.api.getInformationAboutUsersCurrentPlayback().build().execute();
                    if (context == null) return;
                    Spotify.currentlyPlaying = (Track) context.getItem();
                    if (Spotify.currentlyPlaying != null) {
                        if (Spotify.previousSong == null)
                            Spotify.previousSong = "";
                        if (!Spotify.previousSong.equals(Spotify.currentlyPlaying.getId())) {
                            Spotify.coverImageBuffer = ImageIO.read(new URL(Spotify.currentlyPlaying.getAlbum().getImages()[0].getUrl()));
                            mc.addScheduledTask(() -> {
                                DynamicTexture dynamicTexture = new DynamicTexture(Spotify.coverImageBuffer);
                                Spotify.coverImage = mc.getTextureManager().getDynamicTextureLocation("cover.jpg", dynamicTexture);
                            });
                            previousSong = Spotify.currentlyPlaying.getId();
                        }
                        playing = context.getIs_playing();
                    } else {
                        coverImageBuffer = null;
                        coverImage = null;
                        previousSong = "";
                        playing = null;
                    }
                } catch (IOException | SpotifyWebApiException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public String getName() {
        return "Spotify";
    }

    @Override
    public String getDescription() {
        return "Mostra i Dati di Spotify";
    }

    @Override
    public int getKey() {
        return 0;
    }

    @Override
    public Category getCategory() {
        return Category.RENDER;
    }

    private void setupApi(String code) {
        try {
            AuthorizationCodeCredentials credentials = api.authorizationCodePKCE(code, "NlJx4kD4opk4HY7zBM6WfUHxX7HoF8A2TUhOIPGA74w").build().execute();
            api.setAccessToken(credentials.getAccessToken());
            api.setRefreshToken(credentials.getRefreshToken());
            final int time = credentials.getExpiresIn() - 30;
            (new Thread("Spotify Token Renewer") {
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(time);
                        AuthorizationCodeCredentials credentials1 = Spotify.api.authorizationCodePKCERefresh().build().execute();
                        Spotify.api.setAccessToken(credentials1.getAccessToken());
                        Spotify.api.setRefreshToken(credentials1.getRefreshToken());
                    } catch (IOException | InterruptedException | SpotifyWebApiException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }

    @Override
    public void onEnable() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(42069), 0);
            server.createContext("/", httpExchange -> {
                httpExchange.sendResponseHeaders(302, 0L);
                String code = queryToMap(httpExchange.getRequestURI().getQuery()).get("code");
                if (code != null) {
                    server.stop(0);
                    setupApi(code);
                    FileUtil.saveString2(code);
                }
            });
            server.start();
            try {
                Desktop.getDesktop().browse(authorizationCodeUriRequest.execute());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onEnable();
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof Update) {
            if (this.timeHelper.track(3300.0D)) {
                update();
                this.timeHelper.reset();
            }
        }
        if (event instanceof Overlay) {
            GL11.glPushMatrix();
            ScaledResolution scaledResolution = new ScaledResolution(mc);
            int x = scaledResolution.getScaledWidth() / 2;
            int y = 5;
            if (coverImage != null && coverImageBuffer != null) {
                GlStateManager.color(1,1,1,1);
                mc.getTextureManager().bindTexture(coverImage);
                Gui.drawScaledCustomSizeModalRect((int) (x - widht + 120), 5, 0.0F, 0.0F, 32, 32, 32, 32, 32.0F, 32.0F);
                GlStateManager.color(1,1,1,1);
            }
            if (currentlyPlaying != null) {
                String authorList;
                if ((currentlyPlaying.getArtists()).length == 1) {
                    widht = (90 + mc.getAmadeus().getFontManager().comfortaa16.getStringWidth(currentlyPlaying.getArtists()[0].getName()));
                }
                if ((currentlyPlaying.getArtists()).length == 2) {
                    widht = (90 + mc.getAmadeus().getFontManager().comfortaa16.getStringWidth(currentlyPlaying.getArtists()[0].getName()) + mc.getAmadeus().getFontManager().comfortaa16.getStringWidth(currentlyPlaying.getArtists()[1].getName()));
                }
                if ((currentlyPlaying.getArtists()).length == 3) {
                    widht = (90 + mc.getAmadeus().getFontManager().comfortaa16.getStringWidth(currentlyPlaying.getArtists()[0].getName()) + mc.getAmadeus().getFontManager().comfortaa16.getStringWidth(currentlyPlaying.getArtists()[1].getName()) + mc.getAmadeus().getFontManager().comfortaa16.getStringWidth(currentlyPlaying.getArtists()[2].getName()));
                }
                if ((currentlyPlaying.getArtists()).length == 4) {
                    widht = (120 + mc.getAmadeus().getFontManager().comfortaa16.getStringWidth(currentlyPlaying.getArtists()[0].getName()) + mc.getAmadeus().getFontManager().comfortaa16.getStringWidth(currentlyPlaying.getArtists()[1].getName()) + mc.getAmadeus().getFontManager().comfortaa16.getStringWidth(currentlyPlaying.getArtists()[2].getName()) + mc.getAmadeus().getFontManager().comfortaa16.getStringWidth(currentlyPlaying.getArtists()[3].getName()));
                }

                mc.getAmadeus().getFontManager().comfortaa16.drawStringWithShadow(currentlyPlaying.getName(), (int) (x - widht + 154), 12, Color.WHITE.getRGB());
                if ((currentlyPlaying.getArtists()).length == 1) {
                    authorList = currentlyPlaying.getArtists()[0].getName();
                } else if ((currentlyPlaying.getArtists()).length == 2) {
                    authorList = currentlyPlaying.getArtists()[0].getName() + " and " + currentlyPlaying.getArtists()[1].getName();
                } else {
                    StringBuilder authors = new StringBuilder();
                    int index = 0;
                    for (ArtistSimplified author : currentlyPlaying.getArtists()) {
                        if (index == (currentlyPlaying.getArtists()).length - 1) {
                            authors.append(" and ").append(author.getName());
                        } else if (index == (currentlyPlaying.getArtists()).length - 2) {
                            authors.append(author.getName());
                        } else {
                            authors.append(author.getName()).append(", ");
                        }
                        index++;
                    }
                    authorList = authors.toString();
                }
                mc.getAmadeus().getFontManager().comfortaa16.drawStringWithShadow("By " + authorList, (int) (x - widht + 154), 25.0F, Color.WHITE.getRGB());
            }
            GL11.glPopMatrix();
        }
    }
}
