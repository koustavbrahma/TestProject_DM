package koustav.duelmasters.main.androidgameopengl.androidgameopenglobjectmodels;

import java.util.List;

import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLMaterial;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.ObjectBuilder;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.VertexArray;

/**
 * Created by Koustav on 2/11/2016.
 */
public class Sphere extends GLObject{
    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int NORMAL_COMPONENT_COUNT = 3;
    private static final int STRIDE =
            (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    public final float radius;
    private final VertexArray vertexArray;
    private final List<ObjectBuilder.DrawCommand> drawList;

    public Sphere(GLMaterial Material, float radius, int numPointsAroundPuck) {
        super(Material);
        ObjectBuilder.GeneratedData generatedData =
                ObjectBuilder.createSphere(new GLGeometry.GLPoint(0f, 0f, 0f), radius,
                        numPointsAroundPuck);

        this.radius = radius;
        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }

    @Override
    public void bindData(int aPositionLocation, int aNormalLocation, int aTextureCoordinatesLocation) {
        vertexArray.setVertexAttribPointer(0,
                aPositionLocation,
                POSITION_COMPONENT_COUNT, STRIDE);
        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                aNormalLocation,
                NORMAL_COMPONENT_COUNT,
                STRIDE);
    }

    @Override
    public void draw() {
        for (ObjectBuilder.DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }
}
