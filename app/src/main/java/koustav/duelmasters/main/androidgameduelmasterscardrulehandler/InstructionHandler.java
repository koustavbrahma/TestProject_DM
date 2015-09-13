package koustav.duelmasters.main.androidgameduelmasterscardrulehandler;

import java.util.ArrayList;
import java.util.Random;

import koustav.duelmasters.main.androidgameduelmastersdatastructure.ActiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.InactiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.TypeOfCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.World;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.WorldFlags;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Zone;
import koustav.duelmasters.main.androidgameduelmastersnetworkmodule.DirectiveHeader;
import koustav.duelmasters.main.androidgameduelmastersutil.ActUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.GetUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.NetworkUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.SetUnsetUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.UIUtil;

/**
 * Created by Koustav on 4/17/2015.
 */
public class InstructionHandler {
    enum InstructionState {
        S1,
        S2,
        S3,
        S4,
    }

    InstructionState State;
    InactiveCard CurrentCard;
    InstructionSet instruction;
    World world;
    ArrayList<Cards> CollectCardList;
    boolean InstructionSkipped;

    public InstructionHandler(World world){
        this.world = world;
        this.State = InstructionState.S1;
        CollectCardList = new ArrayList<Cards>();
        InstructionSkipped = false;
    }

    public void setCardAndInstruction(InactiveCard card, InstructionSet instruction) {
        setCurrentCard(card);
        setInstruction(instruction);
    }
    private void setInstruction(InstructionSet instruction){
        this.instruction = instruction;
    }

    private void setCurrentCard(InactiveCard card){
        this.CurrentCard = card;
    }

    public InactiveCard getCurrentCard() {
        return this.CurrentCard;
    }

