package it.amadeus.client.event.events;

import it.amadeus.client.event.Cancellable;
import it.amadeus.client.event.Event;
import net.minecraft.client.Minecraft;

public final class PreMotion extends Event implements Cancellable {

    public double x, y, z;

    public float yaw, pitch;

    private boolean cancel, onGround;

    private final Minecraft mc = Minecraft.getMinecraft();

    /**
     * Introducing the cancellable pre motion event
     * @param yaw - Entity yaw
     * @param pitch - Entity pitch
     * @param x - Entity posX
     * @param y - Entity posY
     * @param z - Entity posZ
     * @param onGround - boolean Onground
     */

    public PreMotion(final float yaw, final float pitch, final double x, final double y, final double z, final boolean onGround) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.x = x;
        this.y = y;
        this.z = z;
        this.onGround = onGround;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public void setOnGround(final boolean onGround) {
        this.onGround = onGround;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(final float yaw) {
        this.yaw = yaw;
        mc.thePlayer.rotationYawHead = yaw;
        mc.thePlayer.renderYawOffset = yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(final float pitch) {
        this.pitch = pitch;
        mc.thePlayer.rotationPitchHead = pitch;
        mc.thePlayer.renderPitchHead = pitch;
    }

    public double getX() {
        return this.x;
    }

    public void setX(final double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(final double y) {
        this.y = y;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(final double z) {
        this.z = z;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.cancel = cancel;
    }
}
