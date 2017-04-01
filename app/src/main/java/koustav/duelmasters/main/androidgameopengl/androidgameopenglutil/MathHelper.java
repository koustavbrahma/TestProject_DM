package koustav.duelmasters.main.androidgameopengl.androidgameopenglutil;

/**
 * Created by Koustav on 2/16/2016.
 */
public class MathHelper {
    public static float GaussFun1D(int index, float sigma2) {
        return ((float) Math.exp(-index*index / (2 * sigma2))) / ((float) Math.sqrt(2 * Math.PI * sigma2));
    }

    public static float UnitaryMethod(float x, float y, float a) {
        float b = (y*a)/x;
        return b;
    }
}
