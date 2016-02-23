package koustav.duelmasters.main.androidgameopenglutil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static android.opengl.GLES20.*;

/**
 * Created by Koustav on 2/20/2016.
 */
public class FrameBufferObject {
    private int[] fboHandle;
    private int[] renderTex;
    private int[] depthBuf;

    public FrameBufferObject(int width, int height) {
        fboHandle = new int[1];
        renderTex = new int[1];
        depthBuf = new int[1];
        // The handle to the FBO
        // Generate and bind the framebuffer
        glGenFramebuffers(1, fboHandle, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, fboHandle[0]);
        // Create the texture object
        glGenTextures(1, renderTex, 0);
        glBindTexture(GL_TEXTURE_2D, renderTex[0]);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,GL_RGBA, GL_UNSIGNED_BYTE, null);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, renderTex[0], 0);

        glGenRenderbuffers(1, depthBuf, 0);
        glBindRenderbuffer(GL_RENDERBUFFER, depthBuf[0]);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, width, height);
        // Bind the depth buffer to the FBO
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuf[0]);

        // check status
        int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (status != GL_FRAMEBUFFER_COMPLETE)
            throw new IllegalArgumentException("FBO failure"+ status);

        // Unbind the framebuffer, and revert to default framebuffer
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getfboHandle() {
        return fboHandle[0];
    }

    public int getrenderTex() {
        return renderTex[0];
    }
}
