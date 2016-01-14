package koustav.duelmasters.main.androidgamesframework;

/**
 * Created by Koustav on 2/10/2015.
 * Abstract: Graphics interface.
 */
public interface Graphics {
    public static enum PixmapFormat {
        ARGB8888, ARGB4444, RGB565;
    }

    public Pixmap newPixmap(String filename, PixmapFormat format);
    public void clear(int color);
    public void drawPixel(int x, int y, int color);
    public void drawLine(int x, int y, int x2, int y2, int color);
    public void drawRect(int x, int y, int width, int height, int color);
    public void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight);
    public void drawPixmap(Pixmap pixmap, int x, int y);
    public void drawPixmap(Pixmap pixmap, int x, int y, float angle);
    public void drawText(String text,int x,int y,int size, int color);
    public int getWidth();
    public int getHeight();
    public int getuColorLocation();
    public int getaPositionLocation();

}
