package koustav.duelmasters.main.androidgameopenglutil;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.WidgetPosition;

import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;
import static android.opengl.Matrix.transposeM;

/**
 * Created by Koustav on 1/18/2016.
 */
public class MatrixHelper {
    public static void perspectiveM(float[] m, float yFovInDegrees, float aspect,
                                    float n, float f) {
        final float angleInRadians = (float) (yFovInDegrees * Math.PI / 180.0);
        final float a = (float) (1.0 / Math.tan(angleInRadians / 2.0));
        m[0] = a / aspect;
        m[1] = 0f;
        m[2] = 0f;
        m[3] = 0f;
        m[4] = 0f;
        m[5] = a;
        m[6] = 0f;
        m[7] = 0f;
        m[8] = 0f;
        m[9] = 0f;
        m[10] = -((f + n) / (f - n));
        m[11] = -1f;
        m[12] = 0f;
        m[13] = 0f;
        m[14] = -((2f * f * n) / (f - n));
        m[15] = 0f;
    }

    public static void setTranslateRotateScale(WidgetPosition position) {
        setIdentityM(AssetsAndResource.modelMatrix, 0);
        translateM(AssetsAndResource.modelMatrix, 0, position.Centerposition.x, position.Centerposition.y,
                position.Centerposition.z);
        if (position.rotaion.angle != 0) {
            rotateM(AssetsAndResource.modelMatrix, 0, position.rotaion.angle, position.rotaion.x,
                    position.rotaion.y, position.rotaion.z);
        }
        scaleM(AssetsAndResource.modelMatrix, 0, position.X_scale, position.Y_scale, position.Z_scale);
        multiplyMM(AssetsAndResource.modelViewMatrix, 0, AssetsAndResource.viewMatrix, 0, AssetsAndResource.modelMatrix, 0);
        invertM(AssetsAndResource.tempMatrix, 0, AssetsAndResource.modelViewMatrix, 0);
        transposeM(AssetsAndResource.it_modelViewMatrix, 0, AssetsAndResource.tempMatrix, 0);
        multiplyMM(
                AssetsAndResource.modelViewProjectionMatrix, 0,
                AssetsAndResource.projectionMatrix, 0,
                AssetsAndResource.modelViewMatrix, 0);

        multiplyMM(
                AssetsAndResource.ShadowMatrix, 0,
                AssetsAndResource.depthVPMatrix, 0,
                AssetsAndResource.modelMatrix, 0);
    }
}
