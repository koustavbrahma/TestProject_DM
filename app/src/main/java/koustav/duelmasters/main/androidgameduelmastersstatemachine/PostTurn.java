package koustav.duelmasters.main.androidgameduelmastersstatemachine;

import java.util.ArrayList;

import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionSet;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.ActiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.InactiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.World;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Zone;
import koustav.duelmasters.main.androidgameduelmastersnetworkmodule.DirectiveHeader;
import koustav.duelmasters.main.androidgameduelmastersutil.InstSetUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.NetworkUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.SetUnsetUtil;

/**
 * Created by Koustav on 3/28/2015.
 */
public class PostTurn {
    enum PostTurnState {
        S1,
        S2,
    }
    World world;
    InstructionSet NewSummonedCreatureCleanUp;
    InstructionSet NotYetSpreadCleanup;
    PostTurnState S;

    public PostTurn(World world){
        this.world = world;
        String instruction = InstSetUtil.GenerateAttributeCleanUpInstruction(1, "NewSummonedCreature", 1);
        NewSummonedCreatureCleanUp = new InstructionSet(instruction);
        String instruction2 = InstSetUtil.GenerateAttributeCleanUpInstruction(3333, "NotYetSpread", 1);
        NotYetSpreadCleanup = new InstructionSet(instruction2);
        S = PostTurnState.S1;
    }

    public boolean update() {
        boolean status = false;
        if (S == PostTurnState.S1)
            Cleanup();
        if (S == PostTurnState.S2) {
            status = ResetAndUnpackOppCardAttr();
        }
        return status;
    }

    private void Cleanup() {
        world.getInstructionHandler().setCardAndInstruction(null, NewSummonedCreatureCleanUp);
        world.getInstructionHandler().execute();
        Zone MyBattleZone = world.getMaze().getZoneList().get(0);

        for (int i = 0; i < MyBattleZone.zoneSize(); i++) {
            ActiveCard card = (ActiveCard) MyBattleZone.getZoneArray().get(i);
            ArrayList<InstructionSet> instructions = card.getTemporaryPostCleanup();
            world.getInstructionIteratorHandler().setCard(card);
            world.getInstructionIteratorHandler().setInstructions(instructions);
            while (!world.getInstructionIteratorHandler().update()) ;
            card.ClearTemporaryPostCleanup();
            card.ClearTemporarySpreadingInst();
        }
        Zone MyHandCards = world.getMaze().getZoneList().get(3);

        for (int i = 0; i < MyHandCards.zoneSize(); i++) {
            ActiveCard card = (ActiveCard) MyHandCards.getZoneArray().get(i);
            ArrayList<InstructionSet> instructions = card.getTemporaryPostCleanup();
            world.getInstructionIteratorHandler().setCard(card);
            world.getInstructionIteratorHandler().setInstructions(instructions);
            while (!world.getInstructionIteratorHandler().update()) ;
            card.ClearTemporaryPostCleanup();
        }

        Zone OppBattleZone = world.getMaze().getZoneList().get(7);

        for (int i = 0; i < OppBattleZone.zoneSize(); i++) {
            InactiveCard card = (InactiveCard) OppBattleZone.getZoneArray().get(i);
            ArrayList<InstructionSet> instructions = card.getTemporaryPreCleanup();
            world.getInstructionIteratorHandler().setCard(card);
            world.getInstructionIteratorHandler().setInstructions(instructions);
            while (!world.getInstructionIteratorHandler().update()) ;
            card.ClearTemporaryPreCleanup();
        }

        Zone OppHandCards = world.getMaze().getZoneList().get(10);

        for (int i = 0; i < OppHandCards.zoneSize(); i++) {
            InactiveCard card = (InactiveCard) OppHandCards.getZoneArray().get(i);
            ArrayList<InstructionSet> instructions = card.getTemporaryPreCleanup();
            world.getInstructionIteratorHandler().setCard(card);
            world.getInstructionIteratorHandler().setInstructions(instructions);
            while (!world.getInstructionIteratorHandler().update()) ;
            card.ClearTemporaryPreCleanup();
        }

        String msg = NetworkUtil.GenerateTappedCardInfo(world, 0);
        String msg1 = NetworkUtil.GenerateTappedCardInfo(world, 1);
        if (msg == null) {
            msg = msg1;
        } else {
            if (msg1 != null) {
                msg = msg.concat("#");
                msg = msg.concat(msg1);
            }
        }
        NetworkUtil.sendDirectiveUpdates(world, DirectiveHeader.TappedCardInfo, msg, null);
        S = PostTurnState.S2;
    }

    private boolean ResetAndUnpackOppCardAttr() {
        if (world.getGame().getNetwork().getSocket() == null || world.getGame().getNetwork().getSocket().isClosed()) {
            SetUnsetUtil.SpreadingFlagAttr(world);
            world.getInstructionHandler().setCardAndInstruction(null, NotYetSpreadCleanup);
            world.getInstructionHandler().execute();
            S = PostTurnState.S1;
            // for now this but later handle it properly
            return true;
        }
        if (world.getGame().getNetwork().getreceivedDirectiveSize() == 0)
            return false;

        String directive = world.getGame().getNetwork().getreceivedDirectiveMsg();
        String [] splitdirective = directive.split("@");

        if (!splitdirective[0].equals(DirectiveHeader.TappedCardInfo)) {
            throw new IllegalArgumentException("Invalid Directive at this point");
        }

        if (splitdirective.length > 2) {
            String [] packedData = splitdirective[1].split("#");
            for (int i =0; i < packedData.length; i++) {
                String[] eventField = packedData[i].split(" ");

                if (eventField.length != 5)
                    throw new IllegalArgumentException("Invalid tapped card info");

                int Cardzone = Integer.parseInt(eventField[0]);
                int GridIndex = Integer.parseInt(eventField[1]);
                if (Cardzone != 7 && Cardzone != 8)
                    throw new IllegalArgumentException("Invalid zone for tapped card info");

                InactiveCard card = (InactiveCard) world.getGridIndexTrackingTable().getCardMappedToGivenGridPosition(Cardzone,GridIndex);
                if (!card.getNameID().equals(eventField[2]))
                    throw new IllegalArgumentException("Data inconsistency");

                if (eventField[4].equals("1"))
                    SetUnsetUtil.SetTappedAttr(card);
                else
                    SetUnsetUtil.UnSetTappedAttr(card);
            }
        }

        SetUnsetUtil.SpreadingFlagAttr(world);
        world.getInstructionHandler().setCardAndInstruction(null, NotYetSpreadCleanup);
        world.getInstructionHandler().execute();
        S = PostTurnState.S1;
        return true;
    }
}
