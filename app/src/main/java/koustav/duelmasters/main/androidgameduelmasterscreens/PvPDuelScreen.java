package koustav.duelmasters.main.androidgameduelmasterscreens;

import koustav.duelmasters.main.androidgameduelmastersworlds.PvPWorld;
import koustav.duelmasters.main.androidgamesframework.Screen;
import koustav.duelmasters.main.androidgamesframeworkimpl.AndroidGame;

/**
 * Created by Koustav on 9/30/2016.
 */
public class PvPDuelScreen extends Screen {
    enum GameState {
        Loading,
        Running
    }
    PvPWorld world;
    GameState state;

    public PvPDuelScreen(AndroidGame game) {
        super(game);
        world = new PvPWorld(game, game.getTurn());
        state = GameState.Loading;
    }

    @Override
    public void update(float deltaTime, float totalTime) {
        if (state == GameState.Loading) {
           world.load();
           state = GameState.Running;
        }

        if (state == GameState.Running) {
            world.update(deltaTime, totalTime);
        }
    }

    @Override
    public void present() {
        if (state == GameState.Running) {
            world.draw();
        }
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
