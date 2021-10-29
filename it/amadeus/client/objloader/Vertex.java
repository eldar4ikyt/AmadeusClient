package it.amadeus.client.objloader;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

@AllArgsConstructor
@Getter
public class Vertex {
    private final Vector3f pos;
    private final Vector2f texCoords;
    private final Vector3f normal;
    private final Vector3f tangent;
}
