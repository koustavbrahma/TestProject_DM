package koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl;

import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkinterface.ViewNode;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLGeometry;

/**
 * Created by Koustav on 4/2/2017.
 */
public abstract class LeafViewNode extends ViewNode {


    public LeafViewNode(GLGeometry shape) {
        super(shape);
    }



}
