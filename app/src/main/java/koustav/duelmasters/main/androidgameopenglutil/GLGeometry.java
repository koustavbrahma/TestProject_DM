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

        public GLPoint translate(GLVector vector) {
            return new GLPoint(x + vector.x, y + vector.y, z + vector.z);
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

    public static class GLConicalFrustum {
        public final GLPoint center;
        public final float topradius;
        public final float botradius;
        public final float height;
        public GLConicalFrustum(GLPoint center, float topradius, float botradius, float height) {
            this.center = center;
            this.topradius = topradius;
            this.botradius = botradius;
            this.height = height;
        }
    }

    public static class GLVector {
        public final float x, y, z;
        public GLVector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static class GLRay {
        public final GLPoint point;
        public final GLVector vector;
        public GLRay(GLPoint point, GLVector vector) {
            this.point = point;
            this.vector = vector;
        }
    }

    public static GLVector GLVectorBetween(GLPoint from, GLPoint to) {
        return new GLVector(
                to.x - from.x,
                to.y - from.y,
                to.z - from.z);
    }



    public static GLPoint GLRayIntersectionWithXZPlane(GLRay ray) {
        GLPoint nearPointRay = ray.point;
        GLPoint farPointRay = ray.point.translate(ray.vector);

        float x = (-nearPointRay.y/(farPointRay.y - nearPointRay.y)) * (farPointRay.x - nearPointRay.x)
                + nearPointRay.x;

        float z = (-nearPointRay.y/(farPointRay.y - nearPointRay.y)) * (farPointRay.z - nearPointRay.z)
                + nearPointRay.z;

        return new GLPoint(x, 0f, z);
    }
}
