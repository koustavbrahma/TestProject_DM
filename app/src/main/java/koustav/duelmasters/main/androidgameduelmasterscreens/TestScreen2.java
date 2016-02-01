package koustav.duelmasters.main.androidgameduelmasterscreens;

import android.graphics.Color;

import java.util.List;

import static android.opengl.GLES20.*;
import koustav.duelmasters.main.androidgameopenglanimation.ParticleShooter;
import koustav.duelmasters.main.androidgameopenglanimation.ParticleSystem;
import koustav.duelmasters.main.androidgameopenglobjects.Base;
import koustav.duelmasters.main.androidgameopenglobjects.CardProjection;
import koustav.duelmasters.main.androidgameopenglobjects.InvCardProjection;
import koustav.duelmasters.main.androidgameopenglobjects.Puck;
import koustav.duelmasters.main.androidgameopenglobjects.Table;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;
import koustav.duelmasters.main.androidgameopenglutil.TextureHelper;
import koustav.duelmasters.main.androidgameopenglutil.UIHelper;
import koustav.duelmasters.main.androidgamesframework.Input;
import koustav.duelmasters.main.androidgamesframework.Screen;
import koustav.duelmasters.main.androidgamesframeworkimpl.AndroidGame;
import koustav.duelmasters.main.androidgamesframeworkimpl.AndroidOpenGLRenderView;
import koustav.duelmasters.main.androidgameshaderprogram.ParticleShaderProgram;
import koustav.duelmasters.main.androidgameshaderprogram.TextureShaderProgram;
import koustav.duelmasters.main.androidgameshaderprogram.UniformColorShaderProgram;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

/**
 * Created by Koustav on 1/24/2016.
 */
public class TestScreen2 extends Screen {
    private Base base;
    private Table table;
    private Puck puck;
    private CardProjection cardProjection;
    private InvCardProjection invCardProjection;

    private TextureShaderProgram textureProgram;
    private UniformColorShaderProgram colorProgram;
    private ParticleShaderProgram particleProgram;

    private int basetexture;
    private int tabletexture;
    private int cardbackside;

    private float[] modelMatrix;
    private float[] viewProjectionMatrix;
    private float[] modelViewProjectionMatrix;
    private float[] invertedViewProjectionMatrix;

    private ParticleSystem particleSystem;
    private ParticleShooter redParticleShooter;

    float tmpx;
    float tmpz;

    final float angleVarianceInDegrees = 5f;
    final float speedVariance = 1f;

    public TestScreen2(AndroidGame game) {
        super(game);
        modelMatrix = new float[16];
        modelViewProjectionMatrix = new float[16];
        base = new Base();
        table = new Table();
        puck = new Puck(0.02f, 0.02f, 32);
        cardProjection = new CardProjection();
        invCardProjection = new InvCardProjection();
        particleSystem = new ParticleSystem(10000);

        GLVector particleDirection = new GLVector(0f, 0.5f, 0f);
        redParticleShooter = new ParticleShooter(
                new GLPoint(-0.5f, 0f, 0f),
                particleDirection,
                Color.rgb(255, 50, 5),
                angleVarianceInDegrees,
                speedVariance);

        tmpx = 0;
        tmpz = 0;
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
        //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClear(GL_COLOR_BUFFER_BIT);
        viewProjectionMatrix = ((AndroidOpenGLRenderView)game.getRenderObj()).getViewProjectionMatrix();
        // Draw the table.
        positionObjectInXZScene(0f, 0f, 0f);
        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, basetexture);
        base.bindData(textureProgram);
        base.draw();
        textureProgram.setUniforms(modelViewProjectionMatrix, tabletexture);
        table.bindData(textureProgram);
        table.draw();
        positionObjectInXZScene(0f, 0f, 0.6f);
        textureProgram.setUniforms(modelViewProjectionMatrix, cardbackside);
        cardProjection.bindData(textureProgram);
        cardProjection.draw();
        positionObjectInXZScene(0f, -0.5f, -0.6f);
        textureProgram.setUniforms(modelViewProjectionMatrix, cardbackside);
        invCardProjection.bindData(textureProgram);
        invCardProjection.draw();
        // Draw the puck.
        colorProgram.useProgram();
        positionObjectInScenetmp(tmpx, puck.height / 2f, tmpz);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorProgram);
        puck.draw();

        // particle system

        redParticleShooter.addParticles(particleSystem, totalTime, 5);

        particleProgram.useProgram();
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
        positionObjectInScenetmp(0, 0, 0);
        particleProgram.setUniforms(modelViewProjectionMatrix, totalTime);
        particleSystem.bindData(particleProgram);
        particleSystem.draw();
        glDisable(GL_BLEND);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
        textureProgram = new TextureShaderProgram(game);
        colorProgram = new UniformColorShaderProgram(game);
        particleProgram = new ParticleShaderProgram(game);
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
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
                0, modelMatrix, 0);
    }

    private void positionObjectInScenetmp(float x, float y, float z) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
                0, modelMatrix, 0);
    }
}
