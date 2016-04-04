package koustav.duelmasters.main.androidgameduelmasterwidgetmodels;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.WidgetPosition;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameopenglmotionmodel.GLDynamics;
import koustav.duelmasters.main.androidgameopenglobjectmodels.Cube;
import koustav.duelmasters.main.androidgameopenglobjectmodels.XZRectangle;
import koustav.duelmasters.main.androidgameopenglutil.DrawObjectHelper;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgameopenglutil.MatrixHelper;
import koustav.duelmasters.main.androidgamesframework.Input;
import koustav.duelmasters.main.androidgamesframework.Pool;

import static android.opengl.Matrix.*;

/**
 * Created by Koustav on 3/21/2016.
 */
public class CardStackWidget implements Widget{
    // Misc var
    WidgetPosition Position;
    float[] relativeNearPointAfterRot;
    float[] relativeFarPointAfterRot;
    GLPoint relativeNearPoint;
    GLPoint relativeFarPoint;
    WidgetMode mode;
    Hashtable<Cards, WidgetPosition> cardWidgetPositionTable;
    Cards SelectedCard;
    ArrayList<Cards> pickedCardsFromTheList;
    ArrayList<Integer> indexTouched;
    float scale;

    // Dynamics
    GLDynamics dynamics;

    // shadow enable;
    boolean shadowEnable;

    // Texture Array
    int[] textureArrays;

    // openGL object model, physical unit
    public Cube cube;
    public XZRectangle glcard;

    // Logical object
    ArrayList<Cards> cardStack;

    public CardStackWidget() {
        Position = new WidgetPosition();
        relativeNearPointAfterRot = new float[4];
        relativeFarPointAfterRot = new float[4];
        relativeNearPoint = new GLPoint(0, 0, 0);
        relativeFarPoint = new GLPoint(0, 0, 0);
        mode = WidgetMode.Expand;
        cardWidgetPositionTable = new Hashtable<Cards, WidgetPosition>();
        SelectedCard = null;
        pickedCardsFromTheList = new ArrayList<Cards>();
        indexTouched = new ArrayList<Integer>();
        scale = 2.0f;

        dynamics = new GLDynamics();

        textureArrays = new int[6];
        shadowEnable = false;
        cube = null;
        glcard = null;
        cardStack = null;
    }

    @Override
    public void  draw(float deltaTime, float totalTime) {
        if (mode == WidgetMode.Normal) {
            drawNormal();
        }

        if (mode == WidgetMode.Transition) {
            drawTransition(deltaTime, totalTime);
        }

        if (mode == WidgetMode.Expand) {
            drawExpand();
        }
    }

    // Draw the widget in normal mode
    private void drawNormal() {
        for (int i = 0; i< 6; i++) {
            textureArrays[i] = AssetsAndResource.cardDeckSides;
        }
        textureArrays[3] = AssetsAndResource.cardBackside;

        DrawObjectHelper.drawOneCube(cube, textureArrays, shadowEnable);
    }

    // Draw the widget in transition mode
    private void drawTransition(float deltaTime, float totalTime) {
        WidgetPosition widgetPosition;
        float x, y, z;
        GLVector vector = new GLVector(0 , 0, 0);
        GLVector Direction = AssetsAndResource.CameraPosition.getVector();

        float gaps =  (0.8f * AssetsAndResource.MazeWidth)/cardStack.size();

        if (gaps > glcard.width/2.0f) {
            gaps = glcard.width/2.0f;
        }

        int midPoint = cardStack.size()/2 + cardStack.size()%2 -1;

        for (int i = 0; i < cardStack.size(); i++) {
            widgetPosition = null;
            widgetPosition = cardWidgetPositionTable.get(cardStack.get(i));

            if (widgetPosition == null) {
                widgetPosition = AssetsAndResource.widgetPositionPool.newObject();
                widgetPosition.Centerposition.x = Position.Centerposition.x;
                widgetPosition.Centerposition.z = Position.Centerposition.z;
                widgetPosition.Centerposition.y = (cube.length *(float)(cardStack.size() - i)) / 40f;
                widgetPosition.rotaion.angle = Position.rotaion.angle;
                widgetPosition.rotaion.x = Position.rotaion.x;
                widgetPosition.rotaion.y = Position.rotaion.y;
                widgetPosition.rotaion.z = Position.rotaion.z;
            }
            y = AssetsAndResource.CameraPosition.y/4.0f;
            z = AssetsAndResource.CameraPosition.z/4.0f;
            x = AssetsAndResource.CameraPosition.x + ((float)(i - midPoint)) * gaps;

            x = x - Direction.x * 0.01f * i;
            y = y - Direction.x * 0.01f * i;
            z = z - Direction.x * 0.01f * i;

            //dynamics.setCentrePosition(widgetPosition.Centerposition.x, widgetPosition.Centerposition.y,
                //    widgetPosition.Centerposition.z);
            //dynamics.setVelocity(x - widgetPosition.Centerposition.x, y - widgetPosition.Centerposition.y,
              //      z - widgetPosition.Centerposition.z);
        }
    }

