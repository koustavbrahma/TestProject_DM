package koustav.duelmasters.main.androidgameduelmastersstatemachine;


import java.util.ArrayList;

import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionID;
import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionSet;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.InactiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.World;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.WorldFlags;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Zone;
import koustav.duelmasters.main.androidgameduelmasterseventlogmodule.DirectiveHeader;
import koustav.duelmasters.main.androidgameduelmastersutil.ActUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.GetUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.InstSetUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.NetworkUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.SetUnsetUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.UIUtil;

/**
 * Created by Koustav on 3/28/2015.
 */
public class WorldUpdateOffTurn {
    World world;
    enum WorldUpdateoffTurnState {
        S1,
        S2,
        S3,
        S4,
        S5,
        S6,
        S7,
        S8,
        S9,
        S10,
        SX,
        SY,
    }

    WorldUpdateoffTurnState S;
    String directive;
    String[] splitdirective;
    InstructionSet NotYetSpreadCleanup;
    InactiveCard AttackingCard;
    InactiveCard AttackedCard;

    public WorldUpdateOffTurn(World world){
        this.world = world;
        this.S = WorldUpdateoffTurnState.S1;
        AttackingCard = null;
        AttackingCard = null;
        String instruction = InstSetUtil.GenerateAttributeCleanUpInstruction(3333, "NotYetSpread", 1);
        NotYetSpreadCleanup = new InstructionSet(instruction);
    }

    public void update(float deltatime) {
        if (S == WorldUpdateoffTurnState.S1) {
            IdealOffTurn();
        }

        if (S == WorldUpdateoffTurnState.S2) {
            ApplyEvents();
        }

        if (S == WorldUpdateoffTurnState.S3) {
            ApplyTappedCardInfo();
        }

        if (S == WorldUpdateoffTurnState.S4) {
            SetCleanUpInst();
        }

        if (S == WorldUpdateoffTurnState.S5) {
            EvolutionHandler();
        }

        if (S == WorldUpdateoffTurnState.S6) {
            SelectBlocker();
        }

        if (S == WorldUpdateoffTurnState.S7) {
            SetTemporarySpreadingInst();
        }

        if (S == WorldUpdateoffTurnState.S8) {
            ExecutePassInstruction();
        }

        if (S == WorldUpdateoffTurnState.S9) {
            SeeOpponentCards();
        }

        if (S == WorldUpdateoffTurnState.S10) {
            UpdateDeckOrder();
        }

        if (S == WorldUpdateoffTurnState.SX) {
            ResumeTurnControl();
        }

        if (S == WorldUpdateoffTurnState.SY) {
            FlagSpreadingUpdate();
        }
    }

