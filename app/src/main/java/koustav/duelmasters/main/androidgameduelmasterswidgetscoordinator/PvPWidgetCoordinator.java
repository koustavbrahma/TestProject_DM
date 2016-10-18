package koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Maze;
import koustav.duelmasters.main.androidgameduelmasterswidgetcoordinationtools.Actions;
import koustav.duelmasters.main.androidgameduelmasterswidgetcoordinationtools.Query;
import koustav.duelmasters.main.androidgameduelmasterswidgetcoordinationtools.Requests;
import koustav.duelmasters.main.androidgameduelmasterswidgetcoordinationtools.Simulation;
import koustav.duelmasters.main.androidgameduelmasterswidgetcoordinationtools.UIRequest;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetPosition;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetSelectedCardTracker;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchFocusLevel;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.FixedButtonsLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.ControllerButton;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.HeadOrientation;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.Layout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.WidgetTouchListener;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.BattleZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.CardStackZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.ControllerLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.HandZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.ManaZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardStackWidget;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.RectangleButtonWidget;
import koustav.duelmasters.main.androidgameduelmasterwidgetsimulation.CancelManaCard;
import koustav.duelmasters.main.androidgameduelmasterwidgetsimulation.CancelSummonOrManaAdd;
import koustav.duelmasters.main.androidgameduelmasterwidgetsimulation.DrawCardFromDeck;
import koustav.duelmasters.main.androidgameduelmasterwidgetsimulation.ManaAdd;
import koustav.duelmasters.main.androidgameduelmasterwidgetsimulation.PreManaAdd;
import koustav.duelmasters.main.androidgameduelmasterwidgetsimulation.PreSummonCard;
import koustav.duelmasters.main.androidgameduelmasterwidgetsimulation.Simulate;
import koustav.duelmasters.main.androidgameduelmasterwidgetsimulation.SummonCreatureCard;
import koustav.duelmasters.main.androidgameduelmasterwidgetsimulation.TransientManaCard;
import koustav.duelmasters.main.androidgameopenglobjectmodels.Cube;
import koustav.duelmasters.main.androidgameopenglobjectmodels.ScreenRectangle;
import koustav.duelmasters.main.androidgameopenglobjectmodels.XZRectangle;
import koustav.duelmasters.main.androidgameopenglutil.DrawObjectHelper;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;
import koustav.duelmasters.main.androidgameopenglutil.GLMaterial;
import koustav.duelmasters.main.androidgameopenglutil.MatrixHelper;
import koustav.duelmasters.main.androidgamesframework.Input;
import koustav.duelmasters.main.androidgamesframework.Pool;

import static android.opengl.GLES20.*;
/**
 * Created by Koustav on 3/12/2016.
 */

public class PvPWidgetCoordinator {
    public enum ZoomLevel {
        No_Zooming,
        Button,
        Touched,
        Button_Touched
    }

    ZoomLevel zoomLevel;
    public enum Expand {
        Nil,
        Battle_Z,
        Battle_OZ,
        Mana_Z,
        Mana_OZ,
        Deck,
        Deck_O,
        Graveyard,
        Graveyard_O,
        Hand_O,
        Player,
        Opponent
    }

    public enum Drag {
        Nil,
        Mana_Z,
        Hand,
    }

    boolean FlushButtons;

    public enum CardSelectMode {
        OFF,
        ON
    }

    CardSelectMode SelectCardMode;

    Maze maze;

    // CardWidget Pool
    Pool<CardWidget> cardWidgetPool;

    // CardStackWidget
    CardStackWidget Graveyard;
    CardStackWidget Deck;
    CardStackWidget Opponent_Graveyard;
    CardStackWidget Opponent_Deck;

    // ButtonWidget
    RectangleButtonWidget pauseButton;
    RectangleButtonWidget EndTurnButton;
    RectangleButtonWidget PlayerButton;
    RectangleButtonWidget OpponentButton;

    RectangleButtonWidget AcceptButton;
    RectangleButtonWidget DeclineButton;
    RectangleButtonWidget SummonOrCastButton;
    RectangleButtonWidget AddToManaButton;
    RectangleButtonWidget AttackButton;
    RectangleButtonWidget BlockButton;
    RectangleButtonWidget TapAbilityButton;
    RectangleButtonWidget ZoomButton;

    // GLObjects
    XZRectangle Base;
    Cube cube;
    Cube glCard;
    ScreenRectangle glRbutton;

    // Zone Layouts
    BattleZoneLayout battleZoneLayout;
    BattleZoneLayout opponentBattleZoneLayout;
    ManaZoneLayout manaZoneLayout;
    ManaZoneLayout opponentManaZoneLayout;
    CardStackZoneLayout deckLayout;
    CardStackZoneLayout opponentDeckLayout;
    CardStackZoneLayout graveyardLayout;
    CardStackZoneLayout opponentGraveyardLayout;
    HandZoneLayout handZoneLayout;

    // Menu Buttons
    FixedButtonsLayout fixedButtonsLayout;

    // Control Buttons
    ControllerLayout controllerLayout;

    // Misc var
    boolean ShadowEnable;
    Layout FocusLayout;
    WidgetSelectedCardTracker selectedCardTracker;
    float start_touch_x;
    float start_touch_y;
    Hashtable<Integer, CardWidget> ZoneToLastWidgetForSetup;
    WidgetPosition basePosition;

    // Listener
    WidgetTouchListener LowFocusListener;
    WidgetTouchListener MediumFocusListener;
    WidgetTouchListener HighFocusListener;
    WidgetTouchListener Listener;

    // UI Requests
    UIRequest MasterRequests;
    UIRequest CopyRequests;

    //Simulations
    TransientManaCard transientManaCard;
    PreSummonCard preSummonCard;
    SummonCreatureCard summonCreatureCard;
    CancelSummonOrManaAdd cancelSummonOrManaAdd;
    PreManaAdd preManaAdd;
    ManaAdd manaAdd;
    CancelManaCard cancelManaCard;
    DrawCardFromDeck drawCardFromDeck;

    //Array list for Simulations
    ArrayList<Simulate> simulationList;
    ArrayList<Simulate> simulationToBeRemoved;

    public PvPWidgetCoordinator(Maze maze) {
        this.maze = maze;

        // Misc var
        this.ShadowEnable = false;
        FocusLayout = null;
        selectedCardTracker = new WidgetSelectedCardTracker();
        ZoneToLastWidgetForSetup = new Hashtable<Integer, CardWidget>();

        // CardWidget Pool
        Pool.PoolObjectFactory<CardWidget> factory = new Pool.PoolObjectFactory<CardWidget>() {
            @Override
            public CardWidget createObject() {
                return new CardWidget();
            }
        };
        cardWidgetPool = new Pool<CardWidget>(factory, 80);

        // CardStack widget
        Graveyard = new CardStackWidget();
        Deck = new CardStackWidget();
        Opponent_Graveyard = new CardStackWidget();
        Opponent_Deck = new CardStackWidget();

        // Controller widget
        pauseButton = new RectangleButtonWidget();
        EndTurnButton = new RectangleButtonWidget();
        PlayerButton = new RectangleButtonWidget();
        OpponentButton = new RectangleButtonWidget();

        AcceptButton = new RectangleButtonWidget();
        DeclineButton = new RectangleButtonWidget();
        SummonOrCastButton = new RectangleButtonWidget();
        AddToManaButton = new RectangleButtonWidget();
        AttackButton = new RectangleButtonWidget();
        BlockButton = new RectangleButtonWidget();
        TapAbilityButton = new RectangleButtonWidget();
        ZoomButton = new RectangleButtonWidget();

        // Initialize GLObject
        Base = new XZRectangle(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), 2.0f, 2.0f, 0);

