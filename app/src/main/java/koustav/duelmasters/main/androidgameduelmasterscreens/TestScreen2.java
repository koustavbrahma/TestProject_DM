package koustav.duelmasters.main.androidgameduelmasterscreens;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;
import koustav.duelmasters.main.androidgameopenglanimation.ParticleShooter;
import koustav.duelmasters.main.androidgameopenglanimation.ParticleSystem;
import koustav.duelmasters.main.androidgameopenglobjects.Base;
import koustav.duelmasters.main.androidgameopenglobjects.Puck;
import koustav.duelmasters.main.androidgameopenglobjects.Table;
import koustav.duelmasters.main.androidgameopenglobjects.XZRectangle;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;
import koustav.duelmasters.main.androidgameopenglutil.GLLight;
import koustav.duelmasters.main.androidgameopenglutil.GLMaterial;
import koustav.duelmasters.main.androidgameopenglutil.TextureHelper;
import koustav.duelmasters.main.androidgameopenglutil.UIHelper;
import koustav.duelmasters.main.androidgamesframework.Input;
import koustav.duelmasters.main.androidgamesframework.Screen;
import koustav.duelmasters.main.androidgamesframeworkimpl.AndroidGame;
import koustav.duelmasters.main.androidgamesframeworkimpl.AndroidOpenGLRenderView;
import koustav.duelmasters.main.androidgameshaderprogram.ParticleShaderProgram;
import koustav.duelmasters.main.androidgameshaderprogram.TextureShaderProgram;
import koustav.duelmasters.main.androidgameshaderprogram.TextureShaderProgramLight;
import koustav.duelmasters.main.androidgameshaderprogram.UniformColorShaderProgram;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.Matrix.*;

/**
 * Created by Koustav on 1/24/2016.
 */
public class TestScreen2 extends Screen {
    private Base base;
    private XZRectangle table;
    private Puck puck;
    private XZRectangle cardProjection;
    private XZRectangle invCardProjection;

    private TextureShaderProgram textureProgram;
    private UniformColorShaderProgram colorProgram;
    private ParticleShaderProgram particleProgram;
    private TextureShaderProgramLight textureShaderProgramLight;

    private int basetexture;
    private int tabletexture;
    private int cardbackside;

    private float[] viewMatrix;
    private float[] projectionMatrix;
    private float[] invertedViewProjectionMatrix;

    private float[] modelMatrix;
    private float[] modelViewMatrix;
    private float[] it_modelViewMatrix;
    private float[] modelViewProjectionMatrix;
    private float[] tempMatrix;


    private ParticleSystem particleSystem;
    private ParticleShooter redParticleShooter;

    float tmpx;
    float tmpz;

    final float angleVarianceInDegrees = 10f;
    final float speedVariance = 1f;

    float[] spotLightDirectionInEyeSpace;
    float[] spotPositionsInEyeSpace;
    float[] spotPositionsInEyeSpace2;
    float[] spotPositionsInEyeSpace3;
    float[] spotPositionsInEyeSpace4;

    final float[] vectorToLight = {0f, -1.0f, 0f, 0f};
    final float[] positionToLight = {1.0f, 1.0f, 1.0f, 1.0f};
    final float[] positionToLight2 = {1.0f, 1.0f, -1.0f, 1.0f};
    final float[] positionToLight3 = {-1.0f, 1.0f, -1.0f, 1.0f};
    final float[] positionToLight4= {-1.0f, 1.0f, 1.0f, 1.0f};

  /*
    final float[] vectorToLight = {0.30f, 0.35f, -0.89f, 0f};

    private final float[] pointLightPositions = new float[]
            {-1f, 1f, 0f, 1f,
              0f, 1f, 0f, 1f,
              1f, 1f, 0f, 1f};

    private final float[] pointLightColors = new float[]
            {1.00f, 0.20f, 0.02f,
             0.02f, 0.25f, 0.02f,
             0.02f, 0.20f, 1.00f};
*/

