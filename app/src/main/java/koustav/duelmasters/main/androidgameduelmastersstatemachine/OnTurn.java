package koustav.duelmasters.main.androidgameduelmastersstatemachine;

import java.util.ArrayList;

import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionID;
import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionSet;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.InactiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.ActiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Maze;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.TypeOfCard;
import koustav.duelmasters.main.androidgameduelmasterswidgetcoordinationtools.Actions;
import koustav.duelmasters.main.androidgameduelmasterswidgetcoordinationtools.Query;
import koustav.duelmasters.main.androidgameduelmasterswidgetcoordinationtools.Requests;
import koustav.duelmasters.main.androidgameduelmasterwidgetsimulation.LocationLayout;
import koustav.duelmasters.main.androidgameduelmasterswidgetcoordinationtools.UIRequest;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.PvPWidgetCoordinator;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.PvPWidgetCoordinator.*;
import koustav.duelmasters.main.androidgameduelmastersworlds.PvPWorld;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.WorldFlags;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Zone;
import koustav.duelmasters.main.androidgameduelmasterseventlogmodule.DirectiveHeader;
import koustav.duelmasters.main.androidgameduelmastersutil.ActUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.GetUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.InstSetUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.NetworkUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.SetUnsetUtil;
import koustav.duelmasters.main.androidgameduelmastersutillegacycode.UIUtil;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.ControllerButton;
import koustav.duelmasters.main.androidgameduelmasterwidgetsimulation.SimulationID;
import koustav.duelmasters.main.androidgameduelmasterwidgetsimulation.SimulationType;

/**
 * Created by Koustav on 3/28/2015.
 */
public class OnTurn {
    enum OnTurnState {
        S1,
        S2,
        S3,
        S4a,
        S4,
        S5,
        S6,
        S7,
        S8,
        S9,
        S10,
        S11,
        S12,
        S12a,
        S12b,
        S13,
        S14,
        S15,
        SX,
        SY,
        SY2,
    }
    OnTurnState S;
    PvPWorld world;
    InstructionSet NotYetSpreadCleanup;
    InstructionSet UnTapCreaturesInstruction;
    boolean SummonTapped;
    boolean UnTapAllAtEndOfTurn;

    SimulationID simulationID;

    // Stores the present state
    SubStates currentState;

    // Sub states
    SubStates IdealOnTurn;
    SubStates MayUnTapCreatureAtTheEndOfTurn;
    SubStates EndingTurn;
    SubStates SummonOrCastUpdate_1;
    SubStates SummonOrCastUpdate_2;
    SubStates SummonUpdate;
    SubStates EvolutionUpdate;
    SubStates CastUpdate;
    SubStates PreManaUpdate;
    SubStates ManaUpdate;
    SubStates DrawCardUpdate;
    SubStates FlagSpreadingUpdate;

    public OnTurn(PvPWorld world) {
        this.world = world;
        S = OnTurnState.S1;
        String instruction = InstSetUtil.GenerateAttributeCleanUpInstruction(3333, "NotYetSpread", 1);
        NotYetSpreadCleanup = new InstructionSet(instruction);
        String instruction2 = InstSetUtil.GenerateAttributeCleanUpInstruction(1, "Tapped", 1);
        UnTapCreaturesInstruction = new InstructionSet(instruction2);
        SummonTapped = false;
        UnTapAllAtEndOfTurn = false;

        simulationID = null;

        DefineStates();
        setCurrentState(IdealOnTurn);
    }

    private void setCurrentState(SubStates state) {
        currentState = state;
        currentState.StateSetting();
    }

