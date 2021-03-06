package koustav.duelmasters.main.androidgameduelmasterwidgetmodels;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.PvPWidgetCoordinator;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.Widget;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetMode;
import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl.ViewNodePosition;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglmotionmodels.DriftSystem;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglobjectmodels.Cube;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.DrawObjectHelper;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLGeometry.*;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.MatrixHelper;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Input;

import static android.opengl.Matrix.*;

/**
 * Created by Koustav on 3/21/2016.
 */
public class CardStackWidget implements Widget{
    enum TransitionState {
        START,
        RUNNING,
        END,
    }
    TransitionState S;

    // Misc var
    ViewNodePosition Position;
    ViewNodePosition init_position;
    ViewNodePosition ref_position;
    float[] GapVector;
    float[] relativeNearPointAfterRot;
    float[] relativeFarPointAfterRot;
    GLPoint relativeNearPoint;
    GLPoint relativeFarPoint;
    WidgetMode mode;
    WidgetMode previousMode;
    Hashtable<Cards, ViewNodePosition> cardWidgetPositionTable;
    Cards SelectedCard;
    ArrayList<Cards> pickedCardsFromTheList;
    ArrayList<Integer> indexTouched;
    float scale;
    int moving;
    int completeCount;
    float k1;
    float k2;
    float angle;
    float rotationDir_x;
    float rotationDir_y;
    float rotationDir_z;
    ArrayList<CardWidget> cardWidgets;
    Hashtable<CardWidget, ViewNodePosition> cardWidgetToWidgetPositionHashtable;
    ArrayList<Object> cardOriginalList;
    int originalSelectedCardPosition;

    // Stack orientation
    boolean flip;

    // Motion model
    Hashtable<Cards, DriftSystem> cardsDriftSystemHashtable;
    Hashtable<CardWidget, DriftSystem> cardWidgetDriftSystemHashtable;

    // shadow enable;
    boolean shadowEnable;

    // Texture Array
    int[] textureArrays;

    // openGL object model, physical unit
    Cube cube;
    Cube glcard;
    Cube glCurrentSelect;
    Cube glSelectedCards;

    // Logical object
    ArrayList<Cards> cardStack;

    public CardStackWidget() {
        S = TransitionState.START;

        Position = new ViewNodePosition();
        init_position = new ViewNodePosition();
        ref_position = new ViewNodePosition();
        GapVector = new float[4];
        relativeNearPointAfterRot = new float[4];
        relativeFarPointAfterRot = new float[4];
        relativeNearPoint = new GLPoint(0, 0, 0);
        relativeFarPoint = new GLPoint(0, 0, 0);
        mode = WidgetMode.Normal;
        previousMode = WidgetMode.Normal;
        cardWidgetPositionTable = new Hashtable<Cards, ViewNodePosition>();
        cardsDriftSystemHashtable = new Hashtable<Cards, DriftSystem>();
        SelectedCard = null;
        pickedCardsFromTheList = new ArrayList<Cards>();
        indexTouched = new ArrayList<Integer>();
        scale = 2.0f;
        moving = 0;
        completeCount = 0;
        k1 = 4.0f;
        k2 = 4.0f;
        cardWidgets = new ArrayList<CardWidget>();
        cardWidgetToWidgetPositionHashtable = new Hashtable<CardWidget, ViewNodePosition>();
        cardOriginalList = new ArrayList<Object>();
        originalSelectedCardPosition = 0;
        cardWidgetDriftSystemHashtable = new Hashtable<CardWidget, DriftSystem>();

        flip = false;

        textureArrays = new int[6];
        shadowEnable = false;
        cube = null;
        glcard = null;
        cardStack = null;

        GLVector rotationAngle = new GLVector(0, 1f, 0f).crossProduct(AssetsAndResource.CameraPosition.getVector().getDirection());
        angle = (float) Math.toDegrees(Math.asin(rotationAngle.getMagnitude()));
        GLVector rotationDir = rotationAngle.getDirection();
        rotationDir_x = rotationDir.x;
        rotationDir_y = rotationDir.y;
        rotationDir_z = rotationDir.z;

        setIdentityM(AssetsAndResource.tempMatrix, 0);
        if (angle != 0) {
            rotateM(AssetsAndResource.tempMatrix, 0, angle, rotationDir_x, rotationDir_y, rotationDir_z);
        }
        multiplyMV(GapVector, 0, AssetsAndResource.tempMatrix, 0, new float[] {1,
                0, 0, 0f}, 0);
    }

    public ArrayList<CardWidget> getCardWidgets() {
        return cardWidgets;
    }

    public ArrayList<CardWidget> getAndClearCardWidgets() {
        ArrayList<CardWidget> list = new ArrayList<CardWidget>(cardWidgets);
        cardWidgets.clear();
        cardWidgetToWidgetPositionHashtable.clear();
        return list;
    }

    public boolean IsWidgetInTransition() {
        if (mode == WidgetMode.ExpandLock || mode == WidgetMode.Transition) {
            return true;
        }
        return false;
    }

    public void lockCardsInExpandMode(ArrayList<Integer> index, PvPWidgetCoordinator coordinator) {
        if (mode != WidgetMode.Expand) {
            throw new RuntimeException("Not in Expand Mode");
        }
        int count = 0;
        cardWidgets.clear();
        cardOriginalList.clear();
        cardWidgetToWidgetPositionHashtable.clear();
        cardWidgetDriftSystemHashtable.clear();
        for (int i = 0; i < cardStack.size(); i++) {
            Cards card = cardStack.get(i);
            if (index.contains(i)) {
                if (card.getWidget() != null) {
                    throw new RuntimeException("Invalid Condition");
                }
                CardWidget widget = coordinator.newCardWidget();
                coordinator.CoupleWidgetForCard(card, widget);
                ViewNodePosition cardPosition = new ViewNodePosition();
                ViewNodePosition originalCardPosition = cardWidgetPositionTable.get(card);
                cardPosition.Centerposition.x = originalCardPosition.Centerposition.x;
                cardPosition.Centerposition.y = originalCardPosition.Centerposition.y;
                cardPosition.Centerposition.z = originalCardPosition.Centerposition.z;
                cardPosition.rotaion.angle = originalCardPosition.rotaion.angle;
                cardPosition.rotaion.x = originalCardPosition.rotaion.x;
                cardPosition.rotaion.y = originalCardPosition.rotaion.y;
                cardPosition.rotaion.z = originalCardPosition.rotaion.z;
                cardPosition.X_scale = originalCardPosition.X_scale;
                cardPosition.Y_scale = originalCardPosition.Y_scale;
                cardPosition.Z_scale = originalCardPosition.Z_scale;

                widget.setTranslateRotateScale(cardPosition);
                cardWidgets.add(widget);
                cardOriginalList.add(widget);
                cardWidgetToWidgetPositionHashtable.put(widget, cardPosition);
            } else {
                count++;
                cardOriginalList.add(card);
            }
        }

        if (count == cardStack.size()) {
            throw new RuntimeException("No Index found");
        }
        originalSelectedCardPosition = cardStack.indexOf(SelectedCard);
        mode = WidgetMode.ExpandLock;
        SelectedCard = null;
    }

