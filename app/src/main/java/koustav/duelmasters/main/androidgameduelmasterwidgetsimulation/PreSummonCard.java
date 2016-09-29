package koustav.duelmasters.main.androidgameduelmasterwidgetsimulation;


import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.HandZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;

/**
 * Created by Koustav on 9/26/2016.
 */
public class PreSummonCard implements Simulate {
    HandZoneLayout handZoneLayout;
    CardWidget cardWidget;
    boolean start;

    public PreSummonCard(HandZoneLayout handZoneLayout) {
        this.handZoneLayout = handZoneLayout;
        start = false;
    }

    @Override
    public void Start(Object ...obj) {
        start = true;
        cardWidget = (CardWidget) obj[0];
        handZoneLayout.LockCardWidget((CardWidget) obj[0], 0f, AssetsAndResource.MazeHeight/10);
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
            start = handZoneLayout.IsWidgetInTransition(cardWidget);
        }
    }
}
