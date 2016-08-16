package koustav.duelmasters.main.androidgameduelmasterwidgetmodels;


import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.Widget;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetMode;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetPosition;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchFocusLevel;
import koustav.duelmasters.main.androidgameopenglobjectmodels.Cube;
import koustav.duelmasters.main.androidgameopenglutil.DrawObjectHelper;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;
import koustav.duelmasters.main.androidgameopenglutil.MatrixHelper;
import koustav.duelmasters.main.androidgamesframework.Input;

import static android.opengl.Matrix.*;

/**
 * Created by Koustav on 3/16/2016.
 */
public class CardWidget implements Widget {
    WidgetPosition Position;
    float[] relativeNearPointAfterRot;
    float[] relativeFarPointAfterRot;
    GLPoint relativeNearPoint;
    GLPoint relativeFarPoint;

    // shadow enable;
    boolean shadowEnable;

    // Texture Array
    int[] textureArrays;

    // OpenGL object model, physical unit
    Cube glcard;

    // logical unit
    Cards card;

    public CardWidget () {
        Position = new WidgetPosition();
        relativeNearPointAfterRot = new float[4];
        relativeFarPointAfterRot = new float[4];
        relativeNearPoint = new GLPoint(0, 0, 0);
        relativeFarPoint = new GLPoint(0, 0, 0);

        shadowEnable = false;
        textureArrays = new int[6];
        glcard = null;
        card = null;
    }

    @Override
    public void update(float deltaTime, float totalTime) {

    }

    @Override
    public void draw(){
        for (int i = 0; i< 6; i++) {
            textureArrays[i] = AssetsAndResource.cardBorder;
        }
        textureArrays[2] = AssetsAndResource.cardBackside;
        textureArrays[3] = AssetsAndResource.getCardTexture(/*card.getNameID()*/"AquaHulcus");
        DrawObjectHelper.drawOneCube(glcard, textureArrays, shadowEnable);
    }

