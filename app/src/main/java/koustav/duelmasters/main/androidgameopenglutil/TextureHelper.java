package koustav.duelmasters.main.androidgameopenglutil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import static android.opengl.GLES20.*;
import static android.opengl.GLUtils.*;
import static android.opengl.Matrix.*;

/**
 * Created by Koustav on 1/18/2016.
 */
public class TextureHelper {
    private static final String TAG = "TextureHelper";

    public static int loadTexture(Context context, String filename) {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);
        if (textureObjectIds[0] == 0) {
            Log.w(TAG, "Could not generate a new OpenGL texture object.");
            return 0;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        InputStream in = null;
        Bitmap bitmap = null;
        try {
            in = context.getAssets().open(filename);
            bitmap = BitmapFactory.decodeStream(in, null, options);
            if (bitmap == null) {
                Log.w(TAG, "File: " + filename + " could not be decoded.");
                glDeleteTextures(1, textureObjectIds, 0);
                return 0;
            }
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load bitmap from asset '" + filename + "'");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);

        return textureObjectIds[0];
    }
}
