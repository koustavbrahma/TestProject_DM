package koustav.duelmasters.main.androidgameduelmasterscreens;

import android.graphics.Color;

import java.util.ArrayList;

import static android.opengl.GLES20.*;
import koustav.duelmasters.main.androidgameopenglanimation.ParticleShooter;
import koustav.duelmasters.main.androidgameopenglanimation.ParticleSystem;
import koustav.duelmasters.main.androidgameopenglmotionmodel.GLDynamics;
import koustav.duelmasters.main.androidgameopenglobjectmodels.XYRectangle;
import koustav.duelmasters.main.androidgameopenglobjectmodels.Puck;
import koustav.duelmasters.main.androidgameopenglobjectmodels.Sphere;
import koustav.duelmasters.main.androidgameopenglobjectmodels.XZRectangle;
import koustav.duelmasters.main.androidgameopenglutil.FrameBufferObject;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;
import koustav.duelmasters.main.androidgameopenglutil.GLLight;
import koustav.duelmasters.main.androidgameopenglutil.GLMaterial;
import koustav.duelmasters.main.androidgameopenglutil.MathHelper;
import koustav.duelmasters.main.androidgameopenglutil.MatrixHelper;
import koustav.duelmasters.main.androidgameopenglutil.TextureHelper;
import koustav.duelmasters.main.androidgameopenglutil.UIHelper;
import koustav.duelmasters.main.androidgamesframework.Screen;
import koustav.duelmasters.main.androidgamesframeworkimpl.AndroidGame;
import koustav.duelmasters.main.androidgameshaderprogram.BloomingShaderProgram;
import koustav.duelmasters.main.androidgameshaderprogram.GaussianBlurShaderProgram;
import koustav.duelmasters.main.androidgameshaderprogram.ParticleShaderProgram;
import koustav.duelmasters.main.androidgameshaderprogram.TextureShaderProgram;
import koustav.duelmasters.main.androidgameshaderprogram.TextureShaderProgramLight;
import koustav.duelmasters.main.androidgameshaderprogram.UniformColorShaderLightProgram;

import static android.opengl.Matrix.*;
import static android.opengl.Matrix.multiplyMM;

/**
 * Created by Koustav on 1/24/2016.
 */
public class TestScreen2 extends Screen {
    private XYRectangle xy_rectangle;
    private XZRectangle table;
    private Puck puck;
    private Sphere sphere;
    private XZRectangle cardProjection;
    private XZRectangle invCardProjection;

    private TextureShaderProgram textureProgram;
    private UniformColorShaderLightProgram colorProgram;
    private ParticleShaderProgram particleProgram;
    private TextureShaderProgramLight textureShaderProgramLight;
    private GaussianBlurShaderProgram gaussianBlurShaderProgram;
    private BloomingShaderProgram bloomingShaderProgram;

    private int basetexture;
    private int tabletexture;
    private int cardbackside;

    private FrameBufferObject FBO;
    private FrameBufferObject FBO2;
    private FrameBufferObject FBO3;
    private FrameBufferObject SFBO;

    private float[] viewMatrix;
    private float[] projectionMatrix;
    private float[] viewProjectionMatrix;
    private float[] invertedViewProjectionMatrix;

    private float[] modelMatrix;
    private float[] modelViewMatrix;
    private float[] it_modelViewMatrix;
    private float[] modelViewProjectionMatrix;
    private float[] tempMatrix;

    private float[] depthVPMatrix;
    private float[] depthMVPMatrix;
    private float[] depthbiasVPMatrix;
    private float[] shadowMatrix;

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

    final float[] weights;

    GLDynamics motion;

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
        viewMatrix = new float[16];
        projectionMatrix = new float[16];
        viewProjectionMatrix = new float[16];
        invertedViewProjectionMatrix = new float[16];

        modelMatrix = new float[16];
        modelViewMatrix = new float[16];
        modelViewProjectionMatrix = new float[16];
        it_modelViewMatrix = new  float[16];
        tempMatrix = new float[16];
        depthVPMatrix = new float[16];
        depthMVPMatrix = new float[16];
        depthbiasVPMatrix = new float[16];
        shadowMatrix = new float[16];

