package koustav.duelmasters.main.androidgameduelmasterwidgetsimulation;

import java.util.ArrayList;

import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.CardStackZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.HandZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;

/**
 * Created by Koustav on 10/14/2016.
 */
public class DrawCardFromDeck implements Simulate {
    HandZoneLayout handZoneLayout;
    CardStackZoneLayout deckLayout;
    ArrayList<CardWidget> cardWidgets;
    boolean start;

    public DrawCardFromDeck(HandZoneLayout handZoneLayout, CardStackZoneLayout deckLayout) {
        this.handZoneLayout = handZoneLayout;
        this.deckLayout = deckLayout;
        cardWidgets = new ArrayList<CardWidget>();
        start = false;
    }

    @Override
    public void Start(Object ...obj) {
        ArrayList<CardWidget> cardWidget = deckLayout.GenerateCardWidgetForFirstNCard((int) obj[0]);
        cardWidgets.clear();
        for (int i = 0; i < cardWidget.size(); i++) {
            cardWidgets.add(cardWidget.get(i));
        }
        handZoneLayout.AddToCardWidgetQueue(cardWidgets);
        start = true;
    }

    @Override
    public boolean IsFinish() {
        if (!start) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void update() {
        if (start) {
            boolean status = false;
            for (int i = 0; i < cardWidgets.size(); i++) {
                status |= handZoneLayout.IsWidgetInTransition(cardWidgets.get(i));
            }
            start = status;
            if (!start) {
                cardWidgets.clear();
            }
        }
    }
}
