package koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkinterface;

import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl.ViewNodePosition;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLGeometry;

/**
 * Created by Koustav on 4/1/2017.
 */
public abstract class ViewNode {
    protected GLGeometry shape;
    protected ViewNode parentNode;
    protected ViewNodePosition centerPosition;

    public ViewNode(GLGeometry shape) {
        this.shape = shape;
        parentNode = null;
        centerPosition = new ViewNodePosition();
    }

    public abstract void draw();
    public abstract void update(float deltaTime, float totalTime);
    public void setParentNode(ViewNode parent) {
        parentNode = parent;
    }
    public ViewNode getParentNode() {
        return parentNode;
    }
    public ViewNodePosition getCenterPosition() {
        ViewNodePosition widgetPosition = new ViewNodePosition();
        widgetPosition.Centerposition.x = centerPosition.Centerposition.x;
        widgetPosition.Centerposition.y = centerPosition.Centerposition.y;
        widgetPosition.Centerposition.z = centerPosition.Centerposition.z;
        widgetPosition.rotaion.angle = centerPosition.rotaion.angle;
        widgetPosition.rotaion.x = centerPosition.rotaion.x;
        widgetPosition.rotaion.y = centerPosition.rotaion.y;
        widgetPosition.rotaion.z = centerPosition.rotaion.z;
        widgetPosition.X_scale = centerPosition.X_scale;
        widgetPosition.Y_scale = centerPosition.Y_scale;
        widgetPosition.Z_scale = centerPosition.Z_scale;
        return widgetPosition;
    }
    public void setCenterPosition(ViewNodePosition position) {
        centerPosition.Centerposition.x = position.Centerposition.x;
        centerPosition.Centerposition.y = position.Centerposition.y;
        centerPosition.Centerposition.z = position.Centerposition.z;
        centerPosition.rotaion.angle = position.rotaion.angle;
        centerPosition.rotaion.x = position.rotaion.x;
        centerPosition.rotaion.y = position.rotaion.y;
        centerPosition.rotaion.z = position.rotaion.z;
        centerPosition.X_scale = position.X_scale;
        centerPosition.Y_scale = position.Y_scale;
        centerPosition.Z_scale = position.Z_scale;
    }
}
