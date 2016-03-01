package koustav.duelmasters.main.androidgamesframeworkimpl;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;
import java.net.Socket;

import koustav.duelmasters.main.androidgamesframework.Audio;
import koustav.duelmasters.main.androidgamesframework.FileIO;
import koustav.duelmasters.main.androidgamesframework.Game;
import koustav.duelmasters.main.androidgamesframework.Graphics;
import koustav.duelmasters.main.androidgamesframework.Input;
import koustav.duelmasters.main.androidgamesframework.RenderView;
import koustav.duelmasters.main.androidgamesframework.Screen;

/**
 * Created by Koustav on 2/12/2015.
 */
public abstract class AndroidGame extends Activity implements Game {
    AndroidFastRenderView renderView;
    AndroidOpenGLRenderView GLrenderView;
    Graphics graphics;
    Audio audio;
    Input input;
    FileIO fileIO;
    Screen screen;
    AndroidNetwork network;
    int frameBufferWidth;
    int frameBufferHeight;
    WakeLock wakeLock;
    boolean TURN;
    boolean UseGLRenderView;
    int currentApiVersion;
    boolean GLFragColor;

    @Override
    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        if(currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(uiOptions);
        }

        frameBufferWidth = 320;
        frameBufferHeight = 480;
        TURN = true;
        UseGLRenderView = true;
        GLFragColor = true;
        Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth,
                frameBufferHeight, Bitmap.Config.RGB_565);

        float scaleX = UseGLRenderView? 1 : (float)frameBufferWidth/getWindowManager().getDefaultDisplay().getWidth();
        float scaleY = UseGLRenderView? 1 : (float)frameBufferHeight/getWindowManager().getDefaultDisplay().getHeight();

        renderView = new AndroidFastRenderView(this, frameBuffer);
        GLrenderView = new AndroidOpenGLRenderView(this);
        graphics = new AndroidGraphics(getAssets(), frameBuffer);
        fileIO = new AndroidFileIO(this);
        //add audio
        input = new AndroidInput(this, getViewObj(), scaleX, scaleY);
        screen = getStartScreen();
        network = new AndroidNetwork(this);
        setContentView(getViewObj());

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        PowerManager powerManager = (PowerManager)
                getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "GLGame");

    }

    @Override
    public void onResume() {
        super.onResume();
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        wakeLock.acquire();
        getRenderObj().resume();
    }

    @Override
    public void onPause() {
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        wakeLock.release();
        getRenderObj().pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (network.getSocket() != null) {
            try {
                network.getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (network.getServerSocket() != null) {
            try {
                network.getServerSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        getCurrentScreen().back();
    }

    @Override
    public Input getInput() {
        return input;
    }

    @Override
    public FileIO getFileIO(){
        return fileIO;
    }

    @Override
    public Graphics getGraphics() {
        return graphics;
    }

    @Override
    public Audio getAudio() {
        return audio;
    }

    @Override
    public void setScreen(Screen screen) {
        if (screen == null)
            throw new IllegalArgumentException("Screen must not be null");
        this.screen.pause();
        this.screen.dispose();
        screen.resume();
        screen.update(0, 0);
        this.screen = screen;
    }

    @Override
    public Screen getCurrentScreen() {
        return screen;
    }

    @Override
    public AndroidNetwork getNetwork() {
        return network;
    }


    @Override
    public int getframeBufferWidth() {
        return UseGLRenderView? GLrenderView.get_width() : frameBufferWidth;
    }

    @Override
    public int getframeBufferHeight(){
        return UseGLRenderView? GLrenderView.get_height() : frameBufferHeight;
    }

    @Override
    public void setTurn(boolean val){
        this.TURN = val;
    }

    @Override
    public boolean getTurn() {
        return TURN;
    }

    public RenderView getRenderObj() {
        return UseGLRenderView ? GLrenderView: renderView;
    }

    public View getViewObj() {return UseGLRenderView ? GLrenderView: renderView;}

    public void setGLFragColoring(boolean val) {
        GLFragColor = val;
    }

    public int getGLFragColoring() {
        return GLFragColor? 0 : 1;
    }
}
