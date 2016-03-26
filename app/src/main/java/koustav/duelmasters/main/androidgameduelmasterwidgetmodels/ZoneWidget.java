package koustav.duelmasters.main.androidgameduelmasterwidgetmodels;

import java.util.List;

import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.WidgetPosition;
import koustav.duelmasters.main.androidgamesframework.Input;

/**
 * Created by Koustav on 3/26/2016.
 */
public class ZoneWidget implements Widget {
    private static float width = 1f;
    private static float height = (26.0f * 2.0f)/36.0f ;

    public ZoneWidget() {

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
