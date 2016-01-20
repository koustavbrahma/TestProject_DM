package koustav.duelmasters.main.androidgameduelmasters;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import koustav.duelmasters.R;
import koustav.duelmasters.main.androidgameopenglobjects.Points;
import koustav.duelmasters.main.androidgameopenglobjects.Table;
import koustav.duelmasters.main.androidgameopenglutil.MatrixHelper;
import koustav.duelmasters.main.androidgameopenglutil.TextureHelper;
import koustav.duelmasters.main.androidgamesframework.Screen;
import koustav.duelmasters.main.androidgamesframeworkimpl.AndroidGame;
import koustav.duelmasters.main.androidgamesframeworkimpl.AndroidOpenGLRenderView;
import koustav.duelmasters.main.androidgameshaderprogram.ColorShaderProgram;
import koustav.duelmasters.main.androidgameshaderprogram.TextureShaderProgram;

import static android.opengl.GLES20.*;
import static android.opengl.GLUtils.*;
import static android.opengl.Matrix.*;

/**
 * Created by Koustav on 1/13/2016.
 */
public class GlTestScreen extends Screen {
    private Table table;
    private Points mypoint;
    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;
    private int texture;

    public GlTestScreen(AndroidGame game) {
        super(game);
        table = new Table();
        mypoint = new Points();
    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public void present(float deltaTime) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);
        // Draw the table.
        textureProgram.useProgram();
        textureProgram.setUniforms(((AndroidOpenGLRenderView)game.getRenderObj()).getProjectionMatrix(),
                texture);
        table.bindData(textureProgram);
        table.draw();
        // Draw the mallets.
        colorProgram.useProgram();
        colorProgram.setUniforms(((AndroidOpenGLRenderView)game.getRenderObj()).getProjectionMatrix());
        mypoint.bindData(colorProgram);
        mypoint.draw();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
        textureProgram = new TextureShaderProgram(game);
        colorProgram = new ColorShaderProgram(game);
        texture = TextureHelper.loadTexture(game, "duelmaze.png");
    }

    @Override
    public void dispose() {

    }

    @Override
    public void back() {

    }
}
