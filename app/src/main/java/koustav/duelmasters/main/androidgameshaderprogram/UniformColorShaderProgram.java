package koustav.duelmasters.main.androidgameshaderprogram;

import android.content.Context;

import koustav.duelmasters.R;

import static android.opengl.GLES20.*;
/**
 * Created by Koustav on 1/23/2016.
 */
public class UniformColorShaderProgram extends ShaderProgram {
    // Uniform locations
    private final int uSkipColor;
    private final int uMatrixLocation;
    // Attribute locations
    private final int aPositionLocation;
    private final int uColorLocation;

    public UniformColorShaderProgram(Context context) {
        super(context, R.raw.simple_vertex_shader,
                R.raw.simple_fragment_shader);
        // Retrieve uniform locations for the shader program.
        uSkipColor = glGetUniformLocation(program, U_SKIP_COLOR);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        // Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        uColorLocation = glGetUniformLocation(program, U_COLOR);
    }

    public void setUniforms(float[] matrix, float r, float g, float b) {
        glUniform1i(uSkipColor, game.getGLFragColoringSkip());
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform4f(uColorLocation, r, g, b, 1f);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
}
