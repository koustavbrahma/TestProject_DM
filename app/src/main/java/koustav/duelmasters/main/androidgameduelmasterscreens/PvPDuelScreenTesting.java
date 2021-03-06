package koustav.duelmasters.main.androidgameduelmasterscreens;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl.ViewNodePosition;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.ControllerButton;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.HeadOrientation;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.BattleZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.ControllerLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.HandZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.ManaZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardStackWidget;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.RectangleButtonWidget;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglobjectmodels.Cube;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglobjectmodels.FullScreenRectangle;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglobjectmodels.Points;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglobjectmodels.ScreenRectangle;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglobjectmodels.XZRectangle;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLGeometry.*;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLMaterial;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Input;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Screen;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkimpl.AndroidGame;

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
    private Cube glcard2;
    private ScreenRectangle DisplayCard;
    private ScreenRectangle glRbutton;

    // Layout
    BattleZoneLayout battleZoneLayout;
    ManaZoneLayout manaZoneLayout;
    HandZoneLayout handZoneLayout;

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
    CardWidget CardWg3;
    CardWidget CardWg4;

    ControllerLayout controllerLayout;

    RectangleButtonWidget SummonButton;
    RectangleButtonWidget AddToManaButton;
    RectangleButtonWidget AttackButton;

    ArrayList<CardWidget> tmplist;
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
        AssetsAndResource.Load(game, 0f, 2.4f, 1.6f);

        // Object
        DisplayRectangle = new FullScreenRectangle();
        Base = new XZRectangle(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), 2.0f, 2.0f, 0);
        glCard = new XZRectangle(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), 0.08f, 0.12f, 0);
        point = new Points( new GLPoint(0f, 0f, 0f), 1f, 0f, 0f);
        cube = new Cube(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), AssetsAndResource.CardWidth, 0.05f, AssetsAndResource.CardHeight, true);
        glcard2 = new Cube(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), AssetsAndResource.CardWidth, AssetsAndResource.CardLength, AssetsAndResource.CardHeight, true);
        DisplayCard = new ScreenRectangle(0.4f, 0.6f);
        glRbutton = new ScreenRectangle(0.15f, 0.15f);

        battleZoneLayout = new BattleZoneLayout();
        manaZoneLayout = new ManaZoneLayout();
        handZoneLayout = new HandZoneLayout();
        // Widget
        CardWg = new CardWidget();
        DeckWg = new CardStackWidget();
        CardWg2 = new CardWidget();
        CardWg3 = new CardWidget();
        CardWg4 = new CardWidget();

        SummonButton = new RectangleButtonWidget();
        AddToManaButton = new RectangleButtonWidget();
        AttackButton = new RectangleButtonWidget();

        controllerLayout = new ControllerLayout();

        SummonButton.LinkGLobject(glRbutton);
        AddToManaButton.LinkGLobject(glRbutton);
        AttackButton.LinkGLobject(glRbutton);

        SummonButton.LinkLogicalObject(ControllerButton.SummonOrCast);
        AddToManaButton.LinkLogicalObject(ControllerButton.AddToMana);
        AttackButton.LinkLogicalObject(ControllerButton.Attack);

        controllerLayout.AddButtonWidget(ControllerButton.SummonOrCast, SummonButton);
        controllerLayout.AddButtonWidget(ControllerButton.AddToMana, AddToManaButton);
        controllerLayout.AddButtonWidget(ControllerButton.Attack, AttackButton);

        controllerLayout.setControllerButton(new ControllerButton[] {ControllerButton.SummonOrCast, ControllerButton.AddToMana});
        tmplist = new ArrayList<CardWidget>();
    }

    @Override
    public void present() {

    }

    @Override
    public void update(float deltaTime, float totalTime) {
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
        ViewNodePosition position = new ViewNodePosition();
        position.Centerposition.x = 0f;
        position.Centerposition.y = 0;
        position.Centerposition.z = -0.3f;
        position.rotaion.angle = 0;
        position.rotaion.y = 0f;
        position.rotaion.x = 1f;
        position.rotaion.z = 0f;
        position.X_scale = 1f;
        position.Z_scale = 1f;
        CardWg.setTranslateRotateScale(position);
        CardWg.draw();
        WidgetTouchEvent tmp2 = CardWg.isTouched(touchEvents);

       // WidgetTouchEvent tmp2 = controllerLayout.TouchResponse(touchEvents);
        if (tmp2 != null && tmp2.isTouched && !tmp2.isTouchedDown /*&& tmp2.object == ControllerButton.SummonOrCast */) {
            /*GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                    new GLRay(game.getInput().getNearPoint(0), GLGeometry.GLVectorBetween(game.getInput().getNearPoint(0),
                            game.getInput().getFarPoint(0))), 0);
            //position.Centerposition.x = intersectingPoint.x;
            //position.Centerposition.z = intersectingPoint.z;
            positionObjectInScene(0.4f, 0f, 0.4f, 0f, 0f, 0f, 0f, 1f, 1f, 1f);
            AssetsAndResource.colorShaderProgram.useProgram();
            AssetsAndResource.colorShaderProgram.setUniforms(modelViewProjectionMatrix);

            point.bindData(AssetsAndResource.colorShaderProgram.getPositionAttributeLocation(),
                    AssetsAndResource.colorShaderProgram.getColorAttributeLocation());
            point.draw(); */
            CardWidget CardWgtmp = new CardWidget();
            CardWgtmp.LinkGLobject(glcard2);
            CardWgtmp.ShadowEnable(false);

            ViewNodePosition position2 = new ViewNodePosition();
            position2.Centerposition.x = 0.2f;
            position2.Centerposition.y = 0.2f;
            position2.Centerposition.z = 0.5f;
            position2.rotaion.angle = 0;
            position2.rotaion.y = 1f;
            position2.rotaion.x = 0f;
            position2.rotaion.z = 0f;
            position2.X_scale = 1f;
            position2.Z_scale = 1f;
            CardWgtmp.setTranslateRotateScale(position2);
            //battleZoneLayout.AddCardWidgetToZone(CardWgtmp);
            //manaZoneLayout.AddCardWidgetToZone(CardWgtmp);
            //DeckWg.setMode(WidgetMode.Transition);
            handZoneLayout.AddCardWidgetToZone(CardWgtmp);
            tmplist.add(CardWgtmp);
        }


  //      CardWg.setTranslateRotateScale(position);
       // CardWg.draw(deltaTime, totalTime);


        GLVector tmp = new GLVector(0 - AssetsAndResource.CameraPosition.x, c * 0.3f - AssetsAndResource.CameraPosition.y, c * 0.2f - AssetsAndResource.CameraPosition.z);
        tmp = tmp.getDirection();
        position.Centerposition.x = 0.3f;
        position.Centerposition.y = 0f;
        position.Centerposition.z = -0.3f;
        position.rotaion.angle = 0;
        position.rotaion.y = 0f;
        position.rotaion.x = 1f;
        position.rotaion.z = 0f;
        position.X_scale = 1f;
        position.Z_scale = 1f;
        CardWg2.setTranslateRotateScale(position);
        CardWg2.draw();
        tmp2 = CardWg2.isTouched(touchEvents);
        boolean remove = false;
        if (tmp2 != null && tmp2.isTouched && !tmp2.isTouchedDown /*&& tmp2.object == ControllerButton.AddToMana */) {
           /* GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                    new GLRay(game.getInput().getNearPoint(0), GLGeometry.GLVectorBetween(game.getInput().getNearPoint(0),
                            game.getInput().getFarPoint(0))), 0);
            //position.Centerposition.x = intersectingPoint.x;
            //position.Centerposition.z = intersectingPoint.z;
            positionObjectInScene(0.4f, 0f, 0.4f, 0f, 0f, 0f, 0f, 1f, 1f, 1f);
            AssetsAndResource.colorShaderProgram.useProgram();
            AssetsAndResource.colorShaderProgram.setUniforms(modelViewProjectionMatrix);

            point.bindData(AssetsAndResource.colorShaderProgram.getPositionAttributeLocation(),
                    AssetsAndResource.colorShaderProgram.getColorAttributeLocation());
            point.draw(); */

            Random R = new Random();
            int tempindex;
            CardWidget rmwg = null;
            if (tmplist.size() > 0) {
                tempindex = (int) R.nextInt(tmplist.size());
             //   rmwg = tmplist.remove(tempindex);
                rmwg = tmplist.get(tempindex);
               // battleZoneLayout.RemoveCardWidgetFromZone(rmwg);
                //manaZoneLayout.RemoveCardWidgetFromZone(rmwg);
                //handZoneLayout.RemoveCardWidgetFromZone(rmwg);
                handZoneLayout.LockCardWidget(rmwg, 0, 0);
                remove = true;
            }
/*
            if (tmplist.size() > 0) {
                tempindex = (int) R.nextInt(tmplist.size());
                rmwg = tmplist.get(tempindex);
                CardWidget CardWgtmp = new CardWidget();
                CardWgtmp.LinkGLobject(glcard2);
                CardWgtmp.ShadowEnable(false);

                WidgetPosition position2 = new WidgetPosition();
                position2.Centerposition.x = 0.2f;
                position2.Centerposition.y = 0.2f;
                position2.Centerposition.z = 0.5f;
                position2.rotaion.angle = 0;
                position2.rotaion.y = 1f;
                position2.rotaion.x = 0f;
                position2.rotaion.z = 0f;
                position2.X_scale = 1f;
                position2.Z_scale = 1f;
                CardWgtmp.setTranslateRotateScale(position2);
                battleZoneLayout.PutCardWidgetOnTopOfExistingCardWidget(CardWgtmp, rmwg);
                tmplist.add(CardWgtmp);
            } */
//            controllerLayout.setControllerButton(new ControllerButton[] {ControllerButton.SummonOrCast, ControllerButton.AddToMana, ControllerButton.Attack});
        }

        position.Centerposition.x = 0.5f;
        position.Centerposition.y = 0f;
        position.Centerposition.z = -0.3f;
        position.rotaion.angle = 0;
        position.rotaion.y = 0f;
        position.rotaion.x = 1f;
        position.rotaion.z = 0f;
        position.X_scale = 1f;
        position.Z_scale = 1f;
        CardWg3.setTranslateRotateScale(position);
        CardWg3.draw();
        //tmp2 = CardWg3.isTouched(touchEvents);
        if (tmp2 != null && tmp2.isTouched && !tmp2.isTouchedDown && tmp2.object == ControllerButton.Attack) {
            CardWidget CardWgtmp = new CardWidget();
            CardWgtmp.LinkGLobject(glcard2);
            CardWgtmp.ShadowEnable(false);

            ViewNodePosition position2 = new ViewNodePosition();
            position2.Centerposition.x = 0.2f;
            position2.Centerposition.y = 0.2f;
            position2.Centerposition.z = 0.5f;
            position2.rotaion.angle = 0;
            position2.rotaion.y = 1f;
            position2.rotaion.x = 0f;
            position2.rotaion.z = 0f;
            position2.X_scale = 1f;
            position2.Z_scale = 1f;
          //  CardWgtmp.setTranslateRotateScale(position2);
            //battleZoneLayout.AddCardWidgetToZone(CardWgtmp);
            //manaZoneLayout.TransferCardWidgetToCoupleSlotZone(CardWgtmp);
            //tmplist.add(CardWgtmp);
     //       controllerLayout.setControllerButton(new ControllerButton[] {ControllerButton.SummonOrCast, ControllerButton.AddToMana});
        }

        //if (!remove) {
        //    tmp2 = manaZoneLayout.TouchResponse(touchEvents);
        //}

        if (tmp2 != null && tmp2.isTouched && !tmp2.isTouchedDown) {
            Random R = new Random();
            int tempindex;
            CardWidget rmwg = null;
            if (tmplist.size() > 0) {
                tempindex = (int) R.nextInt(tmplist.size());
                rmwg = tmplist.get(tempindex);
                // battleZoneLayout.RemoveCardWidgetFromZone(rmwg);
                //manaZoneLayout.AddToTransitionZone(rmwg);
            }
        }
        tmp2 = handZoneLayout.TouchResponse(touchEvents);
        handZoneLayout.update(deltaTime, totalTime);
        handZoneLayout.draw();
        //manaZoneLayout.update(deltaTime, totalTime);
        //manaZoneLayout.draw();
//        tmp2 = battleZoneLayout.TouchResponse(touchEvents);
        //battleZoneLayout.update(deltaTime, totalTime);
        //battleZoneLayout.draw();
        /*
        CardWg2.setTranslateRotateScale(position);
        CardWg2.draw();
*/
        ArrayList<GLAngularRotaion> rotaions = new ArrayList<GLAngularRotaion>();
        GLAngularRotaion rotaion= new GLAngularRotaion(30, 0, 1f, 0);
        //GLAngularRotaion rotaion1 = new GLAngularRotaion(90, 1f, 0 , 0);

        //rotaions.add(rotaion);
        //rotaions.add(rotaion1);

        //rotaion = MatrixHelper.getCombinedRotation(rotaions);
        ViewNodePosition position1 = new ViewNodePosition();
        position1.Centerposition.x = 0.68f;
        position1.Centerposition.y = 0.025f;
        position1.Centerposition.z = 0.15f;
        position1.rotaion.angle = rotaion.angle;
        position1.rotaion.y = rotaion.y;
        position1.rotaion.x = rotaion.x;
        position1.rotaion.z = rotaion.z;

        tmp2 = DeckWg.isTouched(touchEvents);
        if (tmp2.isTouched && !tmp2.isTouchedDown) {
            //GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
            //        new GLRay(game.getInput().getNearPoint(0), GLGeometry.GLVectorBetween(game.getInput().getNearPoint(0),
            //                game.getInput().getFarPoint(0))), 0);
            //position1.Centerposition.x = intersectingPoint.x;
            //position1.Centerposition.z = intersectingPoint.z;
            //DeckWg.setMode(WidgetMode.Transition);
        }

        DeckWg.setTranslateRotateScale(position1);
        DeckWg.update(deltaTime, totalTime);
        DeckWg.draw();

        glDisable(GL_DEPTH_TEST);
        //positionObjectInScene(0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 1f, 1f);
        //AssetsAndResource.textureProgram.useProgram();
        //multiplyMM(AssetsAndResource.tempMatrix, 0, AssetsAndResource.projectionMatrix, 0, AssetsAndResource.viewMatrix, 0);
        //AssetsAndResource.textureProgram.setUniforms(AssetsAndResource.OrthoProjectionMatrix, AssetsAndResource.cardBackside);

        //DisplayCard.bindData(AssetsAndResource.textureProgram.getPositionAttributeLocation(), AssetsAndResource.textureProgram.getTextureCoordinatesAttributeLocation());
        //DisplayCard.draw();
        ViewNodePosition position2 = new ViewNodePosition();
        position2.Centerposition.x = 0.2f;
        position2.Centerposition.y = 0.2f;
        position2.Centerposition.z = 0;
        position2.rotaion.angle = 0;
        position2.rotaion.y = 0;
        position2.rotaion.x = 0;
        position2.rotaion.z = 0;
//        MatrixHelper.setTranslate(position2);
//        DrawObjectHelper.drawOneScreenRectangle(DisplayCard, AssetsAndResource.cardBackside);
  //      controllerLayout.update(deltaTime, totalTime);
   //     controllerLayout.draw();

        if (AssetsAndResource.game.getInput().isTouchDown(0)) {
            if (Math.abs(game.getInput().getNormalizedX(0)) <= DisplayCard.width/2 &&
                    Math.abs(game.getInput().getNormalizedY(0)) <= DisplayCard.length/2) {
                positionObjectInScene(0.4f, 0f, 0.2f, 0f, 0f, 0f, 0f, 1f, 1f, 1f);
                AssetsAndResource.colorShaderProgram.useProgram();
                AssetsAndResource.colorShaderProgram.setUniforms(modelViewProjectionMatrix);

                point.bindData(AssetsAndResource.colorShaderProgram.getPositionAttributeLocation(),
                        AssetsAndResource.colorShaderProgram.getColorAttributeLocation());
  //              point.draw();
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
        CardWg.LinkGLobject(glcard2);
        CardWg.LinkLogicalObject(null);
        CardWg2.ShadowEnable(false);
        CardWg2.LinkGLobject(glcard2);
        CardWg3.ShadowEnable(false);
        CardWg3.LinkGLobject(glcard2);
        CardWg4.ShadowEnable(false);
        CardWg4.LinkGLobject(glcard2);
        DeckWg.ShadowEnable(false);
        DeckWg.LinkGLobject(cube, glcard2);
        DeckWg.LinkLogicalObject(CardStack);
        DeckWg.setFlip(true);
        battleZoneLayout.InitializeBattleZoneLayout(AssetsAndResource.MazeHeight/10, AssetsAndResource.MazeWidth,
                AssetsAndResource.MazeHeight/5, HeadOrientation.North, false, 4f, 4f);
        manaZoneLayout.InitializeBattleZoneLayout((3f * AssetsAndResource.MazeHeight)/10, AssetsAndResource.MazeWidth,
                AssetsAndResource.MazeHeight/5, HeadOrientation.North, false, 4f, 4f);
        manaZoneLayout.SetDraggingMode(true);
        //manaZoneLayout.SetExpandMode(false);
        handZoneLayout.InitializeHandZoneLayout(1f, -0.78f, AssetsAndResource.CameraPosition.x/4, AssetsAndResource.CameraPosition.y/4,
                AssetsAndResource.CameraPosition.z/4, 4f, 4f);
        handZoneLayout.SetDraggingMode(true);
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
