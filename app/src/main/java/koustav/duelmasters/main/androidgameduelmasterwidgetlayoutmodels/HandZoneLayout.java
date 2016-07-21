package koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmastersutil.SetUnsetUtil;
import koustav.duelmasters.main.androidgameduelmasterswidget.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayout.Layout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgameopenglutil.UIHelper;
import koustav.duelmasters.main.androidgamesframework.Input;
import koustav.duelmasters.main.androidgamesframework.Pool;

import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;

/**
 * Created by Koustav on 7/11/2016.
 */
public class HandZoneLayout implements Layout  {

    Pool<DynamicCardSlotLayout> cardSlotLayoutPool;

    DynamicCardSlotLayout SelectedCardSlot;
    ArrayList<DynamicCardSlotLayout> CardSlot;

    Hashtable<CardWidget, DynamicCardSlotLayout> WidgetToSlotMapping;

    float width;
    float normalizedY;
    float perpendicular_x;
    float perpendicular_y;
    float perpendicular_z;
    float k1;
    float k2;

    float center_x;
    float center_y;
    float center_z;
    float angle;
    float rotationDir_x;
    float rotationDir_y;
    float rotationDir_z;
    float gap_vec_x;
    float gap_vec_y;
    float gap_vec_z;

    public HandZoneLayout() {
        Pool.PoolObjectFactory<DynamicCardSlotLayout> factory = new Pool.PoolObjectFactory<DynamicCardSlotLayout>() {
            @Override
            public DynamicCardSlotLayout createObject() {
                return new DynamicCardSlotLayout();
            }
        };

        cardSlotLayoutPool = new Pool<DynamicCardSlotLayout>(factory, 40);

        SelectedCardSlot = null;
        CardSlot = new ArrayList<DynamicCardSlotLayout>();

        WidgetToSlotMapping = new Hashtable<CardWidget, DynamicCardSlotLayout>();
        width = 0;
        normalizedY = 0f;
        perpendicular_x = 0f;
        perpendicular_y = 0f;
        perpendicular_z = 0f;
        k1 = 2f;
        k2 = 2f;

        center_x = 0f;
        center_y = 0f;
        center_z = 0f;
        angle = 0f;
        rotationDir_x = 0f;
        rotationDir_y = 1f;
        rotationDir_z = 0f;
        gap_vec_x = 1f;
        gap_vec_y = 0f;
        gap_vec_z = 0f;
    }

    public void InitializeHandZoneLayout(float width, float normalizedY, float normalFromOrigin_x, float normalFromOrigin_y,
                                         float normalFromOrigin_z, float k1, float k2) {

        this.width = width;
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

        GLGeometry.GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithPlane(
                new GLGeometry.GLRay(new GLGeometry.GLPoint(nearPoint.x, nearPoint.y, nearPoint.z),
                        GLGeometry.GLVectorBetween(nearPoint, farPoint)),
                new GLGeometry.GLPlane(new GLGeometry.GLPoint(perpendicular_x, perpendicular_y, perpendicular_z),
                        new GLGeometry.GLVector(perpendicular_x, perpendicular_y, perpendicular_z)));
        center_x = intersectingPoint.x;
        center_y = intersectingPoint.y;
        center_z = intersectingPoint.z;

        GLGeometry.GLVector rotationAngle = new GLGeometry.GLVector(0, 1f, 0f).crossProduct(new GLGeometry.GLVector(perpendicular_x,
                perpendicular_y, perpendicular_z).getDirection());
        angle = (float) Math.toDegrees(Math.asin(rotationAngle.getMagnitude()));
        GLGeometry.GLVector rotationDir = rotationAngle.getDirection();
        rotationDir_x = rotationDir.x;
        rotationDir_y = rotationDir.y;
        rotationDir_z = rotationDir.z;

        float[] GapVector = new float[4];
        setIdentityM(AssetsAndResource.tempMatrix, 0);
        if (angle != 0) {
            rotateM(AssetsAndResource.tempMatrix, 0, angle, rotationDir_x, rotationDir_y, rotationDir_z);
        }
        multiplyMV(GapVector, 0, AssetsAndResource.tempMatrix, 0, new float[] {1,
                0, 0, 0f}, 0);

        GLGeometry.GLVector gapVec = new GLGeometry.GLVector(GapVector[0], GapVector[1], GapVector[2]).getDirection();
        gap_vec_x = gapVec.x;
        gap_vec_y = gapVec.y;
        gap_vec_z = gapVec.z;
    }

