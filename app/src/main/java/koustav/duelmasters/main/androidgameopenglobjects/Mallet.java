package koustav.duelmasters.main.androidgameopenglobjects;

import java.util.List;

import koustav.duelmasters.main.androidgameopenglutil.GLMaterial;
import koustav.duelmasters.main.androidgameopenglutil.ObjectBuilder;
import koustav.duelmasters.main.androidgameopenglutil.VertexArray;
import koustav.duelmasters.main.androidgameopenglutil.ObjectBuilder.*;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;
import koustav.duelmasters.main.androidgameshaderprogram.UniformColorShaderLightProgram;
import koustav.duelmasters.main.androidgameshaderprogram.UniformColorShaderProgram;

/**
 * Created by Koustav on 1/23/2016.
 */
public class Mallet extends GLObject{
    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int NORMAL_COMPONENT_COUNT = 3;
    private static final int STRIDE =
            (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    public final float radius;
    public final float height;
    private final VertexArray vertexArray;
    private final List<DrawCommand> drawList;

    public Mallet(GLMaterial Material, float radius, float height, int numPointsAroundMallet) {
        super(Material);
        GeneratedData generatedData = ObjectBuilder.createMallet(new GLPoint(0f,
                0f, 0f), radius, height, numPointsAroundMallet);
        this.radius = radius;
        this.height = height;
        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }

    public void bindData(int aPositionLocation, int aNormalLocation) {
        vertexArray.setVertexAttribPointer(0,
                aPositionLocation,
                POSITION_COMPONENT_COUNT, STRIDE);
        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                aNormalLocation,
                NORMAL_COMPONENT_COUNT,
                STRIDE);
    }

    public void draw() {
        for (DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }
}
