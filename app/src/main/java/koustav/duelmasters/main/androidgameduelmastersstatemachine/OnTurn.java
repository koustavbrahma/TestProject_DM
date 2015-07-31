package koustav.duelmasters.main.androidgameduelmastersstatemachine;

import java.util.ArrayList;

import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionID;
import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionSet;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.InactiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.ActiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.TypeOfCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.World;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.WorldFlags;
import koustav.duelmasters.main.androidgameduelmastersnetworkmodule.DirectiveHeader;
import koustav.duelmasters.main.androidgameduelmastersutil.GetUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.InstSetUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.NetworkUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.SetUnsetUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.UIUtil;

/**
 * Created by Koustav on 3/28/2015.
 */
public class OnTurn {
    enum OnTurnState {
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
        S11,
        S12,
        S13,
        S14,
        S15,
        S16,
        SX,
    }
    OnTurnState S;
    World world;
    InstructionSet NotYetSpreadCleanup;


    public OnTurn(World world) {
        this.world = world;
        S = OnTurnState.S1;
        String instruction = InstSetUtil.GenerateAttributeCleanUpInstruction(3333, "NotYetSpread", 1);
        NotYetSpreadCleanup = new InstructionSet(instruction);
    }
/*
 Main API which controls the states and calls other API according to 'S' value
 */
    public boolean update(){
        boolean status = false;
        if (S == OnTurnState.S1)
            status = IdealOnTurn();
        if (S == OnTurnState.S2)
            SummonOrCastUpdate();
        if (S == OnTurnState.S3)
            AttackUpdate();
        if (S == OnTurnState.S4)
            SummonUpdate();
        if (S == OnTurnState.S5)
            CastUpdate();
        if (S == OnTurnState.S6)
            ManaUpdate();
        if (S == OnTurnState.S7)
            ShowInfoUpdate();
        if (S == OnTurnState.S8)
            TapAbilityUpdate();
        if (S == OnTurnState.S9)
            DrawCardUpdate();
        if (S == OnTurnState.S10)
            ShieldUpdate();
        if (S == OnTurnState.S11)
            PostIfAttackUpdate();
        if (S == OnTurnState.S12)
            BlockerUpdate();
        if (S == OnTurnState.S13)
            CreatureBattleUpdate();
        if (S == OnTurnState.S14)
            ShieldBreakingUpdate();
        if (S == OnTurnState.S15)
            PostIfUnBlockedUpdate();
        if (S == OnTurnState.SX)
            FlagSpreadingUpdate();
        return status;
    }
/*
 This is the Ideal state when User make decision what he/she is going to do next (attack,summon,cast,draw card etc.)
 S1
 */
    private boolean IdealOnTurn() {
        if (UIUtil.TouchedSkippedButton(world)) {
            if (!NetworkUtil.sendDirectiveUpdates(world, DirectiveHeader.EndOfTurn, null, null)) {
                //handle the case when socket is closed
            }
            return true;
        }

        if (UIUtil.WorldFetchCard(world)) {
            return false;
        }

        if ((world.getFetchCard() != null) && UIUtil.TouchedInfoTabEnterButton(world)) {
            this.S = OnTurnState.S7;
            world.setWorldFlag(WorldFlags.DisplayInfo);
            return false;
        }

        if (UIUtil.TouchedLeftPlayButton(world)) {
            InactiveCard card = (InactiveCard) world.getFetchCard();
            if (card == null) {
                return false;
            }
            int zone = card.GridPosition().getZone();
            if (zone != 0 && zone != 3) {
                return false;
            }

            if (zone == 0) {
                if (!GetUtil.IsTapped(card)) {
                    if (GetUtil.IsAllowedToAttack(card, world)) {
                        this.S = OnTurnState.S3;
                        world.getMaze().getZoneList().get(6).getZoneArray().clear();
                        world.setWorldFlag(WorldFlags.AttackSelectMode);
                    }
                } else if (GetUtil.EachTurnIfAble(card)) {
                    if (GetUtil.IsAllowedToAttack(card, world)) {
                        this.S = OnTurnState.S3;
                        world.getMaze().getZoneList().get(6).getZoneArray().clear();
                        world.setWorldFlag(WorldFlags.AttackSelectMode);
                    }
                }

                return false;
            }

            if (zone == 3) {
                if (GetUtil.IsAllowedToSummonOrCast(card, world)) {
                    this.S = OnTurnState.S2;
                    world.getMaze().getZoneList().get(6).getZoneArray().clear();
                    world.setWorldFlag(WorldFlags.ManaSelectMode);
                    world.getEventLog().setRecording(true);
                }

                return false;
            }
        }

        if (UIUtil.TouchedRightPlayButton(world)) {
            InactiveCard card = (InactiveCard) world.getFetchCard();
            if (card == null){
                return false;
            }
            int zone = card.GridPosition().getZone();
            if (zone !=0 && zone != 3){
                return false;
            }

            if (zone == 0) {
                if (!GetUtil.IsTapped(card)) {
                    if (GetUtil.IsHasTapAbility(card) && !GetUtil.CantUseTapAbility(card)  &&
                            !(GetUtil.NewSummonedCreature(card) && !GetUtil.NoSummoningSickness(card))) {
                        ActiveCard Acard = (ActiveCard) card;
                        ArrayList<InstructionSet> instructions = Acard.getPrimaryInstructionForTheInstructionID(InstructionID.TapAbility);
                        world.getInstructionIteratorHandler().setCard(Acard);
                        world.getInstructionIteratorHandler().setInstructions(instructions);
                        world.getEventLog().setRecording(true);
                        SetUnsetUtil.SetTappedAttr(Acard);
                        world.getEventLog().registerEvent(Acard, false, 0 , "Tapped", true ,1);
                        this.S = OnTurnState.S8;
                        return false;
                    }
                } else {
                    return false;
                }
            }

            if (zone == 3) {
                if (!world.getWorldFlag(WorldFlags.CantAddToMana)) {
                    this.S = OnTurnState.S6;
                    world.getEventLog().setRecording(true);
                }

                return false;
            }
        }

        if (UIUtil.TouchedDrawCardButton(world)) {
            if ((world.getMaze().getZoneList().get(5).zoneSize()> 0) &&
                    !world.getWorldFlag(WorldFlags.CantDrawCard)) {
                this.S = OnTurnState.S9;
                world.getEventLog().setRecording(true);
            }

            return false;
        }

        return false;
    }
/*
 This API is called when user decides to attack. Here user makes decision what he/she must attack (creature, shield or opponent)
 S3
 */
    private void AttackUpdate() {
        ActiveCard card = (ActiveCard) world.getFetchCard();
        ArrayList<Cards> CollectedCardList = world.getMaze().getZoneList().get(6).getZoneArray();
        ArrayList<Cards> OpponentCreatures = world.getMaze().getZoneList().get(7).getZoneArray();

        if (CollectedCardList.size() == 1) {
            if (UIUtil.TouchedAcceptButton(world)) {
                ArrayList<InstructionSet> IfAttackInsts =
                        card.getPrimaryInstructionForTheInstructionID(InstructionID.IfAttacked);
                world.getInstructionIteratorHandler().setCard(card);
                world.getInstructionIteratorHandler().setInstructions(IfAttackInsts);
                this.S = OnTurnState.S11;
                world.getEventLog().setRecording(true);
                return;
            }
        }

        if (UIUtil.TouchedAttackShieldOrPlayerButton(world)) {
            if (world.getMaze().getZoneList().get(9).zoneSize() > 0) {
                if (GetUtil.CanAttackShield(card) || GetUtil.IgnoreAnyAttackPrevent(card)) {
                    world.getMaze().getZoneList().get(6).getZoneArray().clear();
                    world.setWorldFlag(WorldFlags.ShieldSelectMode);
                    this.S = OnTurnState.S10;
                    world.clearWorldFlag(WorldFlags.AttackSelectMode);
                    return;
                }
            } else {
                if (GetUtil.CanAttackPlayer(card) || GetUtil.IgnoreAnyAttackPrevent(card)) {

                }
            }
            return;
        }

        if (UIUtil.TouchedDeclineButton(world)){
            this.S = OnTurnState.S1;
            world.clearWorldFlag(WorldFlags.AttackSelectMode);
            CollectedCardList.clear();
            return;
        }

        InactiveCard SelectedCard = (InactiveCard) UIUtil.GetTouchedTrackCard(world);

        if (SelectedCard == null)
            return;
        if (SelectedCard.GridPosition().getZone() != 7)
            return;

        if (CollectedCardList.contains(SelectedCard)) {
            UIUtil.TrackSelectedCardsWhenUserIsChoosing(CollectedCardList, OpponentCreatures, SelectedCard);
            return;
        }

        if (CollectedCardList.size() != 0)
            return;

        if (!GetUtil.CanAttackCard(card, SelectedCard))
            return;

        UIUtil.TrackSelectedCardsWhenUserIsChoosing(CollectedCardList, OpponentCreatures, SelectedCard);
    }
/*
 Wait for opponent to decide whether he/she will block or not
 S12
 */
    private void BlockerUpdate() {
        if (world.getWorldFlag(WorldFlags.AttackSelectMode) && world.getWorldFlag(WorldFlags.ShieldSelectMode))
            throw new IllegalArgumentException("Both Attackselect mode and ShieldSelect mode is on");

        if (world.getWorldFlag(WorldFlags.AttackSelectMode))
            S = OnTurnState.S13;
        if (world.getWorldFlag(WorldFlags.ShieldSelectMode))
            S = OnTurnState.S14;
    }
/*
 If user decides to attack creature or if get blocked, this API will be called to evaluate the result of
 the battle.
 S13
 */
    private void CreatureBattleUpdate() {
        ActiveCard AttackingCard = (ActiveCard) world.getFetchCard();
        ArrayList<Cards> CollectedCardList = world.getMaze().getZoneList().get(6).getZoneArray();
        if (CollectedCardList.size() != 1)
            throw new IllegalArgumentException("Something went wrong with the creature to attack");
        InactiveCard AttackedCard = (InactiveCard) CollectedCardList.get(0);
        CollectedCardList.clear();
        int AttackResult = GetUtil.AttackEvaluation(AttackingCard, AttackedCard);
        CollectedCardList.add(AttackingCard);
        if (!GetUtil.IsTapped(AttackingCard)) {
            SetUnsetUtil.SetTappedAttr(AttackingCard);
            world.getEventLog().registerEvent(AttackingCard, false, 0 , "Tapped", true ,1);
        }

        if (AttackResult == 1) {
            ArrayList<InstructionSet> CleanUpInst = AttackedCard.getCrossInstructionForTheInstructionID(InstructionID.CleanUp);
            world.getInstructionIteratorHandler().setCard(AttackedCard);
            world.getInstructionIteratorHandler().setInstructions(CleanUpInst);
            while (!world.getInstructionIteratorHandler().update());
            if (GetUtil.MaskDestroyDstVal(AttackedCard) > 0) {
                String DestroyDstInst = InstSetUtil.GenerateSelfChangeZoneInstruction(GetUtil.MaskDestroyDstVal(AttackedCard) -1);
                InstructionSet instruction = new InstructionSet(DestroyDstInst);
                world.getInstructionHandler().setCardAndInstruction(AttackedCard, instruction);
                world.getInstructionHandler().execute();
            } else {
                ArrayList<InstructionSet> instructionCollection= AttackedCard.getCrossInstructionForTheInstructionID(InstructionID.DestroyDst);
                if (instructionCollection != null) {
                    InstructionSet instruction = instructionCollection.get(0);
                    world.getInstructionHandler().setCardAndInstruction(AttackedCard, instruction);
                    world.getInstructionHandler().execute();
                } else {
                    String DestroyDstInst = InstSetUtil.GenerateSelfChangeZoneInstruction(4);
                    InstructionSet instruction2 = new InstructionSet(DestroyDstInst);
                    world.getInstructionHandler().setCardAndInstruction(AttackedCard, instruction2);
                    world.getInstructionHandler().execute();
                }
            }
        }

        if (AttackResult == 0) {
            ArrayList<InstructionSet> CleanUpInst = AttackedCard.getCrossInstructionForTheInstructionID(InstructionID.CleanUp);
            world.getInstructionIteratorHandler().setCard(AttackedCard);
            world.getInstructionIteratorHandler().setInstructions(CleanUpInst);
            while (!world.getInstructionIteratorHandler().update());
            if (GetUtil.MaskDestroyDstVal(AttackedCard) > 0) {
                String DestroyDstInst = InstSetUtil.GenerateSelfChangeZoneInstruction(GetUtil.MaskDestroyDstVal(AttackedCard) -1);
                InstructionSet instruction = new InstructionSet(DestroyDstInst);
                world.getInstructionHandler().setCardAndInstruction(AttackedCard, instruction);
                world.getInstructionHandler().execute();
            } else {
                ArrayList<InstructionSet> instructionCollection= AttackedCard.getCrossInstructionForTheInstructionID(InstructionID.DestroyDst);
                if (instructionCollection != null) {
                    InstructionSet instruction = instructionCollection.get(0);
                    world.getInstructionHandler().setCardAndInstruction(AttackedCard, instruction);
                    world.getInstructionHandler().execute();
                } else {
                    String DestroyDstInst = InstSetUtil.GenerateSelfChangeZoneInstruction(4);
                    InstructionSet instruction2 = new InstructionSet(DestroyDstInst);
                    world.getInstructionHandler().setCardAndInstruction(AttackedCard, instruction2);
                    world.getInstructionHandler().execute();
                }
            }

            CleanUpInst = AttackingCard.getCrossInstructionForTheInstructionID(InstructionID.CleanUp);
            world.getInstructionIteratorHandler().setCard(AttackingCard);
            world.getInstructionIteratorHandler().setInstructions(CleanUpInst);
            while (!world.getInstructionIteratorHandler().update());
            if (GetUtil.MaskDestroyDstVal(AttackingCard) > 0) {
                String DestroyDstInst = InstSetUtil.GenerateSelfChangeZoneInstruction(GetUtil.MaskDestroyDstVal(AttackingCard) -1);
                InstructionSet instruction = new InstructionSet(DestroyDstInst);
                world.getInstructionHandler().setCardAndInstruction(AttackingCard, instruction);
                world.getInstructionHandler().execute();
            } else {
                ArrayList<InstructionSet> instructionCollection= AttackedCard.getCrossInstructionForTheInstructionID(InstructionID.DestroyDst);
                if (instructionCollection != null) {
                    InstructionSet instruction = instructionCollection.get(0);
                    world.getInstructionHandler().setCardAndInstruction(AttackingCard, instruction);
                    world.getInstructionHandler().execute();
                } else {
                    String DestroyDstInst = InstSetUtil.GenerateSelfChangeZoneInstruction(4);
                    InstructionSet instruction2 = new InstructionSet(DestroyDstInst);
                    world.getInstructionHandler().setCardAndInstruction(AttackingCard, instruction2);
                    world.getInstructionHandler().execute();
                }
            }
        }

        if (AttackResult == -1) {
            ArrayList<InstructionSet> CleanUpInst = AttackingCard.getCrossInstructionForTheInstructionID(InstructionID.CleanUp);
            world.getInstructionIteratorHandler().setCard(AttackingCard);
            world.getInstructionIteratorHandler().setInstructions(CleanUpInst);
            while (!world.getInstructionIteratorHandler().update());
            if (GetUtil.MaskDestroyDstVal(AttackingCard) > 0) {
                String DestroyDstInst = InstSetUtil.GenerateSelfChangeZoneInstruction(GetUtil.MaskDestroyDstVal(AttackingCard) -1);
                InstructionSet instruction = new InstructionSet(DestroyDstInst);
                world.getInstructionHandler().setCardAndInstruction(AttackingCard, instruction);
                world.getInstructionHandler().execute();
            } else {
                ArrayList<InstructionSet> instructionCollection= AttackedCard.getCrossInstructionForTheInstructionID(InstructionID.DestroyDst);
                if (instructionCollection != null) {
                    InstructionSet instruction = instructionCollection.get(0);
                    world.getInstructionHandler().setCardAndInstruction(AttackingCard, instruction);
                    world.getInstructionHandler().execute();
                } else {
                    String DestroyDstInst = InstSetUtil.GenerateSelfChangeZoneInstruction(4);
                    InstructionSet instruction2 = new InstructionSet(DestroyDstInst);
                    world.getInstructionHandler().setCardAndInstruction(AttackingCard, instruction2);
                    world.getInstructionHandler().execute();
                }
            }
        }

        //send Eventlog
        String msg = world.getEventLog().getAndClearEvents();
        NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
        if (!world.getWorldFlag(WorldFlags.WasBlocked)) {
            ArrayList<InstructionSet> IfUnBlocked =
                    ((ActiveCard) CollectedCardList.get(0)).getPrimaryInstructionForTheInstructionID(InstructionID.IfUnblocked);
            world.getInstructionIteratorHandler().setCard((InactiveCard) world.getFetchCard());
            world.getInstructionIteratorHandler().setInstructions(IfUnBlocked);
            S = OnTurnState.S15;
        } else {
            world.clearWorldFlag(WorldFlags.WasBlocked);
            world.getEventLog().setRecording(false);
            S = OnTurnState.SX;
        }
        CollectedCardList.clear();
        world.clearWorldFlag(WorldFlags.AttackSelectMode);
    }
/*
 After attack this is were If attack ability is handled.
 S11
 */
    private void PostIfAttackUpdate() {
        if (world.getInstructionIteratorHandler().update()) {
            S = OnTurnState.S12;
            //send Eventlog
            String msg = world.getEventLog().getAndClearEvents();
            NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
        }
    }
/*
 After attack this is were if unblocked is handled.
 S15
 */
    private void PostIfUnBlockedUpdate() {
        if (world.getInstructionIteratorHandler().update()) {
            world.getEventLog().setRecording(false);
            S = OnTurnState.SX;
            //send Eventlog
            String msg = world.getEventLog().getAndClearEvents();
            NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
        }
    }
/*
 If user attacks shield this API is called to select the shield.
 S10
 */
    private void ShieldUpdate() {
        ActiveCard card = (ActiveCard) world.getFetchCard();
        ArrayList<Cards> CollectedCardList = world.getMaze().getZoneList().get(6).getZoneArray();
        ArrayList<Cards> ShieldCards = world.getMaze().getZoneList().get(9).getZoneArray();
        int NumberOfBreakingShield = (GetUtil.Breaker(card) > ShieldCards.size()) ? ShieldCards.size()
                : GetUtil.Breaker(card);
        if (CollectedCardList.size() == NumberOfBreakingShield) {
            if (UIUtil.TouchedAcceptButton(world)) {
                ArrayList<InstructionSet> IfAttackInsts =
                        card.getPrimaryInstructionForTheInstructionID(InstructionID.IfAttacked);
                world.getInstructionIteratorHandler().setCard(card);
                world.getInstructionIteratorHandler().setInstructions(IfAttackInsts);
                this.S = OnTurnState.S11;
                world.getEventLog().setRecording(true);
                return;
            }
        }

        if (UIUtil.TouchedDeclineButton(world)){
            this.S = OnTurnState.S3;
            world.clearWorldFlag(WorldFlags.ShieldSelectMode);
            world.setWorldFlag(WorldFlags.AttackSelectMode);
            CollectedCardList.clear();
            return;
        }

        InactiveCard SelectedCard = (InactiveCard) UIUtil.GetTouchedTrackCard(world);

        if (SelectedCard == null)
            return;
        if (SelectedCard.GridPosition().getZone() != 9)
            return;

        if (CollectedCardList.contains(SelectedCard)) {
            UIUtil.TrackSelectedCardsWhenUserIsChoosing(CollectedCardList, ShieldCards, SelectedCard);
            return;
        }

        if (!(CollectedCardList.size() < NumberOfBreakingShield))
            return;

        UIUtil.TrackSelectedCardsWhenUserIsChoosing(CollectedCardList, ShieldCards, SelectedCard);
    }
/*
 This API updates the breaking of the shield and detects if there is any shield trigger.
 S14
 */
    private void ShieldBreakingUpdate() {
        ActiveCard card = (ActiveCard) world.getFetchCard();
        ArrayList<Cards> CollectedCardList = world.getMaze().getZoneList().get(6).getZoneArray();
        if (!GetUtil.IsTapped(card)) {
            SetUnsetUtil.SetTappedAttr(card);
            world.getEventLog().registerEvent(card, false, 0 , "Tapped", true ,1);
        }
        if (GetUtil.IsSmashShieldBreaker(card)) {
            String DestroyShieldInstruction = InstSetUtil.GenerateSelfChangeZoneInstruction(4);
            InstructionSet instruction = new InstructionSet(DestroyShieldInstruction);
            for (int i = 0; i < CollectedCardList.size(); i++) {
                InactiveCard shield = (InactiveCard) CollectedCardList.get(i);
                world.getInstructionHandler().setCardAndInstruction(shield, instruction);
                world.getInstructionHandler().execute();
            }
            //send eventlog
            String msg = world.getEventLog().getAndClearEvents();
            NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
            world.clearWorldFlag(WorldFlags.ShieldSelectMode);
            if (!world.getWorldFlag(WorldFlags.WasBlocked)) {
                ArrayList<InstructionSet> IfUnBlocked =
                        card.getPrimaryInstructionForTheInstructionID(InstructionID.IfUnblocked);
                world.getInstructionIteratorHandler().setCard(card);
                world.getInstructionIteratorHandler().setInstructions(IfUnBlocked);
                S = OnTurnState.S15;
            } else {
                world.clearWorldFlag(WorldFlags.WasBlocked);
                S = OnTurnState.SX;
                world.getEventLog().setRecording(false);
            }
            CollectedCardList.clear();
            return;
        }

        boolean isShieldTrigger = false;
        String BreakShieldInstruction = InstSetUtil.GenerateSelfChangeZoneInstruction(3);
        InstructionSet instruction = new InstructionSet(BreakShieldInstruction);
        ArrayList<Cards> TempList = new ArrayList<Cards>();
        for (int i = 0; i < CollectedCardList.size(); i++) {
            InactiveCard shield = (InactiveCard) CollectedCardList.get(i);
            if (GetUtil.IsShieldTrigger(shield)) {
                isShieldTrigger = true;
            }
            world.getInstructionHandler().setCardAndInstruction(shield, instruction);
            world.getInstructionHandler().execute();
            TempList.add(world.getInstructionHandler().getCurrentCard());
        }

        //sendeventlog
        String msg = world.getEventLog().getAndClearEvents();
        NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
        if (TempList.size() != CollectedCardList.size())
            throw new IllegalArgumentException("While Breaking shield something went wrong ");

        CollectedCardList.clear();
        for (int i=0 ; i < TempList.size(); i++) {
            CollectedCardList.add(TempList.get(i));
        }
        TempList.clear();
        if (!isShieldTrigger) {
            world.clearWorldFlag(WorldFlags.ShieldSelectMode);
            if (!world.getWorldFlag(WorldFlags.WasBlocked)) {
                ArrayList<InstructionSet> IfUnBlocked =
                        card.getPrimaryInstructionForTheInstructionID(InstructionID.IfUnblocked);
                world.getInstructionIteratorHandler().setCard(card);
                world.getInstructionIteratorHandler().setInstructions(IfUnBlocked);
                S = OnTurnState.S15;
            } else {
                world.clearWorldFlag(WorldFlags.WasBlocked);
                S = OnTurnState.SX;
                world.getEventLog().setRecording(false);
            }
            CollectedCardList.clear();
            return;
        }

        SetUnsetUtil.SpreadingFlagAttr(world);
        world.clearWorldFlag(WorldFlags.ShieldSelectMode);
        this.S = OnTurnState.S16;
        world.getEventLog().setRecording(false);
    }
/*
 This API is called when user decides to summon or cast card.
 S2
 */
    private void SummonOrCastUpdate() {
        ActiveCard SummoningOrCastCard = (ActiveCard) world.getFetchCard();
        ArrayList<Cards> CollectedCardList = world.getMaze().getZoneList().get(6).getZoneArray();
        ArrayList<Cards> ManaCards = world.getMaze().getZoneList().get(1).getZoneArray();

        if (CollectedCardList.size() == SummoningOrCastCard.getCost()) {
            if (UIUtil.TouchedAcceptButton(world)){
                for (int i = 0; i <CollectedCardList.size(); i++) {
                    InactiveCard tcard = (InactiveCard) CollectedCardList.get(i);
                    SetUnsetUtil.SetTappedAttr(tcard);
                    world.getEventLog().registerEvent(tcard, false, 0 , "Tapped", true ,1);
                }
                if (SummoningOrCastCard.getType() == TypeOfCard.Creature) {
                    boolean summonTapped = false;
                    if (GetUtil.SummonTapped(SummoningOrCastCard)) {
                        summonTapped = true;
                    }
                    String SummonCardInstruction = InstSetUtil.GenerateSelfChangeZoneInstruction(0);
                    InstructionSet instruction = new InstructionSet(SummonCardInstruction);
                    world.getInstructionHandler().setCardAndInstruction(SummoningOrCastCard, instruction);
                    world.getInstructionHandler().execute();
                    if (summonTapped) {
                        SetUnsetUtil.SetTappedAttr((InactiveCard) world.getFetchCard());
                        world.getEventLog().registerEvent(world.getFetchCard(), false, 0 , "Tapped", true ,1);
                    }
                    //sendeventlog
                    String msg = world.getEventLog().getAndClearEvents();
                    NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
                    if (SummoningOrCastCard.getPrimaryInstructionForTheInstructionID(InstructionID.SummonOrCastAbility) == null) {
                        this.S = OnTurnState.SX;
                        world.getEventLog().setRecording(false);
                        world.setFetchCard(null);
                    } else {
                        world.getInstructionIteratorHandler().setCard((InactiveCard) world.getFetchCard());
                        ArrayList<InstructionSet> instructions =
                                ((ActiveCard) world.getFetchCard()).getPrimaryInstructionForTheInstructionID(InstructionID.SummonOrCastAbility);
                        world.getInstructionIteratorHandler().setInstructions(instructions);
                        this.S = OnTurnState.S4;
                    }
                }

                if (SummoningOrCastCard.getType() == TypeOfCard.Spell) {
                    world.getInstructionIteratorHandler().setCard((InactiveCard) world.getFetchCard());
                    ArrayList<InstructionSet> instructions =
                            ((ActiveCard) world.getFetchCard()).getPrimaryInstructionForTheInstructionID(InstructionID.SummonOrCastAbility);
                    world.getInstructionIteratorHandler().setInstructions(instructions);
                    //sendeventlog
                    String msg = world.getEventLog().getAndClearEvents();
                    NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
                    this.S = OnTurnState.S5;
                }

                if (SummoningOrCastCard.getType() == TypeOfCard.Evolution) {
                    //sendEventlog
                    String msg = world.getEventLog().getAndClearEvents();
                    NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
                }

                world.clearWorldFlag(WorldFlags.ManaSelectMode);
                CollectedCardList.clear();
                return;
            }
        }

        if (UIUtil.TouchedDeclineButton(world)){
            this.S = OnTurnState.S1;
            world.clearWorldFlag(WorldFlags.ManaSelectMode);
            CollectedCardList.clear();
            world.getEventLog().setRecording(false);
            return;
        }

        InactiveCard SelectedCard = (InactiveCard) UIUtil.GetTouchedTrackCard(world);

        if (SelectedCard == null)
            return;
        if (SelectedCard.GridPosition().getZone() != 1)
            return;
        if (GetUtil.IsTapped(SelectedCard))
            return;

        if (CollectedCardList.contains(SelectedCard)) {
            UIUtil.TrackSelectedCardsWhenUserIsChoosing(CollectedCardList, ManaCards, SelectedCard);
            return;
        }

        if (!(CollectedCardList.size() < GetUtil.getTotalManaCost(SummoningOrCastCard)))
            return;

        boolean matchedCivilization = false;
        for (int i = 0; i < CollectedCardList.size(); i++) {
            InactiveCard tcard = (InactiveCard) CollectedCardList.get(i);
            if (GetUtil.RequiredCivilization(tcard, SummoningOrCastCard.getCivilization())) {
                matchedCivilization = true;
                break;
            }
        }

        if (matchedCivilization) {
            UIUtil.TrackSelectedCardsWhenUserIsChoosing(CollectedCardList, ManaCards, SelectedCard);
        } else {
            int NumberOfCollectedCard = CollectedCardList.size();
            if (NumberOfCollectedCard < (SummoningOrCastCard.getCost() - 1)) {
                UIUtil.TrackSelectedCardsWhenUserIsChoosing(CollectedCardList, ManaCards, SelectedCard);
            } else {
                if (GetUtil.RequiredCivilization(SelectedCard, SummoningOrCastCard.getCivilization())) {
                    UIUtil.TrackSelectedCardsWhenUserIsChoosing(CollectedCardList, ManaCards, SelectedCard);
                }
            }
        }
    }
/*
 Performs Summon instructions
 S4
 */
    private void SummonUpdate() {
        if (world.getInstructionIteratorHandler().update()) {
            S = OnTurnState.SX;
            world.setFetchCard(null);
            world.getEventLog().setRecording(false);
            //sendevent
            String msg = world.getEventLog().getAndClearEvents();
            NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
        }
    }
/*
 Add mana
 S6
 */
    private void ManaUpdate() {
        boolean addtomanaTapped = false;
        if (GetUtil.AddToManaTapped((InactiveCard)world.getFetchCard())) {
            addtomanaTapped = true;
        }
        String AddToManaInstruction = InstSetUtil.GenerateSelfChangeZoneInstruction(1);
        InstructionSet instruction = new InstructionSet(AddToManaInstruction);
        world.getInstructionHandler().setCardAndInstruction((InactiveCard)world.getFetchCard(),instruction);
        world.getInstructionHandler().execute();
        if (addtomanaTapped) {
            SetUnsetUtil.SetTappedAttr((InactiveCard) world.getFetchCard());
            world.getEventLog().registerEvent(world.getFetchCard(), false, 0 , "Tapped", true ,1);
        }
        world.setFetchCard(null);
        world.setWorldFlag(WorldFlags.CantAddToMana);
        this.S = OnTurnState.SX;
        world.getEventLog().setRecording(false);
        //sendeventlog
        String msg = world.getEventLog().getAndClearEvents();
        NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
    }
/*
 Performs Cast instruction
 S5
 */
    private void CastUpdate() {
        if (world.getInstructionIteratorHandler().update()) {
            InactiveCard card = (InactiveCard) world.getFetchCard();
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
            S = OnTurnState.SX;
            world.setFetchCard(null);
            world.getEventLog().setRecording(false);
            //sendevent
            String msg = world.getEventLog().getAndClearEvents();
            NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
        }
    }

