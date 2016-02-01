package koustav.duelmasters.main.androidgamesframework;

import koustav.duelmasters.main.androidgamesframeworkimpl.AndroidGame;

/**
 * Created by Koustav on 2/10/2015.
 * Abstract: Screen class which is used to create different screens.
 */
public abstract class Screen {
    protected final AndroidGame game;

    public  Screen(AndroidGame game) {
        this.game = game;
    }

    public abstract void update(float deltaTime, float totalTime);
    public abstract void present(float deltaTime, float totalTime);
    public abstract void pause();
    public abstract void resume();
    public abstract void dispose();
    public abstract void back();

}
