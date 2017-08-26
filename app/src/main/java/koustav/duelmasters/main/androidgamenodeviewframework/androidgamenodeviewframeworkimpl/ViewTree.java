package koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl;

import java.util.ArrayList;

import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkinterface.ViewMaps;
import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkinterface.ViewNode;

/**
 * Created by Koustav on 4/2/2017.
 */
public class ViewTree {
    ArrayList<ViewNode> Nodes;
    ViewNode RootNode;

    public ViewTree() {
        Nodes = new ArrayList<ViewNode>();
        RootNode = null;
    }

    public InternalViewNode createInternalNode(ViewMaps maps) {
        return new InternalViewNode(maps);
    }

    public LeafViewNode createLeafNode() {
        return null;
    }

    public void setRootNode(ViewNode node) {
        RootNode = node;
    }


}
