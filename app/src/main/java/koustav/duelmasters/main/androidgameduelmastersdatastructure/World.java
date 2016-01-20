package koustav.duelmasters.main.androidgameduelmastersdatastructure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.List;

import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionHandler;
import koustav.duelmasters.main.androidgameduelmasterseventlogmodule.EventLog;
import koustav.duelmasters.main.androidgameduelmastersstatemachine.InstructionIteratorHandler;
import koustav.duelmasters.main.androidgameduelmastersstatemachine.WorldUpdateOffTurn;
import koustav.duelmasters.main.androidgameduelmastersstatemachine.WorldUpdateOnTurn;
import koustav.duelmasters.main.androidgamesframework.Game;
import koustav.duelmasters.main.androidgamesframework.Input.TouchEvent;

/**
 * Created by Koustav on 2/16/2015.
 */
public class World {
    Game game;
    Maze maze;
    GridIndexTrackingTable gridIndexTrackingTable;
    boolean TURN;
    WorldUpdateOnTurn worldUpdateOnTurn;
    WorldUpdateOffTurn worldUpdateOffTurn;
    List<TouchEvent> touchEvents;
    Cards FetchCard;
    Hashtable<String, Integer> WorldFlag;
    InstructionHandler instructionHandler;
    InstructionIteratorHandler instructionIteratorHandler;
    EventLog eventLog;
    int frameBufferWidth;
    int frameBufferHeight;

    public World(Game game, boolean Turn) {
        this.game = game;
        maze = new Maze();
        gridIndexTrackingTable = new GridIndexTrackingTable();
        TURN = Turn;
        worldUpdateOnTurn = new WorldUpdateOnTurn(this, Turn);
        worldUpdateOffTurn = new WorldUpdateOffTurn(this);
        touchEvents = null;
        FetchCard = null;
        WorldFlag = new Hashtable<String, Integer>();
        instructionHandler = new InstructionHandler(this);
        instructionIteratorHandler = new InstructionIteratorHandler(this);
        eventLog = new EventLog();
        frameBufferWidth = game.getframeBufferWidth();
        frameBufferHeight = game.getframeBufferHeight();
        CreateMyDeck();
        CreateOppDeck();
    }

    public Game getGame() {
        return game;
    }

    public Maze getMaze() {
        return maze;
    }

    public GridIndexTrackingTable getGridIndexTrackingTable() {
        return gridIndexTrackingTable;
    }

    public void setTouchEvents(List<TouchEvent> touchEvents) {
        this.touchEvents = touchEvents;
    }

    public List<TouchEvent> getTouchEvents() {
        return touchEvents;
    }

    public void setFetchCard(Cards card) {
        this.FetchCard = card;
    }
    
    public Cards getFetchCard() {
        return this.FetchCard;
    }

    public boolean getTurn() {
        return this.TURN;
    }

    public void  setTurn(boolean val) {
        this.TURN = val;
    }

    public InstructionIteratorHandler getInstructionIteratorHandler() {
        return this.instructionIteratorHandler;
    }

    public boolean getWorldFlag(String flag){
        if (WorldFlag.get(flag) == null) {
            return false;
        }

        return true;
    }

    public void setWorldFlag(String flag) {
        clearWorldFlag(flag);
        WorldFlag.put(flag, new Integer(1));
    }

    public void clearWorldFlag(String flag) {
        if (WorldFlag.get(flag) != null)
            WorldFlag.remove(flag);
    }

    public int getframeBufferWidht() {
        return this.frameBufferWidth;
    }

    public int getframeBufferHeight() {
        return this.frameBufferHeight;
    }

    public InstructionHandler getInstructionHandler() {
        return this.instructionHandler;
    }

    public EventLog getEventLog() {
        return this.eventLog;
    }

