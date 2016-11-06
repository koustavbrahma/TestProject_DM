package koustav.duelmasters.main.androidgameduelmasterscreens;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmasterswidgetcoordinationtools.Query;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.PvPWidgetCoordinator;
import koustav.duelmasters.main.androidgameduelmastersworlds.PvPWorld;
import koustav.duelmasters.main.androidgamesframework.Screen;
import koustav.duelmasters.main.androidgamesframeworkimpl.AndroidGame;

/**
 * Created by Koustav on 9/30/2016.
 */
public class PvPDuelScreen extends Screen {
    enum GameState {
        Initialize,
        Loading,
        PostLoadingWait,
        Running
    }
    PvPWorld world;
    GameState state;

    public PvPDuelScreen(AndroidGame game) {
        super(game);
        state = GameState.Initialize;
    }

    @Override
    public void update(float deltaTime, float totalTime) {
        if (state == GameState.Initialize) {
            world = new PvPWorld(game, game.getTurn());
            AssetsAndResource.setCurrentWorld(world);
            state = GameState.Loading;
        }

        if (state == GameState.Loading) {
           world.load();
           state = GameState.PostLoadingWait;
        }

        if (state == GameState.PostLoadingWait) {
            PvPWidgetCoordinator coordinator = world.getWidgetCoordinator();
            if ((boolean)coordinator.GetInfo(Query.IsSetUpDone, null)) {
                state = GameState.Running;
            }
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
        AssetsAndResource.FreeAssetsAndResourcesForPvP();
    }

    @Override
    public void resume() {
        AssetsAndResource.InitializeAssetsAndResourceForPvP();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void back() {

    }
}
