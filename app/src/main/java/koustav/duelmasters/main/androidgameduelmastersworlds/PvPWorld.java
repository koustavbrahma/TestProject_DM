package koustav.duelmasters.main.androidgameduelmastersworlds;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionHandler;
import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionSet;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Maze;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.PackedCardInfo;
import koustav.duelmasters.main.androidgameduelmasterseventlogmodule.EventLog;
import koustav.duelmasters.main.androidgameduelmastersstatemachine.InstructionIteratorHandler;
import koustav.duelmasters.main.androidgameduelmastersstatemachine.WorldUpdateOffTurn;
import koustav.duelmasters.main.androidgameduelmastersstatemachine.WorldUpdateOnTurn;
import koustav.duelmasters.main.androidgameduelmastersutil.ActUtil;
import koustav.duelmasters.main.androidgameduelmastersutil.InstSetUtil;
import koustav.duelmasters.main.androidgameduelmasterswidgetcoordinationtools.Actions;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.GridIndexTrackingTable;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.GridPositionIndex;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.PvPWidgetCoordinator;
import koustav.duelmasters.main.androidgamesframework.Input.TouchEvent;
import koustav.duelmasters.main.androidgamesframeworkimpl.AndroidGame;

/**
 * Created by Koustav on 2/16/2015.
 */
public class PvPWorld implements World {
    AndroidGame game;
    Maze maze;
    // Coordinator
    PvPWidgetCoordinator widgetCoordinator;
    InstructionHandler instructionHandler;
    GridIndexTrackingTable gridIndexTrackingTable; // nr

    ArrayList<PackedCardInfo> cardInfos;
    ArrayList<PackedCardInfo> oppCardInfos;
    Hashtable<PackedCardInfo, Cards> cardInfoToCard;
    Hashtable<PackedCardInfo, Cards> oppCardInfoToCard;
    boolean TURN;

    // State Machines
    WorldUpdateOnTurn worldUpdateOnTurn;
    WorldUpdateOffTurn worldUpdateOffTurn;
    InstructionIteratorHandler instructionIteratorHandler;
    List<TouchEvent> touchEvents; // nr

    Cards FetchCard;
    Hashtable<String, Integer> WorldFlag;
    EventLog eventLog;

    int frameBufferWidth; // nr
    int frameBufferHeight; //nr

    public PvPWorld(AndroidGame game, boolean Turn) {
        this.game = game;
        maze = new Maze();
        widgetCoordinator = new PvPWidgetCoordinator(maze);
        gridIndexTrackingTable = new GridIndexTrackingTable();
        cardInfos = new ArrayList<PackedCardInfo>();
        oppCardInfos = new ArrayList<PackedCardInfo>();
        cardInfoToCard = new Hashtable<PackedCardInfo, Cards>();
        oppCardInfoToCard = new Hashtable<PackedCardInfo, Cards>();
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
    }

    public AndroidGame getGame() {
        return game;
    }

    public Maze getMaze() {
        return maze;
    }

