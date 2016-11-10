package koustav.duelmasters.main.androidgameopenglutil;

import android.util.Log;

/**
 * Created by Koustav on 11/10/2016.
 */

public class FPSCounter {
    long startTime;
    int frames;

    public FPSCounter() {
        frames = 0;
        startTime = System.nanoTime();
    }

    public void logFrame() {
        frames++;
        if (System.nanoTime() - startTime >= 1000000000) {
            Log.d("FPSCounter", "fps:" + frames);
            frames = 0;
            startTime = System.nanoTime();
        }
    }
}
