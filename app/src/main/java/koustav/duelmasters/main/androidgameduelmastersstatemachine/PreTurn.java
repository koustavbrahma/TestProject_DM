package koustav.duelmasters.main.androidgameduelmastersstatemachine;

import java.util.ArrayList;

import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionID;
import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionSet;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.ActiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.InactiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.World;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.WorldFlags;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Zone;
import koustav.duelmasters.main.androidgameduelmastersnetworkmodule.DirectiveHeader;
import koustav.duelmasters.main.androidgameduelmastersutil.GetUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.InstSetUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.NetworkUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.SetUnsetUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.UIUtil;

/**
 * Created by Koustav on 3/28/2015.
 */
public class PreTurn {
    enum State {
        S1,
        S2,
        S3,
        S4,
        S5,
    }
    State S;
    World world;
    InstructionSet UnTapManaInstruction;
    InstructionSet UnTapCreaturesInstruction;
    InstructionSet CleanDontUnTap;
    InstructionSet NotYetSpreadCleanup;
    int Counter;

    public PreTurn(World world){
        this.world = world;
        S = State.S1;
        String instruction = InstSetUtil.GenerateAttributeCleanUpInstruction(10, "Tapped", 1);
        UnTapManaInstruction = new InstructionSet(instruction);
        String instruction2 =
                InstSetUtil.GenerateAttributeCleanUpBasedOnNotOfOtherAttributeInstruction(1, "DontUnTap", "Tapped", 1);
        UnTapCreaturesInstruction = new InstructionSet(instruction2);
        String instruction3 = InstSetUtil.GenerateAttributeCleanUpInstruction(1, "DontUnTap", 1);
        CleanDontUnTap = new InstructionSet(instruction3);
        String instruction4 = InstSetUtil.GenerateAttributeCleanUpInstruction(3333, "NotYetSpread", 1);
        NotYetSpreadCleanup = new InstructionSet(instruction4);
        Counter = 0;
    }

    public boolean update(){
        if (S == State.S1)
            Cleanup();
        if (S == State.S2)
            ResetAndUnpackOppTappedAttr();
        if (S == State.S3)
            RunSilentSkill();
        if (S == State.S4)
            ExecuteSilentSkill();
        if (S == State.S5) {
            UnTapCards();
            return true;
        }
        return false;
    }

    private void Cleanup() {
        world.getInstructionHandler().setCardAndInstruction(null, UnTapManaInstruction);
        world.getInstructionHandler().execute();

        Zone MyBattleZone = world.getMaze().getZoneList().get(0);

        for (int i = 0; i < MyBattleZone.zoneSize(); i++) {
            ActiveCard card = (ActiveCard) MyBattleZone.getZoneArray().get(i);
            ArrayList<InstructionSet> instructions = card.getTemporaryPreCleanup();
            world.getInstructionIteratorHandler().setCard(card);
            world.getInstructionIteratorHandler().setInstructions(instructions);
            while (!world.getInstructionIteratorHandler().update()) ;
            card.ClearTemporaryPreCleanup();
        }

        Zone MyHandCards = world.getMaze().getZoneList().get(3);

        for (int i = 0; i < MyHandCards.zoneSize(); i++) {
            ActiveCard card = (ActiveCard) MyHandCards.getZoneArray().get(i);
            ArrayList<InstructionSet> instructions = card.getTemporaryPreCleanup();
            world.getInstructionIteratorHandler().setCard(card);
            world.getInstructionIteratorHandler().setInstructions(instructions);
            while (!world.getInstructionIteratorHandler().update()) ;
            card.ClearTemporaryPreCleanup();
        }

        Zone OppBattleZone = world.getMaze().getZoneList().get(7);

        for (int i = 0; i < OppBattleZone.zoneSize(); i++) {
            InactiveCard card = (InactiveCard) OppBattleZone.getZoneArray().get(i);
            ArrayList<InstructionSet> instructions = card.getTemporaryPostCleanup();
            world.getInstructionIteratorHandler().setCard(card);
            world.getInstructionIteratorHandler().setInstructions(instructions);
            while (!world.getInstructionIteratorHandler().update()) ;
            card.ClearTemporaryPostCleanup();
            card.ClearTemporarySpreadingInst();
        }

        Zone OppHandCards = world.getMaze().getZoneList().get(10);

        for (int i = 0; i < OppHandCards.zoneSize(); i++) {
            InactiveCard card = (InactiveCard) OppHandCards.getZoneArray().get(i);
            ArrayList<InstructionSet> instructions = card.getTemporaryPostCleanup();
            world.getInstructionIteratorHandler().setCard(card);
            world.getInstructionIteratorHandler().setInstructions(instructions);
            while (!world.getInstructionIteratorHandler().update()) ;
            card.ClearTemporaryPostCleanup();
        }

        String msg = NetworkUtil.GenerateTappedCardInfo(world, 1);
        NetworkUtil.sendDirectiveUpdates(world, DirectiveHeader.TappedCardInfo, msg, null);
        S = State.S2;
    }

