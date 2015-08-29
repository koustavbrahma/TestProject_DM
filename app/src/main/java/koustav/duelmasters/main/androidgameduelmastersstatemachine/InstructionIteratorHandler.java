package koustav.duelmasters.main.androidgameduelmastersstatemachine;

import java.util.ArrayList;

import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionSet;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.InactiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.World;
import koustav.duelmasters.main.androidgameduelmastersnetworkmodule.DirectiveHeader;
import koustav.duelmasters.main.androidgameduelmastersutil.InstSetUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.NetworkUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.SetUnsetUtil;

/**
 * Created by Koustav on 5/24/2015.
 */
public class InstructionIteratorHandler {
    enum AbilityHandlerStates {
        S1,
        S2,
    }

    AbilityHandlerStates S;
    World world;
    int InstCount;
    ArrayList<InstructionSet> instructions;
    InactiveCard card;
    InstructionSet NotYetSpreadCleanup;

    public InstructionIteratorHandler(World world) {
        this.world = world;
        S = AbilityHandlerStates.S1;
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
        this.card= card;
    }

    public InactiveCard getCard () {
        return this.card;
    }

    public boolean update() {
        boolean status = false;
        if (S == AbilityHandlerStates.S1)
            status = InstructionIterator();
        if (S == AbilityHandlerStates.S2)
            InstructionExecutor();

        return status;
    }


    private boolean InstructionIterator() {
        if (instructions != null && InstCount < instructions.size()) {
            InstructionSet instruction = instructions.get(InstCount);
            world.getInstructionHandler().setCardAndInstruction(card, instruction);
            S = AbilityHandlerStates.S2;
            InstCount++;
            return false;
        } else {
            InstCount = 0;
            S = AbilityHandlerStates.S1;
            instructions = null;
            card = null;
            return true;
        }
    }

    private void  InstructionExecutor(){
        if (world.getInstructionHandler().execute()) {
            ArrayList<InstructionSet> CleanUpInst = world.getEventLog().getHoldCleanUp();
            if (CleanUpInst != null) {
                for (int i = 0; i < CleanUpInst.size(); i++) {
                    world.getInstructionHandler().setCardAndInstruction(null, CleanUpInst.get(i));
                    world.getInstructionHandler().execute();
                }
            }
            //send Eventlog
            String msg = world.getEventLog().getAndClearEvents();
            NetworkUtil.sendDirectiveUpdates(world, DirectiveHeader.ApplyEvents, msg, null);
            SetUnsetUtil.SpreadingFlagAttr(world);
            world.getInstructionHandler().setCardAndInstruction(null, NotYetSpreadCleanup);
            world.getInstructionHandler().execute();
            S = AbilityHandlerStates.S1;
        }
    }
}
