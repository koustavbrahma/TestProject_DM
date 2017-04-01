package koustav.duelmasters.main.androidgameopengl.androidgameopenglutil;

import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkimpl.AndroidGame;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLGeometry.*;
import static android.opengl.Matrix.*;

/**
 * Created by Koustav on 1/26/2016.
 */
public class UIHelper {
    public static class GLNormalized2DPoint {
        public float normalizedX;
        public float normalizedY;

        public GLNormalized2DPoint(float x, float y) {
            normalizedX = x;
            normalizedY = y;
        }
    }

    public static GLNormalized2DPoint ConvertTouchEventToNormalized2DPoint(AndroidGame game, int event_x, int event_y) {
        // Convert touch coordinates into normalized device
        // coordinates, keeping in mind that Android's Y
        // coordinates are inverted.
        final float normalizedX =
                (event_x / (float)game.getViewObj().getWidth()) * 2 - 1;
        final float normalizedY =
                -((event_y / (float) game.getViewObj().getHeight()) * 2 - 1);
        return new GLNormalized2DPoint(normalizedX, normalizedY);
    }

    public static GLRay convertNormalized2DPointToRay(float[] invertedViewProjectionMatrix,
                                                    float normalizedX, float normalizedY) {
        // We'll convert these normalized device coordinates into world-space
        // coordinates. We'll pick a point on the near and far planes, and draw a
        // line between them. To do this transform, we need to first multiply by
        // the inverse matrix, and then we need to undo the perspective divide.
        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};
        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];
        multiplyMV(
                nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        multiplyMV(
                farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);
        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        GLPoint nearPointRay =
                new GLPoint(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
        GLPoint farPointRay =
                new GLPoint(farPointWorld[0], farPointWorld[1], farPointWorld[2]);

        return new GLRay(nearPointRay,
                GLGeometry.GLVectorBetween(nearPointRay, farPointRay));
    }

    public static void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }
}
