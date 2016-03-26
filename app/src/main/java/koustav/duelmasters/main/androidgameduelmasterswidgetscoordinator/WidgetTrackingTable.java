package koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator;

import java.util.Hashtable;
import java.util.List;

import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.Widget;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;
import koustav.duelmasters.main.androidgamesframework.Input;

/**
 * Created by Koustav on 3/12/2016.
 */

public class WidgetTrackingTable implements Widget{
    Hashtable<Widget, WidgetPosition> widgetTrackingTable;

    public WidgetTrackingTable() {
        widgetTrackingTable = new Hashtable<Widget, WidgetPosition>();
    }

    @Override
    public void draw() {

    }

    @Override
    public boolean isTouched(List<Input.TouchEvent> touchEvents) {
        return true;
    }

    @Override
    public void setTranslateRotateScale(WidgetPosition position) {

    }

    @Override
    public void ShadowEnable(boolean shadowEnable) {

    }

    @Override
    public void LinkGLobject(Object ...objs) {

    }

    @Override
    public void LinkLogicalObject(Object obj) {

    }

    @Override
    public void setMode(int i) {

    }
}
