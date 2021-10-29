package it.amadeus.client.mods;

import it.amadeus.client.clickgui.util.values.valuetypes.BooleanValue;
import it.amadeus.client.clickgui.util.values.valuetypes.ModeValue;
import it.amadeus.client.clickgui.util.values.valuetypes.NumberValue;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.PacketSend;
import it.amadeus.client.event.events.PreMotion;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.RotationUtils;
import it.amadeus.client.utilities.TimerUtil;
import lombok.Getter;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Killaura extends Module {

    @Getter private static EntityLivingBase currentTarget;
    private static float[] rotations;
    private final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.DISTANCE, this);
    private final NumberValue<Double> reach = new NumberValue<>("Reach", 3.24D, 1.0D, 6.7D, this);
    private final NumberValue<Double> max_aps = new NumberValue<>("Max APS", 12.45D, 1.0D, 20.0D, this);
    private final NumberValue<Double> min_aps = new NumberValue<>("Min APS", 6.25D, 1.0D, 15.0D, this);
    private final BooleanValue<Boolean> autoblock = new BooleanValue<>("AutoBlock", true, this);
    private final BooleanValue<Boolean> noRots = new BooleanValue<>("No Rotations", true, this);
    private final TimerUtil timer = new TimerUtil();
    private boolean canBlock;
    private boolean blockedBefore;

    public static long randomClickDelay(final double minCPS, final double maxCPS) {
        return (long) ((Math.random() * (1000 / minCPS - 1000 / maxCPS + 1)) + 1000 / maxCPS);
    }

    @Override
    public String getName() {
        return "Killaura";
    }

    @Override
    public String getDescription() {
        return "Picchia Tutti i giocatori vicini";
    }

    @Override
    public int getKey() {
        return Keyboard.KEY_R;
    }

    @Override
    public Category getCategory() {
        return Category.FIGHT;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof PreMotion) {
            if (currentTarget == null) {
                switch (this.mode.getValue()) {
                    case HEALT:
                        currentTarget = getHealthPriority();
                        break;
                    case DISTANCE:
                        currentTarget = getAnglePriority();
                        break;
                }
            } else {
                if (isValidTarget(currentTarget)) {
                    rotations = RotationUtils.getRotations(currentTarget, 180F, false);
                    if (!noRots.getValue()) {
                        ((PreMotion) event).setYaw(rotations[0]);
                        ((PreMotion) event).setPitch(rotations[1]);
                        mc.thePlayer.renderYawHead = rotations[0];
                        mc.thePlayer.renderPitchHead = rotations[0];
                    }
                    if(autoblock.getValue()){
                        ItemStack heldItem = mc.thePlayer.getHeldItem();
                        this.canBlock = (currentTarget == getAnglePriority() && heldItem != null && heldItem.getItem() instanceof net.minecraft.item.ItemSword);
                        mc.playerController.sendUseItem((EntityPlayer)mc.thePlayer, (World)mc.theWorld, mc.thePlayer.getHeldItem());
                        if ( !currentTarget.isDead) {
                            this.blockedBefore = true;
                        }
                    }
                    if (timer.hasTimeElapsed(randomClickDelay(min_aps.getValue(), max_aps.getValue()))) {
                        attack(currentTarget);
                        timer.reset();
                    }
                }
                if (currentTarget.getDistanceToEntity(mc.thePlayer) > reach.getValue() || currentTarget.isDead) {
                    currentTarget = null;
                }
            }
        }
        if (event instanceof PacketSend) {
            Packet<?> packet = ((PacketSend) event).getPacket();
            if(autoblock.getValue()){
                if (currentTarget != null && packet instanceof C07PacketPlayerDigging && !this.canBlock && this.blockedBefore) {
                    ((PacketSend) event).setPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1.2999999523162842D, -1.0D, -1.0D), EnumFacing.DOWN));
                    this.blockedBefore = false;
                }
            }
        }
    }

    @Override
    public void onDisable() {
        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1.25F, -1, -1), EnumFacing.DOWN));
        currentTarget = null;
        super.onDisable();
    }

    //todo: fix this shit
    private void attack(EntityLivingBase e) {
        final Entity target = raycasting(rotations[0], rotations[1], e);
        if (target != null) {
            if (isValidTarget((EntityLivingBase) target) && !target.isDead) {
                mc.thePlayer.swingItem();
                mc.playerController.attackEntity((EntityPlayer)mc.thePlayer, target);
            }
        }
    }


    private Entity raycasting(float yaw, float pitch, EntityLivingBase target) {
        Entity pointedEntity = null;
        float p_78473_1_ = 1.0F;
        Entity var2 = mc.getRenderViewEntity();
        if (var2 != null && mc.theWorld != null) {
            mc.mcProfiler.startSection("pick");
            mc.pointedEntity = null;
            double var3 = mc.playerController.getBlockReachDistance();
            mc.objectMouseOver = var2.rayTrace(var3, 1.0F);
            Vec3 var5 = var2.getPositionEyes(var2.getEyeHeight());
            var3 = target.getDistanceToEntity(mc.thePlayer);
            double var4 = target.getDistanceToEntity(mc.thePlayer);
            if (mc.objectMouseOver != null)
                var4 = mc.objectMouseOver.hitVec.distanceTo(var5);
            Vec3 var6 = var2.getRotationVec(pitch, yaw);
            Vec3 var7 = var5.addVector(var6.xCoord * var3, var6.yCoord * var3, var6.zCoord * var3);
            pointedEntity = null;
            Vec3 var8 = null;
            float var9 = 0.3F;
            List<Entity> var10 = mc.theWorld.getEntitiesWithinAABBExcludingEntity(var2, var2.getEntityBoundingBox().addCoord(var6.xCoord * var3, var6.yCoord * var3, var6.zCoord * var3).expand(0.30000001192092896D, 0.30000001192092896D, 0.30000001192092896D));
            double var11 = var4;
            for (Entity o : var10) {
                Entity var13 = o;
                if (var13.canBeCollidedWith()) {
                    float var14 = var13.getCollisionBorderSize();
                    AxisAlignedBB var15 = var13.getEntityBoundingBox().expand(var14, var14, var14);
                    MovingObjectPosition var16 = var15.calculateIntercept(var5, var7);
                    if (var15.isVecInside(var5)) {
                        if (0.0D < var11 || var11 == 0.0D) {
                            pointedEntity = var13;
                            var8 = (var16 == null) ? var5 : var16.hitVec;
                            var11 = 0.0D;
                        }
                        continue;
                    }
                    double var17;
                    if (var16 != null && ((var17 = var5.distanceTo(var16.hitVec)) < var11 || var11 == 0.0D)) {
                        if (var13 == var2.ridingEntity) {
                            if (var11 == 0.0D) {
                                pointedEntity = var13;
                                var8 = var16.hitVec;
                            }
                            continue;
                        }
                        pointedEntity = var13;
                        var8 = var16.hitVec;
                        var11 = var17;
                    }
                }
            }
            if (pointedEntity != null && (var11 < var4 || mc.objectMouseOver == null)) {
                mc.objectMouseOver = new MovingObjectPosition(pointedEntity, var8);
                if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof net.minecraft.entity.item.EntityItemFrame)
                    mc.pointedEntity = pointedEntity;
            }
            mc.mcProfiler.endSection();
        }
        return pointedEntity;
    }

    public EntityLivingBase getAnglePriority() {
        List<EntityLivingBase> entities = new ArrayList<>();
        for (Entity e : mc.theWorld.loadedEntityList) {
            if (e instanceof EntityLivingBase) {
                EntityLivingBase player = (EntityLivingBase) e;
                if (mc.thePlayer.getDistanceToEntity(player) < reach.getValue() && isValidTarget(player)) {
                    entities.add(player);
                }
            }
        }
        entities.sort((o1, o2) -> {
            float[] rot1 = RotationUtils.getRotations(o1, 180F, true);
            float[] rot2 = RotationUtils.getRotations(o2, 180F, true);
            return (int) ((mc.thePlayer.rotationYaw - rot1[0]) - (mc.thePlayer.rotationYaw - rot2[0]));
        });
        if (entities.isEmpty()) {
            return null;
        }
        return entities.get(0);
    }

    public EntityLivingBase getHealthPriority() {
        List<EntityLivingBase> entities = new ArrayList<>();
        for (Entity e : mc.theWorld.loadedEntityList) {
            if (e instanceof EntityLivingBase) {
                EntityLivingBase player = (EntityLivingBase) e;
                if (mc.thePlayer.getDistanceToEntity(player) < reach.getValue() && isValidTarget(player)) {
                    entities.add(player);
                }
            }
        }
        entities.sort((o1, o2) -> (int) (o1.getHealth() - o2.getHealth()));
        if (entities.isEmpty()) {
            return null;
        }
        return entities.get(0);
    }

    //TODO: FINIRE TEAMS CHECK
    private boolean isValidTarget(EntityLivingBase entityLivingBase) {
        if (Objects.isNull(entityLivingBase)) {
            return false;
        } else if (Objects.equals(entityLivingBase, mc.thePlayer)) {
            return false;
        } else if (mc.thePlayer.getDistanceToEntity(entityLivingBase) > reach.getValue()) {
            return false;
        } else if (entityLivingBase.isInvisible()) {
            return false;
        } else return !(entityLivingBase instanceof EntityArmorStand);
    }

    private boolean isOnSameTeam(EntityLivingBase entity) {
        if (entity.getTeam() != null && mc.thePlayer.getTeam() != null) {
            char c1 = entity.getDisplayName().getFormattedText().charAt(1);
            char c2 = mc.thePlayer.getDisplayName().getFormattedText().charAt(1);
            return c1 == c2;
        }
        return false;
    }


    public enum Mode {DISTANCE, HEALT}
}
