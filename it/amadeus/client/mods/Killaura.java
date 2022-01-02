package it.amadeus.client.mods;

import it.amadeus.client.clickgui.util.RenderUtil;
import it.amadeus.client.clickgui.util.values.valuetypes.BooleanValue;
import it.amadeus.client.clickgui.util.values.valuetypes.ModeValue;
import it.amadeus.client.clickgui.util.values.valuetypes.NumberValue;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.*;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.RotationUtils;
import it.amadeus.client.utilities.TimerUtil;
import it.amadeus.client.utilities.Translator;
import it.amadeus.client.utilities.angles.angle.Angle;
import it.amadeus.client.utilities.angles.angle.AngleUtility;
import it.amadeus.client.utilities.angles.vector.impl.Vector3;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Killaura extends Module {

    @Getter
    private static EntityLivingBase CurrentTarget;
    private final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.DISTANCE, this);
    private final NumberValue<Double> reach = new NumberValue<>("Reach", 3.24D, 1.0D, 6.7D, this);
    private final NumberValue<Double> min_aps = new NumberValue<>("Min APS", 6.25D, 1.0D, 15.0D, this);
    private final NumberValue<Double> max_aps = new NumberValue<>("Max APS", 10.45D, 1.0D, 20.0D, this);
    private final BooleanValue<Boolean> randomizeCPS = new BooleanValue<>("Randomize CPS", true, this);
    private final BooleanValue<Boolean> autoblock = new BooleanValue<>("AutoBlock", true, this);
    private final BooleanValue<Boolean> rotations = new BooleanValue<>("Rotations", true, this);
    private final BooleanValue<Boolean> death = new BooleanValue<>("Dead", true, this);
    private final BooleanValue<Boolean> teams = new BooleanValue<>("Teams", true, this);
    private final ModeValue<TargetHud> targetHud = new ModeValue<>("TargetHUD", TargetHud.EXHI, this);
    private final TimerUtil timer = new TimerUtil();
    private double healthBarWidth;
    private boolean canBlock;
    private boolean blockedBefore;
    private float yaw, pitch, lastHealth;
    private double x, y;


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
        if (event instanceof Update) {
            if (CurrentTarget != null && (CurrentTarget.getDistanceToEntity(mc.thePlayer) > reach.getValue() || CurrentTarget.isDead)) {
                CurrentTarget = null;
                this.blockedBefore = true;
            }
        } else if (event instanceof PreMotion) {
            if (CurrentTarget == null) {
                switch (this.mode.getValue()) {
                    case HEALT:
                        CurrentTarget = getHealthPriority();
                        break;
                    case DISTANCE:
                        CurrentTarget = getAnglePriority();
                        break;
                }
            } else if (isValidTarget(CurrentTarget) && CurrentTarget != null) {
                if (rotations.getValue()) {
                    ((PreMotion) event).setYaw(yaw);
                    ((PreMotion) event).setPitch(pitch);
                }
                if (randomizeCPS.getValue() ? timer.hasReached(RandomUtils.nextLong(10L, 20L)) : timer.hasReached(RandomUtils.nextLong(this.min_aps.getValue().longValue(), this.max_aps.getValue().longValue()))) {
                    attack(CurrentTarget);
                    timer.reset();
                }
                if (autoblock.getValue()) {
                    this.canBlock = mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
                    if (canBlock) {
                        mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
                    }
                }
            }
        } else if (event instanceof MoveFlying) {
            if (isValidTarget(CurrentTarget) && CurrentTarget != null) {
                final AngleUtility angleUtil = new AngleUtility(10, 190, 10, 10);
                final Vector3<Double> enemyCoords = new Vector3<>(CurrentTarget.posX, CurrentTarget.posY, CurrentTarget.posZ);
                final Vector3<Double> myCoords = new Vector3<>(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                final Angle dstAngle = angleUtil.calculateAngle(enemyCoords, myCoords);
                yaw = dstAngle.getYaw();
                pitch = dstAngle.getPitch();
                if (pitch > 90.0f) {
                    pitch = 90.0f;
                } else if (pitch < -90.0f) {
                    pitch = -90.0f;
                }
            }
        } else if (event instanceof PacketSend) {
            Packet<?> packet = ((PacketSend) event).getPacket();
            if (autoblock.getValue()) {
                if (CurrentTarget != null && !this.canBlock && this.blockedBefore) {
                    if (packet instanceof C07PacketPlayerDigging && mc.thePlayer.isBlocking()) {
                        ((PacketSend) event).setCancelled(true);
                        mc.thePlayer.sendQueue.addToSendQueueNoPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1.2999999523162842D, -1.0D, -1.0D), EnumFacing.DOWN));
                        this.blockedBefore = false;
                    }
                }
            }
        } else if (event instanceof Overlay) {
            switch (this.targetHud.getValue()) {
                case ASTOLFO:
                    final ScaledResolution sr = new ScaledResolution(mc);
                    if (CurrentTarget instanceof EntityPlayer) {
                        GlStateManager.pushMatrix();
                        GlStateManager.translate( -227, -220, 0.0);
                        final int n2 = sr.getScaledWidth() / 2 + 300;
                        final int n3 = sr.getScaledHeight() / 2 + 200;// x, y,  width,  height,  radius, final int color
                        RenderUtil.drawRoundedRect(n2 - 70.0f, n3 + 23.0f, 160, 67, 6.0f, new Color(0, 0, 0, 190).getRGB());
                        final float health2 = CurrentTarget.getHealth();
                        final float healthPercentage2 = health2 / CurrentTarget.getMaxHealth();
                        float scaledWidth = 0.0f;
                        if (healthPercentage2 != this.lastHealth) {
                            final float scaledHeight = healthPercentage2 - this.lastHealth;
                            scaledWidth = this.lastHealth;
                            this.lastHealth += scaledHeight / 8.0f;
                        }
                        RenderUtil.drawRoundedRect2(n2 - 36.0f, n3 + 78.0f, n2 - 36.0f + 120.0, n3 + 85.0f, 6.0, RenderUtil.pulseBrightness(new Color(97, 2, 22), 2, 2).getRGB());

                        // //todo: la vita che scende ha dei bug
                        if (healthPercentage2 * 100.0f <= 75.0f) {
                            RenderUtil.drawRoundedRect2(n2 - 36.0f, n3 + 78.0f, n2 - 36.0f + 120.0 - healthPercentage2, n3 + 85.0f, 6.0, RenderUtil.pulseBrightness(new Color(234, 20, 57), 2, 2).getRGB());
                            RenderUtil.drawRoundedRect2(n2 - 36.0f, n3 + 78.0f, n2 - 36.0f + 120.0, n3 + 85.0f, 6.0, RenderUtil.pulseBrightness(new Color(182, 25, 80), 2, 2).getRGB());
                        }
                        final int x1 = n2 - 50;
                        final int i = n3 + 32;
                        GL11.glPushMatrix();
                        GlStateManager.translate((float) x1, (float) i, 1.0f);
                        GL11.glScaled(1.1, 1.1, 1.1);
                        GlStateManager.translate((float) (-x1), (float) (-i), 1.0f);
                        mc.fontRendererObj.drawStringWithShadow(((EntityPlayer) CurrentTarget).getGameProfile().getName(), x1 + 13.5f, i + 7.5f, -1);
                        GL11.glPopMatrix();
                        final int x2 = n2 - 64;
                        final int yAdd = n3 + 40;
                        GL11.glPushMatrix();
                        GlStateManager.translate((float) x2, (float) yAdd, 1.0f);
                        GL11.glScalef(2.0f, 2.0f, 2.0f);
                        GlStateManager.translate((float) (-x2), (float) (-yAdd), 1.0f);
                        mc.fontRendererObj.drawStringWithShadow(String.format("%.1f", CurrentTarget.getHealth() / 2.0f) + " \u2764", x2 + 13.5f, yAdd + 7.5f,  RenderUtil.pulseBrightness(new Color(97, 2, 22), 2, 2).getRGB());
                        GL11.glPopMatrix();
                        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                        RenderUtil.drawEntityOnScreen(n2 - 53, n3 + 85, 24, 25.0f, 25.0f, CurrentTarget);
                        GlStateManager.popMatrix();
                    }
                    break;
                case EXHI:
                    if (CurrentTarget instanceof EntityPlayer && isValidTarget(CurrentTarget)) {
                        final float startX = 20.0f;
                        final ScaledResolution sr2 = new ScaledResolution(mc);
                        final float x3 = sr2.getScaledWidth() / 2.0f + 30.0f;
                        final float y = sr2.getScaledHeight() / 2.0f + 30.0f;
                        final float healthRender = CurrentTarget.getHealth();
                        double hpPercentage = healthRender / CurrentTarget.getMaxHealth();
                        hpPercentage = MathHelper.clamp_double(hpPercentage, 0.0, 1.0);
                        final double hpWidth = 60.0 * hpPercentage;
                        final String healthStr = String.valueOf((int) CurrentTarget.getHealth() / 1.0f);
                        int xAdd = 0;
                        final double multiplier = 0.85;
                        GlStateManager.pushMatrix();
                        GlStateManager.scale(multiplier, multiplier, multiplier);
                        if (CurrentTarget.getCurrentArmor(3) != null) {
                            mc.getRenderItem().renderItemAndEffectIntoGUI(CurrentTarget.getCurrentArmor(3), (int) ((sr2.getScaledWidth() / 2 + startX + 33.0f + xAdd) / multiplier), (int) ((sr2.getScaledHeight() / 2 + 56) / multiplier));
                            xAdd += 15;
                        }
                        if (CurrentTarget.getCurrentArmor(2) != null) {
                            mc.getRenderItem().renderItemAndEffectIntoGUI(CurrentTarget.getCurrentArmor(2), (int) ((sr2.getScaledWidth() / 2 + startX + 33.0f + xAdd) / multiplier), (int) ((sr2.getScaledHeight() / 2 + 56) / multiplier));
                            xAdd += 15;
                        }
                        if (CurrentTarget.getCurrentArmor(1) != null) {
                            mc.getRenderItem().renderItemAndEffectIntoGUI(CurrentTarget.getCurrentArmor(1), (int) ((sr2.getScaledWidth() / 2 + startX + 33.0f + xAdd) / multiplier), (int) ((sr2.getScaledHeight() / 2 + 56) / multiplier));
                            xAdd += 15;
                        }
                        if (CurrentTarget.getCurrentArmor(0) != null) {
                            mc.getRenderItem().renderItemAndEffectIntoGUI(CurrentTarget.getCurrentArmor(0), (int) ((sr2.getScaledWidth() / 2 + startX + 33.0f + xAdd) / multiplier), (int) ((sr2.getScaledHeight() / 2 + 56) / multiplier));
                            xAdd += 15;
                        }
                        if (CurrentTarget.getHeldItem() != null) {
                            mc.getRenderItem().renderItemAndEffectIntoGUI(CurrentTarget.getHeldItem(), (int) ((sr2.getScaledWidth() / 2 + startX + 33.0f + xAdd) / multiplier), (int) ((sr2.getScaledHeight() / 2 + 56) / multiplier));
                        }
                        GlStateManager.popMatrix();
                        this.healthBarWidth = Translator.animate(hpWidth, this.healthBarWidth, 0.1);
                        Gui.drawGradientRect((int) (x3 - 23.5), (int) (y - 3.5), (int) (x3 + 105.5f), (int) (y + 42.4f), new Color(10, 10, 10, 255).getRGB(), new Color(10, 10, 10, 255).getRGB());
                        Gui.drawGradientRect((int) (x3 - 23.0f), (int) (y - 3.2), (int) (x3 + 104.8f), (int) (y + 41.8f), new Color(40, 40, 40, 255).getRGB(), new Color(40, 40, 40, 255).getRGB());
                        Gui.drawGradientRect((int) (x3 - 21.4), (int) (y - 1.5), (int) (x3 + 103.5f), (int) (y + 40.5f), new Color(74, 74, 74, 255).getRGB(), new Color(74, 74, 74, 255).getRGB());
                        Gui.drawGradientRect((int) (x3 - 21.0f), (int) (y - 1.0f), (int) (x3 + 103.0f), (int) (y + 40.0f), new Color(32, 32, 32, 255).getRGB(), new Color(10, 10, 10, 255).getRGB());
                        Gui.drawRect((int) (x3 + 25.0f), (int) (y + 11.0f), (int) (x3 + 87.0f), (int) (y + 14.29f), new Color(105, 105, 105, 40).getRGB());
                        Gui.drawRect((int) (x3 + 25.0f), (int) (y + 11.0f), (int) (x3 + 27.0f + this.healthBarWidth), (int) (y + 14.29f), getColorFromPercentage(CurrentTarget.getHealth(), CurrentTarget.getMaxHealth()));
                        mc.fontRendererObj.drawStringWithShadow(CurrentTarget.getName(), x3 + 24.8f, y + 1.9f, new Color(255, 255, 255).getRGB());
                        mc.fontRendererObj.drawStringWithShadow("l   l   l   l   l   l   ", x3 + 27.5f, y + 10.2f, new Color(50, 50, 50).getRGB());
                        mc.fontRendererObj.drawStringWithShadow("HP:" + healthStr, x3 - 11.2f + 44.0f - mc.fontRendererObj.getStringWidth(healthStr) / 2.0f, y + 17.0f, -1);
                        drawFace((int) (x3 - 20.0f), (int) y, 8.0f, 8.0f, 8, 8, 40, 40, 64.0f, 64.0f, (AbstractClientPlayer) CurrentTarget);
                    } else {
                        this.healthBarWidth = 92.0;
                    }
                    break;
            }
        } else if (event instanceof EventLoadWorld) {
            if (CurrentTarget != null) {
                CurrentTarget = null;
                this.blockedBefore = true;
            }
        }
    }

    @Override
    public void onDisable() {
        CurrentTarget = null;
        this.blockedBefore = true;
        super.onDisable();
    }

    private void attack(EntityLivingBase e) {
        if (e != null) {
            mc.thePlayer.swingItem();
            mc.thePlayer.sendQueue.addToSendQueueNoPacket(new C02PacketUseEntity(e, C02PacketUseEntity.Action.ATTACK));
            mc.thePlayer.setSprinting(false);
        }
    }

    private int getColorFromPercentage(final float current, final float max) {
        final float percentage = current / max / 3.0f;
        return Color.HSBtoRGB(percentage, 1.0f, 1.0f);
    }

    //CurrentTarget.getDistanceToEntity(mc.thePlayer) > reach.getValue() ||
    private boolean isValidTarget(Entity entityLivingBase) {
        if (entityLivingBase == null) return false;
        else if (Objects.equals(entityLivingBase, mc.thePlayer)) return false;
        else if (isOnSameTeam(CurrentTarget) && teams.getValue()) return false;
        else if ((entityLivingBase.isDead && this.death.getValue())) return false;
        else return !entityLivingBase.isInvisible();
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

    private boolean isOnSameTeam(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            if (entity.getTeam() != null && mc.thePlayer.getTeam() != null && this.teams.getValue()) {
                char c1 = entity.getDisplayName().getFormattedText().charAt(1);
                char c2 = mc.thePlayer.getDisplayName().getFormattedText().charAt(1);
                return c1 == c2;
            }
        }
        return false;
    }

    private EntityLivingBase getHealthPriority() {
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

    private void drawFace(final int x, final int y, final float u, final float v, final int uWidth, final int vHeight, final int width, final int height, final float tileWidth, final float tileHeight, final AbstractClientPlayer target) {
        try {
            final ResourceLocation skin = target.getLocationSkin();
            Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
            GL11.glEnable(3042);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            Gui.drawScaledCustomSizeModalRect(x, y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
            GL11.glDisable(3042);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private enum Mode {DISTANCE, HEALT}

    private enum TargetHud {EXHI, ASTOLFO}
}