package koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface;

import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Graphics.PixmapFormat;

/**
 * Created by Koustav on 2/10/2015.
 * Abstract: The Pixmap interface.
 */
public interface Pixmap {
    public int getWidth();
    public int getHeight();
    public PixmapFormat getFormat();
    public void dispose();
}
