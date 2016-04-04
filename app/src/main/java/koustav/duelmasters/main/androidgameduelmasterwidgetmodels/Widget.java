package koustav.duelmasters.main.androidgameduelmasterwidgetmodels;

import java.util.List;

import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.WidgetPosition;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameopenglobjectmodels.XZRectangle;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;
import koustav.duelmasters.main.androidgamesframework.Input;

/**
 * Created by Koustav on 3/16/2016.
 */
public interface Widget {
    public void draw(float deltaTime, float totalTime);
    public WidgetTouchEvent isTouched(List<Input.TouchEvent> touchEvents);
    public void setTranslateRotateScale(WidgetPosition position);
    public void ShadowEnable(boolean shadowEnable);
    public void LinkGLobject(Object ...objs);
    public void LinkLogicalObject(Object obj);
    public void setMode(WidgetMode mode);
}
