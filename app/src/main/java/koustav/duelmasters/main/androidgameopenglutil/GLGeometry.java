package koustav.duelmasters.main.androidgameopenglutil;

/**
 * Created by Koustav on 1/22/2016.
 */
public class GLGeometry {
    // Static classes
    public static class GLPoint {
        public float x, y, z;
        public GLPoint(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public GLPoint translateX(float distance) {
            return new GLPoint(x + distance, y, z);
        }

        public GLPoint translateY(float distance) {
            return new GLPoint(x, y + distance, z);
        }

        public GLPoint translateZ(float distance) {
            return new GLPoint(x, y, z + distance);
        }

        public GLPoint translate(GLVector vector) {
            return new GLPoint(x + vector.x, y + vector.y, z + vector.z);
        }

        public GLVector getVector() {
            return new GLVector(x, y, z);
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

    public static class GLCube {
        public final GLPoint center;
        public final float width;
        public final float length;
        public final float height;
        public GLCube(GLPoint center, float width, float length, float height) {
            this.center = center;
            this.width = width;
            this.length = length;
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

    public static class GLArch {
        public final GLPoint center;
        public final float angle;
        public final float radius;
        public final float fallhieght;
        public GLArch(GLPoint center, float angle, float radius, float fallhieght) {
            this.center = center;
            this.angle = angle;
            this.radius = radius;
            this.fallhieght = fallhieght;
        }
    }

    public static class GLRectangle {
        public final GLPoint center;
        public final float width;
        public final float height;

        public GLRectangle(GLPoint center, float width, float height) {
            this.center = center;
            this.width = width;
            this.height = height;
        }
    }

    public static class GLVector {
        public float x, y, z;
        public GLVector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public float getMagnitude() {
            return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        }

        public GLVector getDirection() {
            float abs = getMagnitude();
            return new GLVector(x/abs, y/abs, z/abs);
        }

        public GLVector scale(float s) {
            return new GLVector(s*x,s*y,s*z);
        }

        public GLVector crossProduct(GLVector other) {
            return new GLVector(
                    (y * other.z) - (z * other.y),
                    (z * other.x) - (x * other.z),
                    (x * other.y) - (y * other.x));
        }

        public float dotProduct(GLVector other) {
            return (x * other.x) + (y * other.y) + (z * other.z);
        }
    }

    public static class GLRay {
        public static GLPoint point;
        public static GLVector vector;
        public GLRay(GLPoint point, GLVector vector) {
            this.point = point;
            this.vector = vector;
        }
    }

    public static class GLPlane {
        public static GLPoint point;
        public static GLVector normal;
        public GLPlane(GLPoint point, GLVector vector) {
            this.point = point;
            this.normal = vector;
        }
    }

    public static class GLAngularRotaion {
        public float angle;
        public float x;
        public float y;
        public float z;

        public GLAngularRotaion(float angle, float x, float y, float z) {
            this.angle = angle;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    // APIs
    public static GLVector GLVectorBetween(GLPoint from, GLPoint to) {
        return new GLVector(
                to.x - from.x,
                to.y - from.y,
                to.z - from.z);
    }

    public static GLPoint GLRayIntersectionWithXZPlane(GLRay ray, float yOffset) {
        GLPoint nearPointRay = ray.point;
        GLPoint farPointRay = ray.point.translate(ray.vector);

        float x = (yOffset-nearPointRay.y/(farPointRay.y - nearPointRay.y)) * (farPointRay.x - nearPointRay.x)
                + nearPointRay.x;

        float z = (yOffset-nearPointRay.y/(farPointRay.y - nearPointRay.y)) * (farPointRay.z - nearPointRay.z)
                + nearPointRay.z;

        return new GLPoint(x, yOffset, z);
    }

    public static GLPoint GLRayIntersectionWithPlane(GLRay ray, GLPlane plane) {
        GLPoint nearPoint = ray.point;
        GLVector rayDirection = ray.vector;
        GLPoint planePoint = plane.point;
        GLVector planeNormal = plane.normal;

        float t = (planeNormal.dotProduct(planePoint.getVector()) - planeNormal.dotProduct(nearPoint.getVector()))/planeNormal.dotProduct(rayDirection);
        GLPoint pointOfIntersection = nearPoint.translate(rayDirection.scale(t));
        return pointOfIntersection;
    }

    public static boolean GLPlaneFacingPoint(GLPoint center, GLVector normal, GLPoint point) {
        return (GLVectorBetween(center, point).dotProduct(normal) > 0) ? true: false;
    }
}
