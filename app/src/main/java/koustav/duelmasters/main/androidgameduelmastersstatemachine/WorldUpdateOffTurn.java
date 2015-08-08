package koustav.duelmasters.main.androidgameduelmastersstatemachine;


import java.util.ArrayList;

import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionSet;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.InactiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.World;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.WorldFlags;
import koustav.duelmasters.main.androidgameduelmastersnetworkmodule.DirectiveHeader;
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
    }

    private void ApplyEvents() {
        String[] eventString = splitdirective[1].split("#");

        for (int i =0; i < eventString.length; i++) {
            ApplyEventsInt(eventString[i]);
        }

        S = WorldUpdateoffTurnState.SY;
    }

    private void ApplyEventsInt(String event){
        String[] eventField = event.split(" ");

        if (eventField.length != 8)
            throw new IllegalArgumentException("Invalid eventLog");


        int Cardzone = Integer.parseInt(eventField[0]);
        int GridIndex = Integer.parseInt(eventField[1]);
        boolean move = eventField[3].equals("0") ? false : true;
        boolean set = eventField[6].equals("0") ? false : true;
        if (Cardzone != 4 && Cardzone != 5 && Cardzone != 11 && Cardzone != 12) {
            InactiveCard card = (InactiveCard) world.getGridIndexTrackingTable().getCardMappedToGivenGridPosition(Cardzone,GridIndex);
            if (!card.getNameID().equals(eventField[2]))
                throw new IllegalArgumentException("Data inconsistency");

            if (move) {
                String moveInstruction = InstSetUtil.GenerateSelfChangeZoneInstruction(Integer.parseInt(eventField[4]));
                InstructionSet instruction = new InstructionSet(moveInstruction);
                world.getInstructionHandler().setCardAndInstruction(card, instruction);
                world.getInstructionHandler().execute();
            }

            if (!eventField[5].equals("0")) {
                if (set) {
                    String setAttrInstruction = InstSetUtil.GenerateSelfSetAttributeInstruction(eventField[5], Integer.parseInt(eventField[7]));
                    InstructionSet instruction = new InstructionSet(setAttrInstruction);
                    world.getInstructionHandler().setCardAndInstruction(card, instruction);
                    world.getInstructionHandler().execute();
                } else {
                    String unsetAttrInstruction = InstSetUtil.GenerateSelfCleanUpAttributeInstruction(eventField[5], Integer.parseInt(eventField[7]));
                    InstructionSet instruction = new InstructionSet(unsetAttrInstruction);
                    world.getInstructionHandler().setCardAndInstruction(card, instruction);
                    world.getInstructionHandler().execute();
                }
            }
        } else {
            if (move) {
                int ActionZone = 0;
                if (Cardzone == 4 || Cardzone == 5)
                    ActionZone = (int) Math.pow(10, Cardzone);
                if (Cardzone == 11 || Cardzone == 12)
                    ActionZone = 2 * ((int)Math.pow(10, Cardzone - 7));
                String moveInstruction = InstSetUtil.GenerateChangeZoneInstructionBasedOnNameId(ActionZone, eventField[2], Integer.parseInt(eventField[4]));
                InstructionSet instruction = new InstructionSet(moveInstruction);
                world.getInstructionHandler().setCardAndInstruction(null, instruction);
                world.getInstructionHandler().execute();
            }

            if (!eventField[5].equals("0")) {
                throw new IllegalArgumentException("Invalid event");
            }
        }
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
            String[] msgField = msg[i].split("$");

            if (msgField.length != 2)
                throw new  IllegalArgumentException("Invalid Set clean directive 1");

            String[] msgInfo = msgField[0].split(" ");
            if (msgInfo.length != 4)
                throw new  IllegalArgumentException("Invalid Set clean directive 2");
            int Cardzone = Integer.parseInt(msgInfo[0]);
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
