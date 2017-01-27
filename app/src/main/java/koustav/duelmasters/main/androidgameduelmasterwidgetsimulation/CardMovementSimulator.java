package koustav.duelmasters.main.androidgameduelmasterwidgetsimulation;

import android.os.DropBoxManager;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.PvPWidgetCoordinator;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.BattleZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.HandZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.ManaZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.Layout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.LayoutManager;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;

/**
 * Created by Koustav on 1/21/2017.
 */
public class CardMovementSimulator implements Simulate {

    public class SimulationInfo {
        ArrayList<CardWidget> cardWidgets;
        ArrayList<Layout> layout;
        ArrayList<LogicExpForFinish> logicExpForFinish;
        SimulationUpdate update;

        public SimulationInfo() {
            cardWidgets = new ArrayList<CardWidget>();
            layout = new ArrayList<Layout>();
            logicExpForFinish = new ArrayList<LogicExpForFinish>();
            update = null;
        }
    }

    Hashtable<SimulationID, SimulationInfo> simulationInfos;
    int counter;

    ArrayList<SimulationID> simulationIDRemoveList;

    private interface LogicExpForFinish {
        boolean IsFinish();
    }

    private interface SimulationUpdate {
        void run();
    }

    LayoutManager layoutmanager;
    PvPWidgetCoordinator coordinator;

    public CardMovementSimulator(PvPWidgetCoordinator coordinator) {
        this.layoutmanager = coordinator.getLayoutManager();
        this.coordinator = coordinator;
        simulationInfos = new Hashtable<SimulationID, SimulationInfo>();
        counter = 0;

        simulationIDRemoveList = new ArrayList<SimulationID>();
    }

    @Override
    public SimulationID Start(Object ...obj) {
        if (counter == 100) {
            counter = 0;
        }

        SimulationID ID = null;
        LocationLayout source = (LocationLayout) obj[0];
        LocationLayout destination = (LocationLayout) obj[1];

        if (source == LocationLayout.Hand && destination == LocationLayout.HandPreManaZone) {
            ID = preManaAdd(obj);
        } else if (source == LocationLayout.Hand && destination == LocationLayout.HandPreBattleZone) {
            ID = preSummon(obj);
        } else if (source == LocationLayout.Hand && destination == LocationLayout.ManaZone) {
            ID = directManaAdd(obj);
        } else if (source == LocationLayout.HandPreManaZone && destination == LocationLayout.ManaZone) {
            ID = postManaAdd(obj);
        } else if (source == LocationLayout.HandPreManaZone && destination == LocationLayout.Hand) {
            ID = cancelSummonOrManaAdd(obj);
        } else if (source == LocationLayout.HandPreBattleZone && destination == LocationLayout.Hand) {
            ID = cancelSummonOrManaAdd(obj);
        } else if (source == LocationLayout.Deck && destination == LocationLayout.Hand) {
            ID = drawCardFromDeckToHand(obj);
        } else if (source == LocationLayout.HandPreBattleZone && destination == LocationLayout.BattleZone) {
            ID = summonCreature(obj);
        } else if (source == LocationLayout.ManaNewCoupleZone && destination == LocationLayout.ManaZone) {
            ID = transientNewCoupleMana(obj);
        } else if (source == LocationLayout.ManaZone && destination == LocationLayout.ManaNewCoupleZone) {
            ID = transientManaCard(obj);
        } else {
            throw new RuntimeException("Invalid condition");
        }

        return ID;
    }



    @Override
    public boolean IsFinish(SimulationID ID) {
        SimulationInfo simulationInfo = simulationInfos.get(ID);

        if (simulationInfo == null) {
            return true;
        }
        boolean status = true;

        for (LogicExpForFinish logicExpForFinish: simulationInfo.logicExpForFinish) {
            status &= logicExpForFinish.IsFinish();
        }

        return status;
    }

    @Override
    public void update() {
        simulationIDRemoveList.clear();

        Iterator entries = simulationInfos.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry)entries.next();
            SimulationID ID = (SimulationID) entry.getKey();
            SimulationInfo simulationInfo = (SimulationInfo) entry.getValue();

            simulationInfo.update.run();

            boolean status = true;

