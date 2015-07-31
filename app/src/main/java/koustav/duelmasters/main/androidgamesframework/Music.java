package koustav.duelmasters.main.androidgamesframework;

/**
 * Created by Koustav on 2/7/2015.
 * Abstract: Interface for Music.
 */
public interface Music {
    public void play();
    public void stop();
    public void pause();
    public void setLooping(boolean looping);
    public void setVolume(float volume);
    public boolean isPlaying();
    public boolean isStopped();
    public boolean isLooping();
    public void dispose();
}
