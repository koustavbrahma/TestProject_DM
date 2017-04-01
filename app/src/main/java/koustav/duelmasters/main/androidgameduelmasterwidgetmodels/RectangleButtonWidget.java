package koustav.duelmasters.main.androidgameduelmasterwidgetmodels;

import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.Widget;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetMode;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetPosition;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.ControllerButton;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglobjectmodels.ScreenRectangle;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.DrawObjectHelper;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.MatrixHelper;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Input;

import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;

/**
 * Created by Koustav on 8/2/2016.
 */
public class RectangleButtonWidget implements Widget {
    WidgetPosition Position;
    float[] PointAfterRot;

    // GL object
    ScreenRectangle glrectangle;

    // Button Type
    ControllerButton button;

    public RectangleButtonWidget() {
        Position = new WidgetPosition();
        PointAfterRot = new float[4];
    }

    @Override
    public void draw() {
        DrawObjectHelper.drawOneScreenRectangle(glrectangle, AssetsAndResource.getFixedTexture(AssetsAndResource.getTextureIdForButton(button)));
    }

    @Override
    public void update(float deltaTime, float totalTime) {

    }

    @Override
    public WidgetTouchEvent isTouched(List<Input.TouchEvent> touchEvents) {
        WidgetTouchEvent widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
        widgetTouchEvent.resetTouchEvent();

        setIdentityM(AssetsAndResource.tempMatrix, 0);
        if (Position.rotaion.angle != 0) {
            rotateM(AssetsAndResource.tempMatrix, 0, Position.rotaion.angle, 0, 0, -1);
        }

        Input input = AssetsAndResource.game.getInput();
        if (input.isTouchDown(0)) {
            float relative_x = input.getNormalizedX(0) - Position.Centerposition.x;
            float relative_y = input.getNormalizedY(0) - Position.Centerposition.y;

            multiplyMV(PointAfterRot, 0, AssetsAndResource.tempMatrix, 0, new float[] {relative_x,
                    relative_y, 0f, 0f}, 0);

            relative_x = PointAfterRot[0];
            relative_y = PointAfterRot[1];

            float width = Math.abs(relative_x);
            float length = Math.abs(relative_y);

            if (width <= (glrectangle.width * Position.X_scale)/2 && length <= (glrectangle.length * Position.Y_scale)/2) {
                widgetTouchEvent.isTouched = true;
                widgetTouchEvent.isTouchedDown = true;
                widgetTouchEvent.object = button;
                return widgetTouchEvent;
            }
        } else {
            for (int i = 0; i < touchEvents.size(); i++) {
                Input.TouchEvent event = touchEvents.get(i);
                if (event.type == Input.TouchEvent.TOUCH_UP) {
                    float relative_x = event.normalizedX - Position.Centerposition.x;
                    float relative_y = event.normalizedY - Position.Centerposition.y;

                    multiplyMV(PointAfterRot, 0, AssetsAndResource.tempMatrix, 0, new float[] {relative_x,
                            relative_y, 0f, 0f}, 0);

                    relative_x = PointAfterRot[0];
                    relative_y = PointAfterRot[1];

                    float width = Math.abs(relative_x);
                    float length = Math.abs(relative_y);

                    if (width <= (glrectangle.width * Position.X_scale)/2 && length <= (glrectangle.length * Position.Y_scale)/2) {
                        widgetTouchEvent.isTouched = true;
                        widgetTouchEvent.isTouchedDown = false;
                        widgetTouchEvent.object = button;
                        break;
                    }
                }
            }
        }

        return widgetTouchEvent;
    }

    @Override
    public void setTranslateRotateScale(WidgetPosition position) {
        // Store this info required for isTouched

        this.Position.rotaion.angle = position.rotaion.angle;
        this.Position.Centerposition.x = position.Centerposition.x;
        this.Position.Centerposition.y = position.Centerposition.y;
        this.Position.X_scale = position.X_scale;
        this.Position.Y_scale = position.Y_scale;

        MatrixHelper.setTranslate(this.Position);
    }

    @Override
    public void ShadowEnable(boolean shadowEnable) {

    }

    @Override
    public void LinkGLobject(Object ...objs) {
        this.glrectangle = (ScreenRectangle) objs[0];
    }

    @Override
    public void LinkLogicalObject(Object obj) {
        button = (ControllerButton) obj;
    }

    @Override
    public Object getLogicalObject() {
        return null;
    }

    @Override
    public void setMode(WidgetMode mode) {

    }

    @Override
    public WidgetPosition getPosition() {
        return Position;
    }
}
