package koustav.duelmasters.main.androidgameopenglobjectmodels;

import koustav.duelmasters.main.androidgameopenglutil.GLMaterial;

/**
 * Created by Koustav on 2/16/2016.
 */
public class GLObject {
    GLMaterial Material;

    public GLObject(GLMaterial Material) {
        this.Material = Material;
    }

    public GLMaterial getMaterial() {
        return Material;
    }
}
