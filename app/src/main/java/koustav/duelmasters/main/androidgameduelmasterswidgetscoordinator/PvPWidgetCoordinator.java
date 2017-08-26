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
import koustav.duelmasters.main.androidgameduelmasterwidgetsimulation.LocationLayout;
import koustav.duelmasters.main.androidgameduelmasterswidgetcoordinationtools.UIRequest;
import koustav.duelmasters.main.androidgamenodeviewframework.androidgamenodeviewframeworkimpl.ViewNodePosition;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetSelectedCardTracker;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchFocusLevel;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.ControllerButton;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.Layout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.LayoutManager;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.WidgetTouchListener;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardStackWidget;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.RectangleButtonWidget;
import koustav.duelmasters.main.androidgameduelmasterwidgetsimulation.SimulationManager;
import koustav.duelmasters.main.androidgameduelmasterwidgetsimulation.SimulationType;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglobjectmodels.Cube;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglobjectmodels.FullScreenRectangle;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglobjectmodels.ScreenRectangle;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglobjectmodels.UniformXZRectangle;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglobjectmodels.XZRectangle;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.DrawObjectHelper;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLGeometry.*;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.GLMaterial;
import koustav.duelmasters.main.androidgameopengl.androidgameopenglutil.MatrixHelper;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Input;
import koustav.duelmasters.main.androidgamesframework.androidgamesframeworkinterface.Pool;

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
    FullScreenRectangle Screen;
    XZRectangle Base;
    UniformXZRectangle Base2;
    Cube cube;
    Cube glCard;
    ScreenRectangle glRbutton;
    ScreenRectangle ZoomedCard;
    Cube glCurrentSelect;
    Cube glSelectedCards;

    // Zone Layouts
    LayoutManager layoutManager;


    // Misc var
    boolean ShadowEnable;
    Layout FocusLayout;
    WidgetSelectedCardTracker selectedCardTracker;
    float start_touch_x;
    float start_touch_y;
    boolean ZoomMode;
    Hashtable<Integer, CardWidget> ZoneToLastWidgetForSetup;
    ViewNodePosition basePosition;
    ViewNodePosition ScreenCenterPosition;
    Cards PreviousSelectedCard;
    float[] Color;

    // Listener
    WidgetTouchListener LowFocusListener;
    WidgetTouchListener MediumFocusListener;
    WidgetTouchListener HighFocusListener;
    WidgetTouchListener ZoomListener;
    WidgetTouchListener PreviousListener;
    WidgetTouchListener Listener;

    // Simulation
    SimulationManager simulationManager;

    // UI Requests
    UIRequest MasterRequests;
    UIRequest CopyRequests;

    public PvPWidgetCoordinator(Maze maze) {
        this.maze = maze;

        // Misc var
        this.ShadowEnable = true;
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
        Screen = new FullScreenRectangle();

        Base = new XZRectangle(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), 2.0f, 2.0f, 0);

        Base2 = new UniformXZRectangle(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), 2.0f, 2.0f, 0);

        cube = new Cube(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), AssetsAndResource.CardWidth, AssetsAndResource.CardLength * 40f, AssetsAndResource.CardHeight, true);

        glCard = new Cube(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), AssetsAndResource.CardWidth, AssetsAndResource.CardLength, AssetsAndResource.CardHeight, true);

        glRbutton = new ScreenRectangle(0.2f, 0.2f);

        ZoomedCard = new ScreenRectangle(AssetsAndResource.ZoomCardWidth, AssetsAndResource.ZoomCardHeight);

        glCurrentSelect = new Cube(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), AssetsAndResource.CardWidth * 1.05f, AssetsAndResource.CardLength * 1.01f, AssetsAndResource.CardHeight * 1.05f, true);

        glSelectedCards = new Cube(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), AssetsAndResource.CardWidth * 1.1f, AssetsAndResource.CardLength * 1.01f, AssetsAndResource.CardHeight * 1.1f, true);

        // Link to its GLObject (Zones)
        Graveyard.LinkGLobject(cube, glCard, glCurrentSelect, glSelectedCards);
        Graveyard.setFlip(false);
        Graveyard.ShadowEnable(ShadowEnable);
        Deck.LinkGLobject(cube, glCard, glCurrentSelect, glSelectedCards);
        Deck.setFlip(true);
        Deck.ShadowEnable(ShadowEnable);
        Opponent_Graveyard.LinkGLobject(cube, glCard, glCurrentSelect, glSelectedCards);
        Opponent_Graveyard.setFlip(false);
        Opponent_Graveyard.ShadowEnable(ShadowEnable);
        Opponent_Deck.LinkGLobject(cube, glCard, glCurrentSelect, glSelectedCards);
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
        layoutManager = new LayoutManager(this, Deck, Opponent_Deck, Graveyard, Opponent_Graveyard,
                pauseButton, EndTurnButton, PlayerButton, OpponentButton, AcceptButton, DeclineButton,
                SummonOrCastButton, AddToManaButton, AttackButton, BlockButton, TapAbilityButton,
                ZoomButton);

        // UI Requests
        MasterRequests = new UIRequest();
        CopyRequests = new UIRequest();

        // Define Listener
        DefineListener();
        Listener = LowFocusListener;

        // Define Simulation
        simulationManager = new SimulationManager(this);

        // Misc Flag
        zoomLevel = ZoomLevel.Button_Touched;
        FlushButtons = true;
        SelectCardMode = CardSelectMode.OFF;
        start_touch_x = 0;
        start_touch_y = 0;
        ZoomMode = false;
        basePosition = new ViewNodePosition();
        ScreenCenterPosition = new ViewNodePosition();
        PreviousSelectedCard = null;
        Color = new float[] {0.329412f, 0.329412f, 0.329412f, 1f};
    }

    private void ResetFlags() {
        zoomLevel = ZoomLevel.Button_Touched;
        FlushButtons = false;
        SelectCardMode = CardSelectMode.OFF;

        layoutManager.battleZoneLayout.setExpandMode(false);
        layoutManager.opponentBattleZoneLayout.setExpandMode(false);
        layoutManager.manaZoneLayout.setExpandMode(false);
        layoutManager.opponentManaZoneLayout.setExpandMode(false);
        layoutManager.graveyardLayout.setExpandMode(false);
        layoutManager.opponentGraveyardLayout.setExpandMode(false);
        layoutManager.deckLayout.setExpandMode(false);
        layoutManager.opponentDeckLayout.setExpandMode(false);

        layoutManager.manaZoneLayout.SetDraggingMode(false);
        layoutManager.opponentManaZoneLayout.SetDraggingMode(false);
        layoutManager.handZoneLayout.SetDraggingMode(false);

        layoutManager.controllerLayout.setControllerOrientation(true);
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

        layoutManager.controllerLayout.setControllerOrientation(controllerOrientation);

        for (int i = 0; i < expands.length; i++) {
            Expand expand = expands[i];

            switch (expand) {
                case Battle_Z:
                    layoutManager.battleZoneLayout.setExpandMode(true);
                    break;
                case Battle_OZ:
                    layoutManager.opponentDeckLayout.setExpandMode(true);
                    break;
                case Mana_Z:
                    layoutManager.manaZoneLayout.setExpandMode(true);
                    break;
                case Mana_OZ:
                    layoutManager.opponentManaZoneLayout.setExpandMode(true);
                    break;
                case Deck:
                    layoutManager.deckLayout.setExpandMode(true);
                    break;
                case Deck_O:
                    layoutManager.opponentDeckLayout.setExpandMode(true);
                    break;
                case Graveyard:
                    layoutManager.graveyardLayout.setExpandMode(true);
                    break;
                case Graveyard_O:
                    layoutManager.opponentGraveyardLayout.setExpandMode(true);
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
                    layoutManager.handZoneLayout.SetDraggingMode(true);
                    break;
                case Mana_Z:
                    layoutManager.manaZoneLayout.SetDraggingMode(true);
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
        layoutManager.controllerLayout.setControllerButton(controllerButtons1);
    }

    private void clearControlButton() {
        if (selectedCardTracker.getSelectedCard() != null) {
            layoutManager.controllerLayout.unsetControllerButton(false);
        } else {
            layoutManager.controllerLayout.unsetControllerButton(true);
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
                layoutManager.battleZoneLayout.AddCardWidgetToZone(cardWidget);
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
                layoutManager.opponentBattleZoneLayout.AddCardWidgetToZone(cardWidget);
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
                layoutManager.manaZoneLayout.AddCardWidgetToZone(cardWidget);
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
                layoutManager.opponentManaZoneLayout.AddCardWidgetToZone(cardWidget);
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
                layoutManager.handZoneLayout.AddCardWidgetToZone(cardWidget);
            }

            if (cardList.size() > 0) {
                CardWidget cardWidget = getWidgetForCard(cardList.get(cardList.size() - 1));
                ZoneToLastWidgetForSetup.put(new Integer(Maze.hand), cardWidget);
            }
        } else if (zone == Maze.temporaryZone) {
            ZoneToLastWidgetForSetup.clear();
        }
    }

    private Object Simulate(Object ...obj) {
        return simulationManager.Simulate(obj);
    }

    public Object SendAction(Actions action, Object ...obj) {
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
                layoutManager.manaZoneLayout.FreezeNewCoupleSlot();
                break;
            case CleanSelectedCardIfMatch:
                if ((Cards) obj[0] == selectedCardTracker.getSelectedCard()) {
                    selectedCardTracker.setSelectedCard(null);
                    if (FlushButtons) {
                        layoutManager.controllerLayout.unsetControllerButton(true);
                    } else {
                        layoutManager.controllerLayout.removeZoomButton();
                    }
                }
                break;
            case SetUpScenario:
                SetUpScenario(obj);
                break;
            case Simulate:
                return Simulate(obj);
            default:
        }

        return null;
    }

    private boolean SetUpDone() {
        boolean status = true;

        Set<Integer> zones = ZoneToLastWidgetForSetup.keySet();

        for(Integer zone: zones) {
            CardWidget cardWidget = ZoneToLastWidgetForSetup.get(zone);
            if (zone == Maze.battleZone) {
                status &= !layoutManager.battleZoneLayout.IsWidgetInTransition(cardWidget);
            } else if (zone == Maze.Opponent_battleZone) {
                status &= !layoutManager.opponentBattleZoneLayout.IsWidgetInTransition(cardWidget);
            } else if (zone == Maze.manaZone) {
                status &= !layoutManager.manaZoneLayout.IsWidgetInTransition(cardWidget);
            } else if (zone == Maze.Opponent_manaZone) {
                status &= !layoutManager.opponentManaZoneLayout.IsWidgetInTransition(cardWidget);
            } else if (zone == Maze.hand) {
                status &= !layoutManager.handZoneLayout.IsWidgetInTransition(cardWidget);
            }

            if (!status) {
                break;
            }
        }
        return status;
    }

    private boolean SimulationStatus(Object ...obj) {
        return simulationManager.SimulationStatus(obj);
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
                return SimulationStatus(obj);
            case GetZoomSelectedCard:
                return selectedCardTracker.getSelectedCard();
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
                                widgetTouchEvent = layoutManager.battleZoneLayout.TouchResponse(touchEvents);
                                if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                    FocusLayout = layoutManager.battleZoneLayout;
                                }
                            } else if (Math.abs(intersectingPoint.z - ((3f * AssetsAndResource.MazeHeight)/10)) <= (AssetsAndResource.MazeHeight / 10)) {
                                widgetTouchEvent = layoutManager.manaZoneLayout.TouchResponse(touchEvents);
                                if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                    FocusLayout = layoutManager.manaZoneLayout;
                                }
                            }

                            if ((widgetTouchEvent == null) && (input.getNormalizedX(0) < -(0.85f * AssetsAndResource.aspectRatio)) &&
                                    (input.getNormalizedY(0) < -0.85f)) {
                                widgetTouchEvent = layoutManager.fixedButtonsLayout.TouchResponse(touchEvents);
                            }

                            if (widgetTouchEvent == null) {
                                widgetTouchEvent = layoutManager.handZoneLayout.TouchResponse(touchEvents);
                                if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                    FocusLayout = layoutManager.handZoneLayout;
                                }
                            }
                        } else {
                            if (((input.getNormalizedX(0) < -(0.85f * AssetsAndResource.aspectRatio)) && (input.getNormalizedY(0) < -0.85f)) ||
                                    ((input.getNormalizedX(0) > (0.9f * AssetsAndResource.aspectRatio)) && (Math.abs(input.getNormalizedY(0) - 0.3f) < 0.4f))) {
                                widgetTouchEvent = layoutManager.fixedButtonsLayout.TouchResponse(touchEvents);
                            }
                            if (layoutManager.controllerLayout.getControllerOrientation() && (widgetTouchEvent == null) &&
                                    (input.getNormalizedX(0) < -(0.85f * AssetsAndResource.aspectRatio))) {
                                widgetTouchEvent = layoutManager.controllerLayout.TouchResponse(touchEvents);
                            }
                            if ((widgetTouchEvent == null) && (intersectingPoint.z > (0.40f * AssetsAndResource.MazeHeight))) {
                                widgetTouchEvent = layoutManager.handZoneLayout.TouchResponse(touchEvents);
                                if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                    FocusLayout = layoutManager.handZoneLayout;
                                }
                            }

                            if ((widgetTouchEvent == null) && (intersectingPoint.x > (0.50f * AssetsAndResource.MazeWidth))) {
                                if (intersectingPoint.z > (0.25f * AssetsAndResource.MazeHeight)) {
                                    widgetTouchEvent = layoutManager.graveyardLayout.TouchResponse(touchEvents);
                                    if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                        FocusLayout = layoutManager.graveyardLayout;
                                    }

                                    if (widgetTouchEvent == null) {
                                        widgetTouchEvent = layoutManager.deckLayout.TouchResponse(touchEvents);
                                        if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                            FocusLayout = layoutManager.deckLayout;
                                        }
                                    }
                                } else {
                                    widgetTouchEvent = layoutManager.deckLayout.TouchResponse(touchEvents);
                                    if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                        FocusLayout = layoutManager.deckLayout;
                                    }

                                    if (widgetTouchEvent == null) {
                                        widgetTouchEvent = layoutManager.graveyardLayout.TouchResponse(touchEvents);
                                        if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                            FocusLayout = layoutManager.graveyardLayout;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (((input.getNormalizedX(0) < -(0.8f * AssetsAndResource.aspectRatio)) && (input.getNormalizedY(0) > 0.8f)) ||
                                ((input.getNormalizedX(0) > (0.9f * AssetsAndResource.aspectRatio)) && (Math.abs(input.getNormalizedY(0) - 0.3f) < 0.4f)) ||
                                ((input.getNormalizedX(0) > (0.85f * AssetsAndResource.aspectRatio)) && (input.getNormalizedY(0) > 0.85f))) {
                            widgetTouchEvent = layoutManager.fixedButtonsLayout.TouchResponse(touchEvents);
                        }
                        if (!layoutManager.controllerLayout.getControllerOrientation() && (widgetTouchEvent == null) &&
                                (input.getNormalizedY(0) > 0.8f)) {
                            widgetTouchEvent = layoutManager.controllerLayout.TouchResponse(touchEvents);
                        }
                        if (layoutManager.controllerLayout.getControllerOrientation() && (widgetTouchEvent == null) &&
                                (input.getNormalizedX(0) < -(0.85f * AssetsAndResource.aspectRatio))) {
                            widgetTouchEvent = layoutManager.controllerLayout.TouchResponse(touchEvents);
                        }

                        if (widgetTouchEvent == null) {
                            if (Math.abs(intersectingPoint.x) <= (AssetsAndResource.MazeWidth / 2)) {
                                if (Math.abs(intersectingPoint.z + (AssetsAndResource.MazeHeight / 10)) <= (AssetsAndResource.MazeHeight / 10)) {
                                    widgetTouchEvent = layoutManager.opponentBattleZoneLayout.TouchResponse(touchEvents);
                                    if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                        FocusLayout = layoutManager.opponentBattleZoneLayout;
                                    }
                                } else if (Math.abs(intersectingPoint.z + ((3f * AssetsAndResource.MazeHeight) / 10)) <= (AssetsAndResource.MazeHeight / 10)) {
                                    widgetTouchEvent = layoutManager.opponentManaZoneLayout.TouchResponse(touchEvents);
                                    if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                        FocusLayout = layoutManager.opponentManaZoneLayout;
                                    }
                                }
                            } else {
                                if (intersectingPoint.x < -(0.5f * AssetsAndResource.MazeWidth)) {
                                    if (intersectingPoint.z > -(0.25f * AssetsAndResource.MazeHeight)) {
                                        widgetTouchEvent = layoutManager.opponentGraveyardLayout.TouchResponse(touchEvents);
                                        if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                            FocusLayout = layoutManager.opponentGraveyardLayout;
                                        }

                                        if (widgetTouchEvent == null) {
                                            widgetTouchEvent = layoutManager.opponentDeckLayout.TouchResponse(touchEvents);
                                            if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                FocusLayout = layoutManager.opponentDeckLayout;
                                            }
                                        }
                                    } else {
                                        widgetTouchEvent = layoutManager.opponentDeckLayout.TouchResponse(touchEvents);
                                        if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                            FocusLayout = layoutManager.opponentDeckLayout;
                                        }

                                        if (widgetTouchEvent == null) {
                                            widgetTouchEvent = layoutManager.opponentGraveyardLayout.TouchResponse(touchEvents);
                                            if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                FocusLayout = layoutManager.opponentGraveyardLayout;
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
                                        widgetTouchEvent = layoutManager.battleZoneLayout.TouchResponse(touchEvents);
                                        if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                            FocusLayout = layoutManager.battleZoneLayout;
                                        }
                                    } else if (Math.abs(intersectingPoint.z - ((3f * AssetsAndResource.MazeHeight)/10)) <= (AssetsAndResource.MazeHeight / 10)) {
                                        widgetTouchEvent = layoutManager.manaZoneLayout.TouchResponse(touchEvents);
                                        if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                            FocusLayout = layoutManager.manaZoneLayout;
                                        }
                                    }

                                    if ((widgetTouchEvent == null) && (event.normalizedX < -(0.85f * AssetsAndResource.aspectRatio)) &&
                                            (event.normalizedY < -0.85f)) {
                                        widgetTouchEvent = layoutManager.fixedButtonsLayout.TouchResponse(touchEvents);
                                    }
                                    if (widgetTouchEvent == null) {
                                        widgetTouchEvent = layoutManager.handZoneLayout.TouchResponse(touchEvents);
                                        if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                            FocusLayout = layoutManager.handZoneLayout;
                                        }
                                    }
                                } else {
                                    if (((event.normalizedX < -(0.85f * AssetsAndResource.aspectRatio)) && (event.normalizedY < -0.85f)) ||
                                            ((event.normalizedX > (0.9f * AssetsAndResource.aspectRatio)) && (Math.abs(event.normalizedY - 0.3f) < 0.4f))) {
                                        widgetTouchEvent = layoutManager.fixedButtonsLayout.TouchResponse(touchEvents);
                                    }
                                    if (layoutManager.controllerLayout.getControllerOrientation() && (widgetTouchEvent == null) &&
                                            (event.normalizedX < -(0.85f * AssetsAndResource.aspectRatio))) {
                                        widgetTouchEvent = layoutManager.controllerLayout.TouchResponse(touchEvents);
                                    }
                                    if ((widgetTouchEvent == null) && (intersectingPoint.z > (0.40f * AssetsAndResource.MazeHeight))) {
                                        widgetTouchEvent = layoutManager.handZoneLayout.TouchResponse(touchEvents);
                                        if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                            FocusLayout = layoutManager.handZoneLayout;
                                        }
                                    }

                                    if ((widgetTouchEvent == null) && (intersectingPoint.x > (0.5f * AssetsAndResource.MazeWidth))) {
                                        if (intersectingPoint.z > (0.25f * AssetsAndResource.MazeHeight)) {
                                            widgetTouchEvent = layoutManager.graveyardLayout.TouchResponse(touchEvents);
                                            if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                FocusLayout = layoutManager.graveyardLayout;
                                            }

                                            if (widgetTouchEvent == null) {
                                                widgetTouchEvent = layoutManager.deckLayout.TouchResponse(touchEvents);
                                                if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                    FocusLayout = layoutManager.deckLayout;
                                                }
                                            }
                                        } else {
                                            widgetTouchEvent = layoutManager.deckLayout.TouchResponse(touchEvents);
                                            if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                FocusLayout = layoutManager.deckLayout;
                                            }

                                            if (widgetTouchEvent == null) {
                                                widgetTouchEvent = layoutManager.graveyardLayout.TouchResponse(touchEvents);
                                                if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                    FocusLayout = layoutManager.graveyardLayout;
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (((event.normalizedX < -(0.8f * AssetsAndResource.aspectRatio)) && (event.normalizedY > 0.8f)) ||
                                        ((event.normalizedX > (0.9f * AssetsAndResource.aspectRatio)) && (Math.abs(event.normalizedY - 0.3f) < 0.4f)) ||
                                        ((event.normalizedX > (0.85f * AssetsAndResource.aspectRatio)) && (event.normalizedY > 0.85f))) {
                                    widgetTouchEvent = layoutManager.fixedButtonsLayout.TouchResponse(touchEvents);
                                }
                                if (!layoutManager.controllerLayout.getControllerOrientation() && (widgetTouchEvent == null) &&
                                        (event.normalizedY > 0.8f)) {
                                    widgetTouchEvent = layoutManager.controllerLayout.TouchResponse(touchEvents);
                                }
                                if (layoutManager.controllerLayout.getControllerOrientation() && (widgetTouchEvent == null) &&
                                        (event.normalizedX < -(0.85f * AssetsAndResource.aspectRatio))) {
                                    widgetTouchEvent = layoutManager.controllerLayout.TouchResponse(touchEvents);
                                }

                                if (widgetTouchEvent == null) {
                                    if (Math.abs(intersectingPoint.x) <= (AssetsAndResource.MazeWidth / 2)) {
                                        if (Math.abs(intersectingPoint.z + (AssetsAndResource.MazeHeight / 10)) <= (AssetsAndResource.MazeHeight / 10)) {
                                            widgetTouchEvent = layoutManager.opponentBattleZoneLayout.TouchResponse(touchEvents);
                                            if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                FocusLayout = layoutManager.opponentBattleZoneLayout;
                                            }
                                        } else if (Math.abs(intersectingPoint.z + ((3f * AssetsAndResource.MazeHeight) / 10)) <= (AssetsAndResource.MazeHeight / 10)) {
                                            widgetTouchEvent = layoutManager.opponentManaZoneLayout.TouchResponse(touchEvents);
                                            if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                FocusLayout = layoutManager.opponentManaZoneLayout;
                                            }
                                        }
                                    } else {
                                        if (intersectingPoint.x < -(0.5f * AssetsAndResource.MazeWidth)) {
                                            if (intersectingPoint.z > -(0.25f * AssetsAndResource.MazeHeight)) {
                                                widgetTouchEvent = layoutManager.opponentGraveyardLayout.TouchResponse(touchEvents);
                                                if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                    FocusLayout = layoutManager.opponentGraveyardLayout;
                                                }

                                                if (widgetTouchEvent == null) {
                                                    widgetTouchEvent = layoutManager.opponentDeckLayout.TouchResponse(touchEvents);
                                                    if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                        FocusLayout = layoutManager.opponentDeckLayout;
                                                    }
                                                }
                                            } else {
                                                widgetTouchEvent = layoutManager.opponentDeckLayout.TouchResponse(touchEvents);
                                                if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                    FocusLayout = layoutManager.opponentDeckLayout;
                                                }

                                                if (widgetTouchEvent == null) {
                                                    widgetTouchEvent = layoutManager.opponentGraveyardLayout.TouchResponse(touchEvents);
                                                    if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                        FocusLayout = layoutManager.opponentGraveyardLayout;
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
                                layoutManager.controllerLayout.unsetControllerButton(true);
                            } else {
                                layoutManager.controllerLayout.removeZoomButton();
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
                            ZoomMode = true;
                            setWidgetCoordinatorListener(ZoomListener);
                            PreviousListener = LowFocusListener;
                        } else {
                            selectedCardTracker.setSelectedCard((Cards) widgetTouchEvent.object);
                            layoutManager.controllerLayout.addZoomButton();
                            MasterRequests.setRequest(Requests.CardSelected, false);
                            MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                        }
                    } else {
                        selectedCardTracker.setSelectedCard((Cards) widgetTouchEvent.object);
                        layoutManager.controllerLayout.addZoomButton();
                        if (widgetTouchEvent.wasUnderTheStack == false) {
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
                        layoutManager.controllerLayout.unsetControllerButton(true);
                    } else {
                        layoutManager.controllerLayout.removeZoomButton();
                    }
                    if (((ArrayList)widgetTouchEvent.object).size() > 0) {
                        Cards card = (Cards)((ArrayList)widgetTouchEvent.object).get(0);
                        int zone = card.GridPosition().getZone();

                        if (zone == Maze.deck) {
                            MasterRequests.setRequest(Requests.DeckStack, false);
                        } else if (zone == Maze.Opponent_deck) {
                            MasterRequests.setRequest(Requests.OppDeckStack, false);
                        } else if (zone == Maze.graveyard) {
                            MasterRequests.setRequest(Requests.GraveyardStack, false);
                        } else if (zone == Maze.Opponent_graveyard) {
                            MasterRequests.setRequest(Requests.OppGraveyardStack, false);
                        }
                    }
                }

                if (widgetTouchEvent.isFocus == WidgetTouchFocusLevel.Medium) {
                    selectedCardTracker.setSelectedCard(null);
                    if (FlushButtons) {
                        layoutManager.controllerLayout.unsetControllerButton(true);
                    } else {
                        layoutManager.controllerLayout.removeZoomButton();
                    }
                    setWidgetCoordinatorListener(MediumFocusListener);
                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                    return;
                }

                if (widgetTouchEvent.isFocus == WidgetTouchFocusLevel.High) {
                    if (!(widgetTouchEvent.object instanceof Cards)) {
                        throw new RuntimeException("Invalid Condition");
                    }
                    PreviousSelectedCard = selectedCardTracker.getSelectedCard();
                    selectedCardTracker.setSelectedCard((Cards)widgetTouchEvent.object);
                    layoutManager.controllerLayout.addZoomButton();
                    start_touch_x = input.getNormalizedX(0);
                    start_touch_y = input.getNormalizedY(0);
                    setWidgetCoordinatorListener(HighFocusListener);
                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                    return;
                }

                if (!widgetTouchEvent.isTouchedDown && widgetTouchEvent.object == null) {
                    selectedCardTracker.setSelectedCard(null);
                    if (FlushButtons) {
                        layoutManager.controllerLayout.unsetControllerButton(true);
                    } else {
                        layoutManager.controllerLayout.removeZoomButton();
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
                            layoutManager.controllerLayout.removeZoomButton();
                            break;
                        case Decline:
                            MasterRequests.setRequest(Requests.Decline, false);
                            selectedCardTracker.setSelectedCard(null);
                            layoutManager.controllerLayout.removeZoomButton();
                            break;
                        case Zoom:
                            if (selectedCardTracker.getSelectedCard() == null) {
                                throw new RuntimeException("Invalid Condition");
                            }
                            ZoomMode = true;
                            setWidgetCoordinatorListener(ZoomListener);
                            PreviousListener = LowFocusListener;
                            break;
                        case Pause:
                            //Implement pause
                            break;
                        case EndTurn:
                            MasterRequests.setRequest(Requests.EndTurn, false);
                            selectedCardTracker.setSelectedCard(null);
                            layoutManager.controllerLayout.removeZoomButton();
                            break;
                        case Player:
                            MasterRequests.setRequest(Requests.PlayerOrShield, false);
                            selectedCardTracker.setSelectedCard(null);
                            layoutManager.controllerLayout.removeZoomButton();
                            break;
                        case Opponent:
                            MasterRequests.setRequest(Requests.Opponent_PlayerOrShield, false);
                            selectedCardTracker.setSelectedCard(null);
                            layoutManager.controllerLayout.removeZoomButton();
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
                        widgetTouchEvent = layoutManager.fixedButtonsLayout.TouchResponse(touchEvents);
                    }
                    if (!layoutManager.controllerLayout.getControllerOrientation() && (widgetTouchEvent == null) &&
                            (input.getNormalizedY(0) > 0.8f)) {
                        widgetTouchEvent = layoutManager.controllerLayout.TouchResponse(touchEvents);
                    }
                    if (layoutManager.controllerLayout.getControllerOrientation() && (widgetTouchEvent == null) &&
                            (input.getNormalizedX(0) < -(0.85f * AssetsAndResource.aspectRatio))) {
                        widgetTouchEvent = layoutManager.controllerLayout.TouchResponse(touchEvents);
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
                                widgetTouchEvent = layoutManager.fixedButtonsLayout.TouchResponse(touchEvents);
                            }
                            if (!layoutManager.controllerLayout.getControllerOrientation() && (widgetTouchEvent == null) &&
                                    (event.normalizedY > 0.8f)) {
                                widgetTouchEvent = layoutManager.controllerLayout.TouchResponse(touchEvents);
                            }
                            if (layoutManager.controllerLayout.getControllerOrientation() && (widgetTouchEvent == null) &&
                                    (event.normalizedX < -(0.85f * AssetsAndResource.aspectRatio))) {
                                widgetTouchEvent = layoutManager.controllerLayout.TouchResponse(touchEvents);
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
                                layoutManager.controllerLayout.unsetControllerButton(true);
                            } else {
                                layoutManager.controllerLayout.removeZoomButton();
                            }
                        }
                    }
                    return;
                }

                if (widgetTouchEvent.isFocus == WidgetTouchFocusLevel.Low &&
                        !(widgetTouchEvent.object != null && (widgetTouchEvent.object instanceof ControllerButton))) {
                    selectedCardTracker.setSelectedCard(null);
                    if (FlushButtons) {
                        layoutManager.controllerLayout.unsetControllerButton(true);
                    } else {
                        layoutManager.controllerLayout.removeZoomButton();
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
                            ZoomMode = true;
                            setWidgetCoordinatorListener(ZoomListener);
                            PreviousListener = MediumFocusListener;
                        } else {
                            selectedCardTracker.setSelectedCard((Cards) widgetTouchEvent.object);
                            layoutManager.controllerLayout.addZoomButton();
                            MasterRequests.setRequest(Requests.CardSelected, false);
                            MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                        }
                    } else {
                        selectedCardTracker.setSelectedCard((Cards) widgetTouchEvent.object);
                        layoutManager.controllerLayout.addZoomButton();
                        if (widgetTouchEvent.wasUnderTheStack == false) {
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
                    PreviousSelectedCard = selectedCardTracker.getSelectedCard();
                    selectedCardTracker.setSelectedCard((Cards)widgetTouchEvent.object);
                    layoutManager.controllerLayout.addZoomButton();
                    start_touch_x = input.getNormalizedX(0);
                    start_touch_y = input.getNormalizedY(0);
                    setWidgetCoordinatorListener(HighFocusListener);
                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                    return;
                }

                if (widgetTouchEvent.isTouched && !widgetTouchEvent.isTouchedDown && widgetTouchEvent.object == null) {
                    selectedCardTracker.setSelectedCard(null);
                    if (FlushButtons) {
                        layoutManager.controllerLayout.unsetControllerButton(true);
                    } else {
                        layoutManager.controllerLayout.removeZoomButton();
                    }
                }

                if (widgetTouchEvent.isTouched && !widgetTouchEvent.isTouchedDown && (widgetTouchEvent.object instanceof ControllerButton)) {
                    switch ((ControllerButton) widgetTouchEvent.object) {
                        case Accept:
                            MasterRequests.setRequest(Requests.Accept, false);
                            selectedCardTracker.setSelectedCard(null);
                            layoutManager.controllerLayout.removeZoomButton();
                            break;
                        case Decline:
                            MasterRequests.setRequest(Requests.Decline, false);
                            selectedCardTracker.setSelectedCard(null);
                            layoutManager.controllerLayout.removeZoomButton();
                            break;
                        case Zoom:
                            if (selectedCardTracker.getSelectedCard() == null) {
                                throw new RuntimeException("Invalid Condition");
                            }
                            ZoomMode = true;
                            setWidgetCoordinatorListener(ZoomListener);
                            PreviousListener = MediumFocusListener;
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

                if (FocusLayout == layoutManager.handZoneLayout) {
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
                            if (((zoomLevel == ZoomLevel.Button_Touched) || (zoomLevel == ZoomLevel.Touched))
                                    && (PreviousSelectedCard != null) &&
                                    (PreviousSelectedCard == widgetTouchEvent.object)) {
                                ZoomMode = true;
                                setWidgetCoordinatorListener(ZoomListener);
                                PreviousListener = LowFocusListener;
                                FocusLayout = null;
                                AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                return;
                            } else {
                                selectedCardTracker.setSelectedCard((Cards) widgetTouchEvent.object);
                                layoutManager.controllerLayout.addZoomButton();
                                MasterRequests.setRequest(Requests.CardSelected, false);
                                MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                            }
                        } else {
                            selectedCardTracker.setSelectedCard((Cards) widgetTouchEvent.object);
                            layoutManager.controllerLayout.addZoomButton();
                            if (widgetTouchEvent.wasUnderTheStack == false) {
                                MasterRequests.setRequest(Requests.CardSelected, false);
                                MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                            }
                        }
                    }
                    FocusLayout = null;
                    setWidgetCoordinatorListener(LowFocusListener);
                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                    return;
                } else if (FocusLayout == layoutManager.manaZoneLayout) {
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
                            layoutManager.controllerLayout.removeZoomButton();
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
                                    layoutManager.controllerLayout.addZoomButton();
                                    MasterRequests.setRequest(Requests.CardSelected, false);
                                    MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                                } else {
                                    selectedCardTracker.setSelectedCard((Cards) widgetTouchEvent.object);
                                    layoutManager.controllerLayout.addZoomButton();
                                    if (widgetTouchEvent.wasUnderTheStack == false) {
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
                                simulationManager.Simulate(SimulationType.CardMovement, LocationLayout.ManaNewCoupleZone,
                                        LocationLayout.ManaZone, widgetTouchEvent.object);
                                selectedCardTracker.AddCardToSelectedList((Cards) widgetTouchEvent.object, true);
                            } else {
                                if (SelectCardMode == CardSelectMode.OFF) {
                                    selectedCardTracker.setSelectedCard((Cards) widgetTouchEvent.object);
                                    layoutManager.controllerLayout.addZoomButton();
                                    MasterRequests.setRequest(Requests.CardSelected, false);
                                    MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                                } else {
                                    selectedCardTracker.setSelectedCard((Cards) widgetTouchEvent.object);
                                    layoutManager.controllerLayout.addZoomButton();
                                    if (widgetTouchEvent.wasUnderTheStack == false) {
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

        ZoomListener = new WidgetTouchListener() {
            @Override
            public void TouchListener(List<Input.TouchEvent> touchEvents) {
                Input.TouchEvent event = null;
                for (int j = 0; j < touchEvents.size(); j++) {
                    event = touchEvents.get(j);
                    if (event.type == Input.TouchEvent.TOUCH_UP) {
                        if (!((Math.abs(event.normalizedX) <= AssetsAndResource.ZoomCardWidth/2f) &&
                                (Math.abs(event.normalizedY) <= AssetsAndResource.ZoomCardHeight/2f))) {
                            ZoomMode = false;
                            setWidgetCoordinatorListener(PreviousListener);
                        }
                    }
                }
            }
        };
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
        cardWidget.LinkGLobject(glCard, glCurrentSelect, glSelectedCards);
        cardWidget.ShadowEnable(ShadowEnable);

        return cardWidget;
    }

    public void PvPWidgetsTouchListener() {
        List<Input.TouchEvent> touchEvents = AssetsAndResource.game.getInput().getTouchEvents();

        Listener.TouchListener(touchEvents);
    }

    public LayoutManager getLayoutManager() {
        return layoutManager;
    }

    private void SimulationUpdate() {
        simulationManager.SimulationUpdate();
    }

    public void update(float deltaTime, float totalTime) {
        layoutManager.update(deltaTime, totalTime);

        // Must be last
        SimulationUpdate();
    }

    public void draw() {
        AssetsAndResource.ResetCardUsageCount();

        AssetsAndResource.game.setGLFragColoring(false);
        glBindFramebuffer(GL_FRAMEBUFFER, AssetsAndResource.ShadowBuffer.getfboHandle());
        glViewport(0, 0, AssetsAndResource.ShadowBuffer.getWidth(), AssetsAndResource.ShadowBuffer.getHeight());
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        drawPerspectiveProObj();

        AssetsAndResource.game.setGLFragColoring(true);
        glBindFramebuffer(GL_FRAMEBUFFER, AssetsAndResource.SceneBuffer.getfboHandle());
        glViewport(0, 0, AssetsAndResource.SceneBuffer.getWidth(), AssetsAndResource.SceneBuffer.getHeight());
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        drawPerspectiveProObj();

        glDisable(GL_DEPTH_TEST);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, AssetsAndResource.game.getframeBufferWidth(), AssetsAndResource.game.getframeBufferHeight());
        glClear(GL_COLOR_BUFFER_BIT);
        DrawObjectHelper.drawScreen(Screen, AssetsAndResource.SceneBuffer.getrenderTex());
        drawOrthoProObj();
    }

    private void drawPerspectiveProObj() {
        MatrixHelper.setTranslateRotateScale(basePosition);
        DrawObjectHelper.drawOneRectangle(Base, AssetsAndResource.getFixedTexture(AssetsAndResource.BaseID), ShadowEnable);
        //DrawObjectHelper.drawOneUniformRectangle(Base2, Color, ShadowEnable);
        layoutManager.drawPerspectiveProObj();
    }

    private void drawOrthoProObj() {
        layoutManager.drawOrthoProObj();

        if (ZoomMode) {
            MatrixHelper.setTranslate(ScreenCenterPosition);
            DrawObjectHelper.drawOneScreenRectangle(ZoomedCard,
                    AssetsAndResource.getCardTexture(selectedCardTracker.getSelectedCard().getNameID()));
        }
    }
}
