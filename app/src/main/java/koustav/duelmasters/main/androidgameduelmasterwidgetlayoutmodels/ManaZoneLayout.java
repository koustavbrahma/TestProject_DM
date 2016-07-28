package koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmasterswidget.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterswidget.WidgetTouchFocusLevel;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayout.HeadOrientation;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayout.Layout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgamesframework.Input;
import koustav.duelmasters.main.androidgamesframework.Pool;

/**
 * Created by Koustav on 5/31/2016.
 */
public class ManaZoneLayout implements Layout{
    enum TouchModeManaZone {
        NormalMode,
        DragMode,
        SlotExpandMode,
    }
    TouchModeManaZone touchMode;

    Pool<CardSlotLayoutXZPlaner> cardSlotLayoutPool;

    ArrayList<CardSlotLayoutXZPlaner> LeftWingOfCardSlot;
    ArrayList<CardSlotLayoutXZPlaner> RightWingOfCardSlot;
    ArrayList<CardSlotLayoutXZPlaner> CoupleCardSlot;
    ArrayList<CardSlotLayoutXZPlaner> TransitionSlotFromFreeToCouple;
    ArrayList<CardSlotLayoutXZPlaner> TransitionSlotFromCoupleToFree;
    CardSlotLayoutXZPlaner HeadCardSlot;
    CardSlotLayoutXZPlaner SelectedCardSlot;
    CardSlotLayoutXZPlaner SelectedCoupleCardSlot;
    CardSlotLayoutXZPlaner NewCoupleSlot;
    CardSlotLayoutXZPlaner DraggingSlot;

    Hashtable<CardWidget, CardSlotLayoutXZPlaner> WidgetToSlotMapping;

    float ZCoordinateOfZoneCenter;
    HeadOrientation headOrientationOfCard;
    boolean Opponent;
    float width;
    float height;
    float CoupleSlotWidth;
    float k1;
    float k2;

    ArrayList<CardSlotLayoutXZPlaner> TouchedSlots;
    ArrayList<WidgetTouchEvent> widgetTouchEventList;

    boolean DragMode;
    boolean DragLock;
    int DragCount;
    boolean ExpandMode;

    ArrayList<CardSlotLayoutXZPlaner> SlotsToRemoveDuringTransition;

    public ManaZoneLayout() {
        Pool.PoolObjectFactory<CardSlotLayoutXZPlaner> factory = new Pool.PoolObjectFactory<CardSlotLayoutXZPlaner>() {
            @Override
            public CardSlotLayoutXZPlaner createObject() {
                return new CardSlotLayoutXZPlaner();
            }
        };
        touchMode = TouchModeManaZone.NormalMode;

        ZCoordinateOfZoneCenter = 0;
        headOrientationOfCard = HeadOrientation.North;
        this.width = 0;
        this.height = 0;
        CoupleSlotWidth = 0;
        k1 = 2f;
        k2 = 2f;
        this.Opponent = false;

        cardSlotLayoutPool = new Pool<CardSlotLayoutXZPlaner>(factory, 40);

        LeftWingOfCardSlot = new ArrayList<CardSlotLayoutXZPlaner>();
        RightWingOfCardSlot = new ArrayList<CardSlotLayoutXZPlaner>();
        CoupleCardSlot = new ArrayList<CardSlotLayoutXZPlaner>();
        TransitionSlotFromFreeToCouple = new ArrayList<CardSlotLayoutXZPlaner>();
        TransitionSlotFromCoupleToFree = new ArrayList<CardSlotLayoutXZPlaner>();
        SelectedCardSlot = null;
        SelectedCoupleCardSlot = null;
        HeadCardSlot = null;
        NewCoupleSlot = null;
        DraggingSlot = null;

        WidgetToSlotMapping = new Hashtable<CardWidget, CardSlotLayoutXZPlaner>();

        TouchedSlots = new ArrayList<CardSlotLayoutXZPlaner>();
        widgetTouchEventList = new ArrayList<WidgetTouchEvent>();

        DragMode = false;
        DragLock = false;
        DragCount = 0;
        ExpandMode = true;

        SlotsToRemoveDuringTransition = new ArrayList<CardSlotLayoutXZPlaner>();
    }

    public void InitializeBattleZoneLayout(float zCoordinateOfZoneCenter, float width, float height,
                                           HeadOrientation orientation, boolean opponent, float k1, float k2) {
        ZCoordinateOfZoneCenter = zCoordinateOfZoneCenter;
        if (orientation == HeadOrientation.North) {
            headOrientationOfCard = HeadOrientation.South;
        } else if (orientation == HeadOrientation.South) {
            headOrientationOfCard = HeadOrientation.North;
        } else {
            throw new IllegalArgumentException("can only be north or south");
        }
        this.width = width;
        this.height = height;
        this.Opponent = opponent;
        this.k1 = k1;
        this.k2 = k2;
    }

    public void SetDraggingMode(boolean val) {
        DragMode = val;
    }

    public void SetExpandMode(boolean val) {
        ExpandMode = val;
    }

    public void FreezeNewCoupleSlot() {
        NewCoupleSlot = null;
    }

    public boolean FreeManaCardOverlapping() {
        float gap = (this.width - this.CoupleSlotWidth)/(1f + LeftWingOfCardSlot.size() +RightWingOfCardSlot.size());

        if (gap >= AssetsAndResource.CardHeight) {
            gap = AssetsAndResource.CardHeight;
        }

        return  (gap < AssetsAndResource.CardWidth);
    }

    public boolean CoupleManaCardOverlapping() {
        if (CoupleCardSlot.size() == 0) {
            return false;
        }

        float gap2 = 0;
        float expectedCoupleSlotWidth = (CoupleCardSlot.size() + 1) * AssetsAndResource.CardHeight +
                (CoupleCardSlot.size() -1) * (AssetsAndResource.CardWidth/4);

        if (expectedCoupleSlotWidth == CoupleSlotWidth) {
            gap2 = AssetsAndResource.CardHeight + AssetsAndResource.CardWidth/4 ;
        } else {
            gap2 = (CoupleSlotWidth - AssetsAndResource.CardHeight) / CoupleCardSlot.size();
        }

        return  (gap2 < AssetsAndResource.CardHeight) ;
    }

    @Override
    public void update(float deltaTime, float totalTime) {
        SlotsToRemoveDuringTransition.clear();

        for (int i = 0; i < TransitionSlotFromFreeToCouple.size(); i++) {
            CardSlotLayoutXZPlaner slotLayout = TransitionSlotFromFreeToCouple.get(i);
            slotLayout.update(deltaTime, totalTime);
            if (slotLayout.getPercentageComplete() > 0.70f) {
                CardWidget widget = slotLayout.getCardWidget();
                if (slotLayout.stackCount() > 1) {
                    throw new RuntimeException("Count cannot be more than one");
                }

                slotLayout.UpdateTopWidgetPosition();
                WidgetToSlotMapping.remove(widget);
                slotLayout.resetSlot();
                SlotsToRemoveDuringTransition.add(slotLayout);
                TransferCardWidgetToCoupleSlotZone(widget);
            }
        }

        for (int i = 0; i < TransitionSlotFromCoupleToFree.size(); i++) {
            CardSlotLayoutXZPlaner slotLayout = TransitionSlotFromCoupleToFree.get(i);
            slotLayout.update(deltaTime, totalTime);
            if (slotLayout.getPercentageComplete() > 0.70f) {
                CardWidget widget = slotLayout.getCardWidget();
                if (slotLayout.stackCount() > 1) {
                    throw new RuntimeException("Count cannot be more than one");
                }

                slotLayout.UpdateTopWidgetPosition();
                WidgetToSlotMapping.remove(widget);
                slotLayout.resetSlot();
                SlotsToRemoveDuringTransition.add(slotLayout);
                AddCardWidgetToZone(widget);
            }
        }

        for (int i = 0; i < SlotsToRemoveDuringTransition.size(); i++) {
            CardSlotLayoutXZPlaner slotLayout = SlotsToRemoveDuringTransition.get(i);
            TransitionSlotFromFreeToCouple.remove(slotLayout);
            TransitionSlotFromCoupleToFree.remove(slotLayout);
            cardSlotLayoutPool.free(slotLayout);
        }

        if (HeadCardSlot != null) {
            HeadCardSlot.update(deltaTime, totalTime);
        }

        for(int i = 0; i < LeftWingOfCardSlot.size(); i++) {
            CardSlotLayoutXZPlaner slotLayout = LeftWingOfCardSlot.get(i);
            slotLayout.update(deltaTime, totalTime);
        }

        for(int i = 0; i < RightWingOfCardSlot.size(); i++) {
            CardSlotLayoutXZPlaner slotLayout = RightWingOfCardSlot.get(i);
            slotLayout.update(deltaTime, totalTime);
        }

        for (int i = 0; i < CoupleCardSlot.size(); i++) {
            CardSlotLayoutXZPlaner slotLayout = CoupleCardSlot.get(i);
            slotLayout.update(deltaTime, totalTime);
        }

        if (DraggingSlot != null) {
            DraggingSlot.DragUpdate(AssetsAndResource.CardLength * 40f);
        }

        SlotsToRemoveDuringTransition.clear();
    }

