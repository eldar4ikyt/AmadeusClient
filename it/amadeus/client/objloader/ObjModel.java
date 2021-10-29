package it.amadeus.client.objloader;

import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class ObjModel extends Model {
    public List<ObjObject> objObjects;
    protected String filename;

    public ObjModel() {
        this.objObjects = new ArrayList<>();
    }

    public ObjModel(String classpathElem) {
        this();
        this.filename = classpathElem;

        if (filename.contains("/")) {
            setID(filename.substring(filename.lastIndexOf("/") + 1));
        } else {
            setID(filename);
        }
    }

    protected byte[] read(InputStream resource) throws IOException {
        int i;
        byte[] buffer = new byte[65565];
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        while ((i = resource.read(buffer, 0, buffer.length)) != -1) {
            out.write(buffer, 0, i);
        }

        out.flush();
        out.close();
        return out.toByteArray();
    }

    public void renderGroup(ObjObject group) {
        this.renderGroupImpl(group);
    }

    public void render() {
        this.renderImpl();
    }

    protected abstract void renderGroupImpl(ObjObject objGroup);

    protected abstract void renderImpl();
}
