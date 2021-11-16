package it.amadeus.client.mods;

import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.PostMotion;
import it.amadeus.client.event.events.PreMotion;
import it.amadeus.client.event.events.Update;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.TimerUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.optifine.util.EntityUtils;
import net.optifine.util.MathUtils;

import java.util.Arrays;
import java.util.List;

public final class Scaffold extends Module {

    private static final List<Block> blockBlacklist = Arrays.asList(Blocks.air, Blocks.water, Blocks.chest, Blocks.flowing_water, Blocks.lava,
            Blocks.flowing_lava, Blocks.tnt, Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer,
            Blocks.ice, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock,
            Blocks.jukebox, Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.sand, Blocks.lit_redstone_ore,
            Blocks.quartz_ore, Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate,
            Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.red_flower, Blocks.double_plant, Blocks.yellow_flower);
    private final TimerUtil timer = new TimerUtil();
    private int slot = -1;
    private int block = 0;
    private BlockData data;
    private int newSlot;
    private int oldSlot;

    public static float[] getRotationFromPosition(double x, double y, double z) {
        double xDiff = x - mc.thePlayer.posX;
        double yDiff = y - mc.thePlayer.posY - 1.2D;
        double zDiff = z - mc.thePlayer.posZ;
        double dist = Math.hypot(xDiff, zDiff);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / Math.PI);
        return new float[]{yaw, pitch};
    }

