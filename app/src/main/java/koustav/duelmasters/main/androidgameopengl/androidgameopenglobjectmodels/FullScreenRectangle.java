package koustav.duelmasters.main.androidgameopengl.androidgameopenglobjectmodels;

import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.VertexArray;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by Koustav on 1/24/2016.
 */
public class FullScreenRectangle {
    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
            + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA = {
            // Order of coordinates: X, Y, S, T
            // Triangle Fan
            0f, 0f, 0.5f, 0.5f,
            -1.0f, -1.0f, 0f, 0.0f,
            1.0f, -1.0f, 1f, 0.0f,
            1.0f, 1.0f, 1f, 1.0f,
            -1.0f, 1.0f, 0f, 1.0f,
            -1.0f, -1.0f, 0f, 0.0f };

    private final VertexArray vertexArray;

    public FullScreenRectangle() {
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(int aPositionLocation, int aTextureCoordinatesLocation) {
        vertexArray.setVertexAttribPointer(
                0,
                aPositionLocation,
                POSITION_COMPONENT_COUNT,
                STRIDE);
        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                aTextureCoordinatesLocation,
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE);
    }

    public void draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }
}
