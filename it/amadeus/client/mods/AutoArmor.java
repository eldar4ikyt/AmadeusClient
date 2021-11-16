package it.amadeus.client.mods;

import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.Update;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.ArmorUtils;
import net.minecraft.item.ItemStack;

public final class AutoArmor extends Module {

    private final int[] chestplate = new int[]{311, 307, 315, 303, 299};
    private final int[] leggings = new int[]{312, 308, 316, 304, 300};
    private final int[] boots = new int[]{313, 309, 317, 305, 301};
    private final int[] helmet = new int[]{310, 306, 314, 302, 298};
    private final boolean best = true;
    private int delay = 0;

    @Override
    public String getName() {
        return "AutoArmor";
    }

    @Override
    public String getDescription() {
        return "Ti Mette l'armatura in automatico";
    }

    @Override
    public int getKey() {
        return 0;
    }

    @Override
    public Category getCategory() {
        return Category.FIGHT;
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof Update){
            autoArmor();
            betterArmor();
        }
    }


    public void autoArmor() {
        if (this.best)
            return;
        int item = -1;
        this.delay++;
        if (this.delay >= 10) {
            if (mc.thePlayer.inventory.armorInventory[0] == null) {
                int[] boots;
                int length = (boots = this.boots).length;
                for (int i = 0; i < length; i++) {
                    int id = boots[i];
                    if (ArmorUtils.getItem(id) != -1) {
                        item = ArmorUtils.getItem(id);
                        break;
                    }
                }
            }
            if (mc.thePlayer.inventory.armorInventory[1] == null) {
                int[] leggings;
                int length = (leggings = this.leggings).length;
                for (int i = 0; i < length; i++) {
                    int id = leggings[i];
                    if (ArmorUtils.getItem(id) != -1) {
                        item = ArmorUtils.getItem(id);
                        break;
                    }
                }
            }
            if (mc.thePlayer.inventory.armorInventory[2] == null) {
                int[] chestplate;
                int length = (chestplate = this.chestplate).length;
                for (int i = 0; i < length; i++) {
                    int id = chestplate[i];
                    if (ArmorUtils.getItem(id) != -1) {
                        item = ArmorUtils.getItem(id);
                        break;
                    }
                }
            }
            if (mc.thePlayer.inventory.armorInventory[3] == null) {
                int[] helmet;
                int length = (helmet = this.helmet).length;
                for (int i = 0; i < length; i++) {
                    int id = helmet[i];
                    if (ArmorUtils.getItem(id) != -1) {
                        item = ArmorUtils.getItem(id);
                        break;
                    }
                }
            }
            if (item != -1) {
                mc.playerController.windowClick(0, item, 0, 1, mc.thePlayer);
                this.delay = 0;
            }
        }
    }

    public void betterArmor() {
        if (!this.best)
            return;
        this.delay++;
        if (this.delay >= 10 && (mc.thePlayer.openContainer == null || mc.thePlayer.openContainer.windowId == 0)) {
            boolean switchArmor = false;
            int item = -1;
            if (mc.thePlayer.inventory.armorInventory[0] == null) {
                int[] array;
                int m = (array = this.boots).length;
                for (int i = 0; i < m; i++) {
                    int id = array[i];
                    if (ArmorUtils.getItem(id) != -1) {
                        item = ArmorUtils.getItem(id);
                        break;
                    }
                }
            }
            if (ArmorUtils.isBetterArmor(0, this.boots)) {
                item = 8;
                switchArmor = true;
            }
            if (mc.thePlayer.inventory.armorInventory[3] == null) {
                int[] array;
                int m = (array = this.helmet).length;
                for (int i = 0; i < m; i++) {
                    int id = array[i];
                    if (ArmorUtils.getItem(id) != -1) {
                        item = ArmorUtils.getItem(id);
                        break;
                    }
                }
            }
            if (ArmorUtils.isBetterArmor(3, this.helmet)) {
                item = 5;
                switchArmor = true;
            }
            if (mc.thePlayer.inventory.armorInventory[1] == null) {
                int[] array;
                int m = (array = this.leggings).length;
                for (int i = 0; i < m; i++) {
                    int id = array[i];
                    if (ArmorUtils.getItem(id) != -1) {
                        item = ArmorUtils.getItem(id);
                        break;
                    }
                }
            }
            if (ArmorUtils.isBetterArmor(1, this.leggings)) {
                item = 7;
                switchArmor = true;
            }
            if (mc.thePlayer.inventory.armorInventory[2] == null) {
                int[] array;
                int m = (array = this.chestplate).length;
                for (int i = 0; i < m; i++) {
                    int id = array[i];
                    if (ArmorUtils.getItem(id) != -1) {
                        item = ArmorUtils.getItem(id);
                        break;
                    }
                }
            }
            if (ArmorUtils.isBetterArmor(2, this.chestplate)) {
                item = 6;
                switchArmor = true;
            }
            boolean b = false;
            ItemStack[] stackArray;
            int k = (stackArray = mc.thePlayer.inventory.mainInventory).length;
            for (int j = 0; j < k; j++) {
                ItemStack stack = stackArray[j];
                if (stack == null) {
                    b = true;
                    break;
                }
            }
            switchArmor = (switchArmor && !b);
            if (item != -1) {
                mc.playerController.windowClick(0, item, 0, switchArmor ? 4 : 1, mc.thePlayer);
                this.delay = 0;
            }
        }
    }
}