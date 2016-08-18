package koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels;

import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.Layout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.RectangleButtonWidget;
import koustav.duelmasters.main.androidgamesframework.Input;

/**
 * Created by Koustav on 8/18/2016.
 */
public class FixedButtonsLayout implements Layout {
    ButtonSlotLayout pauseButtonLayout;

    public FixedButtonsLayout() {
        pauseButtonLayout = new ButtonSlotLayout();
    }

    public void InitializeFixedButtonLayout(RectangleButtonWidget pauseButton) {
        pauseButtonLayout.intializeButton(-0.85f, 0.85f, 0, pauseButton, 1f, 1f);
    }

    @Override
    public void update(float deltaTime, float totalTime) {

    }

    @Override
    public void draw() {
        pauseButtonLayout.draw();
    }

    @Override
    public WidgetTouchEvent TouchResponse(List<Input.TouchEvent> touchEvents) {
        WidgetTouchEvent widgetTouchEvent = pauseButtonLayout.TouchResponse(touchEvents);

        if (widgetTouchEvent.isTouched) {
            return widgetTouchEvent;
        } else {
            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
        }
        return null;
    }
}
