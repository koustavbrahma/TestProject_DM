package koustav.duelmasters.main.androidgameduelmastersutil;

import java.util.ArrayList;
import java.util.Random;

import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionSet;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.ActiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.GridPositionIndex;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.InactiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.World;

/**
 * Created by Koustav on 4/26/2015.
 */
public class ActUtil {
    public static Cards ChangeZoneOperator(World world, Cards card, InstructionSet instruction) {
        GridPositionIndex gridPosition = card.GridPosition();
        Cards NewCard = null;
        if (gridPosition.getZone() >= 0 && gridPosition.getZone() <= 5) {
            int z = gridPosition.getZone();
            int d = instruction.getActionDestination();
            if (d > 5)
                throw new IllegalArgumentException("Invalid destination to transfer card (1)");
            world.getEventLog().registerEvent(card, true, d, null, false, 0);
            int i = world.getMaze().getZoneList().get(z).getZoneArray().indexOf(card);
            world.getMaze().getZoneList().get(z).getZoneArray().remove(i);
            if (z-4 != 0 &&  z-5 != 0) {
                world.getGridIndexTrackingTable().clearGridIndex(card.GridPosition());
            }
            if (d == 0 || d == 3) {
                int GridIndex = world.getGridIndexTrackingTable().getNewGridIndex(d);
                Cards Bcard = world.getMaze().GetBaseCard(card);
                ActiveCard Acard = new ActiveCard(card.ExtractCardInfo(), new GridPositionIndex(d, GridIndex));
                NewCard = Acard;
                world.getMaze().getZoneList().get(d).getZoneArray().add(Acard);
                world.getGridIndexTrackingTable().trackGridIndex(Acard.GridPosition(), Acard);
                if (Bcard != null) {
                    world.getMaze().ClearEvolutionTrack(card);
                    GridIndex = world.getGridIndexTrackingTable().getNewGridIndex(d);
                    Acard = new ActiveCard(Bcard.ExtractCardInfo(), new GridPositionIndex(d, GridIndex));
                    world.getMaze().getZoneList().get(d).getZoneArray().add(Acard);
                    world.getGridIndexTrackingTable().trackGridIndex(Acard.GridPosition(), Acard);
                }
            }

            if (d == 1 || d == 2) {
                int GridIndex = world.getGridIndexTrackingTable().getNewGridIndex(d);
                Cards Bcard = world.getMaze().GetBaseCard(card);
                InactiveCard Icard = new InactiveCard(card.ExtractCardInfo(), new GridPositionIndex(d, GridIndex));
                NewCard  = Icard;
                world.getMaze().getZoneList().get(d).getZoneArray().add(Icard);
                world.getGridIndexTrackingTable().trackGridIndex(Icard.GridPosition(), Icard);
                if (Bcard != null) {
                    world.getMaze().ClearEvolutionTrack(card);
                    GridIndex = world.getGridIndexTrackingTable().getNewGridIndex(d);
                    Icard = new InactiveCard(Bcard.ExtractCardInfo(), new GridPositionIndex(d, GridIndex));
                    world.getMaze().getZoneList().get(d).getZoneArray().add(Icard);
                    world.getGridIndexTrackingTable().trackGridIndex(Icard.GridPosition(), Icard);
                }
            }

            if (d == 4 || d == 5) {
                Cards Bcard = world.getMaze().GetBaseCard(card);
                Cards Dcard = new Cards(card.ExtractCardInfo(), new GridPositionIndex(d, 0));
                world.getMaze().getZoneList().get(d).getZoneArray().add(0,Dcard);
                if (Bcard != null) {
                    world.getMaze().ClearEvolutionTrack(card);
                    Dcard = new InactiveCard(Bcard.ExtractCardInfo(), new GridPositionIndex(d, 0));
                    world.getMaze().getZoneList().get(d).getZoneArray().add(0, Dcard);
                }
            }
        } else if (gridPosition.getZone() >= 7 && gridPosition.getZone() <=12) {
            int z = gridPosition.getZone();
            int d = instruction.getActionDestination();
            if (d > 5)
                throw new IllegalArgumentException("Invalid destination to transfer card (2)");
            world.getEventLog().registerEvent(card, true, d, null, false, 0);
            int i = world.getMaze().getZoneList().get(z).getZoneArray().indexOf(card);
            world.getMaze().getZoneList().get(z).getZoneArray().remove(i);
            if (z != 11 &&  z != 12) {
                world.getGridIndexTrackingTable().clearGridIndex(card.GridPosition());
            }
            if (d >= 0 && d <= 3) {
                int GridIndex = world.getGridIndexTrackingTable().getNewGridIndex(d+7);
                Cards Bcard = world.getMaze().GetBaseCard(card);
                InactiveCard Icard = new InactiveCard(card.ExtractCardInfo(), new GridPositionIndex(d+7, GridIndex));
                NewCard  = Icard;
                world.getMaze().getZoneList().get(d+7).getZoneArray().add(Icard);
                world.getGridIndexTrackingTable().trackGridIndex(Icard.GridPosition(), Icard);
                if (Bcard != null) {
                    world.getMaze().ClearEvolutionTrack(card);
                    GridIndex = world.getGridIndexTrackingTable().getNewGridIndex(d+7);
                    Icard = new InactiveCard(Bcard.ExtractCardInfo(), new GridPositionIndex(d+7, GridIndex));
                    world.getMaze().getZoneList().get(d).getZoneArray().add(Icard);
                    world.getGridIndexTrackingTable().trackGridIndex(Icard.GridPosition(), Icard);
                }
            }

            if (d == 4 || d == 5) {
                Cards Bcard = world.getMaze().GetBaseCard(card);
                Cards Dcard = new Cards(card.ExtractCardInfo(), new GridPositionIndex(d+7, 0));
                world.getMaze().getZoneList().get(d+7).getZoneArray().add(0,Dcard);
                if (Bcard != null) {
                    world.getMaze().ClearEvolutionTrack(card);
                    Dcard = new InactiveCard(Bcard.ExtractCardInfo(), new GridPositionIndex(d+7, 0));
                    world.getMaze().getZoneList().get(d).getZoneArray().add(0,Dcard);
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid zone of card");
        }

        return NewCard;
    }

    public static void ShuffleDeck(World world, ArrayList<Cards> CardList) {
        int z = CardList.get(0).GridPosition().getZone();

        if (world.getMaze().getZoneList().get(z).zoneSize() != CardList.size())
            throw new IllegalArgumentException("Elements count in zone is not consistent");

        int Zonesize = CardList.size();

        for (int i = 0; i < Zonesize; i++){
            if (world.getMaze().getZoneList().get(z).getZoneArray().get(i) == CardList.get(i))
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

    public static void CopyCardsToTempZone(World world, Cards Card) {
        world.getMaze().getZoneList().get(6).getZoneArray().add(Card);
    }

    public static Cards EvolveCreature(Cards Ecard, Cards Bcard, World world) {
        int zoneOfbase = Bcard.GridPosition().getZone();
        int zoneOfevo = Ecard.GridPosition().getZone();

        if ((zoneOfbase != 0 && zoneOfbase != 7) || (zoneOfevo != 3 && zoneOfevo != 10))
            throw new IllegalArgumentException("invalid evolution config");

        int i = world.getMaze().getZoneList().get(zoneOfbase).getZoneArray().indexOf(Bcard);
        int j = world.getMaze().getZoneList().get(zoneOfevo).getZoneArray().indexOf(Ecard);
        world.getMaze().getZoneList().get(zoneOfbase).getZoneArray().remove(i);
        world.getMaze().getZoneList().get(zoneOfevo).getZoneArray().remove(j);

        world.getGridIndexTrackingTable().clearGridIndex(Bcard.GridPosition());
        world.getGridIndexTrackingTable().clearGridIndex(Ecard.GridPosition());

        Cards newCard;
        if (zoneOfbase == 0) {
            ActiveCard Ncard = new ActiveCard(Ecard.ExtractCardInfo(), new GridPositionIndex(0, Bcard.GridPosition().getGridIndex()));
            world.getMaze().getZoneList().get(0).getZoneArray().add(Ncard);
            world.getGridIndexTrackingTable().trackGridIndex(Ncard.GridPosition(), Ncard);
            Cards Nbcard = new Cards(Bcard.ExtractCardInfo(), null);
            world.getMaze().TrackEvolution(Ncard, Nbcard);
            newCard = Ncard;
        } else {
            InactiveCard Ncard = new ActiveCard(Ecard.ExtractCardInfo(), new GridPositionIndex(7, Bcard.GridPosition().getGridIndex()));
            world.getMaze().getZoneList().get(7).getZoneArray().add(Ncard);
            world.getGridIndexTrackingTable().trackGridIndex(Ncard.GridPosition(), Ncard);
            Cards Nbcard = new Cards(Bcard.ExtractCardInfo(), null);
            world.getMaze().TrackEvolution(Ncard, Nbcard);
            newCard = Ncard;
        }

        return newCard;
    }
}
