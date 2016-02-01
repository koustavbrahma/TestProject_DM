package koustav.duelmasters.main.androidgameopenglobjects;

import java.util.List;

import koustav.duelmasters.main.androidgameopenglutil.ObjectBuilder;
import koustav.duelmasters.main.androidgameopenglutil.ObjectBuilder.*;
import koustav.duelmasters.main.androidgameopenglutil.VertexArray;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;
import koustav.duelmasters.main.androidgameshaderprogram.UniformColorShaderProgram;

/**
 * Created by Koustav on 1/23/2016.
 */
public class Puck {
    private static final int POSITION_COMPONENT_COUNT = 3;
    public final float radius, height;
    private final VertexArray vertexArray;
    private final List<DrawCommand> drawList;

    public Puck(float radius, float height, int numPointsAroundPuck) {
        GeneratedData generatedData = ObjectBuilder.createPuck(new GLCylinder(
                new GLPoint(0f, 0f, 0f), radius, height), numPointsAroundPuck);

        this.radius = radius;
        this.height = height;
        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }

    public void bindData(UniformColorShaderProgram colorProgram) {
        vertexArray.setVertexAttribPointer(0,
                colorProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, 0);
    }

    public void draw() {
        for (DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }
}
