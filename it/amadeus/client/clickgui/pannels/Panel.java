package it.amadeus.client.clickgui.pannels;


import it.amadeus.client.clickgui.ClickGui;
import it.amadeus.client.clickgui.pannels.components.Button;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public final class Panel {
    private final TimerUtil upTimer;
    private final TimerUtil downTimer;
    private final Module.Category category;
    private final List<Button> buttons = new ArrayList<>();
    public double lastClickedMs = 0.0;
    public boolean onTop;
    private int x, y;
    private int width = 115;
    private int height = 15;
    private int animation = 0;
    private boolean open = false;
    private boolean dragging;
    private int xOffset;
    private int yOffset;

    public Panel(int x, int y, Module.Category cat) {
        this.x = x;
        this.y = y;
        category = cat;

        Minecraft.getMinecraft().getAmadeus().getModManager().getModsByCat(category).stream().map(Button::new).forEach(buttons::add);

        buttons.sort((a, b) -> Double.compare(b.lastInteract, a.lastInteract));
        upTimer = new TimerUtil();
        downTimer = new TimerUtil();
    }

    public void reload() {
        buttons.clear();

        Minecraft.getMinecraft().getAmadeus().getModManager().getModsByCat(category).stream().map(Button::new).forEach(buttons::add);

        buttons.sort((a, b) -> Double.compare(b.lastInteract, a.lastInteract));
    }

    public void drawScreen(int mouseX, int mouseY) {
        if (dragging) {
            x = mouseX - (width / 2) + xOffset;
            y = mouseY - (height / 2) + yOffset;
        }
        GL11.glPushMatrix();
        Gui.drawRect(x - 1, y, x + width + 1, y + height, ClickGui.getPrimaryColor().getRGB());
        ClickGui.getFont().drawStringWithFont(category.name(), x + 5, y + (height >> 1) - (ClickGui.getFont().getStringHeight(category.name()) >> 1), -1);
        GL11.glPopMatrix();
        width = 115;
        height = 20;
        int offset = height;
        if (open) {
            if (animation > 0) {
                if (downTimer.sleep(15)) {
                    animation--;
                    downTimer.reset();
                }
            }
            for (int i = 0; i < (buttons.size() - animation); i++) {
                offset += buttons.get(i).drawScreen(mouseX, mouseY, x, y + offset, width, open);
            }
        } else {
            if (animation < 0) {
                if (upTimer.sleep(10)) {
                    animation++;
                    upTimer.reset();
                }
                for (int i = 0; i < Math.abs(animation); i++) {
                    if (i < buttons.size()) {
                        if (buttons.get(i).opened) {
                            animation = -buttons.get(i).settings.size();
                            buttons.get(i).opened = false;
                        }
                        offset += buttons.get(i).drawScreen(mouseX, mouseY, x, y + offset, width, open);
                    }
                }
            }
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
        for (Button b : buttons) {
            b.keyTyped(typedChar, keyCode);
        }
    }

    public void mouseClicked(int x, int y, int button) {
        lastClickedMs = (double) System.currentTimeMillis();
        if (isHovered(x, y) && Mouse.isButtonDown(1)) {
            open = !open;
            if (open) {
                animation = buttons.size();
            } else {
                animation = -buttons.size();
            }
            //System.out.println(open);
        } else if (isHovered(x, y) && Mouse.isButtonDown(0) && !ClickGui.dragging) {
            dragging = true;
            ClickGui.dragging = true;
            int xPos = this.x + (width / 2);
            int yPos = this.y + (height / 2);
            this.xOffset = xPos - x;
            this.yOffset = yPos - y;
            lastClickedMs = (double) System.currentTimeMillis();
        }
        for (Button value : buttons) {
            value.mouseClicked(x, y, button);
        }
    }

    public int getWidth() {
        return width;
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (dragging && state == 0) {
            dragging = false;
            ClickGui.dragging = false;
            lastClickedMs = (double) System.currentTimeMillis();
        }

        for (Button b : buttons) {
            b.mouseReleased(mouseX, mouseY, state);
        }
    }

    private boolean isHovered(int mouseX, int mouseY) {
        return (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height);
    }
}
