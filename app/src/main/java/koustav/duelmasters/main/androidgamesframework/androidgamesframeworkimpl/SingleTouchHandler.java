package koustav.duelmasters.main.androidgamesframework.androidgamesframeworkimpl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLGeometry.*;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.UIHelper;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Pool;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Input.TouchEvent;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Pool.PoolObjectFactory;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.TouchHandler;

import static android.opengl.Matrix.multiplyMV;


/**
 * Created by: Koustav on 2/8/2015.
 * Abstract: Single touch handler class
 */
public class SingleTouchHandler implements TouchHandler{
    Context context;
    View view;
    boolean isTouched;
    int type;
    int touchX;
    int touchY;
    GLPoint[] nearPoint;
    GLPoint[] farPoint;
    float normalizedX;
    float normalizedY;

    float[] invertedOrthoProjectionMatrix;
    ArrayList<float[]> invertedViewProjectionMatrices;
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
        invertedViewProjectionMatrices = new ArrayList<float[]>();
        nearP = new float[4];
        farP = new float[4];
        nearPoint = new GLPoint[4];
        farPoint = new GLPoint[4];
        for (int i=0 ; i < 4; i++) {
            nearPoint[i] = new GLPoint(0, 0, 0);
            farPoint[i] = new GLPoint(0, 0, 0);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        synchronized (this) {
            TouchEvent touchEvent = touchEventPool.newObject();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchEvent.type = TouchEvent.TOUCH_DOWN;
                    type = TouchEvent.TOUCH_DOWN;
                    isTouched = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchEvent.type = TouchEvent.TOUCH_DRAGGED;
                    type = TouchEvent.TOUCH_DRAGGED;
                    isTouched = true;
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    touchEvent.type = TouchEvent.TOUCH_UP;
                    type = TouchEvent.TOUCH_UP;
                    isTouched =false;
                    break;
            }

            touchEvent.x = touchX = (int)(event.getX()*scaleX);
            touchEvent.y = touchY = (int)(event.getY()*scaleY);

            float LocalNormalizedX =
                    ((float) event.getX() / (float) ((AndroidGame)context).getframeBufferWidth()) * 2 - 1;
            float LocalNormalizedY =
                    -(((float)event.getY() / (float) ((AndroidGame)context).getframeBufferHeight()) * 2 - 1);

            float[] PointNdc = {LocalNormalizedX, LocalNormalizedY, 0, 1};

            if (invertedOrthoProjectionMatrix != null) {
                multiplyMV(
                        nearP, 0, invertedOrthoProjectionMatrix, 0, PointNdc, 0);
                UIHelper.divideByW(nearP);

                touchEvent.normalizedX = normalizedX = nearP[0];
                touchEvent.normalizedY = normalizedY = nearP[1];
            }

            if (invertedViewProjectionMatrices.size() > 0) {
                for (int i = 0; i < invertedViewProjectionMatrices.size(); i++) {
                    float[] nearPointNdc = {LocalNormalizedX, LocalNormalizedY, -1, 1};
                    float[] farPointNdc = {LocalNormalizedX, LocalNormalizedY, 1, 1};

                    multiplyMV(
                            nearP, 0, invertedViewProjectionMatrices.get(i), 0, nearPointNdc, 0);
                    multiplyMV(
                            farP, 0, invertedViewProjectionMatrices.get(i), 0, farPointNdc, 0);
                    UIHelper.divideByW(nearP);
                    UIHelper.divideByW(farP);

                    touchEvent.nearPoint[i].x = nearPoint[i].x = nearP[0];
                    touchEvent.nearPoint[i].y = nearPoint[i].y = nearP[1];
                    touchEvent.nearPoint[i].z = nearPoint[i].z = nearP[2];

                    touchEvent.farPoint[i].x = farPoint[i].x = farP[0];
                    touchEvent.farPoint[i].y = farPoint[i].y = farP[1];
                    touchEvent.farPoint[i].z = farPoint[i].z = farP[2];
                }
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
    public int TouchType(int pointer) {
        synchronized (this) {
            return type;
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
    public GLPoint getNearPoint(int pointer) {
        synchronized (this) {
            return nearPoint[pointer];
        }
    }

    @Override
    public GLPoint getFarPoint(int pointer) {
        synchronized (this) {
            return farPoint[pointer];
        }
    }

    @Override
    public float getNormalizedX(int pointer) {
        synchronized (this) {
            return normalizedX;
        }
    }

    @Override
    public float getNormalizedY(int pointer) {
        synchronized (this) {
            return normalizedY;
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
    public void setMatrices(Object ...obj) {
        synchronized (this) {
            // First argument must be inverted Ortho Projection matrix
            this.invertedOrthoProjectionMatrix = (float[]) obj[0];
            invertedViewProjectionMatrices.clear();
            for (int i = 1; i < obj.length; i++) {
                invertedViewProjectionMatrices.add((float[]) obj[i]);
            }
        }
    }
}
