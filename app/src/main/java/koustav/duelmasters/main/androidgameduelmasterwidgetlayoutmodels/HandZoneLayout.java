package koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels;

import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmasterswidget.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayout.Layout;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgameopenglutil.UIHelper;
import koustav.duelmasters.main.androidgamesframework.Input;
import koustav.duelmasters.main.androidgamesframework.Pool;

import static android.opengl.Matrix.multiplyMV;

/**
 * Created by Koustav on 7/11/2016.
 */
public class HandZoneLayout implements Layout  {

    Pool<DynamicCardSlotLayout> cardSlotLayoutPool;

    float normalizedY;
    float perpendicular_x;
    float perpendicular_y;
    float perpendicular_z;
    float k1;
    float k2;

    public HandZoneLayout() {
        Pool.PoolObjectFactory<DynamicCardSlotLayout> factory = new Pool.PoolObjectFactory<DynamicCardSlotLayout>() {
            @Override
            public DynamicCardSlotLayout createObject() {
                return new DynamicCardSlotLayout();
            }
        };

        cardSlotLayoutPool = new Pool<DynamicCardSlotLayout>(factory, 40);

        normalizedY = 0f;
        perpendicular_x = 0f;
        perpendicular_y = 0f;
        perpendicular_z = 0f;
        k1 = 2f;
        k2 =2f;
    }

    public void InitializeHandZoneLayout(float normalizedY, float normalFromOrigin_x, float normalFromOrigin_y,
                                         float normalFromOrigin_z, float k1, float k2) {

        this.normalizedY = normalizedY;
        this.perpendicular_x = normalFromOrigin_x;
        this.perpendicular_y = normalFromOrigin_y;
        this.perpendicular_z = normalFromOrigin_z;
        this.k1 = k1;
        this.k2 = k2;

        float[] nearPointNdc = {0, normalizedY, -1, 1};
        float[] farPointNdc = {0, normalizedY, 1, 1};

        float[] nearP = new float[4];
        float[] farP = new float[4];
        multiplyMV(
                nearP, 0, AssetsAndResource.invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        multiplyMV(
                farP, 0, AssetsAndResource.invertedViewProjectionMatrix, 0, farPointNdc, 0);
        UIHelper.divideByW(nearP);
        UIHelper.divideByW(farP);

        GLGeometry.GLPoint nearPoint = new GLGeometry.GLPoint(0, 0, 0);
        GLGeometry.GLPoint farPoint = new GLGeometry.GLPoint(0, 0, 0);
        nearPoint.x = nearP[0];
        nearPoint.y = nearP[1];
        nearPoint.z = nearP[2];

        farPoint.x = farP[0];
        farPoint.y = farP[1];
        farPoint.z = farP[2];
    }
    @Override
    public void update(float deltaTime, float totalTime) {

    }

    @Override
    public void draw() {

    }

    @Override
    public WidgetTouchEvent TouchResponse(List<Input.TouchEvent> touchEvents) {
        return null;
    }
}
