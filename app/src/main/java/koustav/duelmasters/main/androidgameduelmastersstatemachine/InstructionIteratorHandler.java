package koustav.duelmasters.main.androidgameduelmastersstatemachine;

import java.util.ArrayList;

import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionSet;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.InactiveCard;
import koustav.duelmasters.main.androidgameduelmastersworlds.PvPWorld;
import koustav.duelmasters.main.androidgameduelmasterseventlogmodule.DirectiveHeader;
import koustav.duelmasters.main.androidgameduelmastersutil.ActUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.InstSetUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.NetworkUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.SetUnsetUtil;

/**
 * Created by Koustav on 5/24/2015.
 */
public class InstructionIteratorHandler {
    PvPWorld world;
    int InstCount;
    ArrayList<InstructionSet> instructions;
    InactiveCard card;
    InstructionSet NotYetSpreadCleanup;
    SubStates currentState;
    SubStates S1;
    SubStates S2;

    public InstructionIteratorHandler(PvPWorld world) {
        this.world = world;
        DefineStates();
        currentState = S1;
        InstCount = 0;
        instructions = null;
        card = null;
        String instruction = InstSetUtil.GenerateAttributeCleanUpInstruction(3333, "NotYetSpread", 1);
        NotYetSpreadCleanup = new InstructionSet(instruction);
    }

    public void setInstructions(ArrayList<InstructionSet> instructions) {
        this.instructions = instructions;
    }

    public void setCard(InactiveCard card) {
        this.card = card;
    }

    public boolean update() {
        return currentState.updateState();
    }

    private void setCurrentState(SubStates state) {
        currentState = state;
    }

    private void DefineStates() {
        S1 = new SubStates() {
            @Override
            public boolean updateState() {
                if (instructions != null && InstCount < instructions.size()) {
                    InstructionSet instruction = instructions.get(InstCount);
                    world.getInstructionHandler().setCardAndInstruction(card, instruction);
                    setCurrentState(S2);
                    InstCount++;
                    return false;
                } else {
                    InstCount = 0;
                    setCurrentState(S1);
                    instructions = null;
                    card = null;
                    return true;
                }
            }

            @Override
            public void StateSetting() {

            }
        };

        S2 = new SubStates() {
            @Override
            public boolean updateState() {
                if (world.getInstructionHandler().execute()) {
                    ArrayList<String> events = world.getEventLog().getEventsToExecute();
                    if (events != null) {
                        if (world.getEventLog().getRecording() == false)
                            throw new IllegalArgumentException("I am not expecting this to be false here");
                        world.getEventLog().setRecording(false);
                        for (int i = 0; i < events.size(); i++) {
                            ActUtil.ApplyEventsInt(events.get(i), world);
                        }
                        world.getEventLog().setRecording(true);
                    }
                    SetUnsetUtil.PerformCleanUpForMovedCard(world);
                    //send Eventlog
                    String msg = world.getEventLog().getAndClearEvents();
                    NetworkUtil.sendDirectiveUpdates(world, DirectiveHeader.ApplyEvents, msg, null);
                    SetUnsetUtil.SpreadingFlagAttr(world);
                    world.getInstructionHandler().setCardAndInstruction(null, NotYetSpreadCleanup);
                    world.getInstructionHandler().execute();
                    setCurrentState(S1);
                }
                return false;
            }

            @Override
            public void StateSetting() {

            }
        };
    }
}
