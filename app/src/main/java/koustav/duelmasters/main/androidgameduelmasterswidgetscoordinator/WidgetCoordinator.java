package koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Maze;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayout.HeadOrientation;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.BattleZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardStackWidget;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;
import koustav.duelmasters.main.androidgameduelmasterswidget.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameopenglobjectmodels.Cube;
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

    // Layouts
    BattleZoneLayout battleZoneLayout;
    BattleZoneLayout opponentBattleZoneLayout;

    public CardStackWidget Graveyard;
    public CardStackWidget Deck;
    public CardStackWidget Opponent_Graveyard;
    public CardStackWidget Opponent_Deck;

    // GLObjects
    Cube cube;
    Cube glCard;

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

        // Initialize Layouts
        battleZoneLayout = new BattleZoneLayout(AssetsAndResource.MazeHeight/10, AssetsAndResource.MazeWidth,
                AssetsAndResource.MazeHeight/5, HeadOrientation.North);
        opponentBattleZoneLayout = new BattleZoneLayout(-AssetsAndResource.MazeHeight/10, AssetsAndResource.MazeWidth,
                AssetsAndResource.MazeHeight/5, HeadOrientation.South);

        Graveyard = new CardStackWidget();
        Deck = new CardStackWidget();
        Opponent_Graveyard = new CardStackWidget();
        Opponent_Deck = new CardStackWidget();

        // Initialize GLObject
        cube = new Cube(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), 0.08f, 0.05f, 0.12f, true);

        glCard = new Cube(new GLMaterial(new float[] {0.8f, 0.8f, 0.8f}, new float[] {0.8f, 0.8f, 0.8f},
                new float[] {0.1f, 0.1f, 0.1f}, 10.0f), 0.08f, 0.00125f, 0.12f, true);

        Graveyard.LinkLogicalObject(maze.getZoneList().get(Maze.graveyard).getZoneArray());
        Deck.LinkLogicalObject(maze.getZoneList().get(Maze.deck).getZoneArray());
        Opponent_Graveyard.LinkLogicalObject(maze.getZoneList().get(Maze.Opponent_graveyard).getZoneArray());
        Opponent_Deck.LinkLogicalObject(maze.getZoneList().get(Maze.Opponent_deck).getZoneArray());

        // Link to its GLObject
        Graveyard.LinkGLobject(cube, glCard);
        Graveyard.setFlip(false);
        Deck.LinkGLobject(cube, glCard);
        Deck.setFlip(true);
        Opponent_Graveyard.LinkGLobject(cube, glCard);
        Opponent_Graveyard.setFlip(false);
        Opponent_Deck.LinkGLobject(cube, glCard);
        Opponent_Deck.setFlip(true);

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
        return null;
    }

}
