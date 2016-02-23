package koustav.duelmasters.main.androidgameopenglobjects;

import koustav.duelmasters.main.androidgameopenglutil.GLMaterial;
import koustav.duelmasters.main.androidgameopenglutil.VertexArray;
import koustav.duelmasters.main.androidgameshaderprogram.TextureShaderProgramLight;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by Koustav on 2/2/2016.
 */
public class XZRectangle extends GLObject{
    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int NORMAL_COMPONENT_COUNT = 3;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE =
            (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT +TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;


    private final VertexArray vertexArray;

    public XZRectangle(GLMaterial Material, float width, float height, int orientation) {
        super(Material);

        orientation = orientation % 4;

        float[] S = {0f, 1.0f, 1.0f, 0f};
        float[] T = {1.0f, 1.0f, 0f, 0f};

        float tmp, tmp1;
        for (int i = 0; i < orientation; i++) {
            tmp = S[3];
            tmp1 = T[3];

            for (int j = 3 ; j > 0; j-- ) {
                S[j] = S[j - 1];
                T[j] = T[j - 1];
            }

            S[0] = tmp;
            T[0] = tmp1;
        }

        float[] VERTEX_DATA = {
                // Order of coordinates: X, Y, Z, normx, normy, normz, S, T
                // Triangle Fan
                0f, 0f, 0f, 0f, 1.0f, 0f, 0.5f, 0.5f,
                -width/2, 0f, -height/2, 0f, 1.0f, 0f, S[0], T[0],
                width/2, 0f, -height/2, 0f, 1.0f, 0f, S[1], T[1],
                width/2, 0f, height/2, 0f, 1.0f, 0f, S[2], T[2],
                -width/2, 0f, height/2, 0f, 1.0f, 0f, S[3], T[3],
                -width/2, 0f, -height/2, 0f, 1.0f, 0f, S[0], T[0] };

        vertexArray = new VertexArray(VERTEX_DATA);
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
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }
}
