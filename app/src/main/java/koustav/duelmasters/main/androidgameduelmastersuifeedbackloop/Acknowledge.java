package koustav.duelmasters.main.androidgameduelmastersuifeedbackloop;

/**
 * Created by Koustav on 8/15/2016.
 */
public class Acknowledge {
    boolean Acknowledge;
    Requests request;

    public Acknowledge() {
        Acknowledge = false;
        request = Requests.None;
    }

    public void resetAcknowledge() {
        Acknowledge = false;
        request = Requests.None;
    }

    public void setAcknowledge(boolean val) {
        Acknowledge = val;
    }

    public void setRequest(Requests request) {
        this.request = request;
    }

    public Requests getRequest() {
        return request;
    }

    public boolean isAcknowledge() {
        return Acknowledge;
    }
}
