package koustav.duelmasters.main.androidgamesframework;

import java.util.List;

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
    }

    public  boolean isKeyPressed(int keyCode);
    public List<KeyEvent> getKeyEvents();
    public  boolean isTouchDown(int pointer);
    public  int getTouchX(int pointer);
    public  int getTouchY(int pointer);
    public List<TouchEvent> getTouchEvents();
}
