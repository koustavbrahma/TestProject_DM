package koustav.duelmasters.main.androidgameassetsandresourcesallocator;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmastersworlds.World;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.ControllerButton;
import koustav.duelmasters.main.androidgameopenglutil.FrameBufferObject;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgameopenglutil.GLLight;
import koustav.duelmasters.main.androidgameopenglutil.MatrixHelper;
import koustav.duelmasters.main.androidgameopenglutil.TextureHelper;
import koustav.duelmasters.main.androidgamesframework.Pixmap;
import koustav.duelmasters.main.androidgamesframework.Pool;
import koustav.duelmasters.main.androidgamesframeworkimpl.AndroidGame;
import koustav.duelmasters.main.androidgameshaderprogram.ColorShaderProgram;
import koustav.duelmasters.main.androidgameshaderprogram.CubeTextureShaderProgramLight;
import koustav.duelmasters.main.androidgameshaderprogram.TextureShaderProgram;
import koustav.duelmasters.main.androidgameshaderprogram.TextureShaderProgramLight;

import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.setLookAtM;

/**
 * Created by Koustav on 3/15/2015.
 */
public class AssetsAndResource {
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

    // Current World reference
    public static World world;

    // Shaders
    public static TextureShaderProgram textureProgram;
    public static TextureShaderProgramLight textureShaderProgramLight;
    public static CubeTextureShaderProgramLight cubeTextureShaderProgramLight;
    public static ColorShaderProgram colorShaderProgram;

    // Frame buffers
    public static FrameBufferObject SceneBuffer;
    public static FrameBufferObject ShadowBuffer;

    // Matrix Fixed
    public static float[] viewMatrix;
    public static float[] projectionMatrix;
    public static float[] OrthoProjectionMatrix;
    public static float[] depthVPMatrix;
    public static float[] invertedViewProjectionMatrix;
    public static float[] invertedOrthoProjectionMatrix;

    // Matrix Changeable
    public static float[] modelViewMatrix;
    public static float[] modelViewProjectionMatrix;
    public static float[] modelOrthoProjectionMatrix;
    public static float[] it_modelViewMatrix;
    public static float[] modelMatrix;
    public static float[] tempMatrix;
    public static float[] ShadowMatrix;

    // Fixed textures
    public static int Base;
    public static int cardBackside;
    public static int cardBorder;
    public static int cardDeckSides;
    public static int pauseButton;
    public static int AcceptButton;
    public static int DeclineButton;
    public static int SummonButton;
    public static int AddToManaButton;
    public static int AttackButton;
    public static int BlockButton;
    public static int TapAbilityButton;
    public static int ZoomButton;
    public static int EndTurnButton;
    public static int PlayerImage;
    public static int OpponentImage;
    public static int BlueScreenImage;
    public static int RedScreenImage;

    // Fixed textures ID
    public static int BaseID = 0;
    public static int cardBacksideID = 1;
    public static int cardBorderID = 2;
    public static int cardDeckSidesID = 3;
    public static int pauseButtonID = 4;
    public static int SummonButtonID = 5;
    public static int AcceptButtonID = 6;
    public static int DeclineButtonID = 7;
    public static int AddToManaButtonID = 8;
    public static int AttackButtonID = 9;
    public static int BlockButtonID = 10;
    public static int TapAbilityButtonID = 11;
    public static int ZoomButtonID = 12;
    public static int EndTurnButtonID = 13;
    public static int PlayerImageID = 14;
    public static int OpponentImageID = 15;
    public static int BlueScreenImageID = 16;
    public static int RedScreenImageID = 17;

    // flexible textures
    private static Hashtable<String, Integer> CardImages;

    // Card usage count
    private static Hashtable<String, Count> CardCount;

    // Light
    public static ArrayList<GLLight> Light;

    // Object Pools
    public static Pool<WidgetTouchEvent> widgetTouchEventPool;

