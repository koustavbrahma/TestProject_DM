package koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels;

import java.util.ArrayList;
import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl.ViewNodePosition;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.Layout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglmotionmodels.DriftSystem;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Input;

import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;

/**
 * Created by Koustav on 7/12/2016.
 */
public class DynamicCardSlotLayout implements Layout {
    ViewNodePosition TopSlotPosition;
    ViewNodePosition TopWidgetPosition;
    DriftSystem TopDriftSystem;
    CardWidget TopCardWidget;

    ViewNodePosition OldTopSlotPosition;
    ViewNodePosition TopSlotPosition_BreakPoint;

    float k1;
    float k2;
    float percentageComplete;

    boolean Disturbed;
    boolean running;
    boolean locked;
    boolean TwoStepTracking;
    boolean TwoStepTransition;
    boolean changing;

    float[] relativeNearPointAfterRot;
    float[] relativeFarPointAfterRot;
    GLGeometry.GLPoint relativeNearPoint;
    GLGeometry.GLPoint relativeFarPoint;
    GLGeometry.GLRay clearance_ray;

    ArrayList<ViewNodePosition> intermediate_p;
    ArrayList<Float> time_steps;
    boolean hasIntermediatePoints;

    public DynamicCardSlotLayout() {
        TopSlotPosition = new ViewNodePosition();
        TopWidgetPosition = new ViewNodePosition();
        OldTopSlotPosition = new ViewNodePosition();
        TopSlotPosition_BreakPoint = new ViewNodePosition();
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
        changing = true;

        relativeNearPointAfterRot = new float[4];
        relativeFarPointAfterRot = new float[4];

        relativeNearPoint = new GLGeometry.GLPoint(0, 0, 0);
        relativeFarPoint = new GLGeometry.GLPoint(0, 0, 0);
        clearance_ray = new GLGeometry.GLRay(new GLGeometry.GLPoint(0, 0, 0), new GLGeometry.GLVector(0, 0, 0));

        intermediate_p = new ArrayList<ViewNodePosition>();
        time_steps = new ArrayList<Float>();
        hasIntermediatePoints = false;
    }

