package koustav.duelmasters.main.androidgameopenglutil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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

    public static int loadCubeMap(Context context, ArrayList<String> filenames) {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);
        if (textureObjectIds[0] == 0) {
            Log.w(TAG, "Could not generate a new OpenGL texture object.");
            return 0;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        InputStream in = null;
        Bitmap[] bitmap = new Bitmap[6];
        for (int i = 0; i<6;i++) {
            try {
                in = null;
                in = context.getAssets().open(filenames.get(i));
                bitmap[i] = BitmapFactory.decodeStream(in, null, options);
                if (bitmap[i] == null) {
                    Log.w(TAG, "File: " + filenames.get(i) + " could not be decoded.");
                    glDeleteTextures(1, textureObjectIds, 0);
                    return 0;
                }
            } catch (IOException e) {
                throw new RuntimeException("Couldn't load bitmap from asset '" + filenames.get(i) + "'");
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureObjectIds[0]);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, bitmap[0], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, bitmap[1], 0);

        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, bitmap[2], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, bitmap[3], 0);

        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, bitmap[4], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, bitmap[5], 0);

        glBindTexture(GL_TEXTURE_2D, 0);
        for (Bitmap bitmaprm : bitmap) {
            bitmaprm.recycle();
        }
        return textureObjectIds[0];
    }

    public static void freeTexture(int textureObjectId) {
        int[] textureObjectIds = new int[1];
        textureObjectIds[0] = textureObjectId;
        glDeleteTextures(1, textureObjectIds, 0);
    }
}
