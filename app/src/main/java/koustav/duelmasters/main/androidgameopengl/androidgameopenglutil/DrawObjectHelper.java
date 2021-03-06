package koustav.duelmasters.main.androidgameopengl.androidgameopenglutil;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmasterswidgetcoordinationtools.Query;
import koustav.duelmasters.main.androidgameduelmastersworlds.PvPWorld;
import koustav.duelmasters.main.androidgameopengl.androidgameopengllightscamerashades.LightsCameraShades;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglobjectmodels.Cube;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglobjectmodels.FullScreenRectangle;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglobjectmodels.ScreenRectangle;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglobjectmodels.UniformXZRectangle;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglobjectmodels.XZRectangle;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CONSTANT_ALPHA;
import static android.opengl.GLES20.GL_ONE_MINUS_CONSTANT_ALPHA;
import static android.opengl.GLES20.glBlendColor;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDepthMask;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.Matrix.setIdentityM;

/**
 * Created by Koustav on 4/4/2016.
 */
public class DrawObjectHelper {
    public static void drawOneRectangle(LightsCameraShades lightsCameraShades, XZRectangle glret,
                                        int texture, boolean shadowEnable) {
        lightsCameraShades.textureShaderProgramLight.useProgram();
        lightsCameraShades.textureShaderProgramLight.setUniforms(lightsCameraShades.modelViewMatrix, lightsCameraShades.it_modelViewMatrix,
                ((lightsCameraShades.game.getGLFragColoringSkip() == 0)? lightsCameraShades.modelViewProjectionMatrix :
                        lightsCameraShades.ShadowMatrix), lightsCameraShades.ShadowMatrix, lightsCameraShades.Light, glret.getMaterial(),
                texture, lightsCameraShades.ShadowBuffer.getrenderTex(),
                ((lightsCameraShades.game.getGLFragColoringSkip() == 0)? shadowEnable: false));

        glret.bindData(lightsCameraShades.textureShaderProgramLight.getPositionAttributeLocation(),
                lightsCameraShades.textureShaderProgramLight.getNormalAttributeLocation(),
                lightsCameraShades.textureShaderProgramLight.getTextureCoordinatesAttributeLocation());
        glret.draw();
    }

    public static void drawOneRectangle(XZRectangle glret, int texture, boolean shadowEnable) {
        AssetsAndResource.textureShaderProgramLight.useProgram();
        AssetsAndResource.textureShaderProgramLight.setUniforms(AssetsAndResource.modelViewMatrix, AssetsAndResource.it_modelViewMatrix,
                ((AssetsAndResource.game.getGLFragColoringSkip() == 0)? AssetsAndResource.modelViewProjectionMatrix :
                        AssetsAndResource.ShadowMatrix), AssetsAndResource.ShadowMatrix, AssetsAndResource.Light, glret.getMaterial(),
                texture, AssetsAndResource.ShadowBuffer.getrenderTex(),
                ((AssetsAndResource.game.getGLFragColoringSkip() == 0)? shadowEnable: false));

        glret.bindData(AssetsAndResource.textureShaderProgramLight.getPositionAttributeLocation(),
                AssetsAndResource.textureShaderProgramLight.getNormalAttributeLocation(),
                AssetsAndResource.textureShaderProgramLight.getTextureCoordinatesAttributeLocation());
        glret.draw();
    }

    public static void drawOneUniformRectangle(LightsCameraShades lightsCameraShades,
                                               UniformXZRectangle glret, float[] Color, boolean shadowEnable) {
        lightsCameraShades.uniformColorShaderLightProgram.useProgram();
        lightsCameraShades.uniformColorShaderLightProgram.setUniforms(lightsCameraShades.modelViewMatrix, lightsCameraShades.it_modelViewMatrix,
                ((lightsCameraShades.game.getGLFragColoringSkip() == 0)? lightsCameraShades.modelViewProjectionMatrix :
                        lightsCameraShades.ShadowMatrix), lightsCameraShades.ShadowMatrix, lightsCameraShades.Light, glret.getMaterial(),
                Color, lightsCameraShades.ShadowBuffer.getrenderTex(),
                ((lightsCameraShades.game.getGLFragColoringSkip() == 0)? shadowEnable: false));

        glret.bindData(lightsCameraShades.uniformColorShaderLightProgram.getPositionAttributeLocation(),
                lightsCameraShades.uniformColorShaderLightProgram.getNormalAttributeLocation(),
                0);
        glret.draw();
    }

