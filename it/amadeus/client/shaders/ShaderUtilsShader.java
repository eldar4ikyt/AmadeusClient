package it.amadeus.client.shaders;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;

public class ShaderUtilsShader {

    private static final String VERTEX_SHADER = "#version 130\n\nvoid main() {\n    gl_TexCoord[0] = gl_MultiTexCoord0;\n    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n}";
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final int program = GL20.glCreateProgram();
    private final long startTime = System.currentTimeMillis();
    private float mousemove = 0.01f;

    public ShaderUtilsShader(String fragment) {
        this.initShader(fragment);
    }

    private static int getMouseX() {
        return Mouse.getX() * GuiScreen.width / mc.displayWidth;
    }

    private static int getMouseY() {
        return GuiScreen.height - Mouse.getY() * GuiScreen.height / mc.displayHeight - 1;
    }

    private void initShader(String frag) {
        int vertex = GL20.glCreateShader(35633);
        int fragment = GL20.glCreateShader(35632);
        GL20.glShaderSource(vertex, VERTEX_SHADER);
        GL20.glShaderSource(fragment, frag);
        GL20.glValidateProgram(this.program);
        GL20.glCompileShader(vertex);
        GL20.glCompileShader(fragment);
        GL20.glAttachShader(this.program, vertex);
        GL20.glAttachShader(this.program, fragment);
        GL20.glLinkProgram(this.program);
    }

    public void renderFirst() {
        GL11.glClear(16640);
        GL20.glUseProgram(this.program);
    }

    public void renderSecond() {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        ScaledResolution sr = new ScaledResolution(mc);
        GL11.glBegin(7);
        GL11.glTexCoord2d(0.0, 1.0);
        GL11.glVertex2d(0.0, 0.0);
        GL11.glTexCoord2d(0.0, 0.0);
        GL11.glVertex2d(0.0, sr.getScaledHeight());
        GL11.glTexCoord2d(1.0, 0.0);
        GL11.glVertex2d(sr.getScaledWidth(), sr.getScaledHeight());
        GL11.glTexCoord2d(1.0, 1.0);
        GL11.glVertex2d(sr.getScaledWidth(), 0.0);
        GL11.glEnd();
        GL20.glUseProgram(0);
    }

    public void bind() {
        GL20.glUseProgram(this.program);
    }

    public int getProgram() {
        return this.program;
    }

    public void addDefaultUniforms(boolean detectmouse) {
        if (Mouse.getX() > 957) {
            this.mousemove -= 0.002f;
        } else {
            this.mousemove += 0.002f;
        }
        float n3 = getMouseX();
        float n4 = getMouseY();
        FloatBuffer floatBuffer3 = BufferUtils.createFloatBuffer(2);
        floatBuffer3.position(0);
        floatBuffer3.put(n3);
        floatBuffer3.put(n4);
        floatBuffer3.flip();
        GL20.glUniform2f(GL20.glGetUniformLocation(this.program, "resolution"), (float) mc.displayWidth, (float) mc.displayHeight);
        float time = (float) (System.currentTimeMillis() - this.startTime) / 1000.0f;
        GL20.glUniform1f(GL20.glGetUniformLocation(this.program, "time"), time);
        if (detectmouse) {
            GL20.glUniform2f(GL20.glGetUniformLocation(this.program, "mouse"), this.mousemove, 0.0f);
        } else {
            GL20.glUniform2(GL20.glGetUniformLocation(this.program, "mouse"), floatBuffer3);
        }
    }
}

