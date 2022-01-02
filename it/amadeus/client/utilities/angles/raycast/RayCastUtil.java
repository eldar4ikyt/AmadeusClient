package it.amadeus.client.utilities.angles.raycast;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.List;

public class RayCastUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static Entity getMouseOver(final float yaw, final float pitch, final Entity target) {
        Entity pointedEntity = null;
        final float p_78473_1_ = 1.0f;
        final Entity var2 = mc.getRenderViewEntity();
        if (var2 != null && mc.theWorld != null) {
            mc.mcProfiler.startSection("pick");
            mc.pointedEntity = null;
            double var3 = mc.playerController.getBlockReachDistance();
            mc.objectMouseOver = var2.rayTrace(var3, p_78473_1_);
            double var4 = var3;
            final Vec3 var5 = var2.getPositionEyes(var2.getEyeHeight());
            var3 = target.getDistanceToEntity(mc.thePlayer);
            var4 = target.getDistanceToEntity(mc.thePlayer);
            if (mc.objectMouseOver != null) {
                var4 = mc.objectMouseOver.hitVec.distanceTo(var5);
            }
            final Vec3 var6 = var2.getRotationVec(pitch, yaw);
            final Vec3 var7 = var5.addVector(var6.xCoord * var3, var6.yCoord * var3, var6.zCoord * var3);
            Vec3 var8 = null;
            final float var9 = 0.3f;
            final List<?> var10 = mc.theWorld.getEntitiesWithinAABBExcludingEntity(var2, var2.getEntityBoundingBox().addCoord(var6.xCoord * var3, var6.yCoord * var3, var6.zCoord * var3).expand(var9, var9, var9));
            double var11 = var4;
            for (int var12 = 0; var12 < var10.size(); ++var12) {
                final Entity var13 = (Entity) var10.get(var12);
                if (var13.canBeCollidedWith()) {
                    final float var14 = var13.getCollisionBorderSize();
                    final AxisAlignedBB var15 = var13.getEntityBoundingBox().expand(var14, var14, var14);
                    final MovingObjectPosition var16 = var15.calculateIntercept(var5, var7);
                    if (var15.isVecInside(var5)) {
                        if (0.0 < var11 || var11 == 0.0) {
                            pointedEntity = var13;
                            var8 = ((var16 == null) ? var5 : var16.hitVec);
                            var11 = 0.0;
                        }
                    } else {
                        final double var17;
                        if (var16 != null && ((var17 = var5.distanceTo(var16.hitVec)) < var11 || var11 == 0.0)) {
                            if (var13 == var2.ridingEntity) {
                                if (var11 == 0.0) {
                                    pointedEntity = var13;
                                    var8 = var16.hitVec;
                                }
                            } else {
                                pointedEntity = var13;
                                var8 = var16.hitVec;
                                var11 = var17;
                            }
                        }
                    }
                }
            }
            if (pointedEntity != null && (var11 < var4 || mc.objectMouseOver == null)) {
                mc.objectMouseOver = new MovingObjectPosition(pointedEntity, var8);
                if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
                    mc.pointedEntity = pointedEntity;
                }
            }
            mc.mcProfiler.endSection();
        }
        return pointedEntity;
    }

}
