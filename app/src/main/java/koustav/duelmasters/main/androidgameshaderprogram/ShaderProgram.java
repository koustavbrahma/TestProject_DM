package koustav.duelmasters.main.androidgameshaderprogram;

import android.content.Context;

import koustav.duelmasters.main.androidgameopenglutil.ShaderHelper;
import koustav.duelmasters.main.androidgamesframeworkimpl.AndroidGame;

import static android.opengl.GLES20.*;
/**
 * Created by Koustav on 1/18/2016.
 */
public class ShaderProgram {
    // Uniform constants
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
    protected static final String U_COLOR = "u_Color";
    protected static final String U_TIME = "u_Time";
    protected static final String U_MV_MATRIX = "u_MVMatrix";
    protected static final String U_IT_MV_MATRIX = "u_IT_MVMatrix";
    protected static final String U_MVP_MATRIX = "u_MVPMatrix";

    protected static final String U_MATERIAL_KA = "u_Material.Ka";
    protected static final String U_MATERIAL_KD = "u_Material.Kd";
    protected static final String U_MATERIAL_KS = "u_Material.Ks";
    protected static final String U_MATERIAL_SHININESS = "u_Material.Shininess";
    protected static final String U_LIGHT_COUNT = "u_LightCount";

    // Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_NORMAL = "a_Normal";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
    protected static final String A_DIRECTION_VECTOR = "a_DirectionVector";
    protected static final String A_PARTICLE_START_TIME = "a_ParticleStartTime";

    // Shader program
    protected final int program;

    protected ShaderProgram(Context context, int vertexShaderResourceId,
                            int fragmentShaderResourceId) {
        // Compile the shaders and link the program.
        program = ShaderHelper.buildProgram(
                ((AndroidGame) context).getFileIO().readTextFileFromResource(vertexShaderResourceId),
                ((AndroidGame) context).getFileIO().readTextFileFromResource(fragmentShaderResourceId));
    }

    public void useProgram() {
        // Set the current OpenGL shader program to this program.
        glUseProgram(program);
    }

    protected String U_LIGHT_TYPE(int i) {
        return "u_Light[" + i +"].Type";
    }

    protected String U_LIGHT_POSITION(int i) {
        return "u_Light[" + i + "].Position";
    }

    protected String U_LIGHT_DIRECTION(int i) {
        return "u_Light[" + i + "].Direction";
    }

    protected String U_LIGHT_INTENSITY(int i) {
        return "u_Light[" + i + "].Intensity";
    }

    protected String U_LIGHT_EXPONENT(int i) {
        return "u_Light[" + i + "].Exponent";
    }

    protected String U_LIGHT_CUTOFF(int i) {
        return "u_Light[" + i + "].Cutoff";
    }
}
