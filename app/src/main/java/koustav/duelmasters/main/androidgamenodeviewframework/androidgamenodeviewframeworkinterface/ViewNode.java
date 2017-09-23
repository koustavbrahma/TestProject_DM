package koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkinterface;

import java.util.List;

import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl.InternalViewNode;
import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl.ViewNodePosition;
import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl.ViewTree;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Input;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Input.*;

/**
 * Created by Koustav on 4/1/2017.
 */
public abstract class ViewNode {
    protected ViewTree tree;
    protected GLGeometry shape;
    protected InternalViewNode parentNode;
    protected ViewNodePosition centerPosition;

    public ViewNode(ViewTree tree, GLGeometry shape) {
        this.tree = tree;
        this.shape = shape;
        parentNode = null;
        centerPosition = new ViewNodePosition();
    }

    public abstract void draw();
    public abstract void update(float deltaTime, float totalTime);
    public abstract boolean isTouched(Input input, List<TouchEvent> touchEvents);
    public void setParentNode(InternalViewNode parent) {
        parentNode = parent;
    }
    public InternalViewNode getParentNode() {
        return parentNode;
    }
    public ViewNodePosition getCenterPosition() {
        ViewNodePosition viewNodePosition = new ViewNodePosition();
        viewNodePosition.Centerposition.x = centerPosition.Centerposition.x;
        viewNodePosition.Centerposition.y = centerPosition.Centerposition.y;
        viewNodePosition.Centerposition.z = centerPosition.Centerposition.z;
        viewNodePosition.rotaion.angle = centerPosition.rotaion.angle;
        viewNodePosition.rotaion.x = centerPosition.rotaion.x;
        viewNodePosition.rotaion.y = centerPosition.rotaion.y;
        viewNodePosition.rotaion.z = centerPosition.rotaion.z;
        viewNodePosition.X_scale = centerPosition.X_scale;
        viewNodePosition.Y_scale = centerPosition.Y_scale;
        viewNodePosition.Z_scale = centerPosition.Z_scale;
        return viewNodePosition;
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
