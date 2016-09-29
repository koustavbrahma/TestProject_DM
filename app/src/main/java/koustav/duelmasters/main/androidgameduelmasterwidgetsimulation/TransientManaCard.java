package koustav.duelmasters.main.androidgameduelmasterwidgetsimulation;

import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.ManaZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;

/**
 * Created by Koustav on 9/26/2016.
 */
public class TransientManaCard implements Simulate {
    ManaZoneLayout manaZoneLayout;
    boolean start;

    public TransientManaCard(ManaZoneLayout manaZoneLayout) {
        this.manaZoneLayout = manaZoneLayout;
        start = false;
    }

    @Override
    public void Start(Object ...obj) {
        start = true;
        manaZoneLayout.AddToTransitionZone((CardWidget) obj[0]);
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
