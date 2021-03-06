package koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels;

import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.Widget;
import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl.ViewNodePosition;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.Layout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.RectangleButtonWidget;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglmotionmodels.DriftSystem;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Input;

/**
 * Created by Koustav on 8/3/2016.
 */
public class ButtonSlotLayout implements Layout {
    ViewNodePosition ButtonSlotPosition;
    ViewNodePosition ButtonWidgetPosition;
    DriftSystem driftSystem;
    Widget buttonWidget;

    float k1;
    float k2;

    boolean Disturbed;
    boolean running;

    public ButtonSlotLayout() {
        ButtonSlotPosition = new ViewNodePosition();
        ButtonWidgetPosition = new ViewNodePosition();
        driftSystem = new DriftSystem();

        k1 = 0;
        k2 = 0;

        Disturbed = false;
        running = false;
    }

    @Override
    public void update(float deltaTime, float totalTime) {
        if (Disturbed) {
            boolean change = false;
            if (buttonWidget.getPosition().rotaion.angle != ButtonSlotPosition.rotaion.angle) {
                change = true;
            } else if (buttonWidget.getPosition().Centerposition.x != ButtonSlotPosition.Centerposition.x) {
                change = true;
            } else if (buttonWidget.getPosition().Centerposition.y != ButtonSlotPosition.Centerposition.y) {
                change = true;
            } else if (buttonWidget.getPosition().X_scale != ButtonSlotPosition.X_scale) {
                change = true;
            } else if (buttonWidget.getPosition().Y_scale != ButtonWidgetPosition.Y_scale) {
                change = true;
            }

            if (change) {
                driftSystem.setDriftInfo(buttonWidget.getPosition(), ButtonSlotPosition, null, null, k1, k2, totalTime);
                running = true;
            }
            Disturbed = false;
        }

        if (running) {
            ViewNodePosition widgetPositionUpdate = driftSystem.getUpdatePosition(totalTime);

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
        widgetTouchEvent.resetTouchEvent();
        return widgetTouchEvent;
    }

    public void intializeButton(float x, float y, float angle, Widget buttonWidget, float k1,
                                float k2) {
        if (!(buttonWidget instanceof RectangleButtonWidget)) {
            throw new IllegalArgumentException("Invalid Type");
        }
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