    @Override
    public void update(float deltaTime, float totalTime) {
        if (mode == WidgetMode.Transition) {
            updateTransition(deltaTime, totalTime);
        }

        if (mode == WidgetMode.ExpandLock) {
            updateExpandLock(deltaTime, totalTime);
        }
    }

    // update transition
    private void updateTransition(float deltaTime, float totalTime) {
        ViewNodePosition widgetPosition;
        DriftSystem driftSystem;

        float gaps = (0.8f * AssetsAndResource.MazeHeight)/((float) cardStack.size());

        if (gaps > glcard.width/2.0f) {
            gaps = glcard.width/2.0f;
        }

        int midPoint = cardStack.size()/2 + cardStack.size()%2 -1;

        if (previousMode == WidgetMode.Normal) {
            moving = 0;
            if (S == TransitionState.START) {
                cardWidgetPositionTable.clear();
                cardsDriftSystemHashtable.clear();
                S = TransitionState.RUNNING;
            }

            if (S == TransitionState.RUNNING) {
                completeCount = 0;
                for (int i = 0; i < cardStack.size(); i++) {
                    moving++;
                    Cards card = cardStack.get(i);
                    widgetPosition = null;
                    widgetPosition = cardWidgetPositionTable.get(card);
                    driftSystem = cardsDriftSystemHashtable.get(card);

                    if (widgetPosition == null) {
                        widgetPosition = new ViewNodePosition();
                        driftSystem = new DriftSystem();

                        cardWidgetPositionTable.put(card, widgetPosition);
                        cardsDriftSystemHashtable.put(card, driftSystem);

                        ref_position.rotaion.angle = angle;
                        ref_position.rotaion.x = rotationDir_x;
                        ref_position.rotaion.y = rotationDir_y;
                        ref_position.rotaion.z = rotationDir_z;

                        GLVector gapVec = new GLVector(GapVector[0], GapVector[1], GapVector[2]).getDirection();
                        ref_position.Centerposition.x = ((AssetsAndResource.CameraPosition.x / 4.0f) + (((float) (i - midPoint)) * gaps * gapVec.x));
                        ref_position.Centerposition.y = ((AssetsAndResource.CameraPosition.y / 4.0f) + (((float) (i - midPoint)) * gaps * gapVec.y));
                        ref_position.Centerposition.z = ((AssetsAndResource.CameraPosition.z / 4.0f) + (((float) (i - midPoint)) * gaps * gapVec.z));
                        ref_position.X_scale = (scale * ((39.0f - (0.3f * (float) i))/39.0f));
                        ref_position.Y_scale = 1f;
                        ref_position.Z_scale = (scale * ((39.0f - (0.3f * (float) i))/39.0f));

                        ArrayList<GLAngularRotaion> rotaions = new ArrayList<GLAngularRotaion>();
                        GLAngularRotaion rotaion;
                        if (flip) {
                            rotaion = new GLAngularRotaion(180f, 0, 0, 1f);
                            rotaions.add(rotaion);
                        }
                        rotaions.add(Position.rotaion);
                        rotaion = MatrixHelper.getCombinedRotation(rotaions);

                        init_position.Centerposition.x = Position.Centerposition.x;
                        if (cardStack.size() > 0) {
                            init_position.Centerposition.y = (Position.Centerposition.y + (0.5f * cube.length * Position.Y_scale) -
                                    (glcard.length * ((float) (i))));
                        } else {
                            init_position.Centerposition.y = Position.Centerposition.y;
                        }
                        init_position.Centerposition.z = Position.Centerposition.z;
                        init_position.rotaion.angle = rotaion.angle;
                        init_position.rotaion.x = rotaion.x;
                        init_position.rotaion.y = rotaion.y;
                        init_position.rotaion.z = rotaion.z;
                        init_position.X_scale = Position.X_scale;
                        init_position.Y_scale = 1f;
                        init_position.Z_scale = Position.Z_scale;
                        driftSystem.setDriftInfo(init_position, ref_position, null, null, k1, k2, totalTime);
                    }

                    float percentageComplete = driftSystem.getPercentageComplete(totalTime);
                    ViewNodePosition updatePosition = driftSystem.getUpdatePosition(totalTime);

                    if (percentageComplete == 1.0f) {
                        completeCount++;
                    }

                    widgetPosition.Centerposition.x = updatePosition.Centerposition.x;
                    widgetPosition.Centerposition.y = updatePosition.Centerposition.y;
                    widgetPosition.Centerposition.z = updatePosition.Centerposition.z;
                    widgetPosition.rotaion.angle = updatePosition.rotaion.angle;
                    widgetPosition.rotaion.x = updatePosition.rotaion.x;
                    widgetPosition.rotaion.y = updatePosition.rotaion.y;
                    widgetPosition.rotaion.z = updatePosition.rotaion.z;
                    widgetPosition.X_scale = updatePosition.X_scale;
                    widgetPosition.Y_scale = updatePosition.Y_scale;
                    widgetPosition.Z_scale = updatePosition.Z_scale;

                    if ((i <cardStack.size() - 1) && cardWidgetPositionTable.get(cardStack.get(i + 1)) == null && percentageComplete <= 0.1) {
                        break;
                    }
                }

                if (completeCount == cardStack.size()) {
                    S = TransitionState.END;
                }
            }

            if (S == TransitionState.END) {
                cardsDriftSystemHashtable.clear();
                S = TransitionState.START;
                mode = WidgetMode.Expand;
                previousMode = WidgetMode.Expand;
                if (cardStack != null && cardStack.size() > 0) {
                    SelectedCard = cardStack.get(0);
                }
            }

        } else if (previousMode == WidgetMode.Expand) {
            if (S == TransitionState.START) {
                cardsDriftSystemHashtable.clear();
                S = TransitionState.RUNNING;
            }


            if (S == TransitionState.RUNNING) {
                completeCount = 0;
                for (int i = cardStack.size() - 1; i >= 0; i--) {
                    widgetPosition = null;
                    Cards card = cardStack.get(i);
                    widgetPosition = cardWidgetPositionTable.get(card);
                    driftSystem = cardsDriftSystemHashtable.get(card);

                    if (widgetPosition == null) {
                        throw new RuntimeException("widgetPosition cannot be null");
                    }

                    if (driftSystem == null) {
                        driftSystem = new DriftSystem();
                        cardsDriftSystemHashtable.put(card, driftSystem);

                        ArrayList<GLAngularRotaion> rotaions = new ArrayList<GLAngularRotaion>();
                        GLAngularRotaion rotaion;
                        if (flip) {
                            rotaion = new GLAngularRotaion(180f, 0, 0, 1f);
                            rotaions.add(rotaion);
                        }
                        rotaions.add(Position.rotaion);
                        rotaion = MatrixHelper.getCombinedRotation(rotaions);

                        ref_position.Centerposition.x = Position.Centerposition.x;
                        if (cardStack.size() > 0) {
                            ref_position.Centerposition.y = (Position.Centerposition.y + (0.5f * cube.length * Position.Y_scale) -
                                    (glcard.length * ((float) (i + 1))));
                        } else {
                            ref_position.Centerposition.y = Position.Centerposition.y;
                        }
                        ref_position.Centerposition.z = Position.Centerposition.z;
                        ref_position.rotaion.angle = rotaion.angle;
                        ref_position.rotaion.x = rotaion.x;
                        ref_position.rotaion.y = rotaion.y;
                        ref_position.rotaion.z = rotaion.z;
                        ref_position.X_scale = Position.X_scale;
                        ref_position.Y_scale = 1.0f;
                        ref_position.Z_scale =Position.Z_scale;

                        init_position.Centerposition.x = widgetPosition.Centerposition.x;
                        init_position.Centerposition.y = widgetPosition.Centerposition.y;
                        init_position.Centerposition.z = widgetPosition.Centerposition.z;
                        init_position.rotaion.angle = widgetPosition.rotaion.angle;
                        init_position.rotaion.x = widgetPosition.rotaion.x;
                        init_position.rotaion.y = widgetPosition.rotaion.y;
                        init_position.rotaion.z = widgetPosition.rotaion.z;
                        init_position.X_scale = widgetPosition.X_scale;
                        init_position.Y_scale = 1.0f;
                        init_position.Z_scale = widgetPosition.Z_scale;

                        driftSystem.setDriftInfo(init_position, ref_position, null, null, k1, k2, totalTime);
                    }

                    float percentageComplete = driftSystem.getPercentageComplete(totalTime);
                    ViewNodePosition updatePosition = driftSystem.getUpdatePosition(totalTime);

                    if (percentageComplete == 1.0f) {
                        completeCount++;
                    }

                    widgetPosition.Centerposition.x = updatePosition.Centerposition.x;
                    widgetPosition.Centerposition.y = updatePosition.Centerposition.y;
                    widgetPosition.Centerposition.z = updatePosition.Centerposition.z;
                    widgetPosition.rotaion.angle = updatePosition.rotaion.angle;
                    widgetPosition.rotaion.x = updatePosition.rotaion.x;
                    widgetPosition.rotaion.y = updatePosition.rotaion.y;
                    widgetPosition.rotaion.z = updatePosition.rotaion.z;
                    widgetPosition.X_scale = updatePosition.X_scale;
                    widgetPosition.Y_scale = updatePosition.Y_scale;
                    widgetPosition.Z_scale = updatePosition.Z_scale;

                    if (i > 0 && (cardsDriftSystemHashtable.get(cardStack.get(i - 1)) == null) && percentageComplete <= 0.1) {
                        break;
                    }
                }

                if (completeCount == cardStack.size()) {
                    S = TransitionState.END;
                }
            }

            if (S == TransitionState.END) {
                cardsDriftSystemHashtable.clear();
                S = TransitionState.START;
                mode = WidgetMode.Normal;
                previousMode = WidgetMode.Normal;
                SelectedCard = null;

                cardWidgetPositionTable.clear();
            }

        } else {
            throw new IllegalArgumentException("Can't be in this Mode");
        }
    }

