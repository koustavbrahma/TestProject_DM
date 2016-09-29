package koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchFocusLevel;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.Layout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgameopenglutil.UIHelper;
import koustav.duelmasters.main.androidgamesframework.Input;
import koustav.duelmasters.main.androidgamesframework.Pool;

import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;

/**
 * Created by Koustav on 7/11/2016.
 */
public class HandZoneLayout implements Layout  {
    enum TouchModeHandZone {
        NormalMode,
        DragMode,
    }
    TouchModeHandZone touchMode;

    Pool<DynamicCardSlotLayout> cardSlotLayoutPool;

    DynamicCardSlotLayout SelectedCardSlot;
    DynamicCardSlotLayout DraggingSlot;
    ArrayList<DynamicCardSlotLayout> CardSlot;

    Hashtable<CardWidget, DynamicCardSlotLayout> WidgetToSlotMapping;

    float width;
    float normalizedY;
    float perpendicular_x;
    float perpendicular_y;
    float perpendicular_z;
    float k1;
    float k2;

    float center_x;
    float center_y;
    float center_z;
    float angle;
    float rotationDir_x;
    float rotationDir_y;
    float rotationDir_z;
    float gap_vec_x;
    float gap_vec_y;
    float gap_vec_z;
    float clearance_x;
    float clearance_y;
    float clearance_z;

    boolean DragMode;
    boolean LockMode;

    float[] relativeNearPointAfterRot;
    float[] relativeFarPointAfterRot;

    GLGeometry.GLPoint relativeNearPoint;
    GLGeometry.GLPoint relativeFarPoint;
    ArrayList<WidgetTouchEvent> widgetTouchEventList;
    ArrayList<DynamicCardSlotLayout> TouchedSlots;

    public HandZoneLayout() {
        Pool.PoolObjectFactory<DynamicCardSlotLayout> factory = new Pool.PoolObjectFactory<DynamicCardSlotLayout>() {
            @Override
            public DynamicCardSlotLayout createObject() {
                return new DynamicCardSlotLayout();
            }
        };

        cardSlotLayoutPool = new Pool<DynamicCardSlotLayout>(factory, 40);

        touchMode = TouchModeHandZone.NormalMode;
        SelectedCardSlot = null;
        DraggingSlot = null;
        CardSlot = new ArrayList<DynamicCardSlotLayout>();

        WidgetToSlotMapping = new Hashtable<CardWidget, DynamicCardSlotLayout>();
        width = 0;
        normalizedY = 0f;
        perpendicular_x = 0f;
        perpendicular_y = 0f;
        perpendicular_z = 0f;
        k1 = 2f;
        k2 = 2f;

        center_x = 0f;
        center_y = 0f;
        center_z = 0f;
        angle = 0f;
        rotationDir_x = 0f;
        rotationDir_y = 1f;
        rotationDir_z = 0f;
        gap_vec_x = 1f;
        gap_vec_y = 0f;
        gap_vec_z = 0f;

        DragMode = false;
        LockMode = false;

        relativeFarPointAfterRot = new float[4];
        relativeNearPointAfterRot = new float[4];
        relativeNearPoint = new GLGeometry.GLPoint(0, 0, 0);
        relativeFarPoint = new GLGeometry.GLPoint(0, 0, 0);
        widgetTouchEventList = new ArrayList<WidgetTouchEvent>();
        TouchedSlots = new ArrayList<DynamicCardSlotLayout>();
    }

