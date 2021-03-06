package koustav.duelmasters.main.androidgameopengl.androidgameopenglobjectmodels;

import java.util.List;

import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLMaterial;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.ObjectBuilder;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.ObjectBuilder.*;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.VertexArray;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLGeometry.*;

/**
 * Created by Koustav on 1/23/2016.
 */
public class Puck extends GLObject{
    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int NORMAL_COMPONENT_COUNT = 3;
    private static final int STRIDE =
            (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    public final float radius, height;
    private final VertexArray vertexArray;
    private final List<DrawCommand> drawList;

    public Puck(GLMaterial Material, float radius, float height, int numPointsAroundPuck) {
        super(Material);
        GeneratedData generatedData = ObjectBuilder.createPuck(new GLCylinder(
                new GLPoint(0f, 0f, 0f), radius, height), numPointsAroundPuck);

        this.radius = radius;
        this.height = height;
        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }

    @Override
    public void bindData(int aPositionLocation, int aNormalLocation, int aTextureCoordinatesLocation) {
        vertexArray.setVertexAttribPointer(0,
                aPositionLocation,
                POSITION_COMPONENT_COUNT,
                STRIDE);
        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                aNormalLocation,
                NORMAL_COMPONENT_COUNT,
                STRIDE);
    }

    @Override
    public void draw() {
        for (DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }
}
