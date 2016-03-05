package koustav.duelmasters.main.androidgameopenglobjectmodels;

import java.util.List;

import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;
import koustav.duelmasters.main.androidgameopenglutil.GLMaterial;
import koustav.duelmasters.main.androidgameopenglutil.ObjectBuilder;
import koustav.duelmasters.main.androidgameopenglutil.VertexArray;

/**
 * Created by Koustav on 3/5/2016.
 */
public class Cube extends GLObject {
    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int NORMAL_COMPONENT_COUNT = 3;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 3;
    private static final int STRIDE =
            (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    private final VertexArray vertexArray;
    private final List<ObjectBuilder.DrawCommand> drawList;
    public Cube(GLMaterial Material, float width, float length, float height, boolean texture) {
        super(Material);
        GLCube cube = new GLCube(new GLPoint(0f, 0f, 0f), width, length, height);
        ObjectBuilder.GeneratedData generatedData =
                ObjectBuilder.createCube(cube, texture);
        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }

    public void bindData(int aPositionLocation, int aNormalLocation, int aTextureCoordinatesLocation) {
        vertexArray.setVertexAttribPointer(
                0,
                aPositionLocation,
                POSITION_COMPONENT_COUNT,
                STRIDE);
        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                aNormalLocation,
                NORMAL_COMPONENT_COUNT,
                STRIDE);
        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT,
                aTextureCoordinatesLocation,
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE);
    }

    public void draw() {
        for (ObjectBuilder.DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }
}
