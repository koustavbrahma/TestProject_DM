package koustav.duelmasters.main.androidgameopengl.androidgameopenglutil;

/**
 * Created by Koustav on 2/7/2016.
 */
public class GLMaterial {
    public float[] Ka;
    public float[] Kd;
    public float[] Ks;
    public float Shininess;

    public GLMaterial(float[] ka, float[] kd, float[] ks, float shininess) {
        Ka = ka;
        Kd = kd;
        Ks = ks;
        Shininess = shininess;
    }
}
