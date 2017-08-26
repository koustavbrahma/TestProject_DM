package koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkinterface;

import java.util.List;

import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Input;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Input.*;

/**
 * Created by Koustav on 6/3/2017.
 */
public abstract class ViewMaps {
    protected GLGeometry shape;

    public ViewMaps(GLGeometry shape) {
        this.shape = shape;
    }

    public GLGeometry getShape() {
        return shape;
    }

    public int mapNodes(ViewNode node) {
        return 1;
    }

    public int mapTouchEvent(Input input, List<TouchEvent> touchEvents) {
        return 1;
    }
}
