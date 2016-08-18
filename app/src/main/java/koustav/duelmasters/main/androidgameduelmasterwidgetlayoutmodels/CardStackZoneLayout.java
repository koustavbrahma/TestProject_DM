package koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels;

import java.util.ArrayList;
import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetMode;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetPosition;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchFocusLevel;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.Layout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardStackWidget;
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

    WidgetPosition widgetPosition;
    CardStackWidget cardStackWidget;

    float length;
    boolean ExpandMode;

    public CardStackZoneLayout() {
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

    public void CloseExpandMode() {
        cardStackWidget.setMode(WidgetMode.Normal);
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
        cardStackWidget.setTranslateRotateScale(widgetPosition);
        cardStackWidget.draw();
    }

    @Override
    public WidgetTouchEvent TouchResponse(List<Input.TouchEvent> touchEvents) {
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
