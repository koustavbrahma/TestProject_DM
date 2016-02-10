package koustav.duelmasters.main.androidgameshaderprogram;

import android.content.Context;

import java.util.ArrayList;

import koustav.duelmasters.R;
import koustav.duelmasters.main.androidgameopenglutil.GLLight;
import koustav.duelmasters.main.androidgameopenglutil.GLMaterial;

import static android.opengl.GLES20.*;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform3fv;

/**
 * Created by Koustav on 2/3/2016.
 */
public class TextureShaderProgramLight extends ShaderProgram {
    // Uniform locations
    private final int uMVMatrixLocation;
    private final int uIT_MVMatrixLocation;
    private final int uMVPMatrixLocation;
    private final int uTextureUnitLocation;

    private final int uMaterialKa;
    private final int uMaterialKd;
    private final int uMaterialKs;
    private final int uMaterialShininess;

    private final int uLightCount;
    private final int[] uLightType;
    private final int[] uLightPosition;
    private final int[] uLightDirection;
    private final int[] uLightIntensity;
    private final int[] uLightExponent;
    private final int[] uLightCutoff;

    // Attribute locations
    private final int aPositionLocation;
    private final int aNormalLocation;
    private final int aTextureCoordinatesLocation;

    public TextureShaderProgramLight(Context context) {
        super(context, R.raw.vertex_shader_texture_light, R.raw.fragment_shader_texture_light);
        // Retrieve uniform locations for the shader program.
        uMVMatrixLocation = glGetUniformLocation(program, U_MV_MATRIX);
        uIT_MVMatrixLocation = glGetUniformLocation(program, U_IT_MV_MATRIX);
        uMVPMatrixLocation = glGetUniformLocation(program, U_MVP_MATRIX);

        uMaterialKa = glGetUniformLocation(program, U_MATERIAL_KA);
        uMaterialKd = glGetUniformLocation(program, U_MATERIAL_KD);
        uMaterialKs = glGetUniformLocation(program, U_MATERIAL_KS);
        uMaterialShininess = glGetUniformLocation(program, U_MATERIAL_SHININESS);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);

        uLightCount = glGetUniformLocation(program, U_LIGHT_COUNT);
        uLightType = new int[8];
        for (int i = 0; i < uLightType.length; i++) {
            uLightType[i] = glGetUniformLocation(program, U_LIGHT_TYPE(i));
        }

        uLightPosition = new int[8];
        for (int i = 0; i < uLightPosition.length; i++) {
            uLightPosition[i] = glGetUniformLocation(program, U_LIGHT_POSITION(i));
        }

        uLightDirection = new int[8];
        for (int i = 0; i < uLightDirection.length; i++) {
            uLightDirection[i] = glGetUniformLocation(program, U_LIGHT_DIRECTION(i));
        }

        uLightIntensity = new int[8];
        for (int i = 0; i < uLightIntensity.length; i++) {
            uLightIntensity[i] = glGetUniformLocation(program, U_LIGHT_INTENSITY(i));
        }

        uLightExponent = new int[8];
        for (int i = 0; i < uLightExponent.length; i++) {
            uLightExponent[i] = glGetUniformLocation(program, U_LIGHT_EXPONENT(i));
        }

        uLightCutoff = new int[8];
        for (int i = 0; i < uLightCutoff.length; i++) {
            uLightCutoff[i] = glGetUniformLocation(program, U_LIGHT_CUTOFF(i));
        }
        // Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aNormalLocation = glGetAttribLocation(program, A_NORMAL);
        aTextureCoordinatesLocation =
                glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    private void setLight(int LightCount, ArrayList<GLLight> Light) {
        glUniform1i(uLightCount, LightCount);
        for (int i = 0; i < LightCount; i++) {
            glUniform1i(uLightType[i], Light.get(i).getLightType());
            glUniform4fv(uLightPosition[i], 1, Light.get(i).Position, 0);
            glUniform3fv(uLightDirection[i], 1, Light.get(i).Direction, 0);
            glUniform3fv(uLightIntensity[i], 1, Light.get(i).Intensity, 0);
            glUniform1f(uLightExponent[i], Light.get(i).Exponent);
            glUniform1f(uLightCutoff[i], Light.get(i).Cutoff);
        }
    }

    public void setUniforms(float[] mvMatrix,
                            float[] it_mvMatrix,
                            float[] mvpMatrix,
                            int LightCount,
                            ArrayList<GLLight> Light,
                            GLMaterial Material,
                            int textureId) {
        // Pass the matrix into the shader program.
        glUniformMatrix4fv(uMVMatrixLocation, 1, false, mvMatrix, 0);
        glUniformMatrix4fv(uIT_MVMatrixLocation, 1, false, it_mvMatrix, 0);
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0);

        glUniform3fv(uMaterialKa, 1, Material.Ka, 0);
        glUniform3fv(uMaterialKd, 1, Material.Kd, 0);
        glUniform3fv(uMaterialKs, 1, Material.Ks, 0);
        glUniform1f(uMaterialShininess, Material.Shininess);

        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);
        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureId);
        // Tell the texture uniform sampler to use this texture in the shader by
        // telling it to read from texture unit 0.
        glUniform1i(uTextureUnitLocation, 0);
        setLight(LightCount, Light);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getNormalAttributeLocation() {
        return aNormalLocation;
    }

    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }
}
