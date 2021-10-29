package it.amadeus.client.utilities;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import it.amadeus.client.event.events.MoveFlying;
import it.amadeus.client.event.events.Moving;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;

public final class MotionUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void sendDirect(Packet<?> p) {
        mc.getNetHandler().getNetworkManager().sendPacket(p, null, (GenericFutureListener<? extends Future<? super Void>>) null);
    }

    public static void strafe(final float speed,float rotationYaw, boolean bypass) {
        final double yaw = getDirection(rotationYaw);
        if(bypass){
            mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput());
        }
        mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }


    public static double getDirection(float rotationYaw) {

        if (mc.thePlayer.moveForward < 0F)
            rotationYaw += 180F;

        float forward = 1F;
        if (mc.thePlayer.moveForward < 0F)
            forward = -0.5F;
        else if (mc.thePlayer.moveForward > 0F)
            forward = 0.5F;

        if (mc.thePlayer.moveStrafing > 0F)
            rotationYaw -= 90F * forward;

        if (mc.thePlayer.moveStrafing < 0F)
            rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }

    public static void legitStrafeMovement(MoveFlying event, float yaw) {
        float strafe = event.getStrafe();
        float friction = event.getFriction();
        float forward = event.getForward();

        float speed = strafe * strafe + forward * forward;

        if (speed >= 1.0E-4F) {
            speed = MathHelper.sqrt_float(speed);

            if (speed < 1.0F) speed = 1.0F;

            speed = friction / speed;
            strafe *= speed;
            forward *= speed;

            float yawSin = MathHelper.sin((float) (yaw * Math.PI / 180F));
            float yawCos = MathHelper.cos((float) (yaw * Math.PI / 180F));

            mc.thePlayer.motionX += strafe * yawCos - forward * yawSin;
            mc.thePlayer.motionZ += forward * yawCos + strafe * yawSin;
        }

        event.setCancelled(true);
    }

    public static void damage() {
        if (mc.thePlayer.onGround) {
            double x = mc.thePlayer.posX;
            double y = mc.thePlayer.posY;
            double z = mc.thePlayer.posZ;
            for (int i = 0; i < 9; i++) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.42F, z, false));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 1.23E-6, z, false));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(false));
            }
            mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput());
        }
    }

    public static float getDistanceToGround(Entity e) {
        if (mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround) {
            return 0.0F;
        }
        for (float a = (float) e.posY; a > 0.0F; a -= 1.0F) {
            int[] stairs = {53, 67, 108, 109, 114, 128, 134, 135, 136, 156, 163, 164, 180};
            int[] exemptIds = {
                    6, 27, 28, 30, 31, 32, 37, 38, 39, 40, 50, 51, 55, 59,
                    63, 65, 66, 68, 69, 70, 72, 75, 76, 77, 83, 92, 93, 94,
                    104, 105, 106, 115, 119, 131, 132, 143, 147, 148, 149, 150,
                    157, 171, 175, 176, 177};
            Block block = mc.theWorld.getBlockState(new BlockPos(e.posX, a - 1.0F, e.posZ)).getBlock();
            if (!(block instanceof BlockAir)) {
                if ((Block.getIdFromBlock(block) == 44) || (Block.getIdFromBlock(block) == 126)) {
                    return Math.max((float) (e.posY - a - 0.5D), 0.0F);
                }
                int[] arrayOfInt1;
                int j = (arrayOfInt1 = stairs).length;
                for (int i = 0; i < j; i++) {
                    int id = arrayOfInt1[i];
                    if (Block.getIdFromBlock(block) == id) {
                        return Math.max((float) (e.posY - a - 1.0D), 0.0F);
                    }
                }
                j = (arrayOfInt1 = exemptIds).length;
                for (int i = 0; i < j; i++) {
                    int id = arrayOfInt1[i];
                    if (Block.getIdFromBlock(block) == id) {
                        return Math.max((float) (e.posY - a), 0.0F);
                    }
                }
                return (float) (e.posY - a + block.getBlockBoundsMaxY() - 1.0D);
            }
        }
        return 0.0F;
    }

    public static double getMotion(float baseMotionY) {
        Potion potion = Potion.jump;

        if (mc.thePlayer.isPotionActive(potion)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(potion).getAmplifier();
            baseMotionY += (amplifier + 1) * 0.1F;
        }

        return baseMotionY;
    }

    /**
     * @param event
     * @param moveSpeed
     * @author aristhena
     */
    public static void setMotion(Moving event, float moveSpeed) {
        MovementInput movementInput = mc.thePlayer.movementInput;
        double moveForward = movementInput.moveForward;
        double moveStrafe = movementInput.moveStrafe;
        double rotationYaw = mc.thePlayer.rotationYaw;
        if (moveForward == 0.0D && moveStrafe == 0.0D) {
            event.setX(0.0D);
            event.setZ(0.0D);
        } else {
            if (moveStrafe > 0) {
                moveStrafe = 1;
            } else if (moveStrafe < 0) {
                moveStrafe = -1;
            }
            if (moveForward != 0.0D) {
                if (moveStrafe > 0.0D) {
                    rotationYaw += (moveForward > 0.0D ? -45 : 45);
                } else if (moveStrafe < 0.0D) {
                    rotationYaw += (moveForward > 0.0D ? 45 : -45);
                }
                moveStrafe = 0.0D;
                if (moveForward > 0.0D) {
                    moveForward = 1.0D;
                } else if (moveForward < 0.0D) {
                    moveForward = -1.0D;
                }
            }
            double cos = Math.cos(Math.toRadians(rotationYaw + 90.0F));
            double sin = Math.sin(Math.toRadians(rotationYaw + 90.0F));
            event.setX(moveForward * moveSpeed * cos
                    + moveStrafe * moveSpeed * sin);
            event.setZ(moveForward * moveSpeed * sin
                    - moveStrafe * moveSpeed * cos);
        }
    }

    public static void setMotion(float moveSpeed) {
        MovementInput movementInput = mc.thePlayer.movementInput;
        double moveForward = movementInput.moveForward;
        double moveStrafe = movementInput.moveStrafe;
        double rotationYaw = mc.thePlayer.rotationYaw;
        if (moveForward == 0.0D && moveStrafe == 0.0D) {
            mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
        } else {
            if (moveStrafe > 0) {
                moveStrafe = 1;
            } else if (moveStrafe < 0) {
                moveStrafe = -1;
            }
            if (moveForward != 0.0D) {
                if (moveStrafe > 0.0D) {
                    rotationYaw += (moveForward > 0.0D ? -45 : 45);
                } else if (moveStrafe < 0.0D) {
                    rotationYaw += (moveForward > 0.0D ? 45 : -45);
                }
                moveStrafe = 0.0D;
                if (moveForward > 0.0D) {
                    moveForward = 1.0D;
                } else if (moveForward < 0.0D) {
                    moveForward = -1.0D;
                }
            }
            double cos = Math.cos(Math.toRadians(rotationYaw + 90.0F));
            double sin = Math.sin(Math.toRadians(rotationYaw + 90.0F));
            mc.thePlayer.motionX = moveForward * moveSpeed * cos
                    + moveStrafe * moveSpeed * sin;
            mc.thePlayer.motionZ = moveForward * moveSpeed * sin
                    - moveStrafe * moveSpeed * cos;
        }
    }

    /**
     * @param baseJumpHeight base jump height e.g. 0.42D
     * @return {@code baseJumpHeight} multiplied by jump boost modifier
     */

    public static double getJumpBoostModifier(double baseJumpHeight) {
        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            baseJumpHeight += (float) (amplifier + 1) * 0.1F;
        }

        return baseJumpHeight;
    }

    /**
     * Gets the movement speed of the {@code EntityPlayerSP}
     *
     * @return the base speed
     */
    public static float getBaseSpeed() {
        float baseSpeed = 0.2873F;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amp = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0F + 0.2F * (amp + 1);
        }
        return baseSpeed;
    }
}