package koustav.duelmasters.main.androidgamesframeworkimpl;


import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.widget.Toast;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import koustav.duelmasters.R;
import koustav.duelmasters.main.androidgameopenglutil.MatrixHelper;
import koustav.duelmasters.main.androidgamesframework.RenderView;
import koustav.duelmasters.main.androidgameopenglutil.ShaderHelper;
import static android.opengl.GLES20.*;
import static android.opengl.GLUtils.*;
import static android.opengl.Matrix.*;

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
    private float[] viewMatrix;
    private float[] projectionMatrix;

    public AndroidOpenGLRenderView(AndroidGame game) {
        super(game);
        this.game = game;
        viewMatrix = new float[16];
        projectionMatrix = new float[16];
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
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        synchronized (stateChanged) {
            game.getCurrentScreen().resume();
            this.state = GLGameState.Running;
        }
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 10f);
        setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);
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

    public float [] getProjectionMatrix() {
        return projectionMatrix;
    }

    public float [] getViewMatrix() {
        return viewMatrix;
    }
}