            for (LogicExpForFinish logicExpForFinish: simulationInfo.logicExpForFinish) {
                status &= logicExpForFinish.IsFinish();
            }

            if (status) {
                simulationIDRemoveList.add(ID);
            }
        }

        for (SimulationID ID: simulationIDRemoveList) {
            simulationInfos.remove(ID);
        }
    }


    private SimulationID preManaAdd(Object ...obj) {
        CardWidget cardWidget = (CardWidget)coordinator.getWidgetForCard((Cards)obj[2]);
        if (!layoutmanager.handZoneLayout.IsPresent(cardWidget)) {
            throw new RuntimeException("Invalid condition");
        }
        final SimulationInfo simulationInfo = new SimulationInfo();
        counter++;
        SimulationID ID = new SimulationID(counter);

        simulationInfo.layout.add(layoutmanager.handZoneLayout);
        simulationInfo.cardWidgets.add(cardWidget);
        layoutmanager.handZoneLayout.LockCardWidget(cardWidget, 0f, (3f * AssetsAndResource.MazeHeight)/10);
        simulationInfo.logicExpForFinish.add(new LogicExpForFinish() {
            @Override
            public boolean IsFinish() {
                return !((HandZoneLayout)simulationInfo.layout.get(0)).IsWidgetInTransition(simulationInfo.cardWidgets.get(0));
            }
        });

        simulationInfo.update = new SimulationUpdate() {
            @Override
            public void run() {
            }
        };

        simulationInfos.put(ID, simulationInfo);
        return ID;
    }

    private SimulationID preSummon(Object ...obj) {
        CardWidget cardWidget = coordinator.getWidgetForCard((Cards) obj[2]);
        if (!layoutmanager.handZoneLayout.IsPresent(cardWidget)) {
            throw new RuntimeException("Invalid condition");
        }
        final SimulationInfo simulationInfo = new SimulationInfo();
        counter++;
        SimulationID ID = new SimulationID(counter);

        simulationInfo.layout.add(layoutmanager.handZoneLayout);
        simulationInfo.cardWidgets.add(cardWidget);
        layoutmanager.handZoneLayout.LockCardWidget(cardWidget, 0f, AssetsAndResource.MazeHeight/10);
        simulationInfo.logicExpForFinish.add(new LogicExpForFinish() {
            @Override
            public boolean IsFinish() {
                return !((HandZoneLayout)simulationInfo.layout.get(0)).IsWidgetInTransition(simulationInfo.cardWidgets.get(0));
            }
        });

        simulationInfo.update = new SimulationUpdate() {
            @Override
            public void run() {
            }
        };

        simulationInfos.put(ID, simulationInfo);
        return ID;
    }

    private SimulationID directManaAdd(Object ...obj) {
        CardWidget cardWidget = coordinator.getWidgetForCard((Cards) obj[2]);
        if (!layoutmanager.handZoneLayout.IsPresent(cardWidget)) {
            throw new RuntimeException("Invalid condition");
        }
        final SimulationInfo simulationInfo = new SimulationInfo();
        counter++;
        SimulationID ID = new SimulationID(counter);

        simulationInfo.layout.add(layoutmanager.handZoneLayout);
        simulationInfo.layout.add(layoutmanager.manaZoneLayout);
        simulationInfo.cardWidgets.add(cardWidget);
        layoutmanager.handZoneLayout.LockCardWidget(cardWidget, 0f, (3f * AssetsAndResource.MazeHeight)/10);

        simulationInfo.logicExpForFinish.add(new LogicExpForFinish() {
            @Override
            public boolean IsFinish() {
                return !((HandZoneLayout)simulationInfo.layout.get(0)).IsWidgetInTransition(simulationInfo.cardWidgets.get(0));
            }
        });

        simulationInfo.update = new SimulationUpdate() {
            @Override
            public void run() {
                HandZoneLayout handZoneLayout = (HandZoneLayout)simulationInfo.layout.get(0);
                CardWidget cardWidget = simulationInfo.cardWidgets.get(0);
                if (!handZoneLayout.IsWidgetInTransition(cardWidget)) {
                    handZoneLayout.UnlockCardWidget(cardWidget);
                    if (cardWidget != handZoneLayout.RemoveCardWidgetFromZone(cardWidget)) {
                        throw new RuntimeException("Invalid Condition");
                    }
                    ((ManaZoneLayout)simulationInfo.layout.get(1)).AddCardWidgetToZone(cardWidget);
                }
            }
        };
        simulationInfos.put(ID, simulationInfo);
        return ID;
    }

    private SimulationID postManaAdd(Object ...obj) {
        CardWidget cardWidget = coordinator.getWidgetForCard((Cards) obj[2]);
        if (!layoutmanager.handZoneLayout.IsPresent(cardWidget)) {
            throw new RuntimeException("Invalid condition");
        }
        final SimulationInfo simulationInfo = new SimulationInfo();
        counter++;
        SimulationID ID = new SimulationID(counter);

        simulationInfo.layout.add(layoutmanager.handZoneLayout);
        simulationInfo.layout.add(layoutmanager.manaZoneLayout);
        simulationInfo.cardWidgets.add(cardWidget);

        layoutmanager.handZoneLayout.UnlockCardWidget(cardWidget);
        if (cardWidget != layoutmanager.handZoneLayout.RemoveCardWidgetFromZone(cardWidget)) {
            throw new RuntimeException("Invalid Condition");
        }
        layoutmanager.manaZoneLayout.AddCardWidgetToZone(cardWidget);
        simulationInfo.update = new SimulationUpdate() {
            @Override
            public void run() {
            }
        };

        simulationInfos.put(ID, simulationInfo);
        return ID;
    }

    private SimulationID cancelSummonOrManaAdd(Object ...obj) {
        CardWidget cardWidget = coordinator.getWidgetForCard((Cards) obj[2]);
        if (!layoutmanager.handZoneLayout.IsPresent(cardWidget)) {
            throw new RuntimeException("Invalid condition");
        }
        final SimulationInfo simulationInfo = new SimulationInfo();
        counter++;
        SimulationID ID = new SimulationID(counter);

        simulationInfo.layout.add(layoutmanager.handZoneLayout);
        simulationInfo.cardWidgets.add(cardWidget);
        layoutmanager.handZoneLayout.UnlockCardWidget(cardWidget);

        simulationInfo.logicExpForFinish.add(new LogicExpForFinish() {
            @Override
            public boolean IsFinish() {
                return !((HandZoneLayout)simulationInfo.layout.get(0)).IsWidgetInTransition(simulationInfo.cardWidgets.get(0));
            }
        });

        simulationInfo.update = new SimulationUpdate() {
            @Override
            public void run() {
            }
        };

        simulationInfos.put(ID, simulationInfo);
        return ID;
    }

    private SimulationID drawCardFromDeckToHand(Object ...obj) {
        if (obj[2] instanceof Integer) {
            final SimulationInfo simulationInfo = new SimulationInfo();
            counter++;
            SimulationID ID = new SimulationID(counter);

            simulationInfo.layout.add(layoutmanager.handZoneLayout);
            ArrayList<CardWidget> cardWidget = layoutmanager.deckLayout.GenerateCardWidgetForFirstNCard((int) obj[2]);
            for (int i = 0; i < cardWidget.size(); i++) {
                simulationInfo.cardWidgets.add(cardWidget.get(i));
            }
            layoutmanager.handZoneLayout.AddToCardWidgetQueue(simulationInfo.cardWidgets);

            simulationInfo.logicExpForFinish.add(new LogicExpForFinish() {
                @Override
                public boolean IsFinish() {
                    boolean status = true;
                    for (int i = 0; i < simulationInfo.cardWidgets.size(); i++) {
                        status &= !((HandZoneLayout)simulationInfo.layout.get(0)).IsWidgetInTransition(simulationInfo.cardWidgets.get(0));
                    }
                    return status;
                }
            });

            simulationInfo.update = new SimulationUpdate() {
                @Override
                public void run() {
                }
            };

            simulationInfos.put(ID, simulationInfo);
            return ID;
        }

        return null;
    }

    private SimulationID summonCreature(Object ...obj) {
        CardWidget cardWidget = coordinator.getWidgetForCard((Cards) obj[2]);
        if (!layoutmanager.handZoneLayout.IsPresent(cardWidget)) {
            throw new RuntimeException("Invalid condition");
        }
        final SimulationInfo simulationInfo = new SimulationInfo();
        counter++;
        SimulationID ID = new SimulationID(counter);

        simulationInfo.layout.add(layoutmanager.battleZoneLayout);
        simulationInfo.cardWidgets.add(cardWidget);

        layoutmanager.handZoneLayout.UnlockCardWidget(cardWidget);
        if (cardWidget != layoutmanager.handZoneLayout.RemoveCardWidgetFromZone(cardWidget)) {
            throw new RuntimeException("Invalid Condition");
        }
        layoutmanager.battleZoneLayout.AddCardWidgetToZone(cardWidget);

        simulationInfo.logicExpForFinish.add(new LogicExpForFinish() {
            @Override
            public boolean IsFinish() {
                return !((BattleZoneLayout)simulationInfo.layout.get(0)).IsWidgetInTransition(simulationInfo.cardWidgets.get(0));
            }
        });
        simulationInfo.update = new SimulationUpdate() {
            @Override
            public void run() {
            }
        };

        simulationInfos.put(ID, simulationInfo);
        return ID;
    }

    private SimulationID transientNewCoupleMana(Object ...obj) {
        SimulationID ID;
        if (obj.length == 2) {
            ID = cancelNewCoupleCardInMana();
        } else {
            ID = transientManaCard(obj);
        }

        return ID;
    }

    private SimulationID transientManaCard(Object ...obj) {
        if (obj[2] instanceof Cards) {
            CardWidget cardWidget = coordinator.getWidgetForCard((Cards) obj[2]);
            if (!layoutmanager.manaZoneLayout.IsPresent(cardWidget)) {
                throw new RuntimeException("Invalid condition");
            }

            final SimulationInfo simulationInfo = new SimulationInfo();
            counter++;
            SimulationID ID = new SimulationID(counter);

            simulationInfo.layout.add(layoutmanager.manaZoneLayout);
            layoutmanager.manaZoneLayout.AddToTransitionZone(cardWidget);
            simulationInfo.logicExpForFinish.add(new LogicExpForFinish() {
                @Override
                public boolean IsFinish() {
                    return !((ManaZoneLayout)simulationInfo.layout.get(0)).WidgetsInTransitionZone();
                }
            });
            simulationInfo.update = new SimulationUpdate() {
                @Override
                public void run() {
                }
            };
            simulationInfos.put(ID, simulationInfo);
            return ID;
        } else if (obj[2] instanceof ArrayList) {
            ArrayList<Cards> cards = (ArrayList) obj[2];
            for (int i = 0; i < cards.size(); i++) {
                CardWidget cardWidget = coordinator.getWidgetForCard((Cards) cards.get(i));
                if (!layoutmanager.manaZoneLayout.IsPresent(cardWidget)) {
                    throw new RuntimeException("Invalid condition");
                }
                layoutmanager.manaZoneLayout.AddToTransitionZone(cardWidget);
            }
            final SimulationInfo simulationInfo = new SimulationInfo();
            counter++;
            SimulationID ID = new SimulationID(counter);

            simulationInfo.layout.add(layoutmanager.manaZoneLayout);

            simulationInfo.logicExpForFinish.add(new LogicExpForFinish() {
                @Override
                public boolean IsFinish() {
                    return !((ManaZoneLayout)simulationInfo.layout.get(0)).WidgetsInTransitionZone();
                }
            });
            simulationInfo.update = new SimulationUpdate() {
                @Override
                public void run() {
                }
            };
            simulationInfos.put(ID, simulationInfo);
            return ID;
        } else {
            throw new RuntimeException("Invalid condition");
        }
    }

    private SimulationID cancelNewCoupleCardInMana() {
        final SimulationInfo simulationInfo = new SimulationInfo();
        counter++;
        SimulationID ID = new SimulationID(counter);

        simulationInfo.layout.add(layoutmanager.manaZoneLayout);
        layoutmanager.manaZoneLayout.FreeNewCoupleSlot();

        simulationInfo.logicExpForFinish.add(new LogicExpForFinish() {
            @Override
            public boolean IsFinish() {
                return !((ManaZoneLayout)simulationInfo.layout.get(0)).WidgetsInTransitionZone();
            }
        });
        simulationInfo.update = new SimulationUpdate() {
            @Override
            public void run() {
            }
        };
        simulationInfos.put(ID, simulationInfo);
        return ID;
    }
}