        xy_rectangle = new XYRectangle();
        table = new XZRectangle(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), 1.2f, 1.6f, 0);
        puck = new Puck(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), 0.02f, 0.02f, 32);
        sphere = new Sphere(new GLMaterial(new float[] {0.5f, 0.5f, 0.5f}, new float[] {0.5f, 0.5f, 0.5f},
                new float[] {0.8f, 0.8f, 0.8f}, 3.0f), 0.05f, 32);
        cardProjection = new XZRectangle(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), 0.08f, 0.12f, 0);
        invCardProjection = new XZRectangle(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), 0.08f, 0.12f, 2);
        particleSystem = new ParticleSystem(10000);

        GLVector particleDirection = new GLVector(0.0f, -0.5f, 0f);
        redParticleShooter = new ParticleShooter(
                new GLPoint(0.0f, 1.0f, 0.0f),
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
        weights = new float[10];
        generateWeights();

        motion = new GLDynamics();
        motion.setCentrePosition(0f, 0.01f, 0.6f);
        motion.setVelocity(0f, 0.5f, -0.5f);
        motion.setAcceleration(0f, -1.0f, 0f);
        motion.setAngularVelocity(0f, 1.0f, 0f);
    }

    private void generateWeights() {
        float sum = 0;
        float sigma2 = 4.0f;
        weights[0] = MathHelper.GaussFun1D(0, sigma2);
        sum = weights[0];
        for (int i = 1; i  < weights.length; i++) {
            weights[i] = MathHelper.GaussFun1D(i, sigma2);
            sum += 2 * weights[i];
        }

        for (int i = 0; i < weights.length; i++) {
            weights[i] = weights[i]/sum;
        }
    }

    @Override
    public void update(float deltaTime, float totalTime) {
        GLRay ray;
        /*
        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        int len = touchEvents.size();
        for(int i = 0; i<len; i++) {
            Input.TouchEvent event = touchEvents.get(i);
            if (event.type == Input.TouchEvent.TOUCH_UP) {
                UIHelper.GLNormalized2DPoint Npoint = UIHelper.ConvertTouchEventToNormalized2DPoint(game, event.x, event.y);
                ray = UIHelper.convertNormalized2DPointToRay(invertedViewProjectionMatrix, Npoint.normalizedX, Npoint.normalizedY);
                GLPoint point = GLGeometry.GLRayIntersectionWithXZPlane(ray);
                tmpx = point.x;
                tmpz = point.z;
                break;
            }
        } */
        if (game.getInput().isTouchDown(0)) {
            UIHelper.GLNormalized2DPoint Npoint = UIHelper.ConvertTouchEventToNormalized2DPoint(game, game.getInput().getTouchX(0),
                    game.getInput().getTouchY(0));
            ray = UIHelper.convertNormalized2DPointToRay(invertedViewProjectionMatrix, Npoint.normalizedX, Npoint.normalizedY);
            GLPoint point = GLGeometry.GLRayIntersectionWithXZPlane(ray);
            tmpx = point.x;
            tmpz = point.z;
        }
    }

    @Override
    public void present(float deltaTime, float totalTime) {
        // Clear the rendering surface.
        //glViewport(0, 0, 512, 512);
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
        motion.update(deltaTime);
        GLPoint position = motion.getPosition();
        if (position.y < 0) {
            motion.setCentrePosition(0f, 0.01f, 0.6f);
            motion.setVelocity(0f, 0.5f, -0.5f);
        }

        float angle = motion.getAngleOfRotation();
        GLVector axis= motion.getAxisOfRotation();

        game.setGLFragColoring(false);
        glBindFramebuffer(GL_FRAMEBUFFER, SFBO.getfboHandle());
        glViewport(0, 0, SFBO.getWidth(), SFBO.getHeight());
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //glClear(GL_COLOR_BUFFER_BIT);
        // Draw the table.
        positionObjectInXZScene(0f, 0f, 0f);
        textureProgram.useProgram();
        textureProgram.setUniforms(depthMVPMatrix, basetexture);
        xy_rectangle.bindData(textureProgram.getPositionAttributeLocation(), textureProgram.getTextureCoordinatesAttributeLocation());
        xy_rectangle.draw();


        //final float[] vectorToLightInEyeSpace = new float[4];
        //final float[] pointPositionsInEyeSpace = new float[12];
        //multiplyMV(vectorToLightInEyeSpace, 0, viewMatrix, 0, vectorToLight, 0);
        //multiplyMV(pointPositionsInEyeSpace, 0, viewMatrix, 0, pointLightPositions, 0);
        //multiplyMV(pointPositionsInEyeSpace, 4, viewMatrix, 0, pointLightPositions, 4);
        //multiplyMV(pointPositionsInEyeSpace, 8, viewMatrix, 0, pointLightPositions, 8);




        positionObjectInScenetmp(0f, 0f, 0f);
        textureShaderProgramLight.useProgram();
        textureShaderProgramLight.setUniforms(modelViewMatrix, it_modelViewMatrix,
                depthMVPMatrix, null, Light, table.getMaterial(), tabletexture, 0, false);

        table.bindData(textureShaderProgramLight.getPositionAttributeLocation(),
                textureShaderProgramLight.getNormalAttributeLocation(),
                textureShaderProgramLight.getTextureCoordinatesAttributeLocation());
        table.draw();

        //positionObjectInScenetmp2(0f, 0.01f, 0.6f, totalTime);
        positionObjectInScenetmp3(position, angle, axis);
        textureShaderProgramLight.setUniforms(modelViewMatrix, it_modelViewMatrix,
                depthMVPMatrix, null, Light, cardProjection.getMaterial(), cardbackside, 0 , false);

        cardProjection.bindData(textureShaderProgramLight.getPositionAttributeLocation(),
                textureShaderProgramLight.getNormalAttributeLocation(),
                textureShaderProgramLight.getTextureCoordinatesAttributeLocation());
        cardProjection.draw();

        positionObjectInScenetmp(0f, 0.01f, -0.6f);
        textureShaderProgramLight.setUniforms(modelViewMatrix, it_modelViewMatrix,
                depthMVPMatrix, null, Light, invCardProjection.getMaterial(), cardbackside, 0, false);

        invCardProjection.bindData(textureShaderProgramLight.getPositionAttributeLocation(),
                textureShaderProgramLight.getNormalAttributeLocation(),
                textureShaderProgramLight.getTextureCoordinatesAttributeLocation());
        invCardProjection.draw();


        // Draw the puck.
        colorProgram.useProgram();
        positionObjectInScenetmp(tmpx, puck.height / 2f, tmpz);
        colorProgram.setUniforms(modelViewMatrix, it_modelViewMatrix,
                depthMVPMatrix, null, Light, puck.getMaterial(), new float[] {0.8f, 0.8f, 1f, 1f}, 0, false);
        puck.bindData(colorProgram.getPositionAttributeLocation(), colorProgram.getNormalAttributeLocation());
        puck.draw();

        colorProgram.useProgram();
        //positionObjectInScenetmp2(0.5f, 0.1f, 0.1f, totalTime);
        positionObjectInScenetmp(0.5f, 0.1f, 0.1f);
        colorProgram.setUniforms(modelViewMatrix, it_modelViewMatrix,
                depthMVPMatrix, null, Light, sphere.getMaterial(), new float[] {0.8f, 0.8f, 1f, 1f}, 0 , false);
        sphere.bindData(colorProgram.getPositionAttributeLocation(), colorProgram.getNormalAttributeLocation());
        sphere.draw();


        game.setGLFragColoring(true);
        glBindFramebuffer(GL_FRAMEBUFFER, FBO.getfboHandle());
        glViewport(0, 0, FBO.getWidth(), FBO.getHeight());
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        // Draw the table.
        positionObjectInXZScene(0f, 0f, 0f);
        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, basetexture);
        xy_rectangle.bindData(textureProgram.getPositionAttributeLocation(), textureProgram.getTextureCoordinatesAttributeLocation());
        xy_rectangle.draw();


        positionObjectInScenetmp(0f, 0f, 0f);
        textureShaderProgramLight.useProgram();
        textureShaderProgramLight.setUniforms(modelViewMatrix, it_modelViewMatrix,
                modelViewProjectionMatrix, shadowMatrix, Light, table.getMaterial(), tabletexture, SFBO.getrenderTex(), true);

        table.bindData(textureShaderProgramLight.getPositionAttributeLocation(),
                textureShaderProgramLight.getNormalAttributeLocation(),
                textureShaderProgramLight.getTextureCoordinatesAttributeLocation());
        table.draw();

        //positionObjectInScenetmp2(0f, 0.01f, 0.6f, totalTime);
        positionObjectInScenetmp3(position, angle, axis);
        textureShaderProgramLight.setUniforms(modelViewMatrix, it_modelViewMatrix,
                modelViewProjectionMatrix, shadowMatrix, Light, cardProjection.getMaterial(), cardbackside, SFBO.getrenderTex() , true);

        cardProjection.bindData(textureShaderProgramLight.getPositionAttributeLocation(),
                textureShaderProgramLight.getNormalAttributeLocation(),
                textureShaderProgramLight.getTextureCoordinatesAttributeLocation());
        cardProjection.draw();

        positionObjectInScenetmp(0f, 0.01f, -0.6f);
        textureShaderProgramLight.setUniforms(modelViewMatrix, it_modelViewMatrix,
                modelViewProjectionMatrix, shadowMatrix, Light, invCardProjection.getMaterial(), cardbackside, SFBO.getrenderTex(), true);

        invCardProjection.bindData(textureShaderProgramLight.getPositionAttributeLocation(),
                textureShaderProgramLight.getNormalAttributeLocation(),
                textureShaderProgramLight.getTextureCoordinatesAttributeLocation());
        invCardProjection.draw();


        // Draw the puck.
        colorProgram.useProgram();
        positionObjectInScenetmp(tmpx, puck.height / 2f, tmpz);
        colorProgram.setUniforms(modelViewMatrix, it_modelViewMatrix,
                modelViewProjectionMatrix, shadowMatrix, Light, puck.getMaterial(), new float[] {0.8f, 0.8f, 1f, 1f}, SFBO.getrenderTex(), true);
        puck.bindData(colorProgram.getPositionAttributeLocation(), colorProgram.getNormalAttributeLocation());
        puck.draw();

        colorProgram.useProgram();
        //positionObjectInScenetmp2(0.5f, 0.1f, 0.1f, totalTime);
        positionObjectInScenetmp(0.5f, 0.1f, 0.1f);
        colorProgram.setUniforms(modelViewMatrix, it_modelViewMatrix,
                modelViewProjectionMatrix, shadowMatrix, Light, sphere.getMaterial(), new float[] {0.8f, 0.8f, 1f, 1f}, SFBO.getrenderTex() , true);
        sphere.bindData(colorProgram.getPositionAttributeLocation(), colorProgram.getNormalAttributeLocation());
        sphere.draw();


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

        glViewport(0, 0, FBO2.getWidth(), FBO2.getHeight());
        glBindFramebuffer(GL_FRAMEBUFFER, FBO2.getfboHandle());
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        textureShaderProgramLight.useProgram();
        positionObjectInScenetmp2(0f, 0.01f, 0.6f, totalTime);
        textureShaderProgramLight.setUniforms(modelViewMatrix, it_modelViewMatrix,
                modelViewProjectionMatrix, null, Light, cardProjection.getMaterial(), cardbackside, 0 , false);

        cardProjection.bindData(textureShaderProgramLight.getPositionAttributeLocation(),
                textureShaderProgramLight.getNormalAttributeLocation(),
                textureShaderProgramLight.getTextureCoordinatesAttributeLocation());
        cardProjection.draw();

        glBindFramebuffer(GL_FRAMEBUFFER, FBO3.getfboHandle());
        glDisable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT);
        bloomingShaderProgram.useProgram();
        bloomingShaderProgram.setUniforms(FBO2.getrenderTex(), FBO.getrenderTex(), 512, 512,
                weights, totalTime);
        bloomingShaderProgram.setPass(1);
        xy_rectangle.bindData(bloomingShaderProgram.getPositionAttributeLocation(),
                bloomingShaderProgram.getTextureCoordinatesAttributeLocation());
        xy_rectangle.draw();

        glViewport(0, 0, game.getframeBufferWidth(), game.getframeBufferHeight());
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glClear(GL_COLOR_BUFFER_BIT);
        bloomingShaderProgram.setUniforms(FBO3.getrenderTex(), FBO.getrenderTex(), 512, 512,
                weights, totalTime);
        bloomingShaderProgram.setPass(2);
        xy_rectangle.bindData(bloomingShaderProgram.getPositionAttributeLocation(),
                bloomingShaderProgram.getTextureCoordinatesAttributeLocation());
        xy_rectangle.draw();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
        CreateV_P_IVPMatrix();
        CreateDepthVPMatrix();
        textureProgram = new TextureShaderProgram(game);
        colorProgram = new UniformColorShaderLightProgram(game);
        particleProgram = new ParticleShaderProgram(game);
        textureShaderProgramLight = new TextureShaderProgramLight(game);
        gaussianBlurShaderProgram = new GaussianBlurShaderProgram(game);
        bloomingShaderProgram = new BloomingShaderProgram(game);

        basetexture = TextureHelper.loadTexture(game, "Base_1.png");
        tabletexture = TextureHelper.loadTexture(game, "duelmaze.png");
        cardbackside = TextureHelper.loadTexture(game, "cardbackside.png");

         // The handle to the FBO
         // Generate and bind the framebuffer
        //FBO = new FrameBufferObject(512, 512);
        FBO = new FrameBufferObject(game.getframeBufferWidth(), game.getframeBufferHeight());
        FBO2 = new FrameBufferObject(512, 512);
        FBO3 = new FrameBufferObject(512, 512);
        SFBO = new FrameBufferObject(game.getframeBufferWidth(), game.getframeBufferHeight());
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
        updateShadowMatrix();
    }

    private void positionObjectInScenetmp(float x, float y, float z) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        updateMvpMatrix();
        updateShadowMatrix();
    }

    private void positionObjectInScenetmp2(float x, float y, float z, float time) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        float d = (float) Math.toDegrees(Math.asin(Math.sin(time)));
        rotateM(modelMatrix, 0, d, 0f, 1f, 0f);
        updateMvpMatrix();
        updateShadowMatrix();
    }

    private void positionObjectInScenetmp3(GLPoint position, float angle, GLVector axis) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, position.x, position.y, position.z);
        rotateM(modelMatrix, 0, angle, axis.x, axis.y, axis.z);
        updateMvpMatrix();
        updateShadowMatrix();
    }

    private void CreateV_P_IVPMatrix(){
        CreatePMatrix(game.getframeBufferWidth(), game.getframeBufferHeight());
        //setLookAtM(viewMatrix, 0, 0f, 2.5f, 0.0f, 0f, 0f, 0.0f, 0f, 0f, -1.0f);
        setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.4f, 0f, 0f, 0.2f, 0f, 1f, 0f);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);
    }

    private void CreatePMatrix(int width, int height) {
        MatrixHelper.perspectiveM(projectionMatrix, 18, (float) width / (float) height, 1f, 10f);
        //MatrixHelper.perspectiveM(projectionMatrix, 26, (float) width / (float) height, 1f, 10f);
    }

    private void CreateDepthVPMatrix() {
        float[] viewMatrixtmp = new float[16];
        float[] projectionMatrixtmp = new float[16];
        setLookAtM(viewMatrixtmp, 0, 0f, 2.5f, 0.0f, 0f, 0f, 0.0f, 0f, 0f, -1.0f);
        MatrixHelper.perspectiveM(projectionMatrixtmp, 26, (float) game.getframeBufferWidth() / (float) game.getframeBufferHeight(), 1f, 10f);
        multiplyMM(depthVPMatrix, 0 , projectionMatrixtmp,0, viewMatrixtmp, 0);
        /*float[] bias = {
                0.5f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.5f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f}; */
        float[] bias = new float[16];
        setIdentityM(bias, 0);
        multiplyMM(depthbiasVPMatrix, 0, bias, 0, depthVPMatrix, 0);
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

    private void updateShadowMatrix() {
        multiplyMM(shadowMatrix, 0, depthbiasVPMatrix, 0, modelMatrix, 0);
        multiplyMM(depthMVPMatrix, 0, depthVPMatrix, 0 , modelMatrix, 0);
    }
}
