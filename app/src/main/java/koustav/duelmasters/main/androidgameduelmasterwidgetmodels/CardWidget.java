package koustav.duelmasters.main.androidgameduelmasterwidgetmodels;


import java.util.List;

import koustav.duelmasters.main.androidgameduelmastersassetsandresourcesforscreen.AssetsAndResourceForPvP;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.WidgetPosition;
import koustav.duelmasters.main.androidgameopenglobjectmodels.XZRectangle;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;
import koustav.duelmasters.main.androidgamesframework.Input;

import static android.opengl.Matrix.*;

/**
 * Created by Koustav on 3/16/2016.
 */
public class CardWidget implements Widget {
    WidgetPosition Position;
    float[] relativeNearPointAfterRot;
    float[] relativeFarPointAfterRot;
    GLPoint relativeNearPoint;
    GLPoint relativeFarPoint;

    // shadow enable;
    boolean shadowEnable;

    // OpenGL object model, physical unit
    XZRectangle glcard;

    // logical unit
    Cards card;

    public CardWidget () {
        Position = new WidgetPosition();
        relativeNearPointAfterRot = new float[4];
        relativeFarPointAfterRot = new float[4];
        relativeNearPoint = new GLPoint(0, 0, 0);
        relativeFarPoint = new GLPoint(0, 0, 0);

        shadowEnable = false;
        glcard = null;
        card = null;
    }

    @Override
    public void draw(){
        AssetsAndResourceForPvP.textureShaderProgramLight.useProgram();
        AssetsAndResourceForPvP.textureShaderProgramLight.setUniforms(AssetsAndResourceForPvP.modelViewMatrix, AssetsAndResourceForPvP.it_modelViewMatrix,
                ((AssetsAndResourceForPvP.game.getGLFragColoring() == 0)? AssetsAndResourceForPvP.modelViewProjectionMatrix :
                AssetsAndResourceForPvP.ShadowMatrix), AssetsAndResourceForPvP.ShadowMatrix, AssetsAndResourceForPvP.Light, glcard.getMaterial(),
                AssetsAndResourceForPvP.getCardTexture(/*card.getNameID()*/"cardbackside"), AssetsAndResourceForPvP.ShadowBuffer.getrenderTex(),
                ((AssetsAndResourceForPvP.game.getGLFragColoring() == 0)? shadowEnable: false));

        glcard.bindData(AssetsAndResourceForPvP.textureShaderProgramLight.getPositionAttributeLocation(),
                AssetsAndResourceForPvP.textureShaderProgramLight.getNormalAttributeLocation(),
                AssetsAndResourceForPvP.textureShaderProgramLight.getTextureCoordinatesAttributeLocation());
        glcard.draw();
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
                    new GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)), 0);

            float width = Math.abs(intersectingPoint.x);
            float height = Math.abs(intersectingPoint.z);

            if (width <= (glcard.width * Position.X_scale)/2 && height <= (glcard.height * Position.Z_scale)/2) {
                return true;
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
                        new GLRay(relativeNearPoint, GLGeometry.GLVectorBetween(relativeNearPoint, relativeFarPoint)), 0);

                float width = Math.abs(intersectingPoint.x);
                float height = Math.abs(intersectingPoint.z);

                if (width <= (glcard.width * Position.X_scale)/2 && height <= (glcard.height * Position.Z_scale)/2) {
                    isTouched = true;
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
        this.Position.Centerposition.x = position.Centerposition.x;
        this.Position.Centerposition.y = position.Centerposition.y;
        this.Position.Centerposition.z = position.Centerposition.z;
        this.Position.X_scale = position.X_scale;
        this.Position.Y_scale = position.Y_scale;
        this.Position.Z_scale = position.Z_scale;

        setIdentityM(AssetsAndResourceForPvP.modelMatrix, 0);
        translateM(AssetsAndResourceForPvP.modelMatrix, 0, this.Position.Centerposition.x, this.Position.Centerposition.y,
                this.Position.Centerposition.z);
        if (this.Position.rotaion.angle != 0) {
            rotateM(AssetsAndResourceForPvP.modelMatrix, 0, this.Position.rotaion.angle, this.Position.rotaion.x, this.Position.rotaion.y,
                    this.Position.rotaion.z);
        }
        scaleM(AssetsAndResourceForPvP.modelMatrix, 0, this.Position.X_scale, this.Position.Y_scale,
                this.Position.Z_scale);
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
        if (objs.length != 1)
            throw new IllegalArgumentException("Number of arguments must be one");

        glcard = (XZRectangle) objs[0];
    }

    @Override
    public void LinkLogicalObject(Object obj) {
        this.card = (Cards) obj;
    }

    @Override
    public void setMode(int i) {

    }
}
