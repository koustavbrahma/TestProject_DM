package koustav.duelmasters.main.androidgameduelmasterswidget;

/**
 * Created by Koustav on 4/2/2016.
 */
public class WidgetTouchEvent {
    public boolean isTouched;
    public boolean isTouchedDown;
    public boolean isMoving;
    public boolean isDoubleTouched;
    public Object object;

    public WidgetTouchEvent() {
        isTouched = false;
        isTouchedDown = false;
        isMoving = false;
        isDoubleTouched = false;
        object = null;
    }
}
