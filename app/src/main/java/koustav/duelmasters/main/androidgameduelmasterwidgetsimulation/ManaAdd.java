package koustav.duelmasters.main.androidgameduelmasterwidgetsimulation;


import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.HandZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.ManaZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;

/**
 * Created by Koustav on 9/28/2016.
 */
public class ManaAdd implements Simulate {
    HandZoneLayout handZoneLayout;
    ManaZoneLayout manaZoneLayout;
    CardWidget cardWidget;
    boolean start;

    public ManaAdd(HandZoneLayout handZoneLayout, ManaZoneLayout manaZoneLayout) {
        this.handZoneLayout = handZoneLayout;
        this.manaZoneLayout = manaZoneLayout;
        start = false;
    }

    public void Start(Object ...obj) {
        if ((boolean) obj[1]) {
            start = true;
            cardWidget = (CardWidget) obj[0];
            handZoneLayout.LockCardWidget((CardWidget) obj[0], 0f, (3f * AssetsAndResource.MazeHeight) / 10);
        } else {
            handZoneLayout.UnlockCardWidget((CardWidget) obj[0]);
            if (obj[0] != handZoneLayout.RemoveCardWidgetFromZone((CardWidget) obj[0])) {
                throw new RuntimeException("Invalid Condition");
            }
            manaZoneLayout.AddCardWidgetToZone((CardWidget) obj[0]);
        }
    }

    public boolean IsFinish() {
        if (!start) {
            return true;
        } else {
            return false;
        }
    }

    public void update() {
        if (start) {
            if (!handZoneLayout.IsWidgetInTransition(cardWidget)) {
                if (cardWidget != handZoneLayout.RemoveCardWidgetFromZone(cardWidget)) {
                    throw new RuntimeException("Invalid Condition");
                }
                manaZoneLayout.AddCardWidgetToZone(cardWidget);
                start = false;
            }
        }
    }
}
