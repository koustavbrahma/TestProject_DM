package koustav.duelmasters.main.androidgamesframeworkimpl;

import android.opengl.GLSurfaceView;

import koustav.duelmasters.main.androidgamesframework.Graphics;
import koustav.duelmasters.main.androidgamesframework.Pixmap;

/**
 * Created by Koustav on 1/4/2016.
 */
public class AndroidGLGraphics implements Graphics{
    GLSurfaceView glview;

    AndroidGLGraphics(GLSurfaceView view) {
        this.glview = view;
    }

    @Override
    public Pixmap newPixmap(String filename, PixmapFormat format) {
        return null;
    }

    @Override
    public void clear(int color) {

    }

    @Override
    public void drawPixel(int x, int y, int color){

    }

    @Override
    public void drawLine(int x, int y, int x2, int y2, int color) {

    }

    @Override
    public void drawRect(int x, int y, int width, int height, int color) {

    }

    @Override
    public void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight) {

    }

    @Override
    public void drawPixmap(Pixmap pixmap, int x, int y) {

    }

    @Override
    public void drawPixmap(Pixmap pixmap, int x, int y, float angle) {

    }

    @Override
    public void drawText(String text,int x,int y,int size, int color) {

    }

    public int getWidth() {
        return glview.getWidth();
    }

    public int getHeight() {
        return glview.getHeight();
    }

    public int getuColorLocation() {
        return ((AndroidOpenGLRenderView) glview).getuColorLocation();
    }

    public int getaPositionLocation() {
        return ((AndroidOpenGLRenderView) glview).getaPositionLocation();
    }
}
