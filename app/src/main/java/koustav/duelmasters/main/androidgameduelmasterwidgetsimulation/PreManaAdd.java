package koustav.duelmasters.main.androidgameduelmasterwidgetsimulation;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.HandZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;

/**
 * Created by Koustav on 9/27/2016.
 */
public class PreManaAdd implements Simulate {
    HandZoneLayout handZoneLayout;
    CardWidget cardWidget;
    boolean start;

    public PreManaAdd(HandZoneLayout handZoneLayout) {
        this.handZoneLayout = handZoneLayout;
        start = false;
    }

    @Override
    public void Start(Object ...obj) {
        start = true;
        cardWidget = (CardWidget) obj[0];
        handZoneLayout.LockCardWidget((CardWidget) obj[0], 0f, (3f * AssetsAndResource.MazeHeight)/10);
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
