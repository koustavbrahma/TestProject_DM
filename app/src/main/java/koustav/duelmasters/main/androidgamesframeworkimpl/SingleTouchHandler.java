package koustav.duelmasters.main.androidgamesframeworkimpl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;
import koustav.duelmasters.main.androidgameopenglutil.UIHelper;
import koustav.duelmasters.main.androidgamesframework.Input;
import koustav.duelmasters.main.androidgamesframework.Pool;
import koustav.duelmasters.main.androidgamesframework.Input.TouchEvent;
import koustav.duelmasters.main.androidgamesframework.Pool.PoolObjectFactory;
import koustav.duelmasters.main.androidgamesframework.TouchHandler;

import static android.opengl.Matrix.multiplyMV;


/**
 * Created by: Koustav on 2/8/2015.
 * Abstract: Single touch handler class
 */
public class SingleTouchHandler implements TouchHandler{
    Context context;
    View view;
    boolean isTouched;
    int touchX;
    int touchY;
    GLPoint nearPoint;
    GLPoint farPoint;
    float[] invertedViewProjectionMatrix;
    Pool<TouchEvent> touchEventPool;
    List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
    List<TouchEvent> touchEventsBuffer = new ArrayList<TouchEvent>();
    float scaleX;
    float scaleY;
    float[] nearP;
    float[] farP;

    public SingleTouchHandler(Context context, View view, float scaleX, float scaleY) {
        PoolObjectFactory<TouchEvent> factory = new PoolObjectFactory<TouchEvent>() {
            @Override
            public TouchEvent createObject() {
                return new TouchEvent();
            }
        } ;
        this.context = context;
        this.view = view;
        touchEventPool = new Pool<TouchEvent>(factory, 100);
        view.setOnTouchListener(this);
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        invertedViewProjectionMatrix = null;
        nearP = new float[4];
        farP = new float[4];
        nearPoint = new GLPoint(0, 0 ,0);
        farPoint = new GLPoint(0, 0, 0);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        synchronized (this) {
            TouchEvent touchEvent = touchEventPool.newObject();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchEvent.type = TouchEvent.TOUCH_DOWN;
                    isTouched = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchEvent.type = TouchEvent.TOUCH_DRAGGED;
                    isTouched = true;
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    touchEvent.type = TouchEvent.TOUCH_UP;
                    isTouched =false;
                    break;
            }

            touchEvent.x = touchX = (int)(event.getX()*scaleX);
            touchEvent.y = touchY = (int)(event.getY()*scaleY);

            if (invertedViewProjectionMatrix != null) {
                float normalizedX =
                        ((float) event.getX() / (float) view.getWidth()) * 2 - 1;
                float normalizedY =
                        -(((float)event.getY() / (float) view.getHeight()) * 2 - 1);
                float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
                float[] farPointNdc = {normalizedX, normalizedY, 1, 1};

                multiplyMV(
                        nearP, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
                multiplyMV(
                        farP, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);
                UIHelper.divideByW(nearP);
                UIHelper.divideByW(farP);

                touchEvent.nearPoint.x = nearPoint.x = nearP[0];
                touchEvent.nearPoint.y = nearPoint.y = nearP[1];
                touchEvent.nearPoint.z = nearPoint.z = nearP[2];

                touchEvent.farPoint.x = farPoint.x = farP[0];
                touchEvent.farPoint.y = farPoint.y = farP[1];
                touchEvent.farPoint.z = farPoint.z = farP[2];
            }

            touchEventsBuffer.add(touchEvent);
            return true;
        }
    }

    @Override
    public boolean isTouchDown(int pointer) {
        synchronized (this) {
            if (pointer == 0)
                return isTouched;
            else
                return false;
        }
    }

    @Override
    public int getTouchX(int pointer) {
        synchronized (this) {
            return touchX;
        }
    }

    @Override
    public int getTouchY(int pointer) {
        synchronized (this) {
            return touchY;
        }
    }

    @Override
    public List<TouchEvent> getTouchEvents() {
        synchronized (this) {
            int len = touchEvents.size();
            for(int i = 0; i < len; i++)
                touchEventPool.free(touchEvents.get(i));
            touchEvents.clear();
            touchEvents.addAll(touchEventsBuffer);
            touchEventsBuffer.clear();
            return touchEvents;
        }
    }

    @Override
    public GLPoint getNearPoint(int pointer) {
        synchronized (this) {
            return nearPoint;
        }
    }

    @Override
    public GLPoint getFarPoint(int pointer) {
        synchronized (this) {
            return farPoint;
        }
    }

    @Override
    public void setIVPMatrix(float[] Matrix) {
        synchronized (this) {
            this.invertedViewProjectionMatrix = Matrix;
        }
    }
}
