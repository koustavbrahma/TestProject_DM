package koustav.duelmasters.main.androidgameopenglutil;

/**
 * Created by Koustav on 2/16/2016.
 */
public class MathHelper {
    public static float GaussFun1D(int index, float sigma2) {
        return ((float) Math.exp(-index*index / (2 * sigma2))) / ((float) Math.sqrt(2 * Math.PI * sigma2));
    }
}
