package it.amadeus.client.objloader.mc;

import it.amadeus.client.objloader.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

/**
 * @author jglrxavpok
 */
public class TessellatorModel extends ObjModel {
    public TessellatorModel(String string) {
        super(string);
        try {
            String content = new String(read(Objects.requireNonNull(Model.class.getResourceAsStream(string))),
                    StandardCharsets.UTF_8);
            String startPath = string.substring(0, string.lastIndexOf('/') + 1);
            HashMap<ObjObject, IndexedModel> map = new OBJLoader().loadModel(startPath, content);
            objObjects.clear();
            Set<ObjObject> keys = map.keySet();
            for (ObjObject object : keys) {
                Mesh mesh = new Mesh();
                object.mesh = mesh;
                objObjects.add(object);
                map.get(object).toMesh(mesh);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void renderImpl() {
        this.objObjects.sort((a, b) -> {
            Vec3 v = Minecraft.getMinecraft().getRenderViewEntity().getPositionVector();
            double aDist = v.distanceTo(new Vec3(a.center.x, a.center.y, a.center.z));
            double bDist = v.distanceTo(new Vec3(b.center.x, b.center.y, b.center.z));
            return Double.compare(aDist, bDist);
        });
        for (ObjObject object : objObjects) {
            renderGroup(object);
        }
    }

    @Override
    public void renderGroupImpl(ObjObject obj) {
        Tessellator tess = Tessellator.getInstance();
        WorldRenderer renderer = tess.getWorldRenderer();
        if (obj.mesh == null)
            return;
        if (obj.material != null) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, obj.material.diffuseTexture);
        }
        int[] indices = obj.mesh.indices;
        Vertex[] vertices = obj.mesh.vertices;
        renderer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
        for (int i = 0; i < indices.length; i += 3) {
            int i0 = indices[i];
            int i1 = indices[i + 1];
            int i2 = indices[i + 2];
            Vertex v0 = vertices[i0];
            Vertex v1 = vertices[i1];
            Vertex v2 = vertices[i2];

            renderer.pos(v0.getPos().x, v0.getPos().y, v0.getPos().z).tex(v0.getTexCoords().x, 1f - v0.getTexCoords().y).normal(v0.getNormal().x, v0.getNormal().y, v0.getNormal().z).endVertex();
            renderer.pos(v1.getPos().x, v1.getPos().y, v1.getPos().z).tex(v1.getTexCoords().x, 1f - v1.getTexCoords().y).normal(v1.getNormal().x, v1.getNormal().y, v1.getNormal().z).endVertex();
            renderer.pos(v2.getPos().x, v2.getPos().y, v2.getPos().z).tex(v2.getTexCoords().x, 1f - v2.getTexCoords().y).normal(v2.getNormal().x, v2.getNormal().y, v2.getNormal().z).endVertex();
        }
        tess.draw();
    }
}
