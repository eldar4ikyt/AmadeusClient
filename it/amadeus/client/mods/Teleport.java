package it.amadeus.client.mods;

import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.Render3D;
import it.amadeus.client.event.events.Update;
import it.amadeus.client.module.Module;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public final class Teleport extends Module {

    public static Color glColor(int hex) {
        float alpha = (hex >> 24 & 0xFF) / 256.0F;
        float red = (hex >> 16 & 0xFF) / 255.0F;
        float green = (hex >> 8 & 0xFF) / 255.0F;
        float blue = (hex & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
        return new Color(red, green, blue, alpha);
    }

    public static int getColor(Color color) {
        return getColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static int getColor(int brightness) {
        return getColor(brightness, brightness, brightness, 255);
    }

    public static int getColor(int brightness, int alpha) {
        return getColor(brightness, brightness, brightness, alpha);
    }

    public static int getColor(int red, int green, int blue) {
        return getColor(red, green, blue, 255);
    }

    public static int getColor(int red, int green, int blue, int alpha) {
        int color = 0;
        color |= alpha << 24;
        color |= red << 16;
        color |= green << 8;
        color |= blue;
        return color;
    }

    public static void pre3D() {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDisable(2896);
        GL11.glDepthMask(false);
        GL11.glHint(3154, 4354);
    }

    public static void post3D() {
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public String getName() {
        return "Teleport";
    }

    @Override
    public String getDescription() {
        return "Ti Teleporta sul blocco selezionato";
    }

    @Override
    public int getKey() {
        return 0;
    }

    @Override
    public Category getCategory() {
        return Category.MOVEMENTS;
    }

    @Override
    public void onEvent(Event event) {
        MovingObjectPosition ray = rayTrace(17.0D);
        if (event instanceof Render3D) {
            if (ray == null) return;
            double x = ray.getBlockPos().getX() + 0.5D;
            double y = (ray.getBlockPos().getY() + 1);
            double z = ray.getBlockPos().getZ() + 0.5D;
            drawBlock(new Vec3(x, y, z));
        }
        if (event instanceof Update) {
            if (ray == null)
                return;
            if (Mouse.isButtonDown(1)) {
                double x_new = ray.getBlockPos().getX() + 0.5D;
                double y_new = (ray.getBlockPos().getY() + 1);
                double z_new = ray.getBlockPos().getZ() + 0.5D;
                double distance = mc.thePlayer.getDistance(x_new, y_new, z_new);
                for (double d = 0.0D; d < distance; d += 2.0D)
                    setPos(mc.thePlayer.posX + (x_new - mc.thePlayer.getHorizontalFacing().getFrontOffsetX() - mc.thePlayer.posX) * d / distance, mc.thePlayer.posY + (y_new - mc.thePlayer.posY) * d / distance, mc.thePlayer.posZ + (z_new - mc.thePlayer.getHorizontalFacing().getFrontOffsetZ() - mc.thePlayer.posZ) * d / distance);
                mc.renderGlobal.loadRenderers();
            }
        }
    }

    public MovingObjectPosition rayTrace(double blockReachDistance) {
        Vec3 vec3 = mc.thePlayer.getPositionEyes(1.0F);
        Vec3 vec4 = mc.thePlayer.getLookVec();
        Vec3 vec5 = vec3.addVector(vec4.xCoord * blockReachDistance, vec4.yCoord * blockReachDistance, vec4.zCoord * blockReachDistance);
        return mc.theWorld.rayTraceBlocks(vec3, vec5, !mc.thePlayer.isInWater(), false, false);
    }

    public void setPos(double x, double y, double z) {
        mc.thePlayer.setPosition(x, y, z);
    }

    private void drawBlock(Vec3 vec) {
        double x = vec.xCoord - RenderManager.renderPosX;
        double y = vec.yCoord - RenderManager.renderPosY;
        double z = vec.zCoord - RenderManager.renderPosZ;
        double width = 0.3D;
        double height = mc.thePlayer.getEyeHeight();
        pre3D();
        GL11.glLoadIdentity();
        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
        int[] colors = {getColor(Color.YELLOW), getColor(Color.RED)};
        for (int i = 0; i < 2; i++) {
            glColor(colors[i]);
            GL11.glLineWidth((3 - i * 2));
            GL11.glBegin(3);
            GL11.glVertex3d(x - width, y, z - width);
            GL11.glVertex3d(x - width, y, z - width);
            GL11.glVertex3d(x - width, y + height, z - width);
            GL11.glVertex3d(x + width, y + height, z - width);
            GL11.glVertex3d(x + width, y, z - width);
            GL11.glVertex3d(x - width, y, z - width);
            GL11.glVertex3d(x - width, y, z + width);
            GL11.glEnd();
            GL11.glBegin(3);
            GL11.glVertex3d(x + width, y, z + width);
            GL11.glVertex3d(x + width, y + height, z + width);
            GL11.glVertex3d(x - width, y + height, z + width);
            GL11.glVertex3d(x - width, y, z + width);
            GL11.glVertex3d(x + width, y, z + width);
            GL11.glVertex3d(x + width, y, z - width);
            GL11.glEnd();
            GL11.glBegin(3);
            GL11.glVertex3d(x + width, y + height, z + width);
            GL11.glVertex3d(x + width, y + height, z - width);
            GL11.glEnd();
            GL11.glBegin(3);
            GL11.glVertex3d(x - width, y + height, z + width);
            GL11.glVertex3d(x - width, y + height, z - width);
            GL11.glEnd();
        }
        post3D();
    }
}