    // Draw the widget in Expand mode
    private void drawExpand() {
        WidgetPosition widgetPosition;
        int selectedCardIndex;

        selectedCardIndex = cardStack.indexOf(SelectedCard);

        float gaps = (0.8f * AssetsAndResource.MazeHeight)/((float) cardStack.size());

        if (gaps > glcard.width/2.0f) {
            gaps = glcard.width/2.0f;
        }

        int midPoint = cardStack.size()/2 + cardStack.size()%2 -1;

        for (int i = cardStack.size() -1; i > selectedCardIndex; i--) {
            widgetPosition = null;
            widgetPosition = cardWidgetPositionTable.get(cardStack.get(i));

            if (widgetPosition == null) {
                widgetPosition = AssetsAndResource.widgetPositionPool.newObject();
                widgetPosition.Centerposition.x = AssetsAndResource.CameraPosition.x / 4.0f + ((float) (i - midPoint)) * gaps;
                widgetPosition.Centerposition.z = AssetsAndResource.CameraPosition.z / 4.0f;
                widgetPosition.Centerposition.y = AssetsAndResource.CameraPosition.y / 4.0f;
                widgetPosition.rotaion.angle = AssetsAndResource.CameraAngle;
                widgetPosition.rotaion.x = 1f;
                widgetPosition.rotaion.y = 0f;
                widgetPosition.rotaion.z = 0f;
                cardWidgetPositionTable.put(cardStack.get(i), widgetPosition);
            }
            widgetPosition.X_scale = scale * (39.0f - 0.3f * (float) Math.abs(i - selectedCardIndex))/39.0f;
            widgetPosition.Z_scale = scale * (39.0f - 0.3f * (float) Math.abs(i - selectedCardIndex))/39.0f;

            MatrixHelper.setTranslateRotateScale(widgetPosition);
            DrawObjectHelper.drawOneCard(cardStack.get(i), glcard, shadowEnable);
        }

        for (int i = 0; i <= selectedCardIndex; i++) {
            widgetPosition = null;
            widgetPosition = cardWidgetPositionTable.get(cardStack.get(i));

            if (widgetPosition == null) {
                widgetPosition = AssetsAndResource.widgetPositionPool.newObject();
                widgetPosition.Centerposition.x = AssetsAndResource.CameraPosition.x / 4.0f + ((float) (i - midPoint)) * gaps;
                widgetPosition.Centerposition.z = AssetsAndResource.CameraPosition.z / 4.0f;
                widgetPosition.Centerposition.y = AssetsAndResource.CameraPosition.y / 4.0f;
                widgetPosition.rotaion.angle = AssetsAndResource.CameraAngle;
                widgetPosition.rotaion.x = 1f;
                widgetPosition.rotaion.y = 0f;
                widgetPosition.rotaion.z = 0f;
                cardWidgetPositionTable.put(cardStack.get(i), widgetPosition);
            }
            widgetPosition.X_scale = scale * (39.0f - 0.3f * (float) Math.abs(i - selectedCardIndex))/39.0f;
            widgetPosition.Z_scale = scale * (39.0f - 0.3f * (float) Math.abs(i - selectedCardIndex))/39.0f;

            MatrixHelper.setTranslateRotateScale(widgetPosition);
            DrawObjectHelper.drawOneCard(cardStack.get(i), glcard, shadowEnable);
        }
    }

    @Override
    public WidgetTouchEvent isTouched(List<Input.TouchEvent> touchEvents) {
        WidgetTouchEvent widgetTouchEvent = null;
        if (mode == WidgetMode.Normal) {
            widgetTouchEvent = isTouchedForNormalMode(touchEvents);
        }

        if (mode == WidgetMode.Expand) {
            widgetTouchEvent = isTouchedForExpandMode(touchEvents);
        }

        return widgetTouchEvent;
    }

