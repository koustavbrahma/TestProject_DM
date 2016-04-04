package koustav.duelmasters.main.androidgameduelmastersdatastructure;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Koustav on 2/16/2015.
 */
public class Maze {
    public static int battleZone = 0;
    public static int manaZone = 1;
    public static int shieldZone = 2;
    public static int hand = 3;
    public static int graveyard = 4;
    public static int deck = 5;
    public static int temporaryZone = 6;
    public static int Opponent_battleZone= 7;
    public static int Opponent_manaZone= 8;
    public static int Opponent_shieldZone= 9;
    public static int Opponent_hand= 10;
    public static int Opponent_graveyard= 11;
    public static int Opponent_deck= 12;

    ArrayList<Zone> zoneList;

    Zone BattleZone;
    Zone ManaZone;
    Zone ShieldZone;
    Zone Hand;
    Zone Graveyard;
    Zone Deck;

    Zone OBattleZone;
    Zone OManaZone;
    Zone OShieldZone;
    Zone OHand;
    Zone OGraveyard;
    Zone ODeck;

    Zone TempZone;

    Hashtable<Cards, Cards> EvolutionTracker;

    public Maze() {
        zoneList = new ArrayList<Zone>();

        BattleZone = new Zone();
        ManaZone = new Zone();
        ShieldZone = new Zone();
        Hand = new Zone();
        Graveyard = new Zone();
        Deck = new Zone();


        OBattleZone = new Zone();
        OManaZone = new Zone();
        OShieldZone = new Zone();
        OHand = new Zone();
        OGraveyard = new Zone();
        ODeck = new Zone();

        TempZone = new Zone();

        zoneList.add(BattleZone);    //index 0
        zoneList.add(ManaZone);      //index 1
        zoneList.add(ShieldZone);    //index 2
        zoneList.add(Hand);          //index 3
        zoneList.add(Graveyard);     //index 4
        zoneList.add(Deck);          //index 5
        zoneList.add(TempZone);      //index 6

        zoneList.add(OBattleZone);   //index 7
        zoneList.add(OManaZone);     //index 8
        zoneList.add(OShieldZone);   //index 9
        zoneList.add(OHand);         //index 10
        zoneList.add(OGraveyard);    //index 11
        zoneList.add(ODeck);         //index 12

        EvolutionTracker = new Hashtable<Cards, Cards>();
    }

    public ArrayList<Zone> getZoneList() {
        return zoneList;
    }

    public void TrackEvolution(Cards Ecard, Cards Bcard) {
        EvolutionTracker.put(Ecard, Bcard);
    }

    public Cards ClearEvolutionTrack(Cards card){
        return EvolutionTracker.remove(card);
    }

    public Cards GetBaseCard(Cards card) {
        return EvolutionTracker.get(card);
    }
}
