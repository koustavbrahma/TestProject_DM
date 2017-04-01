package koustav.duelmasters.main.androidgameduelmasters;

import koustav.duelmasters.main.androidgameduelmasterscreens.TestLoadingScreen;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Screen;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkimpl.AndroidGame;

/**
 * Created by Koustav on 2/14/2015.
 */
public class DuelMasters extends AndroidGame {

    @Override
    public Screen getStartScreen() {
        return new TestLoadingScreen(this);
    }
}
