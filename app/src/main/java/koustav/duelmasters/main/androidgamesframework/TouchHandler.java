package koustav.duelmasters.main.androidgamesframework;

import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgamesframework.Input.TouchEvent;
import java.util.List;
import android.view.View.OnTouchListener;

/**
 * Created by: Koustav on 2/8/2015.
 * Abstract: interface for Touch handler.
 */
public interface TouchHandler extends  OnTouchListener {
    public  boolean isTouchDown(int pointer);
    public  int getTouchX(int pointer);
    public  int getTouchY(int pointer);
    public List<TouchEvent> getTouchEvents();
    public GLGeometry.GLPoint getNearPoint(int pointer);
    public GLGeometry.GLPoint getFarPoint(int pointer);
    public void setIVPMatrix(float[] Matrix);
}
