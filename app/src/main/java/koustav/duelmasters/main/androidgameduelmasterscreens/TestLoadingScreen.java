package koustav.duelmasters.main.androidgameduelmasterscreens;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Graphics;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Graphics.PixmapFormat;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Screen;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkimpl.AndroidGame;

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
        AssetsAndResource.background = g.newPixmap("duelmaze.png", PixmapFormat.RGB565);
        AssetsAndResource.cardbackside = g.newPixmap("cardbackside.png", PixmapFormat.RGB565);
        AssetsAndResource.Button = g.newPixmap("PauseButton.png", PixmapFormat.RGB565);
        AssetsAndResource.InfoBackground = g.newPixmap("InfoBackground.png", PixmapFormat.RGB565);
        //game.setScreen(new TestScreen(game));
        //game.setScreen(new MenuScreen(game));
        //game.setScreen(new GlTestScreen(game));
        //game.setScreen(new TestScreen2(game));
        //game.setScreen(new PvPDuelScreenTesting(game));
        AssetsAndResource.Load(game, 0f, 2.4f, 1.6f);
        game.setScreen(new PvPDuelScreen(game));
    }

    @Override
    public void present() {
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
