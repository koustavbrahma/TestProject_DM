package koustav.duelmasters.main.androidgameshaderprogram;

import android.content.Context;

import koustav.duelmasters.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE1;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.Matrix.setIdentityM;

/**
 * Created by Koustav on 2/21/2016.
 */
public class BloomingShaderProgram extends ShaderProgram {
    // Uniform locations
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;
    private final int uTextureUnitLocation2;
    private final int uWidthLocation;
    private final int uHeightLocation;
    private final int uWeightCountLocation;
    private final int[] uWeightLocation;
    private final int uPassLocation;
    private final int uTimeLocation;

    // Attribute locations
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;

    public BloomingShaderProgram(Context context) {
        super(context, R.raw.texture_vertex_shader, R.raw.bloom_fragment_shader);
        // Retrieve uniform locations for the shader program.
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
        uTextureUnitLocation2 = glGetUniformLocation(program, U_TEXTURE_UNIT2);
        uWidthLocation = glGetUniformLocation(program, U_WIDTH);
        uHeightLocation = glGetUniformLocation(program, U_HEIGHT);
        uWeightCountLocation = glGetUniformLocation(program, U_WEIGHT_COUNT);
        uPassLocation = glGetUniformLocation(program, U_PASS);
        uTimeLocation = glGetUniformLocation(program, U_TIME);

        uWeightLocation = new int[10];

        for (int i = 0; i < uWeightLocation.length; i++) {
            uWeightLocation[i] = glGetUniformLocation(program, U_WEIGHT(i));
        }

        // Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation =
                glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    private void setWeight(float[] weight) {
        if (weight.length > 10)
            throw new IllegalArgumentException("more than 10 weight not supported for this Shader");
        glUniform1i(uWeightCountLocation, weight.length);
        for (int i = 0; i < weight.length; i++) {
            glUniform1f(uWeightLocation[i], weight[i]);
        }
    }

    public void setUniforms(int textureId, int textureId2, int width, int height, float[] weight, float elapsedTime) {
        // Pass the matrix into the shader program.
        float[] matrix = new float[16];
        setIdentityM(matrix, 0);
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform1i(uWidthLocation, width);
        glUniform1i(uHeightLocation, height);
        glUniform1f(uTimeLocation, elapsedTime);
        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);
        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureId);
        // Tell the texture uniform sampler to use this texture in the shader by
        // telling it to read from texture unit 0.
        glUniform1i(uTextureUnitLocation, 0);

        glActiveTexture(GL_TEXTURE1);
        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureId2);
        // Tell the texture uniform sampler to use this texture in the shader by
        // telling it to read from texture unit 1.
        glUniform1i(uTextureUnitLocation2, 1);
        setWeight(weight);
    }

    public void setPass(int pass) {
        glUniform1i(uPassLocation, pass);
    }
    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }
}
