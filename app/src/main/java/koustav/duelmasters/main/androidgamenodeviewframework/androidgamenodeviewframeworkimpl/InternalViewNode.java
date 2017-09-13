package koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl;

import java.util.Iterator;
import java.util.List;

import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkinterface.ViewMaps;
import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkinterface.ViewNode;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Input;

/**
 * Created by Koustav on 4/2/2017.
 */
public class InternalViewNode extends ViewNode {
    ChildNodes childNodes;

    public InternalViewNode(ViewTree tree, ViewMaps maps) {
        super(tree, maps.getShape());
        childNodes = new ChildNodes(this, maps);
    }

    @Override
    public void draw() {
        Iterator<ViewNode> childItr = childNodes.getChildDrawIterator();
        while (childItr.hasNext()) {
            ViewNode node = childItr.next();
            node.draw();
        }
    }

    @Override
    public void update(float deltaTime, float totalTime) {
        Iterator<ViewNode> childItr = childNodes.getChildUpdateIterator();
        while (childItr.hasNext()) {
            ViewNode node = childItr.next();
            node.update(deltaTime, totalTime);
        }
    }

    @Override
    public boolean isTouched(Input input, List<Input.TouchEvent> touchEvents) {
        return childNodes.isTouched(input, touchEvents);
    }

    public void clearViewMapKeysForChildNode(ViewNode node) {
        childNodes.clearViewMapKeysForChildNode(node);
    }

    public void addViewMapKeysForChildNode(ViewNode node) {
        childNodes.addViewMapKeysForChildNode(node);
    }
}
