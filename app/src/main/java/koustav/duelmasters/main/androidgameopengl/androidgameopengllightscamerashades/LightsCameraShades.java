package koustav.duelmasters.main.androidgameopengl.androidgameopengllightscamerashades;

import java.util.ArrayList;

import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.FrameBufferObject;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLLight;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.MatrixHelper;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkimpl.AndroidGame;
import koustav.duelmasters.main.androidgameshaderprogram.BloomingShaderProgram;
import koustav.duelmasters.main.androidgameshaderprogram.ColorShaderProgram;
import koustav.duelmasters.main.androidgameshaderprogram.CubeTextureShaderProgramLight;
import koustav.duelmasters.main.androidgameshaderprogram.GaussianBlurShaderProgram;
import koustav.duelmasters.main.androidgameshaderprogram.ParticleShaderProgram;
import koustav.duelmasters.main.androidgameshaderprogram.ShaderProgram;
import koustav.duelmasters.main.androidgameshaderprogram.TextureShaderProgram;
import koustav.duelmasters.main.androidgameshaderprogram.TextureShaderProgramLight;
import koustav.duelmasters.main.androidgameshaderprogram.UniformColorShaderLightProgram;
import koustav.duelmasters.main.androidgameshaderprogram.UniformColorShaderProgram;

import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.setLookAtM;

/**
 * Created by Koustav on 9/24/2017.
 */
public abstract class LightsCameraShades {
    public AndroidGame game;

    // Frame buffers
    public FrameBufferObject SceneBuffer;
    public FrameBufferObject ShadowBuffer;

    // Matrix Fixed
    public float[] viewMatrix;
    public float[] projectionMatrix;
    public float[] OrthoProjectionMatrix;
    public float[] depthVPMatrix;
    public float[] invertedViewProjectionMatrix;
    public float[] invertedOrthoProjectionMatrix;

    // Matrix Changeable
    public float[] modelViewMatrix;
    public float[] modelViewProjectionMatrix;
    public float[] modelOrthoProjectionMatrix;
    public float[] it_modelViewMatrix;
    public float[] modelMatrix;
    public float[] tempMatrix;
    public float[] ShadowMatrix;

    // Light
    public ArrayList<GLLight> Light;

    // Fixed paratemers
    public float aspectRatio;
    public GLGeometry.GLPoint CameraPosition;
    public GLGeometry.GLPoint LightPosition;

    // Shaders
    public BloomingShaderProgram bloomingShaderProgram;
    public ColorShaderProgram colorShaderProgram;
    public CubeTextureShaderProgramLight cubeTextureShaderProgramLight;
    public GaussianBlurShaderProgram gaussianBlurShaderProgram;
    public ParticleShaderProgram particleShaderProgram;
    public ShaderProgram shaderProgram;
    public TextureShaderProgram textureProgram;
    public TextureShaderProgramLight textureShaderProgramLight;
    public UniformColorShaderLightProgram uniformColorShaderLightProgram;
    public UniformColorShaderProgram uniformColorShaderProgram;

    public LightsCameraShades(AndroidGame game, float camera_x, float camera_y, float camera_z,
                              float light_x, float light_y, float light_z) {
        // Store the game reference
        this.game = game;

        // Matrix fixed
        viewMatrix = new float[16];
        projectionMatrix = new float[16];
        OrthoProjectionMatrix = new float[16];
        depthVPMatrix = new float[16];
        invertedViewProjectionMatrix = new float[16];
        invertedOrthoProjectionMatrix = new float[16];

        // Matrix Changeable
        modelViewMatrix = new float[16];
        modelViewProjectionMatrix = new float[16];
        modelOrthoProjectionMatrix = new float[16];
        it_modelViewMatrix = new float[16];
        modelMatrix = new float[16];
        tempMatrix = new float[16];
        ShadowMatrix = new float[16];

        // Light
        Light = new ArrayList<GLLight>();

        aspectRatio = (float) game.getframeBufferWidth() / (float) game.getframeBufferHeight();
        CameraPosition = new GLGeometry.GLPoint(camera_x, camera_y, camera_z);
        LightPosition = new GLGeometry.GLPoint(light_x, light_y, light_z);

        // Fixed Matrix initialization
        MatrixHelper.perspectiveM(projectionMatrix, 18f, aspectRatio, 1f, 10f);

        // setup primary view
        setLookAtM(viewMatrix, 0, CameraPosition.x, CameraPosition.y, CameraPosition.z, 0f, 0f, 0.1f, 0f, 1f, 0f);
        multiplyMM(tempMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        invertM(invertedViewProjectionMatrix, 0, tempMatrix, 0);

        // setup ortho projection matrix
        orthoM(OrthoProjectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        invertM(invertedOrthoProjectionMatrix, 0, OrthoProjectionMatrix, 0);

        // setup light source view
        setLookAtM(tempMatrix, 0, LightPosition.x, LightPosition.y, LightPosition.z, 0f, 0f, 0.0f, 0f, 0f, -1.0f);
        multiplyMM(depthVPMatrix, 0, projectionMatrix, 0, tempMatrix, 0);

        // Setup IVP Matrix for UI
        game.getInput().setMatrices(invertedOrthoProjectionMatrix, invertedViewProjectionMatrix);
    }

    public void load() {
        // Frame Buffers
        SceneBuffer = new FrameBufferObject((int) (aspectRatio * (game.getframeBufferHeight() * 0.75)),
                (int) (game.getframeBufferHeight() * 0.75));
        ShadowBuffer = new FrameBufferObject((int) (aspectRatio * (game.getframeBufferHeight()/4)),
                (game.getframeBufferHeight()/4));

        loadShades();
    }

    public void free() {
        // Frame buffer free
        SceneBuffer.freeFBO();
        ShadowBuffer.freeFBO();
        freeShades();
    }

    public abstract void loadShades();
    public abstract void freeShades();
}