    public ArrayList<Cards> getCollectCardList() {
        return CollectCardList;
    }
/*
 execute instruction API. Only public function through which all engines can be used.
 */
    public boolean execute() {
        if (instruction == null)
            return true;

        boolean status = false;
        if (instruction.getInstructionType() == InstructionType.Choose) {
            status = ChooseAnyNCard(false);
        }

        if (instruction.getInstructionType() == InstructionType.MayChoose) {
            status = ChooseAnyNCard(true);
        }

        if (instruction.getInstructionType() == InstructionType.ChooseFromBegin) {
            status = ChooseFirstNCard(false);
        }

        if (instruction.getInstructionType() == InstructionType.MayChooseFromBegin) {
            status = ChooseFirstNCard(true);
        }

        if (instruction.getInstructionType() == InstructionType.ChooseRandom) {
            collectCards();
            ChooseRandomNCard();
            PerformActionOnFilterCard();
            status = true;
        }

        if (instruction.getInstructionType() == InstructionType.SelfChangeZone) {
            ChangeZoneOfCurrentCard();
            status = true;
        }

        if (instruction.getInstructionType() == InstructionType.ChangeZoneAll) {
            collectCards();
            ChangeZoneAll();
            status = true;
        }

        if (instruction.getInstructionType() == InstructionType.SelfBooster) {
            collectCards();
            CardBoostFlagAttribute();
            status = true;
        }

        if (instruction.getInstructionType() == InstructionType.SelfBoosterMultiplier) {
            collectCards();
            CardBoostMultiplierFlagAttribute();
            status = true;
        }

        if (instruction.getInstructionType() == InstructionType.Aura) {
            collectCards();
            SpreadFlagAttribute();
            status = true;
        }

        if (instruction.getInstructionType() == InstructionType.SelfSetAttr) {
            SetFlagAttributeOfCurrentCard();
            status = true;
        }

        if (instruction.getInstructionType() == InstructionType.SetAttr) {
            collectCards();
            SetFlagAttributeAll();
            status = true;
        }

        if (instruction.getInstructionType() == InstructionType.SelfCleanUp) {
            CleanUpFlagAttributeOfCurrentCard();
            status = true;
        }

        if (instruction.getInstructionType() == InstructionType.CleanUp) {
            collectCards();
            CleanUpFlagAttributeAll();
            status = true;
        }

        if (instruction.getInstructionType() == InstructionType.SelfCleanUpMultiplier) {
            collectCards();
            CleanUpFlagAttrMultiplierOfCurrentCard();
            status = true;
        }

        if (instruction.getInstructionType() == InstructionType.Shuffle) {
            collectCards();
            PerformShuffleOnZone();
            status = true;
        }

        if (instruction.getInstructionType() == InstructionType.Copy) {
            collectCards();
            CopyCardToTempZone();
            status = true;
        }

        if (instruction.getInstructionType() == InstructionType.SetTempSpreadInst) {
            collectCards();
            SetTemporarySpreadInstruction();
            status = true;
        }

        if (instruction.getInstructionType() == InstructionType.PassControlToOpponent) {
            status = PassControl();
        }

        if (instruction.getInstructionType() == InstructionType.ShowCardToYourOpponent) {
            status = ShowCardToYourOpponent();
        }

        if (instruction.getInstructionType() == InstructionType.SendDeckShuffleUpdate) {
            SendDeckShuffleUpdate();
            status = true;
        }


        SetCleanupInst(status);
        status = PerformCascade(status);

        return status;
    }
/*
 This API is used to choose N card by user.
 */
    private boolean ChooseAnyNCard(boolean CanSkip) {
        boolean status = false;
        if (State == InstructionState.S1) {
            collectCards();
            CheckActionZoneConsistencyForChoose();
            world.getMaze().getZoneList().get(6).getZoneArray().clear();
            int zone = 0;
            if (CollectCardList.size() > 0)
                zone = CollectCardList.get(0).GridPosition().getZone();

            if (zone == 4 || zone == 5 || zone == 11 || zone == 12) {
                State = InstructionState.S4;
                world.setWorldFlag(WorldFlags.CardSearchSelectingMode);
                if (CanSkip)
                    world.setWorldFlag(WorldFlags.MaySkipCardSelectingMode);
            } else {
                State = InstructionState.S2;
                world.setWorldFlag(WorldFlags.CardSelectingMode);
                if (CanSkip)
                    world.setWorldFlag(WorldFlags.MaySkipCardSelectingMode);
            }
        }

        if (State == InstructionState.S2) {
            UserSelectingCard(CanSkip);
        }

        if (State == InstructionState.S4) {
            UserSearchAndSelectCard(CanSkip);
        }

        if (State == InstructionState.S3) {
            if (PerformActionOnFilterCard()) {
                State = InstructionState.S1;
                status = true;
            }
        }
        return status;
    }
/*
 When user selects cards, this API is used.
 */
    private void UserSelectingCard(boolean CanSkip){
        int filtercount;
        if(instruction.getCount() !=0) {
            filtercount = instruction.getCount();
        } else {
            filtercount = instruction.getConditionCount();
        }
        filtercount = (CollectCardList.size() > filtercount) ? filtercount : CollectCardList.size();
        if (filtercount == 0) {
            this.State = InstructionState.S3;
            world.clearWorldFlag(WorldFlags.CardSelectingMode);
            world.clearWorldFlag(WorldFlags.AcceptCardSelectingMode);
            if (CanSkip)
                world.clearWorldFlag(WorldFlags.MaySkipCardSelectingMode);
            return;
        }
        ArrayList<Cards> TempZone = world.getMaze().getZoneList().get(6).getZoneArray();
        if (TempZone.size() == filtercount) {
            if (UIUtil.TouchedAcceptButton(world)) {
                CollectCardList.clear();
                for (int i =0; i< TempZone.size(); i++) {
                    CollectCardList.add(TempZone.get(i));
                }
                TempZone.clear();
                this.State = InstructionState.S3;
                world.clearWorldFlag(WorldFlags.CardSelectingMode);
                world.clearWorldFlag(WorldFlags.AcceptCardSelectingMode);
                if (CanSkip)
                    world.clearWorldFlag(WorldFlags.MaySkipCardSelectingMode);
                return;
            }
        }

        if (CanSkip && UIUtil.TouchedDeclineButton(world)){
            this.State = InstructionState.S3;
            TempZone.clear();
            CollectCardList.clear();
            world.clearWorldFlag(WorldFlags.CardSelectingMode);
            world.clearWorldFlag(WorldFlags.MaySkipCardSelectingMode);
            world.clearWorldFlag(WorldFlags.AcceptCardSelectingMode);
            InstructionSkipped = true;
            return;
        }

        InactiveCard SelectedCard = (InactiveCard) UIUtil.GetTouchedTrackCard(world);

        if (SelectedCard == null)
            return;

        if (!CollectCardList.contains(SelectedCard))
            return;

        if (TempZone.contains(SelectedCard)) {
            UIUtil.TrackSelectedCardsWhenUserIsChoosing(TempZone, CollectCardList, SelectedCard);
            world.clearWorldFlag(WorldFlags.AcceptCardSelectingMode);
            return;
        }

        if (!(TempZone.size() < filtercount))
            return;

        UIUtil.TrackSelectedCardsWhenUserIsChoosing(TempZone, CollectCardList, SelectedCard);
        if (filtercount == TempZone.size())
            world.setWorldFlag(WorldFlags.AcceptCardSelectingMode);
    }
/*
    This API is used to search and select card in a stack like deck or graveyard
     */
    private void UserSearchAndSelectCard(boolean CanSkip) {
        int filtercount;
        if(instruction.getCount() !=0) {
            filtercount = instruction.getCount();
        } else {
            filtercount = instruction.getConditionCount();
        }
        filtercount = (CollectCardList.size() > filtercount) ? filtercount : CollectCardList.size();
        if (filtercount == 0) {
            this.State = InstructionState.S3;
            world.clearWorldFlag(WorldFlags.CardSearchSelectingMode);
            world.clearWorldFlag(WorldFlags.AcceptCardSelectingMode);
            if (CanSkip)
                world.clearWorldFlag(WorldFlags.MaySkipCardSelectingMode);
            return;
        }
        Cards card;
        int size = CollectCardList.size() - 1;
        ArrayList<Cards> TempZone = world.getMaze().getZoneList().get(6).getZoneArray();

        if (TempZone.size() == filtercount) {
            if (UIUtil.TouchedInfoTabBackButton(world)) {
                CollectCardList.clear();
                for (int i =0; i< TempZone.size(); i++) {
                    CollectCardList.add(TempZone.get(i));
                }
                TempZone.clear();
                this.State = InstructionState.S3;
                world.clearWorldFlag(WorldFlags.CardSearchSelectingMode);
                world.clearWorldFlag(WorldFlags.AcceptCardSelectingMode);
                if (CanSkip)
                    world.clearWorldFlag(WorldFlags.MaySkipCardSelectingMode);
                return;
            }
        }

        if (CanSkip && UIUtil.TouchedAttackShieldOrPlayerButton(world)){
            this.State = InstructionState.S3;
            TempZone.clear();
            CollectCardList.clear();
            world.clearWorldFlag(WorldFlags.CardSearchSelectingMode);
            world.clearWorldFlag(WorldFlags.MaySkipCardSelectingMode);
            world.clearWorldFlag(WorldFlags.AcceptCardSelectingMode);
            InstructionSkipped = true;
            return;
        }

        if (UIUtil.TouchedAcceptButton(world)) {
            card = CollectCardList.remove(size);
            CollectCardList.add(0 , card);
        }

        if (UIUtil.TouchedDeclineButton(world)) {
            card = CollectCardList.remove(0);
            CollectCardList.add(card);
        }

        Cards SelectedCard;
        if (UIUtil.TouchedMaze(world)) {
            SelectedCard = CollectCardList.get(0);
        } else {
            return;
        }

        if (TempZone.contains(SelectedCard)) {
            UIUtil.TrackSelectedCardsWhenUserIsChoosing(TempZone, CollectCardList, SelectedCard);
            world.clearWorldFlag(WorldFlags.AcceptCardSelectingMode);
            return;
        }

        if (!(TempZone.size() < filtercount))
            return;

        UIUtil.TrackSelectedCardsWhenUserIsChoosing(TempZone, CollectCardList, SelectedCard);
        if (filtercount == TempZone.size())
            world.setWorldFlag(WorldFlags.AcceptCardSelectingMode);
    }
/*
 This API is used to set a attribute on CollectCardList.
 Used for Spreading
 */
    private void SpreadFlagAttribute() {
        InactiveCard card;
        for (int i =0 ; i < CollectCardList.size(); i++) {
            card = (InactiveCard) CollectCardList.get(i);
            SetUnsetUtil.SetSpreadFlagAttr(card, instruction);
        }

        if (CollectCardList.size() != 0) {
            instruction.setResult(true);
        }
    }
/*
    Set the attr val. For non spreading
     */
    private void SetFlagAttributeOfCurrentCard() {
        SetUnsetUtil.SetFlagAttr(world, CurrentCard, instruction);
    }
/*
    Set the attr val. For non spreading
     */
    private void SetFlagAttributeOnGivenCard(InactiveCard card) {
        SetUnsetUtil.SetFlagAttr(world, card, instruction);
    }
/*
    Set the attr val. For non spreading
     */
    private void SetFlagAttributeAll() {
        InactiveCard card;
        for (int i = 0; i < CollectCardList.size(); i++) {
            card = (InactiveCard) CollectCardList.get(i);
            SetFlagAttributeOnGivenCard(card);
        }
    }
/*
 This API is used to set an attribute on CurrentCard. Mainly during spreading
 */
    private void CardBoostFlagAttribute() {
        boolean boost = false;
        if (instruction.getAttrCountOrIndex() == 1)
            boost = true;
        if ((instruction.getAttrCountOrIndex() == 2) && (CollectCardList.size() > 0))
            boost = true;

        if (instruction.getAttrCountOrIndex() == 3) {
            int[] ActionZone = instruction.getActionZone();
            int count = 0;
            if (CurrentCard != null && (CurrentCard.GridPosition().getZone() > 6)) {
                for (int i = 0; i < 7; i++) {
                    if (ActionZone[i] == 0)
                        continue;

                    if (ActionZone[i] == 1) {
                        count = count + world.getMaze().getZoneList().get(i+7).zoneSize();
                        if ((i+7) == CurrentCard.GridPosition().getZone())
                            count--;
                    }

                    if (ActionZone[i] == 2) {
                        count = count + world.getMaze().getZoneList().get(i).zoneSize();
                    }

                    if (ActionZone[i] == 3) {
                        count = count + world.getMaze().getZoneList().get(i).zoneSize();
                        count = count + world.getMaze().getZoneList().get(i+7).zoneSize();
                        if ((i+7) == CurrentCard.GridPosition().getZone())
                            count--;
                    }
                }
            }else {
                for (int i = 0; i < 7; i++) {
                    if (ActionZone[i] == 0)
                        continue;

                    if (ActionZone[i] == 1) {
                        count = count + world.getMaze().getZoneList().get(i).zoneSize();
                        if ((CurrentCard != null) && (i == CurrentCard.GridPosition().getZone()))
                            count--;
                    }

                    if (ActionZone[i] == 2) {
                        count = count + world.getMaze().getZoneList().get(i+7).zoneSize();
                    }

                    if (ActionZone[i] == 3) {
                        count = count + world.getMaze().getZoneList().get(i).zoneSize();
                        count = count + world.getMaze().getZoneList().get(i+7).zoneSize();
                        if ((CurrentCard != null) && (i == CurrentCard.GridPosition().getZone()))
                            count--;
                    }
                }
            }
            if (CollectCardList.size() == count)
                boost = true;
        }

        if (instruction.getAttrCountOrIndex() == 4) {
            int count = (instruction.getCount() > 0) ? instruction.getCount() : instruction.getConditionCount();
            if (CollectCardList.size() >= count)
                boost = true;
        }

        SetUnsetUtil.SetUnsetBoostFlagAttr(CurrentCard, instruction, boost);
    }
/*
 This API is used to set an attribute on CurrentCard. Value set is multiple of CollectCardlist size.
 Used for spreading
 */
    private void  CardBoostMultiplierFlagAttribute() {
        SetUnsetUtil.SetUnsetBoostMultiplierFlagAttr(CurrentCard, instruction, CollectCardList.size());
    }
/*
 This API is used to Clean up an attribute from CurrentCard.
 */
    private void CleanUpFlagAttributeOfCurrentCard() {
        SetUnsetUtil.CleanFlagAttr(CurrentCard, instruction);
    }
/*
 This API is used to Clean up an attribute from a given card.
 */
    private void CleanUpFlagAttributeOfGivenCard(InactiveCard card) {
        SetUnsetUtil.CleanFlagAttr(card, instruction);
    }
/*
 This API is used to clean up an attribute from CollectCardList.
 */
    private void CleanUpFlagAttributeAll() {
        InactiveCard card;
        for (int i = 0; i < CollectCardList.size(); i++) {
            card = (InactiveCard) CollectCardList.get(i);
            CleanUpFlagAttributeOfGivenCard(card);
        }
    }
/*
 This API is used to change zone of CurrentCard.
 */
    private void ChangeZoneOfCurrentCard() {
        InactiveCard card;
        card = (InactiveCard) ActUtil.ChangeZoneOperator(world, CurrentCard, instruction);
        if (CurrentCard == world.getFetchCard()) {
            world.setFetchCard(card);
        }
        setCurrentCard(card);
    }
/*
 This API is used to change zone of all card in CollectCardList.
 */
    private void ChangeZoneAll() {
        Cards card;
        for (int i = 0; i < CollectCardList.size(); i++) {
            card = CollectCardList.get(i);
            ChangeZoneOfGivenCard(card);
        }
    }
/*
 This API is used to change zone of given card.
 */
    private void ChangeZoneOfGivenCard(Cards card) {
        ActUtil.ChangeZoneOperator(world, card, instruction);
    }
/*
    This API set Temporary spread inst
     */
    private void SetTemporarySpreadInstruction(){
        if (CurrentCard == null)
            throw new IllegalArgumentException("Current card cannot be null for this instruction");

        String SpreadinstructionStr = ((ActiveCard)CurrentCard).cardInfo().PrimaryInstruction.get(instruction.getAttrCountOrIndex() - 1);
        InactiveCard card;
        String msg = new String("");
        for (int i = 0; i < CollectCardList.size(); i++) {
            if (CollectCardList.get(i).GridPosition().getZone() == 4 || CollectCardList.get(i).GridPosition().getZone() == 5 ||
                    CollectCardList.get(i).GridPosition().getZone() == 11 || CollectCardList.get(i).GridPosition().getZone() == 12)
                throw new IllegalArgumentException("Invalid zone to set temp spreading inst");
            card = (InactiveCard) CollectCardList.get(i);
            InstructionSet Spreadinstruction = new InstructionSet(SpreadinstructionStr);
            card.AddTemporarySpreadingInst(Spreadinstruction);
            int zone;
            if (card.GridPosition().getZone() > 6) {
                zone = card.GridPosition().getZone() - 7;
            } else if (card.GridPosition().getZone() < 6) {
                zone = card.GridPosition().getZone() + 7;
            } else {
                throw new IllegalArgumentException("Unexpected zone");
            }
            String tmp = zone + " " + card.GridPosition().getGridIndex() +
                    " " + card.getNameID();
            msg = msg.concat(tmp);
            msg = msg.concat("%");
            msg = msg.concat(SpreadinstructionStr);
            msg = msg.concat("#");
        }

        if (!(msg.length() > 0))
            msg = null;

        NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.SetTmpSpreadingInst, msg, null);
    }
