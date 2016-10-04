package koustav.duelmasters.main.androidgameduelmastersstatemachine;

import koustav.duelmasters.main.androidgameduelmastersworlds.PvPWorld;

/**
 * Created by Koustav on 3/28/2015.
 */
public class WorldUpdateOnTurn {
    enum WorldStateOnTurn {
        PreTurn,
        OnTurn,
        PostTurn,
    }
    PvPWorld world;
    WorldStateOnTurn stateOnTurn;
    PreTurn preTurn;
    OnTurn onTurn;
    PostTurn postTurn;

    public WorldUpdateOnTurn(PvPWorld world, boolean turn) {
        this.world = world;
        if (turn) {
            stateOnTurn = WorldStateOnTurn.OnTurn;
        } else {
            stateOnTurn = WorldStateOnTurn.PreTurn;
        }
        preTurn = new PreTurn(world);
        onTurn = new OnTurn(world);
        postTurn = new PostTurn(world);
    }

    public void update() {
        if (stateOnTurn == WorldStateOnTurn.PreTurn)
            RunPreTurnHandler();
        if (stateOnTurn == WorldStateOnTurn.OnTurn)
            RunOnTurnHandler();
        if (stateOnTurn == WorldStateOnTurn.PostTurn)
            RunPostTurnHandler();
    }

    private void RunPreTurnHandler() {
        if(preTurn.update())
            stateOnTurn = WorldStateOnTurn.OnTurn;
    }

    private void RunOnTurnHandler() {
        if(onTurn.update())
            stateOnTurn = WorldStateOnTurn.PostTurn;
    }

    private void RunPostTurnHandler() {
        if (postTurn.update()) {
            stateOnTurn = WorldStateOnTurn.PreTurn;
            world.setTurn(false);
        }
    }

}