    @Override
    public void draw() {
        if (SelectedCardSlot == HeadCardSlot) {
            for (int i = LeftWingOfCardSlot.size() - 1; i >= 0; i--) {
                CardSlotLayoutXZPlaner slotLayout = LeftWingOfCardSlot.get(i);
                slotLayout.draw();
            }

            for (int i = RightWingOfCardSlot.size() - 1; i >= 0; i--) {
                CardSlotLayoutXZPlaner slotLayout = RightWingOfCardSlot.get(i);
                slotLayout.draw();
            }

            if (HeadCardSlot != null) {
                HeadCardSlot.draw();
            }
        } else if (LeftWingOfCardSlot.contains(SelectedCardSlot)) {
            int index = LeftWingOfCardSlot.indexOf(SelectedCardSlot);

            for (int i = LeftWingOfCardSlot.size() - 1; i > index; i--) {
                CardSlotLayoutXZPlaner slotLayout = LeftWingOfCardSlot.get(i);
                slotLayout.draw();
            }

            for (int i = RightWingOfCardSlot.size() - 1; i >= 0; i--) {
                CardSlotLayoutXZPlaner slotLayout = RightWingOfCardSlot.get(i);
                slotLayout.draw();
            }

            HeadCardSlot.draw();

            for (int i = 0; i <= index; i++) {
                CardSlotLayoutXZPlaner slotLayout = LeftWingOfCardSlot.get(i);
                slotLayout.draw();
            }
        } else if (RightWingOfCardSlot.contains(SelectedCardSlot)) {
            int index = RightWingOfCardSlot.indexOf(SelectedCardSlot);

            for (int i = LeftWingOfCardSlot.size() - 1; i >= 0; i--) {
                CardSlotLayoutXZPlaner slotLayout = LeftWingOfCardSlot.get(i);
                slotLayout.draw();
            }

            HeadCardSlot.draw();

            for (int i = 0; i <index; i++) {
                CardSlotLayoutXZPlaner slotLayout = RightWingOfCardSlot.get(i);
                slotLayout.draw();
            }

            for (int i = RightWingOfCardSlot.size() -1; i >= index; i--) {
                CardSlotLayoutXZPlaner slotLayout = RightWingOfCardSlot.get(i);
                slotLayout.draw();
            }
        } else {
            throw new RuntimeException("SelectedCardSlot not found");
        }

        int index = CoupleCardSlot.size();
        if (SelectedCoupleCardSlot != null) {
            index = CoupleCardSlot.indexOf(SelectedCoupleCardSlot);
        }

        for (int i = 0; i < index; i++) {
            CardSlotLayoutXZPlaner slotLayout =  CoupleCardSlot.get(i);
            slotLayout.draw();
        }

        for (int i = CoupleCardSlot.size() - 1; i >= index; i--) {
            CardSlotLayoutXZPlaner slotLayout =  CoupleCardSlot.get(i);
            slotLayout.draw();
        }

        for (int i = 0; i < TransitionSlotFromFreeToCouple.size(); i++) {
            CardSlotLayoutXZPlaner slotLayout =  TransitionSlotFromFreeToCouple.get(i);
            slotLayout.draw();
        }

        for (int i = 0; i < TransitionSlotFromCoupleToFree.size(); i++) {
            CardSlotLayoutXZPlaner slotLayout = TransitionSlotFromCoupleToFree.get(i);
            slotLayout.draw();
        }
    }

    @Override
    public WidgetTouchEvent TouchResponse(List<Input.TouchEvent> touchEvents) {
        if (touchMode == TouchModeManaZone.NormalMode) {
            return TouchResponseInNormalMode(touchEvents);
        } else if (touchMode == TouchModeManaZone.DragMode) {
            return TouchResponseInDragMode(touchEvents);
        } else if (touchMode == TouchModeManaZone.SlotExpandMode) {
            return TouchResponseInExpandMode(touchEvents);
        }

        return null;
    }

    public WidgetTouchEvent TouchResponseInDragMode(List<Input.TouchEvent> touchEvents) {
        if (DraggingSlot == null) {
            throw new RuntimeException("Dragging Slot cannot be null");
        }
        WidgetTouchEvent widgetTouchEvent;
        Input input = AssetsAndResource.game.getInput();
        if (input.isTouchDown(0)) {
            widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
            widgetTouchEvent.isTouched = true;
            widgetTouchEvent.isTouchedDown = true;
            if (input.TouchType(0) == Input.TouchEvent.TOUCH_DRAGGED) {
                if (!DragLock) {
                    DragCount++;
                }
                widgetTouchEvent.isMoving = true;
            } else {
                widgetTouchEvent.isMoving = false;
            }
            widgetTouchEvent.isFocus = WidgetTouchFocusLevel.High;
            widgetTouchEvent.isDoubleTouched = false;
            widgetTouchEvent.object = DraggingSlot.getCardWidget().getLogicalObject();

            if (!DragLock && (DragCount > 2)) {
                DragLock = true;
                DragCount = 0;
            }
            return widgetTouchEvent;
        } else {
            if (!DragLock && CoupleCardSlot.contains(DraggingSlot) &&
                    (ExpandMode && (NewCoupleSlot != null && SelectedCoupleCardSlot == NewCoupleSlot) && NewCoupleSlot.stackCount() > 1)) {
                touchMode = TouchModeManaZone.SlotExpandMode;
                widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
                widgetTouchEvent.isTouched = true;
                widgetTouchEvent.isTouchedDown = false;
                widgetTouchEvent.isMoving = false;
                widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Medium;
                widgetTouchEvent.isDoubleTouched = false;
                widgetTouchEvent.object = DraggingSlot.getCardWidget().getLogicalObject();
                if (DraggingSlot != SelectedCoupleCardSlot) {
                    throw new RuntimeException("Invalid condition");
                }
                SelectedCoupleCardSlot.ExpandOrShrinkSlot(true, AssetsAndResource.CardLength * 40, AssetsAndResource.MazeWidth/2 * (Opponent == true ? -1 : 1));
            } else {
                touchMode = TouchModeManaZone.NormalMode;
                widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
                widgetTouchEvent.isTouched = true;
                widgetTouchEvent.isTouchedDown = false;
                widgetTouchEvent.isMoving = false;
                widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Low;
                widgetTouchEvent.isDoubleTouched = false;
                widgetTouchEvent.object = DraggingSlot.getCardWidget().getLogicalObject();
                DraggingSlot.SetSlotDisturbed();
            }
            DragLock = false;
            DragCount = 0;
            DraggingSlot = null;
            return widgetTouchEvent;
        }
    }

    public WidgetTouchEvent TouchResponseInExpandMode(List<Input.TouchEvent> touchEvents) {
        WidgetTouchEvent widgetTouchEvent;
        Input input = AssetsAndResource.game.getInput();
        if (input.isTouchDown(0)) {
            if (DraggingSlot != null) {
                widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
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
            } else {
                widgetTouchEvent = SelectedCoupleCardSlot.TouchResponse(touchEvents);
                widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Medium;

                if (widgetTouchEvent.isTouched) {
                    if (DragMode) {
                        widgetTouchEvent.isFocus = WidgetTouchFocusLevel.High;
                        DraggingSlot = SelectedCoupleCardSlot;
                    }
                }
            }
            return widgetTouchEvent;
        } else {
            if (DraggingSlot != null) {
                widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
                widgetTouchEvent.isTouched = true;
                widgetTouchEvent.isTouchedDown = false;
                widgetTouchEvent.isMoving = false;
                widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Medium;
                widgetTouchEvent.isDoubleTouched = false;
                widgetTouchEvent.object = DraggingSlot.getCardWidget().getLogicalObject();
                DraggingSlot.SetSlotDisturbed();
            } else {
                if (SelectedCoupleCardSlot!= null) {
                    widgetTouchEvent = SelectedCoupleCardSlot.TouchResponse(touchEvents);
                } else {
                    touchMode = TouchModeManaZone.NormalMode;
                    DraggingSlot = null;
                    return null;
                }
            }
            DraggingSlot = null;
            if (widgetTouchEvent.isTouched) {
                widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Medium;
            } else {
                if (touchEvents.size() > 0) {
                    widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Low;
                    touchMode = TouchModeManaZone.NormalMode;
                    SelectedCoupleCardSlot.ExpandOrShrinkSlot(false, 0f, 0f);
                } else {
                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                    widgetTouchEvent = null;
                }
            }
            return widgetTouchEvent;
        }
    }

