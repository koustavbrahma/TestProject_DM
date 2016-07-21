package koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels;

import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmasterswidget.WidgetPosition;
import koustav.duelmasters.main.androidgameduelmasterswidget.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterswidget.WidgetTouchFocusLevel;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayout.Layout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;
import koustav.duelmasters.main.androidgameopenglanimation.DriftSystem;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgamesframework.Input;

/**
 * Created by Koustav on 7/12/2016.
 */
public class DynamicCardSlotLayout implements Layout {
    WidgetPosition TopSlotPosition;
    WidgetPosition TopWidgetPosition;
    DriftSystem TopDriftSystem;
    CardWidget TopCardWidget;

    WidgetPosition OldTopSlotPosition;

    float k1;
    float k2;
    float percentageComplete;

    boolean Disturbed;
    boolean running;

    public DynamicCardSlotLayout() {
        TopSlotPosition = new WidgetPosition();
        TopWidgetPosition = new WidgetPosition();
        OldTopSlotPosition = new WidgetPosition();
        TopDriftSystem = new DriftSystem();
        TopCardWidget = null;

        k1 = 0f;
        k2 = 0f;
        percentageComplete = 1.0f;

        Disturbed = false;
        running = false;
    }

    @Override
    public void update(float deltaTime, float totalTime) {
        if (TopCardWidget == null) {
            return;
        }

        if (Disturbed) {
            TopDriftSystem.setDriftInfo(TopCardWidget.getPosition(), TopSlotPosition, k1, k2, totalTime);
            Disturbed = false;
            running = true;
        }

        if (running) {
            WidgetPosition widgetPositionUpdate = TopDriftSystem.getUpdatePosition(totalTime);

            percentageComplete = TopDriftSystem.getPercentageComplete(totalTime);
            if (percentageComplete == 1.0f) {
                running = false;
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
        }
    }

    public void DragUpdate(float x, float y, float z) {
        Input input = AssetsAndResource.game.getInput();
        if (!input.isTouchDown(0) || running) {
            return;
        }

        GLGeometry.GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithPlane(
                new GLGeometry.GLRay(new GLGeometry.GLPoint(input.getNearPoint(0).x, input.getNearPoint(0).y, input.getNearPoint(0).z),
                        GLGeometry.GLVectorBetween(input.getNearPoint(0), input.getFarPoint(0))),
                new GLGeometry.GLPlane(new GLGeometry.GLPoint(x, y, z), new GLGeometry.GLVector(x, y, z)));
        if ((Math.abs(intersectingPoint.x - TopSlotPosition.Centerposition.x) <= (AssetsAndResource.CardWidth * TopSlotPosition.X_scale)/2 &&
                Math.abs(intersectingPoint.z - TopSlotPosition.Centerposition.z) <= (AssetsAndResource.CardHeight * TopSlotPosition.Z_scale)/2) ||
                (input.TouchType(0) != Input.TouchEvent.TOUCH_DRAGGED)) {
            return;
        }

        GLGeometry.GLVector crossPro = (new GLGeometry.GLVector(0, 1f, 0)).crossProduct((new GLGeometry.GLVector(x, y, z)).getDirection());
        GLGeometry.GLVector dir = crossPro.getDirection();
        float angle = (float) Math.asin(crossPro.getMagnitude());
        angle =(float) Math.toDegrees(angle);
        TopWidgetPosition.rotaion.angle = angle;
        TopWidgetPosition.rotaion.x = dir.x;
        TopWidgetPosition.rotaion.y = dir.y;
        TopWidgetPosition.rotaion.z = dir.z;
        TopWidgetPosition.Centerposition.x = intersectingPoint.x;
        TopWidgetPosition.Centerposition.y = intersectingPoint.y;
        TopWidgetPosition.Centerposition.z = intersectingPoint.z;
        TopWidgetPosition.X_scale = TopSlotPosition.X_scale;
        TopWidgetPosition.Y_scale = TopSlotPosition.Y_scale;
        TopWidgetPosition.Z_scale = TopSlotPosition.Z_scale;
    }

    @Override
    public void draw() {
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
        widgetTouchEvent.isFocus = WidgetTouchFocusLevel.Low;
        widgetTouchEvent.isDoubleTouched = false;
        widgetTouchEvent.object = null;
        return widgetTouchEvent;
    }

    public void initializeSlot(float x, float y, float z, float angle, float x_axis, float y_axis, float z_axis, CardWidget cardWidget,
                               float k1, float k2) {
        this.k1 = k1;
        this.k2 = k2;

        TopSlotPosition.Centerposition.x = x;
        TopSlotPosition.Centerposition.y = y;
        TopSlotPosition.Centerposition.z = z;
        TopSlotPosition.rotaion.angle = angle;
        TopSlotPosition.rotaion.x = x_axis;
        TopSlotPosition.rotaion.y = y_axis;
        TopSlotPosition.rotaion.z = z_axis;
        TopSlotPosition.X_scale = 1f;
        TopSlotPosition.Y_scale = 1f;
        TopSlotPosition.Z_scale = 1f;

        this.TopCardWidget = cardWidget;

        Disturbed = true;
    }

    public void resetSlot() {
        this.TopCardWidget = null;
    }

    public CardWidget getCardWidget() {
        return TopCardWidget;
    }

    public boolean IsTransition() {
        return running;
    }

    public float getPercentageComplete() {
        return percentageComplete;
    }

    public void SetSlotDisturbed() {
        this.Disturbed = true;
    }

    public void setSlotPosition(float x, float y, float z) {
        TopSlotPosition.Centerposition.x = x;
        TopSlotPosition.Centerposition.y = y;
        TopSlotPosition.Centerposition.z = z;
    }
}