        cube = new Cube(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), AssetsAndResource.CardWidth, AssetsAndResource.CardLength * 40f, AssetsAndResource.CardHeight, true);

        glCard = new Cube(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), AssetsAndResource.CardWidth, AssetsAndResource.CardLength, AssetsAndResource.CardHeight, true);

        glRbutton = new ScreenRectangle(0.2f, 0.2f);

        // Link to its GLObject (Zones)
        Graveyard.LinkGLobject(cube, glCard);
        Graveyard.setFlip(false);
        Graveyard.ShadowEnable(ShadowEnable);
        Deck.LinkGLobject(cube, glCard);
        Deck.setFlip(true);
        Deck.ShadowEnable(ShadowEnable);
        Opponent_Graveyard.LinkGLobject(cube, glCard);
        Opponent_Graveyard.setFlip(false);
        Opponent_Graveyard.ShadowEnable(ShadowEnable);
        Opponent_Deck.LinkGLobject(cube, glCard);
        Opponent_Deck.setFlip(true);
        Opponent_Deck.ShadowEnable(ShadowEnable);

        // Link to its Logical Object (Zones)
        Graveyard.LinkLogicalObject(this.maze.getZoneList().get(Maze.graveyard).getZoneArray());
        Deck.LinkLogicalObject(this.maze.getZoneList().get(Maze.deck).getZoneArray());
        Opponent_Graveyard.LinkLogicalObject(this.maze.getZoneList().get(Maze.Opponent_graveyard).getZoneArray());
        Opponent_Deck.LinkLogicalObject(this.maze.getZoneList().get(Maze.Opponent_deck).getZoneArray());

        // Link to its GLObject (controller)
        pauseButton.LinkGLobject(glRbutton);
        EndTurnButton.LinkGLobject(glRbutton);
        PlayerButton.LinkGLobject(glRbutton);
        OpponentButton.LinkGLobject(glRbutton);

        AcceptButton.LinkGLobject(glRbutton);
        DeclineButton.LinkGLobject(glRbutton);
        SummonOrCastButton.LinkGLobject(glRbutton);
        AddToManaButton.LinkGLobject(glRbutton);
        AttackButton.LinkGLobject(glRbutton);
        BlockButton.LinkGLobject(glRbutton);
        TapAbilityButton.LinkGLobject(glRbutton);
        ZoomButton.LinkGLobject(glRbutton);

        // Link to its texture (controller)
        pauseButton.LinkLogicalObject(ControllerButton.Pause);
        EndTurnButton.LinkLogicalObject(ControllerButton.EndTurn);
        PlayerButton.LinkLogicalObject(ControllerButton.Player);
        OpponentButton.LinkLogicalObject(ControllerButton.Opponent);

        AcceptButton.LinkLogicalObject(ControllerButton.Accept);
        DeclineButton.LinkLogicalObject(ControllerButton.Decline);
        SummonOrCastButton.LinkLogicalObject(ControllerButton.SummonOrCast);
        AddToManaButton.LinkLogicalObject(ControllerButton.AddToMana);
        AttackButton.LinkLogicalObject(ControllerButton.Attack);
        BlockButton.LinkLogicalObject(ControllerButton.Block);
        TapAbilityButton.LinkLogicalObject(ControllerButton.TapAbility);
        ZoomButton.LinkLogicalObject(ControllerButton.Zoom);

        // Initialize Zone Layouts
        battleZoneLayout = new BattleZoneLayout();
        opponentBattleZoneLayout = new BattleZoneLayout();
        manaZoneLayout = new ManaZoneLayout();
        opponentManaZoneLayout = new ManaZoneLayout();
        deckLayout = new CardStackZoneLayout(this);
        opponentDeckLayout = new CardStackZoneLayout(this);
        graveyardLayout = new CardStackZoneLayout(this);
        opponentGraveyardLayout = new CardStackZoneLayout(this);
        handZoneLayout = new HandZoneLayout();

        battleZoneLayout.InitializeBattleZoneLayout(AssetsAndResource.MazeHeight/10, AssetsAndResource.MazeWidth,
                AssetsAndResource.MazeHeight/5, HeadOrientation.North, false, 4f, 4f);
        opponentBattleZoneLayout.InitializeBattleZoneLayout(-AssetsAndResource.MazeHeight/10, AssetsAndResource.MazeWidth,
                AssetsAndResource.MazeHeight/5, HeadOrientation.South, true, 4f, 4f);
        manaZoneLayout.InitializeBattleZoneLayout((3f * AssetsAndResource.MazeHeight)/10, AssetsAndResource.MazeWidth,
                AssetsAndResource.MazeHeight/5, HeadOrientation.North, false, 4f, 4f);
        opponentManaZoneLayout.InitializeBattleZoneLayout(-(3f * AssetsAndResource.MazeHeight)/10, AssetsAndResource.MazeWidth,
                AssetsAndResource.MazeHeight/5, HeadOrientation.South, true, 4f, 4f);
        deckLayout.InitializeCardStackZoneLayout(0.68f * AssetsAndResource.MazeWidth, AssetsAndResource.CardLength * 20f,
                0.15f * AssetsAndResource.MazeHeight, 30f, 0, 1f, 0f, Deck);
        opponentDeckLayout.InitializeCardStackZoneLayout(-(0.68f * AssetsAndResource.MazeWidth), AssetsAndResource.CardLength * 20f,
                -(0.15f * AssetsAndResource.MazeHeight), 210f, 0, 1f, 0f, Opponent_Deck);
        graveyardLayout.InitializeCardStackZoneLayout(0.65f * AssetsAndResource.MazeWidth, AssetsAndResource.CardLength * 20f,
                0.35f * AssetsAndResource.MazeHeight, 30f, 0, 1f, 0f, Graveyard);
        opponentGraveyardLayout.InitializeCardStackZoneLayout(-(0.65f * AssetsAndResource.MazeWidth), AssetsAndResource.CardLength * 20f,
                -(0.35f * AssetsAndResource.MazeHeight), 210f, 0, 1f, 0f, Opponent_Graveyard);
        handZoneLayout.InitializeHandZoneLayout(AssetsAndResource.MazeWidth, -0.8f, AssetsAndResource.CameraPosition.x/4,
                AssetsAndResource.CameraPosition.y/4, AssetsAndResource.CameraPosition.z/4, 4f, 4f);

        // Initialize Menu Button
        fixedButtonsLayout = new FixedButtonsLayout();
        fixedButtonsLayout.InitializeFixedButtonLayout(pauseButton, EndTurnButton, PlayerButton, OpponentButton);

        // Initialize Control Button
        controllerLayout = new ControllerLayout();
        controllerLayout.setControllerOrientation(true);

        // Added Button To the Controller Layout
        controllerLayout.AddButtonWidget(ControllerButton.Accept, AcceptButton);
        controllerLayout.AddButtonWidget(ControllerButton.Decline, DeclineButton);
        controllerLayout.AddButtonWidget(ControllerButton.SummonOrCast, SummonOrCastButton);
        controllerLayout.AddButtonWidget(ControllerButton.AddToMana, AddToManaButton);
        controllerLayout.AddButtonWidget(ControllerButton.Attack, AttackButton);
        controllerLayout.AddButtonWidget(ControllerButton.Block, BlockButton);
        controllerLayout.AddButtonWidget(ControllerButton.TapAbility, TapAbilityButton);
        controllerLayout.AddButtonWidget(ControllerButton.Zoom, ZoomButton);

        // UI Requests
        MasterRequests = new UIRequest();
        CopyRequests = new UIRequest();

        // Define Listener
        DefineListener();
        Listener = LowFocusListener;

        // Define Simulation
        DefineSimulation();

        //Array List for Simulation
        simulationList = new ArrayList<Simulate>();
        simulationToBeRemoved = new ArrayList<Simulate>();

        // Misc Flag
        zoomLevel = ZoomLevel.Button_Touched;
        FlushButtons = true;
        SelectCardMode = CardSelectMode.OFF;
        start_touch_x = 0;
        start_touch_y = 0;
        basePosition = new WidgetPosition();
    }

    private void ResetFlags() {
        zoomLevel = ZoomLevel.Button_Touched;
        FlushButtons = false;
        SelectCardMode = CardSelectMode.OFF;

        battleZoneLayout.setExpandMode(false);
        opponentBattleZoneLayout.setExpandMode(false);
        manaZoneLayout.setExpandMode(false);
        opponentManaZoneLayout.setExpandMode(false);
        graveyardLayout.setExpandMode(false);
        opponentGraveyardLayout.setExpandMode(false);
        deckLayout.setExpandMode(false);
        opponentDeckLayout.setExpandMode(false);

        manaZoneLayout.SetDraggingMode(false);
        opponentManaZoneLayout.SetDraggingMode(false);
        handZoneLayout.SetDraggingMode(false);

        controllerLayout.setControllerOrientation(true);
    }

    public void SetFlags(ZoomLevel zoomLevel, Expand[] expands, Drag[] drags, boolean flushButtons,
                         CardSelectMode selectCardMode, boolean controllerOrientation) {
        ResetFlags();

        this.zoomLevel= zoomLevel;
        FlushButtons = flushButtons;
        this.SelectCardMode = selectCardMode;
        if (this.SelectCardMode != CardSelectMode.OFF) {
            this.zoomLevel = ZoomLevel.Button;
        }

        controllerLayout.setControllerOrientation(controllerOrientation);

        for (int i = 0; i < expands.length; i++) {
            Expand expand = expands[i];

            switch (expand) {
                case Battle_Z:
                    battleZoneLayout.setExpandMode(true);
                    break;
                case Battle_OZ:
                    opponentDeckLayout.setExpandMode(true);
                    break;
                case Mana_Z:
                    manaZoneLayout.setExpandMode(true);
                    break;
                case Mana_OZ:
                    opponentManaZoneLayout.setExpandMode(true);
                    break;
                case Deck:
                    deckLayout.setExpandMode(true);
                    break;
                case Deck_O:
                    opponentDeckLayout.setExpandMode(true);
                    break;
                case Graveyard:
                    graveyardLayout.setExpandMode(true);
                    break;
                case Graveyard_O:
                    opponentGraveyardLayout.setExpandMode(true);
                    break;
                case Hand_O:
                    break;
                case Player:
                    break;
                case Opponent:
                    break;
                case Nil:
                    break;
                default:
            }
        }

        for (int i = 0; i < drags.length; i++) {
            Drag drag = drags[i];

            switch (drag) {
                case Hand:
                    handZoneLayout.SetDraggingMode(true);
                    break;
                case Mana_Z:
                    manaZoneLayout.SetDraggingMode(true);
                    break;
                case Nil:
                    break;
                default:
            }
        }
    }

    private void setControlButton(ArrayList<ControllerButton> controllerButtons) {
        if ((zoomLevel == ZoomLevel.Button_Touched || zoomLevel == ZoomLevel.Button) && selectedCardTracker.getSelectedCard() != null) {
            if (!controllerButtons.contains(ControllerButton.Zoom)) {
                controllerButtons.add(ControllerButton.Zoom);
            }
        }
        ControllerButton[] controllerButtons1 = controllerButtons.toArray(new ControllerButton[controllerButtons.size()]);
        controllerLayout.setControllerButton(controllerButtons1);
    }

    private void clearControlButton() {
        if (selectedCardTracker.getSelectedCard() != null) {
            controllerLayout.unsetControllerButton(false);
        } else {
            controllerLayout.unsetControllerButton(true);
        }
    }

    private void SetUpScenario(Object ...obj) {
        int zone = (int) obj[0];
        if (zone == Maze.battleZone) {
            ArrayList<Cards> cardList = (ArrayList<Cards>) obj[1];
            for (int i = 0; i < cardList.size(); i++) {
                Cards card = cardList.get(i);
                if (card.getWidget() != null) {
                    throw new RuntimeException("Invalid Condition");
                }
                CardWidget cardWidget = newCardWidget();
                CoupleWidgetForCard(card, cardWidget);
                battleZoneLayout.AddCardWidgetToZone(cardWidget);
            }

            if (cardList.size() > 0) {
                CardWidget cardWidget = getWidgetForCard(cardList.get(cardList.size() - 1));
                ZoneToLastWidgetForSetup.put(new Integer(Maze.battleZone), cardWidget);
            }
        } else if (zone == Maze.Opponent_battleZone) {
            ArrayList<Cards> cardList = (ArrayList<Cards>) obj[1];
            for (int i = 0; i < cardList.size(); i++) {
                Cards card = cardList.get(i);
                if (card.getWidget() != null) {
                    throw new RuntimeException("Invalid Condition");
                }
                CardWidget cardWidget = newCardWidget();
                CoupleWidgetForCard(card, cardWidget);
                opponentBattleZoneLayout.AddCardWidgetToZone(cardWidget);
            }

            if (cardList.size() > 0) {
                CardWidget cardWidget = getWidgetForCard(cardList.get(cardList.size() - 1));
                ZoneToLastWidgetForSetup.put(new Integer(Maze.Opponent_battleZone), cardWidget);
            }
        } else if (zone == Maze.manaZone) {
            ArrayList<Cards> cardList = (ArrayList<Cards>) obj[1];
            for (int i = 0; i < cardList.size(); i++) {
                Cards card = cardList.get(i);
                if (card.getWidget() != null) {
                    throw new RuntimeException("Invalid Condition");
                }
                CardWidget cardWidget = newCardWidget();
                CoupleWidgetForCard(card, cardWidget);
                manaZoneLayout.AddCardWidgetToZone(cardWidget);
            }

            if (cardList.size() > 0) {
                CardWidget cardWidget = getWidgetForCard(cardList.get(cardList.size() - 1));
                ZoneToLastWidgetForSetup.put(new Integer(Maze.manaZone), cardWidget);
            }
        } else if (zone == Maze.Opponent_manaZone) {
            ArrayList<Cards> cardList = (ArrayList<Cards>) obj[1];
            for (int i = 0; i < cardList.size(); i++) {
                Cards card = cardList.get(i);
                if (card.getWidget() != null) {
                    throw new RuntimeException("Invalid Condition");
                }
                CardWidget cardWidget = newCardWidget();
                CoupleWidgetForCard(card, cardWidget);
                opponentManaZoneLayout.AddCardWidgetToZone(cardWidget);
            }

            if (cardList.size() > 0) {
                CardWidget cardWidget = getWidgetForCard(cardList.get(cardList.size() - 1));
                ZoneToLastWidgetForSetup.put(new Integer(Maze.Opponent_manaZone), cardWidget);
            }
        } else if (zone == Maze.hand) {
            ArrayList<Cards> cardList = (ArrayList<Cards>) obj[1];
            for (int i = 0; i < cardList.size(); i++) {
                Cards card = cardList.get(i);
                if (card.getWidget() != null) {
                    throw new RuntimeException("Invalid Condition");
                }
                CardWidget cardWidget = newCardWidget();
                CoupleWidgetForCard(card, cardWidget);
                handZoneLayout.AddCardWidgetToZone(cardWidget);
            }

            if (cardList.size() > 0) {
                CardWidget cardWidget = getWidgetForCard(cardList.get(cardList.size() - 1));
                ZoneToLastWidgetForSetup.put(new Integer(Maze.hand), cardWidget);
            }
        } else if (zone == Maze.temporaryZone) {
            ZoneToLastWidgetForSetup.clear();
        }
    }

    private void Simulate(Object ...obj) {
        Simulation simulation = (Simulation) obj[0];
        switch (simulation) {
            case TransientManaCard:
                transientManaCard.Start(getWidgetForCard((Cards)obj[1]));
                if (!simulationList.contains(transientManaCard)) {
                    simulationList.add(transientManaCard);
                }
                break;
            case PreSummonCard:
                preSummonCard.Start(getWidgetForCard((Cards)obj[1]));
                if (!simulationList.contains(preSummonCard)) {
                    simulationList.add(preSummonCard);
                }
                break;
            case SummonCreatureCard:
                summonCreatureCard.Start(getWidgetForCard((Cards) obj[1]));
                if (!simulationList.contains(summonCreatureCard)) {
                    simulationList.add(summonCreatureCard);
                }
                break;
            case PreManaAdd:
                preManaAdd.Start(getWidgetForCard((Cards) obj[1]));
                if (!simulationList.contains(preManaAdd)) {
                    simulationList.add(preManaAdd);
                }
                break;
            case ManaAdd:
                manaAdd.Start(getWidgetForCard((Cards) obj[1]), (boolean) obj[2]);
                if (!simulationList.contains(manaAdd)) {
                    simulationList.add(manaAdd);
                }
                break;
            case CancelSummonOrManaAdd:
                cancelSummonOrManaAdd.Start(getWidgetForCard((Cards) obj[1]));
                if (!simulationList.contains(cancelSummonOrManaAdd)) {
                    simulationList.add(cancelSummonOrManaAdd);
                }
                break;
            case CancelManaCard:
                cancelManaCard.Start(null);
                if (!simulationList.contains(cancelManaCard)) {
                    simulationList.add(cancelManaCard);
                }
                break;
            case DrawCardFromDeck:
                drawCardFromDeck.Start((int)obj[1]);
                if (!simulationList.contains(drawCardFromDeck)) {
                    simulationList.add(drawCardFromDeck);
                }
                break;
            default:
        }
    }

    public void SendAction(Actions action, Object ...obj) {
        switch (action) {
            case AddControlButton:
                setControlButton((ArrayList<ControllerButton>)obj[0]);
                break;
            case ClearControlButton:
                clearControlButton();
                break;
            case ClearSelectedCards:
                selectedCardTracker.ClearSelectedCard();
                break;
            case AddCardToSelectedList:
                if (SelectCardMode == CardSelectMode.ON) {
                    selectedCardTracker.AddCardToSelectedList((Cards) obj[0], (boolean) obj[1]);
                }
                break;
            case RemoveCardFromSelectedList:
                if (SelectCardMode == CardSelectMode.ON) {
                    selectedCardTracker.RemoveCardFromSelectedList((Cards) obj[0]);
                }
                break;
            case ClearFocusCardList:
                selectedCardTracker.getOnFocusCards().clear();
                break;
            case AddCardToFocusCardList:
                selectedCardTracker.getOnFocusCards().add((Cards) obj[0]);
                break;
            case RemoveCardFromFocusCardList:
                selectedCardTracker.getOnFocusCards().remove((Cards) obj[0]);
                break;
            case FreezeSelectedManaCards:
                manaZoneLayout.FreezeNewCoupleSlot();
                break;
            case CleanSelectedCardIfMatch:
                if ((Cards) obj[0] == selectedCardTracker.getSelectedCard()) {
                    selectedCardTracker.setSelectedCard(null);
                    if (FlushButtons) {
                        controllerLayout.unsetControllerButton(true);
                    } else {
                        controllerLayout.removeZoomButton();
                    }
                }
                break;
            case SetUpScenario:
                SetUpScenario(obj);
                break;
            case Simulate:
                Simulate(obj);
                break;
            default:
        }
    }

    private boolean SetUpDone() {
        boolean status = true;

        Set<Integer> zones = ZoneToLastWidgetForSetup.keySet();

        for(Integer zone: zones) {
            CardWidget cardWidget = ZoneToLastWidgetForSetup.get(zone);
            if (zone == Maze.battleZone) {
                status &= !battleZoneLayout.IsWidgetInTransition(cardWidget);
            } else if (zone == Maze.Opponent_battleZone) {
                status &= !opponentBattleZoneLayout.IsWidgetInTransition(cardWidget);
            } else if (zone == Maze.manaZone) {
                status &= !manaZoneLayout.IsWidgetInTransition(cardWidget);
            } else if (zone == Maze.Opponent_manaZone) {
                status &= !opponentManaZoneLayout.IsWidgetInTransition(cardWidget);
            } else if (zone == Maze.hand) {
                status &= !handZoneLayout.IsWidgetInTransition(cardWidget);
            }

            if (!status) {
                break;
            }
        }
        return status;
    }

    private boolean SimulationStatus(Simulation simulation) {
        switch (simulation) {
            case TransientManaCard:
                return transientManaCard.IsFinish();
            case PreSummonCard:
                return preSummonCard.IsFinish();
            case SummonCreatureCard:
                return summonCreatureCard.IsFinish();
            case PreManaAdd:
                return preManaAdd.IsFinish();
            case ManaAdd:
                return manaAdd.IsFinish();
            case CancelSummonOrManaAdd:
                return cancelSummonOrManaAdd.IsFinish();
            case CancelManaCard:
                return cancelManaCard.IsFinish();
            case DrawCardFromDeck:
                return drawCardFromDeck.IsFinish();
            default:
                return true;
        }

    }

    public Object GetInfo(Query query, Object ...obj) {
        switch (query) {
            case IsCardSelected:
                return selectedCardTracker.IsSelectedCard((Cards) obj[0]);
            case IsCardSelectedPile:
                return selectedCardTracker.IsSelectedPileCard((Cards) obj[0]);
            case GetSelectedCardList:
                return selectedCardTracker.getSelectedCardsList();
            case GetSelectedCardCopyList:
                ArrayList<Cards> selectedCards = (ArrayList) selectedCardTracker.getSelectedCardsList().clone();
                return selectedCards;
            case SelectedCardCount:
                return selectedCardTracker.getSelectedCardsList().size();
            case IsSetUpDone:
                return SetUpDone();
            case IsSimulationDone:
                return SimulationStatus((Simulation)obj[0]);
            default:
                return null;
        }
    }

    public UIRequest getUIRequests() {
        CopyRequests.resetRequest();
        CopyRequests.setRequest(MasterRequests.getRequest(), MasterRequests.isDragged());
        CopyRequests.setCard(MasterRequests.getCard());
        MasterRequests.resetRequest();
        return CopyRequests;
    }

    private void setWidgetCoordinatorListener(WidgetTouchListener listener) {
        this.Listener = listener;
    }

    private void DefineListener() {
        LowFocusListener = new WidgetTouchListener() {
            @Override
            public void TouchListener(List<Input.TouchEvent> touchEvents) {
                Input input = AssetsAndResource.game.getInput();
                WidgetTouchEvent widgetTouchEvent = null;
                FocusLayout = null;

                if (input.isTouchDown(0)) {
                    GLPoint NearPoint = new GLPoint(input.getNearPoint(0).x, input.getNearPoint(0).y,
                            input.getNearPoint(0).z);
                    GLPoint FarPoint = new GLPoint(input.getFarPoint(0).x, input.getFarPoint(0).y,
                            input.getFarPoint(0).z);

                    GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                            new GLRay(NearPoint, GLGeometry.GLVectorBetween(NearPoint, FarPoint)), 0);

                    if (intersectingPoint.z >= 0) {
                        if (Math.abs(intersectingPoint.x) <= (AssetsAndResource.MazeWidth/2)) {
                            if (Math.abs(intersectingPoint.z - (AssetsAndResource.MazeHeight / 10)) <= (AssetsAndResource.MazeHeight / 10)) {
                                widgetTouchEvent = battleZoneLayout.TouchResponse(touchEvents);
                                if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                    FocusLayout = battleZoneLayout;
                                }
                            } else if (Math.abs(intersectingPoint.z - ((3f * AssetsAndResource.MazeHeight)/10)) <= (AssetsAndResource.MazeHeight / 10)) {
                                widgetTouchEvent = manaZoneLayout.TouchResponse(touchEvents);
                                if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                    FocusLayout = manaZoneLayout;
                                }
                            }

                            if ((widgetTouchEvent == null) && (input.getNormalizedX(0) < -(0.85f * AssetsAndResource.aspectRatio)) &&
                                    (input.getNormalizedY(0) < -0.85f)) {
                                widgetTouchEvent = fixedButtonsLayout.TouchResponse(touchEvents);
                            }

                            if (widgetTouchEvent == null) {
                                widgetTouchEvent = handZoneLayout.TouchResponse(touchEvents);
                                if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                    FocusLayout = handZoneLayout;
                                }
                            }
                        } else {
                            if (((input.getNormalizedX(0) < -(0.85f * AssetsAndResource.aspectRatio)) && (input.getNormalizedY(0) < -0.85f)) ||
                                    ((input.getNormalizedX(0) > (0.9f * AssetsAndResource.aspectRatio)) && (Math.abs(input.getNormalizedY(0) - 0.3f) < 0.4f))) {
                                widgetTouchEvent = fixedButtonsLayout.TouchResponse(touchEvents);
                            }
                            if (controllerLayout.getControllerOrientation() && (widgetTouchEvent == null) &&
                                    (input.getNormalizedX(0) < -(0.85f * AssetsAndResource.aspectRatio))) {
                                widgetTouchEvent = controllerLayout.TouchResponse(touchEvents);
                            }
                            if ((widgetTouchEvent == null) && (intersectingPoint.z > (0.40f * AssetsAndResource.MazeHeight))) {
                                widgetTouchEvent = handZoneLayout.TouchResponse(touchEvents);
                                if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                    FocusLayout = handZoneLayout;
                                }
                            }

                            if ((widgetTouchEvent == null) && (intersectingPoint.x > (0.50f * AssetsAndResource.MazeWidth))) {
                                if (intersectingPoint.z > (0.25f * AssetsAndResource.MazeHeight)) {
                                    widgetTouchEvent = graveyardLayout.TouchResponse(touchEvents);
                                    if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                        FocusLayout = graveyardLayout;
                                    }

                                    if (widgetTouchEvent == null) {
                                        widgetTouchEvent = deckLayout.TouchResponse(touchEvents);
                                        if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                            FocusLayout = deckLayout;
                                        }
                                    }
                                } else {
                                    widgetTouchEvent = deckLayout.TouchResponse(touchEvents);
                                    if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                        FocusLayout = deckLayout;
                                    }

                                    if (widgetTouchEvent == null) {
                                        widgetTouchEvent = graveyardLayout.TouchResponse(touchEvents);
                                        if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                            FocusLayout = graveyardLayout;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (((input.getNormalizedX(0) < -(0.8f * AssetsAndResource.aspectRatio)) && (input.getNormalizedY(0) > 0.8f)) ||
                                ((input.getNormalizedX(0) > (0.9f * AssetsAndResource.aspectRatio)) && (Math.abs(input.getNormalizedY(0) - 0.3f) < 0.4f)) ||
                                ((input.getNormalizedX(0) > (0.85f * AssetsAndResource.aspectRatio)) && (input.getNormalizedY(0) > 0.85f))) {
                            widgetTouchEvent = fixedButtonsLayout.TouchResponse(touchEvents);
                        }
                        if (!controllerLayout.getControllerOrientation() && (widgetTouchEvent == null) &&
                                (input.getNormalizedY(0) > 0.8f)) {
                            widgetTouchEvent = controllerLayout.TouchResponse(touchEvents);
                        }
                        if (controllerLayout.getControllerOrientation() && (widgetTouchEvent == null) &&
                                (input.getNormalizedX(0) < -(0.85f * AssetsAndResource.aspectRatio))) {
                            widgetTouchEvent = controllerLayout.TouchResponse(touchEvents);
                        }

                        if (widgetTouchEvent == null) {
                            if (Math.abs(intersectingPoint.x) <= (AssetsAndResource.MazeWidth / 2)) {
                                if (Math.abs(intersectingPoint.z + (AssetsAndResource.MazeHeight / 10)) <= (AssetsAndResource.MazeHeight / 10)) {
                                    widgetTouchEvent = opponentBattleZoneLayout.TouchResponse(touchEvents);
                                    if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                        FocusLayout = opponentBattleZoneLayout;
                                    }
                                } else if (Math.abs(intersectingPoint.z + ((3f * AssetsAndResource.MazeHeight) / 10)) <= (AssetsAndResource.MazeHeight / 10)) {
                                    widgetTouchEvent = opponentManaZoneLayout.TouchResponse(touchEvents);
                                    if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                        FocusLayout = opponentManaZoneLayout;
                                    }
                                }
                            } else {
                                if (intersectingPoint.x < -(0.5f * AssetsAndResource.MazeWidth)) {
                                    if (intersectingPoint.z > -(0.25f * AssetsAndResource.MazeHeight)) {
                                        widgetTouchEvent = opponentGraveyardLayout.TouchResponse(touchEvents);
                                        if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                            FocusLayout = opponentGraveyardLayout;
                                        }

                                        if (widgetTouchEvent == null) {
                                            widgetTouchEvent = opponentDeckLayout.TouchResponse(touchEvents);
                                            if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                FocusLayout = opponentDeckLayout;
                                            }
                                        }
                                    } else {
                                        widgetTouchEvent = opponentDeckLayout.TouchResponse(touchEvents);
                                        if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                            FocusLayout = opponentDeckLayout;
                                        }

                                        if (widgetTouchEvent == null) {
                                            widgetTouchEvent = opponentGraveyardLayout.TouchResponse(touchEvents);
                                            if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                FocusLayout = opponentGraveyardLayout;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Input.TouchEvent event = null;
                    for (int j = 0; j < touchEvents.size(); j++) {
                        event = touchEvents.get(j);
                        if (event.type == Input.TouchEvent.TOUCH_UP) {
                            GLPoint NearPoint = new GLPoint(event.nearPoint[0].x, event.nearPoint[0].y,
                                    event.nearPoint[0].z);
                            GLPoint FarPoint = new GLPoint(event.farPoint[0].x, event.farPoint[0].y,
                                    event.farPoint[0].z);

                            GLPoint intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                                    new GLRay(NearPoint, GLGeometry.GLVectorBetween(NearPoint, FarPoint)), 0);

                            if (intersectingPoint.z >= 0) {
                                if (Math.abs(intersectingPoint.x) <= (AssetsAndResource.MazeWidth/2)) {
                                    if (Math.abs(intersectingPoint.z - (AssetsAndResource.MazeHeight / 10)) <= (AssetsAndResource.MazeHeight / 10)) {
                                        widgetTouchEvent = battleZoneLayout.TouchResponse(touchEvents);
                                        if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                            FocusLayout = battleZoneLayout;
                                        }
                                    } else if (Math.abs(intersectingPoint.z - ((3f * AssetsAndResource.MazeHeight)/10)) <= (AssetsAndResource.MazeHeight / 10)) {
                                        widgetTouchEvent = manaZoneLayout.TouchResponse(touchEvents);
                                        if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                            FocusLayout = manaZoneLayout;
                                        }
                                    }

                                    if ((widgetTouchEvent == null) && (event.normalizedX < -(0.85f * AssetsAndResource.aspectRatio)) &&
                                            (event.normalizedY < -0.85f)) {
                                        widgetTouchEvent = fixedButtonsLayout.TouchResponse(touchEvents);
                                    }
                                    if (widgetTouchEvent == null) {
                                        widgetTouchEvent = handZoneLayout.TouchResponse(touchEvents);
                                        if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                            FocusLayout = handZoneLayout;
                                        }
                                    }
                                } else {
                                    if (((event.normalizedX < -(0.85f * AssetsAndResource.aspectRatio)) && (event.normalizedY < -0.85f)) ||
                                            ((event.normalizedX > (0.9f * AssetsAndResource.aspectRatio)) && (Math.abs(event.normalizedY - 0.3f) < 0.4f))) {
                                        widgetTouchEvent = fixedButtonsLayout.TouchResponse(touchEvents);
                                    }
                                    if (controllerLayout.getControllerOrientation() && (widgetTouchEvent == null) &&
                                            (event.normalizedX < -(0.85f * AssetsAndResource.aspectRatio))) {
                                        widgetTouchEvent = controllerLayout.TouchResponse(touchEvents);
                                    }
                                    if ((widgetTouchEvent == null) && (intersectingPoint.z > (0.40f * AssetsAndResource.MazeHeight))) {
                                        widgetTouchEvent = handZoneLayout.TouchResponse(touchEvents);
                                        if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                            FocusLayout = handZoneLayout;
                                        }
                                    }

                                    if ((widgetTouchEvent == null) && (intersectingPoint.x > (0.5f * AssetsAndResource.MazeWidth))) {
                                        if (intersectingPoint.z > (0.25f * AssetsAndResource.MazeHeight)) {
                                            widgetTouchEvent = graveyardLayout.TouchResponse(touchEvents);
                                            if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                FocusLayout = graveyardLayout;
                                            }

                                            if (widgetTouchEvent == null) {
                                                widgetTouchEvent = deckLayout.TouchResponse(touchEvents);
                                                if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                    FocusLayout = deckLayout;
                                                }
                                            }
                                        } else {
                                            widgetTouchEvent = deckLayout.TouchResponse(touchEvents);
                                            if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                FocusLayout = deckLayout;
                                            }

                                            if (widgetTouchEvent == null) {
                                                widgetTouchEvent = graveyardLayout.TouchResponse(touchEvents);
                                                if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                    FocusLayout = graveyardLayout;
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (((event.normalizedX < -(0.8f * AssetsAndResource.aspectRatio)) && (event.normalizedY > 0.8f)) ||
                                        ((event.normalizedX > (0.9f * AssetsAndResource.aspectRatio)) && (Math.abs(event.normalizedY - 0.3f) < 0.4f)) ||
                                        ((event.normalizedX > (0.85f * AssetsAndResource.aspectRatio)) && (event.normalizedY > 0.85f))) {
                                    widgetTouchEvent = fixedButtonsLayout.TouchResponse(touchEvents);
                                }
                                if (!controllerLayout.getControllerOrientation() && (widgetTouchEvent == null) &&
                                        (event.normalizedY > 0.8f)) {
                                    widgetTouchEvent = controllerLayout.TouchResponse(touchEvents);
                                }
                                if (controllerLayout.getControllerOrientation() && (widgetTouchEvent == null) &&
                                        (event.normalizedX < -(0.85f * AssetsAndResource.aspectRatio))) {
                                    widgetTouchEvent = controllerLayout.TouchResponse(touchEvents);
                                }

                                if (widgetTouchEvent == null) {
                                    if (Math.abs(intersectingPoint.x) <= (AssetsAndResource.MazeWidth / 2)) {
                                        if (Math.abs(intersectingPoint.z + (AssetsAndResource.MazeHeight / 10)) <= (AssetsAndResource.MazeHeight / 10)) {
                                            widgetTouchEvent = opponentBattleZoneLayout.TouchResponse(touchEvents);
                                            if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                FocusLayout = opponentBattleZoneLayout;
                                            }
                                        } else if (Math.abs(intersectingPoint.z + ((3f * AssetsAndResource.MazeHeight) / 10)) <= (AssetsAndResource.MazeHeight / 10)) {
                                            widgetTouchEvent = opponentManaZoneLayout.TouchResponse(touchEvents);
                                            if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                FocusLayout = opponentManaZoneLayout;
                                            }
                                        }
                                    } else {
                                        if (intersectingPoint.x < -(0.5f * AssetsAndResource.MazeWidth)) {
                                            if (intersectingPoint.z > -(0.25f * AssetsAndResource.MazeHeight)) {
                                                widgetTouchEvent = opponentGraveyardLayout.TouchResponse(touchEvents);
                                                if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                    FocusLayout = opponentGraveyardLayout;
                                                }

                                                if (widgetTouchEvent == null) {
                                                    widgetTouchEvent = opponentDeckLayout.TouchResponse(touchEvents);
                                                    if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                        FocusLayout = opponentDeckLayout;
                                                    }
                                                }
                                            } else {
                                                widgetTouchEvent = opponentDeckLayout.TouchResponse(touchEvents);
                                                if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                    FocusLayout = opponentDeckLayout;
                                                }

                                                if (widgetTouchEvent == null) {
                                                    widgetTouchEvent = opponentGraveyardLayout.TouchResponse(touchEvents);
                                                    if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                        FocusLayout = opponentGraveyardLayout;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                }

                MasterRequests.resetRequest();

                if (widgetTouchEvent == null) {
                    if (!input.isTouchDown(0)) {
                        boolean touchUp = false;
                        Input.TouchEvent event = null;
                        for (int j = 0; j < touchEvents.size(); j++) {
                            event = touchEvents.get(j);
                            if (event.type == Input.TouchEvent.TOUCH_UP) {
                                touchUp = true;
                                break;
                            }
                        }
                        if (touchUp) {
                            selectedCardTracker.setSelectedCard(null);
                            if (FlushButtons) {
                                controllerLayout.unsetControllerButton(true);
                            } else {
                                controllerLayout.removeZoomButton();
                            }
                        }
                    }
                    return;
                }

                if (!widgetTouchEvent.isTouched) {
                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                    throw new RuntimeException("Invalid condition");
                }

                if (!widgetTouchEvent.isTouchedDown &&
                        (widgetTouchEvent.isFocus == WidgetTouchFocusLevel.Low) && (widgetTouchEvent.object instanceof Cards)) {
                    if (SelectCardMode == CardSelectMode.OFF) {
                        if (((zoomLevel == ZoomLevel.Button_Touched) || (zoomLevel == ZoomLevel.Touched))
                                && (selectedCardTracker.getSelectedCard() != null) &&
                                (selectedCardTracker.getSelectedCard() == widgetTouchEvent.object)) {
                            // Need to implement zoom
                        } else {
                            selectedCardTracker.setSelectedCard((Cards) widgetTouchEvent.object);
                            controllerLayout.addZoomButton();
                            MasterRequests.setRequest(Requests.CardSelected, false);
                            MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                        }
                    } else {
                        if (widgetTouchEvent.wasUnderTheStack == false) {
                            selectedCardTracker.setSelectedCard((Cards) widgetTouchEvent.object);
                            controllerLayout.addZoomButton();
                            MasterRequests.setRequest(Requests.CardSelected, false);
                            MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                        }
                    }

                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                    return;
                }

                if (!widgetTouchEvent.isTouchedDown &&
                        (widgetTouchEvent.isFocus == WidgetTouchFocusLevel.Low) && (widgetTouchEvent.object instanceof ArrayList)) {
                    selectedCardTracker.setSelectedCard(null);
                    if (FlushButtons) {
                        controllerLayout.unsetControllerButton(true);
                    } else {
                        controllerLayout.removeZoomButton();
                    }
                    if (((ArrayList)widgetTouchEvent.object).size() > 0) {
                        Cards card = (Cards)((ArrayList)widgetTouchEvent.object).get(0);
                        int zone = card.GridPosition().getZone();

                        if (zone == Maze.deck) {
                            MasterRequests.setRequest(Requests.DeckStack, false);
                        } else if (zone == Maze.Opponent_deck) {
                            MasterRequests.setRequest(Requests.OppDeckStack, false);
                        } else if (zone == Maze.graveyard) {
                            MasterRequests.setRequest(Requests.Graveyard, false);
                        } else if (zone == Maze.Opponent_graveyard) {
                            MasterRequests.setRequest(Requests.OppGraveyard, false);
                        }
                    }
                }

                if (widgetTouchEvent.isFocus == WidgetTouchFocusLevel.Medium) {
                    selectedCardTracker.setSelectedCard(null);
                    if (FlushButtons) {
                        controllerLayout.unsetControllerButton(true);
                    } else {
                        controllerLayout.removeZoomButton();
                    }
                    setWidgetCoordinatorListener(MediumFocusListener);
                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                    return;
                }

                if (widgetTouchEvent.isFocus == WidgetTouchFocusLevel.High) {
                    if (!(widgetTouchEvent.object instanceof Cards)) {
                        throw new RuntimeException("Invalid Condition");
                    }
                    selectedCardTracker.setSelectedCard((Cards)widgetTouchEvent.object);
                    controllerLayout.addZoomButton();
                    start_touch_x = input.getNormalizedX(0);
                    start_touch_y = input.getNormalizedY(0);
                    setWidgetCoordinatorListener(HighFocusListener);
                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                    return;
                }

                if (!widgetTouchEvent.isTouchedDown && widgetTouchEvent.object == null) {
                    selectedCardTracker.setSelectedCard(null);
                    if (FlushButtons) {
                        controllerLayout.unsetControllerButton(true);
                    } else {
                        controllerLayout.removeZoomButton();
                    }
                }

                if (!widgetTouchEvent.isTouchedDown && (widgetTouchEvent.object instanceof ControllerButton)) {
                    boolean status = false;
                    switch ((ControllerButton)widgetTouchEvent.object) {
                        case Attack:
                            if (selectedCardTracker.getSelectedCard() != null) {
                                MasterRequests.setRequest(Requests.Attack, false);
                                status = true;
                            }
                            break;
                        case SummonOrCast:
                            if (selectedCardTracker.getSelectedCard() != null) {
                                MasterRequests.setRequest(Requests.SummonOrCast, false);
                                status = true;
                            }
                            break;
                        case Block:
                            if (selectedCardTracker.getSelectedCard() != null) {
                                MasterRequests.setRequest(Requests.Block, false);
                                status = true;
                            }
                            break;
                        case AddToMana:
                            if (selectedCardTracker.getSelectedCard() != null) {
                                MasterRequests.setRequest(Requests.AddToMana, false);
                                status = true;
                            }
                            break;
                        case TapAbility:
                            if (selectedCardTracker.getSelectedCard() != null) {
                                MasterRequests.setRequest(Requests.TapAbility, false);
                                status = true;
                            }
                            break;
                        case Accept:
                            MasterRequests.setRequest(Requests.Accept, false);
                            selectedCardTracker.setSelectedCard(null);
                            controllerLayout.removeZoomButton();
                            break;
                        case Decline:
                            MasterRequests.setRequest(Requests.Decline, false);
                            selectedCardTracker.setSelectedCard(null);
                            controllerLayout.removeZoomButton();
                            break;
                        case Zoom:
                            //Implement zoom
                            break;
                        case Pause:
                            //Implement pause
                            break;
                        case EndTurn:
                            MasterRequests.setRequest(Requests.EndTurn, false);
                            selectedCardTracker.setSelectedCard(null);
                            controllerLayout.removeZoomButton();
                            break;
                        case Player:
                            MasterRequests.setRequest(Requests.PlayerOrShield, false);
                            selectedCardTracker.setSelectedCard(null);
                            controllerLayout.removeZoomButton();
                            break;
                        case Opponent:
                            MasterRequests.setRequest(Requests.Opponent_PlayerOrShield, false);
                            selectedCardTracker.setSelectedCard(null);
                            controllerLayout.removeZoomButton();
                            break;
                        default:
                    }

                    if (status) {
                        MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                    }
                }
                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
            }
        };

        MediumFocusListener = new WidgetTouchListener() {
            @Override
            public void TouchListener(List<Input.TouchEvent> touchEvents) {
                Input input = AssetsAndResource.game.getInput();
                WidgetTouchEvent widgetTouchEvent = null;

                if (input.isTouchDown(0)) {
                    if ((input.getNormalizedX(0) < -(0.8f * AssetsAndResource.aspectRatio)) && (input.getNormalizedY(0) > 0.8f)) {
                        widgetTouchEvent = fixedButtonsLayout.TouchResponse(touchEvents);
                    }
                    if ((widgetTouchEvent == null) && (input.getNormalizedY(0) > 0.8f)) {
                        widgetTouchEvent = controllerLayout.TouchResponse(touchEvents);
                    }
                    if (widgetTouchEvent == null) {
                        widgetTouchEvent = FocusLayout.TouchResponse(touchEvents);
                    }
                } else {
                    Input.TouchEvent event = null;
                    for (int j = 0; j < touchEvents.size(); j++) {
                        event = touchEvents.get(j);
                        if (event.type == Input.TouchEvent.TOUCH_UP) {
                            if ((event.normalizedX < -(0.8f * AssetsAndResource.aspectRatio)) && (event.normalizedY > 0.8f)) {
                                widgetTouchEvent = fixedButtonsLayout.TouchResponse(touchEvents);
                            }
                            if ((widgetTouchEvent == null) && (event.normalizedY > 0.8f)) {
                                widgetTouchEvent = controllerLayout.TouchResponse(touchEvents);
                            }
                            if (widgetTouchEvent == null) {
                                widgetTouchEvent = FocusLayout.TouchResponse(touchEvents);
                            }
                            break;
                        }
                    }
                }

                MasterRequests.resetRequest();

                if (widgetTouchEvent == null) {
                    if (!input.isTouchDown(0)) {
                        boolean touchUp = false;
                        Input.TouchEvent event = null;
                        for (int j = 0; j < touchEvents.size(); j++) {
                            event = touchEvents.get(j);
                            if (event.type == Input.TouchEvent.TOUCH_UP) {
                                touchUp = true;
                                break;
                            }
                        }
                        if (touchUp) {
                            selectedCardTracker.setSelectedCard(null);
                            if (FlushButtons) {
                                controllerLayout.unsetControllerButton(true);
                            } else {
                                controllerLayout.removeZoomButton();
                            }
                        }
                    }
                    return;
                }

                if (widgetTouchEvent.isFocus == WidgetTouchFocusLevel.Low) {
                    selectedCardTracker.setSelectedCard(null);
                    if (FlushButtons) {
                        controllerLayout.unsetControllerButton(true);
                    } else {
                        controllerLayout.removeZoomButton();
                    }
                    FocusLayout = null;
                    setWidgetCoordinatorListener(LowFocusListener);
                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                    return;
                }

                if (widgetTouchEvent.isTouched && !widgetTouchEvent.isTouchedDown &&
                        (widgetTouchEvent.isFocus == WidgetTouchFocusLevel.Medium) && (widgetTouchEvent.object instanceof Cards)) {
                    if (SelectCardMode == CardSelectMode.OFF) {
                        if (((zoomLevel == ZoomLevel.Button_Touched) || (zoomLevel == ZoomLevel.Touched))
                                && (selectedCardTracker.getSelectedCard() != null) &&
                                (selectedCardTracker.getSelectedCard() == widgetTouchEvent.object)) {
                            // Need to implement zoom
                        } else {
                            selectedCardTracker.setSelectedCard((Cards) widgetTouchEvent.object);
                            controllerLayout.addZoomButton();
                            MasterRequests.setRequest(Requests.CardSelected, false);
                            MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                        }
                    } else {
                        if (widgetTouchEvent.wasUnderTheStack == false) {
                            selectedCardTracker.setSelectedCard((Cards) widgetTouchEvent.object);
                            controllerLayout.addZoomButton();
                            if (!selectedCardTracker.IsSelectedPileCard((Cards) widgetTouchEvent.object)) {
                                MasterRequests.setRequest(Requests.CardSelected, false);
                                MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                            }
                        }
                    }

                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                    return;
                }

                if (widgetTouchEvent.isFocus == WidgetTouchFocusLevel.High) {
                    if (!(widgetTouchEvent.object instanceof Cards)) {
                        throw new RuntimeException("Invalid Condition");
                    }
                    selectedCardTracker.setSelectedCard((Cards)widgetTouchEvent.object);
                    controllerLayout.addZoomButton();
                    start_touch_x = input.getNormalizedX(0);
                    start_touch_y = input.getNormalizedY(0);
                    setWidgetCoordinatorListener(HighFocusListener);
                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                    return;
                }

                if (widgetTouchEvent.isTouched && !widgetTouchEvent.isTouchedDown && widgetTouchEvent.object == null) {
                    selectedCardTracker.setSelectedCard(null);
                    if (FlushButtons) {
                        controllerLayout.unsetControllerButton(true);
                    } else {
                        controllerLayout.removeZoomButton();
                    }
                }

                if (widgetTouchEvent.isTouched && !widgetTouchEvent.isTouchedDown && (widgetTouchEvent.object instanceof ControllerButton)) {
                    switch ((ControllerButton) widgetTouchEvent.object) {
                        case Accept:
                            MasterRequests.setRequest(Requests.Accept, false);
                            selectedCardTracker.setSelectedCard(null);
                            controllerLayout.removeZoomButton();
                            break;
                        case Decline:
                            MasterRequests.setRequest(Requests.Decline, false);
                            selectedCardTracker.setSelectedCard(null);
                            controllerLayout.removeZoomButton();
                            break;
                        case Zoom:
                            //Implement zoom
                            break;
                        case Pause:
                            //Implement Pause
                            break;
                        default:
                    }
                }
                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
            }
        };

        HighFocusListener = new WidgetTouchListener() {
            @Override
            public void TouchListener(List<Input.TouchEvent> touchEvents) {
                MasterRequests.resetRequest();
                WidgetTouchEvent widgetTouchEvent = FocusLayout.TouchResponse(touchEvents);

                if (widgetTouchEvent == null) {
                    throw new RuntimeException("Invalid Condition");
                }
                if (widgetTouchEvent.object == null) {
                    throw new RuntimeException("Invalid Condition");
                }
                if (widgetTouchEvent.object != selectedCardTracker.getSelectedCard()) {
                    throw new RuntimeException("Invalid Condition");
                }
                if (widgetTouchEvent.isFocus == WidgetTouchFocusLevel.High) {
                    if (!widgetTouchEvent.isTouched || !widgetTouchEvent.isTouchedDown) {
                        throw new RuntimeException("Invalid Condition");
                    }
                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                    return;
                }

                if (FocusLayout == handZoneLayout) {
                    if (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                        throw new RuntimeException("Invalid Condition");
                    }
                    CardWidget widget = getWidgetForCard(selectedCardTracker.getSelectedCard());
                    if ((widget.getPosition().rotaion.y != 0) && (widget.getPosition().rotaion.x == 0) &&
                            (widget.getPosition().rotaion.z == 0)) {
                        if (Math.abs(widget.getPosition().Centerposition.z - ((3f * AssetsAndResource.MazeHeight)/10)) < AssetsAndResource.MazeHeight / 5) {
                            MasterRequests.setRequest(Requests.AddToMana, true);
                            MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                        } else if (Math.abs(widget.getPosition().Centerposition.z - (AssetsAndResource.MazeHeight/10)) < AssetsAndResource.MazeHeight / 5) {
                            MasterRequests.setRequest(Requests.SummonOrCast, false);
                            MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                        }
                    } else {
                        if (SelectCardMode == CardSelectMode.OFF) {
                            selectedCardTracker.setSelectedCard((Cards) widgetTouchEvent.object);
                            controllerLayout.addZoomButton();
                            MasterRequests.setRequest(Requests.CardSelected, false);
                            MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                        } else {
                            if (widgetTouchEvent.wasUnderTheStack == false) {
                                selectedCardTracker.setSelectedCard((Cards) widgetTouchEvent.object);
                                controllerLayout.addZoomButton();
                                MasterRequests.setRequest(Requests.CardSelected, false);
                                MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                            }
                        }
                    }
                    FocusLayout = null;
                    setWidgetCoordinatorListener(LowFocusListener);
                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                    return;
                } else if (FocusLayout == manaZoneLayout) {
                    Input input = AssetsAndResource.game.getInput();
                    boolean moved = false;
                    if ((Math.abs(input.getNormalizedX(0) - start_touch_x) > AssetsAndResource.CardWidth/2) ||
                            (Math.abs(input.getNormalizedY(0) - start_touch_y) > AssetsAndResource.CardHeight/2)) {
                        moved = true;
                    }
                    if (widgetTouchEvent.isFocus == WidgetTouchFocusLevel.Medium) {
                        if (!selectedCardTracker.IsSelectedPileCard((Cards) widgetTouchEvent.object)) {
                            throw new RuntimeException("Invalid Condition");
                        }
                        if (moved) {
                            MasterRequests.setRequest(Requests.CardSelected, true);
                            MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                            selectedCardTracker.setSelectedCard(null);
                            controllerLayout.removeZoomButton();
                        }
                        setWidgetCoordinatorListener(MediumFocusListener);
                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                        return;
                    }
                    if (widgetTouchEvent.isFocus == WidgetTouchFocusLevel.Low) {
                        if (!selectedCardTracker.IsSelectedCard((Cards) widgetTouchEvent.object) ||
                                selectedCardTracker.IsSelectedPileCard((Cards) widgetTouchEvent.object)) {
                            if (moved) {
                                MasterRequests.setRequest(Requests.CardSelected, true);
                                MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                            } else if (!selectedCardTracker.IsSelectedPileCard((Cards) widgetTouchEvent.object)){
                                if (SelectCardMode == CardSelectMode.OFF) {
                                    selectedCardTracker.setSelectedCard((Cards) widgetTouchEvent.object);
                                    controllerLayout.addZoomButton();
                                    MasterRequests.setRequest(Requests.CardSelected, false);
                                    MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                                } else {
                                    if (widgetTouchEvent.wasUnderTheStack == false) {
                                        selectedCardTracker.setSelectedCard((Cards) widgetTouchEvent.object);
                                        controllerLayout.addZoomButton();
                                        MasterRequests.setRequest(Requests.CardSelected, false);
                                        MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                                    }
                                }
                            }
                        } else {
                            if (moved) {
                                if (((Cards) widgetTouchEvent.object).GridPosition().getZone() != Maze.manaZone) {
                                    throw new RuntimeException("Invalid Condition");
                                }
                                transientManaCard.Start(getWidgetForCard((Cards) widgetTouchEvent.object));
                                if (!simulationList.contains(transientManaCard)) {
                                    simulationList.add(transientManaCard);
                                }
                                selectedCardTracker.AddCardToSelectedList((Cards) widgetTouchEvent.object, true);
                            } else {
                                if (SelectCardMode == CardSelectMode.OFF) {
                                    selectedCardTracker.setSelectedCard((Cards) widgetTouchEvent.object);
                                    controllerLayout.addZoomButton();
                                    MasterRequests.setRequest(Requests.CardSelected, false);
                                    MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                                } else {
                                    if (widgetTouchEvent.wasUnderTheStack == false) {
                                        selectedCardTracker.setSelectedCard((Cards) widgetTouchEvent.object);
                                        controllerLayout.addZoomButton();
                                        MasterRequests.setRequest(Requests.CardSelected, false);
                                        MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                                    }
                                }
                            }
                        }
                        FocusLayout = null;
                        setWidgetCoordinatorListener(LowFocusListener);
                        AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                        return;
                    }
                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                } else {
                    throw new RuntimeException("Invalid Condition");
                }
            }
        };
    }

    private void DefineSimulation() {
        transientManaCard = new TransientManaCard(manaZoneLayout);
        preSummonCard = new PreSummonCard(handZoneLayout);
        summonCreatureCard = new SummonCreatureCard(handZoneLayout, battleZoneLayout);
        cancelSummonOrManaAdd = new CancelSummonOrManaAdd(handZoneLayout);
        preManaAdd = new PreManaAdd(handZoneLayout);
        manaAdd = new ManaAdd(handZoneLayout, manaZoneLayout);
        cancelManaCard = new CancelManaCard(manaZoneLayout);
        drawCardFromDeck = new DrawCardFromDeck(handZoneLayout, deckLayout);
    }

    public CardWidget getWidgetForCard(Cards card) {
        if (card.getWidget() != null && card != card.getWidget().getLogicalObject()) {
            throw new RuntimeException("card and CardWidget inconsistency");
        }

        return card.getWidget();
    }

    public CardWidget DecoupleWidgetFormCard(Cards card) {
        if (card != card.getWidget().getLogicalObject()) {
            throw new RuntimeException("card and CardWidget inconsistency");
        }

        CardWidget widget = card.getWidget();
        card.unsetWidget();
        widget.LinkLogicalObject(null);
        return widget;
    }

    public void CoupleWidgetForCard(Cards card, CardWidget cardWidget) {
        card.setWidget(cardWidget);
        cardWidget.LinkLogicalObject(card);
    }

    public void freeCardWidget(CardWidget cardWidget) {
        cardWidgetPool.free(cardWidget);
    }

    public CardWidget newCardWidget() {
        CardWidget cardWidget = cardWidgetPool.newObject();
        cardWidget.LinkGLobject(glCard);
        cardWidget.ShadowEnable(ShadowEnable);

        return cardWidget;
    }

    public void PvPWidgetsTouchListener() {
        List<Input.TouchEvent> touchEvents = AssetsAndResource.game.getInput().getTouchEvents();

        Listener.TouchListener(touchEvents);
    }

    private void SimulationUpdate() {
        simulationToBeRemoved.clear();
        for (int i = 0; i < simulationList.size(); i++) {
            Simulate simulate = simulationList.get(i);
            simulate.update();
            if (simulate.IsFinish()) {
                simulationToBeRemoved.add(simulate);
            }
        }

        for (int i = 0; i < simulationToBeRemoved.size(); i++) {
            Simulate simulate = simulationToBeRemoved.get(i);
            simulationList.remove(simulate);
        }
    }

    public void update(float deltaTime, float totalTime) {
        battleZoneLayout.update(deltaTime, totalTime);
        opponentBattleZoneLayout.update(deltaTime, totalTime);
        manaZoneLayout.update(deltaTime, totalTime);
        opponentManaZoneLayout.update(deltaTime, totalTime);
        deckLayout.update(deltaTime, totalTime);
        opponentDeckLayout.update(deltaTime, totalTime);
        graveyardLayout.update(deltaTime, totalTime);
        opponentGraveyardLayout.update(deltaTime, totalTime);
        handZoneLayout.update(deltaTime, totalTime);

        controllerLayout.update(deltaTime, totalTime);

        // Must be last
        SimulationUpdate();
    }

    public void draw() {
        AssetsAndResource.ResetCardUsageCount();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, AssetsAndResource.game.getframeBufferWidth(), AssetsAndResource.game.getframeBufferHeight());
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        MatrixHelper.setTranslateRotateScale(basePosition);
        DrawObjectHelper.drawOneRectangle(Base, AssetsAndResource.Base, ShadowEnable);
        battleZoneLayout.draw();
        opponentBattleZoneLayout.draw();
        manaZoneLayout.draw();
        opponentManaZoneLayout.draw();
        deckLayout.draw();
        opponentDeckLayout.draw();
        graveyardLayout.draw();
        opponentGraveyardLayout.draw();
        handZoneLayout.draw();

        fixedButtonsLayout.draw();
        controllerLayout.draw();
    }
}
