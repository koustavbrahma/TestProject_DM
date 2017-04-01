package koustav.duelmasters.main.androidgameopengl.androidgameopenglobjectmodels;

import java.util.List;

import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLMaterial;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.ObjectBuilder;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.VertexArray;

/**
 * Created by Koustav on 11/17/2016.
 */
public class UniformXZRectangle extends GLObject {
    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int NORMAL_COMPONENT_COUNT = 3;
    private static final int STRIDE =
            (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private final VertexArray vertexArray;
    private final List<ObjectBuilder.DrawCommand> drawList;
    public final float width;
    public final float height;

    public UniformXZRectangle(GLMaterial Material, float width, float height, int orientation) {
        super(Material);
        GLGeometry.GLRectangle rectangle = new GLGeometry.GLRectangle(new GLGeometry.GLPoint(0f, 0f, 0f), width, height);
        ObjectBuilder.GeneratedData generatedData =
                ObjectBuilder.createRectangle(rectangle, false, 0f, 0f, 0f , 0f, orientation);
        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
        this.width = width;
        this.height = height;
    }

    @Override
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
    }

    @Override
    public void draw() {
        for (ObjectBuilder.DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }
}
