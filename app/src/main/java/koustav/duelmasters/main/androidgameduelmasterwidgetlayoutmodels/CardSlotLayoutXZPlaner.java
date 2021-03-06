package koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.ActiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.InactiveCard;
import koustav.duelmasters.main.androidgameduelmastersutil.GetUtil;
import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl.ViewNodePosition;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.HeadOrientation;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.Layout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglmotionmodels.DriftSystem;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Input;

/**
 * Created by Koustav on 5/1/2016.
 */
public class CardSlotLayoutXZPlaner implements Layout {
    ViewNodePosition TopSlotPosition;
    Hashtable<CardWidget, ViewNodePosition> SlotPositions;
    ViewNodePosition TopWidgetPosition;
    Hashtable<CardWidget, ViewNodePosition> widgetPositions;
    DriftSystem TopDriftSystem;
    Hashtable<CardWidget, DriftSystem> driftSystems;
    CardWidget TopCardWidget;
    ArrayList<CardWidget> cardWidgets;

    ViewNodePosition OldTopSlotPosition;

    boolean Disturbed;
    boolean running;
    boolean Expanded;
    boolean changing;

    CardWidget SelectedCardWidget;
    ArrayList<CardWidget> TouchedWidget;
    ArrayList<WidgetTouchEvent> WidgetTouchEventList;

    float headOrientationAngle;
    float k1;
    float k2;
    float percentageComplete;
    float derivative;
    float length;
    float ExpandLimit_X;

    public CardSlotLayoutXZPlaner() {
        TopSlotPosition = new ViewNodePosition();
        TopWidgetPosition = new ViewNodePosition();
        OldTopSlotPosition = new ViewNodePosition();
        TopDriftSystem = new DriftSystem();
        SlotPositions = new Hashtable<CardWidget, ViewNodePosition>();
        widgetPositions = new Hashtable<CardWidget, ViewNodePosition>();
        driftSystems = new Hashtable<CardWidget, DriftSystem>();
        cardWidgets = new ArrayList<CardWidget>();

        Disturbed = false;
        running = false;
        Expanded = false;
        changing = true;
        TopCardWidget = null;
        SelectedCardWidget = null;
        TouchedWidget = new ArrayList<CardWidget>();
        WidgetTouchEventList = new ArrayList<WidgetTouchEvent>();

        headOrientationAngle = 0f;
        k1 = 0f;
        k2 = 0f;
        percentageComplete = 1.0f;
        derivative = 0f;
        length = 0f;
        ExpandLimit_X = 0f;
    }

    private boolean isSame(ViewNodePosition position1, ViewNodePosition position2) {
        boolean status = true;

        if (position1.Centerposition.x != position2.Centerposition.x) {
            status = false;
        } else if (position1.Centerposition.y != position2.Centerposition.y) {
            status = false;
        } else if (position1.Centerposition.z != position2.Centerposition.z) {
            status = false;
        } else if (position1.rotaion.angle != position2.rotaion.angle) {
            status = false;
        } else if (position1.rotaion.x != position2.rotaion.x) {
            status = false;
        } else if (position1.rotaion.y != position2.rotaion.y) {
            status = false;
        } else if (position1.rotaion.z != position2.rotaion.z) {
            status = false;
        } else if (position1.X_scale != position2.X_scale) {
            status = false;
        } else if (position1.Y_scale != position2.Y_scale) {
            status = false;
        } else if (position1.Z_scale != position2.Z_scale) {
            status = false;
        }

        return status;
    }