    private void ShowInfoUpdate() {
        if (UIUtil.TouchedInfoTabBackButton(world)) {
            this.S = OnTurnState.S1;
            world.clearWorldFlag(WorldFlags.DisplayInfo);
        }
    }

    private void TapAbilityUpdate() {
        if (world.getInstructionIteratorHandler().update()) {
            S = OnTurnState.SX;
            world.getEventLog().setRecording(false);
            //send Eventlog
            String msg = world.getEventLog().getAndClearEvents();
            NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
        }
    }
/*
 Draw Card
 S9
 */
    private void DrawCardUpdate() {
        String DrawCardInstruction = InstSetUtil.GenerateDrawCardInstruction();
        InstructionSet instruction = new InstructionSet(DrawCardInstruction);
        world.getInstructionHandler().setCardAndInstruction(null,instruction);
        world.getInstructionHandler().execute();
        world.setWorldFlag(WorldFlags.CantDrawCard);
        this.S = OnTurnState.SX;
        world.getEventLog().setRecording(false);
        //sendeventlog
        String msg = world.getEventLog().getAndClearEvents();
        NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
    }
/*
  spread flag attr
  SX
 */
    private void FlagSpreadingUpdate() {
        SetUnsetUtil.SpreadingFlagAttr(world);
        world.getInstructionHandler().setCardAndInstruction(null, NotYetSpreadCleanup);
        world.getInstructionHandler().execute();
        S = OnTurnState.S1;
    }
}