    // update Expand Lock
    private void updateExpandLock(float deltaTime, float totalTime) {
        completeCount = 0;
        for (int i = 0; i < cardWidgets.size(); i++) {
            CardWidget widget = cardWidgets.get(i);
            int position = cardOriginalList.indexOf(widget);
            if (!(position >= 0 && position < cardOriginalList.size())) {
                throw new RuntimeException("Invalid condition");
            }
            DriftSystem driftSystem = cardWidgetDriftSystemHashtable.get(widget);
            if (driftSystem == null) {
                driftSystem = new DriftSystem();
                cardWidgetDriftSystemHashtable.put(widget, driftSystem);
                ViewNodePosition widgetPosition = cardWidgetToWidgetPositionHashtable.get(widget);
                if (widgetPosition == null) {
                    throw new RuntimeException("Invalid Condition");
                }

                init_position.Centerposition.x = widgetPosition.Centerposition.x;
                init_position.Centerposition.y = widgetPosition.Centerposition.y;
                init_position.Centerposition.z = widgetPosition.Centerposition.z;
                init_position.rotaion.angle = widgetPosition.rotaion.angle;
                init_position.rotaion.x = widgetPosition.rotaion.x;
                init_position.rotaion.y = widgetPosition.rotaion.y;
                init_position.rotaion.z = widgetPosition.rotaion.z;
                init_position.X_scale = widgetPosition.X_scale;
                init_position.Y_scale = 1.0f;
                init_position.Z_scale = widgetPosition.Z_scale;

                ArrayList<ViewNodePosition> trans_position = new ArrayList<ViewNodePosition>();
                ArrayList<Float> tracking_point = new ArrayList<Float>();
                if (position != originalSelectedCardPosition) {
                    ViewNodePosition position1 = new ViewNodePosition();
                    GLVector gapVec = new GLVector(GapVector[0], GapVector[1], GapVector[2]).getDirection();
                    float gap = (position > originalSelectedCardPosition) ? (2 * AssetsAndResource.CardWidth):
                            (2 * (-AssetsAndResource.CardWidth));
                    position1.Centerposition.x = widgetPosition.Centerposition.x + (gap * gapVec.x) + AssetsAndResource.CameraPosition.x * 0.01f;
                    position1.Centerposition.y = widgetPosition.Centerposition.y + (gap * gapVec.y) + AssetsAndResource.CameraPosition.y * 0.01f;
                    position1.Centerposition.z = widgetPosition.Centerposition.z + (gap * gapVec.z) + AssetsAndResource.CameraPosition.z * 0.01f;
                    position1.rotaion.angle = widgetPosition.rotaion.angle;
                    position1.rotaion.x = widgetPosition.rotaion.x;
                    position1.rotaion.y = widgetPosition.rotaion.y;
                    position1.rotaion.z = widgetPosition.rotaion.z;
                    position1.X_scale = widgetPosition.X_scale;
                    position1.Y_scale = 1.0f;
                    position1.Z_scale = widgetPosition.Z_scale;

                    trans_position.add(position1);
                    tracking_point.add(new Float(0.5f));
                }
                ref_position.Centerposition.x = widgetPosition.Centerposition.x;
                ref_position.Centerposition.y = widgetPosition.Centerposition.y + (AssetsAndResource.CardHeight/4);
                ref_position.Centerposition.z = widgetPosition.Centerposition.z - (AssetsAndResource.CardHeight/8);
                ref_position.rotaion.angle = widgetPosition.rotaion.angle;
                ref_position.rotaion.x = widgetPosition.rotaion.x;
                ref_position.rotaion.y = widgetPosition.rotaion.y;
                ref_position.rotaion.z = widgetPosition.rotaion.z;
                ref_position.X_scale = widgetPosition.X_scale;
                ref_position.Y_scale = 1.0f;
                ref_position.Z_scale = widgetPosition.Z_scale;

                driftSystem.setDriftInfo(init_position, ref_position, trans_position, tracking_point, k1, k2, totalTime);
            } else {
                ViewNodePosition updatePosition = driftSystem.getUpdatePosition(totalTime);
                float percentageComplete = driftSystem.getPercentageComplete(totalTime);
                ViewNodePosition widgetPosition = cardWidgetToWidgetPositionHashtable.get(widget);

                if (widgetPosition == null) {
                    throw new RuntimeException("Invalid Condition");
                }

                if (percentageComplete == 1.0f) {
                    completeCount++;
                }

                widgetPosition.Centerposition.x = updatePosition.Centerposition.x;
                widgetPosition.Centerposition.y = updatePosition.Centerposition.y;
                widgetPosition.Centerposition.z = updatePosition.Centerposition.z;
                widgetPosition.rotaion.angle = updatePosition.rotaion.angle;
                widgetPosition.rotaion.x = updatePosition.rotaion.x;
                widgetPosition.rotaion.y = updatePosition.rotaion.y;
                widgetPosition.rotaion.z = updatePosition.rotaion.z;
                widgetPosition.X_scale = updatePosition.X_scale;
                widgetPosition.Y_scale = updatePosition.Y_scale;
                widgetPosition.Z_scale = updatePosition.Z_scale;
            }
        }

        if (completeCount == cardWidgets.size()) {
            cardOriginalList.clear();
            cardWidgetDriftSystemHashtable.clear();
            setMode(WidgetMode.Normal);
        }
    }

