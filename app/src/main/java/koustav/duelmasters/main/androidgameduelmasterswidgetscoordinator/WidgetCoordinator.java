package koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Maze;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Zone;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardStackWidget;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.ZoneWidget;
import koustav.duelmasters.main.androidgameopenglobjectmodels.Cube;
import koustav.duelmasters.main.androidgameopenglobjectmodels.XZRectangle;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry;
import koustav.duelmasters.main.androidgameopenglutil.GLGeometry.*;
import koustav.duelmasters.main.androidgameopenglutil.GLMaterial;
import koustav.duelmasters.main.androidgamesframework.Input;
import koustav.duelmasters.main.androidgamesframework.Pool;

/**
 * Created by Koustav on 3/12/2016.
 */

public class WidgetCoordinator {
    Maze maze;

    // CardWidget Pool
    Pool<CardWidget> cardWidgetPool;

    // Widgets
    ZoneWidget BattleZone;
    ZoneWidget ManaZone;
    ZoneWidget Opponent_BattleZone;
    ZoneWidget Opponent_ManaZone;

    public CardStackWidget Graveyard;
    public CardStackWidget Deck;
    public CardStackWidget Opponent_Graveyard;
    public CardStackWidget Opponent_Deck;

    // GLObjects
    Cube cube;
    XZRectangle glCard;

    // Tracking tables
    Hashtable<Cards, CardWidget> CardToWidget;

    // Local variables
    ArrayList<Cards> cardsArrayList;
    ArrayList<Float> floatArrayList;

    public WidgetCoordinator(Maze maze) {
        this.maze = maze;

        // CardWidget Pool
        Pool.PoolObjectFactory<CardWidget> factory = new Pool.PoolObjectFactory<CardWidget>() {
            @Override
            public CardWidget createObject() {
                return new CardWidget();
            }
        };
        cardWidgetPool = new Pool<CardWidget>(factory, 80);

        // Initialize Widgets
        BattleZone =new ZoneWidget();
        ManaZone = new ZoneWidget();
        Opponent_BattleZone = new ZoneWidget();
        Opponent_ManaZone = new ZoneWidget();

        Graveyard = new CardStackWidget();
        Deck = new CardStackWidget();
        Opponent_Graveyard = new CardStackWidget();
        Opponent_Deck = new CardStackWidget();

        // Initialize GLObject
        cube = new Cube(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), 0.08f, 0.05f, 0.12f, true);

        glCard = new XZRectangle(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), 0.08f, 0.12f, 0);

        // Link to its logical unit
        BattleZone.LinkLogicalObject(maze.getZoneList().get(Maze.battleZone));
        ManaZone.LinkLogicalObject(maze.getZoneList().get(Maze.manaZone));
        Opponent_BattleZone.LinkLogicalObject(maze.getZoneList().get(Maze.Opponent_battleZone));
        Opponent_ManaZone.LinkLogicalObject(maze.getZoneList().get(Maze.Opponent_manaZone));

        Graveyard.LinkLogicalObject(maze.getZoneList().get(Maze.graveyard).getZoneArray());
        Deck.LinkLogicalObject(maze.getZoneList().get(Maze.deck).getZoneArray());
        Opponent_Graveyard.LinkLogicalObject(maze.getZoneList().get(Maze.Opponent_graveyard).getZoneArray());
        Opponent_Deck.LinkLogicalObject(maze.getZoneList().get(Maze.Opponent_deck).getZoneArray());

        // Link to its GLObject
        Graveyard.LinkGLobject(cube, glCard);
        Deck.LinkGLobject(cube, glCard);
        Opponent_Graveyard.LinkGLobject(cube, glCard);
        Opponent_Deck.LinkGLobject(cube, glCard);

        // Tracking tables
        CardToWidget = new Hashtable<Cards, CardWidget>();

        // Local variables
        cardsArrayList = new ArrayList<Cards>();
        floatArrayList = new ArrayList<Float>();
    }

    public CardWidget getWidgetForCard(Cards card) {
        return CardToWidget.get(card);
    }

    public CardWidget removeWidgetForCard(Cards card) {
        return CardToWidget.remove(card);
    }

    public void addWidgetForCard(Cards card, CardWidget cardWidget) {
        CardToWidget.put(card, cardWidget);
    }

    public void freeCardWidget(CardWidget cardWidget) {
        cardWidgetPool.free(cardWidget);
    }

    public CardWidget newCardWidget() {
        CardWidget cardWidget = cardWidgetPool.newObject();
        cardWidget.LinkGLobject(glCard);

        return cardWidget;
    }

    public Cards getTouchedCard(List<Input.TouchEvent> touchEvents) {
        GLPoint intersectingPoint = null;
        Input input = AssetsAndResource.game.getInput();
        Cards card = null;
        Zone zone = null;

        if (input.isTouchDown(0)) {
            intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                    new GLRay(input.getNearPoint(0), GLGeometry.GLVectorBetween(input.getNearPoint(0), input.getFarPoint(0))), 0);
        }

        for(int i = 0; i<touchEvents.size(); i++) {
            Input.TouchEvent event = touchEvents.get(i);
            if (event.type == Input.TouchEvent.TOUCH_UP) {
                intersectingPoint = GLGeometry.GLRayIntersectionWithXZPlane(
                        new GLRay(event.nearPoint[0], GLGeometry.GLVectorBetween(event.nearPoint[0], event.farPoint[0])), 0);
            }
        }

        if (intersectingPoint != null) {
            WidgetTouchEvent widgetTouchEvent = null;
            if (intersectingPoint.z > 0) {
                if ((widgetTouchEvent = BattleZone.isTouched(touchEvents)) != null && widgetTouchEvent.isTouched) {
                    zone = BattleZone.zone;
                } else if (AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent) &&
                        (widgetTouchEvent = ManaZone.isTouched(touchEvents)) != null && widgetTouchEvent.isTouched){
                    zone = ManaZone.zone;
                }

                if (widgetTouchEvent != null) {
                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                }
            } else {
                if ((widgetTouchEvent = Opponent_BattleZone.isTouched(touchEvents)) != null && widgetTouchEvent.isTouched) {
                    zone = Opponent_BattleZone.zone;
                } else if (AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent) &&
                        (widgetTouchEvent = Opponent_ManaZone.isTouched(touchEvents)) != null && widgetTouchEvent.isTouched) {
                    zone = Opponent_ManaZone.zone;
                }

                if (widgetTouchEvent != null) {
                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                }
            }

            cardsArrayList.clear();

            for (int i = 0; i < zone.zoneSize(); i++) {
                Cards tcard = zone.getZoneArray().get(i);

                CardWidget widget = CardToWidget.get(tcard);

                if (widget == null) {
                    throw new RuntimeException("For the card no widget exist");
                }


            }
        }

        return card;
    }

}