    // Misc parameters
    public static float aspectRatio;
    public static float MazeWidth;
    public static float MazeHeight;
    public static float CardWidth;
    public static float CardHeight;
    public static float CardLength;
    public static float CardStackShift;
    public static float ZoomCardWidth;
    public static float ZoomCardHeight;
    public static GLGeometry.GLPoint CameraPosition;

    // APIs

    public static void Load(AndroidGame game, float camera_x, float camera_y, float camera_z) {
        // Store the game reference
        AssetsAndResource.game = game;

        // Matrix fixed
        viewMatrix = new float[16];
        projectionMatrix = new float[16];
        OrthoProjectionMatrix = new float[16];
        depthVPMatrix = new float[16];
        invertedViewProjectionMatrix = new float[16];
        invertedOrthoProjectionMatrix = new float[16];

        // Matrix Changeable
        modelViewMatrix = new float[16];
        modelViewProjectionMatrix = new float[16];
        modelOrthoProjectionMatrix = new float[16];
        it_modelViewMatrix = new float[16];
        modelMatrix = new float[16];
        tempMatrix = new float[16];
        ShadowMatrix = new float[16];

        // Light
        Light = new ArrayList<GLLight>();

        // Object Pools
        Pool.PoolObjectFactory<WidgetTouchEvent> factory = new Pool.PoolObjectFactory<WidgetTouchEvent>() {
            @Override
            public WidgetTouchEvent createObject() {
                return new WidgetTouchEvent();
            }
        };
        widgetTouchEventPool = new Pool<WidgetTouchEvent>(factory, 10);

        // Misc Parameters
        aspectRatio = (float) game.getframeBufferWidth() / (float) game.getframeBufferHeight();
        MazeWidth = 1.0f * (AssetsAndResource.aspectRatio/ 1.778f);
        MazeHeight = 1.0f;
        CardWidth = 0.1067f;
        CardHeight = 0.16f;
        CardLength =  0.00125f;
        CardStackShift = 0.00267f;
        ZoomCardWidth = 1.005f;
        ZoomCardHeight = 1.5f;
        CameraPosition = new GLGeometry.GLPoint(camera_x, camera_y, camera_z);
    }


