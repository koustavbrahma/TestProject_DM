package koustav.duelmasters.main.androidgameduelmasterscreens;

import java.util.ArrayList;
import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Maze;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.WidgetPosition;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.World;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardStackWidget;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;
import koustav.duelmasters.main.androidgameopenglobjectmodels.Cube;
import koustav.duelmasters.main.androidgameopenglobjectmodels.FullScreenRectangle;
import koustav.duelmasters.main.androidgameopenglobjectmodels.Points;
import koustav.duelmasters.main.androidgameopenglobjectmodels.ScreenRectangle;
import koustav.duelmasters.main.androidgameopenglobjectmodels.XZRectangle;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;
import koustav.duelmasters.main.androidgameopenglutil.GLMaterial;
import koustav.duelmasters.main.androidgamesframework.Input;
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
    //World world;
    ArrayList<Cards> CardStack;


    // Objects
    private FullScreenRectangle DisplayRectangle;
    private XZRectangle Base;
    private XZRectangle glCard;
    private Points point;
    private Cube cube;
    private ScreenRectangle DisplayCard;

   // Matrix
    private float[] tempMatrix;
    private float[] modelMatrix;

    private float[] modelViewMatrix;
    private float[] modelViewProjectionMatrix;
    private float[] it_modelViewMatrix;

    // Widget
    CardWidget CardWg;
    CardStackWidget DeckWg;
    CardWidget CardWg2;

    public PvPDuelScreenTesting(AndroidGame game) {
        super(game);

        // GameLogic
        //world = new World(game, game.getTurn());
        CardStack = new ArrayList<Cards>();

        for (int i = 0; i < 40; i++) {
            Cards card = new Cards(null, null);
            CardStack.add(card);
        }

        // Matrix
        tempMatrix = new float[16];
        modelMatrix = new float[16];

        modelViewMatrix = new float[16];
        modelViewProjectionMatrix = new float[16];
        it_modelViewMatrix = new float[16];

        // Load Assets and Resource for this screen
        AssetsAndResource.Load(game);

        // Object
        DisplayRectangle = new FullScreenRectangle();
        Base = new XZRectangle(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), 2.0f, 2.0f, 0);
        glCard = new XZRectangle(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), 0.08f, 0.12f, 0);
        point = new Points( new GLPoint(0f, 0f, 0f), 1f, 0f, 0f);
        cube = new Cube(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), 0.08f, 0.05f, 0.12f, true);
        DisplayCard = new ScreenRectangle(0.4f, 0.6f);

        // Widget
        CardWg = new CardWidget();
        DeckWg = new CardStackWidget();
        CardWg2 = new CardWidget();
    }

    @Override
    public void update(float deltaTime, float totalTime) {

    }

    @Override
    public void present(float deltaTime, float totalTime) {
        AssetsAndResource.ResetCardUsageCount();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, game.getframeBufferWidth(), game.getframeBufferHeight());
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        positionObjectInScene(0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 1f, 1f);
        AssetsAndResource.textureShaderProgramLight.useProgram();
        AssetsAndResource.textureShaderProgramLight.setUniforms(modelViewMatrix, it_modelViewMatrix,
                modelViewProjectionMatrix, null, AssetsAndResource.Light, Base.getMaterial(), AssetsAndResource.Base, 0, false);

        Base.bindData(AssetsAndResource.textureShaderProgramLight.getPositionAttributeLocation(),
                AssetsAndResource.textureShaderProgramLight.getNormalAttributeLocation(),
                AssetsAndResource.textureShaderProgramLight.getTextureCoordinatesAttributeLocation());
        Base.draw();


        float c = 1.0f;

        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
        WidgetPosition position = new WidgetPosition();
        position.Centerposition.x = 0.3f;
        position.Centerposition.y = c * 0.3f;
        position.Centerposition.z = c * 0.2f;
        position.rotaion.angle = AssetsAndResource.CameraAngle;
        position.rotaion.y = 0f;
        position.rotaion.x = 1f;
        position.rotaion.z = 0f;
        position.X_scale = 1f;
        position.Z_scale = 1f;
        if (CardWg.isTouched(touchEvents).isTouched) {
            GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                    new GLRay(game.getInput().getNearPoint(0), GLGeometry.GLVectorBetween(game.getInput().getNearPoint(0),
                            game.getInput().getFarPoint(0))), 0);
            //position.Centerposition.x = intersectingPoint.x;
            //position.Centerposition.z = intersectingPoint.z;
            positionObjectInScene(0.4f, 0f, 0.4f, 0f, 0f, 0f, 0f, 1f, 1f, 1f);
            AssetsAndResource.colorShaderProgram.useProgram();
            AssetsAndResource.colorShaderProgram.setUniforms(modelViewProjectionMatrix);

            point.bindData(AssetsAndResource.colorShaderProgram.getPositionAttributeLocation(),
                    AssetsAndResource.colorShaderProgram.getColorAttributeLocation());
            point.draw();
        }

        CardWg.setTranslateRotateScale(position);
       // CardWg.draw(deltaTime, totalTime);

        GLVector tmp = new GLVector(0 - AssetsAndResource.CameraPosition.x, c * 0.3f - AssetsAndResource.CameraPosition.y, c * 0.2f - AssetsAndResource.CameraPosition.z);
        tmp = tmp.getDirection();
        position.Centerposition.x = 0.02f + 0.01f * tmp.x;
        position.Centerposition.y = c * 0.3f + 0.01f * tmp.y;
        position.Centerposition.z = c * 0.2f + 0.01f* tmp.z;
        position.rotaion.angle = AssetsAndResource.CameraAngle;
        position.rotaion.y = 0f;
        position.rotaion.x = 1f;
        position.rotaion.z = 0f;
        position.X_scale = 1f;
        position.Z_scale = 1f;
        if (CardWg2.isTouched(touchEvents).isTouched) {
            GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                    new GLRay(game.getInput().getNearPoint(0), GLGeometry.GLVectorBetween(game.getInput().getNearPoint(0),
                            game.getInput().getFarPoint(0))), 0);
            //position.Centerposition.x = intersectingPoint.x;
            //position.Centerposition.z = intersectingPoint.z;
            positionObjectInScene(0.4f, 0f, 0.4f, 0f, 0f, 0f, 0f, 1f, 1f, 1f);
            AssetsAndResource.colorShaderProgram.useProgram();
            AssetsAndResource.colorShaderProgram.setUniforms(modelViewProjectionMatrix);

            point.bindData(AssetsAndResource.colorShaderProgram.getPositionAttributeLocation(),
                    AssetsAndResource.colorShaderProgram.getColorAttributeLocation());
            point.draw();
        }

        CardWg2.setTranslateRotateScale(position);
        //CardWg2.draw(deltaTime, totalTime);

        WidgetPosition position1 = new WidgetPosition();
        position1.Centerposition.y = 0.025f;
        if (DeckWg.isTouched(touchEvents).isTouched) {
            GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                    new GLRay(game.getInput().getNearPoint(0), GLGeometry.GLVectorBetween(game.getInput().getNearPoint(0),
                            game.getInput().getFarPoint(0))), 0);
            position1.Centerposition.x = intersectingPoint.x;
            position1.Centerposition.z = intersectingPoint.z;
        }

        DeckWg.setTranslateRotateScale(position1);
        DeckWg.draw(deltaTime, totalTime);

        glDisable(GL_DEPTH_TEST);
        positionObjectInScene(0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 1f, 1f);
        AssetsAndResource.textureProgram.useProgram();
        multiplyMM(AssetsAndResource.tempMatrix, 0, AssetsAndResource.projectionMatrix, 0, AssetsAndResource.viewMatrix, 0);
        AssetsAndResource.textureProgram.setUniforms(AssetsAndResource.tempMatrix, AssetsAndResource.cardBackside);

        DisplayCard.bindData(AssetsAndResource.textureProgram.getPositionAttributeLocation(), AssetsAndResource.textureProgram.getTextureCoordinatesAttributeLocation());
        //DisplayCard.draw();

        if (AssetsAndResource.game.getInput().isTouchDown(0)) {
            if (Math.abs(game.getInput().getNormalizedX(0)) <= DisplayCard.width/2 &&
                    Math.abs(game.getInput().getNormalizedY(0)) <= DisplayCard.length/2) {
                positionObjectInScene(0.4f, 0f, 0.2f, 0f, 0f, 0f, 0f, 1f, 1f, 1f);
                AssetsAndResource.colorShaderProgram.useProgram();
                AssetsAndResource.colorShaderProgram.setUniforms(modelViewProjectionMatrix);

                point.bindData(AssetsAndResource.colorShaderProgram.getPositionAttributeLocation(),
                        AssetsAndResource.colorShaderProgram.getColorAttributeLocation());
                point.draw();
            }
        }
    }

    @Override
    public void pause() {
        // Free
        AssetsAndResource.FreeAssetsAndResourcesForPvP();
    }

    @Override
    public void resume() {
        // Assets and Resources initialization
        AssetsAndResource.InitializeAssetsAndResourceForPvP();

        // Widget
        CardWg.ShadowEnable(false);
        CardWg.LinkGLobject(glCard);
        CardWg2.ShadowEnable(false);
        CardWg2.LinkGLobject(glCard);
        DeckWg.ShadowEnable(false);
        DeckWg.LinkGLobject(cube, glCard);
        DeckWg.LinkLogicalObject(CardStack);
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
        multiplyMM(modelViewMatrix, 0, AssetsAndResource.viewMatrix, 0, modelMatrix, 0);
        invertM(tempMatrix, 0, modelViewMatrix, 0);
        transposeM(it_modelViewMatrix, 0, tempMatrix, 0);
        multiplyMM(
                modelViewProjectionMatrix, 0,
                AssetsAndResource.projectionMatrix, 0,
                modelViewMatrix, 0);
    }
}
