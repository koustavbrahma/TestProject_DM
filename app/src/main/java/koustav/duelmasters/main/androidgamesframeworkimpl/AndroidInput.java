package koustav.duelmasters.main.androidgamesframeworkimpl;

import java.util.List;

import android.content.Context;
import android.view.View;

import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgamesframework.Input;
import koustav.duelmasters.main.androidgamesframework.TouchHandler;

/**
 * Created by Koustav on 2/10/2015.
 * Abstract: Input implementation of out game framework ties together all the handlers.
 */
public class AndroidInput implements Input {
    KeyboardHandler keyHandler;
    TouchHandler touchHandler;

    public AndroidInput (Context context, View view, float scaleX, float scaleY) {
        keyHandler = new KeyboardHandler(view);
        touchHandler = new SingleTouchHandler(context, view, scaleX, scaleY);
    }

    @Override
    public  boolean isKeyPressed(int keyCode) {
        return keyHandler.isKeyPressed(keyCode);
    }
    @Override
    public List<KeyEvent> getKeyEvents() {
        return keyHandler.getKeyEvents();
    }
    @Override
    public  boolean isTouchDown(int pointer) {
        return touchHandler.isTouchDown(pointer);
    }
    @Override
    public  int getTouchX(int pointer) {
        return touchHandler.getTouchX(pointer);
    }
    @Override
    public  int getTouchY(int pointer) {
        return touchHandler.getTouchY(pointer);
    }

    @Override
    public float getNormalizedX(int pointer) {
        return touchHandler.getNormalizedX(pointer);
    }

    @Override
    public float getNormalizedY(int pointer) {
        return touchHandler.getNormalizedY(pointer);
    }

    @Override
    public List<TouchEvent> getTouchEvents() {
        return touchHandler.getTouchEvents();
    }

    @Override
    public GLGeometry.GLPoint getNearPoint(int pointer) {
        return touchHandler.getNearPoint(pointer);
    }

    @Override
    public GLGeometry.GLPoint getFarPoint(int pointer) {
        return touchHandler.getFarPoint(pointer);
    }

    @Override
    public void setMatrices(Object ...obj) {
        touchHandler.setMatrices(obj);
    }
}
