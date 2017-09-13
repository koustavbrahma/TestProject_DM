package koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl;

import java.util.Hashtable;
import java.util.List;

import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkinterface.ViewMaps;
import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkinterface.ViewNode;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Input;

/**
 * Created by Koustav on 4/2/2017.
 */
public class ViewTree {
    Hashtable<Integer, ViewNode> Nodes;
    ViewNode RootNode;
    ViewNode DragNode;
    ViewNode PopUpNode;
    int IdCounter;

    public ViewTree(ViewMaps maps) {
        Nodes = new Hashtable<Integer, ViewNode>();
        RootNode = new InternalViewNode(this, maps);
        Nodes.put(new Integer(1), RootNode);
        DragNode = null;
        PopUpNode = null;
        IdCounter = 2;
    }

    public int createInternalNode(ViewMaps maps) {
        InternalViewNode newNode = new InternalViewNode(this, maps);
        int i = IdCounter;
        IdCounter ++;
        Nodes.put(i, newNode);
        return i;
    }

    public boolean setPopUpNode(int Id) {
        ViewNode node = Nodes.get(Id);
        if (node == null) {
            return false;
        }

        PopUpNode = node;
        return true;
    }

    public void clearPopUpNode() {
        PopUpNode = null;
    }

    public void draw() {
        RootNode.draw();
        if (PopUpNode != null) {
            PopUpNode.draw();
        }
    }

    public void update(float deltaTime, float totalTime) {
        RootNode.update(deltaTime, totalTime);
        if (PopUpNode != null) {
            PopUpNode.update(deltaTime, totalTime);
        }
    }

    public void processTouch(Input input, List<Input.TouchEvent> touchEvents) {
        if (DragNode != null) {
            DragNode.isTouched(input, touchEvents);
        } else {
            if ((PopUpNode == null) || !PopUpNode.isTouched(input, touchEvents)) {
                RootNode.isTouched(input, touchEvents);
            }
        }
    }
}
