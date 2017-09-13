package koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkinterface.ViewMaps;
import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkinterface.ViewNode;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Input;

/**
 * Created by Koustav on 4/22/2017.
 */
public class ChildNodes {
    ViewNode parentNode;
    ArrayList<ViewNode> childrens;
    Hashtable<ViewNode, ArrayList<Integer>> nodes;
    Hashtable<Integer, ArrayList<ViewNode>> keys;
    ViewMaps maps;

    public ChildNodes(ViewNode parent, ViewMaps maps) {
        parentNode = parent;
        childrens = new ArrayList<ViewNode>();
        nodes = new Hashtable<ViewNode, ArrayList<Integer>>();
        keys = new Hashtable<Integer, ArrayList<ViewNode>>();
        this.maps = maps;
    }

    public void addChild(ViewNode node) {
        if (childrens.contains(node)) {
            return;
        }
        childrens.add(node);
        ArrayList<Integer> targetKeys = new ArrayList<Integer>();
        nodes.put(node, targetKeys);
    }

    public boolean removeChild(ViewNode node) {
        if (!childrens.contains(node)) {
            return false;
        }
        clearViewMapKeysForChildNode(node);
        nodes.remove(node);
        childrens.remove(node);
        return true;
    }

    public boolean isTouched(Input input, List<Input.TouchEvent> touchEvents) {
        if (!(nodes.size() > 0)) {
            return false;
        }
        int key = maps.mapTouchEvent(input, touchEvents);
        ArrayList<ViewNode> childNodes = keys.get(key);
        ViewNode touchedChild = null;
        if (childNodes != null) {
            for (ViewNode node : childNodes) {
                if (node.isTouched(input, touchEvents)) {
                    touchedChild = node;
                    break;
                }
            }
        }
        if ((touchedChild != null) || (key > 0)) {
            return true;
        }
        return false;
    }

    public Iterator<ViewNode> getChildUpdateIterator() {
        return null;
    }

    public Iterator<ViewNode> getChildDrawIterator() {
        return null;
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
        ArrayList<Integer> targetKeys = maps.mapNodes(node);
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
}
