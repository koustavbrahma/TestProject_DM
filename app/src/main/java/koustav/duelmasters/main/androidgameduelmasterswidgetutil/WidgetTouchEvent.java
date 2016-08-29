package koustav.duelmasters.main.androidgameduelmasterswidgetutil;

/**
 * Created by Koustav on 4/2/2016.
 */
public class WidgetTouchEvent {
    public boolean isTouched;
    public boolean isTouchedDown;
    public boolean isMoving;
    public boolean isDoubleTouched;
    public boolean wasUnderTheStack;
    public WidgetTouchFocusLevel isFocus;
    public Object object;

    public WidgetTouchEvent() {
        isTouched = false;
        isTouchedDown = false;
        isMoving = false;
        isDoubleTouched = false;
        wasUnderTheStack = false;
        isFocus = WidgetTouchFocusLevel.Low;
        object = null;
    }

    public void resetTouchEvent() {
        isTouched = false;
        isTouchedDown = false;
        isMoving = false;
        isDoubleTouched = false;
        wasUnderTheStack = false;
        isFocus = WidgetTouchFocusLevel.Low;
        object = null;
    }
}
