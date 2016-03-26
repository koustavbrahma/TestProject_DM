package koustav.duelmasters.main.androidgameduelmasterscreens;

import koustav.duelmasters.main.androidgameduelmastersassetsandresourcesforscreen.AssetsAndResourceForPvP;
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
    public void update(float deltaTime, float totalTime) {
        Graphics g = game.getGraphics();
        AssetsAndResourceForPvP.background = g.newPixmap("duelmaze.png", PixmapFormat.RGB565);
        AssetsAndResourceForPvP.cardbackside = g.newPixmap("cardbackside.png", PixmapFormat.RGB565);
        AssetsAndResourceForPvP.Button = g.newPixmap("Button.png", PixmapFormat.RGB565);
        AssetsAndResourceForPvP.InfoBackground = g.newPixmap("InfoBackground.png", PixmapFormat.RGB565);
        //game.setScreen(new TestScreen(game));
        //game.setScreen(new MenuScreen(game));
        //game.setScreen(new GlTestScreen(game));
        //game.setScreen(new TestScreen2(game));
        game.setScreen(new PvPDuelScreenTesting(game));
    }

    @Override
    public void present(float deltaTime, float totalTime) {
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
