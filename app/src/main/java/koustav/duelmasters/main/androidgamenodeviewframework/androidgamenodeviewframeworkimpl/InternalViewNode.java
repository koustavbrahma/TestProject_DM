package koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkinterface.ViewMaps;
import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkinterface.ViewNode;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Input;

/**
 * Created by Koustav on 4/2/2017.
 */
public class InternalViewNode extends ViewNode {
    ViewMaps maps;
    ArrayList<ViewNode> touchedNodes;

    public InternalViewNode(ViewTree tree, ViewMaps maps) {
        super(tree, maps.getShape());
        this.maps = maps;
        this.maps.setNode(this);
        touchedNodes = new ArrayList<ViewNode>();
    }

    @Override
    public void draw() {
        ArrayList<ViewNode> childItr = maps.getChildDrawIterator();
        for (ViewNode node : childItr) {
            node.draw();
        }
    }

    @Override
    public void update(float deltaTime, float totalTime) {
        ArrayList<ViewNode> childItr = maps.getChildDrawIterator();
        for (ViewNode node: childItr) {
            node.update(deltaTime, totalTime);
        }
    }

    @Override
    public boolean isTouched(Input input, List<Input.TouchEvent> touchEvents) {
        if (!(maps.hasChildren())) {
            return false;
        }

        boolean touchedChild = maps.mapTouchEvent(input, touchEvents, touchedNodes);
        for (ViewNode node : touchedNodes) {
            if (node.isTouched(input, touchEvents)) {
                touchedChild = true;
                break;
            }
        }
        touchedNodes.clear();
        if (touchedChild) {
            return true;
        }
        return false;
    }

    public void addChild(ViewNode node) {
        maps.addChild(node);
    }

    public boolean removeChild(ViewNode node) {
        return maps.removeChild(node);
    }

    public void clearViewMapKeysForChildNode(ViewNode node) {
        maps.clearViewMapKeysForChildNode(node);
    }

    public void addViewMapKeysForChildNode(ViewNode node) {
        maps.addViewMapKeysForChildNode(node);
    }
}