    private boolean isSame(ViewNodePosition position1, ViewNodePosition position2) {
        boolean status = true;

        if (position1.Centerposition.x != position2.Centerposition.x) {
            status = false;
        } else if (position1.Centerposition.y != position2.Centerposition.y) {
            status = false;
        } else if (position1.Centerposition.z != position2.Centerposition.z) {
            status = false;
        } else if (position1.rotaion.angle != position2.rotaion.angle) {
            status = false;
        } else if (position1.rotaion.x != position2.rotaion.x) {
            status = false;
        } else if (position1.rotaion.y != position2.rotaion.y) {
            status = false;
        } else if (position1.rotaion.z != position2.rotaion.z) {
            status = false;
        } else if (position1.X_scale != position2.X_scale) {
            status = false;
        } else if (position1.Y_scale != position2.Y_scale) {
            status = false;
        } else if (position1.Z_scale != position2.Z_scale) {
            status = false;
        }

        return status;
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
                GLGeometry.GLPoint point1 = GLGeometry.GLRayIntersectionWithPlane(clearance_ray,
                        new GLGeometry.GLPlane(TopCardWidget.getPosition().Centerposition, clearance_ray.vector));
                GLGeometry.GLPoint point2 = GLGeometry.GLRayIntersectionWithPlane(clearance_ray,
                        new GLGeometry.GLPlane(TopSlotPosition.Centerposition, clearance_ray.vector));

                TopSlotPosition_BreakPoint.Centerposition.x = (point1.x + point2.x)/ 2.0f;
                TopSlotPosition_BreakPoint.Centerposition.y = (point1.y + point2.y)/ 2.0f;
                TopSlotPosition_BreakPoint.Centerposition.z = (point1.z + point2.z)/ 2.0f;
                TopSlotPosition_BreakPoint.rotaion.angle = TopCardWidget.getPosition().rotaion.angle;
                TopSlotPosition_BreakPoint.rotaion.x = TopCardWidget.getPosition().rotaion.x;
                TopSlotPosition_BreakPoint.rotaion.y = TopCardWidget.getPosition().rotaion.y;
                TopSlotPosition_BreakPoint.rotaion.z = TopCardWidget.getPosition().rotaion.z;
                TopSlotPosition_BreakPoint.X_scale = TopCardWidget.getPosition().X_scale;
                TopSlotPosition_BreakPoint.Y_scale = TopCardWidget.getPosition().Y_scale;
                TopSlotPosition_BreakPoint.Z_scale = TopCardWidget.getPosition().Z_scale;
                ArrayList<ViewNodePosition> trans_position = new ArrayList<ViewNodePosition>();
                trans_position.add(TopSlotPosition_BreakPoint);
                ArrayList<Float> tracking_point = new ArrayList<Float>();
                tracking_point.add(new Float(0.5f));
                TopDriftSystem.setDriftInfo(TopCardWidget.getPosition(), TopSlotPosition, trans_position, tracking_point, k1, k2, totalTime);
            } else if (hasIntermediatePoints) {
                TopDriftSystem.setDriftInfo(TopCardWidget.getPosition(), TopSlotPosition, intermediate_p, time_steps, k1, k2, totalTime);
                hasIntermediatePoints = false;
                intermediate_p.clear();
                time_steps.clear();
            } else {
                TopDriftSystem.setDriftInfo(TopCardWidget.getPosition(), TopSlotPosition, null, null, k1, k2, totalTime);
            }
            TwoStepTracking = false;
            Disturbed = false;
            running = true;
            changing = false;
        }

        if (running) {
            ViewNodePosition widgetPositionUpdate = TopDriftSystem.getUpdatePosition(totalTime);

            percentageComplete = TopDriftSystem.getPercentageComplete(totalTime);
            if (percentageComplete == 1.0f) {
                running = false;
            }

            if (!changing && percentageComplete != 0) {
                if (isSame(this.TopWidgetPosition, widgetPositionUpdate)) {
                    running = false;
                }
            }

            if (percentageComplete != 0) {
                changing = true;
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

        TopWidgetPosition.Centerposition.x = cardWidget.getPosition().Centerposition.x;
        TopWidgetPosition.Centerposition.y = cardWidget.getPosition().Centerposition.y;
        TopWidgetPosition.Centerposition.z = cardWidget.getPosition().Centerposition.z;
        TopWidgetPosition.rotaion.angle = cardWidget.getPosition().rotaion.angle;
        TopWidgetPosition.rotaion.x = cardWidget.getPosition().rotaion.x;
        TopWidgetPosition.rotaion.y = cardWidget.getPosition().rotaion.y;
        TopWidgetPosition.rotaion.z = cardWidget.getPosition().rotaion.z;
        TopWidgetPosition.X_scale = cardWidget.getPosition().X_scale;
        TopWidgetPosition.Y_scale = cardWidget.getPosition().Y_scale;
        TopWidgetPosition.Z_scale = cardWidget.getPosition().Z_scale;
        this.TopCardWidget = cardWidget;

        Disturbed = true;
    }

    public void resetSlot() {
        this.TopCardWidget = null;
        TwoStepTransition = false;
        locked = false;
        running = false;
        changing = true;
    }

    public void addIntermediatePoint(ArrayList<ViewNodePosition> points, ArrayList<Float> time_steps) {
        if (Disturbed) {
            if (points.size() != time_steps.size()) {
                throw new IllegalArgumentException("size must match");
            }
            intermediate_p.clear();
            this.time_steps.clear();

            for (int i = 0; i < points.size(); i++) {
                intermediate_p.add(points.get(i));
            }

            for (int i = 0; i < time_steps.size(); i++) {
                float val = time_steps.get(i);
                if (!(val > 0 && val < 1)) {
                    throw new IllegalArgumentException("Value must be inside 0 and 1");
                }
                this.time_steps.add(new Float(val));
            }
            if (points.size() > 0) {
                hasIntermediatePoints = true;
            }
        }
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

    public void UpdateTopWidgetPosition() {
        TopCardWidget.setTranslateRotateScale(TopWidgetPosition);
    }

    public void setSlotPosition(float x, float y, float z) {
        if (!locked) {
            TopSlotPosition.Centerposition.x = x;
            TopSlotPosition.Centerposition.y = y;
            TopSlotPosition.Centerposition.z = z;

            Disturbed = true;
        }
    }

    public void setTwoStepTransition(boolean val, float x, float y, float z, float dir_x, float dir_y, float dir_z) {
        this.TwoStepTransition = val;
        clearance_ray.point.x = x;
        clearance_ray.point.y = y;
        clearance_ray.point.z = z;
        clearance_ray.vector.x = dir_x;
        clearance_ray.vector.y = dir_y;
        clearance_ray.vector.z = dir_z;
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