    public static void drawOneUniformRectangle(UniformXZRectangle glret, float[] Color, boolean shadowEnable) {
        AssetsAndResource.uniformColorShaderLightProgram.useProgram();
        AssetsAndResource.uniformColorShaderLightProgram.setUniforms(AssetsAndResource.modelViewMatrix, AssetsAndResource.it_modelViewMatrix,
                ((AssetsAndResource.game.getGLFragColoringSkip() == 0)? AssetsAndResource.modelViewProjectionMatrix :
                        AssetsAndResource.ShadowMatrix), AssetsAndResource.ShadowMatrix, AssetsAndResource.Light, glret.getMaterial(),
                Color, AssetsAndResource.ShadowBuffer.getrenderTex(),
                ((AssetsAndResource.game.getGLFragColoringSkip() == 0)? shadowEnable: false));

        glret.bindData(AssetsAndResource.uniformColorShaderLightProgram.getPositionAttributeLocation(),
                AssetsAndResource.uniformColorShaderLightProgram.getNormalAttributeLocation(),
                0);
        glret.draw();
    }

    public static void drawOneCube(LightsCameraShades lightsCameraShades,
                                   Cube cube, int[] textureArrays, boolean shadowEnable) {
        lightsCameraShades.cubeTextureShaderProgramLight.useProgram();
        lightsCameraShades.cubeTextureShaderProgramLight.setUniforms(lightsCameraShades.modelViewMatrix, lightsCameraShades.it_modelViewMatrix,
                ((lightsCameraShades.game.getGLFragColoringSkip() == 0) ? lightsCameraShades.modelViewProjectionMatrix:
                        lightsCameraShades.ShadowMatrix), lightsCameraShades.ShadowMatrix, lightsCameraShades.Light, cube.getMaterial(),
                textureArrays, lightsCameraShades.ShadowBuffer.getrenderTex(),
                ((lightsCameraShades.game.getGLFragColoringSkip() == 0)? shadowEnable: false));

        cube.bindData(lightsCameraShades.cubeTextureShaderProgramLight.getPositionAttributeLocation(),
                lightsCameraShades.cubeTextureShaderProgramLight.getNormalAttributeLocation(),
                lightsCameraShades.cubeTextureShaderProgramLight.getTextureCoordinatesAttributeLocation());
        cube.draw();
    }

    public static void drawOneCube(Cube cube, int[] textureArrays, boolean shadowEnable) {
        AssetsAndResource.cubeTextureShaderProgramLight.useProgram();
        AssetsAndResource.cubeTextureShaderProgramLight.setUniforms(AssetsAndResource.modelViewMatrix, AssetsAndResource.it_modelViewMatrix,
                ((AssetsAndResource.game.getGLFragColoringSkip() == 0) ? AssetsAndResource.modelViewProjectionMatrix:
                        AssetsAndResource.ShadowMatrix),AssetsAndResource.ShadowMatrix, AssetsAndResource.Light, cube.getMaterial(),
                textureArrays, AssetsAndResource.ShadowBuffer.getrenderTex(),
                ((AssetsAndResource.game.getGLFragColoringSkip() == 0)? shadowEnable: false));

        cube.bindData(AssetsAndResource.cubeTextureShaderProgramLight.getPositionAttributeLocation(),
                AssetsAndResource.cubeTextureShaderProgramLight.getNormalAttributeLocation(),
                AssetsAndResource.cubeTextureShaderProgramLight.getTextureCoordinatesAttributeLocation());
        cube.draw();
    }

