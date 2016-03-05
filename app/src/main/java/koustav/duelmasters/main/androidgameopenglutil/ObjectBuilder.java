package koustav.duelmasters.main.androidgameopenglutil;

import android.util.FloatMath;

import java.util.ArrayList;
import java.util.List;

import koustav.duelmasters.main.androidgameduelmastersdatastructure.Maze;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;
import static android.opengl.GLES20.*;
/**
 * Created by Koustav on 1/23/2016.
 * All object created by this class lay on XZ plane
 */
public class ObjectBuilder {
    public enum TextureMode{
       NOTEXTURE,
       TEXTURE2D,
       TEXTURECUBE,
    };
    private static final int FLOATS_PER_VERTEX = 6;
    private static final int FLOATS_PER_VERTEX_TEXTURE = 8;
    private static final int FLOATS_PER_VERTEX_TEXTURE_CUBE = 9;
    private final float[] vertexData;
    private final List<DrawCommand> drawList;
    private int offset = 0;
    private ObjectBuilder(int sizeInVertices, TextureMode texture) {
        if (texture == TextureMode.TEXTURE2D) {
            vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX_TEXTURE];
        }else if (texture == TextureMode.TEXTURECUBE) {
            vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX_TEXTURE_CUBE];
        } else {
            vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
        }
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

    private static int sizeOfArchInVertices(int numPoints) {
        return (numPoints+ 1) * 2;
    }

    private static int sizeOfRectangleInVertices() { return 6;}

    private static int sizeOfCubeInVertices() {
        return 6 * 6;
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

    private void appendRectangle(GLRectangle rectangle, boolean textureMode, float CenterS, float CenterT, float width, float height, int orientation) {
        final int startVertex = textureMode? offset/FLOATS_PER_VERTEX_TEXTURE: offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfRectangleInVertices();

        orientation = orientation % 4;

        float[] S = {CenterS - width/2, CenterS - width/2, CenterS + width/2, CenterS + width/2};
        float[] T = {CenterT - height/2, CenterT + height/2, CenterT + height/2, CenterT - height/2};

        if (textureMode) {
            float tmp, tmp1;
            for (int i = 0; i < orientation; i++) {
                tmp = S[3];
                tmp1 = T[3];

                for (int j = 3; j > 0; j--) {
                    S[j] = S[j - 1];
                    T[j] = T[j - 1];
                }

                S[0] = tmp;
                T[0] = tmp1;
            }
        }
        vertexData[offset++] = rectangle.center.x;
        vertexData[offset++] = rectangle.center.y;
        vertexData[offset++] = rectangle.center.z;
        vertexData[offset++] = 0f;
        vertexData[offset++] = 1.0f;
        vertexData[offset++] = 0f;
        if (textureMode) {
            vertexData[offset++] = CenterS;
            vertexData[offset++] = CenterT;
        }

        vertexData[offset++] = rectangle.center.x - rectangle.width/2;
        vertexData[offset++] = rectangle.center.y;
        vertexData[offset++] = rectangle.center.z - rectangle.height/2;
        vertexData[offset++] = 0f;
        vertexData[offset++] = 1.0f;
        vertexData[offset++] = 0f;
        if (textureMode) {
            vertexData[offset++] = S[0];
            vertexData[offset++] = T[0];
        }

        vertexData[offset++] = rectangle.center.x - rectangle.width/2;
        vertexData[offset++] = rectangle.center.y;
        vertexData[offset++] = rectangle.center.z + rectangle.height/2;
        vertexData[offset++] = 0f;
        vertexData[offset++] = 1.0f;
        vertexData[offset++] = 0f;
        if (textureMode) {
            vertexData[offset++] = S[1];
            vertexData[offset++] = T[1];
        }

        vertexData[offset++] = rectangle.center.x + rectangle.width/2;
        vertexData[offset++] = rectangle.center.y;
        vertexData[offset++] = rectangle.center.z + rectangle.height/2;
        vertexData[offset++] = 0f;
        vertexData[offset++] = 1.0f;
        vertexData[offset++] = 0f;
        if (textureMode) {
            vertexData[offset++] = S[2];
            vertexData[offset++] = T[2];
        }

        vertexData[offset++] = rectangle.center.x + rectangle.width/2;
        vertexData[offset++] = rectangle.center.y;
        vertexData[offset++] = rectangle.center.z - rectangle.height/2;
        vertexData[offset++] = 0f;
        vertexData[offset++] = 1.0f;
        vertexData[offset++] = 0f;
        if (textureMode) {
            vertexData[offset++] = S[3];
            vertexData[offset++] = T[3];
        }

        vertexData[offset++] = rectangle.center.x - rectangle.width/2;
        vertexData[offset++] = rectangle.center.y;
        vertexData[offset++] = rectangle.center.z - rectangle.height/2;
        vertexData[offset++] = 0f;
        vertexData[offset++] = 1.0f;
        vertexData[offset++] = 0f;
        if (textureMode) {
            vertexData[offset++] = S[0];
            vertexData[offset++] = T[0];
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices);
            }
        });
    }

    private void appendArchUpOrDown(GLArch arch, int numPoints, boolean textureMode, float CenterS, float CenterT, float width, float height, boolean up) {
        int sign = up  ? -1 : 1;
        float archTop = arch.center.z + (float) sign * (arch.radius + arch.fallhieght);

        float TofarchTop = up  ? CenterT - height/2 : CenterT + height/2;

        float archAngleInRadians = ((float)Math.PI/180) * arch.angle;
        float angleoffset = ((float) -sign) * (float) Math.PI/2 - archAngleInRadians/2;

        float SRatio = Math.abs((arch.center.x + arch.radius * FloatMath.cos(archAngleInRadians + angleoffset)) -
                (arch.center.x + arch.radius * FloatMath.cos(angleoffset)));
        float TRatio = Math.abs(archTop - (arch.center.z - arch.radius * FloatMath.sin(angleoffset)));
        float Toffset = up ? 0f : 1.0f;
        float Soffset = up ? 1.0f : 0f;

        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians =
                    (((float) i / (float) numPoints)
                            * (archAngleInRadians)) + angleoffset;
            float xPosition =
                    arch.center.x
                            + arch.radius * FloatMath.cos(angleInRadians);
            float zPosition =
                    arch.center.z
                            - arch.radius * FloatMath.sin(angleInRadians);
            float S = Math.abs(Math.abs(xPosition -  (arch.center.x + arch.radius * FloatMath.cos(angleoffset)))/SRatio - Soffset);
            float T = Math.abs(Math.abs(archTop - zPosition)/TRatio - Toffset);
            S = (S - 0.5f) * width + CenterS;
            T = (T - 0.5f) * height + CenterT;
            vertexData[offset++] = xPosition;
            vertexData[offset++] = arch.center.y;
            vertexData[offset++] = zPosition;
            vertexData[offset++] = 0f;
            vertexData[offset++] = 1.0f;
            vertexData[offset++] = 0f;
            if (textureMode) {
                vertexData[offset++] = S;
                vertexData[offset++] = T;
            }

            vertexData[offset++] = xPosition;
            vertexData[offset++] = arch.center.y;
            vertexData[offset++] = archTop;
            vertexData[offset++] = 0f;
            vertexData[offset++] = 1.0f;
            vertexData[offset++] = 0f;
            if (textureMode) {
                vertexData[offset++] = S;
                vertexData[offset++] = TofarchTop;
            }
        }
    }

    private void appendArchRightOrLeft(GLArch arch, int numPoints, boolean textureMode, float CenterS, float CenterT, float width, float height, boolean right) {
        int sign = right ? 1 : -1;
        float RefAngle = (float) (right ? 0f : Math.PI);
        float archTop = arch.center.x + (float) sign * (arch.radius + arch.fallhieght);

        float SofarchTop = right ? CenterS + width/2 : CenterS - width/2;

        float archAngleInRadians = ((float)Math.PI/180) * arch.angle;
        float angleoffset = RefAngle - archAngleInRadians/2;

        float TRatio = Math.abs((arch.center.z - arch.radius * FloatMath.sin(archAngleInRadians + angleoffset)) -
                (arch.center.z - arch.radius * FloatMath.sin(angleoffset)));
        float SRatio = Math.abs(archTop - (arch.center.x + arch.radius * FloatMath.cos(angleoffset)));
        float Toffset = right ? 1.0f : 0f;
        float Soffset = right ? 1.0f : 0f;
        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians =
                    (((float) i / (float) numPoints)
                            * (archAngleInRadians)) + angleoffset;
            float xPosition =
                    arch.center.x
                            + arch.radius * FloatMath.cos(angleInRadians);
            float zPosition =
                    arch.center.z
                            - arch.radius * FloatMath.sin(angleInRadians);
            float T = Math.abs(Math.abs(zPosition -  (arch.center.z - arch.radius * FloatMath.sin(angleoffset)))/TRatio - Toffset);
            float S = Math.abs(Math.abs(archTop - xPosition)/SRatio - Soffset);

            S = (S - 0.5f) * width + CenterS;
            T = (T - 0.5f) * height + CenterT;
            vertexData[offset++] = xPosition;
            vertexData[offset++] = arch.center.y;
            vertexData[offset++] = zPosition;
            vertexData[offset++] = 0f;
            vertexData[offset++] = 1.0f;
            vertexData[offset++] = 0f;
            if (textureMode) {
                vertexData[offset++] = S;
                vertexData[offset++] = T;
            }

            vertexData[offset++] = archTop;
            vertexData[offset++] = arch.center.y;
            vertexData[offset++] = zPosition;
            vertexData[offset++] = 0f;
            vertexData[offset++] = 1.0f;
            vertexData[offset++] = 0f;
            if (textureMode) {
                vertexData[offset++] = SofarchTop;
                vertexData[offset++] = T;
            }
        }
    }

    private void appendArch(GLArch arch, int numPoints, boolean textureMode, float CenterS, float CenterT, float width, float height, int orientation) {
        final int startVertex = textureMode? offset/FLOATS_PER_VERTEX_TEXTURE: offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfArchInVertices(numPoints);
        orientation = orientation % 4;

        if (orientation == 0 || orientation ==2) {
            appendArchUpOrDown(arch, numPoints, textureMode, CenterS, CenterT, width, height, (orientation == 0) ? true : false);
        } else {
            appendArchRightOrLeft(arch, numPoints, textureMode, CenterS, CenterT, width, height, orientation == 3 ? true : false);
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices);
            }
        });
    }

    private  void appendCube(GLCube cube, boolean texture) {
        final int startVertex = texture? offset/ FLOATS_PER_VERTEX_TEXTURE_CUBE: offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfCubeInVertices();

        ArrayList<GLPoint> CubePoints = new ArrayList<GLPoint>();
        CubePoints.add(0, new GLPoint((float)(cube.center.x - cube.width/2), (float)(cube.center.y + cube.length/2),
                (float) (cube.center.z + cube.height/2)));  // (0) Top-left near
        CubePoints.add(1, new GLPoint((float)(cube.center.x + cube.width/2), (float)(cube.center.y + cube.length/2),
                (float) (cube.center.z + cube.height/2)));  // (1) Top-right near
        CubePoints.add(2, new GLPoint((float)(cube.center.x - cube.width/2), (float)(cube.center.y - cube.length/2),
                (float) (cube.center.z + cube.height/2)));  // (2) Bottom-left near
        CubePoints.add(3, new GLPoint((float)(cube.center.x + cube.width/2), (float)(cube.center.y - cube.length/2),
                (float) (cube.center.z + cube.height/2)));  // (3) Bottom-right near
        CubePoints.add(4, new GLPoint((float)(cube.center.x - cube.width/2), (float)(cube.center.y + cube.length/2),
                (float) (cube.center.z - cube.height/2)));  // (4) Top-left far
        CubePoints.add(5, new GLPoint((float)(cube.center.x + cube.width/2), (float)(cube.center.y + cube.length/2),
                (float) (cube.center.z - cube.height/2)));  // (5) Top-right far
        CubePoints.add(6, new GLPoint((float)(cube.center.x - cube.width/2), (float)(cube.center.y - cube.length/2),
                (float) (cube.center.z - cube.height/2)));  // (6) Bottom-left far
        CubePoints.add(7, new GLPoint((float)(cube.center.x + cube.width/2), (float)(cube.center.y - cube.length/2),
                (float) (cube.center.z - cube.height/2)));  // (7) Bottom-right far

        // Front
        vertexData[offset++] = CubePoints.get(1).x; vertexData[offset++] = CubePoints.get(1).y; vertexData[offset++] = CubePoints.get(1).z;
        vertexData[offset++] = 0f; vertexData[offset++] = 0f; vertexData[offset++] = 1.0f;
        if (texture) {
            vertexData[offset++] = 1;
            vertexData[offset++] = 0;
            vertexData[offset++] = 5;
        }
        vertexData[offset++] = CubePoints.get(0).x; vertexData[offset++] = CubePoints.get(0).y; vertexData[offset++] = CubePoints.get(0).z;
        vertexData[offset++] = 0f; vertexData[offset++] = 0f; vertexData[offset++] = 1.0f;
        if (texture) {
            vertexData[offset++] = 0;
            vertexData[offset++] = 0;
            vertexData[offset++] = 5;
        }
        vertexData[offset++] = CubePoints.get(3).x; vertexData[offset++] = CubePoints.get(3).y; vertexData[offset++] = CubePoints.get(3).z;
        vertexData[offset++] = 0f; vertexData[offset++] = 0f; vertexData[offset++] = 1.0f;
        if (texture) {
            vertexData[offset++] = 1;
            vertexData[offset++] = 1;
            vertexData[offset++] = 5;
        }
        vertexData[offset++] = CubePoints.get(3).x; vertexData[offset++] = CubePoints.get(3).y; vertexData[offset++] = CubePoints.get(3).z;
        vertexData[offset++] = 0f; vertexData[offset++] = 0f; vertexData[offset++] = 1.0f;
        if (texture) {
            vertexData[offset++] = 1;
            vertexData[offset++] = 1;
            vertexData[offset++] = 5;
        }
        vertexData[offset++] = CubePoints.get(0).x; vertexData[offset++] = CubePoints.get(0).y; vertexData[offset++] = CubePoints.get(0).z;
        vertexData[offset++] = 0f; vertexData[offset++] = 0f; vertexData[offset++] = 1.0f;
        if (texture) {
            vertexData[offset++] = 0;
            vertexData[offset++] = 0;
            vertexData[offset++] = 5;
        }
        vertexData[offset++] = CubePoints.get(2).x; vertexData[offset++] = CubePoints.get(2).y; vertexData[offset++] = CubePoints.get(2).z;
        vertexData[offset++] = 0f; vertexData[offset++] = 0f; vertexData[offset++] = 1.0f;
        if (texture) {
            vertexData[offset++] = 0;
            vertexData[offset++] = 1;
            vertexData[offset++] = 5;
        }

        // Back
        vertexData[offset++] = CubePoints.get(4).x; vertexData[offset++] = CubePoints.get(4).y; vertexData[offset++] = CubePoints.get(4).z;
        vertexData[offset++] = 0f; vertexData[offset++] = 0f; vertexData[offset++] = -1.0f;
        if (texture) {
            vertexData[offset++] = 1;
            vertexData[offset++] = 0;
            vertexData[offset++] = 4;
        }
        vertexData[offset++] = CubePoints.get(5).x; vertexData[offset++] = CubePoints.get(5).y; vertexData[offset++] = CubePoints.get(5).z;
        vertexData[offset++] = 0f; vertexData[offset++] = 0f; vertexData[offset++] = -1.0f;
        if (texture) {
            vertexData[offset++] = 0;
            vertexData[offset++] = 0;
            vertexData[offset++] = 4;
        }
        vertexData[offset++] = CubePoints.get(6).x; vertexData[offset++] = CubePoints.get(6).y; vertexData[offset++] = CubePoints.get(6).z;
        vertexData[offset++] = 0f; vertexData[offset++] = 0f; vertexData[offset++] = -1.0f;
        if (texture) {
            vertexData[offset++] = 1;
            vertexData[offset++] = 1;
            vertexData[offset++] = 4;
        }
        vertexData[offset++] = CubePoints.get(6).x; vertexData[offset++] = CubePoints.get(6).y; vertexData[offset++] = CubePoints.get(6).z;
        vertexData[offset++] = 0f; vertexData[offset++] = 0f; vertexData[offset++] = -1.0f;
        if (texture) {
            vertexData[offset++] = 1;
            vertexData[offset++] = 1;
            vertexData[offset++] = 4;
        }
        vertexData[offset++] = CubePoints.get(5).x; vertexData[offset++] = CubePoints.get(5).y; vertexData[offset++] = CubePoints.get(5).z;
        vertexData[offset++] = 0f; vertexData[offset++] = 0f; vertexData[offset++] = -1.0f;
        if (texture) {
            vertexData[offset++] = 0;
            vertexData[offset++] = 0;
            vertexData[offset++] = 4;
        }
        vertexData[offset++] = CubePoints.get(7).x; vertexData[offset++] = CubePoints.get(7).y; vertexData[offset++] = CubePoints.get(7).z;
        vertexData[offset++] = 0f; vertexData[offset++] = 0f; vertexData[offset++] = -1.0f;
        if (texture) {
            vertexData[offset++] = 0;
            vertexData[offset++] = 1;
            vertexData[offset++] = 4;
        }

        // Left
        vertexData[offset++] = CubePoints.get(0).x; vertexData[offset++] = CubePoints.get(0).y; vertexData[offset++] = CubePoints.get(0).z;
        vertexData[offset++] = -1.0f; vertexData[offset++] = 0f; vertexData[offset++] = 0f;
        if (texture) {
            vertexData[offset++] = 1;
            vertexData[offset++] = 0;
            vertexData[offset++] = 0;
        }
        vertexData[offset++] = CubePoints.get(4).x; vertexData[offset++] = CubePoints.get(4).y; vertexData[offset++] = CubePoints.get(4).z;
        vertexData[offset++] = -1.0f; vertexData[offset++] = 0f; vertexData[offset++] = 0f;
        if (texture) {
            vertexData[offset++] = 0;
            vertexData[offset++] = 0;
            vertexData[offset++] = 0;
        }
        vertexData[offset++] = CubePoints.get(2).x; vertexData[offset++] = CubePoints.get(2).y; vertexData[offset++] = CubePoints.get(2).z;
        vertexData[offset++] = -1.0f; vertexData[offset++] = 0f; vertexData[offset++] = 0f;
        if (texture) {
            vertexData[offset++] = 1;
            vertexData[offset++] = 1;
            vertexData[offset++] = 0;
        }
        vertexData[offset++] = CubePoints.get(2).x; vertexData[offset++] = CubePoints.get(2).y; vertexData[offset++] = CubePoints.get(2).z;
        vertexData[offset++] = -1.0f; vertexData[offset++] = 0f; vertexData[offset++] = 0f;
        if (texture) {
            vertexData[offset++] = 1;
            vertexData[offset++] = 1;
            vertexData[offset++] = 0;
        }
        vertexData[offset++] = CubePoints.get(4).x; vertexData[offset++] = CubePoints.get(4).y; vertexData[offset++] = CubePoints.get(4).z;
        vertexData[offset++] = -1.0f; vertexData[offset++] = 0f; vertexData[offset++] = 0f;
        if (texture) {
            vertexData[offset++] = 0;
            vertexData[offset++] = 0;
            vertexData[offset++] = 0;
        }
        vertexData[offset++] = CubePoints.get(6).x; vertexData[offset++] = CubePoints.get(6).y; vertexData[offset++] = CubePoints.get(6).z;
        vertexData[offset++] = -1.0f; vertexData[offset++] = 0f; vertexData[offset++] = 0f;
        if (texture) {
            vertexData[offset++] = 0;
            vertexData[offset++] = 1;
            vertexData[offset++] = 0;
        }
        // Right
        vertexData[offset++] = CubePoints.get(5).x; vertexData[offset++] = CubePoints.get(5).y; vertexData[offset++] = CubePoints.get(5).z;
        vertexData[offset++] = 1.0f; vertexData[offset++] = 0f; vertexData[offset++] = 0f;
        if (texture) {
            vertexData[offset++] = 1;
            vertexData[offset++] = 0;
            vertexData[offset++] = 1;
        }
        vertexData[offset++] = CubePoints.get(1).x; vertexData[offset++] = CubePoints.get(1).y; vertexData[offset++] = CubePoints.get(1).z;
        vertexData[offset++] = 1.0f; vertexData[offset++] = 0f; vertexData[offset++] = 0f;
        if (texture) {
            vertexData[offset++] = 0;
            vertexData[offset++] = 0;
            vertexData[offset++] = 1;
        }
        vertexData[offset++] = CubePoints.get(7).x; vertexData[offset++] = CubePoints.get(7).y; vertexData[offset++] = CubePoints.get(7).z;
        vertexData[offset++] = 1.0f; vertexData[offset++] = 0f; vertexData[offset++] = 0f;
        if (texture) {
            vertexData[offset++] = 1;
            vertexData[offset++] = 1;
            vertexData[offset++] = 1;
        }
        vertexData[offset++] = CubePoints.get(7).x; vertexData[offset++] = CubePoints.get(7).y; vertexData[offset++] = CubePoints.get(7).z;
        vertexData[offset++] = 1.0f; vertexData[offset++] = 0f; vertexData[offset++] = 0f;
        if (texture) {
            vertexData[offset++] = 1;
            vertexData[offset++] = 1;
            vertexData[offset++] = 1;
        }
        vertexData[offset++] = CubePoints.get(1).x; vertexData[offset++] = CubePoints.get(1).y; vertexData[offset++] = CubePoints.get(1).z;
        vertexData[offset++] = 1.0f; vertexData[offset++] = 0f; vertexData[offset++] = 0f;
        if (texture) {
            vertexData[offset++] = 0;
            vertexData[offset++] = 0;
            vertexData[offset++] = 1;
        }
        vertexData[offset++] = CubePoints.get(5).x; vertexData[offset++] = CubePoints.get(5).y; vertexData[offset++] = CubePoints.get(5).z;
        vertexData[offset++] = 1.0f; vertexData[offset++] = 0f; vertexData[offset++] = 0f;
        if (texture) {
            vertexData[offset++] = 0;
            vertexData[offset++] = 1;
            vertexData[offset++] = 1;
        }

        // Top
        vertexData[offset++] = CubePoints.get(5).x; vertexData[offset++] = CubePoints.get(5).y; vertexData[offset++] = CubePoints.get(5).z;
        vertexData[offset++] = 0f; vertexData[offset++] = 1.0f; vertexData[offset++] = 0f;
        if (texture) {
            vertexData[offset++] = 1;
            vertexData[offset++] = 0;
            vertexData[offset++] = 3;
        }
        vertexData[offset++] = CubePoints.get(4).x; vertexData[offset++] = CubePoints.get(4).y; vertexData[offset++] = CubePoints.get(4).z;
        vertexData[offset++] = 0f; vertexData[offset++] = 1.0f; vertexData[offset++] = 0f;
        if (texture) {
            vertexData[offset++] = 0;
            vertexData[offset++] = 0;
            vertexData[offset++] = 3;
        }
        vertexData[offset++] = CubePoints.get(1).x; vertexData[offset++] = CubePoints.get(1).y; vertexData[offset++] = CubePoints.get(1).z;
        vertexData[offset++] = 0f; vertexData[offset++] = 1.0f; vertexData[offset++] = 0f;
        if (texture) {
            vertexData[offset++] = 1;
            vertexData[offset++] = 1;
            vertexData[offset++] = 3;
        }
        vertexData[offset++] = CubePoints.get(1).x; vertexData[offset++] = CubePoints.get(1).y; vertexData[offset++] = CubePoints.get(1).z;
        vertexData[offset++] = 0f; vertexData[offset++] = 1.0f; vertexData[offset++] = 0f;
        if (texture) {
            vertexData[offset++] = 1;
            vertexData[offset++] = 1;
            vertexData[offset++] = 3;
        }
        vertexData[offset++] = CubePoints.get(4).x; vertexData[offset++] = CubePoints.get(4).y; vertexData[offset++] = CubePoints.get(4).z;
        vertexData[offset++] = 0f; vertexData[offset++] = 1.0f; vertexData[offset++] = 0f;
        if (texture) {
            vertexData[offset++] = 0;
            vertexData[offset++] = 0;
            vertexData[offset++] = 3;
        }
        vertexData[offset++] = CubePoints.get(0).x; vertexData[offset++] = CubePoints.get(0).y; vertexData[offset++] = CubePoints.get(0).z;
        vertexData[offset++] = 0f; vertexData[offset++] = 1.0f; vertexData[offset++] = 0f;
        if (texture) {
            vertexData[offset++] = 0;
            vertexData[offset++] = 1;
            vertexData[offset++] = 3;
        }

        // Bottom
        vertexData[offset++] = CubePoints.get(6).x; vertexData[offset++] = CubePoints.get(6).y; vertexData[offset++] = CubePoints.get(6).z;
        vertexData[offset++] = 0f; vertexData[offset++] = -1.0f; vertexData[offset++] = 0f;
        if (texture) {
            vertexData[offset++] = 1;
            vertexData[offset++] = 0;
            vertexData[offset++] = 2;
        }
        vertexData[offset++] = CubePoints.get(7).x; vertexData[offset++] = CubePoints.get(7).y; vertexData[offset++] = CubePoints.get(7).z;
        vertexData[offset++] = 0f; vertexData[offset++] = -1.0f; vertexData[offset++] = 0f;
        if (texture) {
            vertexData[offset++] = 0;
            vertexData[offset++] = 0;
            vertexData[offset++] = 2;
        }
        vertexData[offset++] = CubePoints.get(2).x; vertexData[offset++] = CubePoints.get(2).y; vertexData[offset++] = CubePoints.get(2).z;
        vertexData[offset++] = 0f; vertexData[offset++] = -1.0f; vertexData[offset++] = 0f;
        if (texture) {
            vertexData[offset++] = 1;
            vertexData[offset++] = 1;
            vertexData[offset++] = 2;
        }
        vertexData[offset++] = CubePoints.get(2).x; vertexData[offset++] = CubePoints.get(2).y; vertexData[offset++] = CubePoints.get(2).z;
        vertexData[offset++] = 0f; vertexData[offset++] = -1.0f; vertexData[offset++] = 0f;
        if (texture) {
            vertexData[offset++] = 1;
            vertexData[offset++] = 1;
            vertexData[offset++] = 2;
        }
        vertexData[offset++] = CubePoints.get(7).x; vertexData[offset++] = CubePoints.get(7).y; vertexData[offset++] = CubePoints.get(7).z;
        vertexData[offset++] = 0f; vertexData[offset++] = -1.0f; vertexData[offset++] = 0f;
        if (texture) {
            vertexData[offset++] = 0;
            vertexData[offset++] = 0;
            vertexData[offset++] = 2;
        }
        vertexData[offset++] = CubePoints.get(3).x; vertexData[offset++] = CubePoints.get(3).y; vertexData[offset++] = CubePoints.get(3).z;
        vertexData[offset++] = 0f; vertexData[offset++] = -1.0f; vertexData[offset++] = 0f;
        if (texture) {
            vertexData[offset++] = 0;
            vertexData[offset++] = 1;
            vertexData[offset++] = 2;
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLES, startVertex, numVertices);
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
        ObjectBuilder builder = new ObjectBuilder(size, TextureMode.NOTEXTURE);
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
        ObjectBuilder builder = new ObjectBuilder(size, TextureMode.NOTEXTURE);
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
        ObjectBuilder builder = new ObjectBuilder(size, TextureMode.NOTEXTURE);

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

    public static GeneratedData createRectangle(GLRectangle rectangle, boolean texture, float CenterS, float CenterT, float width, float height, int orientation) {
        int size = sizeOfRectangleInVertices();
        ObjectBuilder builder = new ObjectBuilder(size, texture ? TextureMode.TEXTURE2D: TextureMode.NOTEXTURE);
        builder.appendRectangle(rectangle, texture, CenterS, CenterT, width, height, orientation);
        return builder.build();
    }

    public static GeneratedData createCube(GLCube cube, boolean texture) {
        int size = sizeOfCubeInVertices();
        ObjectBuilder builder = new ObjectBuilder(size, texture ? TextureMode.TEXTURECUBE: TextureMode.NOTEXTURE);
        builder.appendCube(cube, texture);
        return builder.build();
    }
}
