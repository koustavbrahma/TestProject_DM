package koustav.duelmasters.main.androidgameopenglutil;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameopenglobjectmodels.Cube;
import koustav.duelmasters.main.androidgameopenglobjectmodels.XZRectangle;

/**
 * Created by Koustav on 4/4/2016.
 */
public class DrawObjectHelper {

    public static void drawOneCard(Cards card, XZRectangle glcard, boolean shadowEnable) {
        AssetsAndResource.textureShaderProgramLight.useProgram();
        AssetsAndResource.textureShaderProgramLight.setUniforms(AssetsAndResource.modelViewMatrix, AssetsAndResource.it_modelViewMatrix,
                ((AssetsAndResource.game.getGLFragColoring() == 0)? AssetsAndResource.modelViewProjectionMatrix :
                        AssetsAndResource.ShadowMatrix), AssetsAndResource.ShadowMatrix, AssetsAndResource.Light, glcard.getMaterial(),
                AssetsAndResource.getCardTexture(/*card.getNameID()*/"cardbackside"), AssetsAndResource.ShadowBuffer.getrenderTex(),
                ((AssetsAndResource.game.getGLFragColoring() == 0)? shadowEnable: false));

        glcard.bindData(AssetsAndResource.textureShaderProgramLight.getPositionAttributeLocation(),
                AssetsAndResource.textureShaderProgramLight.getNormalAttributeLocation(),
                AssetsAndResource.textureShaderProgramLight.getTextureCoordinatesAttributeLocation());
        glcard.draw();
    }

    public static void drawOneCube(Cube cube, int[] textureArrays, boolean shadowEnable) {
        AssetsAndResource.cubeTextureShaderProgramLight.useProgram();
        AssetsAndResource.cubeTextureShaderProgramLight.setUniforms(AssetsAndResource.modelViewMatrix, AssetsAndResource.it_modelViewMatrix,
                ((AssetsAndResource.game.getGLFragColoring() == 0) ? AssetsAndResource.modelViewProjectionMatrix:
                        AssetsAndResource.ShadowMatrix),AssetsAndResource.ShadowMatrix, AssetsAndResource.Light, cube.getMaterial(),
                textureArrays, AssetsAndResource.ShadowBuffer.getrenderTex(),
                ((AssetsAndResource.game.getGLFragColoring() == 0)? shadowEnable: false));

        cube.bindData(AssetsAndResource.cubeTextureShaderProgramLight.getPositionAttributeLocation(),
                AssetsAndResource.cubeTextureShaderProgramLight.getNormalAttributeLocation(),
                AssetsAndResource.cubeTextureShaderProgramLight.getTextureCoordinatesAttributeLocation());
        cube.draw();
    }
}