    public TestScreen2(AndroidGame game) {
        super(game);
        modelMatrix = new float[16];
        modelViewMatrix = new float[16];
        modelViewProjectionMatrix = new float[16];
        it_modelViewMatrix = new  float[16];
        tempMatrix = new float[16];

        base = new Base();
        table = new XZRectangle(1.2f, 1.6f, 0);
        puck = new Puck(0.02f, 0.02f, 32);
        cardProjection = new XZRectangle(0.08f, 0.12f, 0);
        invCardProjection = new XZRectangle(0.08f, 0.12f, 2);
        particleSystem = new ParticleSystem(10000);

        GLVector particleDirection = new GLVector(0.0f, -0.5f, 0f);
        redParticleShooter = new ParticleShooter(
                new GLPoint(0.0f, 1.0f, 0.6f),
                particleDirection,
                Color.rgb(255, 50, 5),
                angleVarianceInDegrees,
                speedVariance);

        tmpx = 0;
        tmpz = 0;

        spotLightDirectionInEyeSpace = new float[4];
        spotPositionsInEyeSpace = new float[4];
        spotPositionsInEyeSpace2 = new float[4];
        spotPositionsInEyeSpace3 = new float[4];
        spotPositionsInEyeSpace4 = new float[4];
    }

    @Override
    public void update(float deltaTime, float totalTime) {
        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
        invertedViewProjectionMatrix = ((AndroidOpenGLRenderView)game.getRenderObj()).getInvertedViewProjectionMatrix();
        GLRay ray;
        game.getInput().getKeyEvents();
        int len = touchEvents.size();
        for(int i = 0; i<len; i++) {
            Input.TouchEvent event = touchEvents.get(i);
            if (event.type == Input.TouchEvent.TOUCH_UP) {
                UIHelper.GLNormalized2DPoint Npoint = UIHelper.ConvertTouchEventToNormalized2DPoint(game, event);
                ray = UIHelper.convertNormalized2DPointToRay(invertedViewProjectionMatrix, Npoint.normalizedX, Npoint.normalizedY);
                GLPoint point = GLGeometry.GLRayIntersectionWithXZPlane(ray);
                tmpx = point.x;
                tmpz = point.z;
                break;
            }
        }
    }

    @Override
    public void present(float deltaTime, float totalTime) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //glClear(GL_COLOR_BUFFER_BIT);
        viewMatrix = ((AndroidOpenGLRenderView)game.getRenderObj()).getViewMatrix();
        projectionMatrix = ((AndroidOpenGLRenderView)game.getRenderObj()).getProjectionMatrix();
        // Draw the table.
        positionObjectInXZScene(0f, 0f, 0f);
        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, basetexture);
        base.bindData(textureProgram);
        base.draw();

        /*
        final float[] vectorToLightInEyeSpace = new float[4];
        final float[] pointPositionsInEyeSpace = new float[12];
        multiplyMV(vectorToLightInEyeSpace, 0, viewMatrix, 0, vectorToLight, 0);
        multiplyMV(pointPositionsInEyeSpace, 0, viewMatrix, 0, pointLightPositions, 0);
        multiplyMV(pointPositionsInEyeSpace, 4, viewMatrix, 0, pointLightPositions, 4);
        multiplyMV(pointPositionsInEyeSpace, 8, viewMatrix, 0, pointLightPositions, 8);
*/

        GLMaterial Material = new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f);


        multiplyMV(spotLightDirectionInEyeSpace, 0, viewMatrix, 0, vectorToLight, 0);
        multiplyMV(spotPositionsInEyeSpace, 0, viewMatrix, 0, positionToLight, 0);
        multiplyMV(spotPositionsInEyeSpace2, 0, viewMatrix, 0, positionToLight2, 0);
        multiplyMV(spotPositionsInEyeSpace3, 0, viewMatrix, 0, positionToLight3, 0);
        multiplyMV(spotPositionsInEyeSpace4, 0, viewMatrix, 0, positionToLight4, 0);

        ArrayList<GLLight> Light = new ArrayList<GLLight>();

        //GLLight Exp_light = new GLLight(GLLight.LightType.Spot, spotPositionsInEyeSpace, spotLightDirectionInEyeSpace,
        //        new float[] {0.5f, 0.5f, 0.5f}, 2.0f, 40.0f);