    @Override
    public void  draw() {
        if (mode == WidgetMode.Normal) {
            drawNormal();
        }

        if (mode == WidgetMode.Transition) {
            drawTransition();
        }

        if (mode == WidgetMode.Expand) {
            drawExpand();
        }

        if (mode == WidgetMode.ExpandLock) {
            drawExpandLock();
        }
    }

    // Draw the widget in normal mode
    private void drawNormal() {
        for (int i = 0; i< 6; i++) {
            textureArrays[i] = AssetsAndResource.getFixedTexture(AssetsAndResource.cardDeckSidesID);
        }
        Cards cardT = cardStack.get(0);
        Cards cardB = cardStack.get(cardStack.size() - 1);
        textureArrays[2] = flip ? AssetsAndResource.getCardTexture(cardB.getNameID()/*"AquaHulcus"*/) : AssetsAndResource.getFixedTexture(AssetsAndResource.cardBacksideID);
        textureArrays[3] = flip ? AssetsAndResource.getFixedTexture(AssetsAndResource.cardBacksideID) : AssetsAndResource.getCardTexture(cardT.getNameID()/*"AquaHulcus"*/);

        DrawObjectHelper.drawOneCube(cube, textureArrays, shadowEnable);

        for (int i = 0; i < cardWidgets.size(); i++) {
            CardWidget widget = cardWidgets.get(i);
            ViewNodePosition widgetPosition = cardWidgetToWidgetPositionHashtable.get(widget);

            if (widgetPosition == null) {
                throw new RuntimeException("widgetPosition cannot be null");
            }

            widget.setTranslateRotateScale(widgetPosition);
            widget.draw();
        }
    }

