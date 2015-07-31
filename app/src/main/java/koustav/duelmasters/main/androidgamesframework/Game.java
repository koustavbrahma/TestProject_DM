package koustav.duelmasters.main.androidgamesframework;

import java.net.Socket;

import koustav.duelmasters.main.androidgamesframeworkimpl.AndroidNetwork;

/**
 * Created by Koustav on 2/10/2015.
 * Abstract: Game interface.
 */
public interface Game {
    public Input getInput();
    public FileIO getFileIO();
    public Graphics getGraphics();
    public Audio getAudio();
    public void setScreen(Screen screen);
    public Screen getCurrentScreen();
    public Screen getStartScreen();
    public AndroidNetwork getNetwork();
    public int getframeBufferWidth();
    public int getframeBufferHeight();
    public void setTurn(boolean val);
    public boolean getTurn();
}