    @Override
    public void update(float deltaTime, float totalTime) {
        if (TopCardWidget == null) {
            return;
        }

        InactiveCard card = (InactiveCard) TopCardWidget.getLogicalObject();

        boolean tapped = (card != null) ? GetUtil.IsTapped(card): false;

        if (!running && !Expanded) {
            if (tapped && TopCardWidget.getPosition().rotaion.angle != (90f * (headOrientationAngle - 1f))) {
                TopSlotPosition.rotaion.angle = 90f * (headOrientationAngle - 1f);
                for (int i =0; i < cardWidgets.size(); i++) {
                    CardWidget cardWidget = cardWidgets.get(i);
                    ViewNodePosition widgetPosition = SlotPositions.get(cardWidget);
                    widgetPosition.rotaion.angle = 90f * (headOrientationAngle - 1f);
                    if (((cardWidget.getLogicalObject() instanceof  InactiveCard) ||
                            (cardWidget.getLogicalObject() instanceof ActiveCard)) &&
                            !GetUtil.IsTapped((InactiveCard)cardWidget.getLogicalObject())) {
                        throw new RuntimeException("Inconsistency");
                    }
                }
                Disturbed = true;
            } else if (!tapped && TopCardWidget.getPosition().rotaion.angle != (90f * headOrientationAngle)){
                TopSlotPosition.rotaion.angle = 90f * headOrientationAngle;
                for (int i =0; i < cardWidgets.size(); i++) {
                    CardWidget cardWidget = cardWidgets.get(i);
                    ViewNodePosition widgetPosition = SlotPositions.get(cardWidget);
                    widgetPosition.rotaion.angle = 90f * headOrientationAngle;
                    if (((cardWidget.getLogicalObject() instanceof  InactiveCard) ||
                            (cardWidget.getLogicalObject() instanceof ActiveCard)) &&
                            GetUtil.IsTapped((InactiveCard)cardWidget.getLogicalObject())) {
                        throw new RuntimeException("Inconsistency");
                    }
                }
                Disturbed = true;
            }
        }

        if (Disturbed) {
            TopDriftSystem.setDriftInfo(TopCardWidget.getPosition(), TopSlotPosition, null, null, k1, k2, totalTime);

            for (int i =0; i < cardWidgets.size(); i++) {
                CardWidget cardWidget = cardWidgets.get(i);
                ViewNodePosition widgetSlotPosition = SlotPositions.get(cardWidget);
                DriftSystem driftSystem = driftSystems.get(cardWidget);

                driftSystem.setDriftInfo(cardWidget.getPosition(), widgetSlotPosition, null, null,k1, k2, totalTime);
            }
            Disturbed = false;
            running = true;
            changing = false;
        }

        if (running) {
            boolean stop = true;
            ViewNodePosition widgetPositionUpdate = TopDriftSystem.getUpdatePosition(totalTime);

            percentageComplete = TopDriftSystem.getPercentageComplete(totalTime);
            if (percentageComplete == 1.0f) {
                running = false;
            }

            if (!changing) {
                stop &= isSame(this.TopWidgetPosition, widgetPositionUpdate);
            }
            this.TopWidgetPosition.rotaion.angle = widgetPositionUpdate.rotaion.angle;
            this.TopWidgetPosition.rotaion.x = widgetPositionUpdate.rotaion.x;
            this.TopWidgetPosition.rotaion.y = widgetPositionUpdate.rotaion.y;
            this.TopWidgetPosition.rotaion.z = widgetPositionUpdate.rotaion.z;
            this.TopWidgetPosition.Centerposition.x = widgetPositionUpdate.Centerposition.x;
            this.TopWidgetPosition.Centerposition.y = widgetPositionUpdate.Centerposition.y;
            this.TopWidgetPosition.Centerposition.z = widgetPositionUpdate.Centerposition.z;
            this.TopWidgetPosition.X_scale = widgetPositionUpdate.X_scale;
            this.TopWidgetPosition.Y_scale = widgetPositionUpdate.Y_scale;
            this.TopWidgetPosition.Z_scale = widgetPositionUpdate.Z_scale;

            derivative = TopDriftSystem.getCurrentDerivative(totalTime);

            for (int i = 0; i < cardWidgets.size(); i++) {
                CardWidget cardWidget = cardWidgets.get(i);
                ViewNodePosition widgetPosition = widgetPositions.get(cardWidget);
                DriftSystem driftSystem = driftSystems.get(cardWidget);

                widgetPositionUpdate = driftSystem.getUpdatePosition(totalTime);

                if (!changing) {
                    stop &= isSame(widgetPosition, widgetPositionUpdate);
                }

                widgetPosition.rotaion.angle = widgetPositionUpdate.rotaion.angle;
                widgetPosition.rotaion.x = widgetPositionUpdate.rotaion.x;
                widgetPosition.rotaion.y = widgetPositionUpdate.rotaion.y;
                widgetPosition.rotaion.z = widgetPositionUpdate.rotaion.z;
                widgetPosition.Centerposition.x = widgetPositionUpdate.Centerposition.x;
                widgetPosition.Centerposition.y = widgetPositionUpdate.Centerposition.y;
                widgetPosition.Centerposition.z = widgetPositionUpdate.Centerposition.z;
                widgetPosition.X_scale = widgetPositionUpdate.X_scale;
                widgetPosition.Y_scale = widgetPositionUpdate.Y_scale;
                widgetPosition.Z_scale = widgetPositionUpdate.Z_scale;
            }

            if (!changing && stop && percentageComplete != 0) {
                running = false;
            }

            if (percentageComplete != 0) {
                changing = true;
            }
        } else {
            this.TopWidgetPosition.rotaion.angle = TopSlotPosition.rotaion.angle;
            this.TopWidgetPosition.rotaion.x = TopSlotPosition.rotaion.x;
            this.TopWidgetPosition.rotaion.y = TopSlotPosition.rotaion.y;
            this.TopWidgetPosition.rotaion.z = TopSlotPosition.rotaion.z;
            this.TopWidgetPosition.Centerposition.x = TopSlotPosition.Centerposition.x;
            this.TopWidgetPosition.Centerposition.y = TopSlotPosition.Centerposition.y;
            this.TopWidgetPosition.Centerposition.z = TopSlotPosition.Centerposition.z;
            this.TopWidgetPosition.X_scale = TopSlotPosition.X_scale;
            this.TopWidgetPosition.Y_scale = TopSlotPosition.Y_scale;
            this.TopWidgetPosition.Z_scale = TopSlotPosition.Z_scale;

            for (int i = 0; i < cardWidgets.size(); i++) {
                CardWidget cardWidget = cardWidgets.get(i);
                ViewNodePosition widgetPosition = widgetPositions.get(cardWidget);
                ViewNodePosition slotPosition = SlotPositions.get(cardWidget);

                widgetPosition.rotaion.angle = slotPosition.rotaion.angle;
                widgetPosition.rotaion.x = slotPosition.rotaion.x;
                widgetPosition.rotaion.y = slotPosition.rotaion.y;
                widgetPosition.rotaion.z = slotPosition.rotaion.z;
                widgetPosition.Centerposition.x = slotPosition.Centerposition.x;
                widgetPosition.Centerposition.y = slotPosition.Centerposition.y;
                widgetPosition.Centerposition.z = slotPosition.Centerposition.z;
                widgetPosition.X_scale = slotPosition.X_scale;
                widgetPosition.Y_scale = slotPosition.Y_scale;
                widgetPosition.Z_scale = slotPosition.Z_scale;
            }
        }
    }

