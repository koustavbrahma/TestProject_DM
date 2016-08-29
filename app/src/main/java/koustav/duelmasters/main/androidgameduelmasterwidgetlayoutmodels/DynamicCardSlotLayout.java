package koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels;

import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetPosition;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchFocusLevel;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.Layout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;
import koustav.duelmasters.main.androidgameopenglanimation.DriftSystem;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgamesframework.Input;

import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;

/**
 * Created by Koustav on 7/12/2016.
 */
public class DynamicCardSlotLayout implements Layout {
    WidgetPosition TopSlotPosition;
    WidgetPosition TopWidgetPosition;
    DriftSystem TopDriftSystem;
    CardWidget TopCardWidget;

    WidgetPosition OldTopSlotPosition;
    WidgetPosition TopSlotPosition_BreakPoint;

    float k1;
    float k2;
    float percentageComplete;

    boolean Disturbed;
    boolean running;
    boolean locked;
    boolean TwoStepTracking;
    boolean TwoStepTransition;

    float[] relativeNearPointAfterRot;
    float[] relativeFarPointAfterRot;
    GLGeometry.GLPoint relativeNearPoint;
    GLGeometry.GLPoint relativeFarPoint;

    public DynamicCardSlotLayout() {
        TopSlotPosition = new WidgetPosition();
        TopWidgetPosition = new WidgetPosition();
        OldTopSlotPosition = new WidgetPosition();
        TopSlotPosition_BreakPoint = new WidgetPosition();
        TopDriftSystem = new DriftSystem();
        TopCardWidget = null;

        k1 = 0f;
        k2 = 0f;
        percentageComplete = 1.0f;

        Disturbed = false;
        running = false;
        locked = false;
        TwoStepTracking = false;
        TwoStepTransition = false;

        relativeNearPointAfterRot = new float[4];
        relativeFarPointAfterRot = new float[4];

        relativeNearPoint = new GLGeometry.GLPoint(0, 0, 0);
        relativeFarPoint = new GLGeometry.GLPoint(0, 0, 0);
    }

