package koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchFocusLevel;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.HeadOrientation;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.Layout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgamesframework.Input;
import koustav.duelmasters.main.androidgamesframework.Pool;

/**
 * Created by Koustav on 4/27/2016.
 */
public class BattleZoneLayout implements Layout {
    enum TouchModeBattleZone{
        NormalMode,
        SlotExpandMode,
    }
    TouchModeBattleZone touchMode;

    Pool<CardSlotLayoutXZPlaner> cardSlotLayoutPool;

    ArrayList<CardSlotLayoutXZPlaner> LeftWingOfCardSlot;
    ArrayList<CardSlotLayoutXZPlaner> RightWingOfCardSlot;
    CardSlotLayoutXZPlaner HeadCardSlot;
    CardSlotLayoutXZPlaner SelectedCardSlot;

    Hashtable<CardWidget, CardSlotLayoutXZPlaner> WidgetToSlotMapping;

    float ZCoordinateOfZoneCenter;
    HeadOrientation headOrientationOfCard;
    boolean Opponent;
    float width;
    float height;
    float k1;
    float k2;

    boolean ExpandMode;

    ArrayList<CardSlotLayoutXZPlaner> TouchedSlots;
    ArrayList<WidgetTouchEvent> widgetTouchEventList;

    public BattleZoneLayout() {
        Pool.PoolObjectFactory<CardSlotLayoutXZPlaner> factory = new Pool.PoolObjectFactory<CardSlotLayoutXZPlaner>() {
            @Override
            public CardSlotLayoutXZPlaner createObject() {
                return new CardSlotLayoutXZPlaner();
            }
        };
        touchMode = TouchModeBattleZone.NormalMode;

        ZCoordinateOfZoneCenter = 0;
        headOrientationOfCard = HeadOrientation.North;
        this.width = 0;
        this.height = 0;
        k1 = 2f;
        k2 = 2f;

        this.Opponent = false;

        ExpandMode = false;

        cardSlotLayoutPool = new Pool<CardSlotLayoutXZPlaner>(factory, 40);

        LeftWingOfCardSlot = new ArrayList<CardSlotLayoutXZPlaner>();
        RightWingOfCardSlot = new ArrayList<CardSlotLayoutXZPlaner>();
        HeadCardSlot = null;
        SelectedCardSlot = null;

        WidgetToSlotMapping = new Hashtable<CardWidget, CardSlotLayoutXZPlaner>();

        TouchedSlots = new ArrayList<CardSlotLayoutXZPlaner>();
        widgetTouchEventList = new ArrayList<WidgetTouchEvent>();
    }

    public void InitializeBattleZoneLayout(float zCoordinateOfZoneCenter, float width, float height,
                                           HeadOrientation orientation, boolean opponent, float k1, float k2) {
        ZCoordinateOfZoneCenter = zCoordinateOfZoneCenter;
        if (orientation == HeadOrientation.North || orientation == HeadOrientation.South) {
            headOrientationOfCard = orientation;
        } else {
            throw new IllegalArgumentException("can only be north or south");
        }
        this.width = width;
        this.height = height;
        this.Opponent = opponent;
        this.k1 = k1;
        this.k2 = k2;
    }

    public void setExpandMode(boolean val) {
        this.ExpandMode = val;
        if (!val) {
            ForceShrink();
        }
    }

    public void ForceShrink() {
        if (SelectedCardSlot != null && touchMode == TouchModeBattleZone.SlotExpandMode) {
            touchMode = TouchModeBattleZone.NormalMode;
            SelectedCardSlot.ExpandOrShrinkSlot(false, 0f, 0f);
        }
    }

    public boolean IsWidgetInTransition(CardWidget widget) {
        CardSlotLayoutXZPlaner slotLayout = WidgetToSlotMapping.get(widget);
        if (slotLayout == null) {
            return false;
        }

        return slotLayout.IsTransition();
    }

    private boolean BattleZoneCardOverlapping() {
        float gap = (this.width)/(1f + LeftWingOfCardSlot.size() +RightWingOfCardSlot.size());

        if (gap >= AssetsAndResource.CardHeight) {
            gap = AssetsAndResource.CardHeight;
        }

        return  (gap < AssetsAndResource.CardWidth);
    }

    @Override
    public void update(float deltaTime, float totalTime) {
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
    }

    @Override
    public WidgetTouchEvent TouchResponse(List<Input.TouchEvent> touchEvents) {
        if (touchMode == TouchModeBattleZone.NormalMode) {
            return TouchResponseInNormalMode(touchEvents);
        } else if (touchMode == TouchModeBattleZone.SlotExpandMode) {
            return TouchResponseInExpandMode(touchEvents);
        }

        return null;
    }

