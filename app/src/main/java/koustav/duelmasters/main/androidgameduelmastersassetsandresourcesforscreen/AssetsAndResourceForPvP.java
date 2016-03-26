package koustav.duelmasters.main.androidgameduelmastersassetsandresourcesforscreen;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import koustav.duelmasters.main.androidgameopenglutil.FrameBufferObject;
import koustav.duelmasters.main.androidgameopenglutil.GLLight;
import koustav.duelmasters.main.androidgameopenglutil.MatrixHelper;
import koustav.duelmasters.main.androidgameopenglutil.TextureHelper;
import koustav.duelmasters.main.androidgamesframework.Pixmap;
import koustav.duelmasters.main.androidgamesframeworkimpl.AndroidGame;
import koustav.duelmasters.main.androidgameshaderprogram.ColorShaderProgram;
import koustav.duelmasters.main.androidgameshaderprogram.CubeTextureShaderProgramLight;
import koustav.duelmasters.main.androidgameshaderprogram.TextureShaderProgram;
import koustav.duelmasters.main.androidgameshaderprogram.TextureShaderProgramLight;

import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.setLookAtM;

/**
 * Created by Koustav on 3/15/2015.
 */
public class AssetsAndResourceForPvP {
    public static class Count {
        public int val;
        public Count(int val) {
            this.val = val;
        }
    }
    public static Pixmap background;
    public static Pixmap cardbackside;
    public static Pixmap Button;
    public static Pixmap InfoBackground;

    // Game reference
    public static AndroidGame game;

    // Shaders
    public static TextureShaderProgram textureProgram;
    public static TextureShaderProgramLight textureShaderProgramLight;
    public static CubeTextureShaderProgramLight cubeTextureShaderProgramLight;
    public static ColorShaderProgram colorShaderProgram;

    // Frame buffers
    public static FrameBufferObject ShadowBuffer;

    // Matrix Fixed
    public static float[] viewMatrix;
    public static float[] projectionMatrix;
    public static float[] depthVPMatrix;
    public static float[] invertedViewProjectionMatrix;

    // Matrix Changeable
    public static float[] modelViewMatrix;
    public static float[] modelViewProjectionMatrix;
    public static float[] it_modelViewMatrix;
    public static float[] modelMatrix;
    public static float[] tempMatrix;
    public static float[] ShadowMatrix;

    // Fixed textures
    public static int Base;
    public static int cardBackside;
    public static int cardDeckSides;

    // flexible textures
    private static Hashtable<String, Integer> CardImages;

    // Card usage count
    private static Hashtable<String, Count> CardCount;

    // Light
    public static ArrayList<GLLight> Light;

    // APIs

    public static void Load(AndroidGame game) {
        // Store the game reference
        AssetsAndResourceForPvP.game = game;

        // Matrix fixed
        viewMatrix = new float[16];
        projectionMatrix = new float[16];
        depthVPMatrix = new float[16];
        invertedViewProjectionMatrix = new float[16];

        // Matrix Changeable
        modelViewMatrix = new float[16];
        modelViewProjectionMatrix = new float[16];
        it_modelViewMatrix = new float[16];
        modelMatrix = new float[16];
        tempMatrix = new float[16];
        ShadowMatrix = new float[16];

        // Light
        Light = new ArrayList<GLLight>();
    }


