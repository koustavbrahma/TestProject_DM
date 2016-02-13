package koustav.duelmasters.main.androidgameopenglutil;

import android.util.FloatMath;

import java.util.ArrayList;
import java.util.List;

import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;
import static android.opengl.GLES20.*;
/**
 * Created by Koustav on 1/23/2016.
 */
public class ObjectBuilder {
    private static final int FLOATS_PER_VERTEX = 6;
    private final float[] vertexData;
    private final List<DrawCommand> drawList;
    private int offset = 0;
    private ObjectBuilder(int sizeInVertices) {
        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
        drawList = new ArrayList<DrawCommand>();
    }

    public static interface DrawCommand {
        void draw();
    }

    private static int sizeOfCircleInVertices(int numPoints) {
        return 1 + (numPoints + 1);
    }

    private static int sizeOfOpenCylinderInVertices(int numPoints) {
        return (numPoints + 1) * 2;
    }

    private void appendCircle(GLCircle circle, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfCircleInVertices(numPoints);
        // Center point of fan
        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;
        vertexData[offset++] = 0.0f;
        vertexData[offset++] = 1.0f;
        vertexData[offset++] = 0.0f;
        // Fan around center point. <= is used because we want to generate
        // the point at the starting angle twice to complete the fan.
        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians =
                    ((float) i / (float) numPoints)
                            * ((float) Math.PI * 2f);
            vertexData[offset++] =
                    circle.center.x
                            + circle.radius * FloatMath.cos(angleInRadians);
            vertexData[offset++] = circle.center.y;
            vertexData[offset++] =
                    circle.center.z
                            + circle.radius * FloatMath.sin(angleInRadians);
            vertexData[offset++] = 0.0f;
            vertexData[offset++] = 1.0f;
            vertexData[offset++] = 0.0f;
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices);
            }
        });
    }

    private void appendOpenCylinder(GLCylinder cylinder, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfOpenCylinderInVertices(numPoints);
        final float yStart = cylinder.center.y - (cylinder.height / 2f);
        final float yEnd = cylinder.center.y + (cylinder.height / 2f);

        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians =
                    ((float) i / (float) numPoints)
                            * ((float) Math.PI * 2f);
            float xPosition =
                    cylinder.center.x
                            + cylinder.radius * FloatMath.cos(angleInRadians);
            float zPosition =
                    cylinder.center.z
                            + cylinder.radius * FloatMath.sin(angleInRadians);
            vertexData[offset++] = xPosition;
            vertexData[offset++] = yStart;
            vertexData[offset++] = zPosition;
            vertexData[offset++] = xPosition;
            vertexData[offset++] = 0.0f;
            vertexData[offset++] = zPosition;
            vertexData[offset++] = xPosition;
            vertexData[offset++] = yEnd;
            vertexData[offset++] = zPosition;
            vertexData[offset++] = xPosition;
            vertexData[offset++] = 0.0f;
            vertexData[offset++] = zPosition;
        }
        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices);
            }
        });
    }

    private void appendOpenConicalFrustum(GLConicalFrustum frustum, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfOpenCylinderInVertices(numPoints);
        final float yStart = frustum.center.y - (frustum.height / 2f);
        final float yEnd = frustum.center.y + (frustum.height / 2f);

        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians =
                    ((float) i / (float) numPoints)
                            * ((float) Math.PI * 2f);
            float x1Position =
                    frustum.center.x
                            + frustum.botradius * FloatMath.cos(angleInRadians);
            float z1Position =
                    frustum.center.z
                            + frustum.botradius * FloatMath.sin(angleInRadians);
            float x2Position =
                    frustum.center.x
                            + frustum.topradius * FloatMath.cos(angleInRadians);
            float z2Position =
                    frustum.center.z
                            + frustum.topradius * FloatMath.sin(angleInRadians);
            float dx = x1Position - x2Position;
            float dy = yStart - yEnd;
            float dz = z1Position - z2Position;

            float xNorm, yNorm, zNorm;
            if (dy != 0) {
                yNorm = -(dx * x1Position + dz *z1Position)/dy;
                xNorm = x1Position;
                zNorm = z1Position;
            } else {
                zNorm = -(dx * x1Position + dy * yStart) / dz;
                xNorm = x1Position;
                yNorm = yStart;
            }
            vertexData[offset++] = x1Position;
            vertexData[offset++] = yStart;
            vertexData[offset++] = z1Position;
            vertexData[offset++] = xNorm;
            vertexData[offset++] = yNorm;
            vertexData[offset++] = zNorm;
            vertexData[offset++] = x2Position;
            vertexData[offset++] = yEnd;
            vertexData[offset++] = z2Position;
            vertexData[offset++] = xNorm;
            vertexData[offset++] = yNorm;
            vertexData[offset++] = zNorm;
        }
        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices);
            }
        });
    }

    public static class GeneratedData {
        public final float[] vertexData;
        public final List<DrawCommand> drawList;
        GeneratedData(float[] vertexData, List<DrawCommand> drawList) {
            this.vertexData = vertexData;
            this.drawList = drawList;
        }
    }

    private GeneratedData build() {
        return new GeneratedData(vertexData, drawList);
    }

    public static GeneratedData createPuck(GLCylinder puck, int numPoints) {
        int size = sizeOfCircleInVertices(numPoints)
                + sizeOfOpenCylinderInVertices(numPoints);
        ObjectBuilder builder = new ObjectBuilder(size);
        GLCircle puckTop = new GLCircle(
                puck.center.translateY(puck.height / 2f),
                puck.radius);
        builder.appendCircle(puckTop, numPoints);
        builder.appendOpenCylinder(puck, numPoints);
        return builder.build();
    }

    public static GeneratedData createMallet(
            GLPoint center, float radius, float height, int numPoints) {
        int size = sizeOfCircleInVertices(numPoints) * 2
                + sizeOfOpenCylinderInVertices(numPoints) * 2;
        ObjectBuilder builder = new ObjectBuilder(size);
        // First, generate the mallet base.
        float baseHeight = height * 0.25f;
        GLCircle baseCircle = new GLCircle(
                center.translateY(-baseHeight),
                radius);
        GLCylinder baseCylinder = new GLCylinder(
                baseCircle.center.translateY(-baseHeight / 2f),
                radius, baseHeight);
        builder.appendCircle(baseCircle, numPoints);
        builder.appendOpenCylinder(baseCylinder, numPoints);

        float handleHeight = height * 0.75f;
        float handleRadius = radius / 3f;
        GLCircle handleCircle = new GLCircle(
                center.translateY(height * 0.5f),
                handleRadius);
        GLCylinder handleCylinder = new GLCylinder(
                handleCircle.center.translateY(-handleHeight / 2f),
                handleRadius, handleHeight);
        builder.appendCircle(handleCircle, numPoints);
        builder.appendOpenCylinder(handleCylinder, numPoints);

        return builder.build();
    }

    public static GeneratedData createSphere(GLPoint center, float radius, int numPoints) {
        int frustumcount = (int)(numPoints/2) + numPoints%2;
        int size = sizeOfOpenCylinderInVertices(numPoints) * frustumcount;
        ObjectBuilder builder = new ObjectBuilder(size);

        for (int i = 0; i < frustumcount; i++) {
            float angleInRadians1 =
                    ((float) i / (float) frustumcount)
                            * ((float) Math.PI);

            float angleInRadians2 =
                    ((float) (i+1) / (float) frustumcount)
                            * ((float) Math.PI);

            float y1Position =
                    center.y - radius * FloatMath.cos(angleInRadians1);
            float y2Position =
                    center.y - radius * FloatMath.cos(angleInRadians2);

            float botradius =  Math.abs(radius * FloatMath.sin(angleInRadians1));
            float topradius =  Math.abs(radius * FloatMath.sin(angleInRadians2));

            GLConicalFrustum frustum = new GLConicalFrustum(center.translateY((y1Position + y2Position)/ 2f),
                    topradius, botradius, Math.abs(y1Position - y2Position));
            builder.appendOpenConicalFrustum(frustum, numPoints);
        }
        return builder.build();
    }
}