    public void CreateMyDeck() {
        try {
            InputStream DeckList = game.getFileIO().readAsset("Deck.txt");
            BufferedReader bufferedReaderDeckList = new BufferedReader(new InputStreamReader(DeckList));
            String SetName;
            String CardName;
            while ((CardName = bufferedReaderDeckList.readLine()) != null ) {
                String[] SplitVal = CardName.split("#");
                if (SplitVal.length != 2) {
                    throw new IllegalArgumentException("Invalid Card Name in Deck");
                }
                CardName = SplitVal[0];
                SetName = SplitVal[1];
                GridPositionIndex gridPosition = new GridPositionIndex(5, 0);
                Cards card = new Cards(new PackedCardInfo(), gridPosition);
                String CardTitle = "Begin";
                CardTitle = CardTitle.concat(CardName);
                InputStream CardLibList = game.getFileIO().readAsset(SetName);
                BufferedReader bufferedReaderCardLibList = new BufferedReader(new InputStreamReader(CardLibList));
                String CardPackedInfo;
                while (!CardTitle.equals(CardPackedInfo = bufferedReaderCardLibList.readLine()) && CardPackedInfo != null);
                if (CardPackedInfo != null){
                    if (CardTitle.equals(CardPackedInfo)) {
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndSlotAttributes")) {
                            card.cardInfo().SlotAttributes.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndFlagAttributes")) {
                            card.cardInfo().FlagAttributes.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndPrimaryInstructionIndex")) {
                            card.cardInfo().PrimaryInstructionIndex.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndPrimaryInstructions")) {
                            card.cardInfo().PrimaryInstruction.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndCrossInstructionIndex")) {
                            card.cardInfo().CrossInstructionIndex.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndCrossInstruction")) {
                            card.cardInfo().CrossInstruction.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                    }
                    maze.getZoneList().get(5).getZoneArray().add(card);
                }
                bufferedReaderCardLibList.close();
                CardLibList.close();
            }
            bufferedReaderDeckList.close();
            DeckList.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void CreateOppDeck() {
        try {
            InputStream DeckList = game.getFileIO().readAsset("Deck.txt");
            BufferedReader bufferedReaderDeckList = new BufferedReader(new InputStreamReader(DeckList));
            String SetName;
            String CardName;
            while ((CardName = bufferedReaderDeckList.readLine()) != null ) {
                String[] SplitVal = CardName.split("#");
                if (SplitVal.length != 2) {
                    throw new IllegalArgumentException("Invalid Card Name in Deck");
                }
                CardName = SplitVal[0];
                SetName = SplitVal[1];
                GridPositionIndex gridPosition = new GridPositionIndex(12, 0);
                Cards card = new Cards(new PackedCardInfo(), gridPosition);
                String CardTitle = "Begin";
                CardTitle = CardTitle.concat(CardName);
                InputStream CardLibList = game.getFileIO().readAsset(SetName);
                BufferedReader bufferedReaderCardLibList = new BufferedReader(new InputStreamReader(CardLibList));
                String CardPackedInfo;
                while (!CardTitle.equals(CardPackedInfo = bufferedReaderCardLibList.readLine()) && CardPackedInfo != null);
                if (CardPackedInfo != null){
                    if (CardTitle.equals(CardPackedInfo)) {
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndSlotAttributes")) {
                            card.cardInfo().SlotAttributes.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndFlagAttributes")) {
                            card.cardInfo().FlagAttributes.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndPrimaryInstructionIndex")) {
                            card.cardInfo().PrimaryInstructionIndex.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndPrimaryInstructions")) {
                            card.cardInfo().PrimaryInstruction.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndCrossInstructionIndex")) {
                            card.cardInfo().CrossInstructionIndex.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndCrossInstruction")) {
                            card.cardInfo().CrossInstruction.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                    }
                    maze.getZoneList().get(12).getZoneArray().add(card);
                }
                bufferedReaderCardLibList.close();
                CardLibList.close();
            }
            bufferedReaderDeckList.close();
            DeckList.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(float deltatime) {
        if(TURN)
            worldUpdateOnTurn.update(deltatime);
        else
            worldUpdateOffTurn.update(deltatime);
    }
}