    public static void InitializeAssetsAndResourceForPvP() {
        // Program initialization
        textureProgram = new TextureShaderProgram(game);
        textureShaderProgramLight = new TextureShaderProgramLight(game);
        cubeTextureShaderProgramLight = new CubeTextureShaderProgramLight(game);
        colorShaderProgram = new ColorShaderProgram(game);

        // Frame Buffers
        SceneBuffer = new FrameBufferObject((int) (AssetsAndResource.aspectRatio * (game.getframeBufferHeight()/2)), (game.getframeBufferHeight()/2));
        ShadowBuffer = new FrameBufferObject((int) (AssetsAndResource.aspectRatio * (game.getframeBufferHeight()/16)), (game.getframeBufferHeight()/16));

        initializePvPFixedTexture();

        CardImages = new Hashtable<String, Integer>();
        CardCount = new Hashtable<String, Count>();

        // Fixed Matrix initialization
        MatrixHelper.perspectiveM(projectionMatrix, 18f, aspectRatio, 1f, 10f);

        // setup primary view
        setLookAtM(viewMatrix, 0, CameraPosition.x, CameraPosition.y, CameraPosition.z, 0f, 0f, 0.1f, 0f, 1f, 0f);
        multiplyMM(tempMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        invertM(invertedViewProjectionMatrix, 0, tempMatrix, 0);

        // setup ortho projection matrix
        orthoM(OrthoProjectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        invertM(invertedOrthoProjectionMatrix, 0, OrthoProjectionMatrix, 0);

        // setup light source view
        setLookAtM(tempMatrix, 0, 0f, 2.88f, 0.0f, 0f, 0f, 0.0f, 0f, 0f, -1.0f);
        multiplyMM(depthVPMatrix, 0, projectionMatrix, 0, tempMatrix, 0);

        // Setup IVP Matrix for UI
        game.getInput().setMatrices(AssetsAndResource.invertedOrthoProjectionMatrix, AssetsAndResource.invertedViewProjectionMatrix);

        // Light initialization
        float [] LightDirectionOrPositionInEyeSpace = new float[4];
        multiplyMV(LightDirectionOrPositionInEyeSpace, 0, AssetsAndResource.viewMatrix, 0, new float[] {0f, -1.0f, 0f, 0f}, 0);
        GLLight DirectionalLight = new GLLight(GLLight.LightType.Directional, new float[] {0, 0, 0, 0}, LightDirectionOrPositionInEyeSpace,
                new float[] {0.2f, 0.2f, 0.2f}, 0, 0);
        Light.add(DirectionalLight);

        multiplyMV(LightDirectionOrPositionInEyeSpace, 0, AssetsAndResource.viewMatrix, 0, new float[] {1.0f, 1.0f, 1.0f, 1.0f}, 0);
        GLLight PointLight1 = new GLLight(GLLight.LightType.Point, LightDirectionOrPositionInEyeSpace, new float[] {0, 0, 0},
                new float[] {0.2f, 0.2f, 0.2f}, 0, 0);
        Light.add(PointLight1);

        multiplyMV(LightDirectionOrPositionInEyeSpace, 0, AssetsAndResource.viewMatrix, 0, new float[] {1.0f, 1.0f, -1.0f, 1.0f}, 0);
        GLLight PointLight2 = new GLLight(GLLight.LightType.Point, LightDirectionOrPositionInEyeSpace, new float[] {0, 0, 0},
                new float[] {0.2f, 0.2f, 0.2f}, 0, 0);
        Light.add(PointLight2);

        multiplyMV(LightDirectionOrPositionInEyeSpace, 0, AssetsAndResource.viewMatrix, 0, new float[] {-1.0f, 1.0f, -1.0f, 1.0f}, 0);
        GLLight PointLight3 = new GLLight(GLLight.LightType.Point, LightDirectionOrPositionInEyeSpace, new float[] {0, 0, 0},
                new float[] {0.2f, 0.2f, 0.2f}, 0, 0);
        Light.add(PointLight3);

        multiplyMV(LightDirectionOrPositionInEyeSpace, 0, AssetsAndResource.viewMatrix, 0, new float[] {-1.0f, 1.0f, 1.0f, 1.0f}, 0);
        GLLight PointLight4 = new GLLight(GLLight.LightType.Point, LightDirectionOrPositionInEyeSpace, new float[] {0, 0, 0},
                new float[] {0.2f, 0.2f, 0.2f}, 0, 0);
        Light.add(PointLight4);
    }


    public static void FreeAssetsAndResourcesForPvP() {
        // Programs free
        textureProgram.deleteProgram();
        textureShaderProgramLight.deleteProgram();
        cubeTextureShaderProgramLight.deleteProgram();
        colorShaderProgram.deleteProgram();

        // Frame buffer free
        ShadowBuffer.freeFBO();

        freePvPFixedTexture();

        RemoveAllCardTexture();

        // Light free
        Light.clear();

        // Object Pools
        widgetTouchEventPool.clear();
    }

    private static void initializePvPFixedTexture() {
        // Textures generation
        if (Base != 0) {
            TextureHelper.freeTexture(Base);
        }
        Base = TextureHelper.loadTexture(game, "Base_5.png");
        if (cardBackside != 0) {
            TextureHelper.freeTexture(cardBackside);
        }
        cardBackside = TextureHelper.loadTexture(game, "cardbackside.png");
        if (cardBorder != 0) {
            TextureHelper.freeTexture(cardBorder);
        }
        cardBorder = TextureHelper.loadTexture(game, "CardBorder.png");
        if (cardDeckSides != 0) {
            TextureHelper.freeTexture(cardDeckSides);
        }
        cardDeckSides = TextureHelper.loadTexture(game, "CardDeckSide.png");
        if (pauseButton != 0) {
            TextureHelper.freeTexture(pauseButton);
        }
        pauseButton = TextureHelper.loadTexture(game, "PauseButton.png");
        if (AcceptButton != 0) {
            TextureHelper.freeTexture(AcceptButton);
        }
        AcceptButton = TextureHelper.loadTexture(game, "PauseButton.png");
        if (DeclineButton != 0) {
            TextureHelper.freeTexture(DeclineButton);
        }
        DeclineButton = TextureHelper.loadTexture(game, "PauseButton.png");
        if (SummonButton != 0) {
            TextureHelper.freeTexture(SummonButton);
        }
        SummonButton = TextureHelper.loadTexture(game, "PauseButton.png");
        if (AddToManaButton != 0) {
            TextureHelper.freeTexture(AddToManaButton);
        }
        AddToManaButton = TextureHelper.loadTexture(game, "PauseButton.png");
        if (AttackButton != 0) {
            TextureHelper.freeTexture(AttackButton);
        }
        AttackButton = TextureHelper.loadTexture(game, "PauseButton.png");
        if (BlockButton != 0) {
            TextureHelper.freeTexture(BlockButton);
        }
        BlockButton = TextureHelper.loadTexture(game, "PauseButton.png");
        if (TapAbilityButton != 0) {
            TextureHelper.freeTexture(TapAbilityButton);
        }
        TapAbilityButton = TextureHelper.loadTexture(game, "PauseButton.png");
        if (ZoomButton != 0) {
            TextureHelper.freeTexture(ZoomButton);
        }
        ZoomButton = TextureHelper.loadTexture(game, "PauseButton.png");
        if (EndTurnButton != 0) {
            TextureHelper.freeTexture(EndTurnButton);
        }
        EndTurnButton = TextureHelper.loadTexture(game, "PauseButton.png");
        if (PlayerImage != 0) {
            TextureHelper.freeTexture(PlayerImage);
        }
        PlayerImage = TextureHelper.loadTexture(game, "PauseButton.png");
        if (OpponentImage != 0) {
            TextureHelper.freeTexture(OpponentImage);
        }
        OpponentImage = TextureHelper.loadTexture(game, "PauseButton.png");
        if (BlueScreenImage != 0) {
            TextureHelper.freeTexture(BlueScreenImage);
        }
        BlueScreenImage = TextureHelper.loadTexture(game, "BlueScreen.png");
        if (RedScreenImage != 0) {
            TextureHelper.freeTexture(RedScreenImage);
        }
        RedScreenImage = TextureHelper.loadTexture(game, "RedScreen.png");
    }

    private static void freePvPFixedTexture() {
        // Texture free
        if (Base != 0) {
            TextureHelper.freeTexture(Base);
        }
        Base = 0;
        if (cardBackside != 0) {
            TextureHelper.freeTexture(cardBackside);
        }
        cardBackside = 0;
        if (cardBorder !=0) {
            TextureHelper.freeTexture(cardBorder);
        }
        cardBorder = 0;
        if (cardDeckSides != 0) {
            TextureHelper.freeTexture(cardDeckSides);
        }
        cardDeckSides = 0;
        if (pauseButton != 0) {
            TextureHelper.freeTexture(pauseButton);
        }
        pauseButton = 0;
        if (AcceptButton != 0) {
            TextureHelper.freeTexture(AcceptButton);
        }
        AcceptButton = 0;
        if (DeclineButton != 0) {
            TextureHelper.freeTexture(DeclineButton);
        }
        DeclineButton = 0;
        if (SummonButton != 0) {
            TextureHelper.freeTexture(SummonButton);
        }
        SummonButton = 0;
        if (AddToManaButton != 0) {
            TextureHelper.freeTexture(AddToManaButton);
        }
        AddToManaButton = 0;
        if (AttackButton != 0) {
            TextureHelper.freeTexture(AttackButton);
        }
        AttackButton = 0;
        if (BlockButton != 0) {
            TextureHelper.freeTexture(BlockButton);
        }
        BlockButton = 0;
        if (TapAbilityButton != 0) {
            TextureHelper.freeTexture(TapAbilityButton);
        }
        TapAbilityButton = 0;
        if (ZoomButton != 0) {
            TextureHelper.freeTexture(ZoomButton);
        }
        ZoomButton = 0;
        if (EndTurnButton != 0) {
            TextureHelper.freeTexture(EndTurnButton);
        }
        EndTurnButton = 0;
        if (PlayerImage != 0) {
            TextureHelper.freeTexture(PlayerImage);
        }
        PlayerImage = 0;
        if (OpponentImage != 0) {
            TextureHelper.freeTexture(OpponentImage);
        }
        OpponentImage = 0;
        if (BlueScreenImage != 0) {
            TextureHelper.freeTexture(BlueScreenImage);
        }
        BlueScreenImage = 0;
        if (RedScreenImage != 0) {
            TextureHelper.freeTexture(RedScreenImage);
        }
        RedScreenImage = 0;
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

    public static int getFixedTexture(int ID) {
        if (ID == BaseID) {
            return Base;
        } else if (ID == cardBacksideID) {
            return cardBackside;
        } else if (ID == cardBorderID) {
            return cardBorder;
        } else if (ID == cardDeckSidesID) {
            return cardDeckSides;
        } else if (ID == pauseButtonID) {
            return pauseButton;
        } else if (ID == AcceptButtonID) {
            return AcceptButton;
        } else if (ID == DeclineButtonID) {
            return DeclineButton;
        } else if (ID == SummonButtonID) {
            return SummonButton;
        } else if (ID == AddToManaButtonID) {
            return AddToManaButton;
        } else if (ID == AttackButtonID) {
            return AttackButton;
        } else if (ID == BlockButtonID) {
            return BlockButton;
        } else if (ID == TapAbilityButtonID) {
            return TapAbilityButton;
        } else if (ID == ZoomButtonID) {
            return ZoomButton;
        } else if (ID == EndTurnButtonID) {
            return EndTurnButton;
        } else if (ID == PlayerImageID) {
            return PlayerImage;
        } else if (ID == OpponentImageID) {
            return OpponentImage;
        } else if (ID == BlueScreenImageID) {
            return BlueScreenImage;
        } else if (ID == RedScreenImageID) {
            return RedScreenImage;
        } else {
            return 0;
        }
    }

    public static int getTextureIdForButton(ControllerButton button) {
        if (button == ControllerButton.Pause) {
            return pauseButtonID;
        } else if (button == ControllerButton.Accept) {
            return AcceptButtonID;
        } else if (button == ControllerButton.Decline) {
            return DeclineButtonID;
        } else if (button == ControllerButton.SummonOrCast) {
            return SummonButtonID;
        } else if (button == ControllerButton.AddToMana) {
            return AddToManaButtonID;
        } else if (button == ControllerButton.Attack) {
            return AttackButtonID;
        } else if (button == ControllerButton.Block) {
            return BlockButtonID;
        } else if (button == ControllerButton.TapAbility) {
            return TapAbilityButtonID;
        } else if (button == ControllerButton.Zoom) {
            return ZoomButtonID;
        } else if (button == ControllerButton.EndTurn) {
            return EndTurnButtonID;
        } else if (button == ControllerButton.Player) {
            return PlayerImageID;
        } else if (button == ControllerButton.Opponent) {
            return OpponentImageID;
        } else {
            throw new RuntimeException("Invalid Button type");
        }
    }

    public static void setCurrentWorld(World w) {
        world = w;
    }

    public static World getWorld() {
        return world;
    }
}
