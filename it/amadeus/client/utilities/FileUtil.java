package it.amadeus.client.utilities;

import net.minecraft.client.Minecraft;

import java.io.*;

public class FileUtil {

    private static String mainPath = (Minecraft.getMinecraft()).mcDataDir + "/" + "Amadeus";

    public static String LoadSpotify() throws IOException {
        if (!(new File(mainPath)).exists())
            (new File(mainPath)).mkdir();
        if (!(new File(mainPath + "/Spotify")).exists())
            (new File(mainPath + "/Spotify")).createNewFile();
        BufferedReader reader = new BufferedReader(new FileReader(mainPath + "/Spotify"));
        return reader.readLine();
    }

    public static void saveString2(String text) throws IOException {
        if (!(new File(mainPath)).exists())
            (new File(mainPath)).mkdir();
        if (!(new File(mainPath + "/Spotify")).exists())
            (new File(mainPath + "/Spotify")).createNewFile();
        FileWriter writer = new FileWriter(new File(mainPath + "/Spotify"));
        writer.write(text);
        writer.flush();
        writer.close();
    }
}
