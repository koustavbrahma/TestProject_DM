package koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator;

import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Maze;
import koustav.duelmasters.main.androidgameduelmasterswidget.WidgetTouchFocusLevel;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayout.HeadOrientation;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayout.Layout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.BattleZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.CardStackZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.HandZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.ManaZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardStackWidget;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;
import koustav.duelmasters.main.androidgameduelmasterswidget.WidgetTouchEvent;
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
    Maze maze;

    // CardWidget Pool
    Pool<CardWidget> cardWidgetPool;

    // CardStackWidget
    public CardStackWidget Graveyard;
    public CardStackWidget Deck;
    public CardStackWidget Opponent_Graveyard;
    public CardStackWidget Opponent_Deck;

    // GLObjects
    Cube cube;
    Cube glCard;
    ScreenRectangle glbutton;

    // Layouts
    BattleZoneLayout battleZoneLayout;
    BattleZoneLayout opponentBattleZoneLayout;
    ManaZoneLayout manaZoneLayout;
    ManaZoneLayout opponentManaZoneLayout;
    CardStackZoneLayout deckLayout;
    CardStackZoneLayout opponentDeckLayout;
    CardStackZoneLayout graveyardLayout;
    CardStackZoneLayout opponentGraveyardLayout;
    HandZoneLayout handZoneLayout;

    // Misc var
    boolean ShadowEnable;
    Layout FocusLayout;
    Cards selectedCard;

    // Listener
    WidgetTouchListener LowFocusListener;
    WidgetTouchListener MediumFocusListener;
    WidgetTouchListener HighFocusListener;
    WidgetTouchListener Listener;

    public PvPWidgetCoordinator(Maze maze) {
        this.maze = maze;

        // Misc var
        this.ShadowEnable = true;
        FocusLayout = null;
        selectedCard = null;

        // CardWidget Pool
        Pool.PoolObjectFactory<CardWidget> factory = new Pool.PoolObjectFactory<CardWidget>() {
            @Override
            public CardWidget createObject() {
                return new CardWidget();
            }
        };
        cardWidgetPool = new Pool<CardWidget>(factory, 80);

        Graveyard = new CardStackWidget();
        Deck = new CardStackWidget();
        Opponent_Graveyard = new CardStackWidget();
        Opponent_Deck = new CardStackWidget();

        // Initialize GLObject
        cube = new Cube(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), AssetsAndResource.CardWidth, AssetsAndResource.CardLength * 40f, AssetsAndResource.CardHeight, true);

        glCard = new Cube(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), AssetsAndResource.CardWidth, AssetsAndResource.CardLength, AssetsAndResource.CardHeight, true);

        glbutton = new ScreenRectangle(0.1f, 0.1f);

        // Link to its GLObject
        Graveyard.LinkGLobject(cube, glCard);
        Graveyard.setFlip(false);
        Graveyard.ShadowEnable(ShadowEnable);
        Graveyard.LinkLogicalObject(this.maze.getZoneList().get(Maze.graveyard).getZoneArray());
        Deck.LinkGLobject(cube, glCard);
        Deck.setFlip(true);
        Deck.ShadowEnable(ShadowEnable);
        Deck.LinkLogicalObject(this.maze.getZoneList().get(Maze.deck).getZoneArray());
        Opponent_Graveyard.LinkGLobject(cube, glCard);
        Opponent_Graveyard.setFlip(false);
        Opponent_Graveyard.ShadowEnable(ShadowEnable);
        Opponent_Graveyard.LinkLogicalObject(this.maze.getZoneList().get(Maze.Opponent_graveyard).getZoneArray());
        Opponent_Deck.LinkGLobject(cube, glCard);
        Opponent_Deck.setFlip(true);
        Opponent_Deck.ShadowEnable(ShadowEnable);
        Opponent_Deck.LinkLogicalObject(this.maze.getZoneList().get(Maze.Opponent_deck).getZoneArray());

        // Link to its Logical Object
        Graveyard.LinkLogicalObject(maze.getZoneList().get(Maze.graveyard).getZoneArray());
        Deck.LinkLogicalObject(maze.getZoneList().get(Maze.deck).getZoneArray());
        Opponent_Graveyard.LinkLogicalObject(maze.getZoneList().get(Maze.Opponent_graveyard).getZoneArray());
        Opponent_Deck.LinkLogicalObject(maze.getZoneList().get(Maze.Opponent_deck).getZoneArray());

        // Initialize Layouts
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

        // Define Listener
        DefineListener();
        Listener = LowFocusListener;

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
                        if (Math.abs(intersectingPoint.x) <= AssetsAndResource.MazeWidth/2) {
                            if (Math.abs(intersectingPoint.z - AssetsAndResource.MazeHeight / 10) <= AssetsAndResource.MazeHeight / 10) {
                                widgetTouchEvent = battleZoneLayout.TouchResponse(touchEvents);
                                if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                    FocusLayout = battleZoneLayout;
                                }
                            } else if (Math.abs(intersectingPoint.z - ((3f * AssetsAndResource.MazeHeight)/10)) <= AssetsAndResource.CardHeight / 10) {
                                widgetTouchEvent = manaZoneLayout.TouchResponse(touchEvents);
                                if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                    FocusLayout = manaZoneLayout;
                                }
                            }

                            if (widgetTouchEvent == null) {
                                widgetTouchEvent = handZoneLayout.TouchResponse(touchEvents);
                                if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                    FocusLayout = handZoneLayout;
                                }
                            }
                        } else {
                            if (intersectingPoint.z > 0.40f * AssetsAndResource.MazeHeight) {
                                widgetTouchEvent = handZoneLayout.TouchResponse(touchEvents);
                                if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                    FocusLayout = handZoneLayout;
                                }
                            }

                            if (widgetTouchEvent == null && intersectingPoint.x > 0) {
                                if (intersectingPoint.z > 0.25f * AssetsAndResource.MazeHeight) {
                                    widgetTouchEvent = graveyardLayout.TouchResponse(touchEvents);
                                    if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                        FocusLayout = graveyardLayout;
                                    }

                                    if (widgetTouchEvent == null) {
                                        widgetTouchEvent = deckLayout.TouchResponse(touchEvents);
                                    }
                                    if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                        FocusLayout = deckLayout;
                                    }
                                } else {
                                    widgetTouchEvent = deckLayout.TouchResponse(touchEvents);
                                    if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                        FocusLayout = deckLayout;
                                    }

                                    if (widgetTouchEvent == null) {
                                        widgetTouchEvent = graveyardLayout.TouchResponse(touchEvents);
                                    }
                                    if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                        FocusLayout = graveyardLayout;
                                    }
                                }
                            }
                        }
                    } else {
                        if (Math.abs(intersectingPoint.x) <= AssetsAndResource.MazeWidth/2) {
                            if (Math.abs(intersectingPoint.z + AssetsAndResource.MazeHeight / 10) <= AssetsAndResource.MazeHeight / 10) {
                                widgetTouchEvent = opponentBattleZoneLayout.TouchResponse(touchEvents);
                                if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                    FocusLayout = opponentBattleZoneLayout;
                                }
                            } else if (Math.abs(intersectingPoint.z + ((3f * AssetsAndResource.MazeHeight)/10)) <= AssetsAndResource.CardHeight / 10) {
                                widgetTouchEvent = opponentManaZoneLayout.TouchResponse(touchEvents);
                                if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                    FocusLayout = opponentManaZoneLayout;
                                }
                            }
                        } else {
                            if (intersectingPoint.x < 0) {
                                if (intersectingPoint.z > -(0.25f * AssetsAndResource.MazeHeight)) {
                                    widgetTouchEvent = opponentGraveyardLayout.TouchResponse(touchEvents);
                                    if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                        FocusLayout = opponentGraveyardLayout;
                                    }

                                    if (widgetTouchEvent == null) {
                                        widgetTouchEvent = opponentDeckLayout.TouchResponse(touchEvents);
                                    }
                                    if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                        FocusLayout = opponentDeckLayout;
                                    }
                                } else {
                                    widgetTouchEvent = opponentDeckLayout.TouchResponse(touchEvents);
                                    if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                        FocusLayout = opponentDeckLayout;
                                    }

                                    if (widgetTouchEvent == null) {
                                        widgetTouchEvent = opponentGraveyardLayout.TouchResponse(touchEvents);
                                        if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                            FocusLayout = opponentGraveyardLayout;
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
                                if (Math.abs(intersectingPoint.x) <= AssetsAndResource.MazeWidth/2) {
                                    if (Math.abs(intersectingPoint.z - AssetsAndResource.MazeHeight / 10) <= AssetsAndResource.MazeHeight / 10) {
                                        widgetTouchEvent = battleZoneLayout.TouchResponse(touchEvents);
                                        if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                            FocusLayout = battleZoneLayout;
                                        }
                                    } else if (Math.abs(intersectingPoint.z - ((3f * AssetsAndResource.MazeHeight)/10)) <= AssetsAndResource.CardHeight / 10) {
                                        widgetTouchEvent = manaZoneLayout.TouchResponse(touchEvents);
                                        if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                            FocusLayout = manaZoneLayout;
                                        }
                                    }

                                    if (widgetTouchEvent == null) {
                                        widgetTouchEvent = handZoneLayout.TouchResponse(touchEvents);
                                        if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                            FocusLayout = handZoneLayout;
                                        }
                                    }
                                } else {
                                    if (intersectingPoint.z > 0.40f * AssetsAndResource.MazeHeight) {
                                        widgetTouchEvent = handZoneLayout.TouchResponse(touchEvents);
                                        if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                            FocusLayout = handZoneLayout;
                                        }
                                    }

                                    if (widgetTouchEvent == null && intersectingPoint.x > 0) {
                                        if (intersectingPoint.z > 0.25f * AssetsAndResource.MazeHeight) {
                                            widgetTouchEvent = graveyardLayout.TouchResponse(touchEvents);
                                            if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                                FocusLayout = graveyardLayout;
                                            }

                                            if (widgetTouchEvent == null) {
                                                widgetTouchEvent = deckLayout.TouchResponse(touchEvents);
                                                if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                                    FocusLayout = deckLayout;
                                                }
                                            }
                                        } else {
                                            widgetTouchEvent = deckLayout.TouchResponse(touchEvents);
                                            if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                                FocusLayout = deckLayout;
                                            }

                                            if (widgetTouchEvent == null) {
                                                widgetTouchEvent = graveyardLayout.TouchResponse(touchEvents);
                                                if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                                    FocusLayout = graveyardLayout;
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (Math.abs(intersectingPoint.x) <= AssetsAndResource.MazeWidth/2) {
                                    if (Math.abs(intersectingPoint.z + AssetsAndResource.MazeHeight / 10) <= AssetsAndResource.MazeHeight / 10) {
                                        widgetTouchEvent = opponentBattleZoneLayout.TouchResponse(touchEvents);
                                        if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                            FocusLayout = opponentBattleZoneLayout;
                                        }
                                    } else if (Math.abs(intersectingPoint.z + ((3f * AssetsAndResource.MazeHeight)/10)) <= AssetsAndResource.CardHeight / 10) {
                                        widgetTouchEvent = opponentManaZoneLayout.TouchResponse(touchEvents);
                                        if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                            FocusLayout = opponentManaZoneLayout;
                                        }
                                    }
                                } else {
                                    if (intersectingPoint.x < 0) {
                                        if (intersectingPoint.z > -(0.25f * AssetsAndResource.MazeHeight)) {
                                            widgetTouchEvent = opponentGraveyardLayout.TouchResponse(touchEvents);
                                            if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                                FocusLayout = opponentGraveyardLayout;
                                            }

                                            if (widgetTouchEvent == null) {
                                                widgetTouchEvent = opponentDeckLayout.TouchResponse(touchEvents);
                                                if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                                    FocusLayout = opponentDeckLayout;
                                                }
                                            }
                                        } else {
                                            widgetTouchEvent = opponentDeckLayout.TouchResponse(touchEvents);
                                            if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                                FocusLayout = opponentDeckLayout;
                                            }

                                            if (widgetTouchEvent == null) {
                                                widgetTouchEvent = opponentGraveyardLayout.TouchResponse(touchEvents);
                                                if (widgetTouchEvent != null && widgetTouchEvent.isFocus != WidgetTouchFocusLevel.Low) {
                                                    FocusLayout = opponentGraveyardLayout;
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

                if (widgetTouchEvent == null) {
                    return;
                }

                if (!widgetTouchEvent.isTouched) {
                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                    throw new RuntimeException("Invalid condition");
                }

                if (widgetTouchEvent.object instanceof Cards) {
                    selectedCard = (Cards) widgetTouchEvent.object;
                }

                if (widgetTouchEvent.isFocus == WidgetTouchFocusLevel.Medium) {
                    setWidgetCoordinatorListener(MediumFocusListener);
                    return;
                }

                if (widgetTouchEvent.isFocus == WidgetTouchFocusLevel.High) {
                    setWidgetCoordinatorListener(HighFocusListener);
                    return;
                }
            }
        };

        MediumFocusListener = new WidgetTouchListener() {
            @Override
            public void TouchListener(List<Input.TouchEvent> touchEvents) {

            }
        };

        HighFocusListener = new WidgetTouchListener() {
            @Override
            public void TouchListener(List<Input.TouchEvent> touchEvents) {

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
    }
}