/*
  This API clean up flag attr based on collection count
     */
    private void CleanUpFlagAttrMultiplierOfCurrentCard() {
        SetUnsetUtil.CleanFlagAttrMultiplier(CurrentCard, instruction, CollectCardList.size());
    }
/*
 This API is used to perform action (move, spreading attribute etc.) on filtered set of card.
 */
    private boolean PerformActionOnFilterCard() {
        boolean status = false;
        if (instruction.getAction() == Action.Move) {
            ChangeZoneAll();
            status = true;
        }

        if (instruction.getAction() == Action.SetAttr) {
            SetFlagAttributeAll();
            status = true;
        }

        if (instruction.getAction() == Action.Copy) {
            CopyCardToTempZone();
            status = true;
        }

        if (instruction.getAction() == Action.Show) {
            status = ShowInfoOfCard();
        }

        if (instruction.getAction() == Action.Evolution) {
            PerformEvolution();
            status = true;
        }

        if (instruction.getAction() == Action.SetTmpSpreadInst) {
            SetTemporarySpreadInstruction();
            status = true;
        }
        return status;
    }
/*
 This API is used to select first N card.
 */
    private boolean ChooseFirstNCard(boolean CanSkip) {
        boolean status = false;
        if (State == InstructionState.S1) {
            collectCards();
            CheckActionZoneConsistencyForChoose();
            State = InstructionState.S2;
            world.setWorldFlag(WorldFlags.UserDecisionMakingMode);
            world.setWorldFlag(WorldFlags.AcceptCardSelectingMode);
            if (CanSkip)
                world.setWorldFlag(WorldFlags.MaySkipCardSelectingMode);
        }

        if (State == InstructionState.S2) {
            SelectingFirstNCard(CanSkip);
        }

        if (State == InstructionState.S3) {
            if (PerformActionOnFilterCard()) {
                State = InstructionState.S1;
                status = true;
            }
        }

        return status;
    }
