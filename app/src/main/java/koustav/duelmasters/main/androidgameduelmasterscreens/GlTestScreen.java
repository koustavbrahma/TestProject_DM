package koustav.duelmasters.main.androidgameduelmasterscreens;

import koustav.duelmasters.main.androidgameopenglobjects.Mallet;
import koustav.duelmasters.main.androidgameopenglobjects.Puck;
import koustav.duelmasters.main.androidgameopenglobjects.Table;
import koustav.duelmasters.main.androidgameopenglutil.TextureHelper;
import koustav.duelmasters.main.androidgamesframework.Screen;
import koustav.duelmasters.main.androidgamesframeworkimpl.AndroidGame;
import koustav.duelmasters.main.androidgamesframeworkimpl.AndroidOpenGLRenderView;
import koustav.duelmasters.main.androidgameshaderprogram.TextureShaderProgram;
import koustav.duelmasters.main.androidgameshaderprogram.UniformColorShaderProgram;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;
/**
 * Created by Koustav on 1/13/2016.
 */
public class GlTestScreen extends Screen {
    private Table table;
    private Mallet mallet;
    private Puck puck;
    private TextureShaderProgram textureProgram;
    private UniformColorShaderProgram colorProgram;
    private int texture;
    private float[] viewMatrix;
    private float[] projectionMatrix;
    private float[] modelMatrix = new float[16];

    private float[] viewProjectionMatrix = new float[16];
    private float[] modelViewProjectionMatrix = new float[16];

    public GlTestScreen(AndroidGame game) {
        super(game);
        modelMatrix = new float[16];
        viewProjectionMatrix = new float[16];
        modelViewProjectionMatrix = new float[16];

        table = new Table();
        mallet = new Mallet(0.08f, 0.15f, 32);
        puck = new Puck(0.06f, 0.02f, 32);
    }

    @Override
    public void update(float deltaTime, float totalTime) {
    }

    @Override
    public void present(float deltaTime, float totalTime) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);
        projectionMatrix = ((AndroidOpenGLRenderView)game.getRenderObj()).getProjectionMatrix();
        viewMatrix = ((AndroidOpenGLRenderView)game.getRenderObj()).getViewMatrix();
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        // Draw the table.
        positionTableInScene();
        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();
        // Draw the mallets.
        /*
        positionObjectInScene(0f, mallet.height / 2f, -0.4f);
        colorProgram.useProgram();
        colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f);
        mallet.bindData(colorProgram);
        mallet.draw();
        positionObjectInScene(0f, mallet.height / 2f, 0.4f);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
        // Note that we don't have to define the object data twice -- we just
        // draw the same mallet again but in a different position and with a
        // different color.
        mallet.draw();
        // Draw the puck.
        positionObjectInScene(0f, puck.height / 2f, 0f);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorProgram);
        puck.draw();*/
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
        textureProgram = new TextureShaderProgram(game);
        colorProgram = new UniformColorShaderProgram(game);
        texture = TextureHelper.loadTexture(game, "duelmaze.png");
    }

    @Override
    public void dispose() {

    }

    @Override
    public void back() {

    }

    private void positionTableInScene() {
        // The table is defined in terms of X & Y coordinates, so we rotate it
        // 90 degrees to lie flat on the XZ plane.
        setIdentityM(modelMatrix, 0);
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
                0, modelMatrix, 0);
    }

    private void positionObjectInScene(float x, float y, float z) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
                0, modelMatrix, 0);
    }
}