    private void DefineStates() {
        IdealOnTurn = new SubStates() {
            @Override
            public boolean updateState() {
                UIRequest uiRequest = world.getWidgetCoordinator().getUIRequests();
                PvPWidgetCoordinator coordinator = world.getWidgetCoordinator();
                if (uiRequest.getRequest() == Requests.None) {
                    return false;
                }

                if (GetUtil.IsAllowedToEndTurn(world) && uiRequest.getRequest() == Requests.EndTurn) {
                    String CollectMarkedCard = InstSetUtil.GenerateCopyCardToTempZoneBasedOnAttribute("Tapped", 1);
                    InstructionSet CollectInst = new InstructionSet(CollectMarkedCard);
                    world.getInstructionHandler().setCardAndInstruction(null, CollectInst);
                    world.getInstructionHandler().execute();
                    coordinator.SendAction(Actions.ClearFocusCardList, null);
                    ArrayList<Cards> CollectedCardList = world.getMaze().getZoneList().get(Maze.temporaryZone).getZoneArray();
                    ArrayList<Cards> tmplist = new ArrayList<Cards>();
                    for (int i =0; i < CollectedCardList.size(); i++) {
                        InactiveCard card = (InactiveCard) CollectedCardList.get(i);
                        coordinator.SendAction(Actions.AddCardToFocusCardList, card);
                        if (GetUtil.IsMayUntapAtEndOfTheTurn(card)) {
                            tmplist.add(card);
                        }
                    }
                    UnTapAllAtEndOfTurn = GetUtil.IsUnTapAllAtEndOfTurn(world);
                    CollectedCardList.clear();
                    for (int i = 0; i < tmplist.size(); i++) {
                        CollectedCardList.add(tmplist.get(i));
                    }
                    if (!UnTapAllAtEndOfTurn) {
                        coordinator.SendAction(Actions.ClearFocusCardList, null);
                        if (CollectedCardList.size() > 0) {
                            coordinator.SendAction(Actions.AddCardToFocusCardList, CollectedCardList.get(0));
                        }
                    }
                    setCurrentState(MayUnTapCreatureAtTheEndOfTurn);
                }

                if (uiRequest.getRequest() == Requests.CardSelected) {
                    Cards card = uiRequest.getCard();
                    if (card == null) {
                        throw new RuntimeException("Invalid Condition");
                    }

                    ArrayList<ControllerButton> controllerButtons = new ArrayList<ControllerButton>();
                    int zone = card.GridPosition().getZone();
                    if (zone == Maze.battleZone) {
                        if (!GetUtil.IsTapped((InactiveCard)card)) {
                            if (GetUtil.IsAllowedToAttack((InactiveCard) card, world)) {
                                controllerButtons.add(ControllerButton.Attack);
                            }
                        }

                        if (!GetUtil.IsTapped((InactiveCard) card)) {
                            if (GetUtil.IsHasTapAbility((InactiveCard) card) && !GetUtil.CantUseTapAbility((InactiveCard) card) &&
                                    !(GetUtil.NewSummonedCreature((InactiveCard) card) && !GetUtil.NoSummoningSickness((InactiveCard) card))) {
                                controllerButtons.add(ControllerButton.TapAbility);
                            }
                        }
                    }

                    if (zone == Maze.hand) {
                        if (GetUtil.IsAllowedToSummonOrCast((InactiveCard) card, world)) {
                            controllerButtons.add(ControllerButton.SummonOrCast);
                        }

                        if (!world.getWorldFlag(WorldFlags.CantAddToMana)) {
                            controllerButtons.add(ControllerButton.AddToMana);
                        }
                    }

                    coordinator.SendAction(Actions.AddControlButton, controllerButtons);
                }

                if (uiRequest.getRequest() == Requests.Attack) {
                    InactiveCard card = (InactiveCard) uiRequest.getCard();
                    if (card == null) {
                        throw new RuntimeException("Invalid Condition");
                    }

                    int zone = card.GridPosition().getZone();
                    if (zone != Maze.battleZone) {
                        throw new RuntimeException("Invalid Condition");
                    }

                    if (!GetUtil.IsTapped(card)) {
                        if (GetUtil.IsAllowedToAttack(card, world)) {
                            world.setFetchCard(card);
                            world.getMaze().getZoneList().get(Maze.temporaryZone).getZoneArray().clear();
                            //S = OnTurnState.S3;
                        }
                    }

                    return false;
                }

                if (uiRequest.getRequest() == Requests.SummonOrCast) {
                    InactiveCard card = (InactiveCard) uiRequest.getCard();
                    if (card == null) {
                        throw new RuntimeException("Invalid Condition");
                    }

                    int zone = card.GridPosition().getZone();
                    if (zone != Maze.hand) {
                        throw new RuntimeException("Invalid Condition");
                    }
                    if (GetUtil.IsAllowedToSummonOrCast(card, world)) {
                        world.setFetchCard(card);
                        coordinator.SendAction(Actions.ClearSelectedCards, null);
                        coordinator.SendAction(Actions.Simulate, SimulationType.CardMovement,
                                LocationLayout.Hand, LocationLayout.HandPreBattleZone, card);
                        world.getEventLog().setRecording(true);
                        setCurrentState(SummonOrCastUpdate_1);
                    }

                    return false;
                }

                if (uiRequest.getRequest() == Requests.TapAbility) {
                    InactiveCard card = (InactiveCard) uiRequest.getCard();
                    if (card == null) {
                        throw new RuntimeException("Invalid Condition");
                    }

                    int zone = card.GridPosition().getZone();
                    if (zone != Maze.battleZone) {
                        throw new RuntimeException("Invalid Condition");
                    }

                    if (!GetUtil.IsTapped(card)) {
                        if (GetUtil.IsHasTapAbility(card) && !GetUtil.CantUseTapAbility(card)  &&
                                !(GetUtil.NewSummonedCreature(card) && !GetUtil.NoSummoningSickness(card))) {
                            world.setFetchCard(card);
                            ActiveCard Acard = (ActiveCard) card;
                            ArrayList<InstructionSet> instructions = Acard.getPrimaryInstructionForTheInstructionID(InstructionID.TapAbility);
                            world.getInstructionIteratorHandler().setCard(Acard);
                            world.getInstructionIteratorHandler().setInstructions(instructions);
                            world.getEventLog().setRecording(true);
                            SetUnsetUtil.SetTappedAttr(Acard);
                            world.getEventLog().registerEvent(Acard, false, 0 , "Tapped", true ,1);
                            // S = OnTurnState.S8;
                        }
                    }

                    return false;
                }

                if (uiRequest.getRequest() == Requests.AddToMana) {
                    InactiveCard card = (InactiveCard) uiRequest.getCard();
                    if (card == null) {
                        throw new RuntimeException("Invalid Condition");
                    }

                    int zone = card.GridPosition().getZone();
                    if (zone != Maze.hand) {
                        throw new RuntimeException("Invalid Condition");
                    }
                    if (!world.getWorldFlag(WorldFlags.CantAddToMana)) {
                        world.setFetchCard(card);
                        world.getEventLog().setRecording(true);
                        if (uiRequest.isDragged()) {
                            setCurrentState(PreManaUpdate);
                            coordinator.SendAction(Actions.Simulate, SimulationType.CardMovement,
                                    LocationLayout.Hand, LocationLayout.HandPreManaZone, card);
                        } else {
                            setCurrentState(ManaUpdate);
                            simulationID = (SimulationID)coordinator.SendAction(Actions.Simulate, SimulationType.CardMovement,
                                    LocationLayout.Hand, LocationLayout.ManaZone, card);
                        }
                    }

                    return false;
                }

                if (uiRequest.getRequest() == Requests.DeckStack) {
                    if ((world.getMaze().getZoneList().get(Maze.deck).zoneSize()> 0) &&
                            !world.getWorldFlag(WorldFlags.CantDrawCard)) {
                        setCurrentState(DrawCardUpdate);
                        world.getEventLog().setRecording(true);
                    }

                    return false;
                }

                return false;
            }

            @Override
            public void StateSetting() {
                world.getWidgetCoordinator().SendAction(Actions.ClearControlButton, null);
                world.getWidgetCoordinator().SetFlags(ZoomLevel.Button_Touched, new Expand[] {Expand.Mana_Z, Expand.Mana_OZ},
                        new Drag[] {Drag.Hand}, true, CardSelectMode.OFF, true);
            }
        };

        MayUnTapCreatureAtTheEndOfTurn = new SubStates() {
            @Override
            public boolean updateState() {
                UIRequest uiRequest = world.getWidgetCoordinator().getUIRequests();
                PvPWidgetCoordinator coordinator = world.getWidgetCoordinator();
                ArrayList<Cards> CollectedCardList = world.getMaze().getZoneList().get(Maze.temporaryZone).getZoneArray();

                if ((UnTapAllAtEndOfTurn || (CollectedCardList.size() > 0)) && uiRequest.getRequest() == Requests.None) {
                    return false;
                }

                if (UnTapAllAtEndOfTurn) {
                    if (uiRequest.getRequest() == Requests.Accept) {
                        world.getInstructionHandler().setCardAndInstruction(null, UnTapCreaturesInstruction);
                        world.getInstructionHandler().execute();
                        CollectedCardList.clear();
                        coordinator.SendAction(Actions.ClearFocusCardList, null);
                        UnTapAllAtEndOfTurn = false;
                    }

                    if (uiRequest.getRequest() == Requests.Decline) {
                        coordinator.SendAction(Actions.ClearFocusCardList, null);
                        if (CollectedCardList.size() > 0) {
                            coordinator.SendAction(Actions.AddCardToFocusCardList, CollectedCardList.get(0));
                        }
                        UnTapAllAtEndOfTurn = false;
                    }
                } else if (CollectedCardList.size() > 0) {
                    InactiveCard card = (InactiveCard) CollectedCardList.get(0);
                    if (uiRequest.getRequest() == Requests.Accept) {
                        SetUnsetUtil.UnSetTappedAttr(card);
                        CollectedCardList.remove(card);
                        coordinator.SendAction(Actions.ClearFocusCardList, null);
                        if (CollectedCardList.size() > 0) {
                            coordinator.SendAction(Actions.AddCardToFocusCardList, CollectedCardList.get(0));
                        }
                    }

                    if (uiRequest.getRequest() == Requests.Decline) {
                        CollectedCardList.remove(card);
                        coordinator.SendAction(Actions.ClearFocusCardList, null);
                        if (CollectedCardList.size() > 0) {
                            coordinator.SendAction(Actions.AddCardToFocusCardList, CollectedCardList.get(0));
                        }
                    }
                } else {
                    UnTapAllAtEndOfTurn = false;
                    setCurrentState(EndingTurn);
                }
                return false;
            }

            @Override
            public void StateSetting() {
                ArrayList<Cards> CollectedCardList = world.getMaze().getZoneList().get(Maze.temporaryZone).getZoneArray();
                if (UnTapAllAtEndOfTurn || CollectedCardList.size() > 0) {
                    ArrayList<ControllerButton> controllerButtons = new ArrayList<ControllerButton>();
                    controllerButtons.add(ControllerButton.Accept);
                    controllerButtons.add(ControllerButton.Decline);
                    world.getWidgetCoordinator().SendAction(Actions.AddControlButton, controllerButtons);
                } else {
                    world.getWidgetCoordinator().SendAction(Actions.ClearControlButton, (Object) null);
                }
                world.getWidgetCoordinator().SetFlags(ZoomLevel.Button_Touched, new Expand[] {Expand.Nil},
                        new Drag[] {Drag.Nil}, false, CardSelectMode.OFF, true);
            }
        };

        EndingTurn = new SubStates() {
            @Override
            public boolean updateState() {
                if (!NetworkUtil.sendDirectiveUpdates(world, DirectiveHeader.EndOfTurn, null, null)) {
                    //handle the case when socket is closed
                }
                setCurrentState(IdealOnTurn);

                return true;
            }

            @Override
            public void StateSetting() {
                world.getWidgetCoordinator().SendAction(Actions.ClearControlButton, (Object) null);
                world.getWidgetCoordinator().SetFlags(ZoomLevel.Button_Touched, new Expand[] {Expand.Nil},
                        new Drag[] {Drag.Nil}, false, CardSelectMode.OFF, true);
            }
        };

        SummonOrCastUpdate_1 = new SubStates() {
            @Override
            public boolean updateState() {
                UIRequest uiRequest = world.getWidgetCoordinator().getUIRequests();
                if (uiRequest.getRequest() == Requests.None) {
                    return false;
                }

                ActiveCard SummoningOrCastCard = (ActiveCard) world.getFetchCard();
                ArrayList<Cards> ManaCards = world.getMaze().getZoneList().get(Maze.manaZone).getZoneArray();
                PvPWidgetCoordinator coordinator = world.getWidgetCoordinator();
                ArrayList<Cards> CollectedCardList = (ArrayList) coordinator.GetInfo(Query.GetSelectedCardList, null);
                int selectedCardCount = (int) coordinator.GetInfo(Query.SelectedCardCount, null);

                if (selectedCardCount == GetUtil.getTotalManaCost(SummoningOrCastCard)) {
                    if (uiRequest.getRequest() == Requests.Accept){
                        ArrayList<Cards> finalList = world.getMaze().getZoneList().get(Maze.temporaryZone).getZoneArray();
                        finalList.clear();
                        ArrayList<Cards> NonPileCards = new ArrayList<Cards>();
                        for (int i = 0; i < CollectedCardList.size(); i++) {
                            Cards card = CollectedCardList.get(i);
                            finalList.add(card);
                            if (!(boolean)coordinator.GetInfo(Query.IsCardSelectedPile, card)) {
                                NonPileCards.add(card);
                            }
                        }
                        simulationID = (SimulationID)coordinator.SendAction(Actions.Simulate, SimulationType.CardMovement,
                                LocationLayout.ManaZone, LocationLayout.ManaNewCoupleZone, NonPileCards);
                        setCurrentState(SummonOrCastUpdate_2);
                        return false;
                    }
                }

                if (uiRequest.getRequest() == Requests.Decline){
                    setCurrentState(IdealOnTurn);
                    coordinator.SendAction(Actions.ClearSelectedCards, null);
                    coordinator.SendAction(Actions.Simulate, SimulationType.CardMovement,
                            LocationLayout.HandPreBattleZone, LocationLayout.Hand, SummoningOrCastCard);
                    coordinator.SendAction(Actions.Simulate, SimulationType.CardMovement,
                            LocationLayout.ManaNewCoupleZone, LocationLayout.ManaZone);
                    world.getEventLog().setRecording(false);
                    return false;
                }

                if (uiRequest.getRequest() == Requests.CardSelected) {
                    Cards card = uiRequest.getCard();

                    if (card == null) {
                        throw new RuntimeException("Invalid Condition");
                    }

                    if (card.GridPosition().getZone() != Maze.manaZone) {
                        return false;
                    }

                    if (!ManaCards.contains(card)) {
                        throw new RuntimeException("Must be part of Mana zone");
                    }

                    InactiveCard SelectedCard = (InactiveCard) card;
                    if (GetUtil.IsTapped(SelectedCard)) {
                        return false;
                    }

                    if ((boolean)coordinator.GetInfo(Query.IsCardSelected, SelectedCard)) {
                        coordinator.SendAction(Actions.RemoveCardFromSelectedList, SelectedCard);
                        if (uiRequest.isDragged()) {
                            coordinator.SendAction(Actions.Simulate, SimulationType.CardMovement,
                                    LocationLayout.ManaNewCoupleZone, LocationLayout.ManaZone, SelectedCard);
                        }
                        ArrayList<ControllerButton> controllerButtons = new ArrayList<ControllerButton>();
                        controllerButtons.add(ControllerButton.Decline);
                        world.getWidgetCoordinator().SendAction(Actions.AddControlButton, controllerButtons);
                        return false;
                    }

                    if (!(selectedCardCount < GetUtil.getTotalManaCost(SummoningOrCastCard))) {
                        return false;
                    }

                    boolean matchedCivilization = false;
                    for (int i = 0; i < CollectedCardList.size(); i++) {
                        InactiveCard tcard = (InactiveCard) CollectedCardList.get(i);
                        if (GetUtil.RequiredCivilization(tcard, SummoningOrCastCard.getCivilization())) {
                            matchedCivilization = true;
                            break;
                        }
                    }

                    if (matchedCivilization) {
                        coordinator.SendAction(Actions.AddCardToSelectedList, SelectedCard, uiRequest.isDragged());
                        if (uiRequest.isDragged()) {
                            coordinator.SendAction(Actions.Simulate, SimulationType.CardMovement,
                                    LocationLayout.ManaZone, LocationLayout.ManaNewCoupleZone, SelectedCard);
                        }
                    } else {
                        int NumberOfCollectedCard = CollectedCardList.size();
                        if (NumberOfCollectedCard < (SummoningOrCastCard.getCost() - 1)) {
                            coordinator.SendAction(Actions.AddCardToSelectedList, SelectedCard, uiRequest.isDragged());
                            if (uiRequest.isDragged()) {
                                coordinator.SendAction(Actions.Simulate, SimulationType.CardMovement,
                                        LocationLayout.ManaZone, LocationLayout.ManaNewCoupleZone, SelectedCard);
                            }
                        } else {
                            if (GetUtil.RequiredCivilization(SelectedCard, SummoningOrCastCard.getCivilization())) {
                                coordinator.SendAction(Actions.AddCardToSelectedList, SelectedCard, uiRequest.isDragged());
                                if (uiRequest.isDragged()) {
                                    coordinator.SendAction(Actions.Simulate, SimulationType.CardMovement,
                                            LocationLayout.ManaZone, LocationLayout.ManaNewCoupleZone, SelectedCard);
                                }
                            }
                        }
                    }

                    if (CollectedCardList.size() == GetUtil.getTotalManaCost(SummoningOrCastCard)) {
                        ArrayList<ControllerButton> controllerButtons = new ArrayList<ControllerButton>();
                        controllerButtons.add(ControllerButton.Accept);
                        controllerButtons.add(ControllerButton.Decline);
                        world.getWidgetCoordinator().SendAction(Actions.AddControlButton, controllerButtons);
                    }
                }
                return false;
            }

            @Override
            public void StateSetting() {
                ArrayList<ControllerButton> controllerButtons = new ArrayList<ControllerButton>();
                controllerButtons.add(ControllerButton.Decline);
                world.getWidgetCoordinator().SendAction(Actions.AddControlButton, controllerButtons);
                world.getWidgetCoordinator().SetFlags(ZoomLevel.Button, new Expand[] {Expand.Mana_Z},
                        new Drag[] {Drag.Mana_Z}, false, CardSelectMode.ON, true);
            }
        };

        SummonOrCastUpdate_2 = new SubStates() {
            @Override
            public boolean updateState() {
                PvPWidgetCoordinator coordinator = world.getWidgetCoordinator();
                if (!(boolean)coordinator.GetInfo(Query.IsSimulationDone, SimulationType.CardMovement,
                        simulationID)) {
                    return false;
                }

                ActiveCard SummoningOrCastCard = (ActiveCard) world.getFetchCard();
                ArrayList<Cards> CollectedCardList = world.getMaze().getZoneList().get(Maze.temporaryZone).getZoneArray();
                for (int i = 0; i < CollectedCardList.size(); i++) {
                    InactiveCard tcard = (InactiveCard) CollectedCardList.get(i);
                    SetUnsetUtil.SetTappedAttr(tcard);
                    world.getEventLog().registerEvent(tcard, false, 0 , "Tapped", true ,1);
                }
                CollectedCardList.clear();
                coordinator.SendAction(Actions.ClearSelectedCards, null);
                coordinator.SendAction(Actions.FreezeSelectedManaCards, null);

                if (SummoningOrCastCard.getType() == TypeOfCard.Creature) {
                    SummonTapped = false;
                    if (GetUtil.SummonTapped(SummoningOrCastCard)) {
                        SummonTapped = true;
                    }
                    boolean ActiveWaveStriker = GetUtil.IsActiveWaveStriker(SummoningOrCastCard);
                    String SummonCardInstruction = InstSetUtil.GenerateSelfChangeZoneInstruction(Maze.battleZone);
                    InstructionSet instruction = new InstructionSet(SummonCardInstruction);
                    world.getInstructionHandler().setCardAndInstruction(SummoningOrCastCard, instruction);
                    world.getInstructionHandler().execute();
                    if (SummonTapped) {
                        SetUnsetUtil.SetTappedAttr((InactiveCard) world.getFetchCard());
                        world.getEventLog().registerEvent(world.getFetchCard(), false, 0 , "Tapped", true ,1);
                        SummonTapped = false;
                    }
                    simulationID = (SimulationID)coordinator.SendAction(Actions.Simulate, SimulationType.CardMovement,
                            LocationLayout.HandPreBattleZone, LocationLayout.BattleZone, world.getFetchCard());

                    //sendeventlog
                    String msg = world.getEventLog().getAndClearEvents();
                    NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);

                    // Get the instructions to Run
                    if (((ActiveCard)world.getFetchCard()).getPrimaryInstructionForTheInstructionID(InstructionID.SummonOrCastAbility) == null &&
                            !ActiveWaveStriker && !GetUtil.IsSurvivor((ActiveCard) world.getFetchCard())) {
                        setCurrentState(FlagSpreadingUpdate);
                        world.getEventLog().setRecording(false);
                        world.setFetchCard(null);
                    } else {
                        world.getInstructionIteratorHandler().setCard((InactiveCard) world.getFetchCard());
                        ArrayList<InstructionSet> instructions =
                                ((ActiveCard) world.getFetchCard()).getPrimaryInstructionForTheInstructionID(InstructionID.SummonOrCastAbility);
                        if (GetUtil.IsWaveStriker((InactiveCard) world.getFetchCard()) && ActiveWaveStriker) {
                            ArrayList<InstructionSet> tmp =
                                    ((ActiveCard) world.getFetchCard()).getPrimaryInstructionForTheInstructionID(InstructionID.WaveStrikerSummonOrCastAbility);
                            if (instructions != null && tmp != null) {
                                for (int i = 0; i < tmp.size(); i++) {
                                    instructions.add(tmp.get(i));
                                }
                            } else if (tmp != null) {
                                instructions = tmp;
                            }
                        }

                        if (GetUtil.IsSurvivor((ActiveCard) world.getFetchCard())) {
                            Zone zone = world.getMaze().getZoneList().get(Maze.battleZone);
                            ArrayList<InstructionSet> tmp = null;
                            for (int i = 0; i < zone.zoneSize(); i++) {
                                ActiveCard tcard = (ActiveCard) zone.getZoneArray().get(i);
                                if ((tcard != world.getFetchCard()) && GetUtil.IsSurvivor(tcard)) {
                                    ArrayList<InstructionSet> tmp2 = tcard.getPrimaryInstructionForTheInstructionID(InstructionID.SummonOrCastAbility);
                                    if (tmp != null && tmp2 != null) {
                                        for (int j = 0; j < tmp.size(); j++) {
                                            tmp.add(tmp.get(j));
                                        }
                                    } else if (tmp2 != null) {
                                        tmp = tmp2;
                                    }
                                }
                            }
                            if (instructions != null && tmp != null) {
                                for (int i = 0; i < tmp.size(); i++) {
                                    instructions.add(tmp.get(i));
                                }
                            } else if (tmp != null) {
                                instructions = tmp;
                            }
                        }

                        if (instructions.size() > 0) {
                            world.getInstructionIteratorHandler().setInstructions(instructions);
                            setCurrentState(SummonUpdate);
                        } else {
                            setCurrentState(FlagSpreadingUpdate);
                            world.getEventLog().setRecording(false);
                            world.setFetchCard(null);
                        }

                        return false;
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
                    setCurrentState(CastUpdate);
                    return false;
                }

                if (SummoningOrCastCard.getType() == TypeOfCard.Evolution) {
                    SummonTapped = false;
                    if (GetUtil.SummonTapped(SummoningOrCastCard)) {
                        SummonTapped = true;
                    }
                    String EvolutionInst = InstSetUtil.GenerateEvolutionInstruction(SummoningOrCastCard.getEvolutionCompareString());
                    InstructionSet Einstruction = new InstructionSet(EvolutionInst);
                    ArrayList<InstructionSet> instructions = new ArrayList<InstructionSet>();
                    instructions.add(Einstruction);
                    world.getInstructionIteratorHandler().setCard(SummoningOrCastCard);
                    world.getInstructionIteratorHandler().setInstructions(instructions);

                    //sendEventlog
                    String msg = world.getEventLog().getAndClearEvents();
                    NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
                    setCurrentState(EvolutionUpdate);
                    return false;
                }

                return false;
            }

            @Override
            public void StateSetting() {
                world.getWidgetCoordinator().SendAction(Actions.ClearControlButton, (Object) null);
                world.getWidgetCoordinator().SetFlags(ZoomLevel.Button_Touched, new Expand[] {Expand.Nil},
                        new Drag[] {Drag.Nil}, false, CardSelectMode.OFF, true);
            }
        };

        SummonUpdate = new SubStates() {
            @Override
            public boolean updateState() {
                PvPWidgetCoordinator coordinator = world.getWidgetCoordinator();
                if (!(boolean)coordinator.GetInfo(Query.IsSimulationDone, SimulationType.CardMovement,
                        simulationID)) {
                    return false;
                }
                if (world.getInstructionIteratorHandler().update()) {
                    setCurrentState(IdealOnTurn);
                    world.setFetchCard(null);
                    world.getEventLog().setRecording(false);
                }
                return false;
            }

            @Override
            public void StateSetting() {
                world.getWidgetCoordinator().SendAction(Actions.ClearControlButton, (Object) null);
                world.getWidgetCoordinator().SetFlags(ZoomLevel.Button_Touched, new Expand[] {Expand.Nil},
                        new Drag[] {Drag.Nil}, false, CardSelectMode.OFF, true);
            }
        };

        EvolutionUpdate = new SubStates() {
            @Override
            public boolean updateState() {
                if (world.getInstructionIteratorHandler().update()) {
                    ActiveCard card = (ActiveCard) world.getFetchCard();
                    boolean ActiveWaveStriker = GetUtil.IsActiveWaveStriker(card);
                    String CollectAttackMarkedCard = InstSetUtil.GenerateCopyCardToTempZoneBasedOnAttribute("MarkedCard", 1);
                    InstructionSet CollectInst = new InstructionSet(CollectAttackMarkedCard);
                    world.getInstructionHandler().setCardAndInstruction(card, CollectInst);
                    world.getInstructionHandler().execute();
                    ArrayList<Cards> CollectedCardList = world.getMaze().getZoneList().get(Maze.temporaryZone).getZoneArray();
                    if (CollectedCardList.size() != 1)
                        throw new IllegalArgumentException("Something went wrong while evolution");
                    SetUnsetUtil.UnSetMarkedCard((InactiveCard) CollectedCardList.get(0));
                    ArrayList<InstructionSet> CleanUpInst = ((InactiveCard)CollectedCardList.get(0)).getCrossInstructionForTheInstructionID(InstructionID.CleanUp);
                    if (CleanUpInst != null) {
                        for (int i = 0; i < CleanUpInst.size(); i++) {
                            world.getInstructionHandler().setCardAndInstruction((InactiveCard) CollectedCardList.get(0), CleanUpInst.get(i));
                            world.getInstructionHandler().execute();
                        }
                    }
                    InactiveCard card2 = (InactiveCard) ActUtil.EvolveCreature(card, CollectedCardList.get(0), world);
                    world.setFetchCard(card2);
                    if (SummonTapped) {
                        SetUnsetUtil.SetTappedAttr((InactiveCard) world.getFetchCard());
                        world.getEventLog().registerEvent(world.getFetchCard(), false, 0 , "Tapped", true ,1);
                        SummonTapped = false;
                    }

                    //sendeventlog
                    String msg = world.getEventLog().getAndClearEvents();
                    NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);

                    if (((ActiveCard)world.getFetchCard()).getPrimaryInstructionForTheInstructionID(InstructionID.SummonOrCastAbility) == null &&
                            !ActiveWaveStriker && !GetUtil.IsSurvivor((ActiveCard) world.getFetchCard())) {
                        setCurrentState(FlagSpreadingUpdate);
                        world.getEventLog().setRecording(false);
                        world.setFetchCard(null);
                    } else {
                        world.getInstructionIteratorHandler().setCard((InactiveCard) world.getFetchCard());
                        ArrayList<InstructionSet> instructions =
                                ((ActiveCard) world.getFetchCard()).getPrimaryInstructionForTheInstructionID(InstructionID.SummonOrCastAbility);
                        if (GetUtil.IsWaveStriker((InactiveCard) world.getFetchCard()) && ActiveWaveStriker) {
                            ArrayList<InstructionSet> tmp =
                                    ((ActiveCard) world.getFetchCard()).getPrimaryInstructionForTheInstructionID(InstructionID.WaveStrikerSummonOrCastAbility);
                            if (instructions != null && tmp != null) {
                                for (int i = 0; i < tmp.size(); i++) {
                                    instructions.add(tmp.get(i));
                                }
                            } else if (tmp != null) {
                                instructions = tmp;
                            }
                        }

                        if (GetUtil.IsSurvivor((ActiveCard) world.getFetchCard())) {
                            Zone zone = world.getMaze().getZoneList().get(0);
                            ArrayList<InstructionSet> tmp = null;
                            for (int i = 0; i < zone.zoneSize(); i++) {
                                ActiveCard tcard = (ActiveCard) zone.getZoneArray().get(i);
                                if ((tcard != world.getFetchCard()) && GetUtil.IsSurvivor(tcard)) {
                                    ArrayList<InstructionSet> tmp2 = tcard.getPrimaryInstructionForTheInstructionID(InstructionID.SummonOrCastAbility);
                                    if (tmp != null && tmp2 != null) {
                                        for (int j = 0; j < tmp.size(); j++) {
                                            tmp.add(tmp.get(j));
                                        }
                                    } else if (tmp2 != null) {
                                        tmp = tmp2;
                                    }
                                }
                            }
                            if (instructions != null && tmp != null) {
                                for (int i = 0; i < tmp.size(); i++) {
                                    instructions.add(tmp.get(i));
                                }
                            } else if (tmp != null) {
                                instructions = tmp;
                            }
                        }

                        if (instructions.size() > 0) {
                            world.getInstructionIteratorHandler().setInstructions(instructions);
                            setCurrentState(SummonUpdate);
                        } else {
                            setCurrentState(FlagSpreadingUpdate);
                            world.getEventLog().setRecording(false);
                            world.setFetchCard(null);
                        }
                    }
                }
                return false;
            }

            @Override
            public void StateSetting() {
                world.getWidgetCoordinator().SendAction(Actions.ClearControlButton, (Object) null);
                world.getWidgetCoordinator().SetFlags(ZoomLevel.Button_Touched, new Expand[] {Expand.Nil},
                        new Drag[] {Drag.Nil}, false, CardSelectMode.OFF, true);
            }
        };

