package koustav.duelmasters.main.androidgameduelmasterswidgetutil;

import java.util.ArrayList;

import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;

/**
 * Created by Koustav on 8/18/2016.
 */
public class WidgetSelectedCardTracker {
    Cards selectedCard;
    ArrayList<Cards> selectedCards;
    ArrayList<Cards> selectedPileCards;
    ArrayList<Cards> onFocusCards;

    public WidgetSelectedCardTracker() {
        selectedCard = null;
        selectedCards = new ArrayList<Cards>();
        selectedPileCards = new ArrayList<Cards>();
        onFocusCards = new ArrayList<Cards>();
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

    public void ClearSelectedCard() {
        selectedCards.clear();
        selectedPileCards.clear();
    }

    public boolean IsSelectedCard(Cards card) {
        if (selectedCards.contains(card)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean IsSelectedPileCard(Cards card) {
        if (selectedPileCards.contains(card)) {
            if (!selectedCards.contains(card)) {
                throw new RuntimeException("Invalid Condition");
            }
            return true;
        } else {
            return false;
        }
    }

    public void AddCardToSelectedList(Cards card, boolean pile) {
        if (!selectedCards.contains(card)) {
            selectedCards.add(card);
        }
        if (pile) {
            if (!selectedPileCards.contains(card)) {
                selectedPileCards.add(card);
            }
        }
    }

    public void RemoveCardFromSelectedList(Cards card) {
        if (selectedCards.contains(card)) {
            selectedCards.remove(card);
        }
        if (selectedPileCards.contains(card)) {
            selectedPileCards.remove(card);
        }
    }

    public ArrayList<Cards> getOnFocusCards() {
        return onFocusCards;
    }
}
