package it.amadeus.client.mods;

import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.EventLoadWorld;
import it.amadeus.client.event.events.Update;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.List;

public final class MurderFinder extends Module {

    private final TimerUtil timer = new TimerUtil();
    private int sent;

    @Override
    public String getName() {
        return "MurderFinder";
    }

    @Override
    public String getDescription() {
        return "cerca il murder su hypixel";
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
    public void onDisable() {
        this.sent = 0;
        this.timer.reset();
        super.onDisable();
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof Update) {
            for (Entity ent : mc.theWorld.loadedEntityList) {
                if (ent != mc.thePlayer) {
                    if (this.timer.sleep(3000L)) {
                        this.sent = 0;
                        this.timer.reset();
                    }
                    if ((ent instanceof EntityPlayer)) {
                        List<ItemStack> itemsToRender = new ArrayList<>();
                        for (int i = 0; i < 5; i++) {
                            ItemStack stack = ((EntityPlayer) ent).getEquipmentInSlot(i);
                            if (stack != null) {
                                itemsToRender.add(stack);
                            }
                        }
                        for (ItemStack stack : itemsToRender) {
                            if ((stack != null) && ((stack.getItem() instanceof ItemSword))) {
                                this.sent += 1;
                                if (this.sent == 1) {
                                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText((ent.getName() + " Is The Murderer")));
                                    this.timer.reset();
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }if(event instanceof EventLoadWorld){
            this.sent = 0;
            this.timer.reset();
        }
    }
}