    // Draw the widget in transition mode
    private void drawTransition() {
        ViewNodePosition widgetPosition;

        if (previousMode == WidgetMode.Normal) {
            if (cardStack.size() - moving > 2) {
                widgetPosition = new ViewNodePosition();
                widgetPosition.rotaion.angle = Position.rotaion.angle;
                widgetPosition.rotaion.x = Position.rotaion.x;
                widgetPosition.rotaion.y = Position.rotaion.y;
                widgetPosition.rotaion.z = Position.rotaion.z;
                widgetPosition.Centerposition.x = Position.Centerposition.x;
                if (cardStack.size() > 0) {
                    widgetPosition.Centerposition.y = Position.Centerposition.y -
                            ((float) moving * glcard.length * 0.5f);
                } else {
                    widgetPosition.Centerposition.y = Position.Centerposition.y;
                }
                widgetPosition.Centerposition.z = Position.Centerposition.z;
                widgetPosition.X_scale = Position.X_scale;
                widgetPosition.Y_scale = Position.Y_scale * ((float) (Math.abs(cardStack.size() - moving)) / cardStack.size());
                widgetPosition.Z_scale = Position.Z_scale;

                for (int i = 0; i< 6; i++) {
                    textureArrays[i] = AssetsAndResource.getFixedTexture(AssetsAndResource.cardDeckSidesID);
                }
                Cards cardT = cardStack.get(moving);
                Cards cardB = cardStack.get(cardStack.size() - 1);
                textureArrays[2] = flip ? AssetsAndResource.getCardTexture(cardB.getNameID()/*"AquaHulcus"*/) : AssetsAndResource.getFixedTexture(AssetsAndResource.cardBacksideID);
                textureArrays[3] = flip ? AssetsAndResource.getFixedTexture(AssetsAndResource.cardBacksideID) : AssetsAndResource.getCardTexture(cardT.getNameID()/*"AquaHulcus"*/);

                MatrixHelper.setTranslateRotateScale(widgetPosition);
                DrawObjectHelper.drawOneCube(cube, textureArrays, shadowEnable);

            } else if (cardStack.size() - moving > 0) {
                widgetPosition = new ViewNodePosition();
                widgetPosition.rotaion.angle = Position.rotaion.angle;
                widgetPosition.rotaion.x = Position.rotaion.x;
                widgetPosition.rotaion.y = Position.rotaion.y;
                widgetPosition.rotaion.z = Position.rotaion.z;
                widgetPosition.Centerposition.x = Position.Centerposition.x;
                if (cardStack.size() > 0) {
                    widgetPosition.Centerposition.y = Position.Centerposition.y -
                            ((float) (cardStack.size() - 3)  * glcard.length *  0.5f);
                } else {
                    widgetPosition.Centerposition.y = Position.Centerposition.y;
                }
                widgetPosition.Centerposition.z = Position.Centerposition.z;
                widgetPosition.X_scale = Position.X_scale;
                widgetPosition.Y_scale = Position.Y_scale * (3.0f / cardStack.size());
                widgetPosition.Z_scale = Position.Z_scale;

                for (int i = 0; i< 6; i++) {
                    textureArrays[i] = AssetsAndResource.getFixedTexture(AssetsAndResource.cardDeckSidesID);
                }
                Cards cardT = cardStack.get(moving);
                Cards cardB = cardStack.get(cardStack.size() - 1);
                textureArrays[2] = flip ? AssetsAndResource.getCardTexture(cardB.getNameID()/*"AquaHulcus"*/) : AssetsAndResource.getFixedTexture(AssetsAndResource.cardBacksideID);
                textureArrays[3] = flip ? AssetsAndResource.getFixedTexture(AssetsAndResource.cardBacksideID) : AssetsAndResource.getCardTexture(cardT.getNameID()/*"AquaHulcus"*/);

                MatrixHelper.setTranslateRotateScale(widgetPosition);
                DrawObjectHelper.drawOneCube(cube, textureArrays, shadowEnable);

            }

            for (int i = cardStack.size() - 1; i >= 0; i--) {
                Cards card = cardStack.get(i);
                widgetPosition = cardWidgetPositionTable.get(card);

                if (widgetPosition == null) {
                    continue;
                }

                MatrixHelper.setTranslateRotateScale(widgetPosition);
                for (int j = 0; j< 6; j++) {
                    textureArrays[j] = AssetsAndResource.getFixedTexture(AssetsAndResource.cardBorderID);
                }
                textureArrays[2] = AssetsAndResource.getFixedTexture(AssetsAndResource.cardBacksideID);
                textureArrays[3] = AssetsAndResource.getCardTexture(card.getNameID()/*"AquaHulcus"*/);
                DrawObjectHelper.drawOneCube(glcard, textureArrays, shadowEnable);
            }

        } else if (previousMode == WidgetMode.Expand) {
            if (completeCount > 2) {
                widgetPosition = new ViewNodePosition();
                widgetPosition.rotaion.angle = Position.rotaion.angle;
                widgetPosition.rotaion.x = Position.rotaion.x;
                widgetPosition.rotaion.y = Position.rotaion.y;
                widgetPosition.rotaion.z = Position.rotaion.z;
                widgetPosition.Centerposition.x = Position.Centerposition.x;
                if (cardStack.size() > 0) {
                    widgetPosition.Centerposition.y = Position.Centerposition.y -
                            ((float) (cardStack.size() - completeCount) * glcard.length * 0.5f);
                } else {
                    widgetPosition.Centerposition.y = Position.Centerposition.y;
                }
                widgetPosition.Centerposition.z = Position.Centerposition.z;
                widgetPosition.X_scale = Position.X_scale;
                widgetPosition.Y_scale = Position.Y_scale * ((float) (completeCount) / cardStack.size());
                widgetPosition.Z_scale = Position.Z_scale;

                for (int i = 0; i< 6; i++) {
                    textureArrays[i] = AssetsAndResource.getFixedTexture(AssetsAndResource.cardDeckSidesID);
                }
                Cards cardT = cardStack.get(cardStack.size() - completeCount);
                Cards cardB = cardStack.get(cardStack.size() - 1);
                textureArrays[2] = flip ? AssetsAndResource.getCardTexture(cardB.getNameID()/*"AquaHulcus"*/) : AssetsAndResource.getFixedTexture(AssetsAndResource.cardBacksideID);
                textureArrays[3] = flip ? AssetsAndResource.getFixedTexture(AssetsAndResource.cardBacksideID) : AssetsAndResource.getCardTexture(cardT.getNameID()/*"AquaHulcus"*/);

                MatrixHelper.setTranslateRotateScale(widgetPosition);
                DrawObjectHelper.drawOneCube(cube, textureArrays, shadowEnable);

            } else if (completeCount > 0) {
                widgetPosition = new ViewNodePosition();
                widgetPosition.rotaion.angle = Position.rotaion.angle;
                widgetPosition.rotaion.x = Position.rotaion.x;
                widgetPosition.rotaion.y = Position.rotaion.y;
                widgetPosition.rotaion.z = Position.rotaion.z;
                widgetPosition.Centerposition.x = Position.Centerposition.x;
                if (cardStack.size() > 0) {
                    widgetPosition.Centerposition.y = Position.Centerposition.y -
                            ((float) (cardStack.size() - 3) * glcard.length * 0.5f);
                } else {
                    widgetPosition.Centerposition.y = Position.Centerposition.y;
                }
                widgetPosition.Centerposition.z = Position.Centerposition.z;
                widgetPosition.X_scale = Position.X_scale;
                widgetPosition.Y_scale = Position.Y_scale * (3.0f / cardStack.size());
                widgetPosition.Z_scale = Position.Z_scale;

                for (int i = 0; i< 6; i++) {
                    textureArrays[i] = AssetsAndResource.getFixedTexture(AssetsAndResource.cardDeckSidesID);
                }
                Cards cardT = cardStack.get(cardStack.size() - completeCount);
                Cards cardB = cardStack.get(cardStack.size() - 1);
                textureArrays[2] = flip ? AssetsAndResource.getCardTexture(cardB.getNameID()/*"AquaHulcus"*/) : AssetsAndResource.getFixedTexture(AssetsAndResource.cardBacksideID);
                textureArrays[3] = flip ? AssetsAndResource.getFixedTexture(AssetsAndResource.cardBacksideID) : AssetsAndResource.getCardTexture(cardT.getNameID()/*"AquaHulcus"*/);

                MatrixHelper.setTranslateRotateScale(widgetPosition);
                DrawObjectHelper.drawOneCube(cube, textureArrays, shadowEnable);

            }

            int selectedCardIndex = cardStack.indexOf(SelectedCard);
            int limit;
            if (selectedCardIndex > (cardStack.size() - completeCount -1)) {
                limit = cardStack.size() - completeCount -1;
            } else {
                limit = selectedCardIndex;
            }

            for (int i = cardStack.size() -1 - completeCount; i > selectedCardIndex; i--) {
                widgetPosition = null;
                Cards card = cardStack.get(i);
                widgetPosition = cardWidgetPositionTable.get(card);

                if (widgetPosition == null) {
                    throw new RuntimeException("widgetPosition cannot be null");
                }

                MatrixHelper.setTranslateRotateScale(widgetPosition);
                for (int j = 0; j< 6; j++) {
                    textureArrays[j] = AssetsAndResource.getFixedTexture(AssetsAndResource.cardBorderID);
                }
                textureArrays[2] = AssetsAndResource.getFixedTexture(AssetsAndResource.cardBacksideID);
                textureArrays[3] = AssetsAndResource.getCardTexture(card.getNameID()/*"AquaHulcus"*/);
                DrawObjectHelper.drawOneCube(glcard, textureArrays, shadowEnable);
            }

            for (int i = 0; i <= limit; i++) {
                widgetPosition = null;
                Cards card = cardStack.get(i);
                widgetPosition = cardWidgetPositionTable.get(card);

                if (widgetPosition == null) {
                    throw new RuntimeException("widgetPosition cannot be null");
                }

                MatrixHelper.setTranslateRotateScale(widgetPosition);
                for (int j = 0; j< 6; j++) {
                    textureArrays[j] = AssetsAndResource.getFixedTexture(AssetsAndResource.cardBorderID);
                }
                textureArrays[2] = AssetsAndResource.getFixedTexture(AssetsAndResource.cardBacksideID);
                textureArrays[3] = AssetsAndResource.getCardTexture(card.getNameID()/*"AquaHulcus"*/);
                DrawObjectHelper.drawOneCube(glcard, textureArrays, shadowEnable);
            }
        } else {
            throw new IllegalArgumentException("Can't be in this Mode");
        }

        for (int i = 0; i < cardWidgets.size(); i++) {
            CardWidget widget = cardWidgets.get(i);
            widgetPosition = cardWidgetToWidgetPositionHashtable.get(widget);

            if (widgetPosition == null) {
                throw new RuntimeException("widgetPosition cannot be null");
            }

            widget.setTranslateRotateScale(widgetPosition);
            widget.draw();
        }
    }

