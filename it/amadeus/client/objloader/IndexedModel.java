package it.amadeus.client.objloader;

import it.amadeus.client.objloader.OBJLoader.OBJIndex;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;


public class IndexedModel {
    private final ArrayList<Vector3f> vertices;
    private final ArrayList<Vector2f> texCoords;
    private final ArrayList<Vector3f> normals;
    private final ArrayList<Vector3f> tangents;
    private final ArrayList<Integer> indices;
    private final ArrayList<OBJIndex> objindices;

    public IndexedModel() {
        this.vertices = new ArrayList<>();
        this.texCoords = new ArrayList<>();
        this.normals = new ArrayList<>();
        this.tangents = new ArrayList<>();
        this.indices = new ArrayList<>();
        this.objindices = new ArrayList<>();
    }

    public ArrayList<Vector3f> getPositions() {
        return vertices;
    }

    public ArrayList<Vector2f> getTexCoords() {
        return texCoords;
    }

    public ArrayList<Vector3f> getNormals() {
        return normals;
    }

    public ArrayList<Integer> getIndices() {
        return indices;
    }

    public ArrayList<Vector3f> getTangents() {
        return tangents;
    }

    public void toMesh(Mesh mesh) {
        ArrayList<Vertex> verticesList = new ArrayList<>();
        int n = Math.min(vertices.size(), Math.min(texCoords.size(), normals.size()));
        for (int i = 0; i < n; i++) {
            Vertex vertex = new Vertex(vertices.get(i),
                    texCoords.get(i),
                    normals.get(i), new Vector3f());
            verticesList.add(vertex);
        }
        Integer[] indicesArray = indices.toArray(new Integer[0]);
        Vertex[] verticesArray = verticesList.toArray(new Vertex[0]);
        int[] indicesArrayInt = new int[indicesArray.length];
        for (int i = 0; i < indicesArray.length; i++)
            indicesArrayInt[i] = indicesArray[i];
        mesh.vertices = verticesArray;
        mesh.indices = indicesArrayInt;
    }

    public void computeNormals() {
        for (int i = 0; i < indices.size(); i += 3) {
            int i0 = indices.get(i);
            int i1 = indices.get(i + 1);
            int i2 = indices.get(i + 2);

            Vector3f v = (Vector3f) vertices.get(i1).clone();
            v.sub(vertices.get(i0));
            Vector3f l0 = v;
            v = (Vector3f) vertices.get(i2).clone();
            v.sub(vertices.get(i0));
            Vector3f l1 = v;
            v = (Vector3f) l0.clone();
            v.cross(l0, l1);
            Vector3f normal = v;

            v = (Vector3f) normals.get(i0).clone();
            v.add(normal);
            normals.set(i0, v);
            v = (Vector3f) normals.get(i1).clone();
            v.add(normal);
            normals.set(i1, v);
            v = (Vector3f) normals.get(i2).clone();
            v.add(normal);
            normals.set(i2, v);
        }

        for (Vector3f normal : normals) {
            normal.normalize();
        }
    }

    public ArrayList<OBJIndex> getObjIndices() {
        return objindices;
    }

    public Vector3f computeCenter() {
        float x = 0;
        float y = 0;
        float z = 0;
        for (Vector3f position : vertices) {
            x += position.x;
            y += position.y;
            z += position.z;
        }
        x /= vertices.size();
        y /= vertices.size();
        z /= vertices.size();
        return new Vector3f(x, y, z);
    }
}
