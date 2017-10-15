package koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl;

import java.util.ArrayList;
import java.util.List;

import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkinterface.ViewNode;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglmotionmodels.MotionModels;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.MatrixHelper;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Input;

/**
 * Created by Koustav on 4/2/2017.
 */
public abstract class LeafViewNode extends ViewNode {
    protected Input.TouchEvent event;
    protected boolean dragLock;
    protected ArrayList<LeafViewNodeGroup> groups;
    protected MotionModels motionModel;
    protected boolean continueousViewMapUpdate;

    public LeafViewNode(ViewTree tree, GLGeometry shape, ArrayList<LeafViewNodeGroup> groups,
                        boolean continueousViewMapUpdate) {
        super(tree, shape);
        this.groups = groups;
        event = new Input.TouchEvent();
        dragLock = false;
        motionModel = null;
        this.continueousViewMapUpdate = continueousViewMapUpdate;
    }

    @Override
    public boolean isTouched(Input input, List<Input.TouchEvent> touchEvents) {
        if (dragLock) {
            if (!input.isTouchDown(0)) {
                tree.DragNode = null;
                dragLock = false;
                onTouchEndDragNotify();
            } else {
                onTouchDownNotify();
            }
            return true;
        }
        if (input.isTouchDown(0)) {
            event.type = input.TouchType(0);
            event.x = input.getTouchX(0);
            event.y = input.getTouchY(0);
            event.normalizedX = input.getNormalizedX(0);
            event.normalizedY = input.getNormalizedY(0);
            for (int i=0; i < 4; i++) {
                event.nearPoint[i].x = input.getNearPoint(i).x;
                event.nearPoint[i].y = input.getNearPoint(i).y;
                event.nearPoint[i].z = input.getNearPoint(i).z;

                event.farPoint[i].x = input.getFarPoint(i).x;
                event.farPoint[i].y = input.getFarPoint(i).y;
                event.farPoint[i].z = input.getFarPoint(i).z;
            }

            boolean status = false;
            if (evaluateTouch(event)) {
                status = true;
            }
            if (status) {
                onTouchDownNotify();
                if (event.type == Input.TouchEvent.TOUCH_DRAGGED) {
                    onTouchStartDragNotify();
                    tree.DragNode = this;
                    dragLock = true;
                }
            }
            return status;
        } else {
            boolean status = false;
            for (Input.TouchEvent event: touchEvents) {
                if (event.type == Input.TouchEvent.TOUCH_UP) {
                    if (evaluateTouch(event)) {
                        status = true;
                        break;
                    }
                }
            }
            if (status) {
                onTouchUpNotify();
            }
            return status;
        }
    }

    @Override
    public void draw() {
        boolean draw = false;
        for (LeafViewNodeGroup group : groups) {
            if (tree.containsLeafViewNodeGroup(group)) {
                draw = true;
                break;
            }
        }
        if (draw) {
            MatrixHelper.setTranslateRotateScale(getCenterPosition());
            drawLeafNode();
        }
    }

    @Override
    public void update(float deltaTime, float totalTime) {
        if (motionModel != null) {
            ViewNodePosition position = motionModel.update(deltaTime, totalTime);
            centerPosition.Centerposition.x = position.Centerposition.x;
            centerPosition.Centerposition.y = position.Centerposition.y;
            centerPosition.Centerposition.z = position.Centerposition.z;
            centerPosition.rotaion.x = position.rotaion.x;
            centerPosition.rotaion.y = position.rotaion.y;
            centerPosition.rotaion.z = position.rotaion.z;
            centerPosition.rotaion.angle = position.rotaion.angle;
            centerPosition.X_scale = position.X_scale;
            centerPosition.Y_scale = position.Y_scale;
            centerPosition.Z_scale = position.Z_scale;
            if (continueousViewMapUpdate) {
                clearViewMapKeys();
                addViewMapKeys();
            }
            if (motionModel.isFinished(deltaTime, totalTime)) {
                detachMotionModel();
            }
        }
    }

    public void attachMotionModel(MotionModels motionModel) {
        this.motionModel = motionModel;
        clearViewMapKeys();
    }

    public void detachMotionModel() {
        this.motionModel = null;
        addViewMapKeys();
    }

    public abstract boolean evaluateTouch(Input.TouchEvent event);

    public abstract void drawLeafNode();

    public void  onTouchUpNotify() {}

    public void  onTouchDownNotify() {}

    public void  onTouchStartDragNotify() {}

    public void  onTouchEndDragNotify() {}

    public void clearViewMapKeys() {
        if (parentNode != null) {
            parentNode.clearViewMapKeysForChildNode(this);
        }
    }

    public void addViewMapKeys() {
        if (parentNode != null) {
            parentNode.addViewMapKeysForChildNode(this);
        }
    }
}
