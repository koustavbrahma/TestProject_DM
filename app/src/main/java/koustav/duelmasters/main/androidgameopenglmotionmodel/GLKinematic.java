package koustav.duelmasters.main.androidgameopenglmotionmodel;

import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;

/**
 * Created by Koustav on 2/24/2016.
 */
public class GLKinematic {
    private float x, y, z;
    private float Vx, Vy, Vz;
    private float Ax, Ay, Az;
    private float ox, oy, oz;
    private float wx, wy, wz;
    private float ax, ay, az;

    public GLKinematic() {
        x = y = z = 0;
        Vx = Vy = Vz = 0;
        Ax = Ay = Az = 0;
        ox = oy = oz = 0;
        wx = wy = wz = 0;
        ax = ay = az = 0;
    }

    public void setCentrePosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setVelocity(float Vx, float Vy, float Vz) {
        this.Vx = Vx;
        this.Vy = Vy;
        this.Vz = Vz;
    }

    public void setAcceleration(float Ax, float Ay, float Az) {
        this.Ax = Ax;
        this.Ay = Ay;
        this.Az = Az;
    }

    public void setAngle(float ox, float oy, float oz) {
        this.ox = ox;
        this.oy = oy;
        this.oz = oz;
    }

    public void setAngularVelocity(float wx, float wy, float wz) {
        this.wx = wx;
        this.wy = wy;
        this.wz = wz;
    }

    public void setAngularAcceleration(float ax, float ay, float az) {
        this.ax = ax;
        this.ay = ay;
        this.az = az;
    }

    public void update(float deltaTime) {
        x = x + Vx * deltaTime + (float) 0.5 * Ax * deltaTime * deltaTime;
        Vx = Vx + Ax * deltaTime;
        ox = ox + wx * deltaTime + (float) 0.5 * ax * deltaTime * deltaTime;
        wx = wx + ax * deltaTime;

        y = y + Vy * deltaTime + (float) 0.5 * Ay * deltaTime * deltaTime;
        Vy = Vy + Ay * deltaTime;
        oy = oy + wy * deltaTime + (float) 0.5 * ay * deltaTime * deltaTime;
        wy = wy + ay * deltaTime;

        z = z + Vz * deltaTime + (float) 0.5 * Az * deltaTime * deltaTime;
        Vz = Vz + Az * deltaTime;
        oz = oz + wz * deltaTime + (float) 0.5 * az * deltaTime * deltaTime;
        wz = wz + az * deltaTime;
    }

    public GLPoint getPosition() {
        return new GLPoint(x, y, z);
    }

    public float getAngleOfRotation() {
        GLVector AngleVector= new GLVector(ox, oy, oz);
        float radAngle = AngleVector.getMagnitude();

        float degreeAngle = (float)(360 / (2 * Math.PI)) * radAngle;
        degreeAngle = degreeAngle % 360;

        return degreeAngle;
    }

    public GLVector getAxisOfRotation() {
        GLVector AngleVector = new GLVector(ox, oy, oz);
        return AngleVector.getDirection();
    }
}
