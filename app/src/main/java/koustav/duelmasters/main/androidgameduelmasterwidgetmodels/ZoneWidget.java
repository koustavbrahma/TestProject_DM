package koustav.duelmasters.main.androidgameduelmasterwidgetmodels;

import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Zone;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.WidgetPosition;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgamesframework.Input;

/**
 * Created by Koustav on 3/26/2016.
 */
public class ZoneWidget implements Widget {
    WidgetPosition Position;
    GLGeometry.GLPoint relativeNearPoint;
    GLGeometry.GLPoint relativeFarPoint;

    // Logical object
    public Zone zone;

    public ZoneWidget() {
        Position = new WidgetPosition();
    }

    @Override
    public void draw(float deltaTime, float totalTime) {

    }

    @Override
    public WidgetTouchEvent isTouched(List<Input.TouchEvent> touchEvents) {
        WidgetTouchEvent widgetTouchEvent = AssetsAndResource.widgetTouchEventPool.newObject();
        widgetTouchEvent.isTouched = false;
        widgetTouchEvent.isTouchedDown = false;
        widgetTouchEvent.isDoubleTouched = false;
        widgetTouchEvent.object = null;

        Input input = AssetsAndResource.game.getInput();
        if (input.isTouchDown(0)) {
            relativeNearPoint= input.getNearPoint(0).translate(Position.Centerposition.getVector().scale(-1));
            relativeFarPoint = input.getFarPoint(0).translate(Position.Centerposition.getVector().scale(-1));

            GLGeometry.GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                    new GLGeometry.GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)), 0f);

            float width = Math.abs(intersectingPoint.x);
            float height = Math.abs(intersectingPoint.z);

            if (width <= (AssetsAndResource.MazeWidth) && height <= (AssetsAndResource.MazeHeight/ 4.0f)) {
                widgetTouchEvent.isTouched = true;
                return widgetTouchEvent;
            }
        }

        for (int i = 0; i < touchEvents.size(); i++) {
            Input.TouchEvent event = touchEvents.get(i);
            if (event.type == Input.TouchEvent.TOUCH_UP) {
                relativeNearPoint = event.nearPoint[0].translate(Position.Centerposition.getVector().scale(-1));
                relativeFarPoint = event.farPoint[0].translate(Position.Centerposition.getVector().scale(-1));

                GLGeometry.GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                        new GLGeometry.GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)), 0f);

                float width = Math.abs(intersectingPoint.x);
                float height = Math.abs(intersectingPoint.z);

                if (width <= AssetsAndResource.MazeWidth && height <= (AssetsAndResource.MazeHeight /4.0f)) {
                    widgetTouchEvent.isTouched = true;
                    break;
                }
            }
        }

        return widgetTouchEvent;
    }

    @Override
    public void setTranslateRotateScale(WidgetPosition position) {
        // Store this info required for isTouched
        this.Position.rotaion.angle = position.rotaion.angle;
        this.Position.rotaion.x = position.rotaion.x;
        this.Position.rotaion.y = position.rotaion.y;
        this.Position.rotaion.z = position.rotaion.z;
        Position.Centerposition.x = position.Centerposition.x;
        Position.Centerposition.y = position.Centerposition.y;
        Position.Centerposition.z = position.Centerposition.z;
        this.Position.X_scale = position.X_scale;
        this.Position.Y_scale = position.Y_scale;
        this.Position.Z_scale = position.Z_scale;
    }

    @Override
    public void ShadowEnable(boolean shadowEnable) {

    }

    @Override
    public void LinkGLobject(Object ...objs) {

    }

    @Override
    public void LinkLogicalObject(Object obj) {
        zone = (Zone) obj;
    }

    @Override
    public void setMode(WidgetMode mode) {

    }
}