    private void IdealOffTurn() {
        if (world.getGame().getNetwork().getSocket() == null || world.getGame().getNetwork().getSocket().isClosed()) {
            S = WorldUpdateoffTurnState.SX;
            // for now this but later handle it properly
            return;
        }
        if (world.getGame().getNetwork().getreceivedDirectiveSize() == 0)
            return;

        directive = world.getGame().getNetwork().getreceivedDirectiveMsg();
        splitdirective = directive.split("@");

        if (splitdirective[0].equals(DirectiveHeader.EndOfTurn)) {
            S = WorldUpdateoffTurnState.SX;
            return;
        }

        if (splitdirective[0].equals(DirectiveHeader.EndOfTurnDueToShieldTrigger)) {
            S = WorldUpdateoffTurnState.SX;
            world.setWorldFlag(WorldFlags.ShieldTriggerFound);
            return;
        }

        if (splitdirective[0].equals(DirectiveHeader.ApplyEvents) && (splitdirective.length > 2)) {
           S = WorldUpdateoffTurnState.S2;
           return;
        }

        if (splitdirective[0].equals(DirectiveHeader.TappedCardInfo) && (splitdirective.length > 2)) {
            S = WorldUpdateoffTurnState.S3;
            return;
        }

        if (splitdirective[0].equals(DirectiveHeader.SetTempCleanUp) && (splitdirective.length > 2)) {
            S = WorldUpdateoffTurnState.S4;
            return;
        }

        if (splitdirective[0].equals(DirectiveHeader.EvolutionEvent) && (splitdirective.length > 2)) {
            S = WorldUpdateoffTurnState.S5;
            return;
        }

        if (splitdirective[0].equals(DirectiveHeader.RequestBlocker) && (splitdirective.length > 2)){
            S = WorldUpdateoffTurnState.S6;
            world.getMaze().getZoneList().get(6).getZoneArray().clear();
            String[] msg = splitdirective[1].split(" ");

            if (msg.length != 3 && msg.length != 6)
                throw new IllegalArgumentException("Invalid evolution directive");

            int ACardzone = Integer.parseInt(msg[0]);
            int AGridIndex = Integer.parseInt(msg[1]);
            AttackingCard = (InactiveCard) world.getGridIndexTrackingTable().getCardMappedToGivenGridPosition(ACardzone, AGridIndex);
            if (!AttackingCard.getNameID().equals(msg[2]))
                throw new IllegalArgumentException("Data inconsistency");
            if (msg.length == 6) {
                int BCardzone = Integer.parseInt(msg[3]);
                int BGridIndex = Integer.parseInt(msg[4]);
                AttackedCard = (InactiveCard) world.getGridIndexTrackingTable().getCardMappedToGivenGridPosition(BCardzone, BGridIndex);
                if (!AttackedCard.getNameID().equals(msg[5]))
                    throw new IllegalArgumentException("Data inconsistency");
            }
            world.setWorldFlag(WorldFlags.BlockerSelectMode);
            return;
        }

        if (splitdirective[0].equals(DirectiveHeader.SetTmpSpreadingInst) && (splitdirective.length > 2)) {
            S = WorldUpdateoffTurnState.S7;
            return;
        }

        if (splitdirective[0].equals(DirectiveHeader.PassControl) && (splitdirective.length > 2)) {
            S = WorldUpdateoffTurnState.S8;
            String PassInstructionStr = splitdirective[1];
            InstructionSet PassInst = new InstructionSet(PassInstructionStr);
            world.getInstructionHandler().setCardAndInstruction(null, PassInst);
            if (world.getEventLog().getRecording() == true)
                throw new IllegalArgumentException("I am not expecting this to be ON at this point");
            world.getEventLog().setRecording(true);
            return;
        }

        if (splitdirective[0].equals(DirectiveHeader.SeeOpponentPassedCards) && (splitdirective.length > 2)) {
            S = WorldUpdateoffTurnState.S9;
            String[] eventString = splitdirective[1].split("#");
            ArrayList<Cards> TempZone = world.getMaze().getZoneList().get(6).getZoneArray();
            TempZone.clear();

            for (int i =0; i < eventString.length; i++) {
                String[] eventField = eventString[i].split(" ");

                if (eventField.length != 3)
                    throw new IllegalArgumentException("Invalid see opponent card info");

                int Cardzone = Integer.parseInt(eventField[0]);
                int GridIndex = Integer.parseInt(eventField[1]);

                if (Cardzone == 4 || Cardzone == 5 || Cardzone == 11 || Cardzone == 12)
                    throw new IllegalArgumentException("Invalid zone to show info");

                InactiveCard card = (InactiveCard) world.getGridIndexTrackingTable().getCardMappedToGivenGridPosition(Cardzone, GridIndex);
                if (!card.getNameID().equals(eventField[2]))
                    throw new IllegalArgumentException("Data inconsistency");
                TempZone.add(card);
            }
            return;
        }

        if (splitdirective[0].equals(DirectiveHeader.SendDeckShuffleUpdate) && (splitdirective.length > 2)){
            S = WorldUpdateoffTurnState.S10;
            return;
        }
    }

    private void ApplyEvents() {
        String[] eventString = splitdirective[1].split("#");

        for (int i =0; i < eventString.length; i++) {
            ActUtil.ApplyEventsInt(eventString[i], world);
        }

        SetUnsetUtil.PerformCleanUpForMovedCard(world);

        S = WorldUpdateoffTurnState.SY;
    }

