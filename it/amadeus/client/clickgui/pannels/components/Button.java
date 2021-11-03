package it.amadeus.client.clickgui.pannels.components;

import it.amadeus.client.clickgui.ClickGui;
import it.amadeus.client.clickgui.util.values.Value;
import it.amadeus.client.clickgui.util.values.valuetypes.BooleanValue;
import it.amadeus.client.clickgui.util.values.valuetypes.ModeValue;
import it.amadeus.client.clickgui.util.values.valuetypes.NumberValue;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Button {

    private final Module mod;

    private final TimerUtil upTimer;
    private final TimerUtil downTimer;

    public long lastInteract;
    public int animation;
    public boolean opened = false;
    public List<Component> settings = new ArrayList<>();
    private int x, y, width, height;

    private boolean hovered;
    private boolean clickable = false;
    private boolean isMiddleClick = false;

    private final float lastRed = (float) ClickGui.getSecondaryColor(false).getRed() / 255F;
    private final float lastGreen = (float) ClickGui.getSecondaryColor(false).getGreen() / 255F;
    private final float lastBlue = (float) ClickGui.getSecondaryColor(false).getBlue() / 255F;

    public Button(Module mod) {
        this.mod = mod;

        for (Value<?> s : mod.getSettings()) {

            if (s instanceof NumberValue) {
                this.settings.add(new Slider((NumberValue<?>) s, this));
            }
            if (s instanceof BooleanValue) {
                this.settings.add(new Checkbox((BooleanValue<?>) s, this));
            }
            if (s instanceof ModeValue) {
                this.settings.add(new ModeButton((ModeValue<?>) s, this));
            }
        }
        upTimer = new TimerUtil();
        downTimer = new TimerUtil();
    }

    public void keyTyped(char typedChar, int keyCode) {
        for (Component s : this.settings) {
            s.keyTyped(typedChar, keyCode);
        }
        if (isMiddleClick()) {
            if (!Keyboard.getKeyName(keyCode).equalsIgnoreCase("ESCAPE")) {
                //ChatUtil.print("Bound " + mod.getName() + " to " + Keyboard.getKeyName(keyCode));
                mod.setKey(keyCode);
            } else {
                //ChatUtil.print("Bound " + mod.getName() + " to " + "NONE");
                mod.setKey(Keyboard.KEY_NONE);
            }
            setMiddleClick(false);
        }
    }

    public Module getMod() {
        return this.mod;
    }

    public int drawScreen(int mouseX, int mouseY, int x, int y, int width, boolean open) {

        ArrayList<Component> settings = getActiveComponents();
        this.clickable = open;
        this.x = x;
        this.y = y;
        this.height = 15;
        this.width = width;
        this.hovered = this.isHovered(mouseX, mouseY);

        float speed = 256F / (float) Minecraft.getDebugFPS();

        Gui.drawRect(x, y, x + width, y + height, ClickGui.getSecondaryColor(true).getRGB());

       ClickGui.getFont().drawStringWithFont(this.mod.getName() + getKey(), this.x + 5, this.y + (this.height >> 1) - (ClickGui.getFont().getStringHeight(this.mod.getName() + ": " + getKey()) >> 1), this.mod.isToggled() ? ClickGui.getPrimaryColor().getRGB() : new Color(175, 175, 175).getRGB());

       if(isHovered(mouseX, mouseY)){
           ClickGui.getFont().drawStringWithFont(mod.getDescription(), this.x + 55, this.y + (this.height >> 1)  - 4, new Color(75, 175, 75).getRGB());
       }

       int addVal = 0;
        if (!settings.isEmpty()) {
            GL11.glPushMatrix();
            ClickGui.getFont().drawStringWithFont(opened ? "-" : "+", x + width - 10, this.y + (this.height >> 1) - (ClickGui.getFont().getStringHeight("+") >> 1), new Color(175, 175, 175).getRGB());
            GL11.glPopMatrix();
        }

        if (this.opened && !settings.isEmpty()) {
            addVal = this.height;
            if (this.animation > 0) {
                if (this.downTimer.sleep(25)) {
                    this.animation--;
                    this.downTimer.reset();
                }
            }
            for (int i = 0; i < settings.size() - animation; i++) {
                addVal += settings.get(i).drawScreen(mouseX, mouseY, x, y + addVal);
            }
            addVal -= height;
        } else {
            if (this.animation < 0) {
                addVal = this.height;
                if (this.upTimer.sleep(25)) {
                    this.animation++;
                    this.upTimer.reset();
                }
                for (int i = 0; i < Math.abs(animation); i++) {
                    addVal += settings.get(i).drawScreen(mouseX, mouseY, x, y + addVal);
                }
                addVal -= height;
            }
        }

        return this.height + addVal;
    }


    public void mouseClicked(int x, int y, int button) {
        if (!clickable) return;
        this.hovered = this.isHovered(x, y);
        ArrayList<Component> settings = getActiveComponents();
        if (this.hovered && Mouse.isButtonDown(0)) {
            this.mod.toggle();
        } else if (this.hovered && button == 1) {
            opened = !opened;
            if (opened) {
                lastInteract = System.currentTimeMillis();
                animation = settings.size();
            } else {
                animation = -settings.size();
            }
        } else if (hovered && button == 2) {
            opened = !opened;
            if (opened) {
                animation = -settings.size();
            }
            setMiddleClick(!isMiddleClick());
            if (!this.isMiddleClick()) {
                //ChatUtil.print("Bound " + mod.getName() + " to " + "NONE");
                mod.setKey(Keyboard.KEY_NONE);
                setMiddleClick(false);
            }

        } else if (this.opened) {
            for (Component sc : settings) {
                sc.mouseClicked(x, y, button);
            }
        }
    }

    public String getKey() {
        if (isMiddleClick()) {
            return " [" + Keyboard.getKeyName(mod.getKey()) + "]";
        } else {
            return "";
        }
    }

    private boolean isMiddleClick() {
        return isMiddleClick;
    }

    private void setMiddleClick(boolean b) {
        isMiddleClick = b;
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        ArrayList<Component> settings = getActiveComponents();
        if (!clickable) return;
        if (this.opened) {
            for (Component sc : settings) {
                sc.mouseReleased(mouseX, mouseY, state);
            }
        }
    }

    private boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY > y && mouseY < y + height;
    }

    public int getWidth() {
        return this.width;
    }

    public ArrayList<Component> getActiveComponents() {
        ArrayList<Component> activeComponents = new ArrayList<>();
        for (int i = this.settings.size() - 1; i > -1; i--) {
            Component component = settings.get(i);
            if (component.getSetting().checkDependants()) {
                activeComponents.add(component);
            }
        }
        Collections.reverse(activeComponents);
        return activeComponents;
    }
}