        CastUpdate = new SubStates() {
            @Override
            public boolean updateState() {
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

                    // send event
                    String msg = world.getEventLog().getAndClearEvents();
                    NetworkUtil.sendDirectiveUpdates(world, DirectiveHeader.ApplyEvents, msg, null);
                    setCurrentState(IdealOnTurn);
                    world.setFetchCard(null);
                    world.getEventLog().setRecording(false);
                }
                return false;
            }

            @Override
            public void StateSetting() {
                world.getWidgetCoordinator().SendAction(Actions.ClearControlButton, (Object) null);
                world.getWidgetCoordinator().SetFlags(ZoomLevel.Button_Touched, new Expand[] {Expand.Nil},
                        new Drag[] {Drag.Nil}, false, CardSelectMode.OFF, true);
            }
        };

        PreManaUpdate = new SubStates() {
            @Override
            public boolean updateState() {
                PvPWidgetCoordinator coordinator = world.getWidgetCoordinator();
                UIRequest uiRequest = world.getWidgetCoordinator().getUIRequests();
                if (uiRequest.getRequest() == Requests.None) {
                    return false;
                }

                if (uiRequest.getRequest() == Requests.Accept) {
                    setCurrentState(ManaUpdate);
                    simulationID = (SimulationID)coordinator.SendAction(Actions.Simulate, SimulationType.CardMovement,
                            LocationLayout.HandPreManaZone, LocationLayout.ManaZone, world.getFetchCard());
                }

                if (uiRequest.getRequest() == Requests.Decline) {
                    setCurrentState(IdealOnTurn);
                    coordinator.SendAction(Actions.Simulate, SimulationType.CardMovement,
                            LocationLayout.HandPreManaZone, LocationLayout.Hand, world.getFetchCard());
                    world.setFetchCard(null);
                    world.getEventLog().setRecording(false);
                }
                return false;
            }

            @Override
            public void StateSetting() {
                ArrayList<ControllerButton> controllerButtons = new ArrayList<ControllerButton>();
                controllerButtons.add(ControllerButton.Accept);
                controllerButtons.add(ControllerButton.Decline);
                world.getWidgetCoordinator().SendAction(Actions.AddControlButton, controllerButtons);
                world.getWidgetCoordinator().SetFlags(ZoomLevel.Button, new Expand[] {Expand.Nil},
                        new Drag[] {Drag.Nil}, false, CardSelectMode.OFF, true);
            }
        };

        ManaUpdate = new SubStates() {
            @Override
            public boolean updateState() {
                PvPWidgetCoordinator coordinator = world.getWidgetCoordinator();
                if (!(boolean)coordinator.GetInfo(Query.IsSimulationDone, SimulationType.CardMovement,
                        simulationID)) {
                    return false;
                }
                boolean addtomanaTapped = false;
                if (GetUtil.AddToManaTapped((InactiveCard)world.getFetchCard())) {
                    addtomanaTapped = true;
                }
                String AddToManaInstruction = InstSetUtil.GenerateSelfChangeZoneInstruction(Maze.manaZone);
                InstructionSet instruction = new InstructionSet(AddToManaInstruction);
                world.getInstructionHandler().setCardAndInstruction((InactiveCard)world.getFetchCard(),instruction);
                world.getInstructionHandler().execute();
                if (addtomanaTapped) {
                    SetUnsetUtil.SetTappedAttr((InactiveCard) world.getFetchCard());
                    world.getEventLog().registerEvent(world.getFetchCard(), false, 0 , "Tapped", true ,1);
                }
                world.setFetchCard(null);
                world.setWorldFlag(WorldFlags.CantAddToMana);
                setCurrentState(FlagSpreadingUpdate);
                world.getEventLog().setRecording(false);
                //sendeventlog
                String msg = world.getEventLog().getAndClearEvents();
                NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
                return false;
            }

            @Override
            public void StateSetting() {
                world.getWidgetCoordinator().SendAction(Actions.ClearControlButton, (Object) null);
                world.getWidgetCoordinator().SetFlags(ZoomLevel.Button_Touched, new Expand[] {Expand.Nil},
                        new Drag[] {Drag.Nil}, false, CardSelectMode.OFF, true);
            }
        };

        DrawCardUpdate = new SubStates() {
            @Override
            public boolean updateState() {
                PvPWidgetCoordinator coordinator = world.getWidgetCoordinator();
                coordinator.SendAction(Actions.Simulate, SimulationType.CardMovement,
                        LocationLayout.Deck, LocationLayout.Hand, 1);
                String DrawCardInstruction = InstSetUtil.GenerateDrawCardInstruction();
                InstructionSet instruction = new InstructionSet(DrawCardInstruction);
                world.getInstructionHandler().setCardAndInstruction(null,instruction);
                world.getInstructionHandler().execute();
                world.setWorldFlag(WorldFlags.CantDrawCard);
                setCurrentState(FlagSpreadingUpdate);
                world.getEventLog().setRecording(false);

                //sendeventlog
                String msg = world.getEventLog().getAndClearEvents();
                NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
                return false;
            }

            @Override
            public void StateSetting() {
                world.getWidgetCoordinator().SendAction(Actions.ClearControlButton, (Object) null);
                world.getWidgetCoordinator().SetFlags(ZoomLevel.Button_Touched, new Expand[] {Expand.Nil},
                        new Drag[] {Drag.Nil}, false, CardSelectMode.OFF, true);
            }
        };

        FlagSpreadingUpdate = new SubStates() {
            @Override
            public boolean updateState() {
                SetUnsetUtil.SpreadingFlagAttr(world);
                world.getInstructionHandler().setCardAndInstruction(null, NotYetSpreadCleanup);
                world.getInstructionHandler().execute();
                setCurrentState(IdealOnTurn);
                return false;
            }

            @Override
            public void StateSetting() {
                world.getWidgetCoordinator().SendAction(Actions.ClearControlButton, (Object) null);
                world.getWidgetCoordinator().SetFlags(ZoomLevel.Button_Touched, new Expand[] {Expand.Nil},
                        new Drag[] {Drag.Nil}, false, CardSelectMode.OFF, true);
            }
        };
    }
