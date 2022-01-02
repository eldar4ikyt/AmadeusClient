import net.minecraft.client.main.Main;

import java.io.File;
import java.util.Arrays;

public class Start {
    public static void main(String[] args) {
        try {
            File[] files = new File("C:\\Users\\user\\Desktop\\VinnyHub Client\\jars\\versions\\1.8.8\\1.8.8-natives").listFiles();

            for (File file : files) {
                try {
                    System.load(file.getAbsolutePath());
                } catch (UnsatisfiedLinkError ex) {
                    System.out.println("Dll bits doesnt match with system ones. Skipping.");
                }
            }

        } catch (Throwable throwable) {
            System.out.println("Cant load natives, skipping.");
            throwable.printStackTrace();
        }

        Main.main(concat(new String[]{"--version", "mcp", "--accessToken", "0", "--assetsDir", "assets", "--assetIndex", "1.8", "--userProperties", "{}"}, args));
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
