package it.amadeus.client.mods;

import it.amadeus.client.clickgui.util.values.valuetypes.BooleanValue;
import it.amadeus.client.clickgui.util.values.valuetypes.ModeValue;
import it.amadeus.client.clickgui.util.values.valuetypes.NumberValue;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.MoveFlying;
import it.amadeus.client.event.events.PacketSend;
import it.amadeus.client.event.events.PreMotion;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.MotionUtil;
import it.amadeus.client.utilities.RotationUtils;
import it.amadeus.client.utilities.TimerUtil;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.*;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Killaura extends Module {

    @Getter
    private static EntityLivingBase currentTarget;
    private static float[] rotations;
    private final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.DISTANCE, this);
    private final NumberValue<Double> reach = new NumberValue<>("Reach", 3.24D, 1.0D, 6.7D, this);
    private final NumberValue<Double> max_aps = new NumberValue<>("Max APS", 12.45D, 1.0D, 20.0D, this);
    private final NumberValue<Double> min_aps = new NumberValue<>("Min APS", 6.25D, 1.0D, 15.0D, this);
    private final BooleanValue<Boolean> autoblock = new BooleanValue<>("AutoBlock", true, this);
    private final BooleanValue<Boolean> noRots = new BooleanValue<>("Bypass", true, this);
    private final TimerUtil timer = new TimerUtil();
    private float tempYaw;
    private float tempPitch;
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
                    if (timer.hasTimeElapsed((randomClickDelay(min_aps.getValue(), max_aps.getValue()) + RandomUtils.nextLong(100,300)))) {
                        attack(currentTarget);
                        timer.reset();
                    }
                    if (autoblock.getValue()) {
                        ItemStack heldItem = mc.thePlayer.getHeldItem();
                        this.canBlock = heldItem != null && heldItem.getItem() instanceof net.minecraft.item.ItemSword;
                        if (canBlock) { mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());}
                        if (!currentTarget.isDead) {this.blockedBefore = true;}
                    }
                }
                if (currentTarget.getDistanceToEntity(mc.thePlayer) > reach.getValue() || currentTarget.isDead) {
                    currentTarget = null;
                }
            }
        }
        if (event instanceof PacketSend) {
            Packet<?> packet = ((PacketSend) event).getPacket();
            if (autoblock.getValue()) {
                if (currentTarget != null && packet instanceof C07PacketPlayerDigging && !this.canBlock && this.blockedBefore) {
                    ((PacketSend) event).setPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1.2999999523162842D, -1.0D, -1.0D), EnumFacing.DOWN));
                    this.blockedBefore = false;
                }
            }
            if (packet instanceof S08PacketPlayerPosLook && currentTarget != null && mc.thePlayer.swingProgress > 0) {
                S08PacketPlayerPosLook packet2 = (S08PacketPlayerPosLook)packet;
                packet2.setPitch(mc.thePlayer.rotationPitch);
                packet2.setYaw(mc.thePlayer.rotationYaw);
            }
        }
    }

    @Override
    public void onDisable() {
        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1.25F, -1, -1), EnumFacing.DOWN));
        currentTarget = null;
        super.onDisable();
    }

    private void attack(EntityLivingBase e) {
        if (e != null) {
            if (isValidTarget(e) && !e.isDead) {
                mc.thePlayer.swingItem();
                mc.playerController.attackEntity(mc.thePlayer, e);
            }
        }
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
