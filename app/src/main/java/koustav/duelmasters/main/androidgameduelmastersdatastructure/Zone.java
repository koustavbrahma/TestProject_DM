package koustav.duelmasters.main.androidgameduelmastersdatastructure;

import java.util.ArrayList;

/**
 * Created by Koustav on 4/21/2015.
 */
public class Zone {
    ArrayList<Cards> zoneArray;

    public Zone() {
        zoneArray = new ArrayList<Cards>();
    }

    public int zoneSize() {
        return zoneArray.size();
    }

    public ArrayList<Cards> getZoneArray() {
        return zoneArray;
    }
}
