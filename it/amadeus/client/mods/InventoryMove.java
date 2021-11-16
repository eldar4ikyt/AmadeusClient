package it.amadeus.client.mods;

import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.PreMotion;
import it.amadeus.client.module.Module;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public final class InventoryMove extends Module {

    @Override
    public String getName() {
        return "InventoryMove";
    }

    @Override
    public String getDescription() {
        return "Ti Permette di muoverti con l'inventario aperto";
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
        if(event instanceof PreMotion){
            if(mc.currentScreen instanceof GuiContainer){
                if (Keyboard.isKeyDown(200)) {
                    mc.thePlayer.rotationPitch -= 3.0F;
                } else if (Keyboard.isKeyDown(208)) {
                    mc.thePlayer.rotationPitch += 3.0F;
                } else if (Keyboard.isKeyDown(203)) {
                    mc.thePlayer.rotationYaw -= 4.0F;
                } else if (Keyboard.isKeyDown(205)) {
                    mc.thePlayer.rotationYaw += 4.0F;
                }
                KeyBinding.setKeyBindState(Keyboard.KEY_W, Keyboard.isKeyDown(Keyboard.KEY_W));
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), GameSettings.isKeyDown(mc.gameSettings.keyBindSneak));
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), GameSettings.isKeyDown(mc.gameSettings.keyBindJump));
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), GameSettings.isKeyDown(mc.gameSettings.keyBindSprint));
            }
        }
    }
}
