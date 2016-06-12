package koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.InactiveCard;
import koustav.duelmasters.main.androidgameduelmastersutil.GetUtil;
import koustav.duelmasters.main.androidgameduelmasterswidget.WidgetPosition;
import koustav.duelmasters.main.androidgameduelmasterswidget.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayout.HeadOrientation;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayout.Layout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;
import koustav.duelmasters.main.androidgameopenglanimation.DriftSystem;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgamesframework.Input;

/**
 * Created by Koustav on 5/1/2016.
 */
public class CardSlotLayout implements Layout {
    WidgetPosition TopSlotPosition;
    Hashtable<CardWidget, WidgetPosition> SlotPositions;
    WidgetPosition TopWidgetPosition;
    Hashtable<CardWidget, WidgetPosition> widgetPositions;
    DriftSystem TopDriftSystem;
    Hashtable<CardWidget, DriftSystem> driftSystems;
    CardWidget TopCardWidget;
    ArrayList<CardWidget> cardWidgets;
    boolean Disturbed;
    boolean running;
    boolean TappedPreviousValue;
    float headOrientationAngle;
    float k1;
    float k2;

    public CardSlotLayout() {
        TopSlotPosition = new WidgetPosition();
        TopWidgetPosition = new WidgetPosition();
        TopDriftSystem = new DriftSystem();
        SlotPositions = new Hashtable<CardWidget, WidgetPosition>();
        widgetPositions = new Hashtable<CardWidget, WidgetPosition>();
        driftSystems = new Hashtable<CardWidget, DriftSystem>();
        cardWidgets = new ArrayList<CardWidget>();
        Disturbed = false;
        running = false;
        TappedPreviousValue = false;
        TopCardWidget = null;
        headOrientationAngle = 0f;
        k1 = 0f;
        k2 = 0f;
    }

