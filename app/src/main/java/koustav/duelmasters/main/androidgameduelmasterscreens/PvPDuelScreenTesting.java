package koustav.duelmasters.main.androidgameduelmasterscreens;

import koustav.duelmasters.main.androidgameduelmastersassetsandresourcesforscreen.AssetsAndResourceForPvP;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.WidgetPosition;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardStackWidget;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;
import koustav.duelmasters.main.androidgameopenglobjectmodels.Cube;
import koustav.duelmasters.main.androidgameopenglobjectmodels.FullScreenRectangle;
import koustav.duelmasters.main.androidgameopenglobjectmodels.Points;
import koustav.duelmasters.main.androidgameopenglobjectmodels.XZRectangle;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;
import koustav.duelmasters.main.androidgameopenglutil.GLMaterial;
import koustav.duelmasters.main.androidgamesframework.Screen;
import koustav.duelmasters.main.androidgamesframeworkimpl.AndroidGame;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

/**
 * Created by Koustav on 3/6/2016.
 */
public class PvPDuelScreenTesting extends Screen{
    enum GameState {
        Loading,
        Ready,
        Running,
        Paused
    }

    // GameLogic
  //  World world;

    // Objects
    private FullScreenRectangle DisplayRectangle;
    private XZRectangle Base;
    private XZRectangle glCard;
    private Points point;
    private Cube cube;

   // Matrix
    private float[] tempMatrix;
    private float[] modelMatrix;

    private float[] modelViewMatrix;
    private float[] modelViewProjectionMatrix;
    private float[] it_modelViewMatrix;

    // Widget
    CardWidget CardWg;
    CardStackWidget DeckWg;

    public PvPDuelScreenTesting(AndroidGame game) {
        super(game);

        // GameLogic
//        world = new World(game, game.getTurn());

        // Matrix
        tempMatrix = new float[16];
        modelMatrix = new float[16];

        modelViewMatrix = new float[16];
        modelViewProjectionMatrix = new float[16];
        it_modelViewMatrix = new float[16];

        // Load Assets and Resource for this screen
        AssetsAndResourceForPvP.Load(game);

        // Object
        DisplayRectangle = new FullScreenRectangle();
        Base = new XZRectangle(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), 2.0f, 2.0f, 0);
        glCard = new XZRectangle(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), 0.08f, 0.12f, 0);
        point = new Points( new GLPoint(0f, 0f, 0f), 1f, 0f, 0f);
        cube = new Cube(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), 0.08f, 0.05f, 0.12f, true);

        // Widget
        CardWg = new CardWidget();
        DeckWg = new CardStackWidget();
    }

    @Override
    public void update(float deltaTime, float totalTime) {

    }

    @Override
    public void present(float deltaTime, float totalTime) {
        AssetsAndResourceForPvP.ResetCardUsageCount();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, game.getframeBufferWidth(), game.getframeBufferHeight());
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        positionObjectInScene(0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 1f, 1f);
        AssetsAndResourceForPvP.textureShaderProgramLight.useProgram();
        AssetsAndResourceForPvP.textureShaderProgramLight.setUniforms(modelViewMatrix, it_modelViewMatrix,
                modelViewProjectionMatrix, null, AssetsAndResourceForPvP.Light, Base.getMaterial(), AssetsAndResourceForPvP.Base, 0, false);

        Base.bindData(AssetsAndResourceForPvP.textureShaderProgramLight.getPositionAttributeLocation(),
                AssetsAndResourceForPvP.textureShaderProgramLight.getNormalAttributeLocation(),
                AssetsAndResourceForPvP.textureShaderProgramLight.getTextureCoordinatesAttributeLocation());
        Base.draw();

        positionObjectInScene(0.1f, 0f, 0.2f, 0f, 0f, 0f, 0f, 1f, 1f, 1f);
        AssetsAndResourceForPvP.colorShaderProgram.useProgram();
        AssetsAndResourceForPvP.colorShaderProgram.setUniforms(modelViewProjectionMatrix);

        point.bindData(AssetsAndResourceForPvP.colorShaderProgram.getPositionAttributeLocation(),
                AssetsAndResourceForPvP.colorShaderProgram.getColorAttributeLocation());
        point.draw();

        WidgetPosition position = new WidgetPosition();
        position.Centerposition.x = 0;
        position.Centerposition.y = 0.005f;
        position.Centerposition.z = 0.1f;

        CardWg.setTranslateRotateScale(position);
        CardWg.draw();

        position.Centerposition.x = 0.2f;
        position.Centerposition.y = 0.025f;
        position.Centerposition.z = 0f;

        DeckWg.setTranslateRotateScale(position);
        DeckWg.draw();
    }

    @Override
    public void pause() {
        // Free
        AssetsAndResourceForPvP.FreeAssetsAndResources();
    }

    @Override
    public void resume() {
        // Assets and Resources initialization
        AssetsAndResourceForPvP.InitializeAssetsAndResource();

        // Widget
        CardWg.ShadowEnable(false);
        CardWg.LinkGLobject(glCard);
        DeckWg.ShadowEnable(false);
        DeckWg.LinkGLobject(cube);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void back() {

    }

    private void positionObjectInScene(float x, float y, float z, float angle, float X_axis_rot, float Y_axis_rot, float Z_axis_rot,
                                       float scale_x, float scale_y, float scale_z) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        if (angle != 0) {
            rotateM(modelMatrix, 0, angle, X_axis_rot, Y_axis_rot, Z_axis_rot);
        }
        scaleM(modelMatrix, 0, scale_x, scale_y, scale_z);
        multiplyMM(modelViewMatrix, 0, AssetsAndResourceForPvP.viewMatrix, 0, modelMatrix, 0);
        invertM(tempMatrix, 0, modelViewMatrix, 0);
        transposeM(it_modelViewMatrix, 0, tempMatrix, 0);
        multiplyMM(
                modelViewProjectionMatrix, 0,
                AssetsAndResourceForPvP.projectionMatrix, 0,
                modelViewMatrix, 0);
    }
}
