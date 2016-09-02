package koustav.duelmasters.main.androidgameduelmastersuirequest;

import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;

/**
 * Created by Koustav on 8/15/2016.
 */
public class UIRequest {
    Requests request;
    boolean Dragged;
    Cards card;

    public UIRequest() {
        request = Requests.None;
        Dragged = false;
        card = null;
    }

    public void resetRequest() {
        request = Requests.None;
        Dragged = false;
        card = null;
    }

    public Requests getRequest() {
        return request;
    }

    public boolean isForce() {
        return Dragged;
    }

    public Cards getCard() {
        return card;
    }

    public void setRequest(Requests request, boolean Dragged) {
        this.request = request;
        this.Dragged = Dragged;
    }

    public void setCard(Cards card) {
        this.card = card;
    }
}
