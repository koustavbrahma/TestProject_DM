package koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl;

import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLGeometry.*;

/**
 * Created by Koustav on 3/26/2016.
 */
public class ViewNodePosition {
    public GLPoint Centerposition;
    public GLAngularRotaion rotaion;
    public float X_scale;
    public float Y_scale;
    public float Z_scale;

    public ViewNodePosition() {
        Centerposition = new GLPoint(0, 0, 0);
        rotaion = new GLAngularRotaion(0, 0, 0, 0);
        X_scale = 1f;
        Y_scale = 1f;
        Z_scale = 1f;
    }
}