    public WidgetTouchEvent TouchResponseInNormalMode(List<Input.TouchEvent> touchEvents) {
        if (HeadCardSlot == null && SelectedCoupleCardSlot == null) {
            return null;
        }

        if (HeadCardSlot != null && SelectedCardSlot == null) {
            throw new RuntimeException("SelectedCardSlot is null and HeadCardSlot is not null");
        }

        widgetTouchEventList.clear();
        TouchedSlots.clear();
        WidgetTouchEvent widgetTouchEvent;
        float gap = (this.width - this.CoupleSlotWidth)/(1f + LeftWingOfCardSlot.size() +RightWingOfCardSlot.size());

        if (gap >= AssetsAndResource.CardHeight) {
            gap = AssetsAndResource.CardHeight;
        }

        float gap2 = 0;
        if (CoupleCardSlot.size() > 0) {
            float expectedCoupleSlotWidth = (CoupleCardSlot.size() + 1) * AssetsAndResource.CardHeight +
                    (CoupleCardSlot.size() -1) * (AssetsAndResource.CardWidth/4);

            if (expectedCoupleSlotWidth == CoupleSlotWidth) {
                gap2 = AssetsAndResource.CardHeight + AssetsAndResource.CardWidth/4 ;
            } else {
                gap2 = (CoupleSlotWidth - AssetsAndResource.CardHeight) / CoupleCardSlot.size();
            }
        }

        Input input = AssetsAndResource.game.getInput();
        if (input.isTouchDown(0)) {
            GLGeometry.GLPoint relativeNearPointAfterTrans = input.getNearPoint(0).translate(new GLGeometry.GLVector(0f, 0f, -ZCoordinateOfZoneCenter));
            GLGeometry.GLPoint relativeFarPointAfterTrans = input.getFarPoint(0).translate(new GLGeometry.GLVector(0f, 0f, -ZCoordinateOfZoneCenter));

            GLGeometry.GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                    new GLGeometry.GLRay(relativeNearPointAfterTrans, GLGeometry.GLVectorBetween(relativeNearPointAfterTrans,
                            relativeFarPointAfterTrans)), 0);

            float width = Math.abs(intersectingPoint.x);
            float height = Math.abs(intersectingPoint.z);

            float x;
            int index;
            float relative_x;

            if (width <= ((this.width/2) + AssetsAndResource.CardHeight/2) && height <= this.height/2) {
                relative_x = intersectingPoint.x - ((this.CoupleSlotWidth/2) * (Opponent == true ? -1 : 1));
                x = (relative_x / gap) * (Opponent == true ? -1 : 1);
                index = (int) Math.floor(Math.abs(x));
                if (index == 0) {
                    if (HeadCardSlot != null) {
                        widgetTouchEvent = HeadCardSlot.TouchResponse(touchEvents);
                        if (widgetTouchEvent.isTouched) {
                            TouchedSlots.add(HeadCardSlot);
                            widgetTouchEventList.add(widgetTouchEvent);
                        } else {
                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                        }
                    }

                    for (int i =0; i < LeftWingOfCardSlot.size(); i++) {
                        CardSlotLayoutXZPlaner slotLayout = LeftWingOfCardSlot.get(i);

                        widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                        if (widgetTouchEvent.isTouched) {
                            TouchedSlots.add(0, slotLayout);
                            widgetTouchEventList.add(0, widgetTouchEvent);
                        } else {
                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                            break;
                        }
                    }

                    for (int i = 0; i < RightWingOfCardSlot.size(); i++) {
                        CardSlotLayoutXZPlaner slotLayout = RightWingOfCardSlot.get(i);

                        widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                        if (widgetTouchEvent.isTouched) {
                            TouchedSlots.add(slotLayout);
                            widgetTouchEventList.add(widgetTouchEvent);
                        } else {
                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                            break;
                        }
                    }
                } else if (x < 0) {
                    CardSlotLayoutXZPlaner slotLayout;
                    if (index > LeftWingOfCardSlot.size()) {
                        index = LeftWingOfCardSlot.size();
                    }

                    if (index > 0) {
                        slotLayout = LeftWingOfCardSlot.get(index - 1);

                        widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                        if (widgetTouchEvent.isTouched) {
                            TouchedSlots.add(slotLayout);
                            widgetTouchEventList.add(widgetTouchEvent);
                        } else {
                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                        }
                    }

                    for (int i = index; i < LeftWingOfCardSlot.size(); i++) {
                        slotLayout = LeftWingOfCardSlot.get(i);

                        widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                        if (widgetTouchEvent.isTouched) {
                            TouchedSlots.add(0, slotLayout);
                            widgetTouchEventList.add(0, widgetTouchEvent);
                        } else {
                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                            break;
                        }
                    }

                    boolean ok = true;

                    for (int i = index - 2; i >= 0; i--) {
                        slotLayout = LeftWingOfCardSlot.get(i);

                        widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                        if (widgetTouchEvent.isTouched) {
                            TouchedSlots.add(slotLayout);
                            widgetTouchEventList.add(widgetTouchEvent);
                        } else {
                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                            ok = false;
                            break;
                        }
                    }

                    if (ok) {
                        if (HeadCardSlot != null) {
                            widgetTouchEvent = HeadCardSlot.TouchResponse(touchEvents);
                            if (widgetTouchEvent.isTouched) {
                                TouchedSlots.add(HeadCardSlot);
                                widgetTouchEventList.add(widgetTouchEvent);
                            } else {
                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                ok = false;
                            }
                        }
                    }

                    if (ok) {
                        for (int i = 0; i < RightWingOfCardSlot.size(); i++) {
                            slotLayout = RightWingOfCardSlot.get(i);

                            widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                            if (widgetTouchEvent.isTouched) {
                                TouchedSlots.add(slotLayout);
                                widgetTouchEventList.add(widgetTouchEvent);
                            } else {
                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                break;
                            }
                        }
                    }

                    if (TouchedSlots.size() == 0 && CoupleCardSlot.size() > 0) {
                        relative_x = intersectingPoint.x + ((this.width/2) * (Opponent == true ? -1 : 1));
                        x = (relative_x / gap2) * (Opponent == true ? -1 : 1);

                        if (x > CoupleCardSlot.size()) {
                            index = CoupleCardSlot.size() - 1;
                        } else if (x > 0){
                            index = (int) Math.floor(x);
                        } else {
                            index = 0;
                        }

                        for (int i = index; i>= 0 ; i--) {
                            slotLayout = CoupleCardSlot.get(i);

                            widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                            if (widgetTouchEvent.isTouched) {
                                TouchedSlots.add(0, slotLayout);
                                widgetTouchEventList.add(0, widgetTouchEvent);
                            } else {
                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                break;
                            }
                        }

                        for (int i = index + 1; i < CoupleCardSlot.size(); i++) {
                            slotLayout = CoupleCardSlot.get(i);

                            widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                            if (widgetTouchEvent.isTouched) {
                                TouchedSlots.add(slotLayout);
                                widgetTouchEventList.add(widgetTouchEvent);
                            } else {
                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                break;
                            }
                        }
                    }
                } else {
                    CardSlotLayoutXZPlaner slotLayout;
                    if (index > RightWingOfCardSlot.size()) {
                        index = RightWingOfCardSlot.size();
                    }

                    if (index > 0) {
                        slotLayout = RightWingOfCardSlot.get(index - 1);

                        widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                        if (widgetTouchEvent.isTouched) {
                            TouchedSlots.add(slotLayout);
                            widgetTouchEventList.add(widgetTouchEvent);
                        } else {
                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                        }
                    }

                    for (int i = index; i < RightWingOfCardSlot.size(); i++) {
                        slotLayout = RightWingOfCardSlot.get(i);

                        widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                        if (widgetTouchEvent.isTouched) {
                            TouchedSlots.add(slotLayout);
                            widgetTouchEventList.add(widgetTouchEvent);
                        } else {
                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                            break;
                        }
                    }

                    boolean ok = true;

                    for (int i = index - 2; i >= 0; i--) {
                        slotLayout = RightWingOfCardSlot.get(i);

                        widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                        if (widgetTouchEvent.isTouched) {
                            TouchedSlots.add(0, slotLayout);
                            widgetTouchEventList.add(0, widgetTouchEvent);
                        } else {
                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                            ok = false;
                            break;
                        }
                    }

                    if (ok) {
                        if (HeadCardSlot != null) {
                            widgetTouchEvent = HeadCardSlot.TouchResponse(touchEvents);
                            if (widgetTouchEvent.isTouched) {
                                TouchedSlots.add(0, HeadCardSlot);
                                widgetTouchEventList.add(0, widgetTouchEvent);
                            } else {
                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                ok = false;
                            }
                        }
                    }

                    if (ok) {
                        for (int i = 0; i < LeftWingOfCardSlot.size(); i++) {
                            slotLayout = LeftWingOfCardSlot.get(i);

                            widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                            if (widgetTouchEvent.isTouched) {
                                TouchedSlots.add(0, slotLayout);
                                widgetTouchEventList.add(0, widgetTouchEvent);
                            } else {
                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                break;
                            }
                        }
                    }
                }

                if (TouchedSlots.size() > 0) {
                    if (CoupleCardSlot.contains(TouchedSlots.get(0))) {
                        index = CoupleCardSlot.indexOf(SelectedCoupleCardSlot);
                        if (index < CoupleCardSlot.indexOf(TouchedSlots.get(0))) {
                            SelectedCoupleCardSlot = TouchedSlots.get(0);
                            widgetTouchEvent = widgetTouchEventList.remove(0);

                            for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                            }

                            if (widgetTouchEvent.isTouchedDown && DragMode && NewCoupleSlot != null && NewCoupleSlot == SelectedCoupleCardSlot) {
                                DraggingSlot = SelectedCoupleCardSlot;
                                touchMode = TouchModeManaZone.DragMode;
                                widgetTouchEvent.isFocus = WidgetTouchFocusLevel.High;
                            } else if (ExpandMode && !widgetTouchEvent.isTouchedDown) {
                                if (DragMode && NewCoupleSlot != null && SelectedCoupleCardSlot == NewCoupleSlot) {
                                    if (NewCoupleSlot.stackCount() > 1) {
                                        NewCoupleSlot.ExpandOrShrinkSlot(true, AssetsAndResource.CardLength * 40, AssetsAndResource.MazeWidth/2 * (Opponent == true ? -1 : 1));
                                        widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Medium;
                                        touchMode = TouchModeManaZone.SlotExpandMode;
                                    }
                                } else if (!DragMode) {
                                    if (SelectedCoupleCardSlot.stackCount() > 1) {
                                        SelectedCoupleCardSlot.ExpandOrShrinkSlot(true, AssetsAndResource.CardLength * 40, AssetsAndResource.MazeWidth/2 * (Opponent == true ? -1 : 1));
                                        widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Medium;
                                        touchMode = TouchModeManaZone.SlotExpandMode;
                                    }
                                }
                            }

                            widgetTouchEventList.clear();
                            TouchedSlots.clear();
                            return widgetTouchEvent;
                        } else if (index > CoupleCardSlot.indexOf(TouchedSlots.get(TouchedSlots.size() -1))) {
                            SelectedCoupleCardSlot = TouchedSlots.get(TouchedSlots.size() - 1);
                            widgetTouchEvent = widgetTouchEventList.remove(TouchedSlots.size() - 1);

                            for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                            }

                            if (widgetTouchEvent.isTouchedDown && DragMode && NewCoupleSlot != null && NewCoupleSlot == SelectedCoupleCardSlot) {
                                DraggingSlot = SelectedCoupleCardSlot;
                                touchMode = TouchModeManaZone.DragMode;
                                widgetTouchEvent.isFocus = WidgetTouchFocusLevel.High;
                            } else if (ExpandMode && !widgetTouchEvent.isTouchedDown) {
                                if (DragMode && NewCoupleSlot != null && SelectedCoupleCardSlot == NewCoupleSlot) {
                                    if (NewCoupleSlot.stackCount() > 1) {
                                        NewCoupleSlot.ExpandOrShrinkSlot(true, AssetsAndResource.CardLength * 40, AssetsAndResource.MazeWidth/2 * (Opponent == true ? -1 : 1));
                                        widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Medium;
                                        touchMode = TouchModeManaZone.SlotExpandMode;
                                    }
                                } else if (!DragMode) {
                                    if (SelectedCoupleCardSlot.stackCount() > 1) {
                                        SelectedCoupleCardSlot.ExpandOrShrinkSlot(true, AssetsAndResource.CardLength * 40, AssetsAndResource.MazeWidth/2 * (Opponent == true ? -1 : 1));
                                        widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Medium;
                                        touchMode = TouchModeManaZone.SlotExpandMode;
                                    }
                                }
                            }

                            widgetTouchEventList.clear();
                            TouchedSlots.clear();
                            return widgetTouchEvent;
                        } else {
                            int i = TouchedSlots.indexOf(SelectedCoupleCardSlot);
                            widgetTouchEvent = widgetTouchEventList.remove(i);

                            for (i = 0; i < widgetTouchEventList.size(); i++) {
                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                            }

                            if (widgetTouchEvent.isTouchedDown && DragMode && NewCoupleSlot != null && NewCoupleSlot == SelectedCoupleCardSlot) {
                                DraggingSlot = SelectedCoupleCardSlot;
                                touchMode = TouchModeManaZone.DragMode;
                                widgetTouchEvent.isFocus = WidgetTouchFocusLevel.High;
                            } else if (ExpandMode && !widgetTouchEvent.isTouchedDown) {
                                if (DragMode && NewCoupleSlot != null && SelectedCoupleCardSlot == NewCoupleSlot) {
                                    if (NewCoupleSlot.stackCount() > 1) {
                                        NewCoupleSlot.ExpandOrShrinkSlot(true, AssetsAndResource.CardLength * 40, AssetsAndResource.MazeWidth/2 * (Opponent == true ? -1 : 1));
                                        widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Medium;
                                        touchMode = TouchModeManaZone.SlotExpandMode;
                                    }
                                } else if (!DragMode) {
                                    if (SelectedCoupleCardSlot.stackCount() > 1) {
                                        SelectedCoupleCardSlot.ExpandOrShrinkSlot(true, AssetsAndResource.CardLength * 40, AssetsAndResource.MazeWidth/2 * (Opponent == true ? -1 : 1));
                                        widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Medium;
                                        touchMode = TouchModeManaZone.SlotExpandMode;
                                    }
                                }
                            }

                            widgetTouchEventList.clear();
                            TouchedSlots.clear();
                            return widgetTouchEvent;
                        }
                    } else {
                        if (TouchedSlots.contains(SelectedCardSlot)) {
                            int i = TouchedSlots.indexOf(SelectedCardSlot);
                            widgetTouchEvent = widgetTouchEventList.remove(i);

                            for (i = 0; i < widgetTouchEventList.size(); i++) {
                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                            }

                            if (DragMode) {
                                DraggingSlot = SelectedCardSlot;
                                touchMode = TouchModeManaZone.DragMode;
                                widgetTouchEvent.isFocus = WidgetTouchFocusLevel.High;
                            }

                            widgetTouchEventList.clear();
                            TouchedSlots.clear();
                            return widgetTouchEvent;
                        } else {
                            if (LeftWingOfCardSlot.contains(SelectedCardSlot)) {
                                if (LeftWingOfCardSlot.contains(TouchedSlots.get(0))) {
                                    int index1 = LeftWingOfCardSlot.indexOf(SelectedCardSlot);
                                    int index2 = LeftWingOfCardSlot.indexOf(TouchedSlots.get(0));

                                    if (index1 > index2) {
                                        SelectedCardSlot = TouchedSlots.get(0);
                                        widgetTouchEvent = widgetTouchEventList.remove(0);

                                        for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                        }

                                        if (DragMode) {
                                            DraggingSlot = SelectedCardSlot;
                                            touchMode = TouchModeManaZone.DragMode;
                                            widgetTouchEvent.isFocus = WidgetTouchFocusLevel.High;
                                        }

                                        widgetTouchEventList.clear();
                                        TouchedSlots.clear();
                                        return widgetTouchEvent;
                                    } else {
                                        if (!LeftWingOfCardSlot.contains(TouchedSlots.get(TouchedSlots.size() - 1))) {
                                            throw new RuntimeException("Invalid condition");
                                        }

                                        SelectedCardSlot = TouchedSlots.get(TouchedSlots.size() - 1);
                                        widgetTouchEvent = widgetTouchEventList.remove(TouchedSlots.size() - 1);

                                        for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                        }

                                        if (DragMode) {
                                            DraggingSlot = SelectedCardSlot;
                                            touchMode = TouchModeManaZone.DragMode;
                                            widgetTouchEvent.isFocus = WidgetTouchFocusLevel.High;
                                        }

                                        widgetTouchEventList.clear();
                                        TouchedSlots.clear();
                                        return widgetTouchEvent;
                                    }
                                } else {
                                    SelectedCardSlot = TouchedSlots.get(0);
                                    widgetTouchEvent = widgetTouchEventList.remove(0);

                                    for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                    }

                                    if (DragMode) {
                                        DraggingSlot = SelectedCardSlot;
                                        touchMode = TouchModeManaZone.DragMode;
                                        widgetTouchEvent.isFocus = WidgetTouchFocusLevel.High;
                                    }

                                    widgetTouchEventList.clear();
                                    TouchedSlots.clear();
                                    return widgetTouchEvent;
                                }
                            } else if (RightWingOfCardSlot.contains(SelectedCardSlot)) {
                                if (RightWingOfCardSlot.contains(TouchedSlots.get(TouchedSlots.size() - 1))) {
                                    int index1 = RightWingOfCardSlot.indexOf(SelectedCardSlot);
                                    int index2 = RightWingOfCardSlot.indexOf(TouchedSlots.get(TouchedSlots.size() - 1));

                                    if (index1 > index2) {
                                        SelectedCardSlot = TouchedSlots.get(TouchedSlots.size() - 1);
                                        widgetTouchEvent = widgetTouchEventList.remove(TouchedSlots.size() - 1);

                                        for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                        }

                                        if (DragMode) {
                                            DraggingSlot = SelectedCardSlot;
                                            touchMode = TouchModeManaZone.DragMode;
                                            widgetTouchEvent.isFocus = WidgetTouchFocusLevel.High;
                                        }

                                        widgetTouchEventList.clear();
                                        TouchedSlots.clear();
                                        return widgetTouchEvent;
                                    } else {
                                        if (!RightWingOfCardSlot.contains(TouchedSlots.get(0))) {
                                            throw new RuntimeException("Invalid condition");
                                        }

                                        SelectedCardSlot = TouchedSlots.get(0);
                                        widgetTouchEvent = widgetTouchEventList.remove(0);

                                        for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                        }

                                        if (DragMode) {
                                            DraggingSlot = SelectedCardSlot;
                                            touchMode = TouchModeManaZone.DragMode;
                                            widgetTouchEvent.isFocus = WidgetTouchFocusLevel.High;
                                        }

                                        widgetTouchEventList.clear();
                                        TouchedSlots.clear();
                                        return widgetTouchEvent;
                                    }
                                } else {
                                    SelectedCardSlot = TouchedSlots.get(TouchedSlots.size() - 1);
                                    widgetTouchEvent = widgetTouchEventList.remove(TouchedSlots.size() - 1);

                                    for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                    }

                                    if (DragMode) {
                                        DraggingSlot = SelectedCardSlot;
                                        touchMode = TouchModeManaZone.DragMode;
                                        widgetTouchEvent.isFocus = WidgetTouchFocusLevel.High;
                                    }

                                    widgetTouchEventList.clear();
                                    TouchedSlots.clear();
                                    return widgetTouchEvent;
                                }
                            } else if (HeadCardSlot == SelectedCardSlot) {
                                if (RightWingOfCardSlot.contains(TouchedSlots.get(0))) {
                                    SelectedCardSlot = TouchedSlots.get(0);
                                    widgetTouchEvent = widgetTouchEventList.remove(0);

                                    for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                    }

                                    if (DragMode) {
                                        DraggingSlot = SelectedCardSlot;
                                        touchMode = TouchModeManaZone.DragMode;
                                        widgetTouchEvent.isFocus = WidgetTouchFocusLevel.High;
                                    }

                                    widgetTouchEventList.clear();
                                    TouchedSlots.clear();
                                    return widgetTouchEvent;
                                } else if (LeftWingOfCardSlot.contains(TouchedSlots.get(TouchedSlots.size() - 1))) {
                                    SelectedCardSlot = TouchedSlots.get(TouchedSlots.size() - 1);
                                    widgetTouchEvent = widgetTouchEventList.remove(TouchedSlots.size() - 1);

                                    for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                    }

                                    if (DragMode) {
                                        DraggingSlot = SelectedCardSlot;
                                        touchMode = TouchModeManaZone.DragMode;
                                        widgetTouchEvent.isFocus = WidgetTouchFocusLevel.High;
                                    }

                                    widgetTouchEventList.clear();
                                    TouchedSlots.clear();
                                    return widgetTouchEvent;
                                } else {
                                    throw new RuntimeException("Invalid Condition");
                                }
                            } else {
                                throw new RuntimeException("Invalid Condition");
                            }
                        }
                    }
                }
                widgetTouchEventList.clear();
                TouchedSlots.clear();

                widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
                widgetTouchEvent.isTouched = true;
                widgetTouchEvent.isTouchedDown = true;
                widgetTouchEvent.isMoving = false;
                widgetTouchEvent.isDoubleTouched = false;
                widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Low;
                widgetTouchEvent.object = null;

                return widgetTouchEvent;
            }
        }  else {
            boolean isTouched = false;
            WidgetTouchEvent widgetTouchEventOutCome = null;
            Input.TouchEvent event = null;
            for (int j = touchEvents.size() - 1; j >= 0; j--) {
                event = touchEvents.get(j);
                if (event.type == Input.TouchEvent.TOUCH_UP) {
                    GLGeometry.GLPoint relativeNearPointAfterTrans = event.nearPoint[0].translate(new GLGeometry.GLVector(0f, 0f, -ZCoordinateOfZoneCenter));
                    GLGeometry.GLPoint relativeFarPointAfterTrans = event.farPoint[0].translate(new GLGeometry.GLVector(0f, 0f, -ZCoordinateOfZoneCenter));

                    GLGeometry.GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                            new GLGeometry.GLRay(relativeNearPointAfterTrans, GLGeometry.GLVectorBetween(relativeNearPointAfterTrans,
                                    relativeFarPointAfterTrans)), 0);

                    float width = Math.abs(intersectingPoint.x);
                    float height = Math.abs(intersectingPoint.z);

                    float x;
                    int index;
                    float relative_x;

                    if (width <= ((this.width / 2) + AssetsAndResource.CardHeight / 2) && height <= this.height / 2) {
                        isTouched = true;
                        relative_x = intersectingPoint.x - ((this.CoupleSlotWidth / 2) * (Opponent == true ? -1 : 1));
                        x = (relative_x / gap) * (Opponent == true ? -1 : 1);
                        index = (int) Math.floor(Math.abs(x));
                        if (index == 0) {

                            if (HeadCardSlot != null) {
                                widgetTouchEvent = HeadCardSlot.TouchResponse(touchEvents);
                                if (widgetTouchEvent.isTouched) {
                                    TouchedSlots.add(HeadCardSlot);
                                    widgetTouchEventList.add(widgetTouchEvent);
                                } else {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                }
                            }

                            for (int i = 0; i < LeftWingOfCardSlot.size(); i++) {
                                CardSlotLayoutXZPlaner slotLayout = LeftWingOfCardSlot.get(i);

                                widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                                if (widgetTouchEvent.isTouched) {
                                    TouchedSlots.add(0, slotLayout);
                                    widgetTouchEventList.add(0, widgetTouchEvent);
                                } else {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                    break;
                                }
                            }

                            for (int i = 0; i < RightWingOfCardSlot.size(); i++) {
                                CardSlotLayoutXZPlaner slotLayout = RightWingOfCardSlot.get(i);

                                widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                                if (widgetTouchEvent.isTouched) {
                                    TouchedSlots.add(slotLayout);
                                    widgetTouchEventList.add(widgetTouchEvent);
                                } else {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                    break;
                                }
                            }
                        } else if (x < 0) {
                            CardSlotLayoutXZPlaner slotLayout;
                            if (index > LeftWingOfCardSlot.size()) {
                                index = LeftWingOfCardSlot.size();
                            }

                            if (index > 0) {
                                slotLayout = LeftWingOfCardSlot.get(index - 1);

                                widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                                if (widgetTouchEvent.isTouched) {
                                    TouchedSlots.add(slotLayout);
                                    widgetTouchEventList.add(widgetTouchEvent);
                                } else {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                }
                            }

                            for (int i = index; i < LeftWingOfCardSlot.size(); i++) {
                                slotLayout = LeftWingOfCardSlot.get(i);

                                widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                                if (widgetTouchEvent.isTouched) {
                                    TouchedSlots.add(0, slotLayout);
                                    widgetTouchEventList.add(0, widgetTouchEvent);
                                } else {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                    break;
                                }
                            }

                            boolean ok = true;

                            for (int i = index - 2; i >= 0; i--) {
                                slotLayout = LeftWingOfCardSlot.get(i);

                                widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                                if (widgetTouchEvent.isTouched) {
                                    TouchedSlots.add(slotLayout);
                                    widgetTouchEventList.add(widgetTouchEvent);
                                } else {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                    ok = false;
                                    break;
                                }
                            }

                            if (ok) {
                                if (HeadCardSlot != null) {
                                    widgetTouchEvent = HeadCardSlot.TouchResponse(touchEvents);
                                    if (widgetTouchEvent.isTouched) {
                                        TouchedSlots.add(HeadCardSlot);
                                        widgetTouchEventList.add(widgetTouchEvent);
                                    } else {
                                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                        ok = false;
                                    }
                                }
                            }

                            if (ok) {
                                for (int i = 0; i < RightWingOfCardSlot.size(); i++) {
                                    slotLayout = RightWingOfCardSlot.get(i);

                                    widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                                    if (widgetTouchEvent.isTouched) {
                                        TouchedSlots.add(slotLayout);
                                        widgetTouchEventList.add(widgetTouchEvent);
                                    } else {
                                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                        break;
                                    }
                                }
                            }

                            if (TouchedSlots.size() == 0 && CoupleCardSlot.size() > 0) {
                                relative_x = intersectingPoint.x + ((this.width / 2) * (Opponent == true ? -1 : 1));
                                x = (relative_x / gap2) * (Opponent == true ? -1 : 1);

                                if (x > CoupleCardSlot.size()) {
                                    index = CoupleCardSlot.size() - 1;
                                } else if (x > 0) {
                                    index = (int) Math.floor(x);
                                } else {
                                    index = 0;
                                }

                                for (int i = index; i >= 0; i--) {
                                    slotLayout = CoupleCardSlot.get(i);

                                    widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                                    if (widgetTouchEvent.isTouched) {
                                        TouchedSlots.add(0, slotLayout);
                                        widgetTouchEventList.add(0, widgetTouchEvent);
                                    } else {
                                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                        break;
                                    }
                                }

                                for (int i = index + 1; i < CoupleCardSlot.size(); i++) {
                                    slotLayout = CoupleCardSlot.get(i);

                                    widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                                    if (widgetTouchEvent.isTouched) {
                                        TouchedSlots.add(slotLayout);
                                        widgetTouchEventList.add(widgetTouchEvent);
                                    } else {
                                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                        break;
                                    }
                                }
                            }
                        } else {
                            CardSlotLayoutXZPlaner slotLayout;
                            if (index > RightWingOfCardSlot.size()) {
                                index = RightWingOfCardSlot.size();
                            }

                            if (index > 0) {
                                slotLayout = RightWingOfCardSlot.get(index - 1);

                                widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                                if (widgetTouchEvent.isTouched) {
                                    TouchedSlots.add(slotLayout);
                                    widgetTouchEventList.add(widgetTouchEvent);
                                } else {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                }
                            }

                            for (int i = index; i < RightWingOfCardSlot.size(); i++) {
                                slotLayout = RightWingOfCardSlot.get(i);

                                widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                                if (widgetTouchEvent.isTouched) {
                                    TouchedSlots.add(slotLayout);
                                    widgetTouchEventList.add(widgetTouchEvent);
                                } else {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                    break;
                                }
                            }

                            boolean ok = true;

                            for (int i = index - 2; i >= 0; i--) {
                                slotLayout = RightWingOfCardSlot.get(i);

                                widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                                if (widgetTouchEvent.isTouched) {
                                    TouchedSlots.add(0, slotLayout);
                                    widgetTouchEventList.add(0, widgetTouchEvent);
                                } else {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                    ok = false;
                                    break;
                                }
                            }

                            if (ok) {
                                if (HeadCardSlot != null) {
                                    widgetTouchEvent = HeadCardSlot.TouchResponse(touchEvents);
                                    if (widgetTouchEvent.isTouched) {
                                        TouchedSlots.add(0, HeadCardSlot);
                                        widgetTouchEventList.add(0, widgetTouchEvent);
                                    } else {
                                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                        ok = false;
                                    }
                                }
                            }

                            if (ok) {
                                for (int i = 0; i < LeftWingOfCardSlot.size(); i++) {
                                    slotLayout = LeftWingOfCardSlot.get(i);

                                    widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                                    if (widgetTouchEvent.isTouched) {
                                        TouchedSlots.add(0, slotLayout);
                                        widgetTouchEventList.add(0, widgetTouchEvent);
                                    } else {
                                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                        break;
                                    }
                                }
                            }
                        }

                        if (TouchedSlots.size() > 0) {
                            if (CoupleCardSlot.contains(TouchedSlots.get(0))) {
                                index = CoupleCardSlot.indexOf(SelectedCoupleCardSlot);
                                if (index < CoupleCardSlot.indexOf(TouchedSlots.get(0))) {
                                    SelectedCoupleCardSlot = TouchedSlots.get(0);
                                    widgetTouchEvent = widgetTouchEventList.remove(0);

                                    for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                    }

                                    widgetTouchEventOutCome = widgetTouchEvent;
                                } else if (index > CoupleCardSlot.indexOf(TouchedSlots.get(TouchedSlots.size() - 1))) {
                                    SelectedCoupleCardSlot = TouchedSlots.get(TouchedSlots.size() - 1);
                                    widgetTouchEvent = widgetTouchEventList.remove(TouchedSlots.size() - 1);

                                    for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                    }

                                    widgetTouchEventOutCome = widgetTouchEvent;
                                } else {
                                    int i = TouchedSlots.indexOf(SelectedCoupleCardSlot);
                                    widgetTouchEvent = widgetTouchEventList.remove(i);

                                    for (i = 0; i < widgetTouchEventList.size(); i++) {
                                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                    }

                                    widgetTouchEventOutCome = widgetTouchEvent;
                                }

                                if (widgetTouchEventOutCome != null) {
                                    if (ExpandMode) {
                                        if (DragMode && NewCoupleSlot != null && SelectedCoupleCardSlot == NewCoupleSlot) {
                                            if (NewCoupleSlot.stackCount() > 1) {
                                                NewCoupleSlot.ExpandOrShrinkSlot(true, AssetsAndResource.CardLength * 40, AssetsAndResource.MazeWidth / 2 * (Opponent == true ? -1 : 1));
                                                widgetTouchEventOutCome.isFocus = WidgetTouchFocusLevel.Medium;
                                                touchMode = TouchModeManaZone.SlotExpandMode;
                                            }
                                        } else if (!DragMode) {
                                            if (SelectedCoupleCardSlot.stackCount() > 1) {
                                                SelectedCoupleCardSlot.ExpandOrShrinkSlot(true, AssetsAndResource.CardLength * 40, AssetsAndResource.MazeWidth / 2 * (Opponent == true ? -1 : 1));
                                                widgetTouchEventOutCome.isFocus = WidgetTouchFocusLevel.Medium;
                                                touchMode = TouchModeManaZone.SlotExpandMode;
                                            }
                                        }
                                    }
                                    break;
                                }
                            } else {
                                if (TouchedSlots.contains(SelectedCardSlot)) {
                                    int i = TouchedSlots.indexOf(SelectedCardSlot);
                                    widgetTouchEvent = widgetTouchEventList.remove(i);

                                    for (i = 0; i < widgetTouchEventList.size(); i++) {
                                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                    }

                                    widgetTouchEventOutCome = widgetTouchEvent;
                                    break;
                                } else {
                                    if (LeftWingOfCardSlot.contains(SelectedCardSlot)) {
                                        if (LeftWingOfCardSlot.contains(TouchedSlots.get(0))) {
                                            int index1 = LeftWingOfCardSlot.indexOf(SelectedCardSlot);
                                            int index2 = LeftWingOfCardSlot.indexOf(TouchedSlots.get(0));

                                            if (index1 > index2) {
                                                SelectedCardSlot = TouchedSlots.get(0);
                                                widgetTouchEvent = widgetTouchEventList.remove(0);

                                                for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                                }

                                                widgetTouchEventOutCome = widgetTouchEvent;
                                                break;
                                            } else {
                                                if (!LeftWingOfCardSlot.contains(TouchedSlots.get(TouchedSlots.size() - 1))) {
                                                    throw new RuntimeException("Invalid condition");
                                                }

                                                SelectedCardSlot = TouchedSlots.get(TouchedSlots.size() - 1);
                                                widgetTouchEvent = widgetTouchEventList.remove(TouchedSlots.size() - 1);

                                                for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                                }

                                                widgetTouchEventOutCome = widgetTouchEvent;
                                                break;
                                            }
                                        } else {
                                            SelectedCardSlot = TouchedSlots.get(0);
                                            widgetTouchEvent = widgetTouchEventList.remove(0);

                                            for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                            }

                                            widgetTouchEventOutCome = widgetTouchEvent;
                                            break;
                                        }
                                    } else if (RightWingOfCardSlot.contains(SelectedCardSlot)) {
                                        if (RightWingOfCardSlot.contains(TouchedSlots.get(TouchedSlots.size() - 1))) {
                                            int index1 = RightWingOfCardSlot.indexOf(SelectedCardSlot);
                                            int index2 = RightWingOfCardSlot.indexOf(TouchedSlots.get(TouchedSlots.size() - 1));

                                            if (index1 > index2) {
                                                SelectedCardSlot = TouchedSlots.get(TouchedSlots.size() - 1);
                                                widgetTouchEvent = widgetTouchEventList.remove(TouchedSlots.size() - 1);

                                                for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                                }

                                                widgetTouchEventOutCome = widgetTouchEvent;
                                                break;
                                            } else {
                                                if (!RightWingOfCardSlot.contains(TouchedSlots.get(0))) {
                                                    throw new RuntimeException("Invalid condition");
                                                }

                                                SelectedCardSlot = TouchedSlots.get(0);
                                                widgetTouchEvent = widgetTouchEventList.remove(0);

                                                for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                                }

                                                widgetTouchEventOutCome = widgetTouchEvent;
                                                break;
                                            }
                                        } else {
                                            SelectedCardSlot = TouchedSlots.get(TouchedSlots.size() - 1);
                                            widgetTouchEvent = widgetTouchEventList.remove(TouchedSlots.size() - 1);

                                            for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                            }

                                            widgetTouchEventOutCome = widgetTouchEvent;
                                            break;
                                        }
                                    } else if (HeadCardSlot == SelectedCardSlot) {
                                        if (RightWingOfCardSlot.contains(TouchedSlots.get(0))) {
                                            SelectedCardSlot = TouchedSlots.get(0);
                                            widgetTouchEvent = widgetTouchEventList.remove(0);

                                            for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                            }

                                            widgetTouchEventOutCome = widgetTouchEvent;
                                            break;
                                        } else if (LeftWingOfCardSlot.contains(TouchedSlots.get(TouchedSlots.size() - 1))) {
                                            SelectedCardSlot = TouchedSlots.get(TouchedSlots.size() - 1);
                                            widgetTouchEvent = widgetTouchEventList.remove(TouchedSlots.size() - 1);

                                            for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                            }

                                            widgetTouchEventOutCome = widgetTouchEvent;
                                            break;
                                        } else {
                                            throw new RuntimeException("Invalid Condition");
                                        }
                                    } else {
                                        throw new RuntimeException("Invalid Condition");
                                    }
                                }
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
                widgetTouchEventOutCome.isTouched = true;
                widgetTouchEventOutCome.isTouchedDown = false;
                widgetTouchEventOutCome.isMoving = false;
                widgetTouchEventOutCome.isDoubleTouched = false;
                widgetTouchEventOutCome.isFocus = WidgetTouchFocusLevel.Low;
                widgetTouchEventOutCome.object = null;

                return widgetTouchEventOutCome;
            }
        }

        return null;
    }

    public void AddCardWidgetToZone(CardWidget widget) {
        CardSlotLayoutXZPlaner slotLayout = cardSlotLayoutPool.newObject();
        slotLayout.initializeSlot(0, 0, ZCoordinateOfZoneCenter, widget, headOrientationOfCard, k1, k2);
        WidgetToSlotMapping.put(widget, slotLayout);

        SelectedCardSlot = slotLayout;
        if (HeadCardSlot != null) {
            if (LeftWingOfCardSlot.size() <= RightWingOfCardSlot.size()) {
                LeftWingOfCardSlot.add(0, HeadCardSlot);
            }  else {
                RightWingOfCardSlot.add(0, HeadCardSlot);
            }
        }

        HeadCardSlot = slotLayout;

        float gap = (this.width - this.CoupleSlotWidth)/(1f + LeftWingOfCardSlot.size() + RightWingOfCardSlot.size());

        if (gap >= AssetsAndResource.CardHeight) {
            gap = AssetsAndResource.CardHeight;
        }

        float centerPosition = (this.CoupleSlotWidth * (Opponent == true ? -1 : 1)) / 2f;
        HeadCardSlot.setSlotXPosition(centerPosition);

        for(int i = 0; i < LeftWingOfCardSlot.size(); i++) {
            slotLayout = LeftWingOfCardSlot.get(i);
            slotLayout.setSlotXPosition(centerPosition + (gap * (-(i+1)) * (Opponent == true ? -1 : 1)));
        }

        for(int i = 0; i < RightWingOfCardSlot.size(); i++) {
            slotLayout = RightWingOfCardSlot.get(i);
            slotLayout.setSlotXPosition(centerPosition + (gap * (i+1) * (Opponent == true ? -1 : 1)));
        }
    }

    public CardWidget RemoveCardWidgetFromZone(CardWidget widget) {
        CardSlotLayoutXZPlaner slotLayout = WidgetToSlotMapping.get(widget);
        boolean selectedSlotRemoved = false;
        boolean selectedCoupleSlotRemoved = false;

        if (slotLayout == null) {
            return null;
        }

        if (SelectedCardSlot == slotLayout) {
            selectedSlotRemoved = true;
        }

        if (SelectedCoupleCardSlot == slotLayout) {
            if (SelectedCoupleCardSlot.stackCount() == 1) {
                selectedCoupleSlotRemoved = true;
            }
        }

        if (CoupleCardSlot.contains(slotLayout)) {
            if (slotLayout.stackCount() ==1) {
                if (selectedCoupleSlotRemoved) {
                    int index = CoupleCardSlot.indexOf(slotLayout);

                    if (index == CoupleCardSlot.size() -1) {
                        if (CoupleCardSlot.size() == 1) {
                            SelectedCoupleCardSlot = null;
                        } else {
                            SelectedCoupleCardSlot = CoupleCardSlot.get(index -1);
                        }
                    } else {
                        SelectedCoupleCardSlot = CoupleCardSlot.get(index + 1);
                    }
                }

                if (NewCoupleSlot == slotLayout) {
                    NewCoupleSlot = null;
                }
                CoupleCardSlot.remove(slotLayout);

                float expectedCoupleSlotWidth = 0;
                this.CoupleSlotWidth = 0;
                if (CoupleCardSlot.size() > 0) {
                    this.CoupleSlotWidth = expectedCoupleSlotWidth = (CoupleCardSlot.size() + 1) * AssetsAndResource.CardHeight +
                            (CoupleCardSlot.size() -1) * (AssetsAndResource.CardWidth/4);

                    if (this.CoupleSlotWidth > this.width * 0.70f) {
                        this.CoupleSlotWidth = this.width * 0.70f;
                    }
                }

                float gap;
                if (expectedCoupleSlotWidth == CoupleSlotWidth) {
                    gap = AssetsAndResource.CardHeight + AssetsAndResource.CardWidth/4 ;
                } else {
                    gap = (CoupleSlotWidth - AssetsAndResource.CardHeight) / CoupleCardSlot.size();
                }

                for (int i = 0; i < CoupleCardSlot.size(); i++) {
                    float slotPosition = (-this.width/2 * (Opponent == true ? -1 : 1)) + (gap * i * (Opponent == true ? -1 : 1));
                    CardSlotLayoutXZPlaner slotLayout1 = CoupleCardSlot.get(i);
                    slotLayout1.setSlotXPosition(slotPosition);
                }
            }
        } else {
            if (slotLayout.stackCount() > 1) {
                throw new RuntimeException("Stack count cannot be more than 1");
            }
            if (slotLayout == HeadCardSlot) {
                if (selectedSlotRemoved) {
                    if (LeftWingOfCardSlot.size() > 0) {
                        SelectedCardSlot = LeftWingOfCardSlot.get(0);
                    } else if (RightWingOfCardSlot.size() > 0) {
                        SelectedCardSlot = RightWingOfCardSlot.get(0);
                    } else {
                        SelectedCardSlot = null;
                    }
                }

                if (LeftWingOfCardSlot.size() <= RightWingOfCardSlot.size()) {
                    if (RightWingOfCardSlot.size() > 0) {
                        CardSlotLayoutXZPlaner firstRightSlot = RightWingOfCardSlot.remove(0);
                        HeadCardSlot = firstRightSlot;
                    } else {
                        HeadCardSlot = null;
                    }
                } else {
                    CardSlotLayoutXZPlaner firstLeftSlot = LeftWingOfCardSlot.remove(0);
                    HeadCardSlot = firstLeftSlot;
                }
            } else if (LeftWingOfCardSlot.contains(slotLayout)) {
                if (selectedSlotRemoved) {
                    int indexofSlot = LeftWingOfCardSlot.indexOf(slotLayout);
                    if (indexofSlot > 0) {
                        SelectedCardSlot = LeftWingOfCardSlot.get(indexofSlot - 1);
                    } else {
                        SelectedCardSlot = HeadCardSlot;
                    }
                }

                LeftWingOfCardSlot.remove(slotLayout);
                int sizeDiff = LeftWingOfCardSlot.size() - RightWingOfCardSlot.size();

                if (sizeDiff >= 2) {
                    RightWingOfCardSlot.add(0, HeadCardSlot);
                    CardSlotLayoutXZPlaner firstLeftSlot = LeftWingOfCardSlot.remove(0);
                    HeadCardSlot = firstLeftSlot;
                } else if (sizeDiff <= -2) {
                    LeftWingOfCardSlot.add(0, HeadCardSlot);
                    CardSlotLayoutXZPlaner firstRightSlot = RightWingOfCardSlot.remove(0);
                    HeadCardSlot = firstRightSlot;
                }
            } else if (RightWingOfCardSlot.contains(slotLayout)) {
                if (selectedSlotRemoved) {
                    int indexofSlot = RightWingOfCardSlot.indexOf(slotLayout);
                    if (indexofSlot > 0) {
                        SelectedCardSlot = RightWingOfCardSlot.get(indexofSlot - 1);
                    } else {
                        SelectedCardSlot = HeadCardSlot;
                    }
                }

                RightWingOfCardSlot.remove(slotLayout);
                int sizeDiff = LeftWingOfCardSlot.size() - RightWingOfCardSlot.size();

                if (sizeDiff >= 2) {
                    RightWingOfCardSlot.add(0, HeadCardSlot);
                    CardSlotLayoutXZPlaner firstLeftSlot = LeftWingOfCardSlot.remove(0);
                    HeadCardSlot = firstLeftSlot;
                } else if (sizeDiff <= -2) {
                    LeftWingOfCardSlot.add(0, HeadCardSlot);
                    CardSlotLayoutXZPlaner firstRightSlot = RightWingOfCardSlot.remove(0);
                    HeadCardSlot = firstRightSlot;
                }
            } else {
                throw new RuntimeException("Slot object not found in the zone");
            }
        }

        float gap = (this.width - this.CoupleSlotWidth) / (1f + LeftWingOfCardSlot.size() + RightWingOfCardSlot.size());

        if (gap >= AssetsAndResource.CardHeight) {
            gap = AssetsAndResource.CardHeight;
        }

        float centerPosition = (this.CoupleSlotWidth * (Opponent == true ? -1 : 1)) / 2f;
        if (HeadCardSlot != null) {
            HeadCardSlot.setSlotXPosition(centerPosition);
        }

        for(int i = 0; i < LeftWingOfCardSlot.size(); i++) {
            slotLayout = LeftWingOfCardSlot.get(i);
            slotLayout.setSlotXPosition(centerPosition + gap * (-(i + 1)) * (Opponent == true ? -1 : 1));
        }

        for(int i = 0; i < RightWingOfCardSlot.size(); i++) {
            slotLayout = RightWingOfCardSlot.get(i);
            slotLayout.setSlotXPosition(centerPosition + gap * (i + 1) * (Opponent == true ? -1 : 1));
        }

        slotLayout = WidgetToSlotMapping.get(widget);
        CardWidget cardWidget = slotLayout.removeCardWidget(widget);

        if (cardWidget != null && cardWidget != widget) {
            throw new RuntimeException("Invalid condition");
        }

        WidgetToSlotMapping.remove(widget);

        if (cardWidget == null && slotLayout.stackCount() == 1) {
            slotLayout.resetSlot();
            cardSlotLayoutPool.free(slotLayout);
        } else if (slotLayout.stackCount() == 1 && CoupleCardSlot.contains(slotLayout)) {
            slotLayout.ExpandOrShrinkSlot(false, 0f, 0f);
        }

        return widget;
    }

    public void TransferCardWidgetToCoupleSlotZone(CardWidget widget) {
        if (NewCoupleSlot == null) {
            CardSlotLayoutXZPlaner slotLayout = cardSlotLayoutPool.newObject();
            slotLayout.initializeSlot(0, 0, ZCoordinateOfZoneCenter, widget, headOrientationOfCard, k1, k2);
            WidgetToSlotMapping.put(widget, slotLayout);
            NewCoupleSlot = slotLayout;
            CoupleCardSlot.add(NewCoupleSlot);
            SelectedCoupleCardSlot = NewCoupleSlot;
        } else {
            NewCoupleSlot.pushWidget(widget);
            WidgetToSlotMapping.put(widget, NewCoupleSlot);
            SelectedCoupleCardSlot = NewCoupleSlot;
        }

        float expectedCoupleSlotWidth;
        this.CoupleSlotWidth = 0;
        this.CoupleSlotWidth = expectedCoupleSlotWidth = (CoupleCardSlot.size() + 1) * AssetsAndResource.CardHeight +
                (CoupleCardSlot.size() -1) * (AssetsAndResource.CardWidth/4);

        if (this.CoupleSlotWidth > this.width * 0.70f) {
            this.CoupleSlotWidth = this.width * 0.70f;
        }

        float gap;
        if (expectedCoupleSlotWidth == CoupleSlotWidth) {
            gap = AssetsAndResource.CardHeight + AssetsAndResource.CardWidth/4 ;
        } else {
            gap = (CoupleSlotWidth - AssetsAndResource.CardHeight) / CoupleCardSlot.size();
        }

        for (int i = 0; i < CoupleCardSlot.size(); i++) {
            float slotPosition = (-this.width/2 * (Opponent == true ? -1 : 1)) + (gap * i * (Opponent == true ? -1 : 1));
            CardSlotLayoutXZPlaner slotLayout1 = CoupleCardSlot.get(i);
            slotLayout1.setSlotXPosition(slotPosition);
        }

        gap = (this.width - this.CoupleSlotWidth) / (1f + LeftWingOfCardSlot.size() + RightWingOfCardSlot.size());

        if (gap >= AssetsAndResource.CardHeight) {
            gap = AssetsAndResource.CardHeight;
        }

        float centerPosition = (this.CoupleSlotWidth * (Opponent == true ? -1 : 1)) / 2f;
        if (HeadCardSlot != null) {
            HeadCardSlot.setSlotXPosition(centerPosition);
        }

        CardSlotLayoutXZPlaner slotLayout;
        for (int i = 0; i < LeftWingOfCardSlot.size(); i++) {
            slotLayout = LeftWingOfCardSlot.get(i);
            slotLayout.setSlotXPosition(centerPosition + gap * (-(i + 1)) * (Opponent == true ? -1 : 1));
        }

        for (int i = 0; i < RightWingOfCardSlot.size(); i++) {
            slotLayout = RightWingOfCardSlot.get(i);
            slotLayout.setSlotXPosition(centerPosition + gap * (i + 1) * (Opponent == true ? -1 : 1));
        }
    }

    public void AddToTransitionZone(CardWidget widget) {
        CardSlotLayoutXZPlaner slotLayout = WidgetToSlotMapping.get(widget);

        if (CoupleCardSlot.contains(slotLayout)) {
            RemoveCardWidgetFromZone(widget);
            float x = (this.CoupleSlotWidth * (Opponent == true ? -1 : 1)) / 2f;
            slotLayout = cardSlotLayoutPool.newObject();
            slotLayout.initializeSlot(x, AssetsAndResource.CardLength * 40f, ZCoordinateOfZoneCenter, widget, headOrientationOfCard, k1, k2);
            WidgetToSlotMapping.put(widget, slotLayout);
            TransitionSlotFromCoupleToFree.add(slotLayout);
        } else {
            RemoveCardWidgetFromZone(widget);
            float x =(-this.width/2 * (Opponent == true ? -1 : 1)) + (this.CoupleSlotWidth * (Opponent == true ? -1 : 1));
            slotLayout = cardSlotLayoutPool.newObject();
            slotLayout.initializeSlot(x, AssetsAndResource.CardLength * 40f, ZCoordinateOfZoneCenter, widget, headOrientationOfCard, k1, k2);
            WidgetToSlotMapping.put(widget, slotLayout);
            TransitionSlotFromFreeToCouple.add(slotLayout);
        }
    }
}