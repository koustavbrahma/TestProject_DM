package koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmasterswidget.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayout.HeadOrientation;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayout.Layout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgamesframework.Input;
import koustav.duelmasters.main.androidgamesframework.Pool;

/**
 * Created by Koustav on 4/27/2016.
 */
public class BattleZoneLayout implements Layout {
    Pool<CardSlotLayout> cardSlotLayoutPool;
    ArrayList<CardSlotLayout> LeftWingOfCardSlot;
    ArrayList<CardSlotLayout> RightWingOfCardSlot;
    CardSlotLayout HeadCardSlot;
    Hashtable<CardWidget, CardSlotLayout> WidgetToSlotMapping;
    float ZCoordinateOfZoneCenter;
    HeadOrientation headOrientation;
    float width;
    float height;
    CardSlotLayout SelectedCardSlot;
    ArrayList<CardSlotLayout> TouchedSlots;
    ArrayList<WidgetTouchEvent> widgetTouchEventList;

    public BattleZoneLayout() {
        Pool.PoolObjectFactory<CardSlotLayout> factory = new Pool.PoolObjectFactory<CardSlotLayout>() {
            @Override
            public CardSlotLayout createObject() {
                return new CardSlotLayout();
            }
        };
        ZCoordinateOfZoneCenter = 0;
        headOrientation = HeadOrientation.North;
        this.width = 0;
        this.height = 0;

        cardSlotLayoutPool = new Pool<CardSlotLayout>(factory, 40);

        LeftWingOfCardSlot = new ArrayList<CardSlotLayout>();
        RightWingOfCardSlot = new ArrayList<CardSlotLayout>();
        HeadCardSlot = null;
        WidgetToSlotMapping = new Hashtable<CardWidget, CardSlotLayout>();
        SelectedCardSlot = null;
        TouchedSlots = new ArrayList<CardSlotLayout>();
        widgetTouchEventList = new ArrayList<WidgetTouchEvent>();
    }

    public void InitializeBattleZoneLayout(float zCoordinateOfZoneCenter, float width, float height,
                                           HeadOrientation orientation) {
        ZCoordinateOfZoneCenter = zCoordinateOfZoneCenter;
        headOrientation = orientation;
        this.width = width;
        this.height = height;
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
    }

    @Override
    public WidgetTouchEvent TouchResponse(List<Input.TouchEvent> touchEvents) {
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

        if (gap >= 0.125f * AssetsAndResource.MazeWidth) {
            gap = 0.125f * AssetsAndResource.MazeWidth;
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

            if (width <= this.width/2 && height <= this.height/2) {
                x = intersectingPoint.x / gap;
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
                    CardSlotLayout slotLayout = LeftWingOfCardSlot.get(index - 1);

                    widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                    if (widgetTouchEvent.isTouched) {
                        TouchedSlots.add(slotLayout);
                        widgetTouchEventList.add(widgetTouchEvent);
                    } else {
                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
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
                    CardSlotLayout slotLayout = RightWingOfCardSlot.get(index - 1);

                    widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                    if (widgetTouchEvent.isTouched) {
                        TouchedSlots.add(slotLayout);
                        widgetTouchEventList.add(widgetTouchEvent);
                    } else {
                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
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

                                    return widgetTouchEvent;
                                }
                            } else {
                                SelectedCardSlot = TouchedSlots.get(0);
                                widgetTouchEvent = widgetTouchEventList.remove(0);

                                for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
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

                                    return widgetTouchEvent;
                                }
                            } else {
                                SelectedCardSlot = TouchedSlots.get(TouchedSlots.size() - 1);
                                widgetTouchEvent = widgetTouchEventList.remove(TouchedSlots.size() -1);

                                for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
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

                                return widgetTouchEvent;
                            } else if (LeftWingOfCardSlot.contains(TouchedSlots.get(TouchedSlots.size() -1))) {
                                SelectedCardSlot = TouchedSlots.get(TouchedSlots.size() - 1);
                                widgetTouchEvent = widgetTouchEventList.remove(TouchedSlots.size() - 1);

                                for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
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

                widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
                widgetTouchEvent.isTouched = true;
                widgetTouchEvent.isTouchedDown = false;
                widgetTouchEvent.isDoubleTouched = false;
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

                    if (width <= this.width / 2 && height <= this.height / 2) {
                        isTouched = true;
                        x = intersectingPoint.x / gap;
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
                            CardSlotLayout slotLayout = LeftWingOfCardSlot.get(index - 1);

                            widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                            if (widgetTouchEvent.isTouched) {
                                TouchedSlots.add(slotLayout);
                                widgetTouchEventList.add(widgetTouchEvent);
                            } else {
                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
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
                            CardSlotLayout slotLayout = RightWingOfCardSlot.get(index - 1);

                            widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                            if (widgetTouchEvent.isTouched) {
                                TouchedSlots.add(slotLayout);
                                widgetTouchEventList.add(widgetTouchEvent);
                            } else {
                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
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

            if (widgetTouchEventOutCome != null) {
                return widgetTouchEventOutCome;
            } else if (isTouched) {
                widgetTouchEventOutCome = AssetsAndResource.widgetTouchEventPool.newObject();
                widgetTouchEventOutCome.isTouched = true;
                widgetTouchEventOutCome.isTouchedDown = false;
                widgetTouchEventOutCome.isDoubleTouched = false;
                widgetTouchEventOutCome.object = null;

                return widgetTouchEventOutCome;
            }
        }

        return null;
    }

    public void AddCardWidgetToZone(CardWidget widget) {
        CardSlotLayout slotLayout = cardSlotLayoutPool.newObject();
        slotLayout.initializeSlot(0, 0, ZCoordinateOfZoneCenter, widget, headOrientation);
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

        if (gap >= 0.125f * AssetsAndResource.MazeWidth) {
            gap = 0.125f * AssetsAndResource.MazeWidth;
        }

        HeadCardSlot.setSlotXPosition(0);

        for(int i = 0; i < LeftWingOfCardSlot.size(); i++) {
            slotLayout = LeftWingOfCardSlot.get(i);
            slotLayout.setSlotXPosition(gap * (-(i+1)));
        }

        for(int i = 0; i < RightWingOfCardSlot.size(); i++) {
            slotLayout = RightWingOfCardSlot.get(i);
            slotLayout.setSlotXPosition(gap * (i+1));
        }
    }

    public void RemoveCardWidgetFromZone(CardWidget widget) {
        CardSlotLayout slotLayout = WidgetToSlotMapping.get(widget);
        boolean selectedSlotRemoved = false;

        if (slotLayout == null) {
            return;
        }

        if (SelectedCardSlot == slotLayout) {
            selectedSlotRemoved = true;
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

        if (gap >= 0.125f * AssetsAndResource.MazeWidth) {
            gap = 0.125f * AssetsAndResource.MazeWidth;
        }

        if (HeadCardSlot != null) {
            HeadCardSlot.setSlotXPosition(0);
        }

        for(int i = 0; i < LeftWingOfCardSlot.size(); i++) {
            slotLayout = LeftWingOfCardSlot.get(i);
            slotLayout.setSlotXPosition(gap * (-(i+1)));
        }

        for(int i = 0; i < RightWingOfCardSlot.size(); i++) {
            slotLayout = RightWingOfCardSlot.get(i);
            slotLayout.setSlotXPosition(gap * (i+1));
        }

        slotLayout = WidgetToSlotMapping.remove(widget);
        slotLayout.resetSlot();
        cardSlotLayoutPool.free(slotLayout);
    }
}