/*
 Main API which controls the states and calls other API according to 'S' value
 */
    /*
    public boolean update(){
        boolean status = false;
        if (S == OnTurnState.S1)
            IdealOnTurn(); // done
        if (S == OnTurnState.S2)
            SummonOrCastUpdate(); // done
        if (S == OnTurnState.S3)
            AttackUpdate();
        if (S == OnTurnState.S4a)
            EvolutionUpdate(); // done
        if (S == OnTurnState.S4)
            SummonUpdate(); //done
        if (S == OnTurnState.S5)
            CastUpdate(); //done
        if (S == OnTurnState.S6)
            ManaUpdate();  // done
        if (S == OnTurnState.S7)
            ShowInfoUpdate();
        if (S == OnTurnState.S8)
            TapAbilityUpdate();
        if (S == OnTurnState.S9)
            DrawCardUpdate(); // done
        if (S == OnTurnState.S10)
            ShieldUpdate();
        if (S == OnTurnState.S11)
            PostIfAttackUpdate();
        if (S == OnTurnState.S12)
            BlockerUpdate();
        if (S == OnTurnState.S12a)
            OpponentBlockerDirective();
        if (S == OnTurnState.S12b)
            PostIfUnBlockedUpdate();
        if (S == OnTurnState.S13)
            status = CreatureBattleUpdate();
        if (S == OnTurnState.S14)
            status = ShieldBreakingUpdate();
        if (S == OnTurnState.S15)
            ShowDeckCards();
        if (S == OnTurnState.SX)
            FlagSpreadingUpdate(); // done
        if (S == OnTurnState.SY)
            MayUnTapAtTheEndOfTurnCreatureUpdate(); // done
        if (S == OnTurnState.SY2)
            status = TurnEnding(); // done

        return status;
    }
    */
    public boolean update(){
        return currentState.updateState();
    }
