package koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator;

import java.util.ArrayList;
import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Maze;
import koustav.duelmasters.main.androidgameduelmastersuirequestandaction.Actions;
import koustav.duelmasters.main.androidgameduelmastersuirequestandaction.Requests;
import koustav.duelmasters.main.androidgameduelmastersuirequestandaction.UIRequest;
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
import koustav.duelmasters.main.androidgameopenglobjectmodels.Cube;
import koustav.duelmasters.main.androidgameopenglobjectmodels.ScreenRectangle;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;
import koustav.duelmasters.main.androidgameopenglutil.GLMaterial;
import koustav.duelmasters.main.androidgamesframework.Input;
import koustav.duelmasters.main.androidgamesframework.Pool;

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

    // Listener
    WidgetTouchListener LowFocusListener;
    WidgetTouchListener MediumFocusListener;
    WidgetTouchListener HighFocusListener;
    WidgetTouchListener Listener;

    // UI Requests
    UIRequest MasterRequests;
    UIRequest CopyRequests;

    public PvPWidgetCoordinator(Maze maze) {
        this.maze = maze;

        // Misc var
        this.ShadowEnable = true;
        FocusLayout = null;
        selectedCardTracker = new WidgetSelectedCardTracker();

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
        cube = new Cube(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), AssetsAndResource.CardWidth, AssetsAndResource.CardLength * 40f, AssetsAndResource.CardHeight, true);

        glCard = new Cube(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), AssetsAndResource.CardWidth, AssetsAndResource.CardLength, AssetsAndResource.CardHeight, true);

        glRbutton = new ScreenRectangle(0.1f, 0.1f);

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
        deckLayout = new CardStackZoneLayout();
        opponentDeckLayout = new CardStackZoneLayout();
        graveyardLayout = new CardStackZoneLayout();
        opponentGraveyardLayout = new CardStackZoneLayout();
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

        // Added Button To the Controller Layout
        controllerLayout.AddButtonWidget(ControllerButton.Accept, AttackButton);
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

        // Misc Flag
        zoomLevel = ZoomLevel.Button_Touched;
        FlushButtons = true;
        SelectCardMode = CardSelectMode.OFF;
        start_touch_x = 0;
        start_touch_y = 0;
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
    }

    public void SetFlags(ZoomLevel zoomLevel, Expand[] expands, Drag[] drags, boolean flushButtons,
                         CardSelectMode selectCardMode) {
        ResetFlags();

        this.zoomLevel= zoomLevel;
        FlushButtons = flushButtons;
        this.SelectCardMode = selectCardMode;
        if (this.SelectCardMode != CardSelectMode.OFF) {
            this.zoomLevel = ZoomLevel.Button;
        }

        for (int i = 0; i < expands.length; i++) {
            Expand expand = expands[i];

            switch (expand) {
                case Battle_Z:
                    battleZoneLayout.setExpandMode(true);
                    break;
                case  Battle_OZ:
                    opponentDeckLayout.setExpandMode(true);
                    break;
                case  Mana_Z:
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

    public WidgetSelectedCardTracker getSelectedCardTracker() {
        return selectedCardTracker;
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

    public void SendAction(Actions action, Object ...obj) {
        switch (action) {
            case AddControlButton:
                setControlButton((ArrayList<ControllerButton>)obj[0]);
                break;
            case ClearControlButton:
                clearControlButton();
                break;
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
                            if (Math.abs(intersectingPoint.z - (AssetsAndResource.MazeHeight / 10)) <= (AssetsAndResource.MazeHeight / 5)) {
                                widgetTouchEvent = battleZoneLayout.TouchResponse(touchEvents);
                                if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                    FocusLayout = battleZoneLayout;
                                }
                            } else if (Math.abs(intersectingPoint.z - ((3f * AssetsAndResource.MazeHeight)/10)) <= (AssetsAndResource.MazeHeight / 5)) {
                                widgetTouchEvent = manaZoneLayout.TouchResponse(touchEvents);
                                if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                    FocusLayout = manaZoneLayout;
                                }
                            }

                            if ((widgetTouchEvent == null) && (input.getNormalizedX(0) < -0.85f) &&
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
                            if ((input.getNormalizedX(0) < -0.85f) && ((Math.abs(input.getNormalizedY(0)) < 0.1f) ||
                                    (input.getNormalizedY(0) < -0.85f))) {
                                widgetTouchEvent = fixedButtonsLayout.TouchResponse(touchEvents);
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
                                    }
                                    if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                        FocusLayout = deckLayout;
                                    }
                                } else {
                                    widgetTouchEvent = deckLayout.TouchResponse(touchEvents);
                                    if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                        FocusLayout = deckLayout;
                                    }

                                    if (widgetTouchEvent == null) {
                                        widgetTouchEvent = graveyardLayout.TouchResponse(touchEvents);
                                    }
                                    if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                        FocusLayout = graveyardLayout;
                                    }
                                }
                            }
                        }
                    } else {
                        if (((input.getNormalizedX(0) < -0.8f) && ((input.getNormalizedY(0) > 0.8f) || (Math.abs(input.getNormalizedY(0)) < 0.1f))) ||
                                ((input.getNormalizedX(0) > 0.85f) && (input.getNormalizedY(0) > 0.85f))) {
                            widgetTouchEvent = fixedButtonsLayout.TouchResponse(touchEvents);
                        }
                        if ((widgetTouchEvent == null) && (input.getNormalizedY(0) > 0.8f)) {
                            widgetTouchEvent = controllerLayout.TouchResponse(touchEvents);
                        }

                        if (widgetTouchEvent == null) {
                            if (Math.abs(intersectingPoint.x) <= (AssetsAndResource.MazeWidth / 2)) {
                                if (Math.abs(intersectingPoint.z + (AssetsAndResource.MazeHeight / 10)) <= (AssetsAndResource.MazeHeight / 5)) {
                                    widgetTouchEvent = opponentBattleZoneLayout.TouchResponse(touchEvents);
                                    if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                        FocusLayout = opponentBattleZoneLayout;
                                    }
                                } else if (Math.abs(intersectingPoint.z + ((3f * AssetsAndResource.MazeHeight) / 10)) <= (AssetsAndResource.MazeHeight / 5)) {
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
                                        }
                                        if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                            FocusLayout = opponentDeckLayout;
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
                                    if (Math.abs(intersectingPoint.z - (AssetsAndResource.MazeHeight / 10)) <= (AssetsAndResource.MazeHeight / 5)) {
                                        widgetTouchEvent = battleZoneLayout.TouchResponse(touchEvents);
                                        if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                            FocusLayout = battleZoneLayout;
                                        }
                                    } else if (Math.abs(intersectingPoint.z - ((3f * AssetsAndResource.MazeHeight)/10)) <= (AssetsAndResource.MazeHeight / 5)) {
                                        widgetTouchEvent = manaZoneLayout.TouchResponse(touchEvents);
                                        if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                            FocusLayout = manaZoneLayout;
                                        }
                                    }

                                    if ((widgetTouchEvent == null) && (event.normalizedX < -0.85f) &&
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
                                    if ((event.normalizedX < -0.85f) && ((Math.abs(event.normalizedY) < 0.1f) ||
                                            (event.normalizedY < -0.85f))) {
                                        widgetTouchEvent = fixedButtonsLayout.TouchResponse(touchEvents);
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
                                if (((event.normalizedX < -0.8f) && ((event.normalizedY > 0.8f) || (Math.abs(event.normalizedY) < 0.1f))) ||
                                        ((event.normalizedX > 0.85f) && (event.normalizedY > 0.85f))) {
                                    widgetTouchEvent = fixedButtonsLayout.TouchResponse(touchEvents);
                                }
                                if ((widgetTouchEvent == null) && (event.normalizedY > 0.8f)) {
                                    widgetTouchEvent = controllerLayout.TouchResponse(touchEvents);
                                }

                                if (widgetTouchEvent == null) {
                                    if (Math.abs(intersectingPoint.x) <= (AssetsAndResource.MazeWidth / 2)) {
                                        if (Math.abs(intersectingPoint.z + (AssetsAndResource.MazeHeight / 10)) <= (AssetsAndResource.MazeHeight / 5)) {
                                            widgetTouchEvent = opponentBattleZoneLayout.TouchResponse(touchEvents);
                                            if ((widgetTouchEvent != null) && (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low)) {
                                                FocusLayout = opponentBattleZoneLayout;
                                            }
                                        } else if (Math.abs(intersectingPoint.z + ((3f * AssetsAndResource.MazeHeight) / 10)) <= (AssetsAndResource.MazeHeight / 5)) {
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

                    return;
                }

                if (widgetTouchEvent.isFocus == WidgetTouchFocusLevel.Medium) {
                    selectedCardTracker.setSelectedCard(null);
                    if (FlushButtons) {
                        controllerLayout.unsetControllerButton(true);
                    } else {
                        controllerLayout.removeZoomButton();
                    }
                    setWidgetCoordinatorListener(MediumFocusListener);
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
            }
        };

        MediumFocusListener = new WidgetTouchListener() {
            @Override
            public void TouchListener(List<Input.TouchEvent> touchEvents) {
                Input input = AssetsAndResource.game.getInput();
                WidgetTouchEvent widgetTouchEvent = null;

                if (input.isTouchDown(0)) {
                    if ((input.getNormalizedX(0) < -0.8f) && (input.getNormalizedY(0) > 0.8f)) {
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
                            if ((event.normalizedX < -0.8f) && (event.normalizedY > 0.8f)) {
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
                    return;
                }

                if (!widgetTouchEvent.isTouchedDown &&
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
                            MasterRequests.setRequest(Requests.CardSelected, false);
                            MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                        }
                    }

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
                    return;
                }

                if (FocusLayout == handZoneLayout) {
                    if (widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                        throw new RuntimeException("Invalid Condition");
                    }
                    CardWidget widget = selectedCardTracker.getSelectedCard().getWidget();
                    if ((widget.getPosition().rotaion.y != 0) && (widget.getPosition().rotaion.x == 0) &&
                            (widget.getPosition().rotaion.z == 0)) {
                        if (Math.abs(widget.getPosition().Centerposition.z - ((3f * AssetsAndResource.MazeHeight)/10)) < AssetsAndResource.MazeHeight / 5) {
                            MasterRequests.setRequest(Requests.AddToMana, true);
                            MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                        } else if (Math.abs(widget.getPosition().Centerposition.z - (AssetsAndResource.MazeHeight/10)) < AssetsAndResource.MazeHeight / 5) {
                            MasterRequests.setRequest(Requests.SummonOrCast, false);
                            MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                        }
                    }
                    FocusLayout = null;
                    setWidgetCoordinatorListener(LowFocusListener);
                    return;
                } else if (FocusLayout == manaZoneLayout) {
                    Input input = AssetsAndResource.game.getInput();
                    boolean moved = false;
                    if ((Math.abs(input.getNormalizedX(0) - start_touch_x) > 0.01) ||
                            (Math.abs(input.getNormalizedY(0) - start_touch_y) > 0.01)) {
                        moved = true;
                    }
                    if (widgetTouchEvent.isFocus == WidgetTouchFocusLevel.Medium) {
                        if (!selectedCardTracker.getSelectedPileCards().contains(widgetTouchEvent.object)) {
                            throw new RuntimeException("Invalid Condition");
                        }
                        if (moved) {
                            MasterRequests.setRequest(Requests.CardSelected, true);
                            MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                        }
                        selectedCardTracker.setSelectedCard(null);
                        controllerLayout.removeZoomButton();
                        setWidgetCoordinatorListener(MediumFocusListener);
                        return;
                    }
                    if (widgetTouchEvent.isFocus == WidgetTouchFocusLevel.Low) {
                        if (!selectedCardTracker.getSelectedCardsList().contains(widgetTouchEvent.object) ||
                                selectedCardTracker.getSelectedPileCards().contains(widgetTouchEvent.object)) {
                            MasterRequests.setRequest(Requests.CardSelected, true);
                            MasterRequests.setCard(selectedCardTracker.getSelectedCard());
                        } else {
                            if (((Cards)widgetTouchEvent.object).GridPosition().getZone() != Maze.manaZone) {
                                throw new RuntimeException("Invalid Condition");
                            }
                            manaZoneLayout.AddToTransitionZone(((Cards)widgetTouchEvent.object).getWidget());
                            selectedCardTracker.getSelectedPileCards().add((Cards)widgetTouchEvent.object);
                        }
                        FocusLayout = null;
                        setWidgetCoordinatorListener(LowFocusListener);
                        return;
                    }
                } else {
                    throw new RuntimeException("Invalid Condition");
                }
            }
        };
    }

    public CardWidget getWidgetForCard(Cards card) {
        if (card.getWidget() != null && card.getWidget() != card.getWidget().getLogicalObject()) {
            throw new RuntimeException("card and CardWidget inconsistency");
        }

        return card.getWidget();
    }

    public CardWidget  DecoupleWidgetFormCard(Cards card) {
        if (card.getWidget() != card.getWidget().getLogicalObject()) {
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
    }

    public void draw(){
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
