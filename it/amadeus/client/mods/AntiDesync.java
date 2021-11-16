package it.amadeus.client.mods;

import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.PacketReceive;
import it.amadeus.client.event.events.PostMotion;
import it.amadeus.client.event.events.PreMotion;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.ChatUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;

public final class AntiDesync extends Module {

    public static boolean isDesynced;
    public static boolean everGotC0f;
    private int lastTicks;
    private int lastC0fTicks;
    private int lastInventoryTicks;

    public static void swap(int slot, int hotBarNumber) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hotBarNumber, 1, mc.thePlayer);
    }

    @Override
    public String getName() {
        return "AntiDesync";
    }

    @Override
    public String getDescription() {
        return "sex";
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
        if (mc.thePlayer == null || mc.theWorld == null || mc.thePlayer.ticksExisted < 10) {
            everGotC0f = false;
            isDesynced = false;
            this.lastTicks = 0;
            return;
        }
        if (event instanceof PreMotion) {
            if (this.lastInventoryTicks > 1)
                this.lastInventoryTicks--;
            if (this.lastC0fTicks > 0)
                this.lastC0fTicks--;
        }
        if (event instanceof PreMotion || event instanceof PostMotion)
            if (this.lastC0fTicks == 0 && everGotC0f) {
                isDesynced = true;
                this.lastTicks++;
                mc.gameSettings.keyBindForward = false;
                mc.gameSettings.keyBindLeft.pressed = false;
                mc.gameSettings.keyBindRight.pressed = false;
                mc.gameSettings.keyBindBack.pressed = false;
                this.lastInventoryTicks = 1;
                ChatUtil.print("DESYNCED! - PLEASE WAIT / LEAVE IF THIS KEEPS SPAMMING TO PREVENT STAFF ALERTS");
            } else {
                if (this.lastTicks > 0) {
                    if (Keyboard.isKeyDown(Keyboard.KEY_W))
                        mc.gameSettings.keyBindForward = true;
                    if (Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.keyCode))
                        mc.gameSettings.keyBindLeft.pressed = true;
                    if (Keyboard.isKeyDown(mc.gameSettings.keyBindRight.keyCode))
                        mc.gameSettings.keyBindRight.pressed = true;
                    if (Keyboard.isKeyDown(mc.gameSettings.keyBindBack.keyCode))
                        mc.gameSettings.keyBindBack.pressed = true;
                    this.lastTicks = 0;
                }
                isDesynced = false;
            }
        if (event instanceof PacketReceive) {
            Packet<?> packet = ((PacketReceive) event).getPacket();
            if (packet instanceof net.minecraft.network.play.server.S32PacketConfirmTransaction) {
                this.lastC0fTicks = 10;
                if (mc.currentScreen == null && !everGotC0f && this.lastInventoryTicks == 1)
                    everGotC0f = true;
                if (mc.currentScreen != null)
                    this.lastInventoryTicks = 100;
            }
            if (packet instanceof S1CPacketEntityMetadata) {
                S1CPacketEntityMetadata s1CPacketEntityMetadata = (S1CPacketEntityMetadata) packet;
                if (s1CPacketEntityMetadata.getEntityId() == mc.thePlayer.getEntityId()) ;
            }
        }
    }

    public static class BlockData {
        public EnumFacing face;

        public BlockPos position;

        public BlockData(BlockPos position, EnumFacing face) {
            this.position = position;
            this.face = face;
        }
    }

}
