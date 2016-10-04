package koustav.duelmasters.main.androidgameduelmastersutil;

import java.util.ArrayList;
import java.util.Random;

import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionID;
import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionSet;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.ActiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmasterswidgetcoordinationtools.Actions;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.GridPositionIndex;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.InactiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Maze;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.PvPWidgetCoordinator;
import koustav.duelmasters.main.androidgameduelmastersworlds.PvPWorld;
import koustav.duelmasters.main.androidgameduelmastersworlds.World;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;

/**
 * Created by Koustav on 4/26/2015.
 */
public class ActUtil {
    public static Cards ChangeZoneOperator(PvPWorld world, Cards card, InstructionSet instruction) {
        GridPositionIndex gridPosition = card.GridPosition();
        PvPWidgetCoordinator coordinator = world.getWidgetCoordinator();
        Cards NewCard = null;
        if (card.GridPosition().getZone() == Maze.battleZone || card.GridPosition().getZone() == Maze.Opponent_battleZone) {
            ArrayList<InstructionSet> CleanUpInst = ((InactiveCard)card).getCrossInstructionForTheInstructionID(InstructionID.CleanUp);
            if (CleanUpInst != null) {
                world.getEventLog().AddHoldCleanUp(card, CleanUpInst);
            }
            if (GetUtil.IsActiveConditionalFlagSpreading((InactiveCard) card)) {
                CleanUpInst = ((InactiveCard)card).getCrossInstructionForTheInstructionID(InstructionID.CleanUpConditional);
                if (CleanUpInst != null) {
                    world.getEventLog().AddHoldCleanUp(card, CleanUpInst);
                }
            }
        }
        if (gridPosition.getZone() >= Maze.battleZone && gridPosition.getZone() <= Maze.deck) {
            int z = gridPosition.getZone();
            int d = instruction.getActionDestination();
            if (z == Maze.battleZone && d == Maze.graveyard) {
                ArrayList<InstructionSet> DstInst = ((InactiveCard)card).getCrossInstructionForTheInstructionID(InstructionID.DestroyDst);
                if (DstInst != null || GetUtil.MaskDestroyDstVal((InactiveCard)card) > 0) {
                    if (GetUtil.MaskDestroyDstVal((InactiveCard) card) > 0) {
                        d = GetUtil.MaskDestroyDstVal((InactiveCard) card) -1;
                    } else {
                        InstructionSet tmpInst = DstInst.get(0);
                        d = tmpInst.getActionDestination();
                    }
                }
            }
            if (d > Maze.deck)
                throw new IllegalArgumentException("Invalid destination to transfer card (1)");
            world.getEventLog().registerEvent(card, true, d, null, false, 0);
            int i = world.getMaze().getZoneList().get(z).getZoneArray().indexOf(card);
            world.getMaze().getZoneList().get(z).getZoneArray().remove(i);
            coordinator.SendAction(Actions.CleanSelectedCardIfMatch, card);
            if (z-Maze.graveyard != 0 &&  z- Maze.deck != 0) {
//                world.getGridIndexTrackingTable().clearGridIndex(card.GridPosition());
            }
            if (d == Maze.battleZone || d == Maze.hand) {
  //              int GridIndex = world.getGridIndexTrackingTable().getNewGridIndex(d);
                int GridIndex = 0;
                Cards Bcard = world.getMaze().GetBaseCard(card);
                ActiveCard Acard = new ActiveCard(card.ExtractCardInfo(), new GridPositionIndex(d, GridIndex));
                NewCard = Acard;
                world.getMaze().getZoneList().get(d).getZoneArray().add(Acard);
                CardWidget cardWidget = world.getWidgetCoordinator().DecoupleWidgetFormCard(card);
                world.getWidgetCoordinator().CoupleWidgetForCard(NewCard, cardWidget);
                world.UpdateCardInfoToCardTable(card, NewCard);
                //world.getGridIndexTrackingTable().trackGridIndex(Acard.GridPosition(), Acard);
                if (Bcard != null) {
                    world.getMaze().ClearEvolutionTrack(card);
                  //  GridIndex = world.getGridIndexTrackingTable().getNewGridIndex(d);
                    Acard = new ActiveCard(Bcard.ExtractCardInfo(), new GridPositionIndex(d, GridIndex));
                    world.getMaze().getZoneList().get(d).getZoneArray().add(Acard);
                    cardWidget = world.getWidgetCoordinator().DecoupleWidgetFormCard(Bcard);
                    world.getWidgetCoordinator().CoupleWidgetForCard(Acard, cardWidget);
                    world.UpdateCardInfoToCardTable(Bcard, Acard);
                    coordinator.SendAction(Actions.CleanSelectedCardIfMatch, Bcard);
                   // world.getGridIndexTrackingTable().trackGridIndex(Acard.GridPosition(), Acard);
                }
            }

            if (d == Maze.manaZone || d == Maze.shieldZone) {
                int GridIndex = 0 ; //world.getGridIndexTrackingTable().getNewGridIndex(d);
                Cards Bcard = world.getMaze().GetBaseCard(card);
                InactiveCard Icard = new InactiveCard(card.ExtractCardInfo(), new GridPositionIndex(d, GridIndex));
                NewCard  = Icard;
                world.getMaze().getZoneList().get(d).getZoneArray().add(Icard);
                CardWidget cardWidget = world.getWidgetCoordinator().DecoupleWidgetFormCard(card);
                world.getWidgetCoordinator().CoupleWidgetForCard(NewCard, cardWidget);
                world.UpdateCardInfoToCardTable(card, NewCard);
                //world.getGridIndexTrackingTable().trackGridIndex(Icard.GridPosition(), Icard);
                if (Bcard != null) {
                    world.getMaze().ClearEvolutionTrack(card);
                  //  GridIndex = world.getGridIndexTrackingTable().getNewGridIndex(d);
                    Icard = new InactiveCard(Bcard.ExtractCardInfo(), new GridPositionIndex(d, GridIndex));
                    world.getMaze().getZoneList().get(d).getZoneArray().add(Icard);
                    cardWidget = world.getWidgetCoordinator().DecoupleWidgetFormCard(Bcard);
                    world.getWidgetCoordinator().CoupleWidgetForCard(Icard, cardWidget);
                    world.UpdateCardInfoToCardTable(Bcard, Icard);
                    coordinator.SendAction(Actions.CleanSelectedCardIfMatch, Bcard);
                   // world.getGridIndexTrackingTable().trackGridIndex(Icard.GridPosition(), Icard);
                }
            }

            if (d == Maze.graveyard || d == Maze.deck) {
                Cards Bcard = world.getMaze().GetBaseCard(card);
                Cards Dcard = new Cards(card.ExtractCardInfo(), new GridPositionIndex(d, 0));
                world.getMaze().getZoneList().get(d).getZoneArray().add(0,Dcard);
                CardWidget cardWidget = world.getWidgetCoordinator().DecoupleWidgetFormCard(card);
                world.getWidgetCoordinator().CoupleWidgetForCard(Dcard, cardWidget);
                world.UpdateCardInfoToCardTable(card, Dcard);
                if (Bcard != null) {
                    world.getMaze().ClearEvolutionTrack(card);
                    Dcard = new InactiveCard(Bcard.ExtractCardInfo(), new GridPositionIndex(d, 0));
                    world.getMaze().getZoneList().get(d).getZoneArray().add(0, Dcard);
                    cardWidget = world.getWidgetCoordinator().DecoupleWidgetFormCard(Bcard);
                    world.getWidgetCoordinator().CoupleWidgetForCard(Dcard, cardWidget);
                    world.UpdateCardInfoToCardTable(Bcard, Dcard);
                    coordinator.SendAction(Actions.CleanSelectedCardIfMatch, Bcard);
                }
            }
        } else if (gridPosition.getZone() >= Maze.Opponent_battleZone && gridPosition.getZone() <= Maze.Opponent_deck) {
            int z = gridPosition.getZone();
            int d = instruction.getActionDestination();
            if (z == Maze.Opponent_battleZone && d == Maze.graveyard) {
                ArrayList<InstructionSet> DstInst = ((InactiveCard)card).getCrossInstructionForTheInstructionID(InstructionID.DestroyDst);
                if (DstInst != null || GetUtil.MaskDestroyDstVal((InactiveCard)card) > 0) {
                    if (GetUtil.MaskDestroyDstVal((InactiveCard) card) > 0) {
                        d = GetUtil.MaskDestroyDstVal((InactiveCard) card) -1;
                    } else {
                        InstructionSet tmpInst = DstInst.get(0);
                        d = tmpInst.getActionDestination();
                    }
                }
            }
            if (d > Maze.deck)
                throw new IllegalArgumentException("Invalid destination to transfer card (2)");
            world.getEventLog().registerEvent(card, true, d, null, false, 0);
            int i = world.getMaze().getZoneList().get(z).getZoneArray().indexOf(card);
            world.getMaze().getZoneList().get(z).getZoneArray().remove(i);
            coordinator.SendAction(Actions.CleanSelectedCardIfMatch, card);
            if (z != Maze.Opponent_graveyard &&  z != Maze.Opponent_deck) {
               // world.getGridIndexTrackingTable().clearGridIndex(card.GridPosition());
            }
            if (d >= Maze.battleZone && d <= Maze.hand) {
                int GridIndex = 0;//world.getGridIndexTrackingTable().getNewGridIndex(d+Maze.Opponent_battleZone);
                Cards Bcard = world.getMaze().GetBaseCard(card);
                InactiveCard Icard = new InactiveCard(card.ExtractCardInfo(), new GridPositionIndex(d+ Maze.Opponent_battleZone, GridIndex));
                NewCard  = Icard;
                world.getMaze().getZoneList().get(d+ Maze.Opponent_battleZone).getZoneArray().add(Icard);
                CardWidget cardWidget = world.getWidgetCoordinator().DecoupleWidgetFormCard(card);
                world.getWidgetCoordinator().CoupleWidgetForCard(NewCard, cardWidget);
                world.UpdateCardInfoToCardTable(card, NewCard);
                //world.getGridIndexTrackingTable().trackGridIndex(Icard.GridPosition(), Icard);
                if (Bcard != null) {
                    world.getMaze().ClearEvolutionTrack(card);
                  //  GridIndex = world.getGridIndexTrackingTable().getNewGridIndex(d+ Maze.Opponent_battleZone);
                    Icard = new InactiveCard(Bcard.ExtractCardInfo(), new GridPositionIndex(d+ Maze.Opponent_battleZone, GridIndex));
                    world.getMaze().getZoneList().get(d+ Maze.Opponent_battleZone).getZoneArray().add(Icard);
                    cardWidget = world.getWidgetCoordinator().DecoupleWidgetFormCard(Bcard);
                    world.getWidgetCoordinator().CoupleWidgetForCard(Icard, cardWidget);
                    world.UpdateCardInfoToCardTable(Bcard, Icard);
                    coordinator.SendAction(Actions.CleanSelectedCardIfMatch, Bcard);
                  //  world.getGridIndexTrackingTable().trackGridIndex(Icard.GridPosition(), Icard);
                }
            }

            if (d == Maze.graveyard || d == Maze.deck) {
                Cards Bcard = world.getMaze().GetBaseCard(card);
                Cards Dcard = new Cards(card.ExtractCardInfo(), new GridPositionIndex(d+ Maze.Opponent_battleZone, 0));
                world.getMaze().getZoneList().get(d+ Maze.Opponent_battleZone).getZoneArray().add(0,Dcard);
                CardWidget cardWidget = world.getWidgetCoordinator().DecoupleWidgetFormCard(card);
                world.getWidgetCoordinator().CoupleWidgetForCard(Dcard, cardWidget);
                world.UpdateCardInfoToCardTable(card, Dcard);
                if (Bcard != null) {
                    world.getMaze().ClearEvolutionTrack(card);
                    Dcard = new InactiveCard(Bcard.ExtractCardInfo(), new GridPositionIndex(d+ Maze.Opponent_battleZone, 0));
                    world.getMaze().getZoneList().get(d+ Maze.Opponent_battleZone).getZoneArray().add(0,Dcard);
                    cardWidget = world.getWidgetCoordinator().DecoupleWidgetFormCard(Bcard);
                    world.getWidgetCoordinator().CoupleWidgetForCard(Dcard, cardWidget);
                    world.UpdateCardInfoToCardTable(Bcard, Dcard);
                    coordinator.SendAction(Actions.CleanSelectedCardIfMatch, Bcard);
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid zone of card");
        }

        return NewCard;
    }

    public static void ShuffleDeck(PvPWorld world, ArrayList<Cards> CardList) {
        int z = CardList.get(0).GridPosition().getZone();

        if (world.getMaze().getZoneList().get(z).zoneSize() != CardList.size())
            throw new IllegalArgumentException("Elements count in zone is not consistent");

        int Zonesize = CardList.size();

        for (int i = 0; i < Zonesize; i++){
            if (world.getMaze().getZoneList().get(z).getZoneArray().get(i) != CardList.get(i))
                throw new IllegalArgumentException("Card didn't match which is invalid");
        }
        int tempindex;
        Random R = new Random();
        Cards A,B;

        for (int i = 0; i < Zonesize; i++) {
            tempindex = (int) R.nextInt(Zonesize);
            A = CardList.get(tempindex);
            B = CardList.get(i);
            CardList.remove(tempindex);
            CardList.add(tempindex, B);
            CardList.remove(i);
            CardList.add(i, A);
        }

        if (CardList.size() != Zonesize)
            throw new IllegalArgumentException("Something went wrong during Shuffle");

        world.getMaze().getZoneList().get(z).getZoneArray().clear();
        for (int i =0; i < Zonesize; i++) {
            world.getMaze().getZoneList().get(z).getZoneArray().add(CardList.get(i));
        }
    }

    public static void CopyCardsToTempZone(PvPWorld world, Cards Card) {
        world.getMaze().getZoneList().get(Maze.temporaryZone).getZoneArray().add(Card);
    }

    public static Cards EvolveCreature(Cards Ecard, Cards Bcard, PvPWorld world) {
        PvPWidgetCoordinator coordinator = world.getWidgetCoordinator();
        int zoneOfbase = Bcard.GridPosition().getZone();
        int zoneOfevo = Ecard.GridPosition().getZone();

        if ((zoneOfbase != Maze.battleZone && zoneOfbase != Maze.Opponent_battleZone) ||
                (zoneOfevo != Maze.hand && zoneOfevo != Maze.Opponent_hand))
            throw new IllegalArgumentException("invalid evolution config");

        int i = world.getMaze().getZoneList().get(zoneOfbase).getZoneArray().indexOf(Bcard);
        int j = world.getMaze().getZoneList().get(zoneOfevo).getZoneArray().indexOf(Ecard);
        world.getMaze().getZoneList().get(zoneOfbase).getZoneArray().remove(i);
        world.getMaze().getZoneList().get(zoneOfevo).getZoneArray().remove(j);

       // world.getGridIndexTrackingTable().clearGridIndex(Bcard.GridPosition());
        //world.getGridIndexTrackingTable().clearGridIndex(Ecard.GridPosition());

        Cards newCard;
        if (zoneOfbase == Maze.battleZone) {
            ActiveCard Ncard = new ActiveCard(Ecard.ExtractCardInfo(), new GridPositionIndex(Maze.battleZone,
                    Bcard.GridPosition().getGridIndex()));
            world.getMaze().getZoneList().get(Maze.battleZone).getZoneArray().add(Ncard);
            CardWidget cardWidget = world.getWidgetCoordinator().DecoupleWidgetFormCard(Ecard);
            world.getWidgetCoordinator().CoupleWidgetForCard(Ncard, cardWidget);
            world.UpdateCardInfoToCardTable(Ecard, Ncard);
            coordinator.SendAction(Actions.CleanSelectedCardIfMatch, Ecard);
          //  world.getGridIndexTrackingTable().trackGridIndex(Ncard.GridPosition(), Ncard);
            Cards Nbcard = new Cards(Bcard.ExtractCardInfo(), new GridPositionIndex(Maze.battleZone,
                    Bcard.GridPosition().getGridIndex()));
            world.getMaze().TrackEvolution(Ncard, Nbcard);
            cardWidget = world.getWidgetCoordinator().DecoupleWidgetFormCard(Bcard);
            world.getWidgetCoordinator().CoupleWidgetForCard(Nbcard, cardWidget);
            world.UpdateCardInfoToCardTable(Bcard, Nbcard);
            coordinator.SendAction(Actions.CleanSelectedCardIfMatch, Bcard);
            newCard = Ncard;
        } else {
            InactiveCard Ncard = new ActiveCard(Ecard.ExtractCardInfo(), new GridPositionIndex(Maze.Opponent_battleZone,
                    Bcard.GridPosition().getGridIndex()));
            world.getMaze().getZoneList().get(Maze.Opponent_battleZone).getZoneArray().add(Ncard);
            CardWidget cardWidget = world.getWidgetCoordinator().DecoupleWidgetFormCard(Ecard);
            world.getWidgetCoordinator().CoupleWidgetForCard(Ncard, cardWidget);
            world.UpdateCardInfoToCardTable(Ecard, Ncard);
            coordinator.SendAction(Actions.CleanSelectedCardIfMatch, Ecard);
          //  world.getGridIndexTrackingTable().trackGridIndex(Ncard.GridPosition(), Ncard);
            Cards Nbcard = new Cards(Bcard.ExtractCardInfo(), new GridPositionIndex(Maze.Opponent_battleZone,
                    Bcard.GridPosition().getGridIndex()));
            world.getMaze().TrackEvolution(Ncard, Nbcard);
            cardWidget = world.getWidgetCoordinator().DecoupleWidgetFormCard(Bcard);
            world.getWidgetCoordinator().CoupleWidgetForCard(Nbcard, cardWidget);
            world.UpdateCardInfoToCardTable(Bcard, Nbcard);
            coordinator.SendAction(Actions.CleanSelectedCardIfMatch, Bcard);
            newCard = Ncard;
        }

        return newCard;
    }

    public static void ApplyEventsInt(String event, PvPWorld world){
        String[] eventField = event.split(" ");

        if (eventField.length != 8)
            throw new IllegalArgumentException("Invalid eventLog");


        int Cardzone = Integer.parseInt(eventField[0]);
        int GridIndex = Integer.parseInt(eventField[1]);
        boolean move = eventField[3].equals("0") ? false : true;
        boolean set = eventField[6].equals("0") ? false : true;
        if (Cardzone != 4 && Cardzone != 5 && Cardzone != 11 && Cardzone != 12) {
            InactiveCard card = null;//(InactiveCard) world.getGridIndexTrackingTable().getCardMappedToGivenGridPosition(Cardzone,GridIndex);
            if (!card.getNameID().equals(eventField[2]))
                throw new IllegalArgumentException("Data inconsistency");

            if (move) {
                String moveInstruction = InstSetUtil.GenerateSelfChangeZoneInstruction(Integer.parseInt(eventField[4]));
                InstructionSet instruction = new InstructionSet(moveInstruction);
                world.getInstructionHandler().setCardAndInstruction(card, instruction);
                world.getInstructionHandler().execute();
            }

            if (!eventField[5].equals("0") && !eventField[5].equals("MarkedCard")) {
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
}
