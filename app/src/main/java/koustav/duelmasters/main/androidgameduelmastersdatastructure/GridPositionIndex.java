package koustav.duelmasters.main.androidgameduelmastersdatastructure;

/**
 * Created by Koustav on 4/24/2015.
 */
public class GridPositionIndex {
   // Cards card;
    int zone;
    int GridIndex;

    public GridPositionIndex(int zone, int GridIndex) {
        //this.card = card;
        this.zone = zone;
        this.GridIndex = GridIndex;
    }

    //public Cards getCard() {
     //   return card;
    //}

    public int getZone() {
        return zone;
    }

    public int getGridIndex() {
        return GridIndex;
    }
}
