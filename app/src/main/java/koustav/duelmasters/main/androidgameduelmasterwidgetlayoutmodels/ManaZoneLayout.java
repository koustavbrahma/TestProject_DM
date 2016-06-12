package koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmasterswidget.WidgetTouchEvent;
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
    Pool<CardSlotLayout> cardSlotLayoutPool;

    ArrayList<CardSlotLayout> LeftWingOfCardSlot;
    ArrayList<CardSlotLayout> RightWingOfCardSlot;
    ArrayList<CardSlotLayout> CoupleCardSlot;
    ArrayList<CardSlotLayout> UnderTransitionSlot;
    CardSlotLayout HeadCardSlot;
    CardSlotLayout SelectedCardSlot;
    CardSlotLayout SelectedCoupleCardSlot;
    CardSlotLayout NewCoupleSlot;
    CardSlotLayout DraggingSlot;

    Hashtable<CardWidget, CardSlotLayout> WidgetToSlotMapping;

    float ZCoordinateOfZoneCenter;
    HeadOrientation headOrientationOfCard;
    boolean Opponent;
    float width;
    float height;
    float CoupleSlotWidth;

    ArrayList<CardSlotLayout> TouchedSlots;
    ArrayList<WidgetTouchEvent> widgetTouchEventList;

    boolean DragMode;
    boolean Dragging;

    public ManaZoneLayout() {
        Pool.PoolObjectFactory<CardSlotLayout> factory = new Pool.PoolObjectFactory<CardSlotLayout>() {
            @Override
            public CardSlotLayout createObject() {
                return new CardSlotLayout();
            }
        };

        ZCoordinateOfZoneCenter = 0;
        headOrientationOfCard = HeadOrientation.North;
        this.width = 0;
        this.height = 0;
        CoupleSlotWidth = 0;
        this.Opponent = false;

        cardSlotLayoutPool = new Pool<CardSlotLayout>(factory, 40);

        LeftWingOfCardSlot = new ArrayList<CardSlotLayout>();
        RightWingOfCardSlot = new ArrayList<CardSlotLayout>();
        CoupleCardSlot = new ArrayList<CardSlotLayout>();
        UnderTransitionSlot = new ArrayList<CardSlotLayout>();
        SelectedCardSlot = null;
        SelectedCoupleCardSlot = null;
        HeadCardSlot = null;
        NewCoupleSlot = null;
        DraggingSlot = null;

        WidgetToSlotMapping = new Hashtable<CardWidget, CardSlotLayout>();

        TouchedSlots = new ArrayList<CardSlotLayout>();
        widgetTouchEventList = new ArrayList<WidgetTouchEvent>();

        DragMode = false;
        Dragging = false;
    }

    public void InitializeBattleZoneLayout(float zCoordinateOfZoneCenter, float width, float height,
                                           HeadOrientation orientation, boolean opponent) {
        ZCoordinateOfZoneCenter = zCoordinateOfZoneCenter;
        headOrientationOfCard = orientation;
        this.width = width;
        this.height = height;
        this.Opponent = opponent;
    }

    public void SetDraggingMode(boolean val) {
        DragMode = val;
    }

    public boolean FreeManaCardOverlapping() {
        float gap = (this.width - this.CoupleSlotWidth)/(1f + LeftWingOfCardSlot.size() +RightWingOfCardSlot.size());

        if (gap >= 0.125f * this.width) {
            gap = 0.125f * this.width;
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
        if (HeadCardSlot != null) {
            HeadCardSlot.update(deltaTime, totalTime);
        }

        for(int i = 0; i < LeftWingOfCardSlot.size(); i++) {
            CardSlotLayout slotLayout = LeftWingOfCardSlot.get(i);
            slotLayout.update(deltaTime, totalTime);
        }

        for(int i = 0; i < RightWingOfCardSlot.size(); i++) {
            CardSlotLayout slotLayout = RightWingOfCardSlot.get(i);
            slotLayout.update(deltaTime, totalTime);
        }

        for (int i = 0; i < CoupleCardSlot.size(); i++) {
            CardSlotLayout slotLayout = CoupleCardSlot.get(i);
            slotLayout.update(deltaTime, totalTime);
        }

        for (int i = 0; i < UnderTransitionSlot.size(); i++) {
            CardSlotLayout slotLayout = UnderTransitionSlot.get(i);
            slotLayout.update(deltaTime, totalTime);
        }

        if (DraggingSlot != null) {
            DraggingSlot.DragUpdate(AssetsAndResource.CardLength * 40f);
        }
    }

    @Override
    public void draw() {
        if (SelectedCardSlot == HeadCardSlot) {
            for (int i = LeftWingOfCardSlot.size() - 1; i >= 0; i--) {
                CardSlotLayout slotLayout = LeftWingOfCardSlot.get(i);
                slotLayout.draw();
            }

            for (int i = RightWingOfCardSlot.size() - 1; i >= 0; i--) {
                CardSlotLayout slotLayout = RightWingOfCardSlot.get(i);
                slotLayout.draw();
            }

            if (HeadCardSlot != null) {
                HeadCardSlot.draw();
            }
        } else if (LeftWingOfCardSlot.contains(SelectedCardSlot)) {
            int index = LeftWingOfCardSlot.indexOf(SelectedCardSlot);

            for (int i = LeftWingOfCardSlot.size() - 1; i > index; i--) {
                CardSlotLayout slotLayout = LeftWingOfCardSlot.get(i);
                slotLayout.draw();
            }

            for (int i = RightWingOfCardSlot.size() - 1; i >= 0; i--) {
                CardSlotLayout slotLayout = RightWingOfCardSlot.get(i);
                slotLayout.draw();
            }

            HeadCardSlot.draw();

            for (int i = 0; i <= index; i++) {
                CardSlotLayout slotLayout = LeftWingOfCardSlot.get(i);
                slotLayout.draw();
            }
        } else if (RightWingOfCardSlot.contains(SelectedCardSlot)) {
            int index = RightWingOfCardSlot.indexOf(SelectedCardSlot);

            for (int i = LeftWingOfCardSlot.size() - 1; i >= 0; i--) {
                CardSlotLayout slotLayout = LeftWingOfCardSlot.get(i);
                slotLayout.draw();
            }

            HeadCardSlot.draw();

            for (int i = 0; i <index; i++) {
                CardSlotLayout slotLayout = RightWingOfCardSlot.get(i);
                slotLayout.draw();
            }

            for (int i = RightWingOfCardSlot.size() -1; i >= index; i--) {
                CardSlotLayout slotLayout = RightWingOfCardSlot.get(i);
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
            CardSlotLayout slotLayout =  CoupleCardSlot.get(i);
            slotLayout.draw();
        }

        for (int i = CoupleCardSlot.size() - 1; i >= index; i--) {
            CardSlotLayout slotLayout =  CoupleCardSlot.get(i);
            slotLayout.draw();
        }

        for (int i = 0; i < UnderTransitionSlot.size(); i++) {
            CardSlotLayout slotLayout =  CoupleCardSlot.get(i);
            slotLayout.draw();
        }
    }

    @Override
    public WidgetTouchEvent TouchResponse(List<Input.TouchEvent> touchEvents) {
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

        if (gap >= 0.125f * this.width) {
            gap = 0.125f * this.width;
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

            if (Dragging == true) {
                widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
                widgetTouchEvent.isTouched = true;
                widgetTouchEvent.isTouchedDown = true;
                if (input.TouchType(0) == Input.TouchEvent.TOUCH_DRAGGED) {
                    widgetTouchEvent.isMoving = true;
                } else {
                    widgetTouchEvent.isMoving = false;
                }
                widgetTouchEvent.isDoubleTouched = false;
                widgetTouchEvent.object = DraggingSlot.getCardWidget().getLogicalObject();

                return widgetTouchEvent;
            }
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
                        CardSlotLayout slotLayout = LeftWingOfCardSlot.get(i);

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
                        CardSlotLayout slotLayout = RightWingOfCardSlot.get(i);

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
                    CardSlotLayout slotLayout;
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
                        widgetTouchEvent = HeadCardSlot.TouchResponse(touchEvents);
                        if (widgetTouchEvent.isTouched) {
                            TouchedSlots.add(HeadCardSlot);
                            widgetTouchEventList.add(widgetTouchEvent);
                        } else {
                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                            ok = false;
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
                    CardSlotLayout slotLayout;
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
                        widgetTouchEvent = HeadCardSlot.TouchResponse(touchEvents);
                        if (widgetTouchEvent.isTouched) {
                            TouchedSlots.add(0, HeadCardSlot);
                            widgetTouchEventList.add(0, widgetTouchEvent);
                        } else {
                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                            ok = false;
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

                            if (DragMode == true && NewCoupleSlot == SelectedCoupleCardSlot) {
                                DraggingSlot = SelectedCoupleCardSlot;
                                Dragging = true;
                            }

                            return widgetTouchEvent;
                        } else if (index > CoupleCardSlot.indexOf(TouchedSlots.get(TouchedSlots.size() -1))) {
                            SelectedCoupleCardSlot = TouchedSlots.get(TouchedSlots.size() - 1);
                            widgetTouchEvent = widgetTouchEventList.remove(TouchedSlots.size() - 1);

                            for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                            }

                            if (DragMode == true && NewCoupleSlot == SelectedCoupleCardSlot) {
                                DraggingSlot = SelectedCoupleCardSlot;
                                Dragging = true;
                            }

                            return widgetTouchEvent;
                        } else {
                            int i = TouchedSlots.indexOf(SelectedCoupleCardSlot);
                            widgetTouchEvent = widgetTouchEventList.remove(i);

                            for (i = 0; i < widgetTouchEventList.size(); i++) {
                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                            }

                            if (DragMode == true && NewCoupleSlot == SelectedCoupleCardSlot) {
                                DraggingSlot = SelectedCoupleCardSlot;
                                Dragging = true;
                            }

                            return widgetTouchEvent;
                        }
                    } else {
                        if (TouchedSlots.contains(SelectedCardSlot)) {
                            int i = TouchedSlots.indexOf(SelectedCardSlot);
                            widgetTouchEvent = widgetTouchEventList.remove(i);

                            for (i = 0; i < widgetTouchEventList.size(); i++) {
                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                            }

                            if (DragMode == true) {
                                DraggingSlot = SelectedCardSlot;
                                Dragging = true;
                            }
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

                                        if (DragMode == true) {
                                            DraggingSlot = SelectedCardSlot;
                                            Dragging = true;
                                        }

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

                                        if (DragMode == true) {
                                            DraggingSlot = SelectedCardSlot;
                                            Dragging = true;
                                        }

                                        return widgetTouchEvent;
                                    }
                                } else {
                                    SelectedCardSlot = TouchedSlots.get(0);
                                    widgetTouchEvent = widgetTouchEventList.remove(0);

                                    for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                    }

                                    if (DragMode == true) {
                                        DraggingSlot = SelectedCardSlot;
                                        Dragging = true;
                                    }

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

                                        if (DragMode == true) {
                                            DraggingSlot = SelectedCardSlot;
                                            Dragging = true;
                                        }

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

                                        if (DragMode == true) {
                                            DraggingSlot = SelectedCardSlot;
                                            Dragging = true;
                                        }

                                        return widgetTouchEvent;
                                    }
                                } else {
                                    SelectedCardSlot = TouchedSlots.get(TouchedSlots.size() - 1);
                                    widgetTouchEvent = widgetTouchEventList.remove(TouchedSlots.size() - 1);

                                    for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                    }

                                    if (DragMode == true) {
                                        DraggingSlot = SelectedCardSlot;
                                        Dragging = true;
                                    }

                                    return widgetTouchEvent;
                                }
                            } else if (HeadCardSlot == SelectedCardSlot) {
                                if (RightWingOfCardSlot.contains(TouchedSlots.get(0))) {
                                    SelectedCardSlot = TouchedSlots.get(0);
                                    widgetTouchEvent = widgetTouchEventList.remove(0);

                                    for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                    }

                                    if (DragMode == true) {
                                        DraggingSlot = SelectedCardSlot;
                                        Dragging = true;
                                    }

                                    return widgetTouchEvent;
                                } else if (LeftWingOfCardSlot.contains(TouchedSlots.get(TouchedSlots.size() - 1))) {
                                    SelectedCardSlot = TouchedSlots.get(TouchedSlots.size() - 1);
                                    widgetTouchEvent = widgetTouchEventList.remove(TouchedSlots.size() - 1);

                                    for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                    }

                                    if (DragMode == true) {
                                        DraggingSlot = SelectedCardSlot;
                                        Dragging = true;
                                    }

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
                widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
                widgetTouchEvent.isTouched = true;
                widgetTouchEvent.isTouchedDown = false;
                widgetTouchEvent.isMoving = false;
                widgetTouchEvent.isDoubleTouched = false;
                widgetTouchEvent.object = null;

                return widgetTouchEvent;
            }
        }  else {

            if (Dragging == true) {
                Dragging = false;
                widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
                widgetTouchEvent.isTouched = true;
                widgetTouchEvent.isTouchedDown = false;
                widgetTouchEvent.isMoving = false;
                widgetTouchEvent.isDoubleTouched = false;
                widgetTouchEvent.object = DraggingSlot.getCardWidget().getLogicalObject();
                DraggingSlot = null;

                return widgetTouchEvent;
            }

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
                                CardSlotLayout slotLayout = LeftWingOfCardSlot.get(i);

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
                                CardSlotLayout slotLayout = RightWingOfCardSlot.get(i);

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
                            CardSlotLayout slotLayout;
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
                                widgetTouchEvent = HeadCardSlot.TouchResponse(touchEvents);
                                if (widgetTouchEvent.isTouched) {
                                    TouchedSlots.add(HeadCardSlot);
                                    widgetTouchEventList.add(widgetTouchEvent);
                                } else {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                    ok = false;
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
                            CardSlotLayout slotLayout;
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
                                widgetTouchEvent = HeadCardSlot.TouchResponse(touchEvents);
                                if (widgetTouchEvent.isTouched) {
                                    TouchedSlots.add(0, HeadCardSlot);
                                    widgetTouchEventList.add(0, widgetTouchEvent);
                                } else {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                    ok = false;
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
                                    break;
                                } else if (index > CoupleCardSlot.indexOf(TouchedSlots.get(TouchedSlots.size() - 1))) {
                                    SelectedCoupleCardSlot = TouchedSlots.get(TouchedSlots.size() - 1);
                                    widgetTouchEvent = widgetTouchEventList.remove(TouchedSlots.size() - 1);

                                    for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                    }

                                    widgetTouchEventOutCome = widgetTouchEvent;
                                    break;
                                } else {
                                    int i = TouchedSlots.indexOf(SelectedCoupleCardSlot);
                                    widgetTouchEvent = widgetTouchEventList.remove(i);

                                    for (i = 0; i < widgetTouchEventList.size(); i++) {
                                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                    }

                                    widgetTouchEventOutCome = widgetTouchEvent;
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

            if (widgetTouchEventOutCome != null) {
                return widgetTouchEventOutCome;
            } else if (isTouched) {
                widgetTouchEventOutCome = AssetsAndResource.widgetTouchEventPool.newObject();
                widgetTouchEventOutCome.isTouched = true;
                widgetTouchEventOutCome.isTouchedDown = false;
                widgetTouchEventOutCome.isMoving = false;
                widgetTouchEventOutCome.isDoubleTouched = false;
                widgetTouchEventOutCome.object = null;

                return widgetTouchEventOutCome;
            }
        }

        return null;
    }

    public void AddCardWidgetToZone(CardWidget widget) {
        CardSlotLayout slotLayout = cardSlotLayoutPool.newObject();
        slotLayout.initializeSlot(0, 0, ZCoordinateOfZoneCenter, widget, headOrientationOfCard, 2f, 2f);
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

        if (gap >= 0.125f * this.width) {
            gap = 0.125f * this.width;
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

    public ArrayList<CardWidget> RemoveCardWidgetFromZone(CardWidget widget) {
        CardSlotLayout slotLayout = WidgetToSlotMapping.get(widget);
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
                    CardSlotLayout firstRightSlot = RightWingOfCardSlot.remove(0);
                    HeadCardSlot = firstRightSlot;
                } else {
                    HeadCardSlot = null;
                }
            } else {
                CardSlotLayout firstLeftSlot = LeftWingOfCardSlot.remove(0);
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
                CardSlotLayout firstLeftSlot = LeftWingOfCardSlot.remove(0);
                HeadCardSlot = firstLeftSlot;
            } else if (sizeDiff <= -2) {
                LeftWingOfCardSlot.add(0, HeadCardSlot);
                CardSlotLayout firstRightSlot = RightWingOfCardSlot.remove(0);
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
                CardSlotLayout firstLeftSlot = LeftWingOfCardSlot.remove(0);
                HeadCardSlot = firstLeftSlot;
            } else if (sizeDiff <= -2) {
                LeftWingOfCardSlot.add(0, HeadCardSlot);
                CardSlotLayout firstRightSlot = RightWingOfCardSlot.remove(0);
                HeadCardSlot = firstRightSlot;
            }
        } else {
            throw new RuntimeException("Slot object not found in the zone");
        }

        float gap = this.width/(1f + LeftWingOfCardSlot.size() +RightWingOfCardSlot.size());

        if (gap >= 0.125f * this.width) {
            gap = 0.125f * this.width;
        }

        if (HeadCardSlot != null) {
            HeadCardSlot.setSlotXPosition(0);
        }

        for(int i = 0; i < LeftWingOfCardSlot.size(); i++) {
            slotLayout = LeftWingOfCardSlot.get(i);
            slotLayout.setSlotXPosition(gap * (-(i+1)) * (Opponent == true ? -1 : 1));
        }

        for(int i = 0; i < RightWingOfCardSlot.size(); i++) {
            slotLayout = RightWingOfCardSlot.get(i);
            slotLayout.setSlotXPosition(gap * (i+1) * (Opponent == true ? -1 : 1));
        }

        slotLayout = WidgetToSlotMapping.get(widget);
        ArrayList<CardWidget> cardWidgets = new ArrayList<CardWidget>();
        CardWidget popWidget = null;
        do {
            popWidget = slotLayout.popWidget();
            if (popWidget != null) {
                cardWidgets.add(popWidget);
                WidgetToSlotMapping.remove(popWidget);
            }
        } while (popWidget != null);

        cardWidgets.add(slotLayout.getCardWidget());
        WidgetToSlotMapping.remove(slotLayout.getCardWidget());


        slotLayout.resetSlot();
        cardSlotLayoutPool.free(slotLayout);

        return cardWidgets;
    }
}