/*
 Selecting first N card
 */
    private void SelectingFirstNCard(boolean CanSkip) {
        if (CanSkip) {
            if (UIUtil.TouchedAcceptButton(world)) {

            } else if (UIUtil.TouchedDeclineButton(world)) {
                CollectCardList.clear();
                State = InstructionState.S3;
                world.clearWorldFlag(WorldFlags.UserDecisionMakingMode);
                world.clearWorldFlag(WorldFlags.MaySkipCardSelectingMode);
                world.clearWorldFlag(WorldFlags.AcceptCardSelectingMode);
                InstructionSkipped = true;
                return;
            } else {
                return;
            }
        }
        int filtercount;
        if(instruction.getCount() !=0) {
            filtercount = instruction.getCount();
        } else {
            filtercount = instruction.getConditionCount();
        }

        if (CollectCardList.size() > filtercount) {
            ArrayList<Cards> tmp = new ArrayList<Cards>();
            for (int i =0; i < filtercount; i++) {
                tmp.add(CollectCardList.get(i));
            }
            CollectCardList.clear();
            for (int i =0; i <tmp.size(); i++) {
                CollectCardList.add(tmp.get(i));
            }
        }
        State = InstructionState.S3;
        world.clearWorldFlag(WorldFlags.UserDecisionMakingMode);
        world.clearWorldFlag(WorldFlags.MaySkipCardSelectingMode);
        world.clearWorldFlag(WorldFlags.AcceptCardSelectingMode);
    }
/*
 This API is used to select N card in random.
 */
    private void ChooseRandomNCard() {
        CheckActionZoneConsistencyForChoose();
        int filtercount;
        if(instruction.getCount() !=0) {
            filtercount = instruction.getCount();
        } else {
            filtercount = instruction.getConditionCount();
        }

        filtercount = (CollectCardList.size() > filtercount) ? filtercount : CollectCardList.size();

        if (!(filtercount> 0))
            return;

        Random R = new Random();
        int[] selectCardIndex = new int[filtercount];
        for (int i = 0; i < selectCardIndex.length; i++)
            selectCardIndex[i] = -1;

        int i = 0;
        while (i < selectCardIndex.length){
            int tempindex;
            tempindex = (int) R.nextInt(CollectCardList.size());
            boolean status = true;
            for (int j = 0 ; j < i; j++) {
                if (tempindex == selectCardIndex[j]){
                    status = false;
                    break;
                }
            }

            if (status) {
                selectCardIndex[i] = tempindex;
                i++;
            }
        }

        ArrayList<Cards> tmpList = new ArrayList<Cards>();

        for (i = 0; i < selectCardIndex.length; i++) {
            tmpList.add(CollectCardList.get(selectCardIndex[i]));
        }
        if (selectCardIndex.length != tmpList.size())
            throw new IllegalArgumentException("Mismatch in size not possible");

        CollectCardList.clear();
        for (i = 0; i <selectCardIndex.length; i++ ){
            CollectCardList.add(tmpList.get(i));
        }
        tmpList.clear();
    }
/*
 This API is used to Shuffle cards in a zone.
 */
    private void PerformShuffleOnZone() {
        CheckActionZoneConsistencyForShuffle();
        ActUtil.ShuffleDeck(world,CollectCardList);
    }
/*
 This API is used to copy a card to tempzone.
 */
    private void CopyCardToTempZone() {
        Cards card;
        world.getMaze().getZoneList().get(6).getZoneArray().clear();
        for (int i = 0; i < CollectCardList.size(); i++) {
            card = CollectCardList.get(i);
            ActUtil.CopyCardsToTempZone(world, card);
        }
    }