//        GLLight Exp_light2 = new GLLight(GLLight.LightType.Directional, new float[] {0, 0, 0, 0}, spotLightDirectionInEyeSpace,
  //              new float[] {0.7f, 0.7f, 0.7f}, 0, 0);
        GLLight Exp_light3 = new GLLight(GLLight.LightType.Point, spotPositionsInEyeSpace, new float[] {0, 0, 0},
                new float[] {0.2f, 0.2f, 0.2f}, 0, 0);
        GLLight Exp_light4 = new GLLight(GLLight.LightType.Point, spotPositionsInEyeSpace2, new float[] {0, 0, 0},
                new float[] {0.2f, 0.2f, 0.2f}, 0, 0);
        GLLight Exp_light5 = new GLLight(GLLight.LightType.Point, spotPositionsInEyeSpace3, new float[] {0, 0, 0},
                new float[] {0.2f, 0.2f, 0.2f}, 0, 0);
        GLLight Exp_light6 = new GLLight(GLLight.LightType.Point, spotPositionsInEyeSpace4, new float[] {0, 0, 0},
                new float[] {0.2f, 0.2f, 0.2f}, 0, 0);
        //Light.add(Exp_light);
  //      Light.add(Exp_light2);
        Light.add(Exp_light3);
        Light.add(Exp_light4);
        Light.add(Exp_light5);
        Light.add(Exp_light6);
        positionObjectInScenetmp(0f, 0f, 0f);
        textureShaderProgramLight.useProgram();
        textureShaderProgramLight.setUniforms(modelViewMatrix, it_modelViewMatrix,
                modelViewProjectionMatrix, Light.size(), Light, Material, tabletexture);

        table.bindData(textureShaderProgramLight);
        table.draw();

        positionObjectInScenetmp(0f, 0.01f, 0.6f);
        textureShaderProgramLight.setUniforms(modelViewMatrix, it_modelViewMatrix,
                modelViewProjectionMatrix, Light.size(), Light, Material, cardbackside);

        cardProjection.bindData(textureShaderProgramLight);
        cardProjection.draw();

        positionObjectInScenetmp(0f, 0.01f, -0.6f);
        textureShaderProgramLight.setUniforms(modelViewMatrix, it_modelViewMatrix,
                modelViewProjectionMatrix, Light.size(), Light, Material, cardbackside);

        invCardProjection.bindData(textureShaderProgramLight);
        invCardProjection.draw();


        // Draw the puck.
        colorProgram.useProgram();
        positionObjectInScenetmp(tmpx, puck.height / 2f, tmpz);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorProgram);
        puck.draw();

        // particle system

        redParticleShooter.addParticles(particleSystem, totalTime, 100);

        particleProgram.useProgram();
        glDepthMask(false);
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
        positionObjectInScenetmp(0, 0, 0);
        particleProgram.setUniforms(modelViewProjectionMatrix, totalTime);
        particleSystem.bindData(particleProgram);
        particleSystem.draw();
        glDisable(GL_BLEND);
        glDepthMask(true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
        textureProgram = new TextureShaderProgram(game);
        colorProgram = new UniformColorShaderProgram(game);
        particleProgram = new ParticleShaderProgram(game);
        textureShaderProgramLight = new TextureShaderProgramLight(game);
        basetexture = TextureHelper.loadTexture(game, "Base_1.png");
        tabletexture = TextureHelper.loadTexture(game, "duelmaze.png");
        cardbackside = TextureHelper.loadTexture(game, "cardbackside.png");
    }

    @Override
    public void dispose() {

    }

    @Override
    public void back() {

    }

    private void positionObjectInXZScene(float x, float y, float z) {
        // The table is defined in terms of X & Y coordinates, so we rotate it
        // 90 degrees to lie flat on the XZ plane.
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        updateMvpMatrix();
    }

    private void positionObjectInScenetmp(float x, float y, float z) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        updateMvpMatrix();
    }

    private void updateMvpMatrix() {
        multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        invertM(tempMatrix, 0, modelViewMatrix, 0);
        transposeM(it_modelViewMatrix, 0, tempMatrix, 0);
        multiplyMM(
                modelViewProjectionMatrix, 0,
                projectionMatrix, 0,
                modelViewMatrix, 0);
    }
}
