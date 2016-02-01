package koustav.duelmasters.main.androidgameopenglanimation;

import java.util.Random;

import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;
import static android.opengl.Matrix.*;

/**
 * Created by Koustav on 2/1/2016.
 */
public class ParticleShooter {
    private final GLPoint position;
    private final GLVector direction;
    private final int color;
    private final float angleVariance;
    private final float speedVariance;

    private final Random random = new Random();
    private float[] rotationMatrix = new float[16];
    private float[] directionVector = new float[4];
    private float[] resultVector = new float[4];

    public ParticleShooter(GLPoint position, GLVector direction, int color,
                           float angleVarianceInDegrees, float speedVariance) {
        this.position = position;
        this.direction = direction;
        this.color = color;
        this.angleVariance = angleVarianceInDegrees;
        this.speedVariance = speedVariance;
        directionVector[0] = direction.x;
        directionVector[1] = direction.y;
        directionVector[2] = direction.z;
    }

    public void addParticles(ParticleSystem particleSystem, float currentTime,
                             int count) {
        for (int i = 0; i < count; i++) {
            setRotateEulerM(rotationMatrix, 0,
                    (random.nextFloat() - 0.5f) * angleVariance,
                    (random.nextFloat() - 0.5f) * angleVariance,
                    (random.nextFloat() - 0.5f) * angleVariance);
            multiplyMV(
                    resultVector, 0,
                    rotationMatrix, 0,
                    directionVector, 0);
            float speedAdjustment = 1f + random.nextFloat() * speedVariance;
            GLVector thisDirection = new GLVector(
                    resultVector[0] * speedAdjustment,
                    resultVector[1] * speedAdjustment,
                    resultVector[2] * speedAdjustment);
            particleSystem.addParticle(position, color, thisDirection, currentTime);
        }
    }
}
