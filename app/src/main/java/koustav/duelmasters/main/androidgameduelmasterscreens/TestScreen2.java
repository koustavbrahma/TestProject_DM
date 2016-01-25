package koustav.duelmasters.main.androidgameduelmasterscreens;

import koustav.duelmasters.main.androidgamegeometry.Base;
import koustav.duelmasters.main.androidgamegeometry.CardProjection;
import koustav.duelmasters.main.androidgamegeometry.InvCardProjection;
import koustav.duelmasters.main.androidgamegeometry.Puck;
import koustav.duelmasters.main.androidgamegeometry.Table;
import koustav.duelmasters.main.androidgameopenglutil.TextureHelper;
import koustav.duelmasters.main.androidgamesframework.Screen;
import koustav.duelmasters.main.androidgamesframeworkimpl.AndroidGame;
import koustav.duelmasters.main.androidgamesframeworkimpl.AndroidOpenGLRenderView;
import koustav.duelmasters.main.androidgameshaderprogram.TextureShaderProgram;
import koustav.duelmasters.main.androidgameshaderprogram.UniformColorShaderProgram;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

/**
 * Created by Koustav on 1/24/2016.
 */
public class TestScreen2 extends Screen {
    private Base base;
    private Table table;
    private Puck puck;
    private CardProjection cardProjection;
    private InvCardProjection invCardProjection;
    private TextureShaderProgram textureProgram;
    private UniformColorShaderProgram colorProgram;
    private int basetexture;
    private int tabletexture;
    private int cardbackside;
    private float[] modelMatrix = new float[16];

    private float[] viewProjectionMatrix = new float[16];
    private float[] modelViewProjectionMatrix = new float[16];

    public TestScreen2(AndroidGame game) {
        super(game);
        base = new Base();
        table = new Table();
        puck = new Puck(0.02f, 0.02f, 32);
        cardProjection = new CardProjection();
        invCardProjection = new InvCardProjection();
    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public void present(float deltaTime) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);
        viewProjectionMatrix = ((AndroidOpenGLRenderView)game.getRenderObj()).getViewProjectionMatrix();
        // Draw the table.
        positionObjectInXZScene(0f, 0f, 0f);
        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, basetexture);
        base.bindData(textureProgram);
        base.draw();
        textureProgram.setUniforms(modelViewProjectionMatrix, tabletexture);
        table.bindData(textureProgram);
        table.draw();
        positionObjectInXZScene(0f, 0f, 0.6f);
        textureProgram.setUniforms(modelViewProjectionMatrix, cardbackside);
        cardProjection.bindData(textureProgram);
        cardProjection.draw();
        positionObjectInXZScene(0f, 0f, -0.6f);
        textureProgram.setUniforms(modelViewProjectionMatrix, cardbackside);
        invCardProjection.bindData(textureProgram);
        invCardProjection.draw();
        // Draw the puck.
        colorProgram.useProgram();
        positionObjectInScenetmp(0f, puck.height / 2f, 0f);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorProgram);
        puck.draw();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
        textureProgram = new TextureShaderProgram(game);
        colorProgram = new UniformColorShaderProgram(game);
        basetexture = TextureHelper.loadTexture(game, "Base_1.png");
        tabletexture = TextureHelper.loadTexture(game, "duelmaze.png");
        cardbackside = TextureHelper.loadTexture(game, "cardbackside.png");
    }

    @Override
    public void dispose() {

    }

    @Override
    public void back() {

    }

    private void positionObjectInXZScene(float x, float y, float z) {
        // The table is defined in terms of X & Y coordinates, so we rotate it
        // 90 degrees to lie flat on the XZ plane.
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
                0, modelMatrix, 0);
    }

    private void positionObjectInScenetmp(float x, float y, float z) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
                0, modelMatrix, 0);
    }
}
