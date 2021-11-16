package it.amadeus.client.mods;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.PacketReceive;
import it.amadeus.client.event.events.Update;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.ChatUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;

public final class Fucker extends Module {

    private boolean canBreak;

    @Override
    public String getName() {
        return "Breaker";
    }

    @Override
    public String getDescription() {
        return "Spacca Il Letto nelle bed";
    }

    @Override
    public int getKey() {
        return 0;
    }

    @Override
    public Category getCategory() {
        return Category.FUN;
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof Update){
            if(canBreak && !AntiDesync.isDesynced){
                for (int x = -5; x < 5; x++) {
                    for (int y2 = 5; y2 > -5; y2--) {
                        for (int z = -5; z < 5; z++) {
                            int xPos = (int) mc.thePlayer.posX + x;
                            int yPos = (int) mc.thePlayer.posY + y2;
                            int zPos = (int) mc.thePlayer.posZ + z;
                            BlockPos blockPos = new BlockPos(xPos, yPos, zPos);
                            Block block = mc.theWorld.getBlockState(blockPos).getBlock();
                            if (block == Blocks.bed) {
                                sendDirect(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                                sendDirect(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                                sendDirect(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                                mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                            }
                        }
                    }
                }
            }
        }
        if(event instanceof PacketReceive){
            Packet<?> packet = ((PacketReceive) event).getPacket();
            if(packet instanceof S02PacketChat){
                S02PacketChat packetChat = (S02PacketChat) packet;
                if(packetChat.getChatComponent().getFormattedText().equals("§r§cYou cannot destroy your own teams bed!§r")) {
                    canBreak = false;
                }
            }else{
                canBreak = true;
            }
        }
    }

    private void sendDirect(Packet<?> p) {
        mc.getNetHandler().getNetworkManager().sendPacket(p, null, (GenericFutureListener<? extends Future<? super Void>>) null);
    }
}
