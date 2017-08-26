package koustav.duelmasters.main.androidgameopengl.androidgameopenglutil;

/**
 * Created by Koustav on 1/22/2016.
 */
public class GLGeometry {
    public GLShapeType type;

    public GLGeometry(GLShapeType type) {
        this.type = type;
    }

    GLGeometry create(GLShapeType type, Object ...obj) {
        switch (type) {
            case Point: return new GLPoint((float)obj[0], (float)obj[1], (float)obj[2]);
            case Circle: return new GLCircle((GLPoint)obj[0], (float)obj[1]);
            case Cylinder: return new GLCylinder((GLPoint)obj[0], (float)obj[1], (float)obj[2]);
            case Cube: return new GLCube((GLPoint)obj[0], (float)obj[1], (float)obj[2], (float)obj[3]);
            case ConicalFrustum: return new GLConicalFrustum((GLPoint)obj[0], (float)obj[1], (float)obj[2], (float)obj[3]);
            case Arch: return new GLArch((GLPoint)obj[0], (float)obj[1], (float)obj[2], (float)obj[3]);
            case Rectangle: return new GLRectangle((GLPoint)obj[0], (float)obj[1], (float)obj[2]);
            case Plane: return new GLPlane((GLPoint)obj[0], (GLVector)obj[1]);
            case Vector: return new GLVector((float)obj[0], (float)obj[1], (float)obj[2]);
            case Ray: return new GLRay((GLPoint)obj[0], (GLVector)obj[1]);
            case Rotation: return new GLAngularRotaion((float)obj[0], (float)obj[1], (float)obj[2], (float)obj[3]);
            default: return null;
        }
    }

    public enum GLShapeType {
        Point,
        Circle,
        Cylinder,
        Cube,
        ConicalFrustum,
        Arch,
        Rectangle,
        Plane,
        Vector,
        Ray,
        Rotation
    }

    // Static classes
    public static class GLPoint extends GLGeometry{
        public float x, y, z;
        public GLPoint(float x, float y, float z) {
            super(GLShapeType.Point);
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

    public static class GLCircle extends GLGeometry{
        public final GLPoint center;
        public final float radius;
        public GLCircle(GLPoint center, float radius) {
            super(GLShapeType.Circle);
            this.center = center;
            this.radius = radius;
        }
        public GLCircle scale(float scale) {
            return new GLCircle(center, radius * scale);
        }
    }

    public static class GLCylinder extends GLGeometry{
        public final GLPoint center;
        public final float radius;
        public final float height;
        public GLCylinder(GLPoint center, float radius, float height) {
            super(GLShapeType.Cylinder);
            this.center = center;
            this.radius = radius;
            this.height = height;
        }
    }

    public static class GLCube extends GLGeometry{
        public final GLPoint center;
        public final float width;
        public final float length;
        public final float height;
        public GLCube(GLPoint center, float width, float length, float height) {
            super(GLShapeType.Cube);
            this.center = center;
            this.width = width;
            this.length = length;
            this.height = height;
        }
    }

    public static class GLConicalFrustum extends GLGeometry{
        public final GLPoint center;
        public final float topradius;
        public final float botradius;
        public final float height;
        public GLConicalFrustum(GLPoint center, float topradius, float botradius, float height) {
            super(GLShapeType.ConicalFrustum);
            this.center = center;
            this.topradius = topradius;
            this.botradius = botradius;
            this.height = height;
        }
    }

    public static class GLArch extends GLGeometry{
        public final GLPoint center;
        public final float angle;
        public final float radius;
        public final float fallhieght;
        public GLArch(GLPoint center, float angle, float radius, float fallhieght) {
            super(GLShapeType.Arch);
            this.center = center;
            this.angle = angle;
            this.radius = radius;
            this.fallhieght = fallhieght;
        }
    }

    public static class GLRectangle extends GLGeometry{
        public final GLPoint center;
        public final float width;
        public final float height;

        public GLRectangle(GLPoint center, float width, float height) {
            super(GLShapeType.Rectangle);
            this.center = center;
            this.width = width;
            this.height = height;
        }
    }

    public static class GLVector extends GLGeometry{
        public float x, y, z;
        public GLVector(float x, float y, float z) {
            super(GLShapeType.Vector);
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public float getMagnitude() {
            return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        }

        public GLVector getDirection() {
            float abs = getMagnitude();
            if (abs != 0) {
                return new GLVector(x / abs, y / abs, z / abs);
            } else {
                return new GLVector(0f, 1f, 0f);
            }
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

    public static class GLRay extends GLGeometry{
        public GLPoint point;
        public GLVector vector;
        public GLRay(GLPoint point, GLVector vector) {
            super(GLShapeType.Ray);
            this.point = point;
            this.vector = vector;
        }
    }

    public static class GLPlane extends GLGeometry{
        public GLPoint point;
        public GLVector normal;
        public GLPlane(GLPoint point, GLVector vector) {
            super(GLShapeType.Plane);
            this.point = point;
            this.normal = vector;
        }
    }

    public static class GLAngularRotaion extends GLGeometry{
        public float angle;
        public float x;
        public float y;
        public float z;

        public GLAngularRotaion(float angle, float x, float y, float z) {
            super(GLShapeType.Rotation);
            this.angle = angle;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    //// STATIC APIs
    public static GLVector GLVectorBetween(GLPoint from, GLPoint to) {
        return new GLVector(
                to.x - from.x,
                to.y - from.y,
                to.z - from.z);
    }

    public static GLPoint GLRayIntersectionWithXZPlane(GLRay ray, float yOffset) {
        GLPoint nearPointRay = ray.point;
        GLPoint farPointRay = ray.point.translate(ray.vector);

        float x = ((yOffset-nearPointRay.y)/(farPointRay.y - nearPointRay.y)) * (farPointRay.x - nearPointRay.x)
                + nearPointRay.x;

        float z = ((yOffset-nearPointRay.y)/(farPointRay.y - nearPointRay.y)) * (farPointRay.z - nearPointRay.z)
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
