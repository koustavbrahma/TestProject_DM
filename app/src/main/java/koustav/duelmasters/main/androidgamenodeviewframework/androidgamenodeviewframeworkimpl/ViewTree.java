package koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl;

import java.util.ArrayList;
import java.util.List;

import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkinterface.ViewMaps;
import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkinterface.ViewNode;
import koustav.duelmasters.main.androidgameopengl.androidgameopengllightscamerashades.LightsCameraShades;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Input;

/**
 * Created by Koustav on 4/2/2017.
 */
public class ViewTree {
    ArrayList<ViewNode> Nodes;
    ViewNode RootNode;
    ViewNode DragNode;
    ViewNode PopUpNode;
    ArrayList<LeafViewNodeGroup> groups;
    LightsCameraShades lightsCameraShades;
    int IdCounter;

    public ViewTree(ViewMaps maps, LightsCameraShades lightsCameraShades) {
        Nodes = new ArrayList<ViewNode>();
        RootNode = new InternalViewNode(this, maps);
        Nodes.add(0, RootNode);
        DragNode = null;
        PopUpNode = null;
        groups = new ArrayList<LeafViewNodeGroup>();
        this.lightsCameraShades = lightsCameraShades;
        IdCounter = 1;
    }

    public LightsCameraShades getLightsCameraShades() {
        return lightsCameraShades;
    }

    public int createInternalNode(ViewMaps maps) {
        InternalViewNode newNode = new InternalViewNode(this, maps);
        int i = IdCounter;
        IdCounter ++;
        Nodes.add(i, newNode);
        return i;
    }

    public int addLeafNodeToTree(ViewNode node) {
        if (node == null) {
            return -1;
        }
        if (Nodes.contains(node)) {
            return getIdOfNode(node);
        }

        int i = IdCounter;
        IdCounter ++;
        Nodes.add(i, node);
        return i;
    }

    public void setLeafViewNodeGroups(Object ...objects) {
        groups.clear();
        for (int i = 0; i < objects.length; i++) {
            groups.add((LeafViewNodeGroup)objects[i]);
        }
    }

    public boolean containsLeafViewNodeGroup(LeafViewNodeGroup group) {
        if (groups.contains(LeafViewNodeGroup.ALL)) {
            return true;
        }
        return groups.contains(group);
    }

    public void setParentOfChid(int parentId, int childId) {
        ViewNode chidNode = Nodes.get(childId);
        ViewNode parentNode = Nodes.get(parentId);

        if (chidNode == null || parentNode == null) {
            return;
        }

        if (chidNode.getParentNode() == parentNode) {
            return;
        }
        orphanChild(childId);
        ((InternalViewNode)parentNode).addChild(chidNode);
    }

    public void orphanChild(int childId) {
        ViewNode chidNode = Nodes.get(childId);
        if (chidNode == null) {
            return;
        }
        if (chidNode.getParentNode() != null) {
            InternalViewNode oldParentNode = chidNode.getParentNode();
            oldParentNode.removeChild(chidNode);
            chidNode.setParentNode(null);
        }
    }

    public int getIdOfNode(ViewNode node) {
        int id = -1;
        if (Nodes.contains(node)) {
            return Nodes.indexOf(node);
        }
        return id;
    }

    public ViewNode getViewNode(int id) {
        ViewNode node = Nodes.get(id);
        return node;
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
