package koustav.duelmasters.main.androidgameduelmasterwidgetmodels;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.WidgetPosition;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameopenglmotionmodel.GLKinematic;
import koustav.duelmasters.main.androidgameopenglmotionmodel.GLReferenceTracking;
import koustav.duelmasters.main.androidgameopenglobjectmodels.Cube;
import koustav.duelmasters.main.androidgameopenglobjectmodels.XZRectangle;
import koustav.duelmasters.main.androidgameopenglutil.DrawObjectHelper;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgameopenglutil.MatrixHelper;
import koustav.duelmasters.main.androidgamesframework.Input;

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
    WidgetPosition Position;
    float[] relativeNearPointAfterRot;
    float[] relativeFarPointAfterRot;
    GLPoint relativeNearPoint;
    GLPoint relativeFarPoint;
    WidgetMode mode;
    WidgetMode previousMode;
    Hashtable<Cards, WidgetPosition> cardWidgetPositionTable;
    Hashtable<Integer, Float> ref_center_x, ref_center_y, ref_center_z, ref_angle_x, ref_angle_y, ref_angle_z, ref_scale_x, ref_scale_z;
    Hashtable<Integer, Float> init_center_x, init_center_y, init_center_z, init_angle_x, init_angle_y, init_angle_z, init_scale_x, init_scale_z;
    Cards SelectedCard;
    ArrayList<Cards> pickedCardsFromTheList;
    ArrayList<Integer> indexTouched;
    float scale;

    // Stack orientation
    boolean flip;

    // Motion model
    GLReferenceTracking referenceTracking;

    // shadow enable;
    boolean shadowEnable;

    // Texture Array
    int[] textureArrays;

    // openGL object model, physical unit
    public Cube cube;
    public Cube glcard;

    // Logical object
    ArrayList<Cards> cardStack;

    public CardStackWidget() {
        S = TransitionState.START;

        Position = new WidgetPosition();
        relativeNearPointAfterRot = new float[4];
        relativeFarPointAfterRot = new float[4];
        relativeNearPoint = new GLPoint(0, 0, 0);
        relativeFarPoint = new GLPoint(0, 0, 0);
        mode = WidgetMode.Normal;
        previousMode = WidgetMode.Normal;
        cardWidgetPositionTable = new Hashtable<Cards, WidgetPosition>();
        ref_center_x = new Hashtable<Integer, Float>();
        ref_center_y = new Hashtable<Integer, Float>();
        ref_center_z = new Hashtable<Integer, Float>();
        ref_angle_x = new Hashtable<Integer, Float>();
        ref_angle_y = new Hashtable<Integer, Float>();
        ref_angle_z = new Hashtable<Integer, Float>();
        ref_scale_x = new Hashtable<Integer, Float>();
        ref_scale_z = new Hashtable<Integer, Float>();
        init_center_x = new Hashtable<Integer, Float>();
        init_center_y = new Hashtable<Integer, Float>();
        init_center_z = new Hashtable<Integer, Float>();
        init_angle_x = new Hashtable<Integer, Float>();
        init_angle_y = new Hashtable<Integer, Float>();
        init_angle_z = new Hashtable<Integer, Float>();
        init_scale_x = new Hashtable<Integer, Float>();
        init_scale_z = new Hashtable<Integer, Float>();

        SelectedCard = null;
        pickedCardsFromTheList = new ArrayList<Cards>();
        indexTouched = new ArrayList<Integer>();
        scale = 2.0f;

        flip = false;
        referenceTracking = new GLReferenceTracking();

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
        Cards card = flip ? cardStack.get(cardStack.size() - 1) : cardStack.get(0);
        textureArrays[2] = flip ? AssetsAndResource.getCardTexture(/*card.getNameID()*/"AquaHulcus") : AssetsAndResource.cardBackside;
        textureArrays[3] = flip ? AssetsAndResource.cardBackside : AssetsAndResource.getCardTexture(/*card.getNameID()*/"AquaHulcus");

        DrawObjectHelper.drawOneCube(cube, textureArrays, shadowEnable);
    }

    // Draw the widget in transition mode
    private void drawTransition(float deltaTime, float totalTime) {
        WidgetPosition widgetPosition;

        float ref_track;
        float gaps = (0.8f * AssetsAndResource.MazeHeight)/((float) cardStack.size());

        GLVector vector = new GLVector(0f,0f,0f);
        if (gaps > glcard.width/2.0f) {
            gaps = glcard.width/2.0f;
        }

        int midPoint = cardStack.size()/2 + cardStack.size()%2 -1;

        if (previousMode == WidgetMode.Normal) {
            int moving = 0;
            if (S == TransitionState.START) {
                cardWidgetPositionTable.clear();
                referenceTracking.clearRefTracking();
                ClearTransitionVar();
                S = TransitionState.RUNNING;
            }

            if (S == TransitionState.RUNNING) {
                int count = 0;
                for (int i = 0; i < cardStack.size(); i++) {
                    moving++;
                    Cards card = cardStack.get(i);
                    widgetPosition = null;
                    widgetPosition = cardWidgetPositionTable.get(card);

                    if (widgetPosition == null) {
                        widgetPosition = new WidgetPosition();
                        widgetPosition.Y_scale = 1f;
                        cardWidgetPositionTable.put(card, widgetPosition);
                        referenceTracking.addRefTracking(i, 2f, 2f, totalTime);
                        ref_center_x.put(i, (AssetsAndResource.CameraPosition.x / 4.0f + ((float) (i - midPoint)) * gaps));
                        ref_center_y.put(i,(AssetsAndResource.CameraPosition.y / 4.0f));
                        ref_center_z.put(i, (AssetsAndResource.CameraPosition.z / 4.0f));
                        ref_angle_x.put(i, (AssetsAndResource.CameraAngle));
                        ref_angle_y.put(i, 0f);
                        ref_angle_z.put(i, 0f);
                        ref_scale_x.put(i, (scale * ((39.0f - (0.3f * (float) i))/39.0f)));
                        ref_scale_z.put(i, (scale * ((39.0f - (0.3f * (float) i))/39.0f)));

                        ArrayList<GLAngularRotaion> rotaions = new ArrayList<GLAngularRotaion>();
                        GLAngularRotaion rotaion;
                        if (flip) {
                            rotaion = new GLAngularRotaion(180f, 0, 0, 1f);
                            rotaions.add(rotaion);
                        }
                        rotaions.add(Position.rotaion);
                        rotaion = MatrixHelper.getCombinedRotation(rotaions);

                        init_center_x.put(i, Position.Centerposition.x);
                        if (cardStack.size() > 0) {
                            init_center_y.put(i, (Position.Centerposition.y -
                                    ((float) ((i) / cardStack.size())) * cube.length * Position.Y_scale * 0.5f +
                                    0.5f * cube.length * Position.Y_scale * ((float) (Math.abs(cardStack.size() - i)) / cardStack.size())));
                        } else {
                            init_center_y.put(i, Position.Centerposition.y);
                        }
                        init_center_z.put(i, Position.Centerposition.z);
                        init_angle_x.put(i, (rotaion.angle * rotaion.x));
                        init_angle_y.put(i, (rotaion.angle * rotaion.y));
                        init_angle_z.put(i, (rotaion.angle * rotaion.z));
                        init_scale_x.put(i, (Position.X_scale));
                        init_scale_z.put(i, Position.Z_scale);
                    }

                    ref_track = referenceTracking.getRefTrackingOutCome(i, totalTime);

                    if (ref_track >= 0.99f && ref_track <=1.0f) {
                        ref_track = 1.0f;
                        count++;
                    }

                    widgetPosition.Centerposition.x = (1.0f - ref_track) * init_center_x.get(i) + ref_track * ref_center_x.get(i);
                    widgetPosition.Centerposition.z = (1.0f - ref_track) * init_center_z.get(i) + ref_track * ref_center_z.get(i);
                    widgetPosition.Centerposition.y = (1.0f - ref_track) * init_center_y.get(i) + ref_track * ref_center_y.get(i);

                    vector.x = (1.0f - ref_track) * init_angle_x.get(i) + ref_track * ref_angle_x.get(i);
                    vector.y = (1.0f - ref_track) * init_angle_y.get(i) + ref_track * ref_angle_y.get(i);
                    vector.z = (1.0f - ref_track) * init_angle_z.get(i) + ref_track * ref_angle_z.get(i);
                    widgetPosition.rotaion.angle = vector.getMagnitude();

                    GLVector dir = vector.getDirection();
                    widgetPosition.rotaion.x = dir.x;
                    widgetPosition.rotaion.y = dir.y;
                    widgetPosition.rotaion.z = dir.z;
                    widgetPosition.X_scale = (1.0f - ref_track) * init_scale_x.get(i) + ref_track * ref_scale_x.get(i);
                    widgetPosition.Z_scale = (1.0f - ref_track) * init_scale_z.get(i) + ref_track * ref_scale_z.get(i);

                    if ((i <cardStack.size() - 1) && cardWidgetPositionTable.get(cardStack.get(i + 1)) == null && ref_track <= 0.1) {
                        break;
                    }
                }

                if (count == cardStack.size()) {
                    S = TransitionState.END;
                }
            }

            if (S == TransitionState.END) {
                referenceTracking.clearRefTracking();
                ClearTransitionVar();
                S = TransitionState.START;
                mode = WidgetMode.Expand;
                previousMode = WidgetMode.Expand;
                if (cardStack != null && cardStack.size() > 0) {
                    SelectedCard = cardStack.get(0);
                }
            }

            if (cardStack.size() - moving > 2) {
                widgetPosition = new WidgetPosition();
                widgetPosition.rotaion.angle = Position.rotaion.angle;
                widgetPosition.rotaion.x = Position.rotaion.x;
                widgetPosition.rotaion.y = Position.rotaion.y;
                widgetPosition.rotaion.z = Position.rotaion.z;
                widgetPosition.Centerposition.x = Position.Centerposition.x;
                if (cardStack.size() > 0) {
                    widgetPosition.Centerposition.y = Position.Centerposition.y -
                            ((float) (moving / cardStack.size())) * cube.length * Position.Y_scale * 0.5f;
                } else {
                    widgetPosition.Centerposition.y = Position.Centerposition.y;
                }
                widgetPosition.Centerposition.z = Position.Centerposition.z;
                widgetPosition.X_scale = Position.X_scale;
                widgetPosition.Y_scale = Position.Y_scale * ((float) (Math.abs(cardStack.size() - moving)) / cardStack.size());
                widgetPosition.Z_scale = Position.Z_scale;

                for (int i = 0; i< 6; i++) {
                    textureArrays[i] = AssetsAndResource.cardDeckSides;
                }
                Cards card = flip ? cardStack.get(cardStack.size() - 1) : cardStack.get(moving);
                textureArrays[2] = flip ? AssetsAndResource.getCardTexture(/*card.getNameID()*/"AquaHulcus") : AssetsAndResource.cardBackside;
                textureArrays[3] = flip ? AssetsAndResource.cardBackside : AssetsAndResource.getCardTexture(/*card.getNameID()*/"AquaHulcus");

                MatrixHelper.setTranslateRotateScale(widgetPosition);
                DrawObjectHelper.drawOneCube(cube, textureArrays, shadowEnable);

            } else if (cardStack.size() - moving > 0) {
                widgetPosition = new WidgetPosition();
                widgetPosition.rotaion.angle = Position.rotaion.angle;
                widgetPosition.rotaion.x = Position.rotaion.x;
                widgetPosition.rotaion.y = Position.rotaion.y;
                widgetPosition.rotaion.z = Position.rotaion.z;
                widgetPosition.Centerposition.x = Position.Centerposition.x;
                if (cardStack.size() > 0) {
                    widgetPosition.Centerposition.y = Position.Centerposition.y -
                            ((float) ((cardStack.size() - 3) / cardStack.size())) * cube.length * Position.Y_scale * 0.5f;
                } else {
                    widgetPosition.Centerposition.y = Position.Centerposition.y;
                }
                widgetPosition.Centerposition.z = Position.Centerposition.z;
                widgetPosition.X_scale = Position.X_scale;
                widgetPosition.Y_scale = Position.Y_scale * (3.0f / cardStack.size());
                widgetPosition.Z_scale = Position.Z_scale;

                for (int i = 0; i< 6; i++) {
                    textureArrays[i] = AssetsAndResource.cardDeckSides;
                }
                Cards card = flip ? cardStack.get(cardStack.size() - 1) : cardStack.get(moving);
                textureArrays[2] = flip ? AssetsAndResource.getCardTexture(/*card.getNameID()*/"AquaHulcus") : AssetsAndResource.cardBackside;
                textureArrays[3] = flip ? AssetsAndResource.cardBackside : AssetsAndResource.getCardTexture(/*card.getNameID()*/"AquaHulcus");

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
                    textureArrays[j] = AssetsAndResource.cardBorder;
                }
                textureArrays[2] = AssetsAndResource.cardBackside;
                textureArrays[3] = AssetsAndResource.getCardTexture(/*card.getNameID()*/"AquaHulcus");
                DrawObjectHelper.drawOneCube(glcard, textureArrays, shadowEnable);
            }

        } else if (previousMode == WidgetMode.Expand) {
            if (S == TransitionState.START) {
                referenceTracking.clearRefTracking();
                ClearTransitionVar();
                S = TransitionState.RUNNING;
            }

            int count = 0;
            if (S == TransitionState.RUNNING) {
                for (int i = cardStack.size() - 1; i >= 0; i--) {
                    widgetPosition = null;
                    Cards card = cardStack.get(i);
                    widgetPosition = cardWidgetPositionTable.get(card);

                    if (widgetPosition == null) {
                        throw new RuntimeException("widgetPosition cannot be null");
                    }

                    if (ref_center_x.get(i) == null) {
                        referenceTracking.addRefTracking(i, 2f, 2f, totalTime);

                        ArrayList<GLAngularRotaion> rotaions = new ArrayList<GLAngularRotaion>();
                        GLAngularRotaion rotaion;
                        if (flip) {
                            rotaion = new GLAngularRotaion(180f, 0, 0, 1f);
                            rotaions.add(rotaion);
                        }
                        rotaions.add(Position.rotaion);
                        rotaion = MatrixHelper.getCombinedRotation(rotaions);

                        ref_center_x.put(i, Position.Centerposition.x);
                        if (cardStack.size() > 0) {
                            ref_center_y.put(i, (Position.Centerposition.y -
                                    ((float) ((i) / cardStack.size())) * cube.length * Position.Y_scale * 0.5f +
                                    0.5f * cube.length * Position.Y_scale * ((float) (Math.abs(cardStack.size() - i)) / cardStack.size())));
                        } else {
                            ref_center_y.put(i,Position.Centerposition.y);
                        }
                        ref_center_z.put(i, Position.Centerposition.z);
                        ref_angle_x.put(i, (rotaion.angle * rotaion.x));
                        ref_angle_y.put(i, (rotaion.angle * rotaion.y));
                        ref_angle_z.put(i, (rotaion.angle * rotaion.z));
                        ref_scale_x.put(i, Position.X_scale);
                        ref_scale_z.put(i, Position.Z_scale);

                        init_center_x.put(i, widgetPosition.Centerposition.x);
                        init_center_y.put(i, widgetPosition.Centerposition.y);
                        init_center_z.put(i, widgetPosition.Centerposition.z);
                        init_angle_x.put(i, (widgetPosition.rotaion.angle * widgetPosition.rotaion.x));
                        init_angle_y.put(i, (widgetPosition.rotaion.angle * widgetPosition.rotaion.y));
                        init_angle_z.put(i, (widgetPosition.rotaion.angle * widgetPosition.rotaion.z));
                        init_scale_x.put(i, widgetPosition.X_scale);
                        init_scale_z.put(i, widgetPosition.Z_scale);
                    }



                    ref_track = referenceTracking.getRefTrackingOutCome(i, totalTime);

                    if (ref_track >= 0.99f && ref_track <=1.0f) {
                        ref_track = 1.0f;
                        count++;
                    }

                    widgetPosition.Centerposition.x = (1.0f - ref_track) * init_center_x.get(i) + ref_track * ref_center_x.get(i);
                    widgetPosition.Centerposition.z = (1.0f - ref_track) * init_center_z.get(i) + ref_track * ref_center_z.get(i);
                    widgetPosition.Centerposition.y = (1.0f - ref_track) * init_center_y.get(i) + ref_track * ref_center_y.get(i);

                    vector.x = (1.0f - ref_track) * init_angle_x.get(i) + ref_track * ref_angle_x.get(i);
                    vector.y = (1.0f - ref_track) * init_angle_y.get(i) + ref_track * ref_angle_y.get(i);
                    vector.z = (1.0f - ref_track) * init_angle_z.get(i) + ref_track * ref_angle_z.get(i);
                    widgetPosition.rotaion.angle = vector.getMagnitude();

                    GLVector dir = vector.getDirection();
                    widgetPosition.rotaion.x = dir.x;
                    widgetPosition.rotaion.y = dir.y;
                    widgetPosition.rotaion.z = dir.z;
                    widgetPosition.X_scale = (1.0f - ref_track) * init_scale_x.get(i) + ref_track * ref_scale_x.get(i);
                    widgetPosition.Z_scale = (1.0f - ref_track) * init_scale_z.get(i) + ref_track * ref_scale_z.get(i);

                    if (i > 0 && (ref_center_x.get(cardStack.get(i - 1)) == null) && ref_track <= 0.1) {
                        break;
                    }
                }

                if (count == cardStack.size()) {
                    S = TransitionState.END;
                }
            }

            if (S == TransitionState.END) {
                referenceTracking.clearRefTracking();
                S = TransitionState.START;
                mode = WidgetMode.Normal;
                previousMode = WidgetMode.Normal;
                SelectedCard = null;

                cardWidgetPositionTable.clear();
                ClearTransitionVar();
            }

            if (count > 2) {
                widgetPosition = new WidgetPosition();
                widgetPosition.rotaion.angle = Position.rotaion.angle;
                widgetPosition.rotaion.x = Position.rotaion.x;
                widgetPosition.rotaion.y = Position.rotaion.y;
                widgetPosition.rotaion.z = Position.rotaion.z;
                widgetPosition.Centerposition.x = Position.Centerposition.x;
                if (cardStack.size() > 0) {
                    widgetPosition.Centerposition.y = Position.Centerposition.y -
                            ((float) ((cardStack.size() - count) / cardStack.size())) * cube.length * Position.Y_scale * 0.5f;
                } else {
                    widgetPosition.Centerposition.y = Position.Centerposition.y;
                }
                widgetPosition.Centerposition.z = Position.Centerposition.z;
                widgetPosition.X_scale = Position.X_scale;
                widgetPosition.Y_scale = Position.Y_scale * ((float) (count) / cardStack.size());
                widgetPosition.Z_scale = Position.Z_scale;

                for (int i = 0; i< 6; i++) {
                    textureArrays[i] = AssetsAndResource.cardDeckSides;
                }
                Cards card = flip ? cardStack.get(cardStack.size() - 1) : cardStack.get(cardStack.size() - count);
                textureArrays[2] = flip ? AssetsAndResource.getCardTexture(/*card.getNameID()*/"AquaHulcus") : AssetsAndResource.cardBackside;
                textureArrays[3] = flip ? AssetsAndResource.cardBackside : AssetsAndResource.getCardTexture(/*card.getNameID()*/"AquaHulcus");

                MatrixHelper.setTranslateRotateScale(widgetPosition);
                DrawObjectHelper.drawOneCube(cube, textureArrays, shadowEnable);

            } else if (count > 0) {
                widgetPosition = new WidgetPosition();
                widgetPosition.rotaion.angle = Position.rotaion.angle;
                widgetPosition.rotaion.x = Position.rotaion.x;
                widgetPosition.rotaion.y = Position.rotaion.y;
                widgetPosition.rotaion.z = Position.rotaion.z;
                widgetPosition.Centerposition.x = Position.Centerposition.x;
                if (cardStack.size() > 0) {
                    widgetPosition.Centerposition.y = Position.Centerposition.y -
                            ((float) ((cardStack.size() - 3) / cardStack.size())) * cube.length * Position.Y_scale * 0.5f;
                } else {
                    widgetPosition.Centerposition.y = Position.Centerposition.y;
                }
                widgetPosition.Centerposition.z = Position.Centerposition.z;
                widgetPosition.X_scale = Position.X_scale;
                widgetPosition.Y_scale = Position.Y_scale * (3.0f / cardStack.size());
                widgetPosition.Z_scale = Position.Z_scale;

                for (int i = 0; i< 6; i++) {
                    textureArrays[i] = AssetsAndResource.cardDeckSides;
                }
                Cards card = flip ? cardStack.get(cardStack.size() - 1) : cardStack.get(cardStack.size() - count);
                textureArrays[2] = flip ? AssetsAndResource.getCardTexture(/*card.getNameID()*/"AquaHulcus") : AssetsAndResource.cardBackside;
                textureArrays[3] = flip ? AssetsAndResource.cardBackside : AssetsAndResource.getCardTexture(/*card.getNameID()*/"AquaHulcus");

                MatrixHelper.setTranslateRotateScale(widgetPosition);
                DrawObjectHelper.drawOneCube(cube, textureArrays, shadowEnable);

            }

            int selectedCardIndex = cardStack.indexOf(SelectedCard);
            int limit;
            if (selectedCardIndex > (cardStack.size() - count -1)) {
                limit = cardStack.size() - count -1;
            } else {
                limit = selectedCardIndex;
            }

            for (int i = cardStack.size() -1 - count; i > selectedCardIndex; i--) {
                widgetPosition = null;
                Cards card = cardStack.get(i);
                widgetPosition = cardWidgetPositionTable.get(card);

                if (widgetPosition == null) {
                    throw new RuntimeException("widgetPosition cannot be null");
                }

                MatrixHelper.setTranslateRotateScale(widgetPosition);
                for (int j = 0; j< 6; j++) {
                    textureArrays[j] = AssetsAndResource.cardBorder;
                }
                textureArrays[2] = AssetsAndResource.cardBackside;
                textureArrays[3] = AssetsAndResource.getCardTexture(/*card.getNameID()*/"AquaHulcus");
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
                    textureArrays[j] = AssetsAndResource.cardBorder;
                }
                textureArrays[2] = AssetsAndResource.cardBackside;
                textureArrays[3] = AssetsAndResource.getCardTexture(/*card.getNameID()*/"AquaHulcus");
                DrawObjectHelper.drawOneCube(glcard, textureArrays, shadowEnable);
            }
        } else {
            throw new IllegalArgumentException("Can't be in this Mode");
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
            Cards card = cardStack.get(i);
            widgetPosition = cardWidgetPositionTable.get(card);

            if (widgetPosition == null) {
                throw new RuntimeException("widgetPosition cannot be null");
            }
            widgetPosition.X_scale = scale * (39.0f - 0.3f * (float) Math.abs(i - selectedCardIndex))/39.0f;
            widgetPosition.Z_scale = scale * (39.0f - 0.3f * (float) Math.abs(i - selectedCardIndex))/39.0f;

            MatrixHelper.setTranslateRotateScale(widgetPosition);
            for (int j = 0; j < 6; j++) {
                textureArrays[j] = AssetsAndResource.cardBorder;
            }
            textureArrays[2] = AssetsAndResource.cardBackside;
            textureArrays[3] = AssetsAndResource.getCardTexture(/*card.getNameID()*/"AquaHulcus");
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
            for (int j = 0; j < 6; j++) {
                textureArrays[j] = AssetsAndResource.cardBorder;
            }
            textureArrays[2] = AssetsAndResource.cardBackside;
            textureArrays[3] = AssetsAndResource.getCardTexture(/*card.getNameID()*/"AquaHulcus");
            DrawObjectHelper.drawOneCube(glcard, textureArrays, shadowEnable);
        }
    }

    @Override
    public WidgetTouchEvent isTouched(List<Input.TouchEvent> touchEvents) {
        WidgetTouchEvent widgetTouchEvent = null;
        if (mode == WidgetMode.Normal) {
            widgetTouchEvent = isTouchedForNormalMode(touchEvents);
        }

        if (mode == WidgetMode.Transition) {
            widgetTouchEvent = isTouchedForTransition(touchEvents);
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

    private WidgetTouchEvent isTouchedForTransition(List<Input.TouchEvent> touchEvents) {
        WidgetTouchEvent widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
        widgetTouchEvent.isTouched = false;
        widgetTouchEvent.isTouchedDown = false;
        widgetTouchEvent.isDoubleTouched = false;
        widgetTouchEvent.object = null;

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
                    WidgetPosition widgetPosition = cardWidgetPositionTable.get(cardStack.get(i));

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
                            width = Math.abs(intersectingPoint.x - (widgetPosition.Centerposition.x - AssetsAndResource.CameraPosition.x/4));
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
        glcard = (Cube) objs[1];
    }

    @Override
    public void LinkLogicalObject(Object obj) {
        cardStack = (ArrayList<Cards>) obj;
    }

    @Override
    public void setMode(WidgetMode mode) {
        if (this.mode != mode) {
            this.mode = WidgetMode.Transition;
        }
    }

    private void ClearTransitionVar() {
        ref_center_x.clear();
        ref_center_y.clear();
        ref_center_z.clear();
        ref_angle_x.clear();
        ref_angle_y.clear();
        ref_angle_z.clear();
        ref_scale_x.clear();
        ref_scale_z.clear();
        init_center_x.clear();
        init_center_y.clear();
        init_center_z.clear();
        init_angle_x.clear();
        init_angle_y.clear();
        init_angle_z.clear();
        init_scale_x.clear();
        init_scale_z.clear();
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
    }
}