    public static void InitializeAssetsAndResource() {
        // Program initialization
        textureProgram = new TextureShaderProgram(game);
        textureShaderProgramLight = new TextureShaderProgramLight(game);
        cubeTextureShaderProgramLight = new CubeTextureShaderProgramLight(game);
        colorShaderProgram = new ColorShaderProgram(game);

        // Frame Buffers
        ShadowBuffer = new FrameBufferObject(game.getframeBufferWidth(), game.getframeBufferHeight());

        // Textures generation
        if (Base != 0) {
            TextureHelper.freeTexture(Base);
        }
        Base = TextureHelper.loadTexture(game, "Base_5.png");
        if (cardBackside != 0) {
            TextureHelper.freeTexture(cardBackside);
        }
        cardBackside = TextureHelper.loadTexture(game, "cardbackside.png");
        if (cardDeckSides != 0) {
            TextureHelper.freeTexture(cardDeckSides);
        }
        cardDeckSides = TextureHelper.loadTexture(game, "CardDeckSide.png");

        CardImages = new Hashtable<String, Integer>();
        CardCount = new Hashtable<String, Count>();

        // Fixed Matrix initialization
        MatrixHelper.perspectiveM(projectionMatrix, 36, (float) game.getframeBufferWidth() / (float) game.getframeBufferHeight(), 1f, 10f);
        setLookAtM(viewMatrix, 0, 0f, 1.2f, 0.8f, 0f, 0f, 0.1f, 0f, 1f, 0f);
        multiplyMM(tempMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        invertM(invertedViewProjectionMatrix, 0, tempMatrix, 0);

        // Setup IVP Matrix for UI
        game.getInput().setIVPMatrix(AssetsAndResourceForPvP.invertedViewProjectionMatrix);

        // Light initialization
        float [] spotLightDirectionInEyeSpace = new float[4];
        multiplyMV(spotLightDirectionInEyeSpace, 0, AssetsAndResourceForPvP.viewMatrix, 0, new float[] {0f, -1.0f, 0f, 0f}, 0);
        GLLight DirectionalLight = new GLLight(GLLight.LightType.Directional, new float[] {0, 0, 0, 0}, spotLightDirectionInEyeSpace,
                new float[] {0.7f, 0.7f, 0.7f}, 0, 0);

        Light.add(DirectionalLight);
    }


    public static void FreeAssetsAndResources() {
        // Programs free
        textureProgram.deleteProgram();
        textureShaderProgramLight.deleteProgram();
        cubeTextureShaderProgramLight.deleteProgram();
        colorShaderProgram.deleteProgram();

        // Frame buffer free
        ShadowBuffer.freeFBO();

        // Texture free
        if (Base != 0) {
            TextureHelper.freeTexture(Base);
        }
        Base = 0;
        if (cardBackside != 0) {
            TextureHelper.freeTexture(cardBackside);
        }
        cardBackside = 0;
        if (cardDeckSides != 0) {
            TextureHelper.freeTexture(cardDeckSides);
        }
        cardDeckSides = 0;

        RemoveAllCardTexture();

        // Light free
        Light.clear();
    }

    public static int getCardTexture(String CardName) {
        Integer texture = CardImages.get(CardName);
        if (texture == null) {
            String filename = new String(CardName);
            filename = filename.concat(".png");
            int val;
            do {
                val = TextureHelper.loadTexture(game, filename);
                if (val != 0) {
                    break;
                }

                String CardNameToRm = null;
                Set<String> keys = CardCount.keySet();
                for(String key: keys){
                    Count count = CardCount.get(key);
                    if (count.val == 0) {
                        CardNameToRm = key;
                        break;
                    }
                }

                if (CardNameToRm != null) {
                    val = CardImages.remove(CardNameToRm);
                    CardCount.remove(CardNameToRm);
                    TextureHelper.freeTexture(val);
                } else {
                    // This should not happen, if happening limit your texture generation
                    throw new RuntimeException("Texture memory full");
                }
            } while (true);

            texture = new Integer(val);
            CardImages.put(new String(CardName), texture);
            CardCount.put(new String(CardName), new Count(1));
        } else {
            Count count = CardCount.get(CardName);
            if (count == null) {
                throw new RuntimeException("Data inconsistency");
            }
            count.val++;
        }

        return texture.intValue();
    }

    public static void ResetCardUsageCount() {
        Set<String> keys = CardCount.keySet();
        for(String key: keys){
            Count count = CardCount.get(key);
            count.val = 0;
        }
    }

    public static void RemoveAllCardTexture() {
        Set<String> keys = CardImages.keySet();
        for(String key: keys){
            int val = CardImages.get(key);
            TextureHelper.freeTexture(val);
        }

        CardImages.clear();
        CardCount.clear();
    }
}