/*
 Show card
 */
    private boolean ShowInfoOfCard() {
        Cards card;
        int size = CollectCardList.size() - 1;
        ArrayList<Cards> TempZone = world.getMaze().getZoneList().get(6).getZoneArray();
        TempZone.clear();
        world.setWorldFlag(WorldFlags.DisplayInfoUserSelect);
        if (UIUtil.TouchedInfoTabBackButton(world) || (size == -1)) {
            world.clearWorldFlag(WorldFlags.DisplayInfoUserSelect);
            return true;
        }
        if (UIUtil.TouchedAcceptButton(world)) {
            card = CollectCardList.remove(size);
            CollectCardList.add(0 , card);
        }

        if (UIUtil.TouchedDeclineButton(world)) {
            card = CollectCardList.remove(0);
            CollectCardList.add(card);
        }
        for (int i =0; i < CollectCardList.size(); i++) {
            TempZone.add(CollectCardList.get(i));
        }
        return false;
    }
/*
    Do evolution
     */
    private void PerformEvolution() {
        if (CollectCardList.size() == 0)
            return;
        int Ezone = CurrentCard.GridPosition().getZone();
        int Bzone = CollectCardList.get(0).GridPosition().getZone();
        if (Ezone !=3 && Bzone != 0)
            throw new IllegalArgumentException("Inconsistent zone, I am not expecting any other zone");
        Ezone = Ezone + 7;
        Bzone = Bzone + 7;
        String msg = Ezone + " " + CurrentCard.GridPosition().getGridIndex() + " " + CurrentCard.getNameID() + " " +
                Bzone + " " + CollectCardList.get(0).GridPosition().getGridIndex() + " " + CollectCardList.get(0).getNameID();
        NetworkUtil.sendDirectiveUpdates(world, DirectiveHeader.EvolutionEvent, msg, null);
        SetUnsetUtil.SetMarkedCard((InactiveCard) CollectCardList.get(0));
    }
/*
 API which pass the control to opponent temporary to execute some instruction by the opponent
 */
    private boolean PassControl() {
        boolean status = false;
        if (State == InstructionState.S1) {
            if (CurrentCard == null)
                throw new IllegalArgumentException("Current card cannot be null for this instruction");

            String instructionStr = ((ActiveCard)CurrentCard).cardInfo().PrimaryInstruction.get(instruction.getAttrCountOrIndex() - 1);
            NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.PassControl, instructionStr, null);
            State = InstructionState.S2;
        }

        if (State == InstructionState.S2) {
            if (world.getGame().getNetwork().getSocket() == null || world.getGame().getNetwork().getSocket().isClosed()) {
                State = InstructionState.S3;
                // for now this but later handle it properly
                return status;
            }
            if (world.getGame().getNetwork().getreceivedDirectiveSize() == 0)
                return status;

            String directive = world.getGame().getNetwork().getreceivedDirectiveMsg();
            String [] splitdirective = directive.split("@");

            if (splitdirective[0].equals(DirectiveHeader.PassControl)) {
                State = InstructionState.S3;
            }

            if (splitdirective[0].equals(DirectiveHeader.ApplyEvents) && splitdirective.length > 2) {
                String[] eventString = splitdirective[1].split("#");
                world.getEventLog().AddEventsToExecute(eventString);
            }
        }

        if (State == InstructionState.S3) {
            State = InstructionState.S1;
            status = true;
        }
        return status;
    }
/*
 Pass card to opponent to show the info
 */
    private boolean ShowCardToYourOpponent() {
        boolean status = false;
        if (State == InstructionState.S1) {
            collectCards();
            world.setWorldFlag(WorldFlags.UserDecisionMakingMode);
            world.setWorldFlag(WorldFlags.AcceptCardSelectingMode);
            State = InstructionState.S2;
        }

        if (State == InstructionState.S2) {
            if (UIUtil.TouchedAcceptButton(world) || (CollectCardList.size() == 0)){
                State = InstructionState.S3;
                world.clearWorldFlag(WorldFlags.AcceptCardSelectingMode);
                String msg = new String();
                for (int i = 0; i < CollectCardList.size(); i++) {
                    Cards card = CollectCardList.get(i);
                    int zone = card.GridPosition().getZone();
                    if (zone < 4)
                        zone = zone + 7;
                    else
                        throw new IllegalArgumentException("I am not expecting zone to be greater than 4");
                    String tmp = zone + " " + card.GridPosition().getGridIndex() + " " + card.getNameID();
                    msg = msg.concat(tmp);
                    if (i < (CollectCardList.size() - 1))
                        msg = msg.concat("#");
                }
                if (!(msg.length() > 0))
                    msg = null;
                NetworkUtil.sendDirectiveUpdates(world, DirectiveHeader.SeeOpponentPassedCards, msg, null);
                if (msg == null)
                    State = InstructionState.S4;
            }
        }

        if (State == InstructionState.S3) {
            if (world.getGame().getNetwork().getSocket() == null || world.getGame().getNetwork().getSocket().isClosed()) {
                State = InstructionState.S4;
                // for now this but later handle it properly
                return status;
            }
            if (world.getGame().getNetwork().getreceivedDirectiveSize() == 0)
                return status;

            String directive = world.getGame().getNetwork().getreceivedDirectiveMsg();
            String [] splitdirective = directive.split("@");

            if (splitdirective[0].equals(DirectiveHeader.SeeOpponentPassedCards)) {
                State = InstructionState.S4;
            }
        }

        if (State == InstructionState.S4) {
            world.clearWorldFlag(WorldFlags.UserDecisionMakingMode);
            State = InstructionState.S1;
            status = true;
        }
        return status;
    }
/*
 This API send the Update of the present deck update
 */
    private void SendDeckShuffleUpdate() {
        Zone zone = world.getMaze().getZoneList().get(5);
        String msg = new String();

        for (int i = 0; i < zone.zoneSize(); i++) {
            Cards card =  zone.getZoneArray().get(i);
            String tmp = card.getNameID();
            msg = msg.concat(tmp);
            if (i < (zone.zoneSize() - 1))
                msg = msg.concat("#");
        }
        if (!(msg.length() > 0))
            msg = null;
        NetworkUtil.sendDirectiveUpdates(world, DirectiveHeader.SendDeckShuffleUpdate, msg, null);
    }
