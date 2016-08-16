package koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels;

import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetPosition;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchFocusLevel;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.Layout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.RectangleButtonWidget;
import koustav.duelmasters.main.androidgameopenglanimation.DriftSystem;
import koustav.duelmasters.main.androidgamesframework.Input;

/**
 * Created by Koustav on 8/3/2016.
 */
public class ButtonSlotLayout implements Layout {
    WidgetPosition ButtonSlotPosition;
    WidgetPosition ButtonWidgetPosition;
    DriftSystem driftSystem;
    RectangleButtonWidget buttonWidget;

    float k1;
    float k2;

    boolean Disturbed;
    boolean running;

    public ButtonSlotLayout() {
        ButtonSlotPosition = new WidgetPosition();
        ButtonWidgetPosition = new WidgetPosition();
        driftSystem = new DriftSystem();

        k1 = 0;
        k2 = 0;

        Disturbed = false;
        running = false;
    }

    @Override
    public void update(float deltaTime, float totalTime) {
        if (Disturbed) {
            driftSystem.setDriftInfo(buttonWidget.getPosition(), ButtonSlotPosition, k1, k2, totalTime);
            Disturbed = false;
            running = true;
        }

        if (running) {
            WidgetPosition widgetPositionUpdate = driftSystem.getUpdatePosition(totalTime);

            float percentageComplete = driftSystem.getPercentageComplete(totalTime);
            if (percentageComplete == 1.0f) {
                running = false;
            }

            this.ButtonWidgetPosition.rotaion.angle = widgetPositionUpdate.rotaion.angle;
            this.ButtonWidgetPosition.Centerposition.x = widgetPositionUpdate.Centerposition.x;
            this.ButtonWidgetPosition.Centerposition.y = widgetPositionUpdate.Centerposition.y;
            this.ButtonWidgetPosition.X_scale = widgetPositionUpdate.X_scale;
            this.ButtonWidgetPosition.Y_scale = widgetPositionUpdate.Y_scale;
        } else {
            this.ButtonWidgetPosition.rotaion.angle = ButtonSlotPosition.rotaion.angle;
            this.ButtonWidgetPosition.Centerposition.x = ButtonSlotPosition.Centerposition.x;
            this.ButtonWidgetPosition.Centerposition.y = ButtonSlotPosition.Centerposition.y;
            this.ButtonWidgetPosition.X_scale = ButtonSlotPosition.X_scale;
            this.ButtonWidgetPosition.Y_scale = ButtonSlotPosition.Y_scale;
        }
    }

    @Override
    public void draw() {
       buttonWidget.setTranslateRotateScale(ButtonWidgetPosition);
       buttonWidget.draw();
    }

    @Override
    public WidgetTouchEvent TouchResponse(List<Input.TouchEvent> touchEvents) {
        if (!running) {
            return  buttonWidget.isTouched(touchEvents);
        }

        WidgetTouchEvent widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
        widgetTouchEvent.isTouched = false;
        widgetTouchEvent.isTouchedDown = false;
        widgetTouchEvent.isMoving = false;
        widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Low;
        widgetTouchEvent.isDoubleTouched = false;
        widgetTouchEvent.object = null;
        return widgetTouchEvent;
    }

    public void intializeButton(float x, float y, float angle, RectangleButtonWidget buttonWidget, float k1,
                                float k2) {
        ButtonSlotPosition.Centerposition.x = ButtonWidgetPosition.Centerposition.x = x;
        ButtonSlotPosition.Centerposition.y = ButtonWidgetPosition.Centerposition.y = y;
        ButtonSlotPosition.rotaion.angle = ButtonWidgetPosition.rotaion.angle = angle;
        ButtonSlotPosition.X_scale = ButtonWidgetPosition.X_scale = 1f;
        ButtonSlotPosition.Y_scale = ButtonWidgetPosition.Y_scale = 1f;
        this.k1 = k1;
        this.k2 = k2;

        this.buttonWidget = buttonWidget;

        Disturbed = false;
        running = false;
        buttonWidget.setTranslateRotateScale(ButtonWidgetPosition);
    }

    public void setButtonPosition(float x, float y) {
        ButtonSlotPosition.Centerposition.x = x;
        ButtonSlotPosition.Centerposition.y = y;
        Disturbed = true;
    }

    public void forceLoadButtonPosition() {
        ButtonWidgetPosition.Centerposition.x = ButtonSlotPosition.Centerposition.x;
        ButtonWidgetPosition.Centerposition.y = ButtonSlotPosition.Centerposition.y;
        ButtonWidgetPosition.rotaion.angle = ButtonSlotPosition.rotaion.angle;
        ButtonWidgetPosition.X_scale = ButtonSlotPosition.X_scale;
        ButtonWidgetPosition.Y_scale = ButtonSlotPosition.Y_scale;
        buttonWidget.setTranslateRotateScale(ButtonWidgetPosition);
    }
}