    public void DragUpdate(float y) {
        ViewNodePosition widgetPosition;
        ViewNodePosition slotPosition;
        Input input = AssetsAndResource.game.getInput();
        if (!input.isTouchDown(0) || running) {
            return;
        }

        GLGeometry.GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                new GLGeometry.GLRay(new GLGeometry.GLPoint(input.getNearPoint(0).x, input.getNearPoint(0).y, input.getNearPoint(0).z),
                        GLGeometry.GLVectorBetween(input.getNearPoint(0), input.getFarPoint(0))), y);

        if (SelectedCardWidget == null || SelectedCardWidget == TopCardWidget) {
            widgetPosition = TopWidgetPosition;
            slotPosition = TopSlotPosition;
        } else {
            widgetPosition = widgetPositions.get(SelectedCardWidget);
            slotPosition = SlotPositions.get(SelectedCardWidget);
        }
        if ((Math.abs(intersectingPoint.x - slotPosition.Centerposition.x) <= (AssetsAndResource.CardWidth * slotPosition.X_scale)/2 &&
                Math.abs(intersectingPoint.z - slotPosition.Centerposition.z) <= (AssetsAndResource.CardHeight * slotPosition.Z_scale)/2) ||
                (input.TouchType(0) != Input.TouchEvent.TOUCH_DRAGGED)) {
            return;
        }

