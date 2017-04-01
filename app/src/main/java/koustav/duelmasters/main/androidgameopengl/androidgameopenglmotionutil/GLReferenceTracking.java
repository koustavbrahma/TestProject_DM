package koustav.duelmasters.main.androidgameopengl.androidgameopenglmotionutil;

import java.util.Hashtable;


/**
 * Created by Koustav on 4/8/2016.
 */
public class GLReferenceTracking {
    public static class GLRefTrackingParameters {
        public float K1;
        public float K2;
        public float StartingTime;

        public GLRefTrackingParameters () {
            K1 = 0;
            K2 = 0;
            StartingTime = 0;
        }
    }

    Hashtable<Integer, GLRefTrackingParameters> ParameterList;

    public GLReferenceTracking() {
        ParameterList = new Hashtable<Integer, GLRefTrackingParameters>();
    }

    public void addRefTracking(int index, float K1, float K2, float StartingTime) {
        GLRefTrackingParameters parameters = new GLRefTrackingParameters();
        parameters.K1 = K1;
        parameters.K2 = K2;
        parameters.StartingTime = StartingTime;

        ParameterList.put(new Integer(index), parameters);
    }

    public float getRefTrackingOutCome(int index, float TotalTime) {
        GLRefTrackingParameters parameters = ParameterList.get(index);

        if (parameters == null) {
            return 0f;
        }

        float val = 1.0f - (2.0f * (float) Math.exp(-parameters.K1 * (TotalTime - parameters.StartingTime))) +
                (float) Math.exp(-parameters.K2 * (TotalTime - parameters.StartingTime));

        return val;
    }

    public float getRefTrackingDerivative(int index, float TotalTime) {
        GLRefTrackingParameters parameters = ParameterList.get(index);

        if (parameters == null) {
            return 0f;
        }

        float val = (2.0f * parameters.K1 * (float) Math.exp(-parameters.K1 * (TotalTime - parameters.StartingTime))) -
                parameters.K2 * (float) Math.exp(-parameters.K2 * (TotalTime - parameters.StartingTime));

        return val;
    }

    public void clearRefTracking() {
        ParameterList.clear();
    }

    public GLRefTrackingParameters getRefTrackingParameters(int index) {
        return ParameterList.get(index);
    }
}
