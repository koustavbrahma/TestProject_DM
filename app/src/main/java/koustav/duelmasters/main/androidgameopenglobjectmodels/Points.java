package koustav.duelmasters.main.androidgameopenglobjectmodels;

import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;
import koustav.duelmasters.main.androidgameopenglutil.VertexArray;
import koustav.duelmasters.main.androidgameshaderprogram.ColorShaderProgram;

import static android.opengl.GLES20.*;
/**
 * Created by Koustav on 1/18/2016.
 */
public class Points {
    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE =
            (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT)
                    * BYTES_PER_FLOAT;
    private static float[] VERTEX_DATA;
    private final VertexArray vertexArray;

    public Points (GLPoint point, float R, float G, float B) {
        VERTEX_DATA = new float[6];
        VERTEX_DATA[0] = point.x;
        VERTEX_DATA[1] = point.y;
        VERTEX_DATA[2] = point.z;
        VERTEX_DATA[3] = R;
        VERTEX_DATA[4] = G;
        VERTEX_DATA[5] = B;
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(int aPositionLocation, int aColorLocation) {
        vertexArray.setVertexAttribPointer(
                0,
                aPositionLocation,
                POSITION_COMPONENT_COUNT,
                STRIDE);
        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                aColorLocation,
                COLOR_COMPONENT_COUNT,
                STRIDE);
    }

    public void draw() {
        glDrawArrays(GL_POINTS, 0, 1);
    }
}
