package koustav.duelmasters.main.androidgamesframeworkimpl;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import koustav.duelmasters.main.androidgamesframework.RenderView;

/**
 * Created by Koustav on 2/12/2015.
 */
public class AndroidFastRenderView extends SurfaceView implements Runnable, RenderView {
    AndroidGame game;
    Bitmap framebuffer;
    Thread renderThread = null;
    SurfaceHolder holder;
    volatile boolean running = false;

    public AndroidFastRenderView(AndroidGame game, Bitmap framebuffer) {
        super(game);
        this.game = game;
        this.framebuffer = framebuffer;
        this.holder = getHolder();
    }

    @Override
    public void resume() {
        running = true;
        renderThread = new Thread(this);
        renderThread.start();
        game.getCurrentScreen().resume();
    }

    public void run() {
        Rect dstRect = new Rect();
        long startTime = System.nanoTime();
        while (running) {
            if(!holder.getSurface().isValid())
                continue;
            float deltaTime = (System.nanoTime() - startTime) / 1000000000.0f;
            startTime = System.nanoTime();

            game.getCurrentScreen().update(deltaTime, 0);
            game.getCurrentScreen().present(deltaTime, 0);

            Canvas canvas = holder.lockCanvas();
            canvas.getClipBounds(dstRect);
            canvas.drawBitmap(framebuffer, null, dstRect, null);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void pause() {
        running = false;
        game.getCurrentScreen().pause();
        while (true) {
            try {
                renderThread.join();
                 break;
            } catch (InterruptedException e) {

            }
        }
    }
}
