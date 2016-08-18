package koustav.duelmasters.main.androidgameduelmasterswidgetutil;

import java.util.ArrayList;

import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;

/**
 * Created by Koustav on 8/18/2016.
 */
public class WidgetSelectedCardTracker {
    Cards selectedCard;
    ArrayList<Cards> selectedCards;

    public WidgetSelectedCardTracker() {
        selectedCard = null;
        selectedCards = new ArrayList<Cards>();
    }

    public void setSelectedCard(Cards card) {
        selectedCard = card;
    }

    public Cards getSelectedCard() {
        return selectedCard;
    }

    public ArrayList<Cards> getSelectedCardsList() {
        return selectedCards;
    }
}
