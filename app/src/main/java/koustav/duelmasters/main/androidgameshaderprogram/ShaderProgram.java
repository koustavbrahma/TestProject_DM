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
    // Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
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
}
