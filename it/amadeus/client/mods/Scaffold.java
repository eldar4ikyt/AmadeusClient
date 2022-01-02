package it.amadeus.client.mods;

import it.amadeus.client.clickgui.util.values.valuetypes.BooleanValue;
import it.amadeus.client.clickgui.util.values.valuetypes.NumberValue;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.PreMotion;
import it.amadeus.client.event.events.Update;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.InventoryUtils;
import it.amadeus.client.utilities.TimerUtil;
import it.amadeus.client.utilities.scaffold.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.List;

public final class Scaffold extends Module {

    private static final List<Block> blockBlacklist = Arrays.asList(Blocks.air, Blocks.water, Blocks.chest, Blocks.flowing_water, Blocks.lava,
            Blocks.flowing_lava, Blocks.tnt, Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer,
            Blocks.ice, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock,
            Blocks.jukebox, Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.sand, Blocks.lit_redstone_ore,
            Blocks.quartz_ore, Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate,
            Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.red_flower, Blocks.double_plant, Blocks.yellow_flower);
    private final TimerUtil clickTimer = new TimerUtil();
    private final BooleanValue<Boolean> cancelSpeed = new BooleanValue<>("Cancel Speed", true, this);
    private final BooleanValue<Boolean> swing = new BooleanValue<>("Swing", true, this);
    private final BooleanValue<Boolean> tower = new BooleanValue<>("Tower", true, this);
    private final BooleanValue<Boolean> downwards = new BooleanValue<>("Downwards", true, this);
    private final BooleanValue<Boolean> towermove = new BooleanValue<>("Tower Move", true, this);
    private final BooleanValue<Boolean> boolkeepY = new BooleanValue<>("Keep MotionY", true, this);
    private final NumberValue<Double> blockOverride = new NumberValue<>("Block Override", 3.24D, 1.0D, 6.7D, this);
    private final NumberValue<Double> delay = new NumberValue<>("Delay", 3.24D, 1.0D, 6.7D, this);
    private final EnumFacing[] facings = new EnumFacing[]{EnumFacing.EAST, EnumFacing.WEST, EnumFacing.SOUTH, EnumFacing.NORTH};
    private boolean override;
    private double startY;
    private BlockData data;
    private int hotBarSlot, i, bestBlockStack, blockSlot;
    private ItemStack stack;
    private float[] rotations;


    public static boolean isEmpty(ItemStack stack) {
        return (stack == null);
    }

    public static boolean contains(Block block) {
        return blockBlacklist.contains(block);
    }