    // Draw the widget in Expand mode
    private void drawExpand() {
        ViewNodePosition widgetPosition;
        int selectedCardIndex;

        selectedCardIndex = cardStack.indexOf(SelectedCard);

        for (int i = cardStack.size() -1; i > selectedCardIndex; i--) {
            widgetPosition = null;
            Cards card = cardStack.get(i);
            widgetPosition = cardWidgetPositionTable.get(card);

            if (widgetPosition == null) {
                throw new RuntimeException("widgetPosition cannot be null");
            }
            widgetPosition.X_scale = scale * (39.0f - 0.3f * (float) Math.abs(i - selectedCardIndex))/39.0f;
            widgetPosition.Z_scale = scale * (39.0f - 0.3f * (float) Math.abs(i - selectedCardIndex))/39.0f;

            MatrixHelper.setTranslateRotateScale(widgetPosition);
            DrawObjectHelper.drawHighlightBoundaryOfCard(glCurrentSelect, glSelectedCards, card);
            for (int j = 0; j < 6; j++) {
                textureArrays[j] = AssetsAndResource.getFixedTexture(AssetsAndResource.cardBorderID);
            }
            textureArrays[2] = AssetsAndResource.getFixedTexture(AssetsAndResource.cardBacksideID);
            textureArrays[3] = AssetsAndResource.getCardTexture(card.getNameID()/*"AquaHulcus"*/);
            DrawObjectHelper.drawOneCube(glcard, textureArrays, shadowEnable);
        }

        for (int i = 0; i <= selectedCardIndex; i++) {
            widgetPosition = null;
            Cards card = cardStack.get(i);
            widgetPosition = cardWidgetPositionTable.get(card);

            if (widgetPosition == null) {
                throw new RuntimeException("widgetPosition cannot be null");
            }
            widgetPosition.X_scale = scale * (39.0f - 0.3f * (float) Math.abs(i - selectedCardIndex))/39.0f;
            widgetPosition.Z_scale = scale * (39.0f - 0.3f * (float) Math.abs(i - selectedCardIndex))/39.0f;

            MatrixHelper.setTranslateRotateScale(widgetPosition);
            DrawObjectHelper.drawHighlightBoundaryOfCard(glCurrentSelect, glSelectedCards, card);
            for (int j = 0; j < 6; j++) {
                textureArrays[j] = AssetsAndResource.getFixedTexture(AssetsAndResource.cardBorderID);
            }
            textureArrays[2] = AssetsAndResource.getFixedTexture(AssetsAndResource.cardBacksideID);
            textureArrays[3] = AssetsAndResource.getCardTexture(card.getNameID()/*"AquaHulcus"*/);
            DrawObjectHelper.drawOneCube(glcard, textureArrays, shadowEnable);
        }
    }