/*
 This is the Ideal state when User make decision what he/she is going to do next (attack,summon,cast,draw card etc.)
 S1
 done
 */
    private void IdealOnTurn() {
        if (GetUtil.IsAllowedToEndTurn(world) && UIUtil.TouchedSkippedButton(world)) {
            String CollectAttackMarkedCard = InstSetUtil.GenerateCopyCardToTempZoneBasedOnAttribute("Tapped", 1);
            InstructionSet CollectInst = new InstructionSet(CollectAttackMarkedCard);
            world.getInstructionHandler().setCardAndInstruction(null, CollectInst);
            world.getInstructionHandler().execute();
            ArrayList<Cards> CollectedCardList = world.getMaze().getZoneList().get(6).getZoneArray();
            ArrayList<Cards> tmplist = new ArrayList<Cards>();
            for (int i =0; i < CollectedCardList.size(); i++) {
                InactiveCard card = (InactiveCard) CollectedCardList.get(i);
                if (GetUtil.IsMayUntapAtEndOfTheTurn(card)) {
                    tmplist.add(card);
                }
            }
            CollectedCardList.clear();
            for (int i = 0; i < tmplist.size(); i++) {
                CollectedCardList.add(tmplist.get(i));
            }
            UnTapAllAtEndOfTurn = GetUtil.IsUnTapAllAtEndOfTurn(world);
            world.setWorldFlag(WorldFlags.UserDecisionMakingMode);
            world.setWorldFlag(WorldFlags.AcceptCardSelectingMode);
            world.setWorldFlag(WorldFlags.MaySkipCardSelectingMode);
            S = OnTurnState.SY;
            return;
        }

        if (UIUtil.WorldFetchCard(world)) {
            return;
        }

        if ((world.getFetchCard() != null) && UIUtil.TouchedInfoTabEnterButton(world)) {
            this.S = OnTurnState.S7;
            world.setWorldFlag(WorldFlags.DisplayInfo);
            return;
        }

        if (UIUtil.TouchedAttackShieldOrPlayerButton(world)) {
            this.S = OnTurnState.S15;
            world.setWorldFlag(WorldFlags.DisplayDeckCard);
            return;
        }

        if (UIUtil.TouchedLeftPlayButton(world)) {
            InactiveCard card = (InactiveCard) world.getFetchCard();
            if (card == null) {
                return;
            }
            int zone = card.GridPosition().getZone();
            if (zone != 0 && zone != 3) {
                return;
            }

            if (zone == 0) {
                if (!GetUtil.IsTapped(card)) {
                    if (GetUtil.IsAllowedToAttack(card, world)) {
                        this.S = OnTurnState.S3;
                        world.getMaze().getZoneList().get(6).getZoneArray().clear();
                        world.setWorldFlag(WorldFlags.AttackSelectMode);
                    }
                }

                return;
            }

            if (zone == 3) {
                if (GetUtil.IsAllowedToSummonOrCast(card, world)) {
                    this.S = OnTurnState.S2;
                    world.getMaze().getZoneList().get(6).getZoneArray().clear();
                    world.setWorldFlag(WorldFlags.ManaSelectMode);
                    world.getEventLog().setRecording(true);
                }

                return;
            }
        }

        if (UIUtil.TouchedRightPlayButton(world)) {
            InactiveCard card = (InactiveCard) world.getFetchCard();
            if (card == null){
                return;
            }
            int zone = card.GridPosition().getZone();
            if (zone !=0 && zone != 3){
                return;
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
                        return;
                    }
                } else {
                    return;
                }
            }

            if (zone == 3) {
                if (!world.getWorldFlag(WorldFlags.CantAddToMana)) {
                    this.S = OnTurnState.S6;
                    world.getEventLog().setRecording(true);
                }

                return;
            }
        }

        if (UIUtil.TouchedDrawCardButton(world)) {
            if ((world.getMaze().getZoneList().get(5).zoneSize()> 0) &&
                    !world.getWorldFlag(WorldFlags.CantDrawCard)) {
                this.S = OnTurnState.S9;
                world.getEventLog().setRecording(true);
            }

            return;
        }

        return;
    }
/*
  This API update the creature that can be UnTapped at the end of turn
  SY
  Done
 */
    private void MayUnTapAtTheEndOfTurnCreatureUpdate() {
        ArrayList<Cards> CollectedCardList = world.getMaze().getZoneList().get(6).getZoneArray();

        if (UnTapAllAtEndOfTurn) {
            if (UIUtil.TouchedAcceptButton(world)) {
                world.getInstructionHandler().setCardAndInstruction(null, UnTapCreaturesInstruction);
                world.getInstructionHandler().execute();
                CollectedCardList.clear();
                UnTapAllAtEndOfTurn = false;
            }

            if (UIUtil.TouchedDeclineButton(world)) {
                UnTapAllAtEndOfTurn = false;
            }
        }else if (CollectedCardList.size() > 0) {
            InactiveCard card = (InactiveCard) CollectedCardList.get(0);
            if (UIUtil.TouchedAcceptButton(world)) {
                SetUnsetUtil.UnSetTappedAttr(card);
                CollectedCardList.remove(card);
            }

            if (UIUtil.TouchedDeclineButton(world)) {
                CollectedCardList.remove(card);
            }
        } else {
            UnTapAllAtEndOfTurn = false;
            world.clearWorldFlag(WorldFlags.UserDecisionMakingMode);
            world.clearWorldFlag(WorldFlags.AcceptCardSelectingMode);
            world.clearWorldFlag(WorldFlags.MaySkipCardSelectingMode);
            this.S = OnTurnState.SY2;
        }
    }