    public static void drawOneScreenRectangle(ScreenRectangle rectangle, int texture) {
        AssetsAndResource.textureProgram.useProgram();
        AssetsAndResource.textureProgram.setUniforms(AssetsAndResource.modelOrthoProjectionMatrix, texture);
        rectangle.bindData(AssetsAndResource.textureProgram.getPositionAttributeLocation(),
                AssetsAndResource.textureProgram.getTextureCoordinatesAttributeLocation());
        rectangle.draw();
    }

    public static void drawOneScreenRectangle(LightsCameraShades lightsCameraShades,
                                              ScreenRectangle rectangle, int texture) {
        lightsCameraShades.textureProgram.useProgram();
        lightsCameraShades.textureProgram.setUniforms(lightsCameraShades.modelOrthoProjectionMatrix, texture);
        rectangle.bindData(lightsCameraShades.textureProgram.getPositionAttributeLocation(),
                lightsCameraShades.textureProgram.getTextureCoordinatesAttributeLocation());
        rectangle.draw();
    }

    public static void drawScreen(FullScreenRectangle screenRectangle, int texture) {
        AssetsAndResource.textureProgram.useProgram();
        setIdentityM(AssetsAndResource.tempMatrix, 0);
        AssetsAndResource.textureProgram.setUniforms(AssetsAndResource.tempMatrix, texture);
        screenRectangle.bindData(AssetsAndResource.textureProgram.getPositionAttributeLocation(),
                AssetsAndResource.textureProgram.getTextureCoordinatesAttributeLocation());
        screenRectangle.draw();
    }

    public static void drawScreen(LightsCameraShades lightsCameraShades,
                                  FullScreenRectangle screenRectangle, int texture) {
        lightsCameraShades.textureProgram.useProgram();
        setIdentityM(lightsCameraShades.tempMatrix, 0);
        lightsCameraShades.textureProgram.setUniforms(lightsCameraShades.tempMatrix, texture);
        screenRectangle.bindData(lightsCameraShades.textureProgram.getPositionAttributeLocation(),
                lightsCameraShades.textureProgram.getTextureCoordinatesAttributeLocation());
        screenRectangle.draw();
    }

    public static void drawHighlightBoundaryOfCard(Cube glCurrentSelect, Cube glSelectedCards, Cards card) {
        int[] textureArrays = new int[6];
        if (AssetsAndResource.game.getGLFragColoringSkip() == 0) {
            PvPWorld world = null;
            if (AssetsAndResource.getWorld() instanceof PvPWorld) {
                world = (PvPWorld) AssetsAndResource.getWorld();
            }
            if (world != null && world.getWidgetCoordinator().GetInfo(Query.GetZoomSelectedCard) == card) {
                for (int i = 0; i < 6; i++) {
                    textureArrays[i] = AssetsAndResource.getFixedTexture(AssetsAndResource.BlueScreenImageID);
                }
                glDepthMask(false);
                glEnable(GL_BLEND);
                glBlendColor(0f, 0f, 0f, 0.5f);
                glBlendFunc(GL_CONSTANT_ALPHA, GL_ONE_MINUS_CONSTANT_ALPHA);
                DrawObjectHelper.drawOneCube(glCurrentSelect, textureArrays, false);
                glDisable(GL_BLEND);
                glDepthMask(true);
            }

            if (world != null && (boolean) world.getWidgetCoordinator().GetInfo(Query.IsCardSelected, card) &&
                    !((boolean) world.getWidgetCoordinator().GetInfo(Query.IsCardSelectedPile, card))) {
                for (int i = 0; i < 6; i++) {
                    textureArrays[i] = AssetsAndResource.getFixedTexture(AssetsAndResource.RedScreenImageID);
                }
                glDepthMask(false);
                glEnable(GL_BLEND);
                glBlendColor(0f, 0f, 0f, 0.5f);
                glBlendFunc(GL_CONSTANT_ALPHA, GL_ONE_MINUS_CONSTANT_ALPHA);
                DrawObjectHelper.drawOneCube(glSelectedCards, textureArrays, false);
                glDisable(GL_BLEND);
                glDepthMask(true);
            }
        }
    }
}