    // Draw the widget in ExpandLock mode
    private void drawExpandLock() {
        ViewNodePosition widgetPosition;
        int selectedCardIndex;

        selectedCardIndex = originalSelectedCardPosition;

        for (int i = cardOriginalList.size() -1; i > selectedCardIndex; i--) {
            widgetPosition = null;
            Object obj = cardOriginalList.get(i);
            if (obj instanceof Cards) {
                Cards card = (Cards) obj;
                widgetPosition = cardWidgetPositionTable.get(card);

                if (widgetPosition == null) {
                    throw new RuntimeException("widgetPosition cannot be null");
                }

                MatrixHelper.setTranslateRotateScale(widgetPosition);
                DrawObjectHelper.drawHighlightBoundaryOfCard(glCurrentSelect, glSelectedCards, card);
                for (int j = 0; j < 6; j++) {
                    textureArrays[j] = AssetsAndResource.getFixedTexture(AssetsAndResource.cardBorderID);
                }
                textureArrays[2] = AssetsAndResource.getFixedTexture(AssetsAndResource.cardBacksideID);
                textureArrays[3] = AssetsAndResource.getCardTexture(card.getNameID()/*"AquaHulcus"*/);
                DrawObjectHelper.drawOneCube(glcard, textureArrays, shadowEnable);
            } else if (obj instanceof CardWidget) {
                CardWidget widget = (CardWidget) obj;
                widgetPosition = cardWidgetToWidgetPositionHashtable.get(widget);

                if (widgetPosition == null) {
                    throw new RuntimeException("widgetPosition cannot be null");
                }

                widget.setTranslateRotateScale(widgetPosition);
                widget.draw();
            } else {
                throw new RuntimeException("Invalid Condition");
            }
        }

        for (int i = 0; i <= selectedCardIndex; i++) {
            widgetPosition = null;
            Object obj = cardOriginalList.get(i);
            if (obj instanceof Cards) {
                Cards card = (Cards) obj;
                widgetPosition = cardWidgetPositionTable.get(card);

                if (widgetPosition == null) {
                    throw new RuntimeException("widgetPosition cannot be null");
                }

                MatrixHelper.setTranslateRotateScale(widgetPosition);
                DrawObjectHelper.drawHighlightBoundaryOfCard(glCurrentSelect, glSelectedCards, card);
                for (int j = 0; j < 6; j++) {
                    textureArrays[j] = AssetsAndResource.getFixedTexture(AssetsAndResource.cardBorderID);
                }
                textureArrays[2] = AssetsAndResource.getFixedTexture(AssetsAndResource.cardBacksideID);
                textureArrays[3] = AssetsAndResource.getCardTexture(card.getNameID()/*"AquaHulcus"*/);
                DrawObjectHelper.drawOneCube(glcard, textureArrays, shadowEnable);
            } else if (obj instanceof CardWidget) {
                CardWidget widget = (CardWidget) obj;
                widgetPosition = cardWidgetToWidgetPositionHashtable.get(widget);

                if (widgetPosition == null) {
                    throw new RuntimeException("widgetPosition cannot be null");
                }

                widget.setTranslateRotateScale(widgetPosition);
                widget.draw();
            } else {
                throw new RuntimeException("Invalid Condition");
            }
        }
    }

    @Override
    public WidgetTouchEvent isTouched(List<Input.TouchEvent> touchEvents) {
        WidgetTouchEvent widgetTouchEvent = null;
        if (cardStack.size() == 0) {
            return null;
        }

        if (mode == WidgetMode.Normal) {
            widgetTouchEvent = isTouchedForNormalMode(touchEvents);
        }

        if (mode == WidgetMode.Transition || mode == WidgetMode.ExpandLock) {
            widgetTouchEvent = isTouchedForTransitionAndExpandLock(touchEvents);
        }

        if (mode == WidgetMode.Expand) {
            widgetTouchEvent = isTouchedForExpandMode(touchEvents);
        }

        return widgetTouchEvent;
    }

