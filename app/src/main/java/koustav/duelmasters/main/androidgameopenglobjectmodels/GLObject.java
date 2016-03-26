package koustav.duelmasters.main.androidgameopenglobjectmodels;

import koustav.duelmasters.main.androidgameopenglutil.GLMaterial;

/**
 * Created by Koustav on 2/16/2016.
 */
public abstract class GLObject {
    GLMaterial Material;

    public GLObject(GLMaterial Material) {
        this.Material = Material;
    }

    public GLMaterial getMaterial() {
        return Material;
    }

    public abstract void bindData(int aPositionLocation, int aNormalLocation, int aTextureCoordinatesLocation);

    public abstract void draw();
}