    @Override
    public void update(float deltaTime, float totalTime) {
        for (int i = 0; i < CardSlot.size(); i++) {
            DynamicCardSlotLayout slotLayout = CardSlot.get(i);
            slotLayout.update(deltaTime, totalTime);
        }

    }

    @Override
    public void draw() {
        int index = CardSlot.size();
        if (SelectedCardSlot != null) {
            index = CardSlot.indexOf(SelectedCardSlot);
        }

        for (int i = 0; i < index; i++) {
            DynamicCardSlotLayout slotLayout =  CardSlot.get(i);
            slotLayout.draw();
        }

        for (int i = CardSlot.size() - 1; i >= index; i--) {
            DynamicCardSlotLayout slotLayout =  CardSlot.get(i);
            slotLayout.draw();
        }
    }

    @Override
    public WidgetTouchEvent TouchResponse(List<Input.TouchEvent> touchEvents) {
        return null;
    }

    public void AddCardWidgetToZone(CardWidget widget) {
        DynamicCardSlotLayout slotLayout = cardSlotLayoutPool.newObject();
        slotLayout.initializeSlot(center_x, center_y, center_z, angle, rotationDir_x, rotationDir_y, rotationDir_z,
                widget, 4f, 4f);
        WidgetToSlotMapping.put(widget, slotLayout);

        CardSlot.add(slotLayout);

        if (CardSlot.size() == 1) {
            SelectedCardSlot = slotLayout;
        }

        int midPoint = CardSlot.size()/2 + CardSlot.size()%2 -1;

        float gap = this.width / CardSlot.size();

        if (gap >= AssetsAndResource.CardWidth/2) {
            gap = AssetsAndResource.CardWidth/2;
        }

        for (int i = 0; i < CardSlot.size(); i++) {
            float x = ((center_x) + (((float) (i - midPoint)) * gap * gap_vec_x));
            float y = ((center_y) + (((float) (i - midPoint)) * gap * gap_vec_y));
            float z = ((center_z) + (((float) (i - midPoint)) * gap * gap_vec_z));

            DynamicCardSlotLayout slotLayout1 = CardSlot.get(i);
            slotLayout1.setSlotPosition(x, y, z);
        }
    }

    public CardWidget RemoveCardWidgetFromZone(CardWidget widget) {
        DynamicCardSlotLayout slotLayout = WidgetToSlotMapping.get(widget);
        boolean selectedSlotRemoved = false;

        if (slotLayout == null) {
            return null;
        }

        if (SelectedCardSlot == slotLayout) {
            selectedSlotRemoved = true;
        }

        if (selectedSlotRemoved) {
            int indexofSlot = CardSlot.indexOf(slotLayout);
            if (indexofSlot > 0) {
                SelectedCardSlot = CardSlot.get(indexofSlot - 1);
            } else {
                SelectedCardSlot = null;
            }
        }

        CardSlot.remove(slotLayout);

        if (CardSlot.size() > 0) {
            int midPoint = CardSlot.size() / 2 + CardSlot.size() % 2 - 1;

            float gap = this.width / CardSlot.size();

            if (gap >= AssetsAndResource.CardWidth/2) {
                gap = AssetsAndResource.CardWidth/2;
            }

            for (int i = 0; i < CardSlot.size(); i++) {
                float x = ((center_x) + (((float) (i - midPoint)) * gap * gap_vec_x));
                float y = ((center_y) + (((float) (i - midPoint)) * gap * gap_vec_y));
                float z = ((center_z) + (((float) (i - midPoint)) * gap * gap_vec_z));

                DynamicCardSlotLayout slotLayout1 = CardSlot.get(i);
                slotLayout1.setSlotPosition(x, y, z);
            }
        }

        WidgetToSlotMapping.remove(widget);
        slotLayout.resetSlot();
        cardSlotLayoutPool.free(slotLayout);

        return widget;
    }
}