    public PvPWidgetCoordinator getWidgetCoordinator() {
        return widgetCoordinator;
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

    public int GetRefIndexOfMyCard(Cards card) {
        int index = cardInfos.indexOf(card.cardInfo());
        if (index == -1) {
            throw new RuntimeException("Invalid Condition");
        }

        return index;
    }

    public int GetRefIndexOfOppCard(Cards card) {
        int index = oppCardInfos.indexOf(card.cardInfo());
        if (index == -1) {
            throw new RuntimeException("Invalid Condition");
        }

        return index;
    }

    public Cards GetMyCardForGivenRefIndex(int index) {
        PackedCardInfo cardInfo = cardInfos.get(index);
        if (cardInfo == null) {
            throw new RuntimeException("Invalid Condition");
        }

        return cardInfoToCard.get(cardInfo);
    }

    public Cards GetOppCardForGivenRefIndex(int index) {
        PackedCardInfo cardInfo = oppCardInfos.get(index);
        if (cardInfo == null) {
            throw new RuntimeException("Invalid Condition");
        }

        return cardInfoToCard.get(cardInfo);
    }

    public void UpdateCardInfoToCardTable(Cards oldCard, Cards newCard) {
        GridPositionIndex gridPosition = newCard.GridPosition();

        if (gridPosition.getZone() < Maze.temporaryZone) {
            PackedCardInfo cardInfo = newCard.cardInfo();
            Cards card = cardInfoToCard.remove(cardInfo);
            if (card != oldCard) {
                throw new RuntimeException("Invalid Condition");
            }
            cardInfoToCard.put(cardInfo, newCard);
        } else if (gridPosition.getZone() > Maze.temporaryZone) {
            PackedCardInfo cardInfo = newCard.cardInfo();
            Cards card = oppCardInfoToCard.remove(cardInfo);
            if (card != oldCard) {
                throw new RuntimeException("Invalid Condition");
            }
            oppCardInfoToCard.put(cardInfo, newCard);
        } else {
            throw new RuntimeException("Invalid Condition");
        }
    }

    public void load() {
        CreateMyDeck();
        CreateOppDeck();
        CreateScenario();
    }

    private void CreateMyDeck() {
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
                    maze.getZoneList().get(Maze.deck).getZoneArray().add(card);
                    cardInfos.add(card.cardInfo());
                    cardInfoToCard.put(card.cardInfo(), card);
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

    private void CreateOppDeck() {
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
                    maze.getZoneList().get(Maze.Opponent_deck).getZoneArray().add(card);
                    oppCardInfos.add(card.cardInfo());
                    oppCardInfoToCard.put(card.cardInfo(), card);
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

    private void CreateScenario() {
        try {
            InputStream scenario = game.getFileIO().readAsset("Scenario");
            BufferedReader scenarioStream = new BufferedReader(new InputStreamReader(scenario));
            String MyHeader;
            String scenarioInfo;
            ArrayList<Cards> cardList = new ArrayList<Cards>();
            while (!(MyHeader = scenarioStream.readLine()).equals("#MY"));
            while (!(MyHeader = scenarioStream.readLine()).equals("#MYEND")) {
                cardList.clear();
                while (!(scenarioInfo = scenarioStream.readLine()).equals("HandZoneEnd")) {
                    int val = Integer.parseInt(scenarioInfo);
                    Cards card = GetMyCardForGivenRefIndex(val);
                    cardList.add(card);
                }
                getWidgetCoordinator().SendAction(Actions.SetUpScenario, Maze.hand, cardList);
                for (int i = 0; i < cardList.size(); i++) {
                    Cards card = cardList.get(i);
                    String Instructionstr = InstSetUtil.GenerateSelfChangeZoneInstruction(Maze.hand);
                    InstructionSet instruction = new InstructionSet(Instructionstr);
                    ActUtil.ChangeZoneOperator(this, card, instruction);
                }
                scenarioInfo = scenarioStream.readLine();
                cardList.clear();
                while (!(scenarioInfo = scenarioStream.readLine()).equals("BattleZoneEnd")) {
                    int val = Integer.parseInt(scenarioInfo);
                    Cards card = GetMyCardForGivenRefIndex(val);
                    cardList.add(card);
                }
                getWidgetCoordinator().SendAction(Actions.SetUpScenario, Maze.battleZone, cardList);
                for (int i = 0; i < cardList.size(); i++) {
                    Cards card = cardList.get(i);
                    String Instructionstr = InstSetUtil.GenerateSelfChangeZoneInstruction(Maze.battleZone);
                    InstructionSet instruction = new InstructionSet(Instructionstr);
                    ActUtil.ChangeZoneOperator(this, card, instruction);
                }
                scenarioInfo = scenarioStream.readLine();
                cardList.clear();
                while (!(scenarioInfo = scenarioStream.readLine()).equals("ManaZoneEnd")) {
                    int val = Integer.parseInt(scenarioInfo);
                    Cards card = GetMyCardForGivenRefIndex(val);
                    cardList.add(card);
                }
                getWidgetCoordinator().SendAction(Actions.SetUpScenario, Maze.manaZone, cardList);
                for (int i = 0; i < cardList.size(); i++) {
                    Cards card = cardList.get(i);
                    String Instructionstr = InstSetUtil.GenerateSelfChangeZoneInstruction(Maze.manaZone);
                    InstructionSet instruction = new InstructionSet(Instructionstr);
                    ActUtil.ChangeZoneOperator(this, card, instruction);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(float deltatime, float totalTime) {
        widgetCoordinator.PvPWidgetsTouchListener();

        if(TURN) {
            worldUpdateOnTurn.update();
        } else {
            worldUpdateOffTurn.update();
        }

        widgetCoordinator.update(deltatime, totalTime);
    }

    @Override
    public void draw() {
        widgetCoordinator.draw();
    }
}