    private WidgetTouchEvent isTouchedForNormalMode(List<Input.TouchEvent> touchEvents) {
        WidgetTouchEvent widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
        widgetTouchEvent.isTouched = false;
        widgetTouchEvent.isTouchedDown = false;
        widgetTouchEvent.isDoubleTouched = false;
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
                    new GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)), (cube.length * Position.Y_scale)/2);

            float width = Math.abs(intersectingPoint.x);
            float height = Math.abs(intersectingPoint.z);

            if (width <= (cube.width * Position.X_scale)/2 && height <= (cube.height * Position.Z_scale)/2) {
                widgetTouchEvent.isTouched = true;
                widgetTouchEvent.isTouchedDown = true;
                widgetTouchEvent.object = cardStack;
                return widgetTouchEvent;
            }

            intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                    new GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)), -(cube.length * Position.Y_scale)/2);

            width = Math.abs(intersectingPoint.x);
            height = Math.abs(intersectingPoint.z);

            if (width <= (cube.width * Position.X_scale)/2 && height <= (cube.height * Position.Z_scale)/2) {
                widgetTouchEvent.isTouched = true;
                widgetTouchEvent.isTouchedDown = true;
                widgetTouchEvent.object = cardStack;
                return widgetTouchEvent;
            }

            GLGeometry.GLPoint NormalPoint = GLGeometry.GLRayIntersectionWithPlane(new GLGeometry.GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)),
                    new GLGeometry.GLPlane(new GLPoint(0,0,0), GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)));

            width = Math.abs(NormalPoint.x);
            float length = Math.abs(NormalPoint.y);
            height = Math.abs(NormalPoint.z);

            if (width <= (cube.width * Position.X_scale)/2 && length <= (cube.length * Position.Y_scale)/2 &&
                    height <= (cube.height * Position.Z_scale)/2) {
                widgetTouchEvent.isTouched = true;
                widgetTouchEvent.isTouchedDown = true;
                widgetTouchEvent.object = cardStack;
                return widgetTouchEvent;
            }

            if (NormalPoint.z > 0) {
                intersectingPoint = GLGeometry.GLRayIntersectionWithPlane(new GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)),
                        new GLPlane(new GLPoint(0, 0, (cube.height * Position.Z_scale)/2), new GLVector(0,0,1)));

                width = Math.abs(intersectingPoint.x);
                length = Math.abs(intersectingPoint.y);

                if (width <= (cube.width * Position.X_scale)/2 && length <= (cube.length * Position.Y_scale)/2) {
                    widgetTouchEvent.isTouched = true;
                    widgetTouchEvent.isTouchedDown = true;
                    widgetTouchEvent.object = cardStack;
                    return widgetTouchEvent;
                }
            } else {
                intersectingPoint = GLGeometry.GLRayIntersectionWithPlane(new GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)),
                        new GLPlane(new GLPoint(0, 0, -(cube.height * Position.Z_scale)/2), new GLVector(0,0,-1)));

                width = Math.abs(intersectingPoint.x);
                length = Math.abs(intersectingPoint.y);

                if (width <= (cube.width * Position.X_scale)/2 && length <= (cube.length * Position.Y_scale)/2) {
                    widgetTouchEvent.isTouched = true;
                    widgetTouchEvent.isTouchedDown = true;
                    widgetTouchEvent.object = cardStack;
                    return widgetTouchEvent;
                }
            }
        }

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
                        new GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)), (cube.length * Position.Y_scale)/ 2);

                float width = Math.abs(intersectingPoint.x);
                float height = Math.abs(intersectingPoint.z);

                if (width <= (cube.width * Position.X_scale) / 2 && height <= (cube.height * Position.Z_scale) / 2) {
                    widgetTouchEvent.isTouched = true;
                    widgetTouchEvent.object = cardStack;
                    touchCount++;
                    continue;
                }

                intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                        new GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)), -(cube.length * Position.Y_scale)/ 2);

                width = Math.abs(intersectingPoint.x);
                height = Math.abs(intersectingPoint.z);

                if (width <= (cube.width * Position.X_scale) / 2 && height <= (cube.height * Position.Z_scale) / 2) {
                    widgetTouchEvent.isTouched = true;
                    widgetTouchEvent.object = cardStack;
                    touchCount++;
                    continue;
                }

                GLGeometry.GLPoint NormalPoint = GLGeometry.GLRayIntersectionWithPlane(new GLGeometry.GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)),
                        new GLGeometry.GLPlane(new GLPoint(0, 0, 0), GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)));

                width = Math.abs(NormalPoint.x);
                float length = Math.abs(NormalPoint.y);
                height = Math.abs(NormalPoint.z);

                if (width <= (cube.width * Position.X_scale) / 2 && length <= (cube.length * Position.Y_scale) / 2 &&
                        height <= (cube.height * Position.Z_scale) / 2) {
                    widgetTouchEvent.isTouched = true;
                    widgetTouchEvent.object = cardStack;
                    touchCount++;
                    continue;
                }

                if (NormalPoint.z > 0) {
                    intersectingPoint = GLGeometry.GLRayIntersectionWithPlane(new GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)),
                            new GLPlane(new GLPoint(0, 0, (cube.height * Position.Z_scale)/ 2), new GLVector(0, 0, 1)));

                    width = Math.abs(intersectingPoint.x);
                    length = Math.abs(intersectingPoint.y);

                    if (width <= (cube.width * Position.X_scale) / 2 && length <= (cube.length * Position.Y_scale) / 2) {
                        widgetTouchEvent.isTouched = true;
                        widgetTouchEvent.object = cardStack;
                        touchCount++;
                        continue;
                    }
                } else {
                    intersectingPoint = GLGeometry.GLRayIntersectionWithPlane(new GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)),
                            new GLPlane(new GLPoint(0, 0, -(cube.height * Position.Z_scale)/ 2), new GLVector(0, 0, -1)));

                    width = Math.abs(intersectingPoint.x);
                    length = Math.abs(intersectingPoint.y);

                    if (width <= (cube.width * Position.X_scale) / 2 && length <= (cube.length * Position.Y_scale) / 2) {
                        widgetTouchEvent.isTouched = true;
                        widgetTouchEvent.object = cardStack;
                        touchCount++;
                        continue;
                    }
                }
            }
        }

        if (widgetTouchEvent.isTouched && touchCount > 1) {
            widgetTouchEvent.isDoubleTouched = true;
        }

        return widgetTouchEvent;
    }

    private WidgetTouchEvent isTouchedForExpandMode(List<Input.TouchEvent> touchEvents) {
        WidgetTouchEvent widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
        widgetTouchEvent.isTouched = false;
        widgetTouchEvent.isTouchedDown = false;
        widgetTouchEvent.isDoubleTouched = false;
        widgetTouchEvent.object = null;

        indexTouched.clear();
        int touchCount = 0;

        float gaps = (0.8f * AssetsAndResource.MazeHeight)/((float) cardStack.size());

        if (gaps > glcard.width/2.0f) {
            gaps = glcard.width/2.0f;
        }

        int midPoint = cardStack.size()/2 + cardStack.size()%2 -1;

        setIdentityM(AssetsAndResource.tempMatrix, 0);
        if (Position.rotaion.angle != 0) {
            rotateM(AssetsAndResource.tempMatrix, 0, AssetsAndResource.CameraAngle, -1.0f, 0f, 0f);
        }

        Input input = AssetsAndResource.game.getInput();
        if (input.isTouchDown(0)) {
            GLPoint relativeNearPointAfterTrans = input.getNearPoint(0).translate(AssetsAndResource.CameraPosition.getVector().scale(-1/4.0f));
            GLPoint relativeFarPointAfterTrans = input.getFarPoint(0).translate(AssetsAndResource.CameraPosition.getVector().scale(-1/4.0f));

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

            float x;
            int index;

            if (width <= (((midPoint + 2) * gaps) + (glcard.width * scale)/2.0f) &&
                    height <= (glcard.height * scale)/2.0f) {
                x = (intersectingPoint.x + midPoint * gaps)/ gaps;
                if (x > cardStack.size()) {
                    index = cardStack.size() - 1;
                } else if (x > 0){
                    index = (int) Math.floor(x);
                } else {
                    index = 0;
                }

                for (int i = index; i>= 0 ; i--) {
                    WidgetPosition widgetPosition = cardWidgetPositionTable.get(cardStack.get(i));

                    if (widgetPosition != null) {
                        width = Math.abs(intersectingPoint.x - widgetPosition.Centerposition.x);
                        if (width <= (glcard.width * widgetPosition.X_scale)/2 && height <= (glcard.height * widgetPosition.Z_scale)/2) {
                            indexTouched.add(0, new Integer(i));
                        } else {
                            break;
                        }
                    } else {
                        // Should not happen
                        break;
                    }
                }

                for (int i = index + 1; i < cardStack.size(); i++) {
                    WidgetPosition widgetPosition = cardWidgetPositionTable.get(cardStack.get(i));

                    if (widgetPosition != null) {
                        width = Math.abs(intersectingPoint.x - widgetPosition.Centerposition.x);
                        if (width <= (glcard.width * widgetPosition.X_scale)/2 && height <= (glcard.height * widgetPosition.Z_scale)/2) {
                            indexTouched.add(new Integer(i));
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }

                if (indexTouched.size() > 0) {
                    index = cardStack.indexOf(SelectedCard);
                    widgetTouchEvent.isTouched = true;
                    widgetTouchEvent.isTouchedDown = true;
                    if (index < indexTouched.get(0)) {
                        widgetTouchEvent.object = cardStack.get(indexTouched.get(0));
                        SelectedCard = (Cards) widgetTouchEvent.object;
                    } else if (index > indexTouched.get(indexTouched.size() -1)) {
                        widgetTouchEvent.object = cardStack.get(indexTouched.get(indexTouched.size() -1));
                        SelectedCard = (Cards) widgetTouchEvent.object;
                    } else {
                        widgetTouchEvent.object = SelectedCard;
                    }

                    return widgetTouchEvent;
                }
            }
        }

        ArrayList<Cards> temporaryCardList = new ArrayList<Cards>();
        for (int i = 0; i < touchEvents.size(); i++) {
            Input.TouchEvent event = touchEvents.get(i);
            indexTouched.clear();
            if (event.type == Input.TouchEvent.TOUCH_UP) {
                GLPoint relativeNearPointAfterTrans = event.nearPoint[0].translate(AssetsAndResource.CameraPosition.getVector().scale(-1 / 4.0f));
                GLPoint relativeFarPointAfterTrans = event.farPoint[0].translate(AssetsAndResource.CameraPosition.getVector().scale(-1/4.0f));

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
                float x;
                int index;

                if (width <= (((midPoint + 2) * gaps) + (glcard.width * scale)/2.0f) &&
                        height <= (glcard.height * scale)/2.0f) {
                    x = (intersectingPoint.x + midPoint * gaps) / gaps;
                    if (x > cardStack.size()) {
                        index = cardStack.size() - 1;
                    } else if (x > 0){
                        index = (int) Math.floor(x);
                    } else {
                        index = 0;
                    }

                    for (int j = index; j>= 0 ; j--) {
                        WidgetPosition widgetPosition = cardWidgetPositionTable.get(cardStack.get(i));

                        if (widgetPosition != null) {
                            width = Math.abs(intersectingPoint.x - widgetPosition.Centerposition.x);
                            if (width <= (glcard.width * widgetPosition.X_scale)/2 && height <= (glcard.height * widgetPosition.Z_scale)/2) {
                                indexTouched.add(0, new Integer(i));
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }

                    for (int j = index + 1; j < cardStack.size(); j++) {
                        WidgetPosition widgetPosition = cardWidgetPositionTable.get(cardStack.get(i));

                        if (widgetPosition != null) {
                            width = Math.abs(intersectingPoint.x - widgetPosition.Centerposition.x);
                            if (width <= (glcard.width * widgetPosition.X_scale)/2 && height <= (glcard.height * widgetPosition.Z_scale)/2) {
                                indexTouched.add(new Integer(i));
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }

                    if (indexTouched.size() > 0) {
                        index = cardStack.indexOf(SelectedCard);
                        widgetTouchEvent.isTouched = true;
                        touchCount++;
                        if (index < indexTouched.get(0)) {
                            temporaryCardList.add(cardStack.get(indexTouched.get(0)));
                        } else if (index > indexTouched.get(indexTouched.size() -1)) {
                            temporaryCardList.add(cardStack.get(indexTouched.get(indexTouched.size() -1)));
                        } else {
                            temporaryCardList.add(SelectedCard);
                        }
                    }
                }
            }
        }

        if (widgetTouchEvent.isTouched && touchCount > 1) {
            int size = temporaryCardList.size();
            if (temporaryCardList.get(size -1) == temporaryCardList.get(size -2)) {
                widgetTouchEvent.isDoubleTouched = true;
                widgetTouchEvent.object = temporaryCardList.get(size -1);
                SelectedCard = (Cards) widgetTouchEvent.object;
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
        Position.Centerposition.x = position.Centerposition.x;
        Position.Centerposition.y = position.Centerposition.y;
        Position.Centerposition.z = position.Centerposition.z;
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
        cube = (Cube) objs[0];
        glcard = (XZRectangle) objs[1];
    }

    @Override
    public void LinkLogicalObject(Object obj) {
        cardStack = (ArrayList<Cards>) obj;
        SelectedCard = cardStack.get(0);
    }

    @Override
    public void setMode(WidgetMode mode) {
        if (this.mode != mode) {
            this.mode = WidgetMode.Transition;
        }
    }
}
