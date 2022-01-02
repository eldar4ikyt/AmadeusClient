package net.minecraft.client.gui;

import it.amadeus.client.accounts.AdrianAltManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback {

    public GuiMainMenu() {
    }


    public boolean doesGuiPauseGame() {
        return false;
    }


    public void initGui() {
        int i = 24;
        int j = height / 4 + 48;
        this.buttonList.add(new GuiButton(1, width / 2 - 100, j, I18n.format("menu.singleplayer")));
        this.buttonList.add(new GuiButton(2, width / 2 - 100, j + i, I18n.format("menu.multiplayer")));
        this.buttonList.add(new GuiButton(69, width / 2 - 100, j + i * 2, "Accounts"));
        this.buttonList.add(new GuiButton(0, width / 2 - 100, j + 72 + 12, 98, 20, I18n.format("menu.options")));
        this.buttonList.add(new GuiButton(4, width / 2 + 2, j + 72 + 12, 98, 20, I18n.format("menu.quit")));
        this.buttonList.add(new GuiButton(0, width / 2 - 100, j + 72 + 12, 98, 20, I18n.format("menu.options")));
        this.buttonList.add(new GuiButton(4, width / 2 + 2, j + 72 + 12, 98, 20, I18n.format("menu.quit")));
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }
        if (button.id == 1) {
            this.mc.displayGuiScreen(new GuiSelectWorld(this));
        }
        if (button.id == 2) {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }
        if (button.id == 69) {
            this.mc.displayGuiScreen(new AdrianAltManager(this));
        }
        if (button.id == 4) {
            this.mc.shutdown();
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawString(this.fontRendererObj, EnumChatFormatting.DARK_RED + "Amadeus v1.0", 2, height - 10, -1);
        this.drawString(this.fontRendererObj, EnumChatFormatting.RED + "Copyright By AdrianCode", width - this.fontRendererObj.getStringWidth(EnumChatFormatting.RED + "Copyright By AdrianCode") - 2, height - 10, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }


    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
