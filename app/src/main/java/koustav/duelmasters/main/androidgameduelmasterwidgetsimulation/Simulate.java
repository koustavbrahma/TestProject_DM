package koustav.duelmasters.main.androidgameduelmasterwidgetsimulation;

import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;

/**
 * Created by Koustav on 9/26/2016.
 */
public interface Simulate {
    public SimulationID Start(Object ...obj);
    public boolean IsFinish(SimulationID ID);
    public void update();
}