    @Override
    public void update(float deltaTime, float totalTime) {
        if (TopCardWidget == null) {
            return;
        }

        if (Disturbed) {
            TwoStepTracking = false;
            if (TwoStepTransition && Math.abs((TopCardWidget.getPosition().rotaion.angle- TopSlotPosition.rotaion.angle)/ TopSlotPosition.rotaion.angle) > 0.05f) {
                TwoStepTracking = true;
            } else if (TwoStepTransition && Math.abs((TopCardWidget.getPosition().rotaion.x - TopSlotPosition.rotaion.x)/ TopSlotPosition.rotaion.x) > 0.05f) {
                TwoStepTracking = true;
            } else if (TwoStepTransition && Math.abs((TopCardWidget.getPosition().rotaion.y - TopSlotPosition.rotaion.y)/ TopSlotPosition.rotaion.y) > 0.05f) {
                TwoStepTracking = true;
            } else if (TwoStepTransition && Math.abs((TopCardWidget.getPosition().rotaion.z - TopSlotPosition.rotaion.z) / TopSlotPosition.rotaion.z) > 0.05f) {
                TwoStepTracking = true;
            }
            TwoStepTransition = false;
            if (TwoStepTracking) {
                setIdentityM(AssetsAndResource.tempMatrix, 0);
                if (TopSlotPosition.rotaion.angle != 0) {
                    rotateM(AssetsAndResource.tempMatrix, 0, TopSlotPosition.rotaion.angle, TopSlotPosition.rotaion.x, TopSlotPosition.rotaion.y,
                            TopSlotPosition.rotaion.z);
                }
                float[] PointAfterRot = new float[4];
                multiplyMV(PointAfterRot, 0, AssetsAndResource.tempMatrix, 0, new float[] {0, 0, -1, 0f}, 0);

                setIdentityM(AssetsAndResource.tempMatrix, 0);
                if (TopSlotPosition.rotaion.angle != 0) {
                    rotateM(AssetsAndResource.tempMatrix, 0, TopSlotPosition.rotaion.angle, TopSlotPosition.rotaion.x, TopSlotPosition.rotaion.y,
                            TopSlotPosition.rotaion.z);
                }
                float[] PointAfterRot2 = new float[4];
                multiplyMV(PointAfterRot2, 0, AssetsAndResource.tempMatrix, 0, new float[] {0, 1, 0, 0f}, 0);

                TopSlotPosition_BreakPoint.Centerposition.x = TopSlotPosition.Centerposition.x +
                        PointAfterRot[0] * AssetsAndResource.CardHeight + 0.01f * PointAfterRot2[0];
                TopSlotPosition_BreakPoint.Centerposition.y = TopSlotPosition.Centerposition.y +
                        PointAfterRot[1] * AssetsAndResource.CardHeight + 0.01f * PointAfterRot2[1];
                TopSlotPosition_BreakPoint.Centerposition.z = TopSlotPosition.Centerposition.z +
                        PointAfterRot[2] * AssetsAndResource.CardHeight + 0.01f * PointAfterRot2[2];
                TopSlotPosition_BreakPoint.rotaion.angle = TopSlotPosition.rotaion.angle;
                TopSlotPosition_BreakPoint.rotaion.x = TopSlotPosition.rotaion.x;
                TopSlotPosition_BreakPoint.rotaion.y = TopSlotPosition.rotaion.y;
                TopSlotPosition_BreakPoint.rotaion.z = TopSlotPosition.rotaion.z;
                TopSlotPosition_BreakPoint.X_scale = TopSlotPosition.X_scale;
                TopSlotPosition_BreakPoint.Y_scale = TopSlotPosition.Y_scale;
                TopSlotPosition_BreakPoint.Z_scale = TopSlotPosition.Z_scale;
                TopDriftSystem.setDriftInfo(TopCardWidget.getPosition(), TopSlotPosition_BreakPoint, k1, k2, totalTime);
            } else {
                TopDriftSystem.setDriftInfo(TopCardWidget.getPosition(), TopSlotPosition, k1, k2, totalTime);
            }
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

            if (TwoStepTracking) {
                if (!running) {
                    TopDriftSystem.setDriftInfo(TopCardWidget.getPosition(), TopSlotPosition, k1, k2, totalTime);
                    running = true;
                    TwoStepTracking = false;
                }
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

        setIdentityM(AssetsAndResource.tempMatrix, 0);
        if (TopSlotPosition.rotaion.angle != 0) {
            rotateM(AssetsAndResource.tempMatrix, 0, TopSlotPosition.rotaion.angle, -TopSlotPosition.rotaion.x, -TopSlotPosition.rotaion.y,
                    -TopSlotPosition.rotaion.z);
        }

        GLGeometry.GLPoint relativeNearPointAfterTrans = input.getNearPoint(0).translate(TopSlotPosition.Centerposition.getVector().scale(-1));
        GLGeometry.GLPoint relativeFarPointAfterTrans = input.getFarPoint(0).translate(TopSlotPosition.Centerposition.getVector().scale(-1));

        multiplyMV(relativeNearPointAfterRot, 0, AssetsAndResource.tempMatrix, 0, new float[] {relativeNearPointAfterTrans.x,
                relativeNearPointAfterTrans.y, relativeNearPointAfterTrans.z, 0f}, 0);
        multiplyMV(relativeFarPointAfterRot, 0, AssetsAndResource.tempMatrix, 0, new float[] {relativeFarPointAfterTrans.x,
                relativeFarPointAfterTrans.y, relativeFarPointAfterTrans.z, 0f}, 0);

        relativeNearPoint.x = relativeNearPointAfterRot[0];
        relativeNearPoint.y = relativeNearPointAfterRot[1];
        relativeNearPoint.z = relativeNearPointAfterRot[2];

        relativeFarPoint.x = relativeFarPointAfterRot[0];
        relativeFarPoint.y = relativeFarPointAfterRot[1];
        relativeFarPoint.z = relativeFarPointAfterRot[2];

        GLGeometry.GLPoint intersectingPoint2 = GLGeometry.GLRayIntersectionWithXZPlane(
                new GLGeometry.GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)), 0);

        float width = Math.abs(intersectingPoint2.x);
        float height = Math.abs(intersectingPoint2.z);

        if ((width <= AssetsAndResource.CardWidth/2  && height <= AssetsAndResource.CardHeight/2) ||
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
        if (TopCardWidget != null && !running && !locked) {
            return TopCardWidget.isTouched(touchEvents);
        }

        WidgetTouchEvent widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
        widgetTouchEvent.resetTouchEvent();
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
        TwoStepTransition = false;
        locked = false;
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
        if (!locked) {
            TopSlotPosition.Centerposition.x = x;
            TopSlotPosition.Centerposition.y = y;
            TopSlotPosition.Centerposition.z = z;

            Disturbed = true;
        }
    }

    public void setTwoStepTransition(boolean val) {
        this.TwoStepTransition = val;
    }

    public void lockSlot(float x, float y, float z, float angle, float x_axis, float y_axis, float z_axis ) {
        if (locked) {
            return;
        }
        locked = true;
        OldTopSlotPosition.Centerposition.x = TopSlotPosition.Centerposition.x;
        OldTopSlotPosition.Centerposition.y = TopSlotPosition.Centerposition.y;
        OldTopSlotPosition.Centerposition.z = TopSlotPosition.Centerposition.z;
        OldTopSlotPosition.rotaion.angle = TopSlotPosition.rotaion.angle;
        OldTopSlotPosition.rotaion.x = TopSlotPosition.rotaion.x;
        OldTopSlotPosition.rotaion.y = TopSlotPosition.rotaion.y;
        OldTopSlotPosition.rotaion.z = TopSlotPosition.rotaion.z;
        OldTopSlotPosition.X_scale = TopSlotPosition.X_scale;
        OldTopSlotPosition.Y_scale = TopSlotPosition.Y_scale;
        OldTopSlotPosition.Z_scale = TopSlotPosition.Z_scale;

        TopSlotPosition.Centerposition.x = x;
        TopSlotPosition.Centerposition.y = y;
        TopSlotPosition.Centerposition.z = z;
        TopSlotPosition.rotaion.angle = angle;
        TopSlotPosition.rotaion.x = x_axis;
        TopSlotPosition.rotaion.y = y_axis;
        TopSlotPosition.rotaion.z = z_axis;

        Disturbed = true;
    }

    public void unlockSlot() {
        if (!locked) {
            return;
        }
        locked = false;

        TopSlotPosition.Centerposition.x = OldTopSlotPosition.Centerposition.x;
        TopSlotPosition.Centerposition.y = OldTopSlotPosition.Centerposition.y;
        TopSlotPosition.Centerposition.z = OldTopSlotPosition.Centerposition.z;
        TopSlotPosition.rotaion.angle = OldTopSlotPosition.rotaion.angle;
        TopSlotPosition.rotaion.x = OldTopSlotPosition.rotaion.x;
        TopSlotPosition.rotaion.y = OldTopSlotPosition.rotaion.y;
        TopSlotPosition.rotaion.z = OldTopSlotPosition.rotaion.z;
        TopSlotPosition.X_scale = OldTopSlotPosition.X_scale;
        TopSlotPosition.Y_scale = OldTopSlotPosition.Y_scale;
        TopSlotPosition.Z_scale = OldTopSlotPosition.Z_scale;

        Disturbed = true;
    }
}