    public void InitializeHandZoneLayout(float width, float normalizedY, float normalFromOrigin_x, float normalFromOrigin_y,
                                         float normalFromOrigin_z, float k1, float k2) {

        this.width = width;
        this.normalizedY = normalizedY;
        this.perpendicular_x = normalFromOrigin_x;
        this.perpendicular_y = normalFromOrigin_y;
        this.perpendicular_z = normalFromOrigin_z;
        this.k1 = k1;
        this.k2 = k2;

        float[] nearPointNdc = {0, normalizedY, -1, 1};
        float[] farPointNdc = {0, normalizedY, 1, 1};

        float[] nearP = new float[4];
        float[] farP = new float[4];
        multiplyMV(
                nearP, 0, AssetsAndResource.invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        multiplyMV(
                farP, 0, AssetsAndResource.invertedViewProjectionMatrix, 0, farPointNdc, 0);
        UIHelper.divideByW(nearP);
        UIHelper.divideByW(farP);

        GLGeometry.GLPoint nearPoint = new GLGeometry.GLPoint(0, 0, 0);
        GLGeometry.GLPoint farPoint = new GLGeometry.GLPoint(0, 0, 0);
        nearPoint.x = nearP[0];
        nearPoint.y = nearP[1];
        nearPoint.z = nearP[2];

        farPoint.x = farP[0];
        farPoint.y = farP[1];
        farPoint.z = farP[2];

        GLGeometry.GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithPlane(
                new GLGeometry.GLRay(new GLGeometry.GLPoint(nearPoint.x, nearPoint.y, nearPoint.z),
                        GLGeometry.GLVectorBetween(nearPoint, farPoint)),
                new GLGeometry.GLPlane(new GLGeometry.GLPoint(perpendicular_x, perpendicular_y, perpendicular_z),
                        new GLGeometry.GLVector(perpendicular_x, perpendicular_y, perpendicular_z)));
        center_x = intersectingPoint.x;
        center_y = intersectingPoint.y;
        center_z = intersectingPoint.z;

        GLGeometry.GLVector rotationAngle = new GLGeometry.GLVector(0, 1f, 0f).crossProduct(new GLGeometry.GLVector(perpendicular_x,
                perpendicular_y, perpendicular_z).getDirection());
        angle = (float) Math.toDegrees(Math.asin(rotationAngle.getMagnitude()));
        GLGeometry.GLVector rotationDir = rotationAngle.getDirection();
        rotationDir_x = rotationDir.x;
        rotationDir_y = rotationDir.y;
        rotationDir_z = rotationDir.z;

        float[] GapVector = new float[4];
        setIdentityM(AssetsAndResource.tempMatrix, 0);
        if (angle != 0) {
            rotateM(AssetsAndResource.tempMatrix, 0, angle, rotationDir_x, rotationDir_y, rotationDir_z);
        }
        multiplyMV(GapVector, 0, AssetsAndResource.tempMatrix, 0, new float[] {1,
                0, 0, 0f}, 0);

        GLGeometry.GLVector gapVec = new GLGeometry.GLVector(GapVector[0], GapVector[1], GapVector[2]).getDirection();
        gap_vec_x = gapVec.x;
        gap_vec_y = gapVec.y;
        gap_vec_z = gapVec.z;

        setIdentityM(AssetsAndResource.tempMatrix, 0);
        if (angle != 0) {
            rotateM(AssetsAndResource.tempMatrix, 0, angle, rotationDir_x, rotationDir_y, rotationDir_z);
        }
        float[] PointAfterRot = new float[4];
        multiplyMV(PointAfterRot, 0, AssetsAndResource.tempMatrix, 0, new float[] {0, 0, -1, 0f}, 0);

        setIdentityM(AssetsAndResource.tempMatrix, 0);
        if (angle != 0) {
            rotateM(AssetsAndResource.tempMatrix, 0, angle, rotationDir_x, rotationDir_y, rotationDir_z);
        }
        float[] PointAfterRot2 = new float[4];
        multiplyMV(PointAfterRot2, 0, AssetsAndResource.tempMatrix, 0, new float[] {0, 1, 0, 0f}, 0);

        clearance_x = center_x + 1.2f * PointAfterRot[0] * AssetsAndResource.CardHeight + 0.1f * PointAfterRot2[0];
        clearance_y = center_y + 1.2f * PointAfterRot[1] * AssetsAndResource.CardHeight + 0.1f * PointAfterRot2[1];
        clearance_z = center_z + 1.2f * PointAfterRot[2] * AssetsAndResource.CardHeight + 0.1f * PointAfterRot2[2];
    }

    public void SetDraggingMode(boolean val) {
        DragMode = val;
    }

    public boolean IsWidgetInTransition(CardWidget widget) {
        DynamicCardSlotLayout slotLayout = WidgetToSlotMapping.get(widget);
        if (slotLayout == null) {
            return false;
        }

        return slotLayout.IsTransition();
    }
    @Override
    public void update(float deltaTime, float totalTime) {
        for (int i = 0; i < CardSlot.size(); i++) {
            DynamicCardSlotLayout slotLayout = CardSlot.get(i);
            slotLayout.update(deltaTime, totalTime);
        }

        if (DraggingSlot != null) {
            Input input = AssetsAndResource.game.getInput();
            GLGeometry.GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithPlane(
                    new GLGeometry.GLRay(new GLGeometry.GLPoint(input.getNearPoint(0).x, input.getNearPoint(0).y, input.getNearPoint(0).z),
                            GLGeometry.GLVectorBetween(input.getNearPoint(0), input.getFarPoint(0))),
                    new GLGeometry.GLPlane(new GLGeometry.GLPoint(perpendicular_x, perpendicular_y, perpendicular_z),
                            new GLGeometry.GLVector(perpendicular_x, perpendicular_y, perpendicular_z)));

            if ((intersectingPoint.y - center_y) > AssetsAndResource.CardHeight/2) {
                DraggingSlot.DragUpdate(0, center_y + AssetsAndResource.CardHeight/2, 0);
            } else {
                DraggingSlot.DragUpdate(perpendicular_x * 1.01f, perpendicular_y * 1.01f, perpendicular_z * 1.01f);
            }
        }
    }

    @Override
    public void draw() {
        int index = CardSlot.size();
        if (SelectedCardSlot != null) {
            index = CardSlot.indexOf(SelectedCardSlot);
        }

        for (int i = 0; i < index; i++) {
            DynamicCardSlotLayout slotLayout =  CardSlot.get(i);
            slotLayout.draw();
        }

        for (int i = CardSlot.size() - 1; i >= index; i--) {
            DynamicCardSlotLayout slotLayout =  CardSlot.get(i);
            slotLayout.draw();
        }
    }

    @Override
    public WidgetTouchEvent TouchResponse(List<Input.TouchEvent> touchEvents) {
        if (LockMode) {
            return null;
        }

        if (touchMode == TouchModeHandZone.NormalMode) {
            return TouchResponseInNormalMode(touchEvents);
        } else if (touchMode == TouchModeHandZone.DragMode) {
            return TouchResponseInDragMode(touchEvents);
        }

        return null;
    }

    private WidgetTouchEvent TouchResponseInDragMode(List<Input.TouchEvent> touchEvents) {
        if (DraggingSlot == null) {
            throw new RuntimeException("Dragging Slot cannot be null");
        }
        WidgetTouchEvent widgetTouchEvent;
        Input input = AssetsAndResource.game.getInput();
        if (input.isTouchDown(0)) {
            widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
            widgetTouchEvent.resetTouchEvent();
            widgetTouchEvent.isTouched = true;
            widgetTouchEvent.isTouchedDown = true;
            if (input.TouchType(0) == Input.TouchEvent.TOUCH_DRAGGED) {
                widgetTouchEvent.isMoving = true;
            } else {
                widgetTouchEvent.isMoving = false;
            }
            widgetTouchEvent.isFocus = WidgetTouchFocusLevel.High;
            widgetTouchEvent.isDoubleTouched = false;
            widgetTouchEvent.object = DraggingSlot.getCardWidget().getLogicalObject();

            return widgetTouchEvent;
        } else {
            touchMode = TouchModeHandZone.NormalMode;
            widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
            widgetTouchEvent.resetTouchEvent();
            widgetTouchEvent.isTouched = true;
            widgetTouchEvent.isTouchedDown = false;
            widgetTouchEvent.isMoving = false;
            widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Low;
            widgetTouchEvent.isDoubleTouched = false;
            widgetTouchEvent.object = DraggingSlot.getCardWidget().getLogicalObject();
            DraggingSlot.SetSlotDisturbed();
            DraggingSlot.setTwoStepTransition(true, clearance_x, clearance_y, clearance_z, gap_vec_x, gap_vec_y, gap_vec_z);
            DraggingSlot = null;

            return widgetTouchEvent;
        }
    }

    private WidgetTouchEvent TouchResponseInNormalMode(List<Input.TouchEvent> touchEvents) {
        if (CardSlot.size() == 0) {
            return null;
        }

        if (SelectedCardSlot == null) {
            throw new RuntimeException("SelectedCardSlot is null and Zone is not empty");
        }

        if (SelectedCardSlot.IsTransition()) {
            return null;
        }

        widgetTouchEventList.clear();
        TouchedSlots.clear();
        WidgetTouchEvent widgetTouchEvent;

        int midPoint = CardSlot.size()/2 + CardSlot.size()%2 -1;

        float gap = this.width / CardSlot.size();

        if (gap >= (3f * AssetsAndResource.CardWidth)/4f) {
            gap = (3f * AssetsAndResource.CardWidth)/4f;
        }

        setIdentityM(AssetsAndResource.tempMatrix, 0);
        if (angle != 0) {
            rotateM(AssetsAndResource.tempMatrix, 0, angle, -rotationDir_x, -rotationDir_y, -rotationDir_z);
        }

        Input input = AssetsAndResource.game.getInput();
        if (input.isTouchDown(0)) {
            GLGeometry.GLPoint relativeNearPointAfterTrans = input.getNearPoint(0).translate(new GLGeometry.GLVector(center_x, center_y, center_z).scale(-1));
            GLGeometry.GLPoint relativeFarPointAfterTrans = input.getFarPoint(0).translate(new GLGeometry.GLVector(center_x, center_y, center_z).scale(-1));

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

            GLGeometry.GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                    new GLGeometry.GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)), 0);