    private void ResetAndUnpackOppTappedAttr() {
        if (world.getGame().getNetwork().getSocket() == null || world.getGame().getNetwork().getSocket().isClosed()) {
            SetUnsetUtil.SpreadingFlagAttr(world);
            world.getInstructionHandler().setCardAndInstruction(null, NotYetSpreadCleanup);
            world.getInstructionHandler().execute();
            S = State.S3;
            // for now this but later handle it properly
            return;
        }
        if (world.getGame().getNetwork().getreceivedDirectiveSize() == 0)
            return;

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
        S = State.S3;
    }

    private void RunSilentSkill(){
        Zone zone = world.getMaze().getZoneList().get(0);
        if (Counter < zone.zoneSize()) {
            ActiveCard card = (ActiveCard) zone.getZoneArray().get(Counter);
            if (GetUtil.IsTapped(card) && GetUtil.IsSilentSkill(card)) {
                world.setFetchCard(card);
                world.setWorldFlag(WorldFlags.SilentSkillMode);
                if (UIUtil.TouchedAcceptButton(world)) {
                    ArrayList<InstructionSet> instructions = card.getPrimaryInstructionForTheInstructionID(InstructionID.SilentSkill);
                    world.getInstructionIteratorHandler().setCard(card);
                    world.getInstructionIteratorHandler().setInstructions(instructions);
                    card.getflagAttributes().SetAttribute("DontUnTap", 1);
                    S = State.S4;
                    world.getEventLog().setRecording(true);
                    Counter++;
                    world.clearWorldFlag(WorldFlags.SilentSkillMode);
                }

                if (UIUtil.TouchedDeclineButton(world)) {
                    Counter++;
                    world.clearWorldFlag(WorldFlags.SilentSkillMode);
                }
            } else {
                Counter++;
            }
        } else {
            Counter = 0;
            S = State.S5;
        }
    }

    private void ExecuteSilentSkill() {
        if (world.getInstructionIteratorHandler().update()) {
            world.getEventLog().setRecording(false);
            //send Eventlog
            String msg = world.getEventLog().getAndClearEvents();
            NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
            SetUnsetUtil.SpreadingFlagAttr(world);
            world.getInstructionHandler().setCardAndInstruction(null, NotYetSpreadCleanup);
            world.getInstructionHandler().execute();
            S = State.S3;
        }
    }

    private void UnTapCards(){
        world.getInstructionHandler().setCardAndInstruction(null, UnTapCreaturesInstruction);
        world.getInstructionHandler().execute();
        world.getInstructionHandler().setCardAndInstruction(null, CleanDontUnTap);
        world.getInstructionHandler().execute();
        String msg = NetworkUtil.GenerateTappedCardInfo(world, 0);
        NetworkUtil.sendDirectiveUpdates(world, DirectiveHeader.TappedCardInfo, msg, null);
        SetUnsetUtil.SpreadingFlagAttr(world);
        world.getInstructionHandler().setCardAndInstruction(null, NotYetSpreadCleanup);
        world.getInstructionHandler().execute();
        S = State.S1;
    }

}