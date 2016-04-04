package koustav.duelmasters.main.androidgamesframework;

import java.util.List;

import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;

/**
 * Created by: Koustav on 2/7/2015.
 * Abstract: Basic interface for Input.
 */
public interface Input {
    public static class KeyEvent {
        public static final int KEY_DOWN = 0;
        public static final int KEY_UP = 1;

        public int type;
        public int keyCode;
        public char keyChar;
    }

    public static class TouchEvent {
        public static final int TOUCH_DOWN = 0;
        public static final int TOUCH_UP = 1;
        public static final int TOUCH_DRAGGED =2;

        public int type;
        public int x, y;
        public int pointer;
        public GLPoint[] nearPoint;
        public GLPoint[] farPoint;
        public float normalizedX, normalizedY;

        public TouchEvent() {
            nearPoint = new GLPoint[4];
            farPoint = new GLPoint[4];
            for (int i=0 ; i < 4; i++) {
                nearPoint[i] = new GLPoint(0, 0, 0);
                farPoint[i] = new GLPoint(0, 0, 0);
            }
        }
    }

    public  boolean isKeyPressed(int keyCode);
    public List<KeyEvent> getKeyEvents();
    public  boolean isTouchDown(int pointer);
    public  int getTouchX(int pointer);
    public  int getTouchY(int pointer);
    public List<TouchEvent> getTouchEvents();
    public GLPoint getNearPoint(int pointer);
    public GLPoint getFarPoint(int pointer);
    public float getNormalizedX(int pointer);
    public float getNormalizedY(int pointer);
    public void setMatrices(Object ...obj);
}
