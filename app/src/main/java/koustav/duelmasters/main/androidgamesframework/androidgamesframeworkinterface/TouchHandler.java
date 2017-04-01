package koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface;

import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Input.TouchEvent;
import java.util.List;
import android.view.View.OnTouchListener;

/**
 * Created by: Koustav on 2/8/2015.
 * Abstract: interface for Touch handler.
 */
public interface TouchHandler extends  OnTouchListener {
    public  boolean isTouchDown(int pointer);
    public  int TouchType(int pointer);
    public  int getTouchX(int pointer);
    public  int getTouchY(int pointer);
    public List<TouchEvent> getTouchEvents();
    public GLGeometry.GLPoint getNearPoint(int pointer);
    public GLGeometry.GLPoint getFarPoint(int pointer);
    public float getNormalizedX(int pointer);
    public float getNormalizedY(int pointer);
    public void setMatrices(Object ...obj);
}
