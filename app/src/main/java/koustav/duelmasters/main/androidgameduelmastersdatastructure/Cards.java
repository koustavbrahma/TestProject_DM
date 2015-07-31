package koustav.duelmasters.main.androidgameduelmastersdatastructure;

/**
 * Created by Koustav on 2/16/2015.
 */
public class Cards {
    protected PackedCardInfo cardinfo;
    protected GridPositionIndex GridPosition;

    public  Cards(PackedCardInfo cardinfo, GridPositionIndex GridPosition) {
        this.cardinfo = cardinfo;
        this.GridPosition = GridPosition;
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
    public PackedCardInfo ExtractCardInfo() {
        PackedCardInfo A;
        A = cardinfo;
        cardinfo = null;
        return A;
    }
}
