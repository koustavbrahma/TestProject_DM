package koustav.duelmasters.main.androidgameduelmasterwidgetsimulation;

import java.util.ArrayList;

import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.PvPWidgetCoordinator;

/**
 * Created by Koustav on 1/21/2017.
 */
public class SimulationManager {
    PvPWidgetCoordinator coordinator;

    CardMovementSimulator cardMovementSimulator;
    //Array list for Simulations
    ArrayList<Simulate> simulationList;
    ArrayList<Simulate> simulationToBeRemoved;
    public SimulationManager(PvPWidgetCoordinator coordinator) {
        this.coordinator = coordinator;

        cardMovementSimulator = new CardMovementSimulator(coordinator);
    }

    public SimulationID Simulate(Object ...obj) {
        SimulationID ID = null;
        if (obj[0] == SimulationType.CardMovement) {
            if (obj.length == 4) {
                ID = cardMovementSimulator.Start(obj[1], obj[2], obj[3]);
            } else if (obj.length == 3) {
                ID = cardMovementSimulator.Start(obj[1], obj[2]);
            } else {
                throw new RuntimeException("Invalid condition");
            }
        }

        return ID;
    }

    public boolean SimulationStatus(Object ...obj) {
        if (obj[0] == SimulationType.CardMovement) {
            return cardMovementSimulator.IsFinish((SimulationID)obj[1]);
        } else {
            return true;
        }
    }

    public void SimulationUpdate() {
        cardMovementSimulator.update();
    }
}