    private WidgetTouchEvent isTouchedForNormalMode(List<Input.TouchEvent> touchEvents) {
        WidgetTouchEvent widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
        widgetTouchEvent.resetTouchEvent();

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
                if (input.TouchType(0) == Input.TouchEvent.TOUCH_DRAGGED) {
                    widgetTouchEvent.isMoving = true;
                }
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
                if (input.TouchType(0) == Input.TouchEvent.TOUCH_DRAGGED) {
                    widgetTouchEvent.isMoving = true;
                }
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
                if (input.TouchType(0) == Input.TouchEvent.TOUCH_DRAGGED) {
                    widgetTouchEvent.isMoving = true;
                }
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
                    if (input.TouchType(0) == Input.TouchEvent.TOUCH_DRAGGED) {
                        widgetTouchEvent.isMoving = true;
                    }
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
                    if (input.TouchType(0) == Input.TouchEvent.TOUCH_DRAGGED) {
                        widgetTouchEvent.isMoving = true;
                    }
                    widgetTouchEvent.object = cardStack;
                    return widgetTouchEvent;
                }
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
                            new GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)), (cube.length * Position.Y_scale) / 2);

                    float width = Math.abs(intersectingPoint.x);
                    float height = Math.abs(intersectingPoint.z);

                    if (width <= (cube.width * Position.X_scale) / 2 && height <= (cube.height * Position.Z_scale) / 2) {
                        widgetTouchEvent.isTouched = true;
                        widgetTouchEvent.object = cardStack;
                        touchCount++;
                        continue;
                    }

                    intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                            new GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)), -(cube.length * Position.Y_scale) / 2);

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
                                new GLPlane(new GLPoint(0, 0, (cube.height * Position.Z_scale) / 2), new GLVector(0, 0, 1)));

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
                                new GLPlane(new GLPoint(0, 0, -(cube.height * Position.Z_scale) / 2), new GLVector(0, 0, -1)));

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
        }

        return widgetTouchEvent;
    }

    private WidgetTouchEvent isTouchedForTransitionAndExpandLock(List<Input.TouchEvent> touchEvents) {
        WidgetTouchEvent widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
        widgetTouchEvent.resetTouchEvent();

        return widgetTouchEvent;
    }

    private WidgetTouchEvent isTouchedForExpandMode(List<Input.TouchEvent> touchEvents) {
        WidgetTouchEvent widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
        widgetTouchEvent.resetTouchEvent();

        indexTouched.clear();
        int touchCount = 0;

        float gaps = (0.8f * AssetsAndResource.MazeHeight)/((float) cardStack.size());

        if (gaps > glcard.width/2.0f) {
            gaps = glcard.width/2.0f;
        }

        int midPoint = cardStack.size()/2 + cardStack.size()%2 -1;

        setIdentityM(AssetsAndResource.tempMatrix, 0);
        if (angle != 0) {
            rotateM(AssetsAndResource.tempMatrix, 0, angle, -rotationDir_x, -rotationDir_y, -rotationDir_z);
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
                    ViewNodePosition widgetPosition = cardWidgetPositionTable.get(cardStack.get(i));

                    if (widgetPosition != null) {
                        width = Math.abs(intersectingPoint.x - (widgetPosition.Centerposition.x - AssetsAndResource.CameraPosition.x/4));
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
                    ViewNodePosition widgetPosition = cardWidgetPositionTable.get(cardStack.get(i));

                    if (widgetPosition != null) {
                        width = Math.abs(intersectingPoint.x - (widgetPosition.Centerposition.x - AssetsAndResource.CameraPosition.x/4));
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
                    if (input.TouchType(0) == Input.TouchEvent.TOUCH_DRAGGED) {
                        widgetTouchEvent.isMoving = true;
                    }
                    if (index < indexTouched.get(0)) {
                        widgetTouchEvent.object = cardStack.get(indexTouched.get(0));
                        widgetTouchEvent.wasUnderTheStack = true;
                        SelectedCard = (Cards) widgetTouchEvent.object;
                    } else if (index > indexTouched.get(indexTouched.size() -1)) {
                        widgetTouchEvent.object = cardStack.get(indexTouched.get(indexTouched.size() -1));
                        widgetTouchEvent.wasUnderTheStack = true;
                        SelectedCard = (Cards) widgetTouchEvent.object;
                    } else {
                        widgetTouchEvent.object = SelectedCard;
                    }

                    return widgetTouchEvent;
                }
            }
        } else {
            ArrayList<Cards> temporaryCardList = new ArrayList<Cards>();
            for (int i = 0; i < touchEvents.size(); i++) {
                Input.TouchEvent event = touchEvents.get(i);
                indexTouched.clear();
                if (event.type == Input.TouchEvent.TOUCH_UP) {
                    GLPoint relativeNearPointAfterTrans = event.nearPoint[0].translate(AssetsAndResource.CameraPosition.getVector().scale(-1 / 4.0f));
                    GLPoint relativeFarPointAfterTrans = event.farPoint[0].translate(AssetsAndResource.CameraPosition.getVector().scale(-1 / 4.0f));

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

                    if (width <= (((midPoint + 2) * gaps) + (glcard.width * scale) / 2.0f) &&
                            height <= (glcard.height * scale) / 2.0f) {
                        x = (intersectingPoint.x + midPoint * gaps) / gaps;
                        if (x > cardStack.size()) {
                            index = cardStack.size() - 1;
                        } else if (x > 0) {
                            index = (int) Math.floor(x);
                        } else {
                            index = 0;
                        }

                        for (int j = index; j >= 0; j--) {
                            ViewNodePosition widgetPosition = cardWidgetPositionTable.get(cardStack.get(j));

                            if (widgetPosition != null) {
                                width = Math.abs(intersectingPoint.x - (widgetPosition.Centerposition.x - AssetsAndResource.CameraPosition.x / 4));
                                if (width <= (glcard.width * widgetPosition.X_scale) / 2 && height <= (glcard.height * widgetPosition.Z_scale) / 2) {
                                    indexTouched.add(0, new Integer(j));
                                } else {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }

                        for (int j = index + 1; j < cardStack.size(); j++) {
                            ViewNodePosition widgetPosition = cardWidgetPositionTable.get(cardStack.get(j));

                            if (widgetPosition != null) {
                                width = Math.abs(intersectingPoint.x - (widgetPosition.Centerposition.x - AssetsAndResource.CameraPosition.x / 4));
                                if (width <= (glcard.width * widgetPosition.X_scale) / 2 && height <= (glcard.height * widgetPosition.Z_scale) / 2) {
                                    indexTouched.add(new Integer(j));
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
                                widgetTouchEvent.object = cardStack.get(indexTouched.get(0));
                                widgetTouchEvent.wasUnderTheStack = true;
                                SelectedCard = (Cards) widgetTouchEvent.object;
                            } else if (index > indexTouched.get(indexTouched.size() - 1)) {
                                temporaryCardList.add(cardStack.get(indexTouched.get(indexTouched.size() - 1)));
                                widgetTouchEvent.object = cardStack.get(indexTouched.get(indexTouched.size() - 1));
                                widgetTouchEvent.wasUnderTheStack = true;
                                SelectedCard = (Cards) widgetTouchEvent.object;
                            } else {
                                temporaryCardList.add(SelectedCard);
                                widgetTouchEvent.object = SelectedCard;
                            }
                        }
                    }
                }
            }

            if (widgetTouchEvent.isTouched && touchCount > 1) {
                int size = temporaryCardList.size();
                if (temporaryCardList.get(size - 1) == temporaryCardList.get(size - 2)) {
                    widgetTouchEvent.isDoubleTouched = true;
                    widgetTouchEvent.object = temporaryCardList.get(size - 1);
                    SelectedCard = (Cards) widgetTouchEvent.object;
                }
            }
        }

        return widgetTouchEvent;
    }

    @Override
    public void setTranslateRotateScale(ViewNodePosition position) {
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
        cube = (Cube) objs[0];
        glcard = (Cube) objs[1];
        glCurrentSelect = (Cube) objs[2];
        glSelectedCards = (Cube) objs[3];
    }

    @Override
    public void LinkLogicalObject(Object obj) {
        cardStack = (ArrayList<Cards>) obj;
    }

    @Override
    public Object getLogicalObject() {
        return cardStack;
    }

    @Override
    public void setMode(WidgetMode mode) {
        if (this.mode != mode) {
            this.mode = WidgetMode.Transition;
        }
    }

    @Override
    public ViewNodePosition getPosition() {
        return Position;
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
    }

    public boolean getFlip() {
        return flip;
    }
}