    @Override
    public WidgetTouchEvent isTouched(List<Input.TouchEvent> touchEvents) {
        WidgetTouchEvent widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
        widgetTouchEvent.isTouched = false;
        widgetTouchEvent.isTouchedDown = false;
        widgetTouchEvent.isMoving = false;
        widgetTouchEvent.isDoubleTouched = false;
        widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Low;
        widgetTouchEvent.object = null;

        int touchCount = 0;

        setIdentityM(AssetsAndResource.tempMatrix, 0);
        if (Position.rotaion.angle != 0) {
            rotateM(AssetsAndResource.tempMatrix, 0, Position.rotaion.angle, -Position.rotaion.x, -Position.rotaion.y,
                    -Position.rotaion.z);
        }

        Input input = AssetsAndResource.game.getInput();
        if (input.isTouchDown(0)) {
            GLPoint relativeNearPointAfterTrans = input.getNearPoint(0).translate(Position.Centerposition.getVector().scale(-1));
            GLPoint relativeFarPointAfterTrans = input.getFarPoint(0).translate(Position.Centerposition.getVector().scale(-1));

            multiplyMV(relativeNearPointAfterRot, 0, AssetsAndResource.tempMatrix, 0, new float[] {relativeNearPointAfterTrans.x,
                    relativeNearPointAfterTrans.y, relativeNearPointAfterTrans.z, 0f}, 0);
            multiplyMV(relativeFarPointAfterRot, 0, AssetsAndResource.tempMatrix, 0, new float[] {relativeFarPointAfterTrans.x,
                    relativeFarPointAfterTrans.y, relativeFarPointAfterTrans.z, 0f}, 0);

            relativeNearPoint.x = relativeNearPointAfterRot[0];
            relativeNearPoint.y = relativeNearPointAfterRot[1];
            relativeNearPoint.z = relativeNearPointAfterRot[2];

            relativeFarPoint.x = relativeFarPointAfterRot[0];
            relativeFarPoint.y = relativeFarPointAfterRot[1];
            relativeFarPoint.z = relativeFarPointAfterRot[2];

            GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                    new GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)), 0);

            float width = Math.abs(intersectingPoint.x);
            float height = Math.abs(intersectingPoint.z);

            if (width <= (glcard.width * Position.X_scale)/2 && height <= (glcard.height * Position.Z_scale)/2) {
                widgetTouchEvent.isTouched = true;
                widgetTouchEvent.isTouchedDown = true;
                if (input.TouchType(0) == Input.TouchEvent.TOUCH_DRAGGED) {
                    widgetTouchEvent.isMoving = true;
                }
                widgetTouchEvent.object = card;
                return widgetTouchEvent;
            }
        } else {
            for (int i = 0; i < touchEvents.size(); i++) {
                Input.TouchEvent event = touchEvents.get(i);
                if (event.type == Input.TouchEvent.TOUCH_UP) {
                    GLPoint relativeNearPointAfterTrans = event.nearPoint[0].translate(Position.Centerposition.getVector().scale(-1));
                    GLPoint relativeFarPointAfterTrans = event.farPoint[0].translate(Position.Centerposition.getVector().scale(-1));

                    multiplyMV(relativeNearPointAfterRot, 0, AssetsAndResource.tempMatrix, 0, new float[]{relativeNearPointAfterTrans.x,
                            relativeNearPointAfterTrans.y, relativeNearPointAfterTrans.z, 0f}, 0);
                    multiplyMV(relativeFarPointAfterRot, 0, AssetsAndResource.tempMatrix, 0, new float[]{relativeFarPointAfterTrans.x,
                            relativeFarPointAfterTrans.y, relativeFarPointAfterTrans.z, 0f}, 0);

                    relativeNearPoint.x = relativeNearPointAfterRot[0];
                    relativeNearPoint.y = relativeNearPointAfterRot[1];
                    relativeNearPoint.z = relativeNearPointAfterRot[2];

                    relativeFarPoint.x = relativeFarPointAfterRot[0];
                    relativeFarPoint.y = relativeFarPointAfterRot[1];
                    relativeFarPoint.z = relativeFarPointAfterRot[2];

                    GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                            new GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)), 0);

                    float width = Math.abs(intersectingPoint.x);
                    float height = Math.abs(intersectingPoint.z);

                    if (width <= (glcard.width * Position.X_scale) / 2 && height <= (glcard.height * Position.Z_scale) / 2) {
                        widgetTouchEvent.isTouched = true;
                        widgetTouchEvent.object = card;
                        touchCount++;
                        continue;
                    }
                }
            }


            if (widgetTouchEvent.isTouched && touchCount > 1) {
                widgetTouchEvent.isDoubleTouched = true;
            }
        }

        return widgetTouchEvent;
    }

    @Override
    public void setTranslateRotateScale(WidgetPosition position) {
        // Store this info required for isTouched

        this.Position.rotaion.angle = position.rotaion.angle;
        this.Position.rotaion.x = position.rotaion.x;
        this.Position.rotaion.y = position.rotaion.y;
        this.Position.rotaion.z = position.rotaion.z;
        this.Position.Centerposition.x = position.Centerposition.x;
        this.Position.Centerposition.y = position.Centerposition.y;
        this.Position.Centerposition.z = position.Centerposition.z;
        this.Position.X_scale = position.X_scale;
        this.Position.Y_scale = position.Y_scale;
        this.Position.Z_scale = position.Z_scale;

        MatrixHelper.setTranslateRotateScale(this.Position);
    }

    @Override
    public void ShadowEnable(boolean shadowEnable) {
        this.shadowEnable = shadowEnable;
    }

    @Override
    public void LinkGLobject(Object ...objs) {
        glcard = (Cube) objs[0];
    }

    @Override
    public void LinkLogicalObject(Object obj) {
        this.card = (Cards) obj;
    }

    @Override
    public Object getLogicalObject() {
        return card;
    }

    @Override
    public void setMode(WidgetMode mode) {

    }

    @Override
    public WidgetPosition getPosition() {
        return Position;
    }
}
