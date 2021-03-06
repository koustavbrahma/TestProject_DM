package koustav.duelmasters.main.androidgameopengl.androidgameopenglutil;

import java.util.ArrayList;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl.ViewNodePosition;
import koustav.duelmasters.main.androidgameopengl.androidgameopengllightscamerashades.LightsCameraShades;

import static android.opengl.Matrix.*;

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

    public static void setTranslateRotateScale(LightsCameraShades lightsCameraShades, ViewNodePosition position) {
        setIdentityM(lightsCameraShades.modelMatrix, 0);
        translateM(lightsCameraShades.modelMatrix, 0, position.Centerposition.x, position.Centerposition.y,
                position.Centerposition.z);
        if (position.rotaion.angle != 0) {
            rotateM(lightsCameraShades.modelMatrix, 0, position.rotaion.angle, position.rotaion.x,
                    position.rotaion.y, position.rotaion.z);
        }
        scaleM(lightsCameraShades.modelMatrix, 0, position.X_scale, position.Y_scale, position.Z_scale);
        multiplyMM(lightsCameraShades.modelViewMatrix, 0, lightsCameraShades.viewMatrix, 0, lightsCameraShades.modelMatrix, 0);
        invertM(lightsCameraShades.tempMatrix, 0, lightsCameraShades.modelViewMatrix, 0);
        transposeM(lightsCameraShades.it_modelViewMatrix, 0, lightsCameraShades.tempMatrix, 0);
        multiplyMM(
                lightsCameraShades.modelViewProjectionMatrix, 0,
                lightsCameraShades.projectionMatrix, 0,
                lightsCameraShades.modelViewMatrix, 0);

        multiplyMM(
                lightsCameraShades.ShadowMatrix, 0,
                lightsCameraShades.depthVPMatrix, 0,
                lightsCameraShades.modelMatrix, 0);
    }

    public static void setTranslateRotateScale(ViewNodePosition position) {
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

    public static void setTranslate(LightsCameraShades lightsCameraShades, ViewNodePosition position) {
        setIdentityM(lightsCameraShades.modelMatrix, 0);
        translateM(lightsCameraShades.modelMatrix, 0, position.Centerposition.x, position.Centerposition.y,
                0);
        if (position.rotaion.angle != 0) {
            rotateM(lightsCameraShades.modelMatrix, 0, position.rotaion.angle, 0,
                    0, 1);
        }
        scaleM(lightsCameraShades.modelMatrix, 0, position.X_scale, position.Y_scale, 1);
        multiplyMM(
                lightsCameraShades.modelOrthoProjectionMatrix, 0,
                lightsCameraShades.OrthoProjectionMatrix, 0,
                lightsCameraShades.modelMatrix, 0);
    }

    public static void setTranslate(ViewNodePosition position) {
        setIdentityM(AssetsAndResource.modelMatrix, 0);
        translateM(AssetsAndResource.modelMatrix, 0, position.Centerposition.x, position.Centerposition.y,
                0);
        if (position.rotaion.angle != 0) {
            rotateM(AssetsAndResource.modelMatrix, 0, position.rotaion.angle, 0,
                    0, 1);
        }
        scaleM(AssetsAndResource.modelMatrix, 0, position.X_scale, position.Y_scale, 1);
        multiplyMM(
                AssetsAndResource.modelOrthoProjectionMatrix, 0,
                AssetsAndResource.OrthoProjectionMatrix, 0,
                AssetsAndResource.modelMatrix, 0);
    }

    public static GLGeometry.GLAngularRotaion getCombinedRotation(LightsCameraShades lightsCameraShades, ArrayList<GLGeometry.GLAngularRotaion> rotations) {
        setIdentityM(lightsCameraShades.tempMatrix, 0);
        int count = 0;
        for (int i = rotations.size() -1; i >=0; i--) {
            GLGeometry.GLAngularRotaion rotaion = rotations.get(i);
            if (rotaion.angle != 0) {
                rotateM(lightsCameraShades.tempMatrix, 0, rotaion.angle, rotaion.x, rotaion.y,
                        rotaion.z);
                count++;
            }
        }

        if (count == 0) {
            return new GLGeometry.GLAngularRotaion(0,0,0,0);
        }

        GLGeometry.GLAngularRotaion rotaionOut = new GLGeometry.GLAngularRotaion(0, 0, 0, 0);

        float trace = lightsCameraShades.tempMatrix[0] + lightsCameraShades.tempMatrix[5] +
                lightsCameraShades.tempMatrix[10];

        rotaionOut.angle = (float) Math.toDegrees(Math.acos((trace - 1.0f)/2.0f));

        rotaionOut.x = (lightsCameraShades.tempMatrix[6] -lightsCameraShades.tempMatrix[9])/(2.0f * (float) Math.sin(rotaionOut.angle));
        rotaionOut.y = (lightsCameraShades.tempMatrix[8] -lightsCameraShades.tempMatrix[2])/(2.0f * (float) Math.sin(rotaionOut.angle));
        rotaionOut.z = (lightsCameraShades.tempMatrix[1] -lightsCameraShades.tempMatrix[4])/(2.0f * (float) Math.sin(rotaionOut.angle));

        float mag = (float) Math.sqrt((rotaionOut.x *rotaionOut.x) + (rotaionOut.y * rotaionOut.y) + (rotaionOut.z * rotaionOut.z));

        rotaionOut.x = rotaionOut.x/mag;
        rotaionOut.y = rotaionOut.y/mag;
        rotaionOut.z = rotaionOut.z/mag;

        return rotaionOut;
    }

    public static GLGeometry.GLAngularRotaion getCombinedRotation(ArrayList<GLGeometry.GLAngularRotaion> rotations) {
        setIdentityM(AssetsAndResource.tempMatrix, 0);
        int count = 0;
        for (int i = rotations.size() -1; i >=0; i--) {
            GLGeometry.GLAngularRotaion rotaion = rotations.get(i);
            if (rotaion.angle != 0) {
                rotateM(AssetsAndResource.tempMatrix, 0, rotaion.angle, rotaion.x, rotaion.y,
                        rotaion.z);
                count++;
            }
        }

        if (count == 0) {
            return new GLGeometry.GLAngularRotaion(0,0,0,0);
        }

        GLGeometry.GLAngularRotaion rotaionOut = new GLGeometry.GLAngularRotaion(0, 0, 0, 0);

        float trace = AssetsAndResource.tempMatrix[0] + AssetsAndResource.tempMatrix[5] +
                AssetsAndResource.tempMatrix[10];

        rotaionOut.angle = (float) Math.toDegrees(Math.acos((trace - 1.0f)/2.0f));

        rotaionOut.x = (AssetsAndResource.tempMatrix[6] -AssetsAndResource.tempMatrix[9])/(2.0f * (float) Math.sin(rotaionOut.angle));
        rotaionOut.y = (AssetsAndResource.tempMatrix[8] -AssetsAndResource.tempMatrix[2])/(2.0f * (float) Math.sin(rotaionOut.angle));
        rotaionOut.z = (AssetsAndResource.tempMatrix[1] -AssetsAndResource.tempMatrix[4])/(2.0f * (float) Math.sin(rotaionOut.angle));

        float mag = (float) Math.sqrt((rotaionOut.x *rotaionOut.x) + (rotaionOut.y * rotaionOut.y) + (rotaionOut.z * rotaionOut.z));

        rotaionOut.x = rotaionOut.x/mag;
        rotaionOut.y = rotaionOut.y/mag;
        rotaionOut.z = rotaionOut.z/mag;

        return rotaionOut;
    }
}