    private void ApplyTappedCardInfo() {
        String[] eventString = splitdirective[1].split("#");

        for (int i =0; i < eventString.length; i++) {
            String[] eventField = eventString[i].split(" ");

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

        S = WorldUpdateoffTurnState.SY;
    }

    private void SetCleanUpInst() {
        String[] msg = splitdirective[1].split("#");

        for (int i = 0 ; i < msg.length; i++) {
            String[] msgField = msg[i].split("%");

            if (msgField.length != 2)
                throw new  IllegalArgumentException("Invalid Set clean directive 1");

            String[] msgInfo = msgField[0].split(" ");
            if (msgInfo.length != 4)
                throw new  IllegalArgumentException("Invalid Set clean directive 2");
            int Cardzone = Integer.parseInt(msgInfo[0]);
            if (!(Cardzone == 0 || Cardzone == 3 || Cardzone == 7 || Cardzone == 10))
                return;
            int GridIndex = Integer.parseInt(msgInfo[1]);
            int placementLocation = Integer.parseInt(msgInfo[3]);

            InactiveCard card = (InactiveCard) world.getGridIndexTrackingTable().getCardMappedToGivenGridPosition(Cardzone, GridIndex);
            if (!card.getNameID().equals(msgInfo[2]))
                throw new IllegalArgumentException("Data inconsistency");

            if (placementLocation == 1) {
                InstructionSet instruction = new InstructionSet(msgField[1]);
                if (Cardzone >= 0 && Cardzone <= 3) {
                    card.AddTemporaryPreCleanup(instruction);
                } else {
                    card.AddTemporaryPostCleanup(instruction);
                }
            }

            if (placementLocation == 2) {
                InstructionSet instruction = new InstructionSet(msgField[1]);
                if (Cardzone >= 0 && Cardzone <= 3) {
                    card.AddTemporaryPostCleanup(instruction);
                } else {
                    card.AddTemporaryPreCleanup(instruction);
                }
            }
        }

        S = WorldUpdateoffTurnState.S1;
    }

    private void EvolutionHandler() {
        String[] msg = splitdirective[1].split(" ");

        if (msg.length != 6)
            throw new IllegalArgumentException("Invalid evolution directive");

        int ECardzone = Integer.parseInt(msg[0]);
        int EGridIndex = Integer.parseInt(msg[1]);

        int BCardzone = Integer.parseInt(msg[3]);
        int BGridIndex = Integer.parseInt(msg[4]);

        InactiveCard Ecard = (InactiveCard) world.getGridIndexTrackingTable().getCardMappedToGivenGridPosition(ECardzone, EGridIndex);
        if (!Ecard.getNameID().equals(msg[2]))
            throw new IllegalArgumentException("Data inconsistency");

        InactiveCard Bcard = (InactiveCard) world.getGridIndexTrackingTable().getCardMappedToGivenGridPosition(BCardzone, BGridIndex);
        if (!Bcard.getNameID().equals(msg[5]))
            throw new IllegalArgumentException("Data inconsistency");

        ArrayList<InstructionSet> CleanUpInst = Bcard.getCrossInstructionForTheInstructionID(InstructionID.CleanUp);
        world.getInstructionIteratorHandler().setCard(Bcard);
        world.getInstructionIteratorHandler().setInstructions(CleanUpInst);
        ActUtil.EvolveCreature(Ecard,Bcard, world);

        S = WorldUpdateoffTurnState.SY;
    }

    private void SelectBlocker() {
        ArrayList<Cards> CollectedCardList = world.getMaze().getZoneList().get(6).getZoneArray();
        ArrayList<Cards> MyCreatures = world.getMaze().getZoneList().get(0).getZoneArray();

        if (CollectedCardList.size() == 1) {
            if (UIUtil.TouchedAcceptButton(world)) {
                InactiveCard card = (InactiveCard) CollectedCardList.get(0);
                int zone = card.GridPosition().getZone() + 7;
                String msg = zone + " " + card.GridPosition().getGridIndex() + " " +
                        card.getNameID();
                NetworkUtil.sendDirectiveUpdates(world, DirectiveHeader.SendBlocker, msg, null);
                S = WorldUpdateoffTurnState.S1;
                world.clearWorldFlag(WorldFlags.BlockerSelectMode);
                AttackingCard = null;
                AttackedCard = null;
                return;
            }
        }

        if (UIUtil.TouchedDeclineButton(world)){
            NetworkUtil.sendDirectiveUpdates(world, DirectiveHeader.SendBlocker, null, null);
            S = WorldUpdateoffTurnState.S1;
            world.clearWorldFlag(WorldFlags.BlockerSelectMode);
            AttackingCard = null;
            AttackedCard = null;
            return;
        }

        InactiveCard SelectedCard = (InactiveCard) UIUtil.GetTouchedTrackCard(world);

        if (SelectedCard == null)
            return;
        if (SelectedCard.GridPosition().getZone() != 0)
            return;

        if (CollectedCardList.contains(SelectedCard)) {
            UIUtil.TrackSelectedCardsWhenUserIsChoosing(CollectedCardList, MyCreatures, SelectedCard);
            return;
        }

        if (CollectedCardList.size() != 0)
            return;

        if (AttackedCard != null && SelectedCard == AttackedCard)
            return;

        if (!GetUtil.IsBlocker(SelectedCard, AttackingCard))
            return;

        UIUtil.TrackSelectedCardsWhenUserIsChoosing(CollectedCardList, MyCreatures, SelectedCard);
    }

    private void SetTemporarySpreadingInst() {
        String[] msg = splitdirective[1].split("#");

        for (int i = 0 ; i < msg.length; i++) {
            String[] msgField = msg[i].split("%");

            if (msgField.length != 2)
                throw new  IllegalArgumentException("Invalid Set spreadingInst directive 1");

            String[] msgInfo = msgField[0].split(" ");
            if (msgInfo.length != 3)
                throw new  IllegalArgumentException("Invalid Set spreadingInst directive 2");
            int Cardzone = Integer.parseInt(msgInfo[0]);
            int GridIndex = Integer.parseInt(msgInfo[1]);

            InactiveCard card = (InactiveCard) world.getGridIndexTrackingTable().getCardMappedToGivenGridPosition(Cardzone, GridIndex);
            if (!card.getNameID().equals(msgInfo[2]))
                throw new IllegalArgumentException("Data inconsistency");

            InstructionSet instruction = new InstructionSet(msgField[1]);
            card.AddTemporarySpreadingInst(instruction);
        }

        S = WorldUpdateoffTurnState.S1;
    }

    private void ExecutePassInstruction() {
        if (world.getInstructionHandler().execute()) {
            SetUnsetUtil.PerformCleanUpForMovedCard(world);
            //Send event log
            String msg = world.getEventLog().getAndClearEvents();
            NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
            NetworkUtil.sendDirectiveUpdates(world, DirectiveHeader.PassControl, null, null);
            world.getEventLog().setRecording(false);
            S = WorldUpdateoffTurnState.SY;
        }
    }

    private void SeeOpponentCards() {
        Cards card;
        ArrayList<Cards> TempZone = world.getMaze().getZoneList().get(6).getZoneArray();
        int size = TempZone.size() - 1;
        world.setWorldFlag(WorldFlags.DisplayInfoUserSelect);
        if (UIUtil.TouchedInfoTabBackButton(world) || (size == -1)) {
            world.clearWorldFlag(WorldFlags.DisplayInfoUserSelect);
            TempZone.clear();
            S = WorldUpdateoffTurnState.S1;
            NetworkUtil.sendDirectiveUpdates(world, DirectiveHeader.SeeOpponentPassedCards, null, null);
            return;
        }
        if (UIUtil.TouchedAcceptButton(world)) {
            card = TempZone.remove(size);
            TempZone.add(0, card);
        }

        if (UIUtil.TouchedDeclineButton(world)) {
            card = TempZone.remove(0);
            TempZone.add(card);
        }
    }

    private void UpdateDeckOrder() {
        String[] msg = splitdirective[1].split("#");
        Zone zone = world.getMaze().getZoneList().get(12);
        int size = zone.zoneSize();
        ArrayList<Cards> TempZone = world.getMaze().getZoneList().get(6).getZoneArray();
        TempZone.clear();
        Cards card;

        if (!(zone.zoneSize() > 0)) {
            S = WorldUpdateoffTurnState.S1;
            return;
        }

        for (int i = 0 ; i < msg.length; i++) {
            for (int j = 0; j <zone.zoneSize(); j++) {
                card = zone.getZoneArray().get(j);
                if (card.getNameID().equals(msg[i])) {
                    TempZone.add(card);
                    zone.getZoneArray().remove(card);
                    break;
                }
            }
        }

        if (size != TempZone.size())
            throw new IllegalArgumentException("Something went wrong during reordering of Deck");

        zone.getZoneArray().clear();

        for (int i = 0; i < TempZone.size(); i++) {
            zone.getZoneArray().add(TempZone.get(i));
        }

        S = WorldUpdateoffTurnState.S1;
    }

    private void ResumeTurnControl() {
        SetUnsetUtil.SpreadingFlagAttr(world);
        world.getInstructionHandler().setCardAndInstruction(null, NotYetSpreadCleanup);
        world.getInstructionHandler().execute();
        world.setTurn(true);
        world.clearWorldFlag(WorldFlags.CantDrawCard);
        world.clearWorldFlag(WorldFlags.CantAddToMana);
        S = WorldUpdateoffTurnState.S1;
    }

    private void FlagSpreadingUpdate() {
        SetUnsetUtil.SpreadingFlagAttr(world);
        world.getInstructionHandler().setCardAndInstruction(null, NotYetSpreadCleanup);
        world.getInstructionHandler().execute();
        S = WorldUpdateoffTurnState.S1;
    }
}
