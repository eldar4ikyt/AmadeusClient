package it.amadeus.client.utilities;

import com.google.gson.JsonSyntaxException;
import com.jhlabs.image.GaussianFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

public class Blurrer {
    private final HashMap<Integer, Integer> shadowCache = new HashMap<>();

    private final ResourceLocation blurLocation = new ResourceLocation("shaders/post/blur.json");
    private final Minecraft mc = Minecraft.getMinecraft();
    private final boolean oldBlur;
    private ShaderGroup shaderGroup;
    private Framebuffer framebuffer;
    private int lastFactorBlur;
    private int lastWidthBlur;
    private int lastHeightBlur;
    private int lastFactorBuffer;
    private int lastWidthBuffer;
    private int lastHeightBuffer;
    private ShaderGroup blurShaderGroupBuffer;
    private Framebuffer blurBuffer;

    public Blurrer(boolean oldBlur) {
        this.oldBlur = oldBlur;

        this.blurBuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false);
        this.blurBuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);

        ScaledResolution scaledResolution = new ScaledResolution(mc);

        int scaleFactor = scaledResolution.getScaleFactor();
        int width = scaledResolution.getScaledWidth();
        int height = scaledResolution.getScaledHeight();

        this.lastFactorBlur = this.lastFactorBuffer = scaleFactor;
        this.lastWidthBlur = this.lastWidthBuffer = width;
        this.lastHeightBlur = this.lastHeightBuffer = height;
    }

    public void init() {
        try {
            this.shaderGroup = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), blurLocation);
            this.shaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            this.framebuffer = this.shaderGroup.mainFramebuffer;
        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    private void setPostValues() {
        this.shaderGroup.getListShaders().get(0).getShaderManager().getShaderUniform("BlurDir").set(1, 0);
        this.shaderGroup.loadShaderGroup(mc.timer.renderPartialTicks);
        this.shaderGroup.getListShaders().get(0).getShaderManager().getShaderUniform("BlurDir").set(0, 1);
        this.shaderGroup.loadShaderGroup(mc.timer.renderPartialTicks);
    }

    private void setPreValues(float strength) {
        this.shaderGroup.getListShaders().get(1).getShaderManager().getShaderUniform("Radius").set(0);
        this.shaderGroup.getListShaders().get(1).getShaderManager().getShaderUniform("BlurDir").set(0, 0);
        this.shaderGroup.getListShaders().get(0).getShaderManager().getShaderUniform("Radius").set(strength);
    }

    public void update(float partialTicks) {
        // Shader loading
        ScaledResolution scaledResolution = new ScaledResolution(mc);

        int scaleFactor = scaledResolution.getScaleFactor();
        int width = scaledResolution.getScaledWidth();
        int height = scaledResolution.getScaledHeight();

        if (sizeHasChangedBuffer(scaleFactor, width, height)) {
            this.blurBuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false);
            this.blurBuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
            this.loadShader(this.blurLocation, this.blurBuffer);
        }

        this.lastFactorBuffer = scaleFactor;
        this.lastWidthBuffer = width;
        this.lastHeightBuffer = height;

