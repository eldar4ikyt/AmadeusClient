package it.amadeus.client.utilities;

import it.amadeus.client.event.events.PostMotion;
import it.amadeus.client.event.events.PreMotion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class RotationUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static float[] mouseFix(float yaw, float pitch) {
        float k = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float k1 = k * k * k * 8.0F;
        yaw -= yaw % k1;
        pitch -= pitch % k1;
        return new float[]{yaw, pitch};
    }

    public static float[] faceBlock(BlockPos pos, boolean scaffoldFix, float currentYaw, float currentPitch, float speed) {
        double x = pos.getX() + (scaffoldFix ? 0.5D : 0.0D) - mc.thePlayer.posX;
        double y = pos.getY() - (scaffoldFix ? 1.75D : 0.0D) - mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
        double z = pos.getZ() + (scaffoldFix ? 0.5D : 0.0D) - mc.thePlayer.posZ;
        double calculate = MathHelper.sqrt_double(x * x + z * z);
        float calcYaw = (float) (MathHelper.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float calcPitch = (float) -(MathHelper.atan2(y, calculate) * 180.0D / Math.PI);
        float finalPitch = (calcPitch >= 90.0F) ? 90.0F : calcPitch;
        float yaw = updateRotation(currentYaw, calcYaw, speed);
        float pitch = updateRotation(currentPitch, finalPitch, speed);
        float sense = mc.gameSettings.mouseSensitivity * 0.8F + 0.2F;
        float fix = (float) (Math.pow(sense, 3.0D) * 1.5D);
        yaw -= yaw % fix;
        pitch -= pitch % fix;
        return new float[]{yaw, pitch};
    }

    public static float[] getRotationFromPosition(double x, double y, double z, float angle, boolean doSmooth) {
        double xDiff = x - mc.thePlayer.posX;
        double yDiff = y - mc.thePlayer.posY - 1.2D;
        double zDiff = z - mc.thePlayer.posZ;
        double dist = Math.hypot(xDiff, zDiff);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / Math.PI);
        if (doSmooth) {
            yaw = RotationUtils.updateRotation(mc.thePlayer.rotationYaw, yaw, angle);
            pitch = RotationUtils.updateRotation(mc.thePlayer.rotationPitch, pitch, angle);
        }
        return new float[]{yaw, pitch};
    }

    public static double[] getRotationsS2(double posX, double posY, double posZ) {
        EntityPlayerSP player = mc.thePlayer;
        double x = posX - player.posX;
        double y = posY - (player.posY + (double) player.getEyeHeight());
        double z = posZ - player.posZ;
        double dist = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitch = (float) (-(Math.atan2(y, dist) * 180.0D / 3.141592653589793D));
        return new double[]{yaw, pitch};
    }


    public static float[] getRotationsS(double posX, double posY, double posZ) {
        EntityPlayerSP player = mc.thePlayer;
        double x = posX - player.posX;
        double y = posY - (player.posY + (double) player.getEyeHeight());
        double z = posZ - player.posZ;
        double dist = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitch = (float) (-(Math.atan2(y, dist) * 180.0D / 3.141592653589793D));
        return new float[]{yaw, pitch};
    }

    public static float[] getRotationsEntity(EntityLivingBase entity) {
        return mc.thePlayer.isMoving() ? getRotationsS(entity.posX + randomNumber(0.03D, -0.03D), entity.posY + (double) entity.getEyeHeight() - 0.4D + randomNumber(0.07D, -0.07D), entity.posZ + randomNumber(0.03D, -0.03D)) : getRotationsS(entity.posX, entity.posY + (double) entity.getEyeHeight() - 0.4D, entity.posZ);
    }

    public static double[] getRotationsEntity2(EntityLivingBase entity) {
        return mc.thePlayer.isMoving() ? getRotationsS2(entity.posX + randomNumber(0.03D, -0.03D), entity.posY + (double) entity.getEyeHeight() - 0.4D + randomNumber(0.07D, -0.07D), entity.posZ + randomNumber(0.03D, -0.03D)) : getRotationsS2(entity.posX, entity.posY + (double) entity.getEyeHeight() - 0.4D, entity.posZ);
    }

    public static double randomNumber(double max, double min) {
        return Math.random() * (max - min) + min;
    }

    public static float[] getRotations(EntityLivingBase ent, float angle, boolean doSmooth) {
        double x = ent.posX;
        double y = ent.posY + (ent.getEyeHeight() / 2.0F);
        double z = ent.posZ;
        return getRotationFromPosition(x, y, z, angle, doSmooth);
    }

    private static float updateRotation(float inizio, float arrivo, float angle) {
        float f = MathHelper.wrapAngleTo180_float(arrivo - inizio);

        if (f > angle) {
            f = angle;
        }

        if (f < -angle) {
            f = -angle;
        }

        return inizio + f;
    }

    public static double[] getRotationFromPosition2(double x, double y, double z, float angle, boolean doSmooth) {
        double xDiff = x - mc.thePlayer.posX;
        double yDiff = y - mc.thePlayer.posY - 1.2D;
        double zDiff = z - mc.thePlayer.posZ;
        double dist = Math.hypot(xDiff, zDiff);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / Math.PI);
        if (doSmooth) {
            yaw = RotationUtils.updateRotation(mc.thePlayer.rotationYaw, yaw, angle);
            pitch = RotationUtils.updateRotation(mc.thePlayer.rotationPitch, pitch, angle);
        }
        return new double[]{yaw, pitch};
    }

    public static double[] getRotations2(EntityLivingBase ent, float angle, boolean doSmooth) {
        double x = ent.posX;
        double y = ent.posY + (ent.getEyeHeight() / 2.0F);
        double z = ent.posZ;
        return getRotationFromPosition2(x, y, z, angle, doSmooth);
    }

}