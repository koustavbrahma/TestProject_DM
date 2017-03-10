package koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels;

import java.util.ArrayList;
import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.PvPWidgetCoordinator;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetMode;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetPosition;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchFocusLevel;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.Layout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardStackWidget;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgameopenglutil.MatrixHelper;
import koustav.duelmasters.main.androidgamesframework.Input;

/**
 * Created by Koustav on 7/24/2016.
 */
public class CardStackZoneLayout implements Layout {
    enum TouchModeCardStackZone {
        NormalMode,
        ExpandMode,
    }
    TouchModeCardStackZone touchMode;

    PvPWidgetCoordinator coordinator;
    WidgetPosition widgetPosition;
    CardStackWidget cardStackWidget;

    float length;
    boolean ExpandMode;

    public CardStackZoneLayout(PvPWidgetCoordinator coordinator) {
        this.coordinator = coordinator;
        widgetPosition = new WidgetPosition();
        cardStackWidget = null;
    }

    public void InitializeCardStackZoneLayout(float x, float y, float z, float angle, float x_axis, float y_axis, float z_axis,
                                              CardStackWidget cardStackWidget) {
        this.touchMode = TouchModeCardStackZone.NormalMode;

        this.cardStackWidget = cardStackWidget;

        length = y;
        widgetPosition.Centerposition.x = x;
        widgetPosition.Centerposition.y = y;
        widgetPosition.Centerposition.z = z;
        widgetPosition.rotaion.angle = angle;
        widgetPosition.rotaion.x = x_axis;
        widgetPosition.rotaion.y = y_axis;
        widgetPosition.rotaion.z = z_axis;
        widgetPosition.X_scale = 1f;
        widgetPosition.Y_scale = 1f;
        widgetPosition.Z_scale = 1f;

        ExpandMode = false;
    }

    public void setExpandMode(boolean val) {
        this.ExpandMode = val;
    }

    public void ForceShrink() {
        cardStackWidget.setMode(WidgetMode.Normal);
    }

    public ArrayList<CardWidget> getExpandLockCardWidgets() {
        return cardStackWidget.getAndClearCardWidgets();
    }

    public ArrayList<CardWidget> GenerateCardWidgetForFirstNCard(int N) {
        ArrayList<Cards> cardStack = (ArrayList<Cards>)cardStackWidget.getLogicalObject();
        int count = cardStack.size();
        ArrayList<CardWidget> cardWidget = new ArrayList<CardWidget>();
        int limit = (N < count) ? N : count;
        for (int i = 0; i < limit; i++) {
            Cards card = cardStack.get(i);
            if (card.getWidget() != null) {
                throw new RuntimeException("Invalid Condition");
            }
            CardWidget widget = coordinator.newCardWidget();
            coordinator.CoupleWidgetForCard(card, widget);
            WidgetPosition cardPosition = new WidgetPosition();
            cardPosition.Centerposition.x = widgetPosition.Centerposition.x;
            cardPosition.Centerposition.y = widgetPosition.Centerposition.y +
                    (AssetsAndResource.CardLength * count)/2 + (AssetsAndResource.CardLength * (limit - i));
            cardPosition.Centerposition.z = widgetPosition.Centerposition.z;

            ArrayList<GLGeometry.GLAngularRotaion> rotaions = new ArrayList<GLGeometry.GLAngularRotaion>();
            GLGeometry.GLAngularRotaion rotaion;
            if (cardStackWidget.getFlip()) {
                rotaion = new GLGeometry.GLAngularRotaion(180f, 0, 0, 1f);
                rotaions.add(rotaion);
            }
            rotaions.add(widgetPosition.rotaion);
            rotaion = MatrixHelper.getCombinedRotation(rotaions);
            cardPosition.rotaion.angle = rotaion.angle;
            cardPosition.rotaion.x = rotaion.x;
            cardPosition.rotaion.y = rotaion.y;
            cardPosition.rotaion.z = rotaion.z;
            cardPosition.X_scale = 1f;
            cardPosition.Y_scale = 1f;
            cardPosition.Z_scale = 1f;

            widget.setTranslateRotateScale(cardPosition);
            cardWidget.add(widget);
        }
        return cardWidget;
    }

    public void lockGivenCards(ArrayList<Integer> index) {
        cardStackWidget.lockCardsInExpandMode(index, coordinator);
    }

    public boolean IsWidgetInTransition() {
        return cardStackWidget.IsWidgetInTransition();
    }

    @Override
    public void update(float deltaTime, float totalTime) {
        cardStackWidget.update(deltaTime, totalTime);
    }

    @Override
    public void draw() {
        int count = ((ArrayList<Cards>)cardStackWidget.getLogicalObject()).size();
        widgetPosition.Centerposition.y = length + ((AssetsAndResource.CardLength * count) - (AssetsAndResource.CardLength * 40f))/2f;
        widgetPosition.Y_scale = ((float)count)/40f;
        if (count > 0) {
            cardStackWidget.setTranslateRotateScale(widgetPosition);
            cardStackWidget.draw();
        }
    }

    @Override
    public WidgetTouchEvent TouchResponse(List<Input.TouchEvent> touchEvents) {
        int count = ((ArrayList<Cards>)cardStackWidget.getLogicalObject()).size();
        if (count == 0) {
            return null;
        }
        WidgetTouchEvent widgetTouchEvent = cardStackWidget.isTouched(touchEvents);

        if (touchMode == TouchModeCardStackZone.NormalMode) {
            if (ExpandMode && widgetTouchEvent.isTouched && !widgetTouchEvent.isTouchedDown) {
                cardStackWidget.setMode(WidgetMode.Expand);
                widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Medium;
                touchMode = TouchModeCardStackZone.ExpandMode;
                return widgetTouchEvent;
            }

            if (widgetTouchEvent.isTouched) {
                return widgetTouchEvent;
            } else {
                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                return null;
            }
        }

        if (touchMode == TouchModeCardStackZone.ExpandMode) {
            if (widgetTouchEvent.isTouched) {
                widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Medium;
                return widgetTouchEvent;
            } else {
                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                return null;
            }
        }
        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
        return null;
    }

}