//        mc.entityRenderer.useShader = false;
        if (this.blurShaderGroupBuffer == null) {
            this.loadShader(this.blurLocation, this.blurBuffer);
        }
        GlStateManager.enableDepth();

        // Updates frame buffer
        mc.getFramebuffer().unbindFramebuffer();

        this.blurBuffer.bindFramebuffer(true);

        mc.getFramebuffer().framebufferRenderExt(mc.displayWidth, mc.displayHeight, true);

        if (OpenGlHelper.shadersSupported) {
            if (blurShaderGroupBuffer != null) {
                GlStateManager.matrixMode(GL11.GL_TEXTURE);
                GlStateManager.pushMatrix();
                GlStateManager.loadIdentity();
                this.blurShaderGroupBuffer.loadShaderGroup(partialTicks);
                GlStateManager.popMatrix();
            }
        }

        this.blurBuffer.unbindFramebuffer();
        mc.getFramebuffer().bindFramebuffer(true);
        mc.entityRenderer.setupOverlayRendering();
    }

    private void loadShader(ResourceLocation resourceLocationIn, Framebuffer framebuffer) {
        if (OpenGlHelper.isFramebufferEnabled()) {
            try {
                this.blurShaderGroupBuffer = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), framebuffer, resourceLocationIn);
                this.blurShaderGroupBuffer.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void blur(double x, double y, double areaWidth, double areaHeight,float density,
                     boolean setupOverlayRendering, boolean bloom,
                     boolean reverseBloom, int bloomRadius, int bloomAlpha) {
        this.blur(x, y, areaWidth, areaHeight, density,setupOverlayRendering, bloom, reverseBloom, bloomRadius, bloomAlpha, false);
    }

    public void blur(double x, double y, double areaWidth, double areaHeight,float density,
                     boolean setupOverlayRendering, boolean bloom,
                     boolean reverseBloom, int bloomRadius, int bloomAlpha, boolean ignoreModule) {

        if (bloom && !reverseBloom) {
            bloom((int) x - 4, (int) y - 4, (int) areaWidth + 8, (int) areaHeight + 8, bloomRadius, bloomAlpha);
        }

        if (mc.theWorld != null && mc.thePlayer != null && !oldBlur) {

            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            scissor(x, y, areaWidth, areaHeight);
            blur(density, setupOverlayRendering, true);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        } else {
            ScaledResolution scaledResolution = new ScaledResolution(mc);

            int scaleFactor = scaledResolution.getScaleFactor();
            int width = scaledResolution.getScaledWidth();
            int height = scaledResolution.getScaledHeight();

            if (sizeHasChangedBlur(scaleFactor, width, height) || this.framebuffer == null || this.shaderGroup == null) {
                init();
            }

            this.lastFactorBlur = scaleFactor;
            this.lastWidthBlur = width;
            this.lastHeightBlur = height;

            if (bloom && !reverseBloom) {
                bloom((int) x - 4, (int) y - 4, (int) areaWidth + 8, (int) areaHeight + 8, bloomRadius, bloomAlpha);
            }

            GlStateManager.disableDepth();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            scissor(x, y, areaWidth, areaHeight);
            setPreValues(10);
            this.framebuffer.bindFramebuffer(true);
            setPostValues();
            mc.getFramebuffer().bindFramebuffer(false);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GlStateManager.enableDepth();
        }

        if (bloom && reverseBloom) {
            bloom((int) x - 4, (int) y - 4, (int) areaWidth + 8, (int) areaHeight + 8, bloomRadius, bloomAlpha);
        }
    }

    public void blur(float blurStrength, boolean setupOverlayRendering) {
        this.blur(blurStrength, setupOverlayRendering, false);
    }

    public void blurOld(float blurStrength) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);

        int scaleFactor = scaledResolution.getScaleFactor();
        int width = scaledResolution.getScaledWidth();
        int height = scaledResolution.getScaledHeight();

        if (sizeHasChangedBlur(scaleFactor, width, height) || this.framebuffer == null || this.shaderGroup == null) {
            init();
        }

        this.lastFactorBlur = scaleFactor;
        this.lastWidthBlur = width;
        this.lastHeightBlur = height;

        GlStateManager.disableDepth();
        setPreValues(blurStrength);
        this.framebuffer.bindFramebuffer(true);
        setPostValues();
        mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.enableDepth();
    }

    public void blur(float blurStrength, boolean setupOverlayRendering, boolean ignoreModule) {

        if (mc.theWorld != null && mc.thePlayer != null && !oldBlur) {
            GL11.glPushMatrix();
            GL11.glPushMatrix();
            //System.out.println(this.blurShaderGroupBuffer.getListShaders().get(0));
                this.blurShaderGroupBuffer.getListShaders().get(0).getShaderManager().getShaderUniform("Radius").set(blurStrength);
                this.blurShaderGroupBuffer.getListShaders().get(1).getShaderManager().getShaderUniform("Radius").set(blurStrength);

            this.blurBuffer.framebufferRender(mc.displayWidth, mc.displayHeight);

            GL11.glPopMatrix();
            if (setupOverlayRendering) {
                mc.entityRenderer.setupOverlayRendering();
            }
            GlStateManager.enableDepth();
            GlStateManager.enableAlpha();
            GL11.glPopMatrix();
        } else {
            ScaledResolution scaledResolution = new ScaledResolution(mc);

            int scaleFactor = scaledResolution.getScaleFactor();
            int width = scaledResolution.getScaledWidth();
            int height = scaledResolution.getScaledHeight();

            if (sizeHasChangedBlur(scaleFactor, width, height) || this.framebuffer == null || this.shaderGroup == null) {
                init();
            }

            this.lastFactorBlur = scaleFactor;
            this.lastWidthBlur = width;
            this.lastHeightBlur = height;

            GlStateManager.disableDepth();
            setPreValues(blurStrength);
            this.framebuffer.bindFramebuffer(true);
            setPostValues();
            mc.getFramebuffer().bindFramebuffer(true);
            GlStateManager.enableDepth();
        }
    }

    public void blur(double x, double y, double areaWidth, double areaHeight,float density, boolean setupOverlayRendering, boolean ignoreModule) {
        blur(x, y, areaWidth, areaHeight,density, setupOverlayRendering, false, false, 0, 0, ignoreModule);
    }

    public void blur(double x, double y, double areaWidth, double areaHeight,float density,boolean setupOverlayRendering) {
        blur(x, y, areaWidth, areaHeight,density, setupOverlayRendering, false, false, 0, 0);
    }

    public void bloom(int x, int y, int width, int height, int blurRadius, int bloomAlpha, boolean ignoreModule) {
        bloom(x, y, width, height, blurRadius, new Color(0, 0, 0, bloomAlpha), ignoreModule);
    }

    public void bloom(int x, int y, int width, int height, int blurRadius, int bloomAlpha) {
        bloom(x, y, width, height, blurRadius, new Color(0, 0, 0, bloomAlpha), false);
    }

    public void bloom(int x, int y, int width, int height, int blurRadius, Color color, boolean ignoreModule) {

        GlStateManager.pushAttribAndMatrix();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.01f);

        height = Math.max(0, height);
        width = Math.max(0, width);
        width = width + blurRadius * 2;
        height = height + blurRadius * 2;
        x = x - blurRadius;
        y = y - blurRadius;

        float _X = x - 0.25f;
        float _Y = y + 0.25f;

        int identifier = width * height + width + color.hashCode() * blurRadius + blurRadius;

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);

        int texId;
        if (this.shadowCache.containsKey(identifier)) {
            texId = this.shadowCache.get(identifier);

            GlStateManager.bindTexture(texId);
        } else {
            BufferedImage original = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            Graphics g = original.getGraphics();
            g.setColor(color);
            g.fillRect(blurRadius, blurRadius, width - blurRadius * 2, height - blurRadius * 2);
            g.dispose();

            GaussianFilter op = new GaussianFilter(blurRadius);

            BufferedImage blurred = op.filter(original, null);

            texId = TextureUtil.uploadTextureImageAllocate(TextureUtil.glGenTextures(), blurred, true, false);
            this.shadowCache.put(identifier, texId);
        }

        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2d(_X, _Y);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2d(_X, _Y + height);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2d(_X + width, _Y + height);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2d(_X + width, _Y);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.popAttribAndMatrix();
    }

    private boolean sizeHasChangedBlur(int scaleFactor, int width, int height) {
        return this.lastFactorBlur != scaleFactor || this.lastWidthBlur != width || this.lastHeightBlur != height;
    }

    private boolean sizeHasChangedBuffer(int scaleFactor, int width, int height) {
        return this.lastFactorBuffer != scaleFactor || this.lastWidthBuffer != width || this.lastHeightBuffer != height;
    }

    public void scissor(double x, double y, double width, double height) {
        ScaledResolution sr = new ScaledResolution(mc);
        double scale = sr.getScaleFactor();

        y = sr.getScaledHeight() - y;

        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;

        GL11.glScissor((int) x, (int) (y - height), (int) width, (int) height);
    }
}