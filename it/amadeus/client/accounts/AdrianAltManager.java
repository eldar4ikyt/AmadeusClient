package it.amadeus.client.accounts;

import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.microsoft.Microsoft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.net.Proxy;

public class AdrianAltManager extends GuiScreen {
    private final GuiScreen parent;

    private GuiTextField email;

    private GuiPasswordField password;

    private String status;

    public AdrianAltManager(GuiScreen parent) {
        this.parent = parent;
    }

    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 92 - 11, "Login Java"));
        this.buttonList.add(new GuiButton(2, width / 2 - 100, height / 4 + 92 + 12, "Login Microsoft"));
        this.buttonList.add(new GuiButton(3, width / 2 - 100, height / 4 + 116 + 12, "Clipboard"));
        this.buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 146 + 12, "Back"));
        this.email = new GuiTextField(0, this.fontRendererObj, width / 2 - 100, 60, 200, 20);
        this.email.setMaxStringLength(2147483647);
        this.email.setFocused(true);
        this.password = new GuiPasswordField(this.fontRendererObj, width / 2 - 100, 100, 200, 20);
        this.password.setMaxStringLength(2147483647);
    }

    public void keyTyped(char character, int keyCode) throws IOException {
        this.email.textboxKeyTyped(character, keyCode);
        this.password.textboxKeyTyped(character, keyCode);
        if (keyCode == 15) {
            this.email.setFocused(!this.email.isFocused());
            this.password.setFocused(!this.password.isFocused());
        }
        if (keyCode == 28)
            actionPerformed(this.buttonList.get(0));
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.email.mouseClicked(mouseX, mouseY, mouseButton);
        this.password.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(this.mc.fontRendererObj, EnumChatFormatting.AQUA + "AltManager", width / 2, 20, -1);
        if (this.email.getText().isEmpty())
            drawString(this.mc.fontRendererObj, "Username / Email", width / 2 - 96, 66, -7829368);
        if (this.password.getText().isEmpty())
            drawString(this.mc.fontRendererObj, "Password", width / 2 - 96, 106, -7829368);
        this.email.drawTextBox();
        this.password.drawTextBox();
        drawCenteredString(this.mc.fontRendererObj, this.status, width / 2, 30, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 3:
                try {
                    String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                    if (!data.contains(":"))
                        return;
                    String[] credentials = data.split(":");
                    this.email.setText(credentials[0]);
                    this.password.setText(credentials[1]);
                } catch (HeadlessException|java.awt.datatransfer.UnsupportedFlavorException|IOException e) {
                    setStatus(EnumChatFormatting.DARK_RED + "Failed Importing Alt.");
                }
                break;
            case 2:
                (new Thread(() -> {
                    Microsoft login = new Microsoft();
                    login.login(this);
                })).start();
                break;
            case 0:
                if (this.password.getText().isEmpty()) {
                    if (!this.email.getText().trim().isEmpty()) {
                        this.mc.session = new Session(this.email.getText().trim(), "-", "-", "Legacy");
                        setStatus("Logged as " + this.email.getText().trim() + " [SP]");
                    }
                    break;
                }
                if (!this.email.getText().trim().isEmpty()) {
                    YggdrasilUserAuthentication a = (YggdrasilUserAuthentication)(new YggdrasilAuthenticationService(Proxy.NO_PROXY, "")).createUserAuthentication(Agent.MINECRAFT);
                    a.setUsername(this.email.getText().trim());
                    a.setPassword(this.password.getText().trim());
                    try {
                        a.logIn();
                        this.mc.session = new Session(a.getSelectedProfile().getName(), a.getSelectedProfile().getId().toString(), a.getAuthenticatedToken(), "mojang");
                        setStatus("Logged as " + a.getSelectedProfile().getName() + " [Java]");
                    } catch (Exception e) {
                        setStatus(EnumChatFormatting.DARK_RED + "Login Failed.");
                    }
                }
                break;
            case 1:
                this.mc.displayGuiScreen(this.parent);
                break;
        }
    }
}
