package koustav.duelmasters.main.androidgameopenglanimation;

import java.util.Hashtable;

import koustav.duelmasters.main.androidgameduelmasterswidget.WidgetPosition;
import koustav.duelmasters.main.androidgameopenglmotionmodel.GLReferenceTracking;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;

/**
 * Created by Koustav on 4/30/2016.
 */
public class DriftSystem {
    WidgetPosition Position;
    float ref_center_x, ref_center_y, ref_center_z, ref_angle_x, ref_angle_y, ref_angle_z, ref_scale_x, ref_scale_y, ref_scale_z;
    float init_center_x, init_center_y, init_center_z, init_angle_x, init_angle_y, init_angle_z, init_scale_x, init_scale_y, init_scale_z;
    GLReferenceTracking referenceTracking;
    GLGeometry.GLVector vector;

    public DriftSystem() {
        Position = new WidgetPosition();
        referenceTracking = new GLReferenceTracking();
        vector = new GLGeometry.GLVector(0, 0, 0);
    }

    public void setDriftInfo(WidgetPosition init_position, WidgetPosition ref_position, float k1, float k2, float startingTime) {
        init_center_x = init_position.Centerposition.x;
        init_center_y = init_position.Centerposition.y;
        init_center_z = init_position.Centerposition.z;
        init_angle_x = init_position.rotaion.angle * init_position.rotaion.x;
        init_angle_y = init_position.rotaion.angle * init_position.rotaion.y;
        init_angle_z = init_position.rotaion.angle * init_position.rotaion.z;
        init_scale_x = init_position.X_scale;
        init_scale_y = init_position.Y_scale;
        init_scale_z = init_position.Z_scale;

        ref_center_x = ref_position.Centerposition.x;
        ref_center_y = ref_position.Centerposition.y;
        ref_center_z = ref_position.Centerposition.z;
        ref_angle_x = ref_position.rotaion.angle * ref_position.rotaion.x;
        ref_angle_y = ref_position.rotaion.angle * ref_position.rotaion.y;
        ref_angle_z = ref_position.rotaion.angle * ref_position.rotaion.z;
        ref_scale_x = ref_position.X_scale;
        ref_scale_y = ref_position.Y_scale;
        ref_scale_z = ref_position.Z_scale;

        referenceTracking.clearRefTracking();
        referenceTracking.addRefTracking(0, k1, k2, startingTime);
    }

    public WidgetPosition getUpdatePosition(float totalTime) {
        float ref_track = referenceTracking.getRefTrackingOutCome(0, totalTime);

        if (ref_track >= 0.999f && ref_track <=1.0f) {
            ref_track = 1.0f;
        }

        Position.Centerposition.x = (1.0f - ref_track) * init_center_x + ref_track * ref_center_x;
        Position.Centerposition.z = (1.0f - ref_track) * init_center_z + ref_track * ref_center_z;
        Position.Centerposition.y = (1.0f - ref_track) * init_center_y + ref_track * ref_center_y;

        vector.x = (1.0f - ref_track) * init_angle_x + ref_track * ref_angle_x;
        vector.y = (1.0f - ref_track) * init_angle_y + ref_track * ref_angle_y;
        vector.z = (1.0f - ref_track) * init_angle_z + ref_track * ref_angle_z;
        Position.rotaion.angle = vector.getMagnitude();

        GLGeometry.GLVector dir = vector.getDirection();
        Position.rotaion.x = dir.x;
        Position.rotaion.y = dir.y;
        Position.rotaion.z = dir.z;
        Position.X_scale = (1.0f - ref_track) * init_scale_x + ref_track * ref_scale_x;
        Position.Y_scale = (1.0f - ref_track) * init_scale_y + ref_track * ref_scale_y;
        Position.Z_scale = (1.0f - ref_track) * init_scale_z + ref_track * ref_scale_z;

        return Position;
    }

    public float getPercentageComplete(float totalTime) {
        float ref_track = referenceTracking.getRefTrackingOutCome(0, totalTime);

        if (ref_track >= 0.999f && ref_track <=1.0f) {
            ref_track = 1.0f;
        }

        return ref_track;
    }

    public float getCurrentDerivative(float totalTime) {
        return referenceTracking.getRefTrackingDerivative(0, totalTime);
    }
}
