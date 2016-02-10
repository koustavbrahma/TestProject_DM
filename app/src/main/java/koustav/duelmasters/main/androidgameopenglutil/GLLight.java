package koustav.duelmasters.main.androidgameopenglutil;

/**
 * Created by Koustav on 2/7/2016.
 */
public class GLLight {
    public enum LightType {
        Directional,
        Point,
        Spot,
    }

    LightType Type;
    public float[] Position; // Light position in eye coords.
    public float[] Direction;
    public float[] Intensity; // A,D,S intensity
    public float Exponent; // Angular attenuation exponent
    public float Cutoff; // Cutoff angle (between 0 and 90)

    public GLLight(LightType type, float[] position, float[] direction, float[] intensity,
                   float exponent, float cutoff) {
        Type = type;
        Position = position;
        Direction = direction;
        Intensity = intensity;
        Exponent = exponent;
        Cutoff = cutoff;
    }

    public int getLightType() {
        if (Type == LightType.Directional) {
            return 0;
        }

        if (Type == LightType.Point) {
            return 1;
        }

        if (Type == LightType.Spot) {
            return 2;
        }

        return 0;
    }
}
