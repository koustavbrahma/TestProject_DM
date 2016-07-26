package koustav.duelmasters.main.androidgameduelmastersdatastructure;

import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.GridPositionIndex;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;

/**
 * Created by Koustav on 2/16/2015.
 */
public class Cards {
    protected PackedCardInfo cardinfo;
    protected GridPositionIndex GridPosition;
    CardWidget widget;

    public  Cards(PackedCardInfo cardinfo, GridPositionIndex GridPosition) {
        this.cardinfo = cardinfo;
        this.GridPosition = GridPosition;
        this.widget = null;
    }

    public GridPositionIndex GridPosition() {
        return GridPosition;
    }
    public PackedCardInfo cardInfo() {
        return cardinfo;
    }
    public String getNameID() {
        return new String(cardinfo.SlotAttributes.get(0));
    }
    public int getCivilization() {
        return Integer.parseInt(cardinfo.SlotAttributes.get(1));
    }
    public TypeOfCard getType() {
        TypeOfCard type;
        switch (cardinfo.SlotAttributes.get(4)) {
            case "1":
                type = TypeOfCard.Creature;
                break;
            case "2":
                type = TypeOfCard.Evolution;
                break;
            case "3":
                type = TypeOfCard.Spell;
                break;
            default:
                throw new IllegalArgumentException("Invalid Type for " + cardinfo.SlotAttributes.get(0));

        }
        return type;
    }
    public PackedCardInfo ExtractCardInfo() {
        PackedCardInfo A;
        A = cardinfo;
        cardinfo = null;
        return A;
    }
    public void setWidget(CardWidget widget) {
        this.widget = widget;
    }
    public CardWidget getWidget() {
        return widget;
    }
    public CardWidget unsetWidget() {
        CardWidget oldwidget = this.widget;
        this.widget = null;
        return oldwidget;
    }
}
