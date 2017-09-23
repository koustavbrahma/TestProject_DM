package koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkinterface;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl.InternalViewNode;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Input;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Input.*;

/**
 * Created by Koustav on 6/3/2017.
 */
public abstract class ViewMaps {
    protected GLGeometry shape;
    protected InternalViewNode node;
    protected ArrayList<ViewNode> childrens;
    protected Hashtable<ViewNode, ArrayList<Integer>> nodes;
    protected Hashtable<Integer, ArrayList<ViewNode>> keys;

    public ViewMaps(GLGeometry shape) {
        this.shape = shape;
        node = null;
        childrens = new ArrayList<ViewNode>();
        nodes = new Hashtable<ViewNode, ArrayList<Integer>>();
        keys = new Hashtable<Integer, ArrayList<ViewNode>>();
    }

    public GLGeometry getShape() {
        return shape;
    }

    public void setNode(InternalViewNode node) {
        this.node = node;
    }

    public boolean hasChildren() {
        return (childrens.size() > 0);
    }

    public void clearViewMapKeysForChildNode(ViewNode node) {
        if (!childrens.contains(node)) {
            return;
        }

        ArrayList<Integer> targetKeys = nodes.get(node);
        if (targetKeys != null) {
            for (Integer i : targetKeys) {
                ArrayList<ViewNode> targetNodes = keys.get(i);
                targetNodes.remove(node);
            }
            targetKeys.clear();
        }
    }

    public void addViewMapKeysForChildNode(ViewNode node) {
        if (!childrens.contains(node)) {
            return;
        }
        ArrayList<Integer> targetKeys = mapNodes(node);
        ArrayList<Integer> targetKeysOfNode = nodes.get(node);
        if (targetKeysOfNode == null) {
            targetKeysOfNode = new ArrayList<Integer>();
            nodes.put(node, targetKeysOfNode);
        }
        for (Integer i : targetKeys) {
            targetKeysOfNode.add(i);
            ArrayList<ViewNode> targetNodes = keys.get(i);
            if (targetNodes == null) {
                targetNodes = new ArrayList<ViewNode>();
                keys.put(i, targetNodes);
            }
            targetNodes.add(node);
        }
    }

    public void addChild(ViewNode node) {
        if (childrens.contains(node)) {
            return;
        }
        childrens.add(node);
        node.setParentNode(this.node);
        ArrayList<Integer> targetKeys = new ArrayList<Integer>();
        nodes.put(node, targetKeys);
        addChildNotify(node);
    }

    public boolean removeChild(ViewNode node) {
        if (!childrens.contains(node)) {
            return false;
        }
        removeChildNotify(node);
        clearViewMapKeysForChildNode(node);
        nodes.remove(node);
        childrens.remove(node);
        node.setParentNode(null);
        return true;
    }

    public abstract ArrayList<Integer> mapNodes(ViewNode node);

    public abstract boolean mapTouchEvent(Input input, List<TouchEvent> touchEvents,
                                          ArrayList<ViewNode> childNodes);

    public abstract void addChildNotify(ViewNode node);

    public abstract void removeChildNotify(ViewNode node);

    public abstract ArrayList<ViewNode> getChildDrawIterator();
}
