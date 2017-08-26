package koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkinterface.ViewMaps;
import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkinterface.ViewNode;

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
        childrens.add(node);
    }

    public boolean removeChild(ViewNode node) {
        if (!childrens.contains(node)) {
            return false;
        }
        ArrayList<Integer> targetKeys = nodes.get(node);
        if (targetKeys == null) {
            throw new RuntimeException("Keys not found");
        }
        for (Integer i: targetKeys) {
            ArrayList<ViewNode> targetNodes =  keys.get(i);
            targetNodes.remove(node);
        }
        targetKeys.clear();
        nodes.remove(node);
        childrens.remove(node);
        return true;
    }
}
