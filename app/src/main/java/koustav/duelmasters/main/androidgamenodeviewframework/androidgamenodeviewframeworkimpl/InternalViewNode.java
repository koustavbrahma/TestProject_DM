package koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl;

import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkinterface.ViewMaps;
import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkinterface.ViewNode;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLGeometry.*;

/**
 * Created by Koustav on 4/2/2017.
 */
public class InternalViewNode extends ViewNode {
    ChildNodes childNodes;

    public InternalViewNode(ViewMaps maps) {
        super(maps.getShape());
        childNodes = new ChildNodes(this, maps);
    }

    @Override
    public void draw() {

    }

    @Override
    public void update(float deltaTime, float totalTime) {

    }
}