/*
 This API is used to collect card from zones. Sometimes based on condition also.
 */
    private void collectCards() {
        CollectCardList.clear();
        if (instruction.getCount() == 0 && instruction.getConditionCount() == 0) {
            return;
        }
        int[] ActionZone = instruction.getActionZone();
        if (CurrentCard != null && (CurrentCard.GridPosition().getZone() > 6)) {
            for (int i = 0; i < 7; i++) {
                if (ActionZone[i] == 0)
                    continue;

                if (ActionZone[i] == 1) {
                    collectOpponentCards(i);
                }

                if (ActionZone[i] == 2) {
                    collectMyCards(i);
                }

                if (ActionZone[i] == 3) {
                    collectMyCards(i);
                    collectOpponentCards(i);
                }
            }
        }else {
            for (int i = 0; i < 7; i++) {
                if (ActionZone[i] == 0)
                    continue;

                if (ActionZone[i] == 1) {
                    collectMyCards(i);
                }

                if (ActionZone[i] == 2) {
                    collectOpponentCards(i);
                }

                if (ActionZone[i] == 3) {
                    collectMyCards(i);
                    collectOpponentCards(i);
                }
            }
        }
        if (CollectCardList.size() > 0 && CurrentCard != null) {
            int index = CollectCardList.indexOf(CurrentCard);
            if (index != -1) {
                CollectCardList.remove(index);
            }
        }
    }
/*
 Inner API of collectcard. Collect my cards.
 */
    private void collectMyCards(int index) {
        if (instruction.getCount() != 0 && instruction.getConditionCount() != 0)
            throw new IllegalArgumentException("Both count and condition-count can not be non zero");

        Zone zone = world.getMaze().getZoneList().get(index);
        if (instruction.getCount() != 0) {
            for (int i = 0; i < zone.zoneSize(); i++) {
                CollectCardList.add(zone.getZoneArray().get(i));
            }
            return;
        }

        if (!(instruction.getCondition().getConditionType() == ConditionType.NameId ||
                instruction.getCondition().getConditionType() == ConditionType.Civilization ||
                 instruction.getCondition().getConditionType() == ConditionType.TypeOfCard) && (index == 4 || index == 5))
            throw new IllegalArgumentException("deck and graveyard cannot be collected based on condition (1)");

        if (instruction.getConditionCount() != 0) {
            collectCardsBasedOnCondition(zone);
        }
    }
/*
 Inner API of collectcard. Collect opponent card.
 */
    private void collectOpponentCards(int index) {
        if (index > 5)
            return;

        if (instruction.getCount() != 0 && instruction.getConditionCount() != 0)
            throw new IllegalArgumentException("Both count and condition-count can not be non zero");

        Zone zone = world.getMaze().getZoneList().get(index + 7);
        if (instruction.getCount() != 0) {
            for (int i = 0; i < zone.zoneSize(); i++) {
                CollectCardList.add(zone.getZoneArray().get(i));
            }
            return;
        }

        if (!(instruction.getCondition().getConditionType() == ConditionType.NameId ||
                instruction.getCondition().getConditionType() == ConditionType.Civilization ||
                instruction.getCondition().getConditionType() == ConditionType.TypeOfCard) && (index == 4 || index == 5))
            throw new IllegalArgumentException("deck and graveyard cannot be collected based on condition (2)");

        if (instruction.getConditionCount() != 0) {
            collectCardsBasedOnCondition(zone);
        }
    }
