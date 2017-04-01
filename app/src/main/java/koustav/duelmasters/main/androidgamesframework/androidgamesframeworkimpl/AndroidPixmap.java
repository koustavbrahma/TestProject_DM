package koustav.duelmasters.main.androidgamesframework.androidgamesframeworkimpl;

import android.graphics.Bitmap;

import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Graphics.PixmapFormat;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Pixmap;

/**
 * Created by Koustav on 2/11/2015.
 */
public class AndroidPixmap implements Pixmap {
    Bitmap bitmap;
    PixmapFormat format;

    public AndroidPixmap(Bitmap bitmap, PixmapFormat format) {
        this.bitmap = bitmap;
        this.format = format;
    }

    @Override
    public int getWidth() {
        return bitmap.getWidth();
    }

    @Override
    public int getHeight() {
        return bitmap.getHeight();
    }

    @Override
    public PixmapFormat getFormat(){
        return format;
    }

    @Override
    public void dispose() {
        bitmap.recycle();
    }

}
