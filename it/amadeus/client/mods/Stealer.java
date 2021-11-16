package it.amadeus.client.mods;

import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.Update;
import it.amadeus.client.module.Module;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.item.ItemStack;

public final class Stealer extends Module {

    private int delay;

    @Override
    public String getName() {
        return "Stealer";
    }

    @Override
    public String getDescription() {
        return "Ritira Tutto il Malloppo dalle chest";
    }

    @Override
    public int getKey() {
        return 0;
    }

    @Override
    public Category getCategory() {
        return Category.FUN;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof Update) {
            this.delay++;
            if (mc.currentScreen instanceof GuiChest) {
                GuiChest chest = (GuiChest) mc.currentScreen;
                if (isChestEmpty(chest)) {
                    mc.thePlayer.closeScreen();
                }
                for (int index = 0; index < chest.lowerChestInventory.getSizeInventory(); index++) {
                    ItemStack stack = chest.lowerChestInventory.getStackInSlot(index);
                    if (stack != null && this.delay > 0.8D) {
                        mc.playerController.windowClick(chest.inventorySlots.windowId, index, 0, 1, mc.thePlayer);
                        this.delay = 0;
                    }
                }
            }
        }
    }

    private boolean isChestEmpty(GuiChest chest) {
        for (int index = 0; index <= chest.lowerChestInventory.getSizeInventory(); index++) {
            ItemStack stack = chest.lowerChestInventory.getStackInSlot(index);
            if (stack != null)
                return false;
        }
        return true;
    }
}