    private WidgetTouchEvent TouchResponseInExpandMode(List<Input.TouchEvent> touchEvents) {
        WidgetTouchEvent widgetTouchEvent;
        widgetTouchEvent = SelectedCardSlot.TouchResponse(touchEvents);
        Input input = AssetsAndResource.game.getInput();

        if (widgetTouchEvent.isTouched) {
            widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Medium;
        } else {
            boolean touchUp = false;
            Input.TouchEvent event = null;
            for (int j = 0; j < touchEvents.size(); j++) {
                event = touchEvents.get(j);
                if (event.type == Input.TouchEvent.TOUCH_UP) {
                    touchUp = true;
                }
            }
            if (!input.isTouchDown(0) && touchUp) {
                widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Low;
                touchMode = TouchModeBattleZone.NormalMode;
                SelectedCardSlot.ExpandOrShrinkSlot(false, 0f, 0f);
            } else {
                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                widgetTouchEvent = null;
            }
        }
        return widgetTouchEvent;
    }

    private WidgetTouchEvent TouchResponseInNormalMode(List<Input.TouchEvent> touchEvents) {
        if (HeadCardSlot == null) {
            return null;
        }

        if (SelectedCardSlot == null) {
            throw new RuntimeException("SelectedCardSlot is null and HeadCardSlot is not null");
        }

        widgetTouchEventList.clear();
        TouchedSlots.clear();
        WidgetTouchEvent widgetTouchEvent;
        float gap = this.width/(1f + LeftWingOfCardSlot.size() +RightWingOfCardSlot.size());

        if (gap >= AssetsAndResource.CardHeight) {
            gap = AssetsAndResource.CardHeight;
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

            if (width <= ((this.width/2) + AssetsAndResource.CardHeight/2) && height <= this.height/2) {
                x = (intersectingPoint.x / gap) * (Opponent == true ? -1 : 1);
                index = (int) Math.floor(Math.abs(x));
                if (index == 0) {
                    widgetTouchEvent = HeadCardSlot.TouchResponse(touchEvents);
                    if (widgetTouchEvent.isTouched) {
                        TouchedSlots.add(HeadCardSlot);
                        widgetTouchEventList.add(widgetTouchEvent);
                    } else {
                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
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
                    if (TouchedSlots.contains(SelectedCardSlot)) {
                        int i = TouchedSlots.indexOf(SelectedCardSlot);
                        widgetTouchEvent = widgetTouchEventList.remove(i);

                        for (i = 0; i < widgetTouchEventList.size(); i++) {
                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                        }

                        if (ExpandMode && !widgetTouchEvent.isTouchedDown) {
                            if (SelectedCardSlot.stackCount() > 1) {
                                SelectedCardSlot.ExpandOrShrinkSlot(true, AssetsAndResource.CardLength * 40, AssetsAndResource.MazeWidth/2 * (Opponent == true ? -1 : 1));
                                widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Medium;
                                touchMode = TouchModeBattleZone.SlotExpandMode;
                            }
                        }

                        widgetTouchEventList.clear();
                        TouchedSlots.clear();
                        return widgetTouchEvent;
                    } else {
                        boolean wasUnderTheStack = false;
                        if (BattleZoneCardOverlapping()) {
                            wasUnderTheStack = true;
                        }
                        if (LeftWingOfCardSlot.contains(SelectedCardSlot)) {
                            if (LeftWingOfCardSlot.contains(TouchedSlots.get(0))) {
                                int index1 = LeftWingOfCardSlot.indexOf(SelectedCardSlot);
                                int index2 = LeftWingOfCardSlot.indexOf(TouchedSlots.get(0));

                                if (index1 > index2) {
                                    SelectedCardSlot = TouchedSlots.get(0);
                                    widgetTouchEvent = widgetTouchEventList.remove(0);
                                    widgetTouchEvent.wasUnderTheStack = wasUnderTheStack;

                                    for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                    }

                                    if (ExpandMode && !widgetTouchEvent.isTouchedDown) {
                                        if (SelectedCardSlot.stackCount() > 1) {
                                            SelectedCardSlot.ExpandOrShrinkSlot(true, AssetsAndResource.CardLength * 40, AssetsAndResource.MazeWidth/2 * (Opponent == true ? -1 : 1));
                                            widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Medium;
                                            touchMode = TouchModeBattleZone.SlotExpandMode;
                                        }
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
                                    widgetTouchEvent.wasUnderTheStack = wasUnderTheStack;

                                    for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                    }

                                    if (ExpandMode && !widgetTouchEvent.isTouchedDown) {
                                        if (SelectedCardSlot.stackCount() > 1) {
                                            SelectedCardSlot.ExpandOrShrinkSlot(true, AssetsAndResource.CardLength * 40, AssetsAndResource.MazeWidth/2 * (Opponent == true ? -1 : 1));
                                            widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Medium;
                                            touchMode = TouchModeBattleZone.SlotExpandMode;
                                        }
                                    }

                                    widgetTouchEventList.clear();
                                    TouchedSlots.clear();
                                    return widgetTouchEvent;
                                }
                            } else {
                                SelectedCardSlot = TouchedSlots.get(0);
                                widgetTouchEvent = widgetTouchEventList.remove(0);
                                widgetTouchEvent.wasUnderTheStack = wasUnderTheStack;

                                for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                }

                                if (ExpandMode && !widgetTouchEvent.isTouchedDown) {
                                    if (SelectedCardSlot.stackCount() > 1) {
                                        SelectedCardSlot.ExpandOrShrinkSlot(true, AssetsAndResource.CardLength * 40, AssetsAndResource.MazeWidth/2 * (Opponent == true ? -1 : 1));
                                        widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Medium;
                                        touchMode = TouchModeBattleZone.SlotExpandMode;
                                    }
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
                                    widgetTouchEvent.wasUnderTheStack = wasUnderTheStack;

                                    for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                    }

                                    if (ExpandMode && !widgetTouchEvent.isTouchedDown) {
                                        if (SelectedCardSlot.stackCount() > 1) {
                                            SelectedCardSlot.ExpandOrShrinkSlot(true, AssetsAndResource.CardLength * 40, AssetsAndResource.MazeWidth/2 * (Opponent == true ? -1 : 1));
                                            widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Medium;
                                            touchMode = TouchModeBattleZone.SlotExpandMode;
                                        }
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
                                    widgetTouchEvent.wasUnderTheStack = wasUnderTheStack;

                                    for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                    }

                                    if (ExpandMode && !widgetTouchEvent.isTouchedDown) {
                                        if (SelectedCardSlot.stackCount() > 1) {
                                            SelectedCardSlot.ExpandOrShrinkSlot(true, AssetsAndResource.CardLength * 40, AssetsAndResource.MazeWidth/2 * (Opponent == true ? -1 : 1));
                                            widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Medium;
                                            touchMode = TouchModeBattleZone.SlotExpandMode;
                                        }
                                    }

                                    widgetTouchEventList.clear();
                                    TouchedSlots.clear();
                                    return widgetTouchEvent;
                                }
                            } else {
                                SelectedCardSlot = TouchedSlots.get(TouchedSlots.size() - 1);
                                widgetTouchEvent = widgetTouchEventList.remove(TouchedSlots.size() -1);
                                widgetTouchEvent.wasUnderTheStack = wasUnderTheStack;

                                for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                }

                                if (ExpandMode && !widgetTouchEvent.isTouchedDown) {
                                    if (SelectedCardSlot.stackCount() > 1) {
                                        SelectedCardSlot.ExpandOrShrinkSlot(true, AssetsAndResource.CardLength * 40, AssetsAndResource.MazeWidth/2 * (Opponent == true ? -1 : 1));
                                        widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Medium;
                                        touchMode = TouchModeBattleZone.SlotExpandMode;
                                    }
                                }

                                widgetTouchEventList.clear();
                                TouchedSlots.clear();
                                return widgetTouchEvent;
                            }
                        } else if (HeadCardSlot == SelectedCardSlot) {
                            if (RightWingOfCardSlot.contains(TouchedSlots.get(0))) {
                                SelectedCardSlot = TouchedSlots.get(0);
                                widgetTouchEvent = widgetTouchEventList.remove(0);
                                widgetTouchEvent.wasUnderTheStack = wasUnderTheStack;

                                for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                }

                                if (ExpandMode && !widgetTouchEvent.isTouchedDown) {
                                    if (SelectedCardSlot.stackCount() > 1) {
                                        SelectedCardSlot.ExpandOrShrinkSlot(true, AssetsAndResource.CardLength * 40, AssetsAndResource.MazeWidth/2 * (Opponent == true ? -1 : 1));
                                        widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Medium;
                                        touchMode = TouchModeBattleZone.SlotExpandMode;
                                    }
                                }

                                widgetTouchEventList.clear();
                                TouchedSlots.clear();
                                return widgetTouchEvent;
                            } else if (LeftWingOfCardSlot.contains(TouchedSlots.get(TouchedSlots.size() -1))) {
                                SelectedCardSlot = TouchedSlots.get(TouchedSlots.size() - 1);
                                widgetTouchEvent = widgetTouchEventList.remove(TouchedSlots.size() - 1);
                                widgetTouchEvent.wasUnderTheStack = wasUnderTheStack;

                                for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                }

                                if (ExpandMode && !widgetTouchEvent.isTouchedDown) {
                                    if (SelectedCardSlot.stackCount() > 1) {
                                        SelectedCardSlot.ExpandOrShrinkSlot(true, AssetsAndResource.CardLength * 40, AssetsAndResource.MazeWidth/2 * (Opponent == true ? -1 : 1));
                                        widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Medium;
                                        touchMode = TouchModeBattleZone.SlotExpandMode;
                                    }
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

                widgetTouchEventList.clear();
                TouchedSlots.clear();

                widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
                widgetTouchEvent.resetTouchEvent();
                widgetTouchEvent.isTouched = true;
                widgetTouchEvent.isTouchedDown = true;

                return widgetTouchEvent;
            }
        }  else {

            boolean isTouched = false;
            WidgetTouchEvent widgetTouchEventOutCome = null;
            Input.TouchEvent event = null;
            for (int j = 0; j < touchEvents.size(); j++) {
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

                    if (width <= ((this.width / 2) + AssetsAndResource.CardHeight/2) && height <= this.height / 2) {
                        isTouched = true;
                        x = (intersectingPoint.x / gap) * (Opponent == true ? -1 : 1);
                        index = (int) Math.floor(Math.abs(x));
                        if (index == 0) {
                            widgetTouchEvent = HeadCardSlot.TouchResponse(touchEvents);
                            if (widgetTouchEvent.isTouched) {
                                TouchedSlots.add(HeadCardSlot);
                                widgetTouchEventList.add(widgetTouchEvent);
                            } else {
                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
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
                            if (TouchedSlots.contains(SelectedCardSlot)) {
                                int i = TouchedSlots.indexOf(SelectedCardSlot);
                                widgetTouchEvent = widgetTouchEventList.remove(i);

                                for (i = 0; i < widgetTouchEventList.size(); i++) {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                }

                                widgetTouchEventOutCome = widgetTouchEvent;
                                break;
                            } else {
                                boolean wasUnderTheStack = false;
                                if (BattleZoneCardOverlapping()) {
                                    wasUnderTheStack = true;
                                }
                                if (LeftWingOfCardSlot.contains(SelectedCardSlot)) {
                                    if (LeftWingOfCardSlot.contains(TouchedSlots.get(0))) {
                                        int index1 = LeftWingOfCardSlot.indexOf(SelectedCardSlot);
                                        int index2 = LeftWingOfCardSlot.indexOf(TouchedSlots.get(0));

                                        if (index1 > index2) {
                                            SelectedCardSlot = TouchedSlots.get(0);
                                            widgetTouchEvent = widgetTouchEventList.remove(0);
                                            widgetTouchEvent.wasUnderTheStack = wasUnderTheStack;

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
                                            widgetTouchEvent.wasUnderTheStack = wasUnderTheStack;

                                            for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                            }

                                            widgetTouchEventOutCome = widgetTouchEvent;
                                            break;
                                        }
                                    } else {
                                        SelectedCardSlot = TouchedSlots.get(0);
                                        widgetTouchEvent = widgetTouchEventList.remove(0);
                                        widgetTouchEvent.wasUnderTheStack = wasUnderTheStack;

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
                                            widgetTouchEvent.wasUnderTheStack = wasUnderTheStack;

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
                                            widgetTouchEvent.wasUnderTheStack = wasUnderTheStack;

                                            for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                            }

                                            widgetTouchEventOutCome = widgetTouchEvent;
                                            break;
                                        }
                                    } else {
                                        SelectedCardSlot = TouchedSlots.get(TouchedSlots.size() - 1);
                                        widgetTouchEvent = widgetTouchEventList.remove(TouchedSlots.size() - 1);
                                        widgetTouchEvent.wasUnderTheStack = wasUnderTheStack;

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
                                        widgetTouchEvent.wasUnderTheStack = wasUnderTheStack;

                                        for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                        }

                                        widgetTouchEventOutCome = widgetTouchEvent;
                                        break;
                                    } else if (LeftWingOfCardSlot.contains(TouchedSlots.get(TouchedSlots.size() - 1))) {
                                        SelectedCardSlot = TouchedSlots.get(TouchedSlots.size() - 1);
                                        widgetTouchEvent = widgetTouchEventList.remove(TouchedSlots.size() - 1);
                                        widgetTouchEvent.wasUnderTheStack = wasUnderTheStack;

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

            widgetTouchEventList.clear();
            TouchedSlots.clear();

            if (widgetTouchEventOutCome != null) {
                if (ExpandMode) {
                    if (SelectedCardSlot.stackCount() > 1) {
                        SelectedCardSlot.ExpandOrShrinkSlot(true, AssetsAndResource.CardLength * 40, AssetsAndResource.MazeWidth/2 * (Opponent == true ? -1 : 1));
                        widgetTouchEventOutCome.isFocus = WidgetTouchFocusLevel.Medium;
                        touchMode = TouchModeBattleZone.SlotExpandMode;
                    }
                }
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

        float gap = this.width/(1f + LeftWingOfCardSlot.size() +RightWingOfCardSlot.size());

        if (gap >= AssetsAndResource.CardHeight) {
            gap = AssetsAndResource.CardHeight;
        }

        HeadCardSlot.setSlotXPosition(0);

        for(int i = 0; i < LeftWingOfCardSlot.size(); i++) {
            slotLayout = LeftWingOfCardSlot.get(i);
            slotLayout.setSlotXPosition(gap * (-(i+1)) * (Opponent == true ? -1 : 1));
        }

        for(int i = 0; i < RightWingOfCardSlot.size(); i++) {
            slotLayout = RightWingOfCardSlot.get(i);
            slotLayout.setSlotXPosition(gap * (i+1) * (Opponent == true ? -1 : 1));
        }
    }

    public void PutCardWidgetOnTopOfExistingCardWidget(CardWidget newWidget, CardWidget oldWidget) {
        CardSlotLayoutXZPlaner slotLayout = WidgetToSlotMapping.get(oldWidget);
        if (slotLayout.stackCount() == 1) {
            slotLayout.pushWidget(newWidget);
            WidgetToSlotMapping.put(newWidget, slotLayout);
        } else {
            ArrayList<CardWidget> cardWidgets = new ArrayList<CardWidget>();
            CardWidget popWidget = null;
            do {
                popWidget = slotLayout.popWidget();
                if (popWidget != null && popWidget != oldWidget) {
                    cardWidgets.add(0, popWidget);
                } else if (popWidget == oldWidget){
                    slotLayout.pushWidget(popWidget);
                    break;
                }
            } while (popWidget != null);
            slotLayout.pushWidget(newWidget);
            WidgetToSlotMapping.put(newWidget, slotLayout);
            for (int i = 0; i < cardWidgets.size(); i++) {
                slotLayout.pushWidget(cardWidgets.get(i));
            }
        }
    }

    public CardWidget RemoveCardWidgetFromZone(CardWidget widget) {
        CardSlotLayoutXZPlaner slotLayout = WidgetToSlotMapping.get(widget);
        boolean selectedSlotRemoved = false;

        if (slotLayout == null) {
            return null;
        }

        if (SelectedCardSlot == slotLayout) {
            selectedSlotRemoved = true;
        }

        if (slotLayout.stackCount() == 1) {
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

            float gap = this.width / (1f + LeftWingOfCardSlot.size() + RightWingOfCardSlot.size());

            if (gap >= AssetsAndResource.CardHeight) {
                gap = AssetsAndResource.CardHeight;
            }

            if (HeadCardSlot != null) {
                HeadCardSlot.setSlotXPosition(0);
            }

            for (int i = 0; i < LeftWingOfCardSlot.size(); i++) {
                slotLayout = LeftWingOfCardSlot.get(i);
                slotLayout.setSlotXPosition(gap * (-(i + 1)) * (Opponent == true ? -1 : 1));
            }

            for (int i = 0; i < RightWingOfCardSlot.size(); i++) {
                slotLayout = RightWingOfCardSlot.get(i);
                slotLayout.setSlotXPosition(gap * (i + 1) * (Opponent == true ? -1 : 1));
            }
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
        } else if (slotLayout.stackCount() == 1) {
            touchMode = TouchModeBattleZone.NormalMode;
            slotLayout.ExpandOrShrinkSlot(false, 0f, 0f);
        }

        return widget;
    }
}
