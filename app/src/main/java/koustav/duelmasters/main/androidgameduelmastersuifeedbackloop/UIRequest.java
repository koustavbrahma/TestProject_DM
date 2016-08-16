package koustav.duelmasters.main.androidgameduelmastersuifeedbackloop;

import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;

/**
 * Created by Koustav on 8/15/2016.
 */
public class UIRequest {
    Requests request;
    boolean force;
    Cards card;

    public UIRequest() {
        request = Requests.None;
        force = false;
        card = null;
    }

    public void resetRequest() {
        request = Requests.None;
        force = false;
        card = null;
    }

    public Requests getRequest() {
        return request;
    }

    public boolean isForce() {
        return force;
    }

    public Cards getCard() {
        return card;
    }

    public void setRequest(Requests request, boolean force) {
        this.request = request;
        this.force = force;
    }

    public void setCard(Cards card) {
        this.card = card;
    }
}
