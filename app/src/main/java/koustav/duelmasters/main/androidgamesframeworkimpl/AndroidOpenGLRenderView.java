package koustav.duelmasters.main.androidgamesframeworkimpl;


import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.widget.Toast;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import koustav.duelmasters.R;
import koustav.duelmasters.main.androidgamesframework.RenderView;
import koustav.duelmasters.main.androidgamesframework.ShaderHelper;

import static android.opengl.GLES20.*;

/**
 * Created by Koustav on 1/3/2016.
 */
public class AndroidOpenGLRenderView extends GLSurfaceView implements GLSurfaceView.Renderer, RenderView {
    AndroidGame game;
    boolean supportsEs2;
    enum GLGameState {
        Running,
        Pasued,
        Finished,
        Idle
    }
    GLGameState state;
    Object stateChanged = new Object();
    long startTime = System.nanoTime();
    private int program;
    private static final String U_COLOR = "u_Color";
    private int uColorLocation;
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    public AndroidOpenGLRenderView(AndroidGame game) {
        super(game);
        this.game = game;
        ActivityManager activityManager =
                (ActivityManager) game.getSystemService(game.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();
        supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")));

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            this.setEGLContextClientVersion(2);
            // Assign our renderer.
            this.setRenderer(this);
           // rendererSet = true;
        } else {
            Toast.makeText(game, "This device does not support OpenGL ES 2.0.", Toast.LENGTH_LONG).show();
            return;
        }

    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
        String vertexShaderSource = game.getFileIO().readTextFileFromResource(R.raw.vertex_shader);
        String fragmentShaderSource = game.getFileIO().readTextFileFromResource(R.raw.fragment_shader);
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
        ShaderHelper.validateProgram(program);
        glUseProgram(program);
        uColorLocation = glGetUniformLocation(program, U_COLOR);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        synchronized (stateChanged) {
            game.getCurrentScreen().resume();
            this.state = GLGameState.Running;
        }
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        GLGameState glstate = null;

        synchronized (stateChanged) {
            glstate = this.state;
        }

        if (glstate == GLGameState.Running) {
            float deltaTime = (System.nanoTime() - startTime) / 1000000000.0f;
            startTime = System.nanoTime();

            game.getCurrentScreen().update(deltaTime);
            game.getCurrentScreen().present(deltaTime);
        }

        if (glstate == GLGameState.Pasued) {
            game.getCurrentScreen().pause();
            synchronized (stateChanged) {
                this.state = GLGameState.Idle;
                stateChanged.notifyAll();
            }
        }

        if (state == GLGameState.Finished) {
            game.getCurrentScreen().pause();
            game.getCurrentScreen().dispose();
            synchronized (stateChanged) {
                this.state = GLGameState.Idle;
                stateChanged.notifyAll();
            }
        }
        //glClear(GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void resume() {
        this.onResume();
    }

    @Override
    public void pause() {
        if (state == GLGameState.Running) {
            synchronized (stateChanged) {
                if (game.isFinishing())
                    state = GLGameState.Finished;
                else
                    state = GLGameState.Pasued;
                while (true) {
                    try {
                        stateChanged.wait();
                        break;
                    } catch (InterruptedException e) {

                    }
                }
            }
        }
        this.onPause();
    }

    public int getuColorLocation() {
        return uColorLocation;
    }

    public int getaPositionLocation() {
        return aPositionLocation;
    }
}
