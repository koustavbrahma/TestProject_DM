package koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl;

import java.util.List;

import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkinterface.ViewNode;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Input;

/**
 * Created by Koustav on 4/2/2017.
 */
public abstract class LeafViewNode extends ViewNode {
    protected Input.TouchEvent event;
    protected boolean DragLock;

    public LeafViewNode(ViewTree tree, GLGeometry shape) {
        super(tree, shape);
        event = new Input.TouchEvent();
        DragLock = false;
    }

    @Override
    public boolean isTouched(Input input, List<Input.TouchEvent> touchEvents) {
        if (DragLock) {
            if (!input.isTouchDown(0)) {
                tree.DragNode = null;
                DragLock = false;
                onTouchUpNotify();
            } else {
                onTouchDownNotify();
                if (input.TouchType(0) == Input.TouchEvent.TOUCH_DRAGGED) {
                    onTouchDragNotify();
                }
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
                    onTouchDragNotify();
                    tree.DragNode = this;
                    DragLock = true;
                }
            }
            return status;
        } else {
            Input.TouchEvent event = null;
            boolean status = false;
            for (int i = 0; i < touchEvents.size(); i++) {
                event = touchEvents.get(i);
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

    public abstract boolean evaluateTouch(Input.TouchEvent event);

    public void  onTouchUpNotify() {}

    public void  onTouchDownNotify() {}

    public void  onTouchDragNotify() {}

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
