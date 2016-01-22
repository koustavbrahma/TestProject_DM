package koustav.duelmasters.main.androidgameopenglutil;

/**
 * Created by Koustav on 1/22/2016.
 */
public class GLGeometry {
    public static class GLPoint {
        public final float x, y, z;
        public GLPoint(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        public GLPoint translateY(float distance) {
            return new GLPoint(x, y + distance, z);
        }
    }

    public static class GLCircle {
        public final GLPoint center;
        public final float radius;
        public GLCircle(GLPoint center, float radius) {
            this.center = center;
            this.radius = radius;
        }
        public GLCircle scale(float scale) {
            return new GLCircle(center, radius * scale);
        }
    }

    public static class GLCylinder {
        public final GLPoint center;
        public final float radius;
        public final float height;
        public GLCylinder(GLPoint center, float radius, float height) {
            this.center = center;
            this.radius = radius;
            this.height = height;
        }
    }
}