/*
   This API end the turn
   SY2
   done
     */
    private boolean TurnEnding() {
        if (!NetworkUtil.sendDirectiveUpdates(world, DirectiveHeader.EndOfTurn, null, null)) {
            //handle the case when socket is closed
        }
        this.S = OnTurnState.S1;
        return true;
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
                if (GetUtil.IsActiveTurboRush(card)) {
                    ArrayList<InstructionSet> tmp =
                            card.getPrimaryInstructionForTheInstructionID(InstructionID.TurboRushIfAttacked);
                    if (IfAttackInsts != null && tmp != null) {
                        for (int i = 0; i < tmp.size(); i++) {
                            IfAttackInsts.add(tmp.get(i));
                        }
                    } else if (tmp != null) {
                        IfAttackInsts = tmp;
                    }
                }
                world.getInstructionIteratorHandler().setCard(card);
                world.getInstructionIteratorHandler().setInstructions(IfAttackInsts);
                SetUnsetUtil.SetMarkedCard((InactiveCard) CollectedCardList.get(0));
                CollectedCardList.clear();
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
                    world.clearWorldFlag(WorldFlags.AttackSelectMode);
                    world.setWorldFlag(WorldFlags.PlayerAttackMode);
                    world.getMaze().getZoneList().get(6).getZoneArray().clear();
                    ArrayList<InstructionSet> IfAttackInsts =
                            card.getPrimaryInstructionForTheInstructionID(InstructionID.IfAttacked);
                    if (GetUtil.IsActiveTurboRush(card)) {
                        ArrayList<InstructionSet> tmp =
                                card.getPrimaryInstructionForTheInstructionID(InstructionID.TurboRushIfAttacked);
                        if (IfAttackInsts != null && tmp != null) {
                            for (int i = 0; i < tmp.size(); i++) {
                                IfAttackInsts.add(tmp.get(i));
                            }
                        } else if (tmp != null) {
                            IfAttackInsts = tmp;
                        }
                    }

                    world.getInstructionIteratorHandler().setCard(card);
                    world.getInstructionIteratorHandler().setInstructions(IfAttackInsts);
                    this.S = OnTurnState.S11;
                    world.getEventLog().setRecording(true);
                    world.clearWorldFlag(WorldFlags.AttackSelectMode);
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
 Check whether opponent have a blocker, if yes send a request which blocker he/she will use
 S12
 */
    private void BlockerUpdate() {
        int count = 0;
        if (world.getWorldFlag(WorldFlags.AttackSelectMode))
            count++;
        if (world.getWorldFlag(WorldFlags.ShieldSelectMode))
            count++;
        if (world.getWorldFlag(WorldFlags.PlayerAttackMode))
            count++;

        if (count >1)
            throw new IllegalArgumentException("More than one mode is on");

        String CollectAttackMarkedCard = InstSetUtil.GenerateCopyCardToTempZoneBasedOnAttribute("MarkedCard", 2);
        InstructionSet CollectInst = new InstructionSet(CollectAttackMarkedCard);
        world.getInstructionHandler().setCardAndInstruction((InactiveCard) world.getFetchCard(), CollectInst);
        world.getInstructionHandler().execute();
        ArrayList<Cards> CollectedCardList = world.getMaze().getZoneList().get(6).getZoneArray();
        InactiveCard Attacked = (CollectedCardList.size() > 0) ? (InactiveCard) CollectedCardList.get(0) : null;
        if (GetUtil.OpponentHasABlocker(world, (InactiveCard) world.getFetchCard(), Attacked)) {
            int zone = world.getFetchCard().GridPosition().getZone() + 7;
            if (CollectedCardList.size() !=1) {
                String msg = zone + " " + world.getFetchCard().GridPosition().getGridIndex() + " " +
                        world.getFetchCard().getNameID();
                NetworkUtil.sendDirectiveUpdates(world, DirectiveHeader.RequestBlocker, msg, null);
            } else {
                Cards card = CollectedCardList.get(0);
                int zone2 = card.GridPosition().getZone() - 7;
                String msg = zone + " " + world.getFetchCard().GridPosition().getGridIndex() + " " +
                        world.getFetchCard().getNameID() + " " + zone2 + " " + card.GridPosition().getGridIndex()
                        + " " + card.getNameID();
                NetworkUtil.sendDirectiveUpdates(world, DirectiveHeader.RequestBlocker, msg, null);
            }
            world.setWorldFlag(WorldFlags.BlockerSelectMode);
            S = OnTurnState.S12a;
            CollectedCardList.clear();
        } else {
            ArrayList<InstructionSet> IfAttackUnblockedInsts =
                    ((ActiveCard) world.getFetchCard()).getPrimaryInstructionForTheInstructionID(InstructionID.IfUnblocked);
            if (GetUtil.IsActiveTurboRush((ActiveCard) world.getFetchCard())) {
                ArrayList<InstructionSet> tmp =
                        ((ActiveCard) world.getFetchCard()).getPrimaryInstructionForTheInstructionID(InstructionID.TurboRushIfUnblocked);
                if (IfAttackUnblockedInsts != null && tmp != null) {
                    for (int i = 0; i < tmp.size(); i++) {
                        IfAttackUnblockedInsts.add(tmp.get(i));
                    }
                } else if (tmp != null) {
                    IfAttackUnblockedInsts = tmp;
                }
            }

            world.getInstructionIteratorHandler().setCard((ActiveCard)world.getFetchCard());
            world.getInstructionIteratorHandler().setInstructions(IfAttackUnblockedInsts);
            S = OnTurnState.S12b;
        }
    }
/*
 Wait for opponent to decide whether he/she will block or not
 S12a
     */
    private void OpponentBlockerDirective() {
        if (world.getGame().getNetwork().getSocket() == null || world.getGame().getNetwork().getSocket().isClosed()) {
            // for now this but later handle it properly
            ArrayList<InstructionSet> IfAttackUnblockedInsts =
                    ((ActiveCard) world.getFetchCard()).getPrimaryInstructionForTheInstructionID(InstructionID.IfUnblocked);
            if (GetUtil.IsActiveTurboRush((ActiveCard) world.getFetchCard())) {
                ArrayList<InstructionSet> tmp =
                        ((ActiveCard) world.getFetchCard()).getPrimaryInstructionForTheInstructionID(InstructionID.TurboRushIfUnblocked);
                if (IfAttackUnblockedInsts != null && tmp != null) {
                    for (int i = 0; i < tmp.size(); i++) {
                        IfAttackUnblockedInsts.add(tmp.get(i));
                    }
                } else if (tmp != null) {
                    IfAttackUnblockedInsts = tmp;
                }
            }

            world.getInstructionIteratorHandler().setCard((ActiveCard)world.getFetchCard());
            world.getInstructionIteratorHandler().setInstructions(IfAttackUnblockedInsts);
            S = OnTurnState.S12b;
            world.clearWorldFlag(WorldFlags.BlockerSelectMode);
            return;
        }

        if (world.getGame().getNetwork().getreceivedDirectiveSize() == 0)
            return;

        String directive = world.getGame().getNetwork().getreceivedDirectiveMsg();
        String[] splitdirective = directive.split("@");
        if (!splitdirective[0].equals(DirectiveHeader.SendBlocker))
            throw new IllegalArgumentException("Invalid Directive at this point");

        if (!(splitdirective.length > 2)) {
            ArrayList<InstructionSet> IfAttackUnblockedInsts =
                    ((ActiveCard) world.getFetchCard()).getPrimaryInstructionForTheInstructionID(InstructionID.IfUnblocked);
            if (GetUtil.IsActiveTurboRush((ActiveCard) world.getFetchCard())) {
                ArrayList<InstructionSet> tmp =
                        ((ActiveCard) world.getFetchCard()).getPrimaryInstructionForTheInstructionID(InstructionID.TurboRushIfUnblocked);
                if (IfAttackUnblockedInsts != null && tmp != null) {
                    for (int i = 0; i < tmp.size(); i++) {
                        IfAttackUnblockedInsts.add(tmp.get(i));
                    }
                } else if (tmp != null) {
                    IfAttackUnblockedInsts = tmp;
                }
            }

            world.getInstructionIteratorHandler().setCard((ActiveCard)world.getFetchCard());
            world.getInstructionIteratorHandler().setInstructions(IfAttackUnblockedInsts);
            S = OnTurnState.S12b;
            world.clearWorldFlag(WorldFlags.BlockerSelectMode);
            return;
        }

        String[] msg = splitdirective[1].split(" ");

        if (msg.length != 3)
            throw new IllegalArgumentException("Invalid evolution directive");

        int BCardzone = Integer.parseInt(msg[0]);
        int BGridIndex = Integer.parseInt(msg[1]);
        InactiveCard BlockerCard = null; //(InactiveCard) world.getGridIndexTrackingTable().getCardMappedToGivenGridPosition(BCardzone, BGridIndex);
        if (!BlockerCard.getNameID().equals(msg[2]))
            throw new IllegalArgumentException("Data inconsistency");

        int TypeOfAttack = 0;

        if (world.getWorldFlag(WorldFlags.AttackSelectMode))
            TypeOfAttack = 1;
        if (world.getWorldFlag(WorldFlags.ShieldSelectMode))
            TypeOfAttack = 4;
        if (world.getWorldFlag(WorldFlags.PlayerAttackMode))
            TypeOfAttack = 2;

        if (!GetUtil.IsTapped(BlockerCard)) {
            SetUnsetUtil.SetTappedAttr(BlockerCard);
            world.getEventLog().registerEvent(BlockerCard, false, 0 , "Tapped", true ,1);
        }

        if (GetUtil.IsBlockable((InactiveCard) world.getFetchCard(), BlockerCard, TypeOfAttack)) {
            String CollectAttackMarkedCard = InstSetUtil.GenerateCopyCardToTempZoneBasedOnAttribute("MarkedCard", 202);
            InstructionSet CollectInst = new InstructionSet(CollectAttackMarkedCard);
            world.getInstructionHandler().setCardAndInstruction((InactiveCard) world.getFetchCard(), CollectInst);
            world.getInstructionHandler().execute();
            ArrayList<Cards> CollectedCardList = world.getMaze().getZoneList().get(6).getZoneArray();
            for (int i =0; i < CollectedCardList.size(); i++) {
                SetUnsetUtil.UnSetMarkedCard((InactiveCard) CollectedCardList.get(i));
            }
            SetUnsetUtil.SetMarkedCard(BlockerCard);
            world.clearWorldFlag(WorldFlags.ShieldSelectMode);
            world.clearWorldFlag(WorldFlags.PlayerAttackMode);
            world.setWorldFlag(WorldFlags.AttackSelectMode);
            world.setWorldFlag(WorldFlags.WasBlocked);
            CollectedCardList.clear();
            S = OnTurnState.S13;
        } else {
            if (world.getWorldFlag(WorldFlags.AttackSelectMode))
                S = OnTurnState.S13;
            if (world.getWorldFlag(WorldFlags.ShieldSelectMode))
                S = OnTurnState.S14;
            if (world.getWorldFlag(WorldFlags.PlayerAttackMode)) {
                //need to handle later
            }
        }
        world.clearWorldFlag(WorldFlags.BlockerSelectMode);
    }
/*
 If user decides to attack creature or if get blocked, this API will be called to evaluate the result of
 the battle.
 S13
 */
    private boolean CreatureBattleUpdate() {
        boolean WasBlocked = world.getWorldFlag(WorldFlags.WasBlocked);
        ActiveCard AttackingCard = (ActiveCard) world.getFetchCard();
        String CollectAttackMarkedCard = InstSetUtil.GenerateCopyCardToTempZoneBasedOnAttribute("MarkedCard", 2);
        InstructionSet CollectInst = new InstructionSet(CollectAttackMarkedCard);
        world.getInstructionHandler().setCardAndInstruction(AttackingCard, CollectInst);
        world.getInstructionHandler().execute();
        ArrayList<Cards> CollectedCardList = world.getMaze().getZoneList().get(6).getZoneArray();
        if (CollectedCardList.size() != 1)
            throw new IllegalArgumentException("Something went wrong with the creature to attack");
        InactiveCard AttackedCard = (InactiveCard) CollectedCardList.get(0);
        SetUnsetUtil.UnSetMarkedCard(AttackedCard);
        CollectedCardList.clear();
        if (world.getWorldFlag(WorldFlags.WasBlocked)) {
            world.clearWorldFlag(WorldFlags.WasBlocked);
            if (GetUtil.IsBreaksWheneverBlocked(AttackingCard) > 0) {
                int val = GetUtil.IsBreaksWheneverBlocked(AttackingCard);
                int ActionZone = 100 * val;
                String BreakShieldMarkForBlocking = InstSetUtil.GenerateSetAttributeOnRandomSelectedCardInstruction(ActionZone, "MarkedCard", 1, 1);
                InstructionSet BreakShieldMarkInst =  new InstructionSet(BreakShieldMarkForBlocking);
                world.getInstructionHandler().setCardAndInstruction(AttackingCard, BreakShieldMarkInst);
                world.getInstructionHandler().execute();
                String CollectMarkedShield = InstSetUtil.GenerateCopyCardToTempZoneBasedOnAttribute("MarkedCard", ActionZone);
                InstructionSet CollectShieldInst = new InstructionSet(CollectMarkedShield);
                world.getInstructionHandler().setCardAndInstruction(AttackingCard, CollectShieldInst);
                world.getInstructionHandler().execute();
                CollectedCardList = world.getMaze().getZoneList().get(6).getZoneArray();
                if (CollectedCardList.size() != 1)
                    throw new IllegalArgumentException("Something went wrong while marking shield");

                InactiveCard ShieldCard = (InactiveCard) CollectedCardList.get(0);
                SetUnsetUtil.UnSetMarkedCard(ShieldCard);

                String BreakShield = InstSetUtil.GenerateSelfChangeZoneInstruction(3);
                InstructionSet BreakInst = new InstructionSet(BreakShield);
                world.getInstructionHandler().setCardAndInstruction(ShieldCard, BreakInst);
                world.getInstructionHandler().execute();
                CollectedCardList.clear();
                CollectedCardList.add(world.getInstructionHandler().getCurrentCard());
                if (GetUtil.IsShieldTrigger((InactiveCard) CollectedCardList.get(0))) {
                    world.setWorldFlag(WorldFlags.ShieldTriggerFound);
                    int zone;
                    if (CollectedCardList.get(0).GridPosition().getZone() > 6) {
                        zone = CollectedCardList.get(0).GridPosition().getZone() - 7;
                    } else if (CollectedCardList.get(0).GridPosition().getZone() < 6) {
                        zone = CollectedCardList.get(0).GridPosition().getZone() + 7;
                    } else {
                        throw new IllegalArgumentException("Invalid zone");
                    }

                    String msg = zone + " " + CollectedCardList.get(0).GridPosition().getGridIndex() +
                            " " + CollectedCardList.get(0).getNameID();

                    world.getEventLog().AddHoldMsg(msg);
                }
                CollectedCardList.clear();
            }

            Zone Tzone = world.getMaze().getZoneList().get(0);

            for (int i = 0; i < Tzone.zoneSize(); i++) {
                ActiveCard card = (ActiveCard) Tzone.getZoneArray().get(i);
                if (!GetUtil.IsUsedBlockedSetAttrAbility(card)) {
                    ArrayList<InstructionSet> instructions = card.getPrimaryInstructionForTheInstructionID(InstructionID.BlockedSetAttrAbility);
                    if (instructions != null) {
                        for (int j = 0; j < instructions.size(); j++) {
                            world.getInstructionHandler().setCardAndInstruction(card, instructions.get(j));
                            world.getInstructionHandler().execute();
                        }
                    }
                    if (instructions != null)
                        SetUnsetUtil.SetUsedBlockedSetAttrAbility(card);
                }
            }
        }

        int AttackResult = GetUtil.AttackEvaluation(AttackingCard, AttackedCard, WasBlocked);
        if (!GetUtil.IsTapped(AttackingCard)) {
            SetUnsetUtil.SetTappedAttr(AttackingCard);
            world.getEventLog().registerEvent(AttackingCard, false, 0 , "Tapped", true ,1);
        }

        if (AttackResult == 1) {
            String DestroyDstInst = InstSetUtil.GenerateSelfChangeZoneInstruction(4);
            InstructionSet instruction2 = new InstructionSet(DestroyDstInst);
            world.getInstructionHandler().setCardAndInstruction(AttackedCard, instruction2);
            world.getInstructionHandler().execute();
        }

        if (AttackResult == 0) {
            String DestroyDstInst = InstSetUtil.GenerateSelfChangeZoneInstruction(4);
            InstructionSet instruction2 = new InstructionSet(DestroyDstInst);
            world.getInstructionHandler().setCardAndInstruction(AttackedCard, instruction2);
            world.getInstructionHandler().execute();

            world.getInstructionHandler().setCardAndInstruction(AttackingCard, instruction2);
            world.getInstructionHandler().execute();
        }

        if (AttackResult == -1) {
            String DestroyDstInst = InstSetUtil.GenerateSelfChangeZoneInstruction(4);
            InstructionSet instruction2 = new InstructionSet(DestroyDstInst);
            world.getInstructionHandler().setCardAndInstruction(AttackingCard, instruction2);
            world.getInstructionHandler().execute();
        }

        SetUnsetUtil.PerformCleanUpForMovedCard(world);

        //send Eventlog
        String msg = world.getEventLog().getAndClearEvents();
        NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
        world.getEventLog().setRecording(false);
        CollectedCardList.clear();
        world.clearWorldFlag(WorldFlags.AttackSelectMode);
        if (world.getWorldFlag(WorldFlags.ShieldTriggerFound)) {
            SetUnsetUtil.SpreadingFlagAttr(world);
            world.getInstructionHandler().setCardAndInstruction(null, NotYetSpreadCleanup);
            world.getInstructionHandler().execute();
            NetworkUtil.sendDirectiveUpdates(world, DirectiveHeader.EndOfTurnDueToShieldTrigger, null, null);
            S = OnTurnState.S1;
            return true;
        } else {
            S = OnTurnState.SX;
            return false;
        }
    }
/*
 After attack this is were If attack ability is handled.
 S11
 */
    private void PostIfAttackUpdate() {
        if (world.getInstructionIteratorHandler().update()) {
            S = OnTurnState.S12;
        }
    }
/*
 After attack this is were if unblocked is handled.
 S12b
 */
    private void PostIfUnBlockedUpdate() {
        if (world.getInstructionIteratorHandler().update()) {
            if (world.getWorldFlag(WorldFlags.AttackSelectMode))
                S = OnTurnState.S13;
            if (world.getWorldFlag(WorldFlags.ShieldSelectMode))
                S = OnTurnState.S14;
            if (world.getWorldFlag(WorldFlags.PlayerAttackMode)) {
                //need to handle later
            }
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
                if (GetUtil.IsActiveTurboRush(card)) {
                    ArrayList<InstructionSet> tmp =
                            card.getPrimaryInstructionForTheInstructionID(InstructionID.TurboRushIfAttacked);
                    if (IfAttackInsts != null && tmp != null) {
                        for (int i = 0; i < tmp.size(); i++) {
                            IfAttackInsts.add(tmp.get(i));
                        }
                    } else if (tmp != null) {
                        IfAttackInsts = tmp;
                    }
                }

                world.getInstructionIteratorHandler().setCard(card);
                world.getInstructionIteratorHandler().setInstructions(IfAttackInsts);
                this.S = OnTurnState.S11;
                for (int i = 0; i < CollectedCardList.size(); i++) {
                    SetUnsetUtil.SetMarkedCard((InactiveCard) CollectedCardList.get(i));
                }
                CollectedCardList.clear();
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
    private boolean ShieldBreakingUpdate() {
        ActiveCard card = (ActiveCard) world.getFetchCard();
        String CollectAttackMarkedCard = InstSetUtil.GenerateCopyCardToTempZoneBasedOnAttribute("MarkedCard", 200);
        InstructionSet CollectInst = new InstructionSet(CollectAttackMarkedCard);
        world.getInstructionHandler().setCardAndInstruction(card, CollectInst);
        world.getInstructionHandler().execute();
        ArrayList<Cards> CollectedCardList = world.getMaze().getZoneList().get(6).getZoneArray();
        for (int i =0; i < CollectedCardList.size(); i++) {
            SetUnsetUtil.UnSetMarkedCard((InactiveCard) CollectedCardList.get(i));
        }
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
            String ActivateTurboRush = InstSetUtil.GenerateSetAttributeBasedOnPresenceOfOtherAttribute("HasTurboRush", "ActiveTurboRush", 1, 1);
            InstructionSet instruction2 = new InstructionSet(ActivateTurboRush);
            world.getInstructionHandler().setCardAndInstruction(card, instruction2);
            world.getInstructionHandler().execute();
            SetUnsetUtil.SetTurboRushSetAttr(world);

            //send eventlog
            String msg = world.getEventLog().getAndClearEvents();
            NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
            world.clearWorldFlag(WorldFlags.ShieldSelectMode);
            S = OnTurnState.SX;
            world.getEventLog().setRecording(false);
            CollectedCardList.clear();
            return false;
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
            String ActivateTurboRush = InstSetUtil.GenerateSetAttributeBasedOnPresenceOfOtherAttribute("HasTurboRash", "ActiveTurboRash", 1, 1);
            InstructionSet instruction2 = new InstructionSet(ActivateTurboRush);
            world.getInstructionHandler().setCardAndInstruction(card, instruction2);
            world.getInstructionHandler().execute();
            SetUnsetUtil.SetTurboRushSetAttr(world);

            //send eventlog
            String msg2 = world.getEventLog().getAndClearEvents();
            NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg2, null);

            world.clearWorldFlag(WorldFlags.ShieldSelectMode);
            S = OnTurnState.SX;
            world.getEventLog().setRecording(false);
            CollectedCardList.clear();
            return false;
        }

        SetUnsetUtil.SpreadingFlagAttr(world);
        world.getInstructionHandler().setCardAndInstruction(null, NotYetSpreadCleanup);
        world.getInstructionHandler().execute();
        world.clearWorldFlag(WorldFlags.ShieldSelectMode);
        if (!NetworkUtil.sendDirectiveUpdates(world, DirectiveHeader.EndOfTurnDueToShieldTrigger, null, null)) {
            //handle the case when socket is closed
            S = OnTurnState.S1;
            world.getEventLog().setRecording(false);
            CollectedCardList.clear();
            return true;
        }
        this.S = OnTurnState.S1;
        world.getEventLog().setRecording(false);
        world.setWorldFlag(WorldFlags.ShieldTriggerFound);
        String msg2 = new String("");
        for (int i = 0; i < CollectedCardList.size(); i++) {
            if (GetUtil.IsShieldTrigger((InactiveCard) CollectedCardList.get(i))) {
                int zone = CollectedCardList.get(i).GridPosition().getZone() - 7;
                String tmp = zone + " " + CollectedCardList.get(i).GridPosition().getGridIndex() +
                        " " + CollectedCardList.get(i).getNameID();
                msg2 = msg2.concat(tmp);
                msg2 = msg2.concat("#");
            }
        }
        CollectedCardList.clear();
        if (msg2.length() > 0) {
            world.getEventLog().AddHoldMsg(msg2);
        }
        return true;
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
                    SummonTapped = false;
                    if (GetUtil.SummonTapped(SummoningOrCastCard)) {
                        SummonTapped = true;
                    }
                    boolean ActiveWaveStriker = GetUtil.IsActiveWaveStriker(SummoningOrCastCard);
                    String SummonCardInstruction = InstSetUtil.GenerateSelfChangeZoneInstruction(0);
                    InstructionSet instruction = new InstructionSet(SummonCardInstruction);
                    world.getInstructionHandler().setCardAndInstruction(SummoningOrCastCard, instruction);
                    world.getInstructionHandler().execute();
                    if (SummonTapped) {
                        SetUnsetUtil.SetTappedAttr((InactiveCard) world.getFetchCard());
                        world.getEventLog().registerEvent(world.getFetchCard(), false, 0 , "Tapped", true ,1);
                        SummonTapped = false;
                    }
                    //sendeventlog
                    String msg = world.getEventLog().getAndClearEvents();
                    NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
                    if (((ActiveCard)world.getFetchCard()).getPrimaryInstructionForTheInstructionID(InstructionID.SummonOrCastAbility) == null &&
                            ((ActiveCard)world.getFetchCard()).getPrimaryInstructionForTheInstructionID(InstructionID.WaveStrikerSummonOrCastAbility) == null &&
                            ! GetUtil.IsSurvivor((ActiveCard) world.getFetchCard())) {
                        this.S = OnTurnState.SX;
                        world.getEventLog().setRecording(false);
                        world.setFetchCard(null);
                    } else {
                        world.getInstructionIteratorHandler().setCard((InactiveCard) world.getFetchCard());
                        ArrayList<InstructionSet> instructions =
                                ((ActiveCard) world.getFetchCard()).getPrimaryInstructionForTheInstructionID(InstructionID.SummonOrCastAbility);
                        if (GetUtil.IsWaveStriker((InactiveCard) world.getFetchCard()) && ActiveWaveStriker) {
                            ArrayList<InstructionSet> tmp =
                                    ((ActiveCard) world.getFetchCard()).getPrimaryInstructionForTheInstructionID(InstructionID.WaveStrikerSummonOrCastAbility);
                            if (instructions != null && tmp != null) {
                                for (int i = 0; i < tmp.size(); i++) {
                                    instructions.add(tmp.get(i));
                                }
                            } else if (tmp != null) {
                                instructions = tmp;
                            }
                        }

                        if (GetUtil.IsSurvivor((ActiveCard) world.getFetchCard())) {
                            Zone zone = world.getMaze().getZoneList().get(0);
                            ArrayList<InstructionSet> tmp = null;
                            for (int i = 0; i < zone.zoneSize(); i++) {
                                ActiveCard tcard = (ActiveCard) zone.getZoneArray().get(i);
                                if ((tcard != world.getFetchCard()) && GetUtil.IsSurvivor(tcard)) {
                                    ArrayList<InstructionSet> tmp2 = tcard.getPrimaryInstructionForTheInstructionID(InstructionID.SummonOrCastAbility);
                                    if (tmp != null && tmp2 != null) {
                                        for (int j = 0; j < tmp.size(); j++) {
                                            tmp.add(tmp.get(j));
                                        }
                                    } else if (tmp2 != null) {
                                        tmp = tmp2;
                                    }
                                }
                            }
                            if (instructions != null && tmp != null) {
                                for (int i = 0; i < tmp.size(); i++) {
                                    instructions.add(tmp.get(i));
                                }
                            } else if (tmp != null) {
                                instructions = tmp;
                            }
                        }

                        if (instructions.size() > 0) {
                            world.getInstructionIteratorHandler().setInstructions(instructions);
                            this.S = OnTurnState.S4;
                        } else {
                            this.S = OnTurnState.SX;
                            world.getEventLog().setRecording(false);
                            world.setFetchCard(null);
                        }
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
                    SummonTapped = false;
                    if (GetUtil.SummonTapped(SummoningOrCastCard)) {
                        SummonTapped = true;
                    }
                    String EvolutionInst = InstSetUtil.GenerateEvolutionInstruction(SummoningOrCastCard.getEvolutionCompareString());
                    InstructionSet Einstruction = new InstructionSet(EvolutionInst);
                    world.getInstructionHandler().setCardAndInstruction(SummoningOrCastCard, Einstruction);
                    //sendEventlog
                    String msg = world.getEventLog().getAndClearEvents();
                    NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
                    this.S = OnTurnState.S4a;
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
            S = OnTurnState.S1;
            world.setFetchCard(null);
            world.getEventLog().setRecording(false);
        }
    }
/*
  Perform evolution S4a
 */
    private void EvolutionUpdate() {
        if (world.getInstructionHandler().execute()) {
            ActiveCard card = (ActiveCard) world.getFetchCard();
            boolean ActiveWaveStriker = GetUtil.IsActiveWaveStriker(card);
            String CollectAttackMarkedCard = InstSetUtil.GenerateCopyCardToTempZoneBasedOnAttribute("MarkedCard", 1);
            InstructionSet CollectInst = new InstructionSet(CollectAttackMarkedCard);
            world.getInstructionHandler().setCardAndInstruction(card, CollectInst);
            world.getInstructionHandler().execute();
            ArrayList<Cards> CollectedCardList = world.getMaze().getZoneList().get(Maze.temporaryZone).getZoneArray();
            if (CollectedCardList.size() != 1)
                throw new IllegalArgumentException("Something went wrong while evolution");
            SetUnsetUtil.UnSetMarkedCard((InactiveCard) CollectedCardList.get(0));
            ArrayList<InstructionSet> CleanUpInst = ((InactiveCard)CollectedCardList.get(0)).getCrossInstructionForTheInstructionID(InstructionID.CleanUp);
            if (CleanUpInst != null) {
                for (int i = 0; i < CleanUpInst.size(); i++) {
                    world.getInstructionHandler().setCardAndInstruction((InactiveCard) CollectedCardList.get(0), CleanUpInst.get(i));
                    world.getInstructionHandler().execute();
                }
            }
            InactiveCard card2 = (InactiveCard) ActUtil.EvolveCreature(card, CollectedCardList.get(0), world);
            world.setFetchCard(card2);
            if (SummonTapped) {
                SetUnsetUtil.SetTappedAttr((InactiveCard) world.getFetchCard());
                world.getEventLog().registerEvent(world.getFetchCard(), false, 0 , "Tapped", true ,1);
                SummonTapped = false;
            }
            //sendeventlog
            String msg = world.getEventLog().getAndClearEvents();
            NetworkUtil.sendDirectiveUpdates(world,DirectiveHeader.ApplyEvents, msg, null);
            if (((ActiveCard)world.getFetchCard()).getPrimaryInstructionForTheInstructionID(InstructionID.SummonOrCastAbility) == null &&
                    ((ActiveCard)world.getFetchCard()).getPrimaryInstructionForTheInstructionID(InstructionID.WaveStrikerSummonOrCastAbility) == null &&
                    !GetUtil.IsSurvivor((ActiveCard) world.getFetchCard())) {
                this.S = OnTurnState.SX;
                world.getEventLog().setRecording(false);
                world.setFetchCard(null);
            } else {
                world.getInstructionIteratorHandler().setCard((InactiveCard) world.getFetchCard());
                ArrayList<InstructionSet> instructions =
                        ((ActiveCard) world.getFetchCard()).getPrimaryInstructionForTheInstructionID(InstructionID.SummonOrCastAbility);
                if (GetUtil.IsWaveStriker((InactiveCard) world.getFetchCard()) && ActiveWaveStriker) {
                    ArrayList<InstructionSet> tmp =
                            ((ActiveCard) world.getFetchCard()).getPrimaryInstructionForTheInstructionID(InstructionID.WaveStrikerSummonOrCastAbility);
                    if (instructions != null && tmp != null) {
                        for (int i = 0; i < tmp.size(); i++) {
                            instructions.add(tmp.get(i));
                        }
                    } else if (tmp != null) {
                        instructions = tmp;
                    }
                }

                if (GetUtil.IsSurvivor((ActiveCard) world.getFetchCard())) {
                    Zone zone = world.getMaze().getZoneList().get(0);
                    ArrayList<InstructionSet> tmp = null;
                    for (int i = 0; i < zone.zoneSize(); i++) {
                        ActiveCard tcard = (ActiveCard) zone.getZoneArray().get(i);
                        if ((tcard != world.getFetchCard()) && GetUtil.IsSurvivor(tcard)) {
                            ArrayList<InstructionSet> tmp2 = tcard.getPrimaryInstructionForTheInstructionID(InstructionID.SummonOrCastAbility);
                            if (tmp != null && tmp2 != null) {
                                for (int j = 0; j < tmp.size(); j++) {
                                    tmp.add(tmp.get(j));
                                }
                            } else if (tmp2 != null) {
                                tmp = tmp2;
                            }
                        }
                    }
                    if (instructions != null && tmp != null) {
                        for (int i = 0; i < tmp.size(); i++) {
                            instructions.add(tmp.get(i));
                        }
                    } else if (tmp != null) {
                        instructions = tmp;
                    }
                }

                if (instructions.size() > 0) {
                    world.getInstructionIteratorHandler().setInstructions(instructions);
                    this.S = OnTurnState.S4;
                } else {
                    this.S = OnTurnState.SX;
                    world.getEventLog().setRecording(false);
                    world.setFetchCard(null);
                }
            }
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
            // send event
            String msg = world.getEventLog().getAndClearEvents();
            NetworkUtil.sendDirectiveUpdates(world, DirectiveHeader.ApplyEvents, msg, null);
            S = OnTurnState.S1;
            world.setFetchCard(null);
            world.getEventLog().setRecording(false);
        }
    }

    private void ShowInfoUpdate() {
        if (UIUtil.TouchedInfoTabBackButton(world)) {
            this.S = OnTurnState.S1;
            world.clearWorldFlag(WorldFlags.DisplayInfo);
        }
    }

/* S15 */
    private void ShowDeckCards() {
        if (UIUtil.TouchedAttackShieldOrPlayerButton(world)) {
            this.S =OnTurnState.S1;
            world.clearWorldFlag(WorldFlags.DisplayDeckCard);
        }
    }
    private void TapAbilityUpdate() {
        if (world.getInstructionIteratorHandler().update()) {
            S = OnTurnState.S1;
            world.getEventLog().setRecording(false);
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