            float width = Math.abs(intersectingPoint.x);
            float height = Math.abs(intersectingPoint.z);

            float x;
            int index;

            if (width <= (((midPoint + 2) * gap) + (AssetsAndResource.CardWidth)/2.0f) &&
                    height <= (AssetsAndResource.CardHeight)/2.0f) {
                x = (intersectingPoint.x + midPoint * gap) / gap;
                if (x > CardSlot.size()) {
                    index = CardSlot.size() - 1;
                } else if (x > 0) {
                    index = (int) Math.floor(x);
                } else {
                    index = 0;
                }

                for (int i = index; i>= 0 ; i--) {
                    DynamicCardSlotLayout slotLayout = CardSlot.get(i);

                    widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                    if (widgetTouchEvent.isTouched) {
                        TouchedSlots.add(0, slotLayout);
                        widgetTouchEventList.add(0, widgetTouchEvent);
                    } else {
                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                        break;
                    }
                }

                for (int i = index + 1; i < CardSlot.size(); i++) {
                    DynamicCardSlotLayout slotLayout = CardSlot.get(i);

                    widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                    if (widgetTouchEvent.isTouched) {
                        TouchedSlots.add(slotLayout);
                        widgetTouchEventList.add(widgetTouchEvent);
                    } else {
                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                        break;
                    }
                }

                if (TouchedSlots.size() > 0) {
                    index = CardSlot.indexOf(SelectedCardSlot);
                    if (index < CardSlot.indexOf(TouchedSlots.get(0))) {
                        SelectedCardSlot = TouchedSlots.get(0);
                        widgetTouchEvent = widgetTouchEventList.remove(0);
                        widgetTouchEvent.wasUnderTheStack = true;

                        for (int i = 0; i < widgetTouchEventList.size(); i++) {
                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                        }

                        if (DragMode) {
                            DraggingSlot = SelectedCardSlot;
                            touchMode = TouchModeHandZone.DragMode;
                            widgetTouchEvent.isFocus = WidgetTouchFocusLevel.High;
                        }

                        widgetTouchEventList.clear();
                        TouchedSlots.clear();
                        return widgetTouchEvent;
                    } else if (index > CardSlot.indexOf(TouchedSlots.get(TouchedSlots.size() -1))) {
                        SelectedCardSlot = TouchedSlots.get(TouchedSlots.size() - 1);
                        widgetTouchEvent = widgetTouchEventList.remove(TouchedSlots.size() - 1);
                        widgetTouchEvent.wasUnderTheStack = true;

                        for (int i = 0; i < widgetTouchEventList.size(); i++) {
                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                        }

                        if (DragMode) {
                            DraggingSlot = SelectedCardSlot;
                            touchMode = TouchModeHandZone.DragMode;
                            widgetTouchEvent.isFocus = WidgetTouchFocusLevel.High;
                        }

                        widgetTouchEventList.clear();
                        TouchedSlots.clear();
                        return widgetTouchEvent;
                    } else {
                        int i = TouchedSlots.indexOf(SelectedCardSlot);
                        widgetTouchEvent = widgetTouchEventList.remove(i);

                        for (i = 0; i < widgetTouchEventList.size(); i++) {
                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                        }

                        if (DragMode) {
                            DraggingSlot = SelectedCardSlot;
                            touchMode = TouchModeHandZone.DragMode;
                            widgetTouchEvent.isFocus = WidgetTouchFocusLevel.High;
                        }

                        widgetTouchEventList.clear();
                        TouchedSlots.clear();
                        return widgetTouchEvent;
                    }
                }
                widgetTouchEventList.clear();
                TouchedSlots.clear();

                widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
                widgetTouchEvent.resetTouchEvent();
                widgetTouchEvent.isTouched = true;
                widgetTouchEvent.isTouchedDown = true;

                return widgetTouchEvent;
            }
        } else {
            boolean isTouched = false;
            WidgetTouchEvent widgetTouchEventOutCome = null;
            Input.TouchEvent event = null;
            for (int j = 0; j < touchEvents.size(); j++) {
                event = touchEvents.get(j);
                if (event.type == Input.TouchEvent.TOUCH_UP) {
                    GLGeometry.GLPoint relativeNearPointAfterTrans = event.nearPoint[0].translate(new GLGeometry.GLVector(center_x, center_y, center_z).scale(-1));
                    GLGeometry.GLPoint relativeFarPointAfterTrans = event.farPoint[0].translate(new GLGeometry.GLVector(center_x, center_y, center_z).scale(-1));

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

                    GLGeometry.GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                            new GLGeometry.GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)), 0);

                    float width = Math.abs(intersectingPoint.x);
                    float height = Math.abs(intersectingPoint.z);

                    float x;
                    int index;

                    if (width <= (((midPoint + 2) * gap) + (AssetsAndResource.CardWidth)/2.0f) &&
                            height <= (AssetsAndResource.CardHeight)/2.0f) {
                        isTouched = true;
                        x = (intersectingPoint.x + midPoint * gap) / gap;
                        if (x > CardSlot.size()) {
                            index = CardSlot.size() - 1;
                        } else if (x > 0) {
                            index = (int) Math.floor(x);
                        } else {
                            index = 0;
                        }

                        for (int i = index; i>= 0 ; i--) {
                            DynamicCardSlotLayout slotLayout = CardSlot.get(i);

                            widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                            if (widgetTouchEvent.isTouched) {
                                TouchedSlots.add(0, slotLayout);
                                widgetTouchEventList.add(0, widgetTouchEvent);
                            } else {
                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                break;
                            }
                        }

                        for (int i = index + 1; i < CardSlot.size(); i++) {
                            DynamicCardSlotLayout slotLayout = CardSlot.get(i);

                            widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                            if (widgetTouchEvent.isTouched) {
                                TouchedSlots.add(slotLayout);
                                widgetTouchEventList.add(widgetTouchEvent);
                            } else {
                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                break;
                            }
                        }

                        if (TouchedSlots.size() > 0) {
                            index = CardSlot.indexOf(SelectedCardSlot);
                            if (index < CardSlot.indexOf(TouchedSlots.get(0))) {
                                SelectedCardSlot = TouchedSlots.get(0);
                                widgetTouchEvent = widgetTouchEventList.remove(0);
                                widgetTouchEvent.wasUnderTheStack = true;

                                for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                }

                                widgetTouchEventList.clear();
                                TouchedSlots.clear();
                                widgetTouchEventOutCome = widgetTouchEvent;
                                break;
                            } else if (index > CardSlot.indexOf(TouchedSlots.get(TouchedSlots.size() -1))) {
                                SelectedCardSlot = TouchedSlots.get(TouchedSlots.size() - 1);
                                widgetTouchEvent = widgetTouchEventList.remove(TouchedSlots.size() - 1);
                                widgetTouchEvent.wasUnderTheStack = true;

                                for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                }

                                widgetTouchEventList.clear();
                                TouchedSlots.clear();
                                widgetTouchEventOutCome = widgetTouchEvent;
                                break;
                            } else {
                                int i = TouchedSlots.indexOf(SelectedCardSlot);
                                widgetTouchEvent = widgetTouchEventList.remove(i);

                                for (i = 0; i < widgetTouchEventList.size(); i++) {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                }

                                widgetTouchEventList.clear();
                                TouchedSlots.clear();
                                widgetTouchEventOutCome = widgetTouchEvent;
                                break;
                            }
                        }
                    }
                }
            }

            widgetTouchEventList.clear();
            TouchedSlots.clear();

            if (widgetTouchEventOutCome != null) {
                return widgetTouchEventOutCome;
            } else if (isTouched) {
                widgetTouchEventOutCome = AssetsAndResource.widgetTouchEventPool.newObject();
                widgetTouchEventOutCome.resetTouchEvent();
                widgetTouchEventOutCome.isTouched = true;

                return widgetTouchEventOutCome;
            }
        }
        return null;
    }

    public void AddCardWidgetToZone(CardWidget widget) {
        DynamicCardSlotLayout slotLayout = cardSlotLayoutPool.newObject();
        slotLayout.initializeSlot(center_x, center_y, center_z, angle, rotationDir_x, rotationDir_y, rotationDir_z,
                widget, 4f, 4f);
        WidgetToSlotMapping.put(widget, slotLayout);

        CardSlot.add(slotLayout);

        if (CardSlot.size() == 1) {
            SelectedCardSlot = slotLayout;
        }

        int midPoint = CardSlot.size()/2 + CardSlot.size()%2 -1;

        float gap = this.width / CardSlot.size();

        if (gap >= (3f * AssetsAndResource.CardWidth)/4f) {
            gap = (3f * AssetsAndResource.CardWidth)/4f;
        }

        for (int i = 0; i < CardSlot.size(); i++) {
            float x = ((center_x) + (((float) (i - midPoint)) * gap * gap_vec_x));
            float y = ((center_y) + (((float) (i - midPoint)) * gap * gap_vec_y));
            float z = ((center_z) + (((float) (i - midPoint)) * gap * gap_vec_z));

            DynamicCardSlotLayout slotLayout1 = CardSlot.get(i);
            slotLayout1.setSlotPosition(x, y, z);
        }
    }

    public CardWidget RemoveCardWidgetFromZone(CardWidget widget) {
        DynamicCardSlotLayout slotLayout = WidgetToSlotMapping.get(widget);
        boolean selectedSlotRemoved = false;

        if (slotLayout == null) {
            return null;
        }

        if (SelectedCardSlot == slotLayout) {
            selectedSlotRemoved = true;
        }

        if (selectedSlotRemoved) {
            int indexofSlot = CardSlot.indexOf(slotLayout);
            if (indexofSlot > 0) {
                SelectedCardSlot = CardSlot.get(indexofSlot - 1);
            } else {
                if (CardSlot.size() > 1) {
                    SelectedCardSlot = CardSlot.get(indexofSlot + 1);
                } else {
                    SelectedCardSlot = null;
                }
            }
        }

        CardSlot.remove(slotLayout);

        if (CardSlot.size() > 0) {
            int midPoint = CardSlot.size() / 2 + CardSlot.size() % 2 - 1;

            float gap = this.width / CardSlot.size();

            if (gap >= (3f* AssetsAndResource.CardWidth)/4f) {
                gap = (3f * AssetsAndResource.CardWidth)/4f;
            }

            for (int i = 0; i < CardSlot.size(); i++) {
                float x = ((center_x) + (((float) (i - midPoint)) * gap * gap_vec_x));
                float y = ((center_y) + (((float) (i - midPoint)) * gap * gap_vec_y));
                float z = ((center_z) + (((float) (i - midPoint)) * gap * gap_vec_z));

                DynamicCardSlotLayout slotLayout1 = CardSlot.get(i);
                slotLayout1.setSlotPosition(x, y, z);
            }
        }

        WidgetToSlotMapping.remove(widget);
        slotLayout.resetSlot();
        cardSlotLayoutPool.free(slotLayout);

        return widget;
    }

    public void LockCardWidget(CardWidget widget, float x, float z) {
        DynamicCardSlotLayout slotLayout = WidgetToSlotMapping.get(widget);

        slotLayout.lockSlot(x, AssetsAndResource.CardHeight/2, z, 0, 0, 1f, 0);
        slotLayout.setTwoStepTransition(true, clearance_x, clearance_y, clearance_z, gap_vec_x, gap_vec_y, gap_vec_z);
        LockMode = true;
    }

    public void UnlockCardWidget(CardWidget widget) {
        DynamicCardSlotLayout slotLayout = WidgetToSlotMapping.get(widget);

        slotLayout.unlockSlot();
        slotLayout.setTwoStepTransition(true, clearance_x, clearance_y, clearance_z, gap_vec_x, gap_vec_y, gap_vec_z);
        LockMode = false;
    }
}
