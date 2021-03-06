package koustav.duelmasters.main.androidgameduelmasterswidgetutil;

import java.util.List;

import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl.ViewNodePosition;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Input;

/**
 * Created by Koustav on 3/16/2016.
 */
public interface Widget {
    public void draw();
    public void update(float deltaTime, float totalTime);
    public WidgetTouchEvent isTouched(List<Input.TouchEvent> touchEvents);
    public void setTranslateRotateScale(ViewNodePosition position);
    public void ShadowEnable(boolean shadowEnable);
    public void LinkGLobject(Object ...objs);
    public void LinkLogicalObject(Object obj);
    public Object getLogicalObject();
    public void setMode(WidgetMode mode);
    public ViewNodePosition getPosition();
}