    public static void swap(int slot, int hotBarNumber) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hotBarNumber, 2, mc.thePlayer);
    }

    public static float[] getScaffoldRotations(final BlockData data) {
        final Vec3 eyes = mc.thePlayer.getPositionEyes(RandomUtils.nextFloat(2.997f, 3.997f));
        final Vec3 position = new Vec3(data.position.getX() + 0.49, data.position.getY() + 0.49, data.position.getZ() + 0.49).add(new Vec3(data.face.getDirectionVec()).subtract(0.489996999502182, 0.489996999502182, 0.489996999502182));
        final Vec3 resultPosition = position.subtract(eyes);
        final float yaw = (float) Math.toDegrees(Math.atan2(resultPosition.zCoord, resultPosition.xCoord)) - 90.0f;
        final float pitch = (float) (-Math.toDegrees(Math.atan2(resultPosition.yCoord, Math.hypot(resultPosition.xCoord, resultPosition.zCoord))));
        return new float[]{yaw, pitch};
    }

    @Override
    public String getName() {
        return "Scaffold";
    }

    @Override
    public String getDescription() {
        return "Ti costruisce i ponti sotto i piedi";
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
        if (event instanceof Update) {
            if (mc.thePlayer.isPotionActive(Potion.moveSpeed) && this.cancelSpeed.getValue()) {
                mc.thePlayer.motionX *= 0.8180000185966492;
                mc.thePlayer.motionZ *= 0.8180000185966492;
            }
        } else if (event instanceof PreMotion) {
            this.data = this.getBlockData();
            this.bestBlockStack = this.findBestBlockStack();
            if (this.bestBlockStack != -1) {
                if (this.bestBlockStack < 36 && this.clickTimer.sleep(this.delay.getValue().longValue())) {
                    this.override = true;
                    this.i = 44;
                    while (this.i >= 36) {
                        this.stack = InventoryUtils.getStackInSlot(this.i);
                        if (!InventoryUtils.isValid(this.stack)) {
                            InventoryUtils.windowClick(this.bestBlockStack, this.i - 36, InventoryUtils.ClickType.SWAP_WITH_HOT_BAR_SLOT);
                            this.bestBlockStack = this.i;
                            this.override = false;
                            break;
                        }
                        --this.i;
                    }
                    if (this.override) {
                        this.blockSlot = (int) (this.blockOverride.getValue() - 1.0);
                        InventoryUtils.windowClick(this.bestBlockStack, this.blockSlot, InventoryUtils.ClickType.SWAP_WITH_HOT_BAR_SLOT);
                        this.bestBlockStack = this.blockSlot + 36;
                    }
                }
                if (this.data != null && this.bestBlockStack >= 36) {
                    mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                    rotations = getScaffoldRotations(this.data);
                    ((PreMotion) event).setYaw(rotations[0]);
                    ((PreMotion) event).setPitch(rotations[1]);
                }
            }
            if (this.data != null && this.bestBlockStack != -1 && this.bestBlockStack >= 36) {
                mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                this.hotBarSlot = this.bestBlockStack - 36;
                if (mc.thePlayer.inventory.currentItem != this.hotBarSlot) {
                    mc.thePlayer.inventory.currentItem = this.hotBarSlot;
                }
                ((PreMotion) event).setOnGround(false);
                mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), this.data.position, this.data.face, PlayerUtil.getVectorForRotation(rotations[0], rotations[1]))) {
                    if (!this.swing.getValue()) {
                        mc.getNetHandler().getNetworkManager().sendPacket(new C0APacketAnimation());
                    } else {
                        mc.thePlayer.swingItem();
                    }
                    this.data = null;
                }
            }
        }
    }

    private int findBestBlockStack() {
        int bestSlot = -1;
        int blockCount = -1;
        for (int i = 44; i >= 9; --i) {
            final ItemStack stack = InventoryUtils.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemBlock && InventoryUtils.isGoodBlockStack(stack) && stack.stackSize > blockCount) {
                bestSlot = i;
                blockCount = stack.stackSize;
            }
        }
        return bestSlot;
    }

    public BlockData getBlockData() {
        final EnumFacing[] invert = {EnumFacing.UP, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST};
        double yValue = 0.0;
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && !mc.gameSettings.keyBindJump.isKeyDown() && this.downwards.getValue()) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
            yValue -= 0.6;
        }
        BlockPos playerpos;
        final BlockPos aa = playerpos = new BlockPos(mc.thePlayer.getPositionVector()).offset(EnumFacing.DOWN).add(0.0, yValue, 0.0);
        final boolean tower = !this.towermove.getValue() && this.tower.getValue() && !mc.thePlayer.isMoving();
        if (!this.downwards.getValue() && this.boolkeepY.getValue() && !tower) {
            playerpos = new BlockPos(new Vec3(mc.thePlayer.getPositionVector().xCoord, this.startY, mc.thePlayer.getPositionVector().zCoord)).offset(EnumFacing.DOWN);
        } else {
            this.startY = mc.thePlayer.posY;
        }
        for (final EnumFacing facing : EnumFacing.values()) {
            if (playerpos.offset(facing).getBlock().getMaterial() != Material.air) {
                return new BlockData(playerpos.offset(facing), invert[facing.ordinal()]);
            }
        }
        final BlockPos[] addons = {new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1)};
        for (int length2 = addons.length, j = 0; j < length2; ++j) {
            final BlockPos offsetPos = playerpos.add(addons[j].getX(), 0, addons[j].getZ());
            if (mc.theWorld.getBlockState(offsetPos).getBlock() instanceof BlockAir) {
                for (int k = 0; k < EnumFacing.values().length; ++k) {
                    if (mc.theWorld.getBlockState(offsetPos.offset(EnumFacing.values()[k])).getBlock().getMaterial() != Material.air) {
                        return new BlockData(offsetPos.offset(EnumFacing.values()[k]), invert[EnumFacing.values()[k].ordinal()]);
                    }
                }
            }
        }
        return null;
    }

    public static class BlockData {
        public BlockPos position;
        public EnumFacing face;
        public Vec3 hitVec;

        public BlockData(final BlockPos position, final EnumFacing face) {
            this.position = position;
            this.face = face;
            this.hitVec = this.getHitVec();
        }

        public EnumFacing getFacing() {
            return this.face;
        }

        public BlockPos getPosition() {
            return this.position;
        }

        private Vec3 getHitVec() {
            final Vec3i directionVec = this.face.getDirectionVec();
            double x = directionVec.getX() * 0.5;
            double z = directionVec.getZ() * 0.5;
            if (this.face.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE) {
                x = -x;
                z = -z;
            }
            final Vec3 hitVec = new Vec3(this.position).addVector(x + z, directionVec.getY() * 0.5, x + z);
            final Vec3 src = Minecraft.getMinecraft().thePlayer.getPositionEyes(1.0f);
            final MovingObjectPosition obj = Minecraft.getMinecraft().theWorld.rayTraceBlocks(src, hitVec, false, false, true);
            if (obj == null || obj.hitVec == null || obj.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
                return null;
            }
            switch (this.face.getAxis()) {
                case Z: {
                    obj.hitVec = new Vec3(obj.hitVec.xCoord, obj.hitVec.yCoord, (double) Math.round(obj.hitVec.zCoord));
                    break;
                }
                case X: {
                    obj.hitVec = new Vec3((double) Math.round(obj.hitVec.xCoord), obj.hitVec.yCoord, obj.hitVec.zCoord);
                    break;
                }
            }
            if (this.face != EnumFacing.DOWN && this.face != EnumFacing.UP) {
                final IBlockState blockState = Minecraft.getMinecraft().theWorld.getBlockState(obj.getBlockPos());
                final Block blockAtPos = blockState.getBlock();
                double blockFaceOffset;
                if (blockAtPos instanceof BlockSlab && !((BlockSlab) blockAtPos).isDouble()) {
                    final BlockSlab.EnumBlockHalf half = blockState.getValue(BlockSlab.HALF);
                    blockFaceOffset = RandomUtils.nextDouble(0.1, 0.4);
                    if (half == BlockSlab.EnumBlockHalf.TOP) {
                        blockFaceOffset += 0.5;
                    }
                } else {
                    blockFaceOffset = RandomUtils.nextDouble(0.1, 0.9);
                }
                obj.hitVec = obj.hitVec.addVector(0.0, -blockFaceOffset, 0.0);
            }
            return obj.hitVec;
        }
    }
}
