package koustav.duelmasters.main.androidgameduelmasters;

import koustav.duelmasters.main.androidgameduelmastersassets.Assets;
import koustav.duelmasters.main.androidgamesframework.Game;
import koustav.duelmasters.main.androidgamesframework.Graphics;
import koustav.duelmasters.main.androidgamesframework.Graphics.PixmapFormat;
import koustav.duelmasters.main.androidgamesframework.Screen;
import koustav.duelmasters.main.androidgamesframeworkimpl.AndroidGame;

/**
 * Created by Koustav on 2/19/2015.
 */
public class TestLoadingScreen extends Screen {
    public TestLoadingScreen(AndroidGame game) {
        super(game);
    }

    @Override
    public void update(float deltaTime) {
        Graphics g = game.getGraphics();
        Assets.background = g.newPixmap("duelmaze.png", PixmapFormat.RGB565);
        Assets.cardbackside = g.newPixmap("cardbackside.png", PixmapFormat.RGB565);
        Assets.Button = g.newPixmap("Button.png", PixmapFormat.RGB565);
        Assets.InfoBackground = g.newPixmap("InfoBackground.png", PixmapFormat.RGB565);
        //game.setScreen(new TestScreen(game));
        //game.setScreen(new MenuScreen(game));
        game.setScreen(new GlTestScreen(game));
    }

    @Override
    public void present(float deltaTime) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void back() {

    }
}
