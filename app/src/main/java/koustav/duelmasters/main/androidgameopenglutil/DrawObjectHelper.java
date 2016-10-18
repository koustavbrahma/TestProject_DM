package koustav.duelmasters.main.androidgameopenglutil;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameopenglobjectmodels.Cube;
import koustav.duelmasters.main.androidgameopenglobjectmodels.ScreenRectangle;
import koustav.duelmasters.main.androidgameopenglobjectmodels.XZRectangle;

/**
 * Created by Koustav on 4/4/2016.
 */
public class DrawObjectHelper {

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
}
