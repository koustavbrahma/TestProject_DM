package koustav.duelmasters.main.androidgameduelmasterwidgetsimulation;

import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.ManaZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;

/**
 * Created by Koustav on 10/10/2016.
 */
public class CancelManaCard implements Simulate {
    ManaZoneLayout manaZoneLayout;
    boolean start;

    public CancelManaCard(ManaZoneLayout manaZoneLayout) {
        this.manaZoneLayout = manaZoneLayout;
        start = false;
    }

    @Override
    public void Start(Object ...obj) {
        start = true;
        manaZoneLayout.FreeNewCoupleSlot();
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
            start = manaZoneLayout.WidgetsInTransitionZone();
        }
    }
}
