package koustav.duelmasters.main.androidgameopenglmotionmodel;

import java.util.ArrayList;

import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetPosition;
import koustav.duelmasters.main.androidgameopenglmotionutil.GLReferenceTracking;
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
    ArrayList<Float> trans_center_x, trans_center_y, trans_center_z, trans_angle_x, trans_angle_y, trans_angle_z, trans_scale_x, trans_scale_y, trans_scale_z;
    ArrayList<Float> tracking_point;

    ArrayList<Float> Y_val, X_val;

    public DriftSystem() {
        Position = new WidgetPosition();
        referenceTracking = new GLReferenceTracking();
        vector = new GLGeometry.GLVector(0, 0, 0);

        trans_center_x = new ArrayList<Float>();
        trans_center_y = new ArrayList<Float>();
        trans_center_z = new ArrayList<Float>();
        trans_angle_x = new ArrayList<Float>();
        trans_angle_y = new ArrayList<Float>();
        trans_angle_z = new ArrayList<Float>();
        trans_scale_x = new ArrayList<Float>();
        trans_scale_y = new ArrayList<Float>();
        trans_scale_z = new ArrayList<Float>();

        tracking_point = new ArrayList<Float>();

        Y_val = new ArrayList<Float>();
        X_val = new ArrayList<Float>();
    }

    public void setDriftInfo(WidgetPosition init_position, WidgetPosition ref_position, ArrayList<WidgetPosition> trans_position,
                             ArrayList<Float> tracking_point, float k1, float k2, float startingTime) {
        if ((trans_position != null && tracking_point == null) || (tracking_point != null && trans_position == null)) {
            throw new IllegalArgumentException("Invalid argument");
        }

        if (tracking_point != null && trans_position != null) {
            if (tracking_point.size() != trans_position.size()) {
                throw new IllegalArgumentException("Size must be same");
            }
        }
        this.tracking_point.clear();

        this.trans_center_x.clear();
        this.trans_center_y.clear();
        this.trans_center_z.clear();
        this.trans_angle_x.clear();
        this.trans_angle_y.clear();
        this.trans_angle_z.clear();
        this.trans_scale_x.clear();
        this.trans_scale_y.clear();
        this.trans_scale_z.clear();

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

        if (tracking_point != null) {
            for (int i = 0; i < tracking_point.size(); i++) {
                float val = tracking_point.get(i);
                if (!(val > 0 && val < 1)) {
                    throw new IllegalArgumentException("val must be inside zero and one");
                }
                if (this.tracking_point.contains(val)) {
                    throw new IllegalArgumentException("already contains same value");
                } else {
                    this.tracking_point.add(val);
                }
            }
        }
        if (trans_position != null) {
            for (int i = 0; i < trans_position.size(); i++) {
                WidgetPosition position = trans_position.get(i);
                trans_center_x.add(position.Centerposition.x);
                trans_center_y.add(position.Centerposition.y);
                trans_center_z.add(position.Centerposition.z);
                trans_angle_x.add(position.rotaion.angle * position.rotaion.x);
                trans_angle_y.add(position.rotaion.angle * position.rotaion.y);
                trans_angle_z.add(position.rotaion.angle * position.rotaion.z);
                trans_scale_x.add(position.X_scale);
                trans_scale_y.add(position.Y_scale);
                trans_scale_z.add(position.Z_scale);
            }
        }
        referenceTracking.clearRefTracking();
        referenceTracking.addRefTracking(0, k1, k2, startingTime);
    }

    private float solvePolynomial(ArrayList<Float> Y_val, ArrayList<Float> X_val, float x) {
        float val = Y_val.get(0);
        boolean useConstant = true;
        for (int i = 1; i < Y_val.size(); i++) {
            if (val != Y_val.get(i)) {
                useConstant = false;
                break;
            }
        }

        if (useConstant) {
            return val;
        } else {
            val = 0;
            for (int i = 0; i < Y_val.size(); i++) {
                float y0 = Y_val.get(i);
                float x0 = X_val.get(i);
                float product = 1f;
                for (int j = 0; j < X_val.size(); j++) {
                    if (i == j) {
                        continue;
                    }
                    float x1 = X_val.get(j);
                    product = product * ((x - x1)/(x0 - x1));
                }
                product = product * y0;
                val = val + product;
            }
            return val;
        }
    }

    private void LoadYXData(float init_val, float ref_val, ArrayList<Float> trans_val, ArrayList<Float> track_point) {
        Y_val.clear();
        X_val.clear();
        Y_val.add(init_val);
        X_val.add(new Float(0));
        for (int i = 0; i < trans_val.size(); i++) {
            Y_val.add(trans_val.get(i));
            X_val.add(track_point.get(i));
        }
        Y_val.add(ref_val);
        X_val.add(new Float(1f));
    }

    public WidgetPosition getUpdatePosition(float totalTime) {
        float ref_track = referenceTracking.getRefTrackingOutCome(0, totalTime);

        if (ref_track >= 0.999f && ref_track <=1.0f) {
            ref_track = 1.0f;
        }

        LoadYXData(init_center_x, ref_center_x, trans_center_x, tracking_point);
        Position.Centerposition.x = solvePolynomial(Y_val, X_val, ref_track);
        LoadYXData(init_center_y, ref_center_y, trans_center_y, tracking_point);
        Position.Centerposition.y = solvePolynomial(Y_val, X_val, ref_track);
        LoadYXData(init_center_z, ref_center_z, trans_center_z, tracking_point);
        Position.Centerposition.z = solvePolynomial(Y_val, X_val, ref_track);;

        LoadYXData(init_angle_x, ref_angle_x, trans_angle_x, tracking_point);
        vector.x = solvePolynomial(Y_val, X_val, ref_track);
        LoadYXData(init_angle_y, ref_angle_y, trans_angle_y, tracking_point);
        vector.y = solvePolynomial(Y_val, X_val, ref_track);
        LoadYXData(init_angle_z, ref_angle_z, trans_angle_z, tracking_point);
        vector.z = solvePolynomial(Y_val, X_val, ref_track);
        Position.rotaion.angle = vector.getMagnitude();

        GLGeometry.GLVector dir = vector.getDirection();
        Position.rotaion.x = dir.x;
        Position.rotaion.y = dir.y;
        Position.rotaion.z = dir.z;
        LoadYXData(init_scale_x, ref_scale_x, trans_scale_x, tracking_point);
        Position.X_scale = solvePolynomial(Y_val, X_val, ref_track);
        LoadYXData(init_scale_y, ref_scale_y, trans_scale_y, tracking_point);
        Position.Y_scale = solvePolynomial(Y_val, X_val, ref_track);
        LoadYXData(init_scale_z, ref_scale_z, trans_scale_z, tracking_point);
        Position.Z_scale = solvePolynomial(Y_val, X_val, ref_track);

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
