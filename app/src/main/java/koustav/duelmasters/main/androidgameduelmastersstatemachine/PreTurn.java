package koustav.duelmasters.main.androidgameduelmastersstatemachine;

import java.util.ArrayList;

import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionID;
import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionSet;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.ActiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.InactiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.TypeOfCard;
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
        S2a,
        S2b,
        S2c,
        S2d,
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
    InstructionSet CollectInst;
    InstructionSet ActiveTurboRushCleanup;
    InstructionSet UsedTurboRushSetAttrCleanup;
    int Counter;
    Boolean SummonTapped;

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
        String CollectAttackMarkedCard = InstSetUtil.GenerateCopyCardToTempZoneBasedOnAttribute("MarkedCard", 1000);
        CollectInst = new InstructionSet(CollectAttackMarkedCard);
        String instruction5 = InstSetUtil.GenerateAttributeCleanUpInstruction(2, "ActiveTurboRush", 100);
        ActiveTurboRushCleanup = new InstructionSet(instruction5);
        String instruction6 = InstSetUtil.GenerateAttributeCleanUpInstruction(2, "UsedTurboRushSetAttr", 1);
        UsedTurboRushSetAttrCleanup = new InstructionSet(instruction6);
        Counter = 0;
        SummonTapped = false;
    }

    public boolean update(){
        if (S == State.S1)
            Cleanup();
        if (S == State.S2)
            ResetAndUnpackOppTappedAttr();
        if (S == State.S2a)
            ShieldTriggerUpdate();
        if (S == State.S2b)
            RunShieldTrigger();
        if (S == State.S2c)
            ShieldTriggerSummonUpdate();
        if (S == State.S2d)
            ShieldTriggerCastUpdate();
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
        world.getInstructionHandler().setCardAndInstruction(null, ActiveTurboRushCleanup);
        world.getInstructionHandler().execute();
        world.getInstructionHandler().setCardAndInstruction(null, UsedTurboRushSetAttrCleanup);
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
        if (world.getWorldFlag(WorldFlags.ShieldTriggerFound)) {
            world.clearWorldFlag(WorldFlags.ShieldTriggerFound);
            world.setWorldFlag(WorldFlags.ShieldTriggerMode);
            S = State.S2a;
            world.getEventLog().setRecording(true);
        } else {
            S = State.S3;
        }
    }

    private void ShieldTriggerUpdate() {
        if (world.getGame().getNetwork().getSocket() == null || world.getGame().getNetwork().getSocket().isClosed()) {
            SetUnsetUtil.SpreadingFlagAttr(world);
            world.getInstructionHandler().setCardAndInstruction(null, NotYetSpreadCleanup);
            world.getInstructionHandler().execute();
            S = State.S3;
            world.getEventLog().setRecording(false);
            // for now this but later handle it properly
            return;
        }
        if (world.getGame().getNetwork().getreceivedDirectiveSize() == 0)
            return;

        String directive = world.getGame().getNetwork().getreceivedDirectiveMsg();
        String [] splitdirective = directive.split("@");

        if (!splitdirective[0].equals(DirectiveHeader.ShieldTriggerInfo)) {
            throw new IllegalArgumentException("Invalid Directive at this point");
        }

        if (splitdirective.length > 2) {
            String[] packedData = splitdirective[1].split("#");
            for (int i = 0; i < packedData.length; i++) {
                String[] eventField = packedData[i].split(" ");

                if (eventField.length != 3)
                    throw new IllegalArgumentException("Invalid tapped card info");

                int Cardzone = Integer.parseInt(eventField[0]);
                int GridIndex = Integer.parseInt(eventField[1]);
                if (Cardzone != 3)
                    throw new IllegalArgumentException("Invalid zone for shield trigger card info");

                ActiveCard card = (ActiveCard) world.getGridIndexTrackingTable().getCardMappedToGivenGridPosition(Cardzone, GridIndex);
                if (!card.getNameID().equals(eventField[2]))
                    throw new IllegalArgumentException("Data inconsistency");
                SetUnsetUtil.SetMarkedCard(card);
            }
        }

        S = State.S2b;
    }

    private void RunShieldTrigger() {
        world.getInstructionHandler().setCardAndInstruction(null, CollectInst);
        world.getInstructionHandler().execute();
        ArrayList<Cards> CollectedCardList = world.getMaze().getZoneList().get(6).getZoneArray();
        if (CollectedCardList.size() == 0) {
            S = State.S3;
            world.clearWorldFlag(WorldFlags.ShieldTriggerMode);
            world.getEventLog().setRecording(false);
            return;
        }
        ActiveCard card = (ActiveCard) CollectedCardList.get(0);
        if (UIUtil.TouchedAcceptButton(world)) {
            SetUnsetUtil.UnSetMarkedCard(card);
            world.clearWorldFlag(WorldFlags.ShieldTriggerMode);

            if (card.getType() == TypeOfCard.Creature) {
                SummonTapped = false;
                if (GetUtil.SummonTapped(card)) {
                    SummonTapped = true;
                }
                String SummonCardInstruction = InstSetUtil.GenerateSelfChangeZoneInstruction(0);
                InstructionSet instruction = new InstructionSet(SummonCardInstruction);
                world.getInstructionHandler().setCardAndInstruction(card, instruction);
                world.getInstructionHandler().execute();
                card = (ActiveCard) world.getInstructionHandler().getCurrentCard();
                if (SummonTapped) {
                    SetUnsetUtil.SetTappedAttr(card);
                    world.getEventLog().registerEvent(card, false, 0 , "Tapped", true ,1);
                    SummonTapped = false;
                }
                //sendeventlog
                String msg = world.getEventLog().getAndClearEvents();
                NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
                if (card.getPrimaryInstructionForTheInstructionID(InstructionID.SummonOrCastAbility) == null) {
                    world.setWorldFlag(WorldFlags.ShieldTriggerMode);
                    SetUnsetUtil.SpreadingFlagAttr(world);
                    world.getInstructionHandler().setCardAndInstruction(null, NotYetSpreadCleanup);
                    world.getInstructionHandler().execute();
                    return;
                } else {
                    world.getInstructionIteratorHandler().setCard(card);
                    ArrayList<InstructionSet> instructions = card.getPrimaryInstructionForTheInstructionID(InstructionID.SummonOrCastAbility);
                    world.getInstructionIteratorHandler().setInstructions(instructions);
                    this.S = State.S2c;
                }
            }

            if (card.getType() == TypeOfCard.Spell) {
                world.getInstructionIteratorHandler().setCard(card);
                ArrayList<InstructionSet> instructions = card.getPrimaryInstructionForTheInstructionID(InstructionID.SummonOrCastAbility);
                world.getInstructionIteratorHandler().setInstructions(instructions);
                S = State.S2d;
            }
        }

        if (UIUtil.TouchedDeclineButton(world)){
            SetUnsetUtil.UnSetMarkedCard(card);
            return;
        }

    }

    private void ShieldTriggerSummonUpdate() {
        if (world.getInstructionIteratorHandler().update()) {
            S = State.S2b;
            world.setWorldFlag(WorldFlags.ShieldTriggerMode);
            //sendevent
            String msg = world.getEventLog().getAndClearEvents();
            NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
            SetUnsetUtil.SpreadingFlagAttr(world);
            world.getInstructionHandler().setCardAndInstruction(null, NotYetSpreadCleanup);
            world.getInstructionHandler().execute();
        }
    }

    private void ShieldTriggerCastUpdate() {
        if (world.getInstructionIteratorHandler().update()) {
            InactiveCard card = world.getInstructionIteratorHandler().getCard();
            if (GetUtil.MaskDestroyDstVal(card) > 0) {
                String DestroyDstInst = InstSetUtil.GenerateSelfChangeZoneInstruction(GetUtil.MaskDestroyDstVal(card) - 1);
                InstructionSet instruction = new InstructionSet(DestroyDstInst);
                world.getInstructionHandler().setCardAndInstruction(card, instruction);
                world.getInstructionHandler().execute();
            } else {
                ArrayList<InstructionSet> instructions = card.getCrossInstructionForTheInstructionID(InstructionID.DestroyDst);
                if (instructions != null) {
                    InstructionSet instruction = instructions.get(0);
                    world.getInstructionHandler().setCardAndInstruction(card, instruction);
                    world.getInstructionHandler().execute();
                } else {
                    String DestroyInst = InstSetUtil.GenerateSelfChangeZoneInstruction(4);
                    InstructionSet instruction = new InstructionSet(DestroyInst);
                    world.getInstructionHandler().setCardAndInstruction(card, instruction);
                    world.getInstructionHandler().execute();
                }
            }
            S = State.S2b;
            world.setWorldFlag(WorldFlags.ShieldTriggerMode);
            //sendevent
            String msg = world.getEventLog().getAndClearEvents();
            NetworkUtil.sendDirectiveUpdates(world, DirectiveHeader.ApplyEvents, msg, null);
            SetUnsetUtil.SpreadingFlagAttr(world);
            world.getInstructionHandler().setCardAndInstruction(null, NotYetSpreadCleanup);
            world.getInstructionHandler().execute();
        }
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