package koustav.duelmasters.main.androidgameduelmasterwidgetmodels;

import java.util.ArrayList;
import java.util.List;

import koustav.duelmasters.main.androidgameduelmastersassetsandresourcesforscreen.AssetsAndResourceForPvP;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.WidgetPosition;
import koustav.duelmasters.main.androidgameopenglobjectmodels.Cube;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgamesframework.Input;

import static android.opengl.Matrix.*;

/**
 * Created by Koustav on 3/21/2016.
 */
public class CardStackWidget implements Widget{
    WidgetPosition Position;
    float[] relativeNearPointAfterRot;
    float[] relativeFarPointAfterRot;
    GLPoint relativeNearPoint;
    GLPoint relativeFarPoint;

    // shadow enable;
    boolean shadowEnable;

    // Texture Array
    int[] textureArrays;

    // openGL object model, physical unit
    Cube cube;

    // Logical object
    public ArrayList<Cards> cardStack;

    public CardStackWidget() {
        Position = new WidgetPosition();
        relativeNearPointAfterRot = new float[4];
        relativeFarPointAfterRot = new float[4];
        relativeNearPoint = new GLPoint(0, 0, 0);
        relativeFarPoint = new GLPoint(0, 0, 0);

        textureArrays = new int[6];
        shadowEnable = false;
        cube = null;
    }

    @Override
    public void  draw() {
        for (int i = 0; i< 6; i++) {
            textureArrays[i] = AssetsAndResourceForPvP.cardDeckSides;
        }
        textureArrays[3] = AssetsAndResourceForPvP.cardBackside;

        AssetsAndResourceForPvP.cubeTextureShaderProgramLight.useProgram();
        AssetsAndResourceForPvP.cubeTextureShaderProgramLight.setUniforms(AssetsAndResourceForPvP.modelViewMatrix, AssetsAndResourceForPvP.it_modelViewMatrix,
                ((AssetsAndResourceForPvP.game.getGLFragColoring() == 0) ? AssetsAndResourceForPvP.modelViewProjectionMatrix: AssetsAndResourceForPvP.ShadowMatrix),
                AssetsAndResourceForPvP.ShadowMatrix, AssetsAndResourceForPvP.Light, cube.getMaterial(), textureArrays, AssetsAndResourceForPvP.ShadowBuffer.getrenderTex(),
                ((AssetsAndResourceForPvP.game.getGLFragColoring() == 0)? shadowEnable: false));

        cube.bindData(AssetsAndResourceForPvP.cubeTextureShaderProgramLight.getPositionAttributeLocation(), AssetsAndResourceForPvP.cubeTextureShaderProgramLight.getNormalAttributeLocation(),
                AssetsAndResourceForPvP.cubeTextureShaderProgramLight.getTextureCoordinatesAttributeLocation());
        cube.draw();
    }

    @Override
    public boolean isTouched(List<Input.TouchEvent> touchEvents) {
        setIdentityM(AssetsAndResourceForPvP.tempMatrix, 0);
        if (Position.rotaion.angle != 0) {
            rotateM(AssetsAndResourceForPvP.tempMatrix, 0, Position.rotaion.angle, -Position.rotaion.x, -Position.rotaion.y,
                    -Position.rotaion.z);
        }

        boolean isTouched = false;
        Input input = AssetsAndResourceForPvP.game.getInput();
        if (input.isTouchDown(0)) {
            GLPoint relativeNearPointAfterTrans = input.getNearPoint(0).translate(Position.Centerposition.getVector().scale(-1));
            GLPoint relativeFarPointAfterTrans = input.getFarPoint(0).translate(Position.Centerposition.getVector().scale(-1));

            multiplyMV(relativeNearPointAfterRot, 0, AssetsAndResourceForPvP.tempMatrix, 0, new float[] {relativeNearPointAfterTrans.x,
                    relativeNearPointAfterTrans.y, relativeNearPointAfterTrans.z, 0f}, 0);
            multiplyMV(relativeFarPointAfterRot, 0, AssetsAndResourceForPvP.tempMatrix, 0, new float[] {relativeFarPointAfterTrans.x,
                    relativeFarPointAfterTrans.y, relativeFarPointAfterTrans.z, 0f}, 0);

            relativeNearPoint.x = relativeNearPointAfterRot[0];
            relativeNearPoint.y = relativeNearPointAfterRot[1];
            relativeNearPoint.z = relativeNearPointAfterRot[2];

            relativeFarPoint.x = relativeFarPointAfterRot[0];
            relativeFarPoint.y = relativeFarPointAfterRot[1];
            relativeFarPoint.z = relativeFarPointAfterRot[2];

            GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                    new GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)), cube.length/2);

            float width = Math.abs(intersectingPoint.x);
            float height = Math.abs(intersectingPoint.z);

            if (width <= (cube.width * Position.X_scale)/2 && height <= (cube.height * Position.Z_scale)/2) {
                return true;
            }

            intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                    new GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)), -cube.length/2);

            width = Math.abs(intersectingPoint.x);
            height = Math.abs(intersectingPoint.z);

            if (width <= (cube.width * Position.X_scale)/2 && height <= (cube.height * Position.Z_scale)/2) {
                return true;
            }

            GLGeometry.GLPoint NormalPoint = GLGeometry.GLRayIntersectionWithPlane(new GLGeometry.GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)),
                    new GLGeometry.GLPlane(new GLPoint(0,0,0), GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)));

            width = Math.abs(NormalPoint.x);
            float length = Math.abs(NormalPoint.y);
            height = Math.abs(NormalPoint.z);

            if (width <= (cube.width * Position.X_scale)/2 && length <= (cube.length * Position.Y_scale)/2 &&
                    height <= (cube.height * Position.Z_scale)/2) {
                return true;
            }

            if (NormalPoint.z > 0) {
                intersectingPoint = GLGeometry.GLRayIntersectionWithPlane(new GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)),
                        new GLPlane(new GLPoint(0, 0, cube.length/2), new GLVector(0,0,1)));

                width = Math.abs(intersectingPoint.x);
                length = Math.abs(intersectingPoint.y);

                if (width <= (cube.width * Position.X_scale)/2 && length <= (cube.length * Position.Y_scale)/2) {
                    return true;
                }
            } else {
                intersectingPoint = GLGeometry.GLRayIntersectionWithPlane(new GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)),
                        new GLPlane(new GLPoint(0, 0, -cube.length/2), new GLVector(0,0,-1)));

                width = Math.abs(intersectingPoint.x);
                length = Math.abs(intersectingPoint.y);

                if (width <= (cube.width * Position.X_scale)/2 && length <= (cube.length * Position.Y_scale)/2) {
                    return true;
                }
            }
        }

        for(int i = 0; i<touchEvents.size(); i++) {
            Input.TouchEvent event = touchEvents.get(i);
            if (event.type == Input.TouchEvent.TOUCH_UP) {
                GLPoint relativeNearPointAfterTrans = event.nearPoint.translate(Position.Centerposition.getVector().scale(-1));
                GLPoint relativeFarPointAfterTrans = event.farPoint.translate(Position.Centerposition.getVector().scale(-1));

                multiplyMV(relativeNearPointAfterRot, 0, AssetsAndResourceForPvP.tempMatrix, 0, new float[] {relativeNearPointAfterTrans.x,
                        relativeNearPointAfterTrans.y, relativeNearPointAfterTrans.z, 0f}, 0);
                multiplyMV(relativeFarPointAfterRot, 0, AssetsAndResourceForPvP.tempMatrix, 0, new float[] {relativeFarPointAfterTrans.x,
                        relativeFarPointAfterTrans.y, relativeFarPointAfterTrans.z, 0f}, 0);

                relativeNearPoint.x = relativeNearPointAfterRot[0];
                relativeNearPoint.y = relativeNearPointAfterRot[1];
                relativeNearPoint.z = relativeNearPointAfterRot[2];

                relativeFarPoint.x = relativeFarPointAfterRot[0];
                relativeFarPoint.y = relativeFarPointAfterRot[1];
                relativeFarPoint.z = relativeFarPointAfterRot[2];

                GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                        new GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)), cube.length/2);

                float width = Math.abs(intersectingPoint.x);
                float height = Math.abs(intersectingPoint.z);

                if (width <= (cube.width * Position.X_scale)/2 && height <= (cube.height * Position.Z_scale)/2) {
                    isTouched = true;
                    break;
                }

                intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                        new GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)), -cube.length/2);

                width = Math.abs(intersectingPoint.x);
                height = Math.abs(intersectingPoint.z);

                if (width <= (cube.width * Position.X_scale)/2 && height <= (cube.height * Position.Z_scale)/2) {
                    isTouched =true;
                    break;
                }

                GLGeometry.GLPoint NormalPoint = GLGeometry.GLRayIntersectionWithPlane(new GLGeometry.GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)),
                        new GLGeometry.GLPlane(new GLPoint(0,0,0), GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)));

                width = Math.abs(NormalPoint.x);
                float length = Math.abs(NormalPoint.y);
                height = Math.abs(NormalPoint.z);

                if (width <= (cube.width * Position.X_scale)/2 && length <= (cube.length * Position.Y_scale)/2 &&
                        height <= (cube.height * Position.Z_scale)/2) {
                    isTouched = true;
                    break;
                }

                if (NormalPoint.z > 0) {
                    intersectingPoint = GLGeometry.GLRayIntersectionWithPlane(new GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)),
                            new GLPlane(new GLPoint(0, 0, cube.length/2), new GLVector(0,0,1)));

                    width = Math.abs(intersectingPoint.x);
                    length = Math.abs(intersectingPoint.y);

                    if (width <= (cube.width * Position.X_scale)/2 && length <= (cube.length * Position.Y_scale)/2) {
                        isTouched = true;
                        break;
                    }
                } else {
                    intersectingPoint = GLGeometry.GLRayIntersectionWithPlane(new GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)),
                            new GLPlane(new GLPoint(0, 0, -cube.length/2), new GLVector(0,0,-1)));

                    width = Math.abs(intersectingPoint.x);
                    length = Math.abs(intersectingPoint.y);

                    if (width <= (cube.width * Position.X_scale)/2 && length <= (cube.length * Position.Y_scale)/2) {
                        isTouched = true;
                        break;
                    }
                }
            }
        }

        return isTouched;
    }

    @Override
    public void setTranslateRotateScale(WidgetPosition position) {
        // Store this info required for isTouched
        this.Position.rotaion.angle = position.rotaion.angle;
        this.Position.rotaion.x = position.rotaion.x;
        this.Position.rotaion.y = position.rotaion.y;
        this.Position.rotaion.z = position.rotaion.z;
        Position.Centerposition.x = position.Centerposition.x;
        Position.Centerposition.y = position.Centerposition.y;
        Position.Centerposition.z = position.Centerposition.z;


        setIdentityM(AssetsAndResourceForPvP.modelMatrix, 0);
        translateM(AssetsAndResourceForPvP.modelMatrix, 0, Position.Centerposition.x, Position.Centerposition.y,
                Position.Centerposition.z);
        if (this.Position.rotaion.angle != 0) {
            rotateM(AssetsAndResourceForPvP.modelMatrix, 0, this.Position.rotaion.angle, -this.Position.rotaion.x,
                    -this.Position.rotaion.y, -this.Position.rotaion.z);
        }
        scaleM(AssetsAndResourceForPvP.modelMatrix, 0, Position.X_scale, Position.Y_scale, Position.Z_scale);
        multiplyMM(AssetsAndResourceForPvP.modelViewMatrix, 0, AssetsAndResourceForPvP.viewMatrix, 0, AssetsAndResourceForPvP.modelMatrix, 0);
        invertM(AssetsAndResourceForPvP.tempMatrix, 0, AssetsAndResourceForPvP.modelViewMatrix, 0);
        transposeM(AssetsAndResourceForPvP.it_modelViewMatrix, 0, AssetsAndResourceForPvP.tempMatrix, 0);
        multiplyMM(
                AssetsAndResourceForPvP.modelViewProjectionMatrix, 0,
                AssetsAndResourceForPvP.projectionMatrix, 0,
                AssetsAndResourceForPvP.modelViewMatrix, 0);

        multiplyMM(
                AssetsAndResourceForPvP.ShadowMatrix, 0,
                AssetsAndResourceForPvP.depthVPMatrix, 0,
                AssetsAndResourceForPvP.modelMatrix, 0);
    }

    @Override
    public void ShadowEnable(boolean shadowEnable) {
        this.shadowEnable = shadowEnable;
    }

    @Override
    public void LinkGLobject(Object ...objs) {
        cube = (Cube) objs[0];
    }

    @Override
    public void LinkLogicalObject(Object obj) {
        cardStack = (ArrayList<Cards>) obj;
    }

    @Override
    public void setMode(int i) {

    }
}
