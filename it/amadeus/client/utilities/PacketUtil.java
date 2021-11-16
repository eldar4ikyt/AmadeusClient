package it.amadeus.client.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

public class PacketUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void sendPacket(Packet<?> packet) {
        mc.getNetHandler().addToSendQueue(packet);
    }

    public static void sendPacketSilent(Packet<?> packet) {
        mc.thePlayer.sendQueue.silentQueue(packet);
    }

    public static void sendC04(double x, double y, double z, boolean ground, boolean silent) {
        if (silent) {
            sendPacketSilent(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, ground));
        } else {
            sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, ground));
        }
    }
}