//    public static BlockData getBlockData(BlockPos var1) {
//        if (!blockBlacklist.contains(mc.theWorld.getBlockState(var1.add(0, -1, 0)).getBlock()))
//            return new BlockData(var1.add(0, -1, 0), EnumFacing.UP);
//        if (!blockBlacklist.contains(mc.theWorld.getBlockState(var1.add(-1, 0, 0)).getBlock()))
//            return new BlockData(var1.add(-1, 0, 0), EnumFacing.EAST);
//        if (!blockBlacklist.contains(mc.theWorld.getBlockState(var1.add(1, 0, 0)).getBlock()))
//            return new BlockData(var1.add(1, 0, 0), EnumFacing.WEST);
//        if (!blockBlacklist.contains(mc.theWorld.getBlockState(var1.add(0, 0, -1)).getBlock()))
//            return new BlockData(var1.add(0, 0, -1), EnumFacing.SOUTH);
//        if (!blockBlacklist.contains(mc.theWorld.getBlockState(var1.add(0, 0, 1)).getBlock()))
//            return new BlockData(var1.add(0, 0, 1), EnumFacing.NORTH);
//        BlockPos add = var1.add(-1, 0, 0);
//        if (!blockBlacklist.contains(mc.theWorld.getBlockState(add.add(-1, 0, 0)).getBlock()))
//            return new BlockData(add.add(-1, 0, 0), EnumFacing.EAST);
//        if (!blockBlacklist.contains(mc.theWorld.getBlockState(add.add(1, 0, 0)).getBlock()))
//            return new BlockData(add.add(1, 0, 0), EnumFacing.WEST);
//        if (!blockBlacklist.contains(mc.theWorld.getBlockState(add.add(0, 0, -1)).getBlock()))
//            return new BlockData(add.add(0, 0, -1), EnumFacing.SOUTH);
//        if (!blockBlacklist.contains(mc.theWorld.getBlockState(add.add(0, 0, 1)).getBlock()))
//            return new BlockData(add.add(0, 0, 1), EnumFacing.NORTH);
//        BlockPos add2 = var1.add(1, 0, 0);
//        if (!blockBlacklist.contains(mc.theWorld.getBlockState(add2.add(-1, 0, 0)).getBlock()))
//            return new BlockData(add2.add(-1, 0, 0), EnumFacing.EAST);
//        if (!blockBlacklist.contains(mc.theWorld.getBlockState(add2.add(1, 0, 0)).getBlock()))
//            return new BlockData(add2.add(1, 0, 0), EnumFacing.WEST);
//        if (!blockBlacklist.contains(mc.theWorld.getBlockState(add2.add(0, 0, -1)).getBlock()))
//            return new BlockData(add2.add(0, 0, -1), EnumFacing.SOUTH);
//        if (!blockBlacklist.contains(mc.theWorld.getBlockState(add2.add(0, 0, 1)).getBlock()))
//            return new BlockData(add2.add(0, 0, 1), EnumFacing.NORTH);
//        BlockPos add3 = var1.add(0, 0, -1);
//        if (!blockBlacklist.contains(mc.theWorld.getBlockState(add3.add(-1, 0, 0)).getBlock()))
//            return new BlockData(add3.add(-1, 0, 0), EnumFacing.EAST);
//        if (!blockBlacklist.contains(mc.theWorld.getBlockState(add3.add(1, 0, 0)).getBlock()))
//            return new BlockData(add3.add(1, 0, 0), EnumFacing.WEST);
//        if (!blockBlacklist.contains(mc.theWorld.getBlockState(add3.add(0, 0, -1)).getBlock()))
//            return new BlockData(add3.add(0, 0, -1), EnumFacing.SOUTH);
//        if (!blockBlacklist.contains(mc.theWorld.getBlockState(add3.add(0, 0, 1)).getBlock()))
//            return new BlockData(add3.add(0, 0, 1), EnumFacing.NORTH);
//        BlockPos add4 = var1.add(0, 0, 1);
//        if (!blockBlacklist.contains(mc.theWorld.getBlockState(add4.add(-1, 0, 0)).getBlock()))
//            return new BlockData(add4.add(-1, 0, 0), EnumFacing.EAST);
//        if (!blockBlacklist.contains(mc.theWorld.getBlockState(add4.add(1, 0, 0)).getBlock()))
//            return new BlockData(add4.add(1, 0, 0), EnumFacing.WEST);
//        if (!blockBlacklist.contains(mc.theWorld.getBlockState(add4.add(0, 0, -1)).getBlock()))
//            return new BlockData(add4.add(0, 0, -1), EnumFacing.SOUTH);
//        if (!blockBlacklist.contains(mc.theWorld.getBlockState(add4.add(0, 0, 1)).getBlock()))
//            return new BlockData(add4.add(0, 0, 1), EnumFacing.NORTH);
//        return null;
//    }

    public static boolean isEmpty(ItemStack stack) {
        return (stack == null);
    }


    public static double randomNumber(double max, double min) {
        return Math.random() * (max - min) + min;
    }

    public static boolean invCheck() {
        for (int i = 36; i < 45; ) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() ||
                    !isValid(mc.thePlayer.inventoryContainer.getSlot(i).getStack())) {
                i++;
                continue;
            }
            return false;
        }
        return true;
    }

    public static boolean isValid(ItemStack item) {
        if (isEmpty(item))
            return false;
        if (item.getUnlocalizedName().equalsIgnoreCase("tile.chest"))
            return false;
        if (!(item.getItem() instanceof ItemBlock))
            return false;
        return !blockBlacklist.contains(((ItemBlock) item.getItem()).getBlock());
    }

    public static boolean contains(Block block) {
        return blockBlacklist.contains(block);
    }

    public static int getBlockSlot() {
        for (int i = 36; i < 45; i++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof ItemBlock &&
                    !contains(((ItemBlock) stack.getItem()).getBlock()))
                return i - 36;
        }
        return -1;
    }

    public static void swap(int slot, int hotBarNumber) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hotBarNumber, 2, mc.thePlayer);
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
            while (block < 45) {
                mc.thePlayer.inventoryContainer.getSlot(block).getStack();
                ++block;
            }
            final int tempSlot = getBlockSlot();
            if (invCheck()) {
                for (int i = 9; i < 36; ++i) {
                    final Item item;
                    if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && (item = mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()) instanceof ItemBlock && !Scaffold.blockBlacklist.contains(((ItemBlock) item).getBlock()) && !((ItemBlock) item).getBlock().getLocalizedName().toLowerCase().contains("chest")) {
                        swap(i, 7);
                        break;
                    }
                }
            }
            this.data = null;
            this.slot = -1;
            if (tempSlot != -1) {
                this.newSlot = getBlockSlot();
                this.oldSlot = mc.thePlayer.inventory.currentItem;
                mc.thePlayer.inventory.currentItem = this.newSlot;
                final BlockPos blockBelow1 = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.9, mc.thePlayer.posZ);
                mc.thePlayer.inventory.currentItem = this.oldSlot;
                if (mc.theWorld.getBlockState(blockBelow1).getBlock() == Blocks.air) {
                    this.data = getBlockData(blockBelow1);
                    this.slot = tempSlot;
                    if (this.data == null) {
                        return;
                    }
                }
            }
        }
        if (event instanceof PreMotion) {
            if (this.data != null && mc.thePlayer.movementInput.jump) {
                if (!this.timer.delay(45.0f)) {
                    return;
                }
            } else if (!this.timer.delay(85.0f) || this.slot == -1) {
                return;
            }
            if (this.data == null || this.data.position == null || this.data.face == null) {
                return;
            }
            final float[] rots = getRotationFromPosition(this.data.position.getX(), this.data.position.getY(), this.data.position.getZ());
            mc.thePlayer.rotationYawHead = rots[0];
            mc.thePlayer.renderYawOffset = rots[0];
            ((PreMotion) event).setYaw(rots[0]);
            ((PreMotion) event).setPitch(rots[1]);
            mc.thePlayer.inventory.currentItem = this.newSlot;
            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), data.position, data.face, new Vec3(data.position.getX(), data.position.getY(), data.position.getZ()))) {
                mc.thePlayer.motionY = 0.42D;
            }
            mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
            mc.thePlayer.inventory.currentItem = this.oldSlot;
            this.timer.reset();
        }
    }

    private BlockData getBlockData(final BlockPos pos) {
        if (getBlock(pos.add(0, -1, 0)) != Blocks.air) {
            return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
        }
        if (getBlock(pos.add(-1, 0, 0)) != Blocks.air) {
            return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (getBlock(pos.add(1, 0, 0)) != Blocks.air) {
            return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
        }
        if (getBlock(pos.add(0, 0, -1)) != Blocks.air) {
            return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (getBlock(pos.add(0, 0, 1)) != Blocks.air) {
            return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (getBlock(pos.add(0, 1, 0)) != Blocks.air) {
            return new BlockData(pos.add(0, 1, 0), EnumFacing.DOWN);
        }
        return null;
    }
    public static Block getBlock(BlockPos pos) {
        return mc.theWorld.getBlockState(pos).getBlock();
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