        widgetPosition.rotaion.angle = slotPosition.rotaion.angle;
        widgetPosition.rotaion.x = slotPosition.rotaion.x;
        widgetPosition.rotaion.y = slotPosition.rotaion.y;
        widgetPosition.rotaion.z = slotPosition.rotaion.z;
        widgetPosition.Centerposition.x = intersectingPoint.x;
        widgetPosition.Centerposition.y = intersectingPoint.y;
        widgetPosition.Centerposition.z = intersectingPoint.z;
        widgetPosition.X_scale = slotPosition.X_scale;
        widgetPosition.Y_scale = slotPosition.Y_scale;
        widgetPosition.Z_scale = slotPosition.Z_scale;
    }

    @Override
    public void draw() {
        if (!Expanded || SelectedCardWidget == TopCardWidget) {
            for (int i = cardWidgets.size() - 1; i >= 0; i--) {
                CardWidget cardWidget = cardWidgets.get(i);
                ViewNodePosition widgetPosition = widgetPositions.get(cardWidget);
                cardWidget.setTranslateRotateScale(widgetPosition);
                cardWidget.draw();
            }

            if (TopCardWidget != null) {
                TopCardWidget.setTranslateRotateScale(TopWidgetPosition);
                TopCardWidget.draw();
            }
        } else {
            int index = cardWidgets.indexOf(SelectedCardWidget);
            for (int i = cardWidgets.size() - 1; i > index; i--) {
                CardWidget cardWidget = cardWidgets.get(i);
                ViewNodePosition widgetPosition = widgetPositions.get(cardWidget);
                cardWidget.setTranslateRotateScale(widgetPosition);
                cardWidget.draw();
            }

            if (TopCardWidget != null) {
                TopCardWidget.setTranslateRotateScale(TopWidgetPosition);
                TopCardWidget.draw();
            }

            for (int i = 0; i <= index; i++) {
                CardWidget cardWidget = cardWidgets.get(i);
                ViewNodePosition widgetPosition = widgetPositions.get(cardWidget);
                cardWidget.setTranslateRotateScale(widgetPosition);
                cardWidget.draw();
            }
        }
    }

    private WidgetTouchEvent TouchResponseInExpandedMode(List<Input.TouchEvent> touchEvents) {
        ViewNodePosition widgetPosition;
        CardWidget cardWidget;
        if (cardWidgets.size() > 0) {
            cardWidget = cardWidgets.get(cardWidgets.size() - 1);
            widgetPosition = widgetPositions.get(cardWidget);
        } else {
            widgetPosition = TopWidgetPosition;
        }
        float centerX = (TopWidgetPosition.Centerposition.x + widgetPosition.Centerposition.x) / 2f;
        float expandLength = TopWidgetPosition.Centerposition.x - widgetPosition.Centerposition.x;
        TouchedWidget.clear();
        WidgetTouchEventList.clear();

        WidgetTouchEvent widgetTouchEvent;
        float gap;
        float expandMaxLength = ExpandLimit_X - OldTopSlotPosition.Centerposition.x;
        gap = expandMaxLength/ (stackCount());
        if (Math.abs(gap) > AssetsAndResource.CardWidth) {
            if (expandMaxLength > 0) {
                gap = AssetsAndResource.CardWidth;
            } else {
                gap = -AssetsAndResource.CardWidth;
            }
        }

        Input input = AssetsAndResource.game.getInput();

        int midPoint = stackCount()/2 + stackCount()%2 -1;

        if (input.isTouchDown(0)) {
            GLGeometry.GLPoint relativeNearPointAfterTrans = input.getNearPoint(0).translate(new GLGeometry.GLVector(-centerX, -TopSlotPosition.Centerposition.y,
                    -TopSlotPosition.Centerposition.z));
            GLGeometry.GLPoint relativeFarPointAfterTrans = input.getFarPoint(0).translate(new GLGeometry.GLVector(-centerX, -TopSlotPosition.Centerposition.y,
                    -TopSlotPosition.Centerposition.z));

            GLGeometry.GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                    new GLGeometry.GLRay(relativeNearPointAfterTrans, GLGeometry.GLVectorBetween(relativeNearPointAfterTrans,
                            relativeFarPointAfterTrans)), 0);
            float width = Math.abs(intersectingPoint.x);
            float height = Math.abs(intersectingPoint.z);

            float x;
            int index;

            if (width <= ((Math.abs(expandLength)/2) + AssetsAndResource.CardWidth/2) && height <= AssetsAndResource.CardHeight/2) {
                x = (intersectingPoint.x + midPoint * gap)/ gap;
                if (x > cardWidgets.size()) {
                    index = 0;
                } else if (x > 0){
                    index = (int) Math.floor(x);
                } else {
                    index = cardWidgets.size();
                }

                for (int i = index; i < cardWidgets.size(); i++) {
                    cardWidget = cardWidgets.get(i);
                    widgetTouchEvent = cardWidget.isTouched(touchEvents);
                    if (widgetTouchEvent.isTouched) {
                        TouchedWidget.add(0, cardWidget);
                        WidgetTouchEventList.add(0, widgetTouchEvent);
                    } else {
                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                        break;
                    }
                }

                boolean ok = true;
                for (int i = index - 1; i >=0; i--) {
                    cardWidget = cardWidgets.get(i);
                    widgetTouchEvent = cardWidget.isTouched(touchEvents);
                    if (widgetTouchEvent.isTouched) {
                        TouchedWidget.add(cardWidget);
                        WidgetTouchEventList.add(widgetTouchEvent);
                    } else {
                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                        ok = false;
                        break;
                    }
                }

                if (ok || TouchedWidget.size() == 0) {
                    widgetTouchEvent = TopCardWidget.isTouched(touchEvents);
                    if (widgetTouchEvent.isTouched) {
                        TouchedWidget.add(TopCardWidget);
                        WidgetTouchEventList.add(widgetTouchEvent);
                    } else {
                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                    }
                }

                if (TouchedWidget.size() > 0) {
                    if (TouchedWidget.contains(SelectedCardWidget)) {
                        int i = TouchedWidget.indexOf(SelectedCardWidget);
                        widgetTouchEvent = WidgetTouchEventList.remove(i);

                        for (i = 0; i < WidgetTouchEventList.size(); i++) {
                            AssetsAndResource.widgetTouchEventPool.free(WidgetTouchEventList.get(i));
                        }

                        TouchedWidget.clear();
                        WidgetTouchEventList.clear();
                        return widgetTouchEvent;
                    } else {
                        boolean wasUnderTheStack = false;
                        if (Math.abs(gap) < AssetsAndResource.CardWidth) {
                            wasUnderTheStack = true;
                        }
                        if (SelectedCardWidget == TopCardWidget) {
                            SelectedCardWidget = TouchedWidget.get(TouchedWidget.size() -1);
                            widgetTouchEvent = WidgetTouchEventList.remove(TouchedWidget.size() - 1);
                            widgetTouchEvent.wasUnderTheStack = wasUnderTheStack;

                            for (int i = 0; i < WidgetTouchEventList.size(); i++) {
                                AssetsAndResource.widgetTouchEventPool.free(WidgetTouchEventList.get(i));
                            }

                            TouchedWidget.clear();
                            WidgetTouchEventList.clear();
                            return widgetTouchEvent;
                        } else {
                            if (cardWidgets.contains(TouchedWidget.get(0))) {
                                int index1 = cardWidgets.indexOf(SelectedCardWidget);
                                int index2 = cardWidgets.indexOf(TouchedWidget.get(0));

                                if (index2 < index1) {
                                    SelectedCardWidget = TouchedWidget.get(0);
                                    widgetTouchEvent = WidgetTouchEventList.remove(0);
                                    widgetTouchEvent.wasUnderTheStack = wasUnderTheStack;

                                    for (int i = 0; i < WidgetTouchEventList.size(); i++) {
                                        AssetsAndResource.widgetTouchEventPool.free(WidgetTouchEventList.get(i));
                                    }

                                    TouchedWidget.clear();
                                    WidgetTouchEventList.clear();
                                    return widgetTouchEvent;
                                } else {
                                    if (!cardWidgets.contains(TouchedWidget.get(TouchedWidget.size() -1))) {
                                        throw new RuntimeException("Invalid condition");
                                    }
                                    SelectedCardWidget = TouchedWidget.get(TouchedWidget.size() -1);
                                    widgetTouchEvent = WidgetTouchEventList.remove(TouchedWidget.size() - 1);
                                    widgetTouchEvent.wasUnderTheStack = wasUnderTheStack;

                                    for (int i = 0; i < WidgetTouchEventList.size(); i++) {
                                        AssetsAndResource.widgetTouchEventPool.free(WidgetTouchEventList.get(i));
                                    }

                                    TouchedWidget.clear();
                                    WidgetTouchEventList.clear();
                                    return widgetTouchEvent;
                                }
                            } else {
                                if (!TouchedWidget.contains(TopCardWidget)) {
                                    throw new RuntimeException("Invalid condition");
                                }
                                SelectedCardWidget = TopCardWidget;
                                int i = TouchedWidget.indexOf(TopCardWidget);

                                widgetTouchEvent = WidgetTouchEventList.remove(i);
                                widgetTouchEvent.wasUnderTheStack = wasUnderTheStack;

                                for (i = 0; i < WidgetTouchEventList.size(); i++) {
                                    AssetsAndResource.widgetTouchEventPool.free(WidgetTouchEventList.get(i));
                                }

                                TouchedWidget.clear();
                                WidgetTouchEventList.clear();
                                return widgetTouchEvent;
                            }
                        }
                    }
                }
            }
        } else {
            WidgetTouchEvent widgetTouchEventOutCome = null;
            Input.TouchEvent event = null;
            for (int j = 0; j < touchEvents.size(); j++) {
                event = touchEvents.get(j);
                if (event.type == Input.TouchEvent.TOUCH_UP) {
                    GLGeometry.GLPoint relativeNearPointAfterTrans = event.nearPoint[0].translate(new GLGeometry.GLVector(-centerX, -TopSlotPosition.Centerposition.y,
                            -TopSlotPosition.Centerposition.z));
                    GLGeometry.GLPoint relativeFarPointAfterTrans = event.farPoint[0].translate(new GLGeometry.GLVector(-centerX, -TopSlotPosition.Centerposition.y,
                            -TopSlotPosition.Centerposition.z));

                    GLGeometry.GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                            new GLGeometry.GLRay(relativeNearPointAfterTrans, GLGeometry.GLVectorBetween(relativeNearPointAfterTrans,
                                    relativeFarPointAfterTrans)), 0);
                    float width = Math.abs(intersectingPoint.x);
                    float height = Math.abs(intersectingPoint.z);

                    float x;
                    int index;

                    if (width <= ((Math.abs(expandLength)/2) + AssetsAndResource.CardWidth/2) && height <= AssetsAndResource.CardHeight/2) {
                        x = (intersectingPoint.x + midPoint * gap)/ gap;
                        if (x > cardWidgets.size()) {
                            index = 0;
                        } else if (x > 0){
                            index = (int) Math.floor(x);
                        } else {
                            index = cardWidgets.size() - 1;
                        }

                        for (int i = index; i < cardWidgets.size(); i++) {
                            cardWidget = cardWidgets.get(i);
                            widgetTouchEvent = cardWidget.isTouched(touchEvents);
                            if (widgetTouchEvent.isTouched) {
                                TouchedWidget.add(0, cardWidget);
                                WidgetTouchEventList.add(0, widgetTouchEvent);
                            } else {
                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                break;
                            }
                        }

                        boolean ok = true;
                        for (int i = index - 1; i >=0; i--) {
                            cardWidget = cardWidgets.get(i);
                            widgetTouchEvent = cardWidget.isTouched(touchEvents);
                            if (widgetTouchEvent.isTouched) {
                                TouchedWidget.add(cardWidget);
                                WidgetTouchEventList.add(widgetTouchEvent);
                            } else {
                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                ok = false;
                                break;
                            }
                        }

                        if (ok || TouchedWidget.size() == 0) {
                            widgetTouchEvent = TopCardWidget.isTouched(touchEvents);
                            if (widgetTouchEvent.isTouched) {
                                TouchedWidget.add(TopCardWidget);
                                WidgetTouchEventList.add(widgetTouchEvent);
                            } else {
                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                            }
                        }

                        if (TouchedWidget.size() > 0) {
                            if (TouchedWidget.contains(SelectedCardWidget)) {
                                int i = TouchedWidget.indexOf(SelectedCardWidget);
                                widgetTouchEvent = WidgetTouchEventList.remove(i);

                                for (i = 0; i < WidgetTouchEventList.size(); i++) {
                                    AssetsAndResource.widgetTouchEventPool.free(WidgetTouchEventList.get(i));
                                }

                                widgetTouchEventOutCome = widgetTouchEvent;
                                break;
                            } else {
                                boolean wasUnderTheStack = false;
                                if (Math.abs(gap) < AssetsAndResource.CardWidth) {
                                    wasUnderTheStack = true;
                                }
                                if (SelectedCardWidget == TopCardWidget) {
                                    SelectedCardWidget = TouchedWidget.get(TouchedWidget.size() -1);
                                    widgetTouchEvent = WidgetTouchEventList.remove(TouchedWidget.size() - 1);
                                    widgetTouchEvent.wasUnderTheStack = wasUnderTheStack;

                                    for (int i = 0; i < WidgetTouchEventList.size(); i++) {
                                        AssetsAndResource.widgetTouchEventPool.free(WidgetTouchEventList.get(i));
                                    }

                                    widgetTouchEventOutCome = widgetTouchEvent;
                                    break;
                                } else {
                                    if (cardWidgets.contains(TouchedWidget.get(0))) {
                                        int index1 = cardWidgets.indexOf(SelectedCardWidget);
                                        int index2 = cardWidgets.indexOf(TouchedWidget.get(0));

                                        if (index2 < index1) {
                                            SelectedCardWidget = TouchedWidget.get(0);
                                            widgetTouchEvent = WidgetTouchEventList.remove(0);
                                            widgetTouchEvent.wasUnderTheStack = wasUnderTheStack;

                                            for (int i = 0; i < WidgetTouchEventList.size(); i++) {
                                                AssetsAndResource.widgetTouchEventPool.free(WidgetTouchEventList.get(i));
                                            }

                                            widgetTouchEventOutCome = widgetTouchEvent;
                                            break;
                                        } else {
                                            if (!cardWidgets.contains(TouchedWidget.get(TouchedWidget.size() -1))) {
                                                throw new RuntimeException("Invalid condition");
                                            }
                                            SelectedCardWidget = TouchedWidget.get(TouchedWidget.size() -1);
                                            widgetTouchEvent = WidgetTouchEventList.remove(TouchedWidget.size() - 1);
                                            widgetTouchEvent.wasUnderTheStack = wasUnderTheStack;

                                            for (int i = 0; i < WidgetTouchEventList.size(); i++) {
                                                AssetsAndResource.widgetTouchEventPool.free(WidgetTouchEventList.get(i));
                                            }

                                            widgetTouchEventOutCome = widgetTouchEvent;
                                            break;
                                        }
                                    } else {
                                        if (!TouchedWidget.contains(TopCardWidget)) {
                                            throw new RuntimeException("Invalid condition");
                                        }
                                        SelectedCardWidget = TopCardWidget;
                                        int i = TouchedWidget.indexOf(TopCardWidget);

                                        widgetTouchEvent = WidgetTouchEventList.remove(i);
                                        widgetTouchEvent.wasUnderTheStack = wasUnderTheStack;

                                        for (i = 0; i < WidgetTouchEventList.size(); i++) {
                                            AssetsAndResource.widgetTouchEventPool.free(WidgetTouchEventList.get(i));
                                        }

                                        widgetTouchEventOutCome = widgetTouchEvent;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (widgetTouchEventOutCome != null) {
                WidgetTouchEventList.clear();
                TouchedWidget.clear();

                return widgetTouchEventOutCome;
            }
        }
        TouchedWidget.clear();
        WidgetTouchEventList.clear();

        widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
        widgetTouchEvent.resetTouchEvent();

        return widgetTouchEvent;
    }

    @Override
    public WidgetTouchEvent TouchResponse(List<Input.TouchEvent> touchEvents) {
        if (TopCardWidget != null && !running) {
            if (!Expanded) {
                return TopCardWidget.isTouched(touchEvents);
            } else {
                return TouchResponseInExpandedMode(touchEvents);
            }
        }

        WidgetTouchEvent widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
        widgetTouchEvent.resetTouchEvent();
        return widgetTouchEvent;
    }

    public void initializeSlot(float x, float y, float z, CardWidget cardWidget, HeadOrientation headOrientation, float k1, float k2) {
        if (headOrientation == HeadOrientation.North) {
            headOrientationAngle = 0f;
        } else if (headOrientation == HeadOrientation.West) {
            headOrientationAngle = 1f;
        } else if (headOrientation == HeadOrientation.South) {
            headOrientationAngle = 2f;
        } else {
            headOrientationAngle = 3f;
        }

        this.k1 = k1;
        this.k2 = k2;

        length = y;

        TopSlotPosition.Centerposition.x = x;
        TopSlotPosition.Centerposition.y = y;
        TopSlotPosition.Centerposition.z = z;
        TopSlotPosition.rotaion.angle = 90f * headOrientationAngle;
        TopSlotPosition.rotaion.x = 0f;
        TopSlotPosition.rotaion.y = 1f;
        TopSlotPosition.rotaion.z = 0f;
        TopSlotPosition.X_scale = 1f;
        TopSlotPosition.Y_scale = 1f;
        TopSlotPosition.Z_scale = 1f;

        TopWidgetPosition.Centerposition.x = cardWidget.getPosition().Centerposition.x;
        TopWidgetPosition.Centerposition.y = cardWidget.getPosition().Centerposition.y;
        TopWidgetPosition.Centerposition.z = cardWidget.getPosition().Centerposition.z;
        TopWidgetPosition.rotaion.angle = cardWidget.getPosition().rotaion.angle;
        TopWidgetPosition.rotaion.x = cardWidget.getPosition().rotaion.x;
        TopWidgetPosition.rotaion.y = cardWidget.getPosition().rotaion.y;
        TopWidgetPosition.rotaion.z = cardWidget.getPosition().rotaion.z;
        TopWidgetPosition.X_scale = cardWidget.getPosition().X_scale;
        TopWidgetPosition.Y_scale = cardWidget.getPosition().Y_scale;
        TopWidgetPosition.Z_scale = cardWidget.getPosition().Z_scale;

        this.TopCardWidget = cardWidget;

        Disturbed = true;
        Expanded = false;
    }

    public boolean pushWidget(CardWidget widget) {
        if (TopCardWidget == null) {
            throw new RuntimeException("While pushing TopCardWidget cannot be null");
        }

        if (Expanded) {
            return false;
        }
        cardWidgets.add(0, TopCardWidget);
        ViewNodePosition cardWidgetPosition = new ViewNodePosition();
        cardWidgetPosition.Centerposition.x = TopCardWidget.getPosition().Centerposition.x;
        cardWidgetPosition.Centerposition.y = TopCardWidget.getPosition().Centerposition.y;
        cardWidgetPosition.Centerposition.z = TopCardWidget.getPosition().Centerposition.z;
        cardWidgetPosition.rotaion.angle = TopCardWidget.getPosition().rotaion.angle;
        cardWidgetPosition.rotaion.x = TopCardWidget.getPosition().rotaion.x;
        cardWidgetPosition.rotaion.y = TopCardWidget.getPosition().rotaion.y;
        cardWidgetPosition.rotaion.z = TopCardWidget.getPosition().rotaion.z;
        cardWidgetPosition.X_scale = TopCardWidget.getPosition().X_scale;
        cardWidgetPosition.Y_scale = TopCardWidget.getPosition().Y_scale;
        cardWidgetPosition.Z_scale = TopCardWidget.getPosition().Z_scale;
        widgetPositions.put(TopCardWidget, cardWidgetPosition);

        ViewNodePosition slotPosition = new ViewNodePosition();
        slotPosition.Centerposition.y = TopSlotPosition.Centerposition.y;
        slotPosition.Centerposition.z = TopSlotPosition.Centerposition.z;
        slotPosition.rotaion.angle = TopSlotPosition.rotaion.angle;
        slotPosition.rotaion.x = TopSlotPosition.rotaion.x;
        slotPosition.rotaion.y = TopSlotPosition.rotaion.y;
        slotPosition.rotaion.z = TopSlotPosition.rotaion.z;
        slotPosition.X_scale = TopSlotPosition.X_scale;
        slotPosition.Y_scale = TopSlotPosition.Y_scale;
        slotPosition.Z_scale = TopSlotPosition.Z_scale;
        SlotPositions.put(TopCardWidget, slotPosition);

        DriftSystem driftSystem = new DriftSystem();
        driftSystems.put(TopCardWidget, driftSystem);

        TopCardWidget = widget;

        TopSlotPosition.Centerposition.y = AssetsAndResource.CardLength * cardWidgets.size() + length;

        for (int i = 0; i < cardWidgets.size(); i++) {
            CardWidget cardWidget = cardWidgets.get(i);
            ViewNodePosition widgetPosition = SlotPositions.get(cardWidget);

            widgetPosition.Centerposition.x = TopSlotPosition.Centerposition.x + (i+ 1) * AssetsAndResource.CardStackShift;
        }

        Disturbed = true;
        return true;
    }

    public CardWidget popWidget() {
        if (cardWidgets.size() == 0) {
            return null;
        }

        CardWidget cardWidget = TopCardWidget;

        if (SelectedCardWidget == TopCardWidget) {
            SelectedCardWidget = cardWidgets.get(0);
        }
        TopCardWidget = cardWidgets.remove(0);
        widgetPositions.remove(TopCardWidget);
        SlotPositions.remove(TopCardWidget);
        driftSystems.remove(TopCardWidget);

        if (!Expanded) {
            TopSlotPosition.Centerposition.y = AssetsAndResource.CardLength * cardWidgets.size() + length;

            for (int i = 0; i < cardWidgets.size(); i++) {
                CardWidget cardWidget2 = cardWidgets.get(i);
                ViewNodePosition widgetPosition = SlotPositions.get(cardWidget2);
                widgetPosition.Centerposition.x = TopSlotPosition.Centerposition.x + (i + 1) * AssetsAndResource.CardStackShift;
            }
        } else {
            float gap;
            float expandMaxLength = ExpandLimit_X - OldTopSlotPosition.Centerposition.x;
            gap = expandMaxLength/ (stackCount());
            if (Math.abs(gap) > AssetsAndResource.CardWidth) {
                if (expandMaxLength > 0) {
                    gap = AssetsAndResource.CardWidth;
                } else {
                    gap = -AssetsAndResource.CardWidth;
                }
            }

            for (int i = 0; i < cardWidgets.size(); i++) {
                CardWidget cardWidget2 = cardWidgets.get(i);
                ViewNodePosition widgetPosition = SlotPositions.get(cardWidget2);
                widgetPosition.Centerposition.x = OldTopSlotPosition.Centerposition.x + (cardWidgets.size() - 1 - i) * gap;
            }

            TopSlotPosition.Centerposition.x = OldTopSlotPosition.Centerposition.x + cardWidgets.size() * gap;
        }

        Disturbed = true;
        return cardWidget;
    }

    public CardWidget removeCardWidget(CardWidget widget) {
        if (widget == TopCardWidget) {
            CardWidget cardWidget = popWidget();

            if (cardWidget != null) {
                Disturbed = true;
                return cardWidget;
            } else {
                return null;
            }
        }

        int index = cardWidgets.indexOf(widget);
        if (index == -1) {
            return null;
        }

        if (Expanded) {
            if (widget == SelectedCardWidget) {
                if (index == 0) {
                    SelectedCardWidget = TopCardWidget;
                } else {
                    SelectedCardWidget = cardWidgets.get(index - 1);
                }
            }
        }

        CardWidget cardWidget = cardWidgets.remove(index);
        widgetPositions.remove(cardWidget);
        SlotPositions.remove(cardWidget);
        driftSystems.remove(cardWidget);

        if (!Expanded) {
            TopSlotPosition.Centerposition.y = AssetsAndResource.CardLength * cardWidgets.size() + length;

            for (int i = 0; i < cardWidgets.size(); i++) {
                CardWidget cardWidget2 = cardWidgets.get(i);
                ViewNodePosition widgetPosition = SlotPositions.get(cardWidget2);
                widgetPosition.Centerposition.x = TopSlotPosition.Centerposition.x + (i + 1) * AssetsAndResource.CardStackShift;
                widgetPosition.Centerposition.y = AssetsAndResource.CardLength * (cardWidgets.size() - 1 - i) + length;
            }
        } else {
            float gap;
            float expandMaxLength = ExpandLimit_X - OldTopSlotPosition.Centerposition.x;
            gap = expandMaxLength/ (stackCount());
            if (Math.abs(gap) > AssetsAndResource.CardWidth) {
                if (expandMaxLength > 0) {
                    gap = AssetsAndResource.CardWidth;
                } else {
                    gap = -AssetsAndResource.CardWidth;
                }
            }

            for (int i = 0; i < cardWidgets.size(); i++) {
                CardWidget cardWidget2 = cardWidgets.get(i);
                ViewNodePosition widgetPosition = SlotPositions.get(cardWidget2);
                widgetPosition.Centerposition.x = OldTopSlotPosition.Centerposition.x + (cardWidgets.size() - 1 - i) * gap;
            }

            TopSlotPosition.Centerposition.x = OldTopSlotPosition.Centerposition.x + cardWidgets.size() * gap;
        }

        Disturbed = true;

        return cardWidget;
    }

    public boolean ExpandOrShrinkSlot(boolean Expand, float y, float Expand_X_Limit) {
        if (Expand == this.Expanded) {
            return false;
        }

        if (TopCardWidget == null || (stackCount() == 1 && Expand)) {
            return false;
        }

        this.Expanded = Expand;
        Disturbed = true;
        this.ExpandLimit_X = Expand_X_Limit;

        if (Expanded) {
            SelectedCardWidget = TopCardWidget;

            OldTopSlotPosition.Centerposition.x = TopSlotPosition.Centerposition.x;
            OldTopSlotPosition.Centerposition.y = TopSlotPosition.Centerposition.y;
            OldTopSlotPosition.Centerposition.z = TopSlotPosition.Centerposition.z;
            OldTopSlotPosition.rotaion.angle = TopSlotPosition.rotaion.angle;
            OldTopSlotPosition.rotaion.x = TopSlotPosition.rotaion.x;
            OldTopSlotPosition.rotaion.y = TopSlotPosition.rotaion.y;
            OldTopSlotPosition.rotaion.z = TopSlotPosition.rotaion.z;
            OldTopSlotPosition.X_scale = TopSlotPosition.X_scale;
            OldTopSlotPosition.Y_scale = TopSlotPosition.Y_scale;
            OldTopSlotPosition.Z_scale = TopSlotPosition.Z_scale;

            float gap;
            float expandMaxLength = ExpandLimit_X - OldTopSlotPosition.Centerposition.x;
            gap = expandMaxLength/ (stackCount());
            if (Math.abs(gap) > AssetsAndResource.CardWidth) {
                if (expandMaxLength > 0) {
                    gap = AssetsAndResource.CardWidth;
                } else {
                    gap = -AssetsAndResource.CardWidth;
                }
            }

            for (int i = 0; i < cardWidgets.size(); i++) {
                CardWidget cardWidget = cardWidgets.get(i);
                ViewNodePosition widgetPosition = SlotPositions.get(cardWidget);

                widgetPosition.Centerposition.x = OldTopSlotPosition.Centerposition.x + (cardWidgets.size() - 1 - i) * gap;
                widgetPosition.Centerposition.y = y;
                widgetPosition.Centerposition.z = OldTopSlotPosition.Centerposition.z;
                widgetPosition.rotaion.angle = 90f * headOrientationAngle;
            }

            TopSlotPosition.Centerposition.x = OldTopSlotPosition.Centerposition.x + cardWidgets.size() * gap;
            TopSlotPosition.Centerposition.y = y;
            TopSlotPosition.Centerposition.z = OldTopSlotPosition.Centerposition.z;
            TopSlotPosition.rotaion.angle = 90f * headOrientationAngle;
        } else {
            SelectedCardWidget = null;
            TopSlotPosition.Centerposition.x = OldTopSlotPosition.Centerposition.x;
            TopSlotPosition.Centerposition.y =  AssetsAndResource.CardLength * cardWidgets.size() + length;
            TopSlotPosition.Centerposition.z = OldTopSlotPosition.Centerposition.z;
            TopSlotPosition.rotaion.angle = OldTopSlotPosition.rotaion.angle;

            for (int i = 0; i < cardWidgets.size(); i++) {
                CardWidget cardWidget = cardWidgets.get(i);
                ViewNodePosition widgetPosition = SlotPositions.get(cardWidget);

                widgetPosition.Centerposition.x = OldTopSlotPosition.Centerposition.x + (i+ 1) * AssetsAndResource.CardStackShift;;
                widgetPosition.Centerposition.y = AssetsAndResource.CardLength * (cardWidgets.size() - 1 - i) + length;
                widgetPosition.Centerposition.z = OldTopSlotPosition.Centerposition.z;
                widgetPosition.rotaion.angle = OldTopSlotPosition.rotaion.angle;
            }
        }

        return true;
    }

    public void resetSlot() {
        this.TopCardWidget = null;
        SlotPositions.clear();
        widgetPositions.clear();
        driftSystems.clear();
        cardWidgets.clear();
        Expanded = false;
        running = false;
        changing = true;
        SelectedCardWidget = null;
    }

    public void setSlotXPosition(float x) {
        if (!Expanded) {
            TopSlotPosition.Centerposition.x = x;

            for (int i = 0; i < cardWidgets.size(); i++) {
                CardWidget cardWidget = cardWidgets.get(i);
                ViewNodePosition widgetPosition = SlotPositions.get(cardWidget);

                widgetPosition.Centerposition.x = x + (i + 1) * AssetsAndResource.CardStackShift;
            }

            Disturbed = true;
        } else {
            OldTopSlotPosition.Centerposition.x = x;
        }
    }

    public CardWidget getCardWidget() {
        return (SelectedCardWidget == null) ? TopCardWidget : SelectedCardWidget;
    }

    public int stackCount() {
        return (1 + cardWidgets.size());
    }

    public boolean IsTransition() {
        return running;
    }

    public float getTopWidgetXPosition() {
        return TopCardWidget.getPosition().Centerposition.x;
    }

    public float getTopWidgetYPosition() {
        return TopCardWidget.getPosition().Centerposition.y;
    }

    public float getTopWidgetZPosition() {
        return TopCardWidget.getPosition().Centerposition.z;
    }

    public float getPercentageComplete() {
        return percentageComplete;
    }

    public float getDerivative() {
        return derivative;
    }

    public void UpdateTopWidgetPosition() {
        TopCardWidget.setTranslateRotateScale(TopWidgetPosition);
    }

    public void SetSlotDisturbed() {
        this.Disturbed = true;
    }

    public ArrayList<CardWidget> getCardWidgetsStack() {
        ArrayList<CardWidget> widgets = new ArrayList<CardWidget>();

        widgets.add(TopCardWidget);

        for (int i = 0; i< cardWidgets.size(); i++) {
            widgets.add(cardWidgets.get(i));
        }
        return widgets;
    }
}
