package koustav.duelmasters.main.androidgameduelmastersstatemachine;

import koustav.duelmasters.main.androidgameduelmastersdatastructure.World;

/**
 * Created by Koustav on 3/28/2015.
 */
public class WorldUpdateOnTurn {
    enum WorldStateOnTurn {
        PreTurn,
        OnTurn,
        PostTurn,
    }
    World world;
    WorldStateOnTurn stateOnTurn;
    PreTurn preTurn;
    OnTurn onTurn;
    PostTurn postTurn;

    public WorldUpdateOnTurn(World world, boolean trun) {
        this.world = world;
        if (trun) {
            stateOnTurn = WorldStateOnTurn.OnTurn;
        } else {
            stateOnTurn = WorldStateOnTurn.PreTurn;
        }
        preTurn = new PreTurn(world);
        onTurn = new OnTurn(world);
        postTurn = new PostTurn(world);
    }

    public void update(float deltatime) {
        if (stateOnTurn == WorldStateOnTurn.PreTurn)
            RunPreTurnHandler(deltatime);
        if (stateOnTurn == WorldStateOnTurn.OnTurn)
            RunOnTurnHandler(deltatime);
        if (stateOnTurn == WorldStateOnTurn.PostTurn)
            RunPostTurnHandler(deltatime);
    }

    public void RunPreTurnHandler(float deltatime) {
        if(preTurn.update())
            stateOnTurn = WorldStateOnTurn.OnTurn;
    }

    public void RunOnTurnHandler(float deltatime) {
        if(onTurn.update())
            stateOnTurn = WorldStateOnTurn.PostTurn;
    }

    public void RunPostTurnHandler(float deltatime) {
        if (postTurn.update()) {
            stateOnTurn = WorldStateOnTurn.PreTurn;
            world.setTurn(false);
        }
    }

}