    @Override
    public void update(float deltaTime, float totalTime) {
        if (TopCardWidget == null) {
            return;
        }

        InactiveCard card = (InactiveCard) TopCardWidget.getLogicalObject();

        boolean tapped = (card != null) ? GetUtil.IsTapped(card): false;

        if (tapped != TappedPreviousValue) {
            TappedPreviousValue = tapped;
            if (tapped) {
                TopSlotPosition.rotaion.angle = 90f * (headOrientationAngle - 1f);
            } else {
                TopSlotPosition.rotaion.angle = 90f * headOrientationAngle;
            }

            for (int i =0; i < cardWidgets.size(); i++) {
                CardWidget cardWidget = cardWidgets.get(i);
                WidgetPosition widgetPosition = SlotPositions.get(cardWidget);

                if (tapped) {
                    widgetPosition.rotaion.angle = 90f * (headOrientationAngle - 1f);
                } else {
                    widgetPosition.rotaion.angle = 90f * headOrientationAngle;
                }
            }
            Disturbed = true;
        }

        if (Disturbed) {
            TopDriftSystem.setDriftInfo(TopCardWidget.getPosition(), TopSlotPosition, k1, k2, totalTime);

            for (int i =0; i < cardWidgets.size(); i++) {
                CardWidget cardWidget = cardWidgets.get(i);
                WidgetPosition widgetPosition = SlotPositions.get(cardWidget);
                DriftSystem driftSystem = driftSystems.get(cardWidget);

                driftSystem.setDriftInfo(cardWidget.getPosition(), widgetPosition, k1, k2, totalTime);
            }
            Disturbed = false;
            running = true;
        }

        if (running) {
            WidgetPosition widgetPositionUpdate = TopDriftSystem.getUpdatePosition(totalTime);

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

            float percentageComplete = TopDriftSystem.getPercentageComplete(totalTime);
            if (percentageComplete == 1.0f) {
                running = false;
            }

            for (int i = 0; i < cardWidgets.size(); i++) {
                CardWidget cardWidget = cardWidgets.get(i);
                WidgetPosition widgetPosition = widgetPositions.get(cardWidget);
                DriftSystem driftSystem = driftSystems.get(cardWidget);

                widgetPositionUpdate = driftSystem.getUpdatePosition(totalTime);

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
                WidgetPosition widgetPosition = widgetPositions.get(cardWidget);
                WidgetPosition slotPosition = SlotPositions.get(cardWidget);

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
        Input input = AssetsAndResource.game.getInput();
        if (!input.isTouchDown(0) || running) {
            return;
        }

        GLGeometry.GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                new GLGeometry.GLRay(new GLGeometry.GLPoint(input.getNearPoint(0).x, input.getNearPoint(0).y, input.getNearPoint(0).z),
                        GLGeometry.GLVectorBetween(input.getNearPoint(0), input.getFarPoint(0))), y);

        if (Math.abs(intersectingPoint.x - TopSlotPosition.Centerposition.x) <= (AssetsAndResource.CardWidth * TopSlotPosition.X_scale)/2 &&
                Math.abs(intersectingPoint.z - TopSlotPosition.Centerposition.z) <= (AssetsAndResource.CardHeight * TopSlotPosition.Z_scale)/2) {
            return;
        }

        this.TopWidgetPosition.rotaion.angle = TopSlotPosition.rotaion.angle;
        this.TopWidgetPosition.rotaion.x = TopSlotPosition.rotaion.x;
        this.TopWidgetPosition.rotaion.y = TopSlotPosition.rotaion.y;
        this.TopWidgetPosition.rotaion.z = TopSlotPosition.rotaion.z;
        this.TopWidgetPosition.Centerposition.x = intersectingPoint.x;
        this.TopWidgetPosition.Centerposition.y = intersectingPoint.y;
        this.TopWidgetPosition.Centerposition.z = intersectingPoint.z;
        this.TopWidgetPosition.X_scale = TopSlotPosition.X_scale;
        this.TopWidgetPosition.Y_scale = TopSlotPosition.Y_scale;
        this.TopWidgetPosition.Z_scale = TopSlotPosition.Z_scale;
    }

    @Override
    public void draw() {
        for (int i = cardWidgets.size() -1; i >= 0 ; i--) {
            CardWidget cardWidget = cardWidgets.get(i);
            WidgetPosition widgetPosition = widgetPositions.get(cardWidget);
            cardWidget.setTranslateRotateScale(widgetPosition);
            cardWidget.draw();
        }

        if (TopCardWidget != null) {
            TopCardWidget.setTranslateRotateScale(TopWidgetPosition);
            TopCardWidget.draw();
        }
    }

    @Override
    public WidgetTouchEvent TouchResponse(List<Input.TouchEvent> touchEvents) {
        if (TopCardWidget != null && !running) {
            return TopCardWidget.isTouched(touchEvents);
        }

        WidgetTouchEvent widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
        widgetTouchEvent.isTouched = false;
        widgetTouchEvent.isTouchedDown = false;
        widgetTouchEvent.isMoving = false;
        widgetTouchEvent.isDoubleTouched = false;
        widgetTouchEvent.object = null;
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

        this.TopCardWidget = cardWidget;

        Disturbed = true;
    }

    public void pushWidget(CardWidget widget) {
        if (TopCardWidget == null) {
            throw new RuntimeException("While pushing TopCardWidget cannot be null");
        }

        cardWidgets.add(0, TopCardWidget);
        WidgetPosition cardWidgetPosition = new WidgetPosition();
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

        WidgetPosition slotPosition = new WidgetPosition();
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

        TopSlotPosition.Centerposition.y = AssetsAndResource.CardLength * cardWidgets.size();

        for (int i = 0; i < cardWidgets.size(); i++) {
            CardWidget cardWidget = cardWidgets.get(i);
            WidgetPosition widgetPosition = SlotPositions.get(cardWidget);

            widgetPosition.Centerposition.x = TopSlotPosition.Centerposition.x + (i+ 1) * AssetsAndResource.CardStackShift;
        }

        Disturbed = true;
    }

    public CardWidget popWidget() {
        if (cardWidgets.size() == 0) {
            return null;
        }

        CardWidget cardWidget = TopCardWidget;

        TopCardWidget = cardWidgets.remove(0);
        widgetPositions.remove(TopCardWidget);
        SlotPositions.remove(TopCardWidget);
        driftSystems.remove(TopCardWidget);

        for (int i = 0; i < cardWidgets.size(); i++) {
            CardWidget cardWidget2 = cardWidgets.get(i);
            WidgetPosition widgetPosition = SlotPositions.get(cardWidget2);

            widgetPosition.Centerposition.x = TopSlotPosition.Centerposition.x + (i+ 1) * AssetsAndResource.CardStackShift;
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

        CardWidget cardWidget = cardWidgets.remove(index);
        widgetPositions.remove(cardWidget);
        SlotPositions.remove(cardWidget);
        driftSystems.remove(cardWidget);

        for (int i = 0; i < cardWidgets.size(); i++) {
            CardWidget cardWidget2 = cardWidgets.get(i);
            WidgetPosition widgetPosition = SlotPositions.get(cardWidget2);

            widgetPosition.Centerposition.x = TopSlotPosition.Centerposition.x + (i+ 1) * AssetsAndResource.CardStackShift;
        }

        Disturbed = true;

        return cardWidget;
    }

    public void resetSlot() {
        this.TopCardWidget = null;
        SlotPositions.clear();
        widgetPositions.clear();
        driftSystems.clear();
        cardWidgets.clear();
    }

    public void setSlotXPosition(float x) {
        TopSlotPosition.Centerposition.x = x;

        for (int i = 0; i < cardWidgets.size(); i++) {
            CardWidget cardWidget = cardWidgets.get(i);
            WidgetPosition widgetPosition = SlotPositions.get(cardWidget);

            widgetPosition.Centerposition.x = x + (i+ 1) * AssetsAndResource.CardStackShift;
        }

        Disturbed = true;
    }

    public CardWidget getCardWidget() {
        return TopCardWidget;
    }

    public int stackCount() {
        return (1 + cardWidgets.size());
    }

    public boolean IsTransition() {
        return running;
    }
}
