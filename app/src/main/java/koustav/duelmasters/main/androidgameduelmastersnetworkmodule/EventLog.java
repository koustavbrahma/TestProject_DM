package koustav.duelmasters.main.androidgameduelmastersnetworkmodule;

import java.util.ArrayList;

import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionSet;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.World;
import koustav.duelmasters.main.androidgamesframework.Screen;

/**
 * Created by Koustav on 7/7/2015.
 */
public class EventLog {
    ArrayList<String> events;
    ArrayList<String> HoldMsg;
    ArrayList<InstructionSet> HoldCleanUp;
    ArrayList<String> EventsToExecute;
    boolean recording;

    public EventLog() {
        events = new ArrayList<String>();
        HoldMsg = new ArrayList<String>();
        HoldCleanUp = new ArrayList<InstructionSet>();
        EventsToExecute = new ArrayList<String>();
        recording = false;
    }

    public void setRecording(boolean val) {
        recording = val;
    }

    public boolean getRecording() {
        return recording;
    }

    public void registerEvent(Cards card, boolean move, int zone, String attribute, boolean set, int val) {
        if (!recording)
            return;

        int cardzone = card.GridPosition().getZone();
        if (cardzone < 6) {
            cardzone = cardzone +7;
        } else if (cardzone > 6) {
            cardzone = cardzone - 7;
        } else {
            throw new IllegalArgumentException("Invalid zone of card");
        }
        int m = move ? 1 : 0;
        int s = set ? 1: 0;
        String a = (attribute != null) ? attribute : "0";
        String eventString;
        eventString = cardzone + " " + card.GridPosition().getGridIndex() + " " + card.getNameID()+ " "
                + m + " " + zone + " " + a + " " + s + " " + val;
        events.add(eventString);
    }

    public String getAndClearEvents() {
        String eventTrunk = null;
        if (events.size() != 0) {
            eventTrunk = new String(events.get(0));
            for (int i =1; i < events.size(); i++) {
                eventTrunk = eventTrunk.concat("#");
                eventTrunk = eventTrunk.concat(events.get(i));
            }
        }
        events.clear();
        return eventTrunk;
    }

    public void AddHoldMsg(String msg) {
        HoldMsg.add(msg);
    }

    public String getHoldMsg(){
        String holdmsg = null;
        if (HoldMsg.size() !=0) {
            holdmsg = new String(HoldMsg.get(0));
            for (int i = 1; i < HoldMsg.size(); i++) {
                holdmsg = holdmsg.concat("#");
                holdmsg = holdmsg.concat(HoldMsg.get(i));
            }
        }
        HoldMsg.clear();
        return holdmsg;
    }

    public void AddHoldCleanUp(ArrayList<InstructionSet> Inst) {
        for (int i = 0 ; i < Inst.size(); i++) {
            HoldCleanUp.add(Inst.get(i));
        }
    }

    public ArrayList<InstructionSet> getHoldCleanUp() {
        ArrayList<InstructionSet> holdCleanUP = null;
        if (HoldCleanUp.size() != 0) {
            holdCleanUP = new ArrayList<InstructionSet>();
            for (int i = 0; i < holdCleanUP.size(); i++) {
                holdCleanUP.add(HoldCleanUp.get(i));
            }
        }
        HoldCleanUp.clear();
        return holdCleanUP;
    }

    public void AddEventsToExecute(String [] events){
        for (int i = 0; i < events.length; i++) {
            EventsToExecute.add(events[i]);
        }
    }

    public ArrayList<String> getEventsToExecute() {
        ArrayList<String> events = null;
        if (EventsToExecute.size() != 0) {
            events = new ArrayList<String>();
            for (int i =0; i < EventsToExecute.size(); i++) {
                events.add(EventsToExecute.get(i));
            }
        }
        EventsToExecute.clear();
        return events;
    }
}
