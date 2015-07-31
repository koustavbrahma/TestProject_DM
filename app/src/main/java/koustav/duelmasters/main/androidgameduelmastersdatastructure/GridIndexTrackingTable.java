package koustav.duelmasters.main.androidgameduelmastersdatastructure;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Koustav on 4/28/2015.
 */
public class GridIndexTrackingTable {
    ArrayList<Hashtable<Integer, Cards>> zoneList;

    Hashtable<Integer, Cards> BattleZone;
    Hashtable<Integer, Cards> ManaZone;
    Hashtable<Integer, Cards> ShieldZone;
    Hashtable<Integer, Cards> Hand;
    Hashtable<Integer, Cards> Graveyard;
    Hashtable<Integer, Cards> Deck;
    Hashtable<Integer, Cards> TempZone;
    Hashtable<Integer, Cards> OShieldZone;
    Hashtable<Integer, Cards> OBattleZone;
    Hashtable<Integer, Cards> OManaZone;
    Hashtable<Integer, Cards> OHand;
    Hashtable<Integer, Cards> OGraveyard;
    Hashtable<Integer, Cards> ODeck;

/*
 tracking is not done for graveyard, deck and tempzone.
 */
    public GridIndexTrackingTable() {
        zoneList = new ArrayList<Hashtable<Integer, Cards>>();

        BattleZone = new Hashtable<Integer, Cards>();
        ManaZone = new Hashtable<Integer, Cards>();
        ShieldZone = new Hashtable<Integer, Cards>();
        Hand = new Hashtable<Integer, Cards>();
        Graveyard = new Hashtable<Integer, Cards>();
        Deck = new Hashtable<Integer, Cards>();
        TempZone = new Hashtable<Integer, Cards>();
        OShieldZone = new Hashtable<Integer, Cards>();
        OBattleZone = new Hashtable<Integer, Cards>();
        OManaZone = new Hashtable<Integer, Cards>();
        OHand = new Hashtable<Integer, Cards>();
        OGraveyard = new Hashtable<Integer, Cards>();
        ODeck = new Hashtable<Integer, Cards>();

        zoneList.add(BattleZone);    //index 0
        zoneList.add(ManaZone);      //index 1
        zoneList.add(ShieldZone);    //index 2
        zoneList.add(Hand);          //index 3
        zoneList.add(Graveyard);     //index 4
        zoneList.add(Deck);          //index 5
        zoneList.add(TempZone);      //index 6
        zoneList.add(OShieldZone);   //index 7
        zoneList.add(OBattleZone);   //index 8
        zoneList.add(OManaZone);     //index 9
        zoneList.add(OHand);         //index 10
        zoneList.add(OGraveyard);    //index 11
        zoneList.add(ODeck);         //index 12
    }

    public int getNewGridIndex(int zone) {
        int GridIndex = 0;
        while (zoneList.get(zone).containsKey(new Integer(GridIndex))) {
            GridIndex++;
        }

        return GridIndex;
    }

    public void trackGridIndex(GridPositionIndex gridPositionIndex, Cards card) {
        if (gridPositionIndex.getZone() == 4 || gridPositionIndex.getZone() == 5 || gridPositionIndex.getZone() == 6
                || gridPositionIndex.getZone() == 11 || gridPositionIndex.getZone() == 12)
            throw new IllegalArgumentException("Invalid zone to track");
        zoneList.get(gridPositionIndex.getZone()).put(new Integer(gridPositionIndex.getGridIndex()), card);
    }

    public Cards clearGridIndex(GridPositionIndex gridPositionIndex) {
        if (gridPositionIndex.getZone() == 4 || gridPositionIndex.getZone() == 5 || gridPositionIndex.getZone() == 6
                || gridPositionIndex.getZone() == 11 || gridPositionIndex.getZone() == 12)
            throw new IllegalArgumentException("Invalid zone to track");
        return zoneList.get(gridPositionIndex.getZone()).remove(new Integer(gridPositionIndex.getGridIndex()));
    }

    public Cards getCardMappedToGivenGridPosition(int zone, int gridIndex) {
        return zoneList.get(zone).get(new Integer(gridIndex));
    }
}
