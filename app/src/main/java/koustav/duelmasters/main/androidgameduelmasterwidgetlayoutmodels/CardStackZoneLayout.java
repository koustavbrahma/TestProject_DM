package koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels;

import java.util.List;

import koustav.duelmasters.main.androidgameduelmasterswidget.WidgetPosition;
import koustav.duelmasters.main.androidgameduelmasterswidget.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayout.Layout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardStackWidget;
import koustav.duelmasters.main.androidgamesframework.Input;

/**
 * Created by Koustav on 7/24/2016.
 */
public class CardStackZoneLayout implements Layout {
    WidgetPosition widgetPosition;
    CardStackWidget cardStackWidget;

    public CardStackZoneLayout() {
        widgetPosition = new WidgetPosition();
        cardStackWidget = null;
    }

    public void InitializeCardStackZoneLayout(float x, float y, float z, float angle, float x_axis, float y_axis, float z_axis,
                                              CardStackWidget cardStackWidget) {
        this.cardStackWidget = cardStackWidget;

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
    }

    @Override
    public void update(float deltaTime, float totalTime) {
        cardStackWidget.update(deltaTime, totalTime);
    }

    @Override
    public void draw() {
        cardStackWidget.setTranslateRotateScale(widgetPosition);
        cardStackWidget.draw();
    }

    @Override
    public WidgetTouchEvent TouchResponse(List<Input.TouchEvent> touchEvents) {
        return cardStackWidget.isTouched(touchEvents);
    }

    public CardStackWidget getCardStackWidget() {
        return cardStackWidget;
    }
}
