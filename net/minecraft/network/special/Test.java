package net.minecraft.network.special;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class Test {

    private static final AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
    private static SourceDataLine sourceDataLine;

    public static void main(String[] args) {
        //questo è un semplice test di norma il packet funzionerebbe cosi

        //packet c99mantex manda -> getAudioFromMicAndConvertInBytes()

        //il server inoltra il contenuto del c99mantex al giocatore più vicino

        //il client del giocatore più vicino legge il packet del server es. S99Mantex e riproduce il suono usando questo metodo  reproduce(byte[]);

        reproduce(getAudioFromMicAndConvertInBytes());
    }

    //questo lo fai mandare al packet C99Mantex Al Server che Lo rigira al client, che lo riprocessa prenendo i byte in forward dal server per processarli in reproduce(byte[])
    private static byte[] getAudioFromMicAndConvertInBytes() {
        TargetDataLine microphone;
        byte[] audioData = new byte[1000];
        try {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int numBytesRead;
            int CHUNK_SIZE = 1024;
            byte[] data = new byte[microphone.getBufferSize() / 5];
            microphone.start();
            int bytesRead = 0;
            try {
                while (bytesRead < 100000) {
                    numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
                    bytesRead = bytesRead + numBytesRead;
                    out.write(data, 0, numBytesRead);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            microphone.close();
            audioData = out.toByteArray();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        return audioData;
    }

    private static void reproduce(byte... audioData) {
        InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
        AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream, format, audioData.length / format.getFrameSize());
        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
        int cnt = 0;
        byte[] tempBuffer = new byte[8000];
        try {
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(format);
            sourceDataLine.start();
            while ((cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1) {
                if (cnt > 0) {
                    sourceDataLine.write(tempBuffer, 0, cnt);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sourceDataLine.drain();
        sourceDataLine.close();
    }
}