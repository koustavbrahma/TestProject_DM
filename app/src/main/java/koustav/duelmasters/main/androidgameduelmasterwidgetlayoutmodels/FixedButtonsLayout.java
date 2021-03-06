package koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels;

import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.Layout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.RectangleButtonWidget;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Input;

/**
 * Created by Koustav on 8/18/2016.
 */
public class FixedButtonsLayout implements Layout {
    ButtonSlotLayout pauseButtonLayout;
    ButtonSlotLayout EndTurnButtonLayout;
    ButtonSlotLayout PlayerButtonLayout;
    ButtonSlotLayout OpponentButtonLayout;

    public FixedButtonsLayout() {
        pauseButtonLayout = new ButtonSlotLayout();
        EndTurnButtonLayout = new ButtonSlotLayout();
        PlayerButtonLayout = new ButtonSlotLayout();
        OpponentButtonLayout = new ButtonSlotLayout();
    }

    public void InitializeFixedButtonLayout(RectangleButtonWidget pauseButton, RectangleButtonWidget EndTurnButton,
                                            RectangleButtonWidget PlayerButton, RectangleButtonWidget OpponentButton) {
        pauseButtonLayout.intializeButton(-(0.92f * AssetsAndResource.aspectRatio), 0.85f, 0, pauseButton, 1f, 1f);
        EndTurnButtonLayout.intializeButton((0.92f * AssetsAndResource.aspectRatio), 0.3f, 0, EndTurnButton, 1f, 1f);
        PlayerButtonLayout.intializeButton(-(0.9f * AssetsAndResource.aspectRatio), -0.9f, 0, PlayerButton, 1f, 1f);
        OpponentButtonLayout.intializeButton((0.9f * AssetsAndResource.aspectRatio), 0.9f, 0, OpponentButton, 1f, 1f);
    }

    @Override
    public void update(float deltaTime, float totalTime) {

    }

    @Override
    public void draw() {
        pauseButtonLayout.draw();
        EndTurnButtonLayout.draw();
    }

    @Override
    public WidgetTouchEvent TouchResponse(List<Input.TouchEvent> touchEvents) {
        WidgetTouchEvent widgetTouchEvent = null;
        Input input = AssetsAndResource.game.getInput();
        if (input.isTouchDown(0)) {
            if (input.getNormalizedY(0) > 0.6f) {
                if (input.getNormalizedX(0) < 0) {
                    widgetTouchEvent = pauseButtonLayout.TouchResponse(touchEvents);
                } else {
                    widgetTouchEvent = OpponentButtonLayout.TouchResponse(touchEvents);
                }
            } else {
                if (input.getNormalizedY(0) > -0.5f) {
                    widgetTouchEvent = EndTurnButtonLayout.TouchResponse(touchEvents);
                } else {
                    widgetTouchEvent = PlayerButtonLayout.TouchResponse(touchEvents);
                }
            }
        } else {
            for (int i = 0; i < touchEvents.size(); i++) {
                Input.TouchEvent event = touchEvents.get(i);
                if (event.type == Input.TouchEvent.TOUCH_UP) {
                    if (event.normalizedY > 0.6f) {
                        if (event.normalizedX < 0) {
                            widgetTouchEvent = pauseButtonLayout.TouchResponse(touchEvents);
                        } else {
                            widgetTouchEvent = OpponentButtonLayout.TouchResponse(touchEvents);
                        }
                    } else {
                        if (event.normalizedY > -0.5f) {
                            widgetTouchEvent = EndTurnButtonLayout.TouchResponse(touchEvents);
                        } else {
                            widgetTouchEvent = PlayerButtonLayout.TouchResponse(touchEvents);
                        }
                    }
                    break;
                }
            }
        }

        if (widgetTouchEvent != null) {
            if (widgetTouchEvent.isTouched) {
                return widgetTouchEvent;
            } else {
                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
            }
        }
        return null;
    }
}
