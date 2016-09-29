package koustav.duelmasters.main.androidgameduelmasterwidgetsimulation;

import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.BattleZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.HandZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;

/**
 * Created by Koustav on 9/27/2016.
 */
public class SummonCreatureCard implements Simulate {
    HandZoneLayout handZoneLayout;
    BattleZoneLayout battleZoneLayout;
    CardWidget cardWidget;
    boolean start;

    public SummonCreatureCard(HandZoneLayout handZoneLayout, BattleZoneLayout battleZoneLayout) {
        this.handZoneLayout = handZoneLayout;
        this.battleZoneLayout = battleZoneLayout;
        start = false;
    }

    @Override
    public void Start(Object ...obj) {
        start = true;
        cardWidget = (CardWidget) obj[0];
        handZoneLayout.UnlockCardWidget((CardWidget) obj[0]);
        if (obj[0] != handZoneLayout.RemoveCardWidgetFromZone((CardWidget) obj[0])) {
            throw new RuntimeException("Invalid Condition");
        }
        battleZoneLayout.AddCardWidgetToZone((CardWidget) obj[0]);
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
            start = battleZoneLayout.IsWidgetInTransition(cardWidget);
        }
    }
}
