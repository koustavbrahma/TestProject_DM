package koustav.duelmasters.main.androidgameopengl.androidgameopenglmotionmodels;

import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl.ViewNodePosition;

/**
 * Created by Koustav on 9/26/2017.
 */
public interface MotionModels {
    public ViewNodePosition update(float deltaTime, float totalTime);
    public boolean isFinished(float deltaTime, float totalTime);
}
