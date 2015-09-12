package koustav.duelmasters.main.androidgameduelmasters;

import java.util.List;

import koustav.duelmasters.main.androidgameduelmastersdatastructure.World;
import koustav.duelmasters.main.androidgameduelmastersassets.Assets;
import koustav.duelmasters.main.androidgameduelmastersutil.CreateScenarioUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.GraphicUtil;
import koustav.duelmasters.main.androidgamesframework.Game;
import koustav.duelmasters.main.androidgamesframework.Graphics;
import koustav.duelmasters.main.androidgamesframework.Input.TouchEvent;
import koustav.duelmasters.main.androidgamesframework.Screen;
import koustav.duelmasters.main.androidgamesframeworkimpl.AndroidGame;

/**
 * Created by Koustav on 2/13/2015.
 */
public class TestScreen extends Screen {
    enum GameState {
        Loading,
        Ready,
        Running,
        Paused
    }

    World world;
    GameState state;

    public TestScreen(AndroidGame game) {
        super(game);
        world = new World(game, game.getTurn());
        state = GameState.Loading;
        game.getNetwork().start();
    }

    @Override
    public void update(float deltaTime) {
        if(state == GameState.Loading)
            updateLoading();
        if(state == GameState.Ready)
            updateReady();
        if(state == GameState.Running)
            updateRunning(deltaTime);
        if(state == GameState.Paused)
            updatePaused();
    }

    private void updateLoading() {
        CreateScenarioUtil.CreateTestScenario(world);
        CreateScenarioUtil.CreateTestScenario2(world);
        state = GameState.Ready;
    }

    private void updateReady() {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        int len = touchEvents.size();
        for(int i = 0; i<len; i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if (event.x < 64 && event.y < 64) {
                    state = GameState.Running;
                }
            }
        }
    }

    private void updateRunning(float deltaTime) {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        world.setTouchEvents(touchEvents);
        boolean run = true;
        int len = touchEvents.size();
        for(int i = 0; i<len; i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if (event.x < 40 && event.y < 40) {
                    state = GameState.Paused;
                    run = false;
                }
            }
        }

        if (run) {
            world.update(deltaTime);
        }
    }

    private void updatePaused() {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        for(int i = 0; i<touchEvents.size(); i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if (event.x < 40 && event.y < 40) {
                    state = GameState.Running;
                }
            }
        }
    }

    @Override
    public void present(float deltaTime) {
        Graphics g = game.getGraphics();
        g.drawPixmap(Assets.background, 0, 0);
        g.drawPixmap(Assets.Button, 0, 0);

        if (state == GameState.Running)
            drawRunningUI();
    }

    public void drawRunningUI() {
        GraphicUtil.presentControlButtons(world);
        GraphicUtil.presentYourCards(world);
        GraphicUtil.presentOpponentCards(world);
        GraphicUtil.presentInfoTab(world);
        GraphicUtil.presentHighlightManaCard(world);
        GraphicUtil.presentAttackedCard(world);
        GraphicUtil.presentSilentSkillOrShieldTrigger(world);
        GraphicUtil.presentCardSelectMode(world);
        GraphicUtil.presentCardInfoUserSelect(world);
        GraphicUtil.presentBlockerSelect(world);
        GraphicUtil.presentCardInfoUserSearchSelect(world);
        GraphicUtil.presentUserDecisionMakingMode(world);
    }

    @Override
    public void pause() {
        if(state == GameState.Running)
            state = GameState.Paused;
    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void back() {
        if(state == GameState.Running) {
            state = GameState.Paused;
        } else if(state == GameState.Paused) {
            state = GameState.Running;
        }
    }
}