/*
 Collect card based on condition.
 */
    private void collectCardsBasedOnCondition(Zone zone) {
        if (instruction.getCondition().getConditionType() == ConditionType.Civilization) {
            Cards card;
            for (int i =0; i < zone.zoneSize(); i++) {
                card = zone.getZoneArray().get(i);
                if (GetUtil.RequiredCivilization(card,
                        Integer.parseInt(instruction.getCondition().getValue()))) {
                    CollectCardList.add(card);
                }
            }
        } else if (instruction.getCondition().getConditionType() == ConditionType.CivilizationFromTmpCard) {
            InactiveCard card;
            InactiveCard tcard = (InactiveCard) getFirstTempCard();
            for (int i = 0; i < zone.zoneSize(); i++) {
                card = (InactiveCard) zone.getZoneArray().get(i);
                if (GetUtil.RequiredCivilization(card, tcard.getCivilization())) {
                    CollectCardList.add(card);
                }
            }
        } else if (instruction.getCondition().getConditionType() == ConditionType.Race) {
            InactiveCard card;
            for (int i =0; i < zone.zoneSize(); i++) {
                card = (InactiveCard) zone.getZoneArray().get(i);
                if (instruction.getCondition().getValue().equals(card.getRace())) {
                    CollectCardList.add(card);
                }
            }
        } else if (instruction.getCondition().getConditionType() == ConditionType.RaceFromTmpCard) {
            InactiveCard card;
            InactiveCard tcard = (InactiveCard) getFirstTempCard();
            for (int i =0; i < zone.zoneSize(); i++) {
                card = (InactiveCard) zone.getZoneArray().get(i);
                if (tcard.getRace().equals(card.getRace())) {
                    CollectCardList.add(card);
                }
            }
        } else if (instruction.getCondition().getConditionType() == ConditionType.TypeOfCard) {
            Cards card;
            switch (instruction.getCondition().getValue()) {
                case "1":
                    for (int i = 0; i < zone.zoneSize(); i++) {
                        card = zone.getZoneArray().get(i);
                        if (card.getType() == TypeOfCard.Creature) {
                            CollectCardList.add(card);
                        }
                    }
                    break;
                case "2":
                    for (int i = 0; i < zone.zoneSize(); i++) {
                        card = zone.getZoneArray().get(i);
                        if (card.getType() == TypeOfCard.Evolution) {
                            CollectCardList.add(card);
                        }
                    }
                    break;
                case "3":
                    for (int i = 0; i < zone.zoneSize(); i++) {
                        card = zone.getZoneArray().get(i);
                        if (card.getType() == TypeOfCard.Spell) {
                            CollectCardList.add(card);
                        }
                    }
                    break;
                case "4":
                    for (int i = 0; i < zone.zoneSize(); i++) {
                        card = zone.getZoneArray().get(i);
                        if (card.getType() == TypeOfCard.Creature || card.getType() == TypeOfCard.Evolution) {
                            CollectCardList.add(card);
                        }
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Invalid value when condition type is typeofcard");
            }
        } else if (instruction.getCondition().getConditionType() == ConditionType.Attribute) {
            InactiveCard card;
            for (int i = 0; i < zone.zoneSize(); i++) {
                card = (InactiveCard) zone.getZoneArray().get(i);
                if (card.getflagAttributes().GetAttribute(instruction.getCondition().getValue()) > 0) {
                    CollectCardList.add(card);
                }
            }
        } else if (instruction.getCondition().getConditionType() == ConditionType.NotOfAttribute) {
            InactiveCard card;
            for (int i = 0; i < zone.zoneSize(); i++) {
                card = (InactiveCard) zone.getZoneArray().get(i);
                if (card.getflagAttributes().GetAttribute(instruction.getCondition().getValue()) == 0) {
                    CollectCardList.add(card);
                }
            }
        }else if (instruction.getCondition().getConditionType() == ConditionType.Power){
            InactiveCard card;
            for (int i = 0; i < zone.zoneSize(); i++) {
                card = (InactiveCard) zone.getZoneArray().get(i);
                if(instruction.getCondition().getUpperPower() == -1) {
                    if(instruction.getCondition().getLowerPower() <= GetUtil.getTotalPower(card))
                        CollectCardList.add(card);
                } else  {
                    if(instruction.getCondition().getLowerPower() <= GetUtil.getTotalPower(card) &&
                            instruction.getCondition().getUpperPower() >= GetUtil.getTotalPower(card))
                        CollectCardList.add(card);
                }
            }
        } else if (instruction.getCondition().getConditionType() == ConditionType.WorldFlag) {
            InactiveCard card;
            if (world.getWorldFlag(instruction.getCondition().getValue()) || instruction.getResult()) {
                for (int i = 0; i < zone.zoneSize(); i++) {
                    card = (InactiveCard) zone.getZoneArray().get(i);
                    CollectCardList.add(card);
                    if (card != CurrentCard)
                        break;
                }
            }
        } else if (instruction.getCondition().getConditionType() == ConditionType.CollectCardEqualToTempZoneCount) {
            Cards card;
            for (int i = 0; i < zone.zoneSize(); i++) {
                if (i < world.getMaze().getZoneList().get(6).zoneSize()) {
                    card = zone.getZoneArray().get(i);
                    CollectCardList.add(card);
                } else {
                    break;
                }
            }
        }else if (instruction.getCondition().getConditionType() == ConditionType.NameId){
            Cards card;
            for (int i = 0; i< zone.zoneSize(); i++) {
                card = zone.getZoneArray().get(i);
                if (card.getNameID().equals(instruction.getCondition().getValue())) {
                    CollectCardList.add(card);
                }
            }
        }else if (instruction.getCondition().getConditionType() == ConditionType.RaceContainSubStringForEvolutionOrFlagSpread) {
            InactiveCard card;
            for (int i =0; i < zone.zoneSize(); i++) {
                card = (InactiveCard) zone.getZoneArray().get(i);
                if ((card.getRace().contains(instruction.getCondition().getValue()) && (!(instruction.getAction() == Action.Evolution) || !GetUtil.IsTapped(card)))
                        || (instruction.getAction() == Action.Evolution && GetUtil.EvolutionCompatible(card) && !GetUtil.IsTapped(card))) {
                    CollectCardList.add(card);
                }
            }
        }else if (instruction.getCondition().getConditionType() == ConditionType.Nil) {

        }else {
            throw new IllegalArgumentException("condition type is Illegal");
        }
    }
/*
 Checks consistency while choosing card.
 */
    private void CheckActionZoneConsistencyForChoose() {
        int [] ActionZone = instruction.getActionZone();
        boolean val;
        int count = 0;

        if (ActionZone[0] != 0 || ActionZone[1] != 0 || ActionZone[2] != 0 || ActionZone[3] != 0) {
            val = true;
        } else {
            val = false;
        }

        if (val)
            count++;
        if (ActionZone[4] != 0)
            count++;
        if (ActionZone[5] != 0)
            count++;
        if (ActionZone[6] != 0)
            count++;

        if (count > 1)
            throw new IllegalArgumentException("Failed Action zone consistency test");

        if (ActionZone[4] == 3 || ActionZone[5] == 3)
            throw new IllegalArgumentException("Failed Action zone consistency test");
    }
/*
 Checks consistency while shuffle.
 */
    private void CheckActionZoneConsistencyForShuffle () {
        int[] ActionZone = instruction.getActionZone();
        int count = 0;

        if (ActionZone[0] != 0) {
           count++;
            if (ActionZone[0] == 3)
                count++;
        }

        if (ActionZone[1] != 0) {
            count++;
            if (ActionZone[1] == 3)
                count++;
        }

        if (ActionZone[2] != 0) {
            count++;
            if (ActionZone[2] == 3)
                count++;
        }

        if (ActionZone[3] != 0) {
            count++;
            if (ActionZone[3] == 3)
                count++;
        }

        if (ActionZone[4] != 0) {
            count++;
            if (ActionZone[4] == 3)
                count++;
        }

        if (ActionZone[5] != 0) {
            count++;
            if (ActionZone[5] == 3)
                count++;
        }

        if (ActionZone[6] != 0) {
            count++;
        }

        if (count > 1)
            throw new IllegalArgumentException("Failed Action zone consistency test");
    }

    private Cards getFirstTempCard() {
        return world.getMaze().getZoneList().get(6).getZoneArray().get(0);
    }

    private boolean PerformCascade(boolean status) {
        if (status == false)
            return false;

        int count = (instruction.getCount() > 0) ? instruction.getCount() : instruction.getConditionCount();
        if (instruction.getCascadeCondition() == CascadeType.Nil) {
            InstructionSkipped = false;
            return true;
        }

        if (instruction.getCascadeCondition() == CascadeType.AlwaysCascade && instruction.getNextInst() != null) {
            instruction = instruction.getNextInst();
            InstructionSkipped = false;
            return false;
        }

        if (instruction.getCascadeCondition() == CascadeType.IfTempZoneIsNonEmptyWithLessValue && instruction.getNextInst() != null) {
            if (world.getMaze().getZoneList().get(6).zoneSize() > 0 && world.getMaze().getZoneList().get(6).zoneSize() <= count) {
                instruction = instruction.getNextInst();
                InstructionSkipped = false;
                return false;
            } else {
                InstructionSkipped = false;
                return true;
            }
        }

        if (instruction.getCascadeCondition() == CascadeType.IfTempZoneIsEmpty && instruction.getNextInst() != null) {
            if (world.getMaze().getZoneList().get(6).zoneSize() == 0) {
                instruction = instruction.getNextInst();
                InstructionSkipped = false;
                return false;
            } else {
                InstructionSkipped = false;
                return true;
            }
        }


        if (instruction.getCascadeCondition() == CascadeType.IfTempZoneIsNonEmptyWithMoreOrEqualValue && instruction.getNextInst() != null) {
            if (world.getMaze().getZoneList().get(6).zoneSize() >= count) {
                instruction = instruction.getNextInst();
                InstructionSkipped = false;
                return false;
            } else {
                InstructionSkipped = false;
                return true;
            }
        }

        if (instruction.getCascadeCondition() == CascadeType.IfTempZoneIsNonEmptyAndSetCount && instruction.getNextInst() != null) {
            if (world.getMaze().getZoneList().get(6).zoneSize() > 0) {
                instruction.getNextInst().setCount(world.getMaze().getZoneList().get(6).zoneSize());
                instruction = instruction.getNextInst();
                InstructionSkipped = false;
                return false;
            } else {
                InstructionSkipped = false;
                return true;
            }
        }

        if (instruction.getCascadeCondition() == CascadeType.IfInstructionIsSkipped && instruction.getNextInst() != null) {
            if (InstructionSkipped) {
                instruction = instruction.getNextInst();
                InstructionSkipped = false;
                return false;
            } else {
                InstructionSkipped = false;
                return true;
            }
        }
        InstructionSkipped = false;
        return true;
    }

    private void SetCleanupInst(boolean status) {
        if (status == false)
            return;

        if (CurrentCard == null)
            return;

        if (CurrentCard.GridPosition().getZone() != 0 && CurrentCard.GridPosition().getZone() != 3)
            return;

        int index = instruction.getCleanUpIndex();
        if (instruction.getCleanUpPlacement() == CleanUpPlacement.Nil)
            return;

        if (!(index > 0))
            return;

        String Sinstruction = CurrentCard.cardInfo().PrimaryInstruction.get(index);
        String msg = new String();

        if (instruction.getCleanUpPlacement() == CleanUpPlacement.PostCleanUp) {
            if (instruction.getInstructionType() == InstructionType.SetAttr || instruction.getAction() == Action.SetAttr) {
                for (int i = 0; i < CollectCardList.size(); i++) {
                    String msgT;
                    int zone = CollectCardList.get(i).GridPosition().getZone();
                    if ((zone == 0 || zone == 3) || (zone == 7 || zone == 10)) {
                        InactiveCard card = (InactiveCard) CollectCardList.get(i);
                        InstructionSet Cinstruction = new InstructionSet(Sinstruction);
                        if (zone == 0 || zone == 3) {
                            card.AddTemporaryPostCleanup(Cinstruction);
                        } else {
                            card.AddTemporaryPreCleanup(Cinstruction);
                        }
                        if (zone < 6) {
                            zone = zone + 7;
                        } else {
                            zone = zone - 7;
                        }

                        msgT = zone + " " + card.GridPosition().getGridIndex() + " " + card.getNameID() + " " + 1 + "%" + Sinstruction;
                        msg = msg.concat(msgT);
                        if (i < CollectCardList.size() - 1) {
                            msg = msg.concat("#");
                        }
                    }
                }
            } else if (instruction.getInstructionType() == InstructionType.SelfSetAttr) {
                InstructionSet Cinstruction = new InstructionSet(Sinstruction);
                CurrentCard.AddTemporaryPostCleanup(Cinstruction);

                String msgT;
                int zone = CurrentCard.GridPosition().getZone() + 7;
                msgT = zone + " " + CurrentCard.GridPosition().getGridIndex() + " " + CurrentCard.getNameID() + " " + 1 + "%" + Sinstruction;
                msg = msg.concat(msgT);
            }

            if (msg.length() == 0)
                msg = null;

            NetworkUtil.sendDirectiveUpdates(world, DirectiveHeader.SetTempCleanUp, msg, null);
        }

        if (instruction.getCleanUpPlacement() == CleanUpPlacement.PreCleanUp) {
            if (instruction.getInstructionType() == InstructionType.SetAttr || instruction.getAction() == Action.SetAttr) {
                for (int i = 0; i < CollectCardList.size(); i++) {
                    String msgT;
                    int zone = CollectCardList.get(i).GridPosition().getZone();
                    if ((zone == 0 || zone == 3) || (zone == 7 || zone == 10)) {
                        InactiveCard card = (InactiveCard) CollectCardList.get(i);
                        InstructionSet Cinstruction = new InstructionSet(Sinstruction);
                        if (zone >= 0 && zone <= 3) {
                            card.AddTemporaryPreCleanup(Cinstruction);
                        } else {
                            card.AddTemporaryPostCleanup(Cinstruction);
                        }
                        if (zone < 6) {
                            zone = zone + 7;
                        } else {
                            zone = zone - 7;
                        }

                        msgT = zone + " " + card.GridPosition().getGridIndex() + " " + card.getNameID() + " " + 2 + "%" + Sinstruction;
                        msg = msg.concat(msgT);
                        if (i < CollectCardList.size() - 1) {
                            msg = msg.concat("#");
                        }
                    }
                }
            } else if (instruction.getInstructionType() == InstructionType.SelfSetAttr) {
                InstructionSet Cinstruction = new InstructionSet(Sinstruction);
                CurrentCard.AddTemporaryPreCleanup(Cinstruction);

                String msgT;
                int zone = CurrentCard.GridPosition().getZone() + 7;
                msgT = zone + " " + CurrentCard.GridPosition().getGridIndex() + " " + CurrentCard.getNameID() + " " + 1 + "%" + Sinstruction;
                msg = msg.concat(msgT);
            }

            if (msg.length() == 0)
                msg = null;

            NetworkUtil.sendDirectiveUpdates(world, DirectiveHeader.SetTempCleanUp, msg, null);
        }
    }
}
