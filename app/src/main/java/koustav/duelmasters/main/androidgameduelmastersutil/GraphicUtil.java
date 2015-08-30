package koustav.duelmasters.main.androidgameduelmastersutil;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Iterator;

import koustav.duelmasters.main.androidgameduelmastersassets.Assets;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.InactiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.World;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.WorldFlags;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Zone;
import koustav.duelmasters.main.androidgamesframework.Graphics;

/**
 * Created by Koustav on 5/19/2015.
 */
public class GraphicUtil {
    public static boolean BlockPresent(World world) {
        if (world.getWorldFlag(WorldFlags.ManaSelectMode) || world.getWorldFlag(WorldFlags.ShieldSelectMode) ||
                world.getWorldFlag(WorldFlags.AttackSelectMode) || world.getWorldFlag(WorldFlags.SilentSkillMode) ||
                world.getWorldFlag(WorldFlags.CardSelectingMode) || world.getWorldFlag(WorldFlags.BlockerSelectMode)||
                world.getWorldFlag(WorldFlags.ShieldTriggerMode) || world.getWorldFlag(WorldFlags.ShieldTriggerFound) ||
                !world.getTurn())
            return true;
        else
            return false;
    }
    public static void presentControlButtons(World world) {
        if (BlockPresent(world)) {
            return;
        }

        Graphics g = world.getGame().getGraphics();
        int w = world.getframeBufferWidht()/8;
        int h = world.getframeBufferHeight()/10;

        g.drawPixmap(Assets.Button, w *7, h *5);

        if ((world.getMaze().getZoneList().get(5).zoneSize() > 0) &&
                !world.getWorldFlag(WorldFlags.CantDrawCard)) {
            g.drawPixmap(Assets.Button, w *6, h *5);
        }

        boolean status = false;
        InactiveCard card = (InactiveCard) world.getFetchCard();
        if (card == null) {
            return;
        }
        int zone = card.GridPosition().getZone();
        if (zone != 0 && zone != 3) {
            return;
        }

        if (zone == 0) {
            if (!GetUtil.IsTapped(card)) {
                if (GetUtil.IsAllowedToAttack(card, world)) {
                    status = true;
                }
            } else if (GetUtil.EachTurnIfAble(card)) {
                if (GetUtil.IsAllowedToAttack(card, world)) {
                    status = true;
                }
            }
        }

        if (zone == 3) {
            if (GetUtil.IsAllowedToSummonOrCast(card, world)) {
                status = true;
            }
        }

        if (status){
            g.drawPixmap(Assets.Button, 0, h * 5);
        }

        status = false;

        if (zone == 0) {
            if (!GetUtil.IsTapped(card)) {
                if (GetUtil.IsHasTapAbility(card)) {
                    status =true;
                }
            }
        }

        if (zone == 3) {
            if (!world.getWorldFlag(WorldFlags.CantAddToMana)) {
                status = true;
            }
        }

        if (status) {
            g.drawPixmap(Assets.Button, w * 2 , h * 5);
        }
    }

    public static void presentYourCards(World world) {
        Graphics g = world.getGame().getGraphics();
        int w = world.getframeBufferWidht()/8;
        int h = world.getframeBufferHeight()/10;
        if (world.getMaze().getZoneList().get(5).zoneSize() > 0) {
            g.drawPixmap(Assets.cardbackside,(6 * w), (7 * h) );
        }

        if (world.getMaze().getZoneList().get(4).zoneSize() > 0) {
            g.drawPixmap(Assets.cardbackside, (7 * w), (7 * h));
        }

        if (world.getMaze().getZoneList().get(0).zoneSize() > 0) {
            Zone zone = world.getMaze().getZoneList().get(0);
            InactiveCard card;
            Integer x, y;
            for (int i = 0; i < zone.zoneSize(); i++) {
                card = (InactiveCard) zone.getZoneArray().get(i);
                y = UIUtil.ZoneIndexToYCoordinateTransform(0, world.getframeBufferHeight()) + 4;
                x = UIUtil.GridPositionToXCoordinateTransform(card.GridPosition().getGridIndex(),
                        world.getframeBufferWidht()) + 6;
                if (y != null && x != null) {
                    if (!GetUtil.IsTapped(card)) {
                        g.drawPixmap(Assets.cardbackside, x.intValue(), y.intValue());
                    } else {
                        g.drawPixmap(Assets.cardbackside, x.intValue(), y.intValue(), 90);
                    }
                }
            }
        }

        if (world.getMaze().getZoneList().get(1).zoneSize() > 0) {
            Zone zone = world.getMaze().getZoneList().get(1);
            InactiveCard card;
            Integer x, y;
            for (int i = 0; i < zone.zoneSize(); i++) {
                card = (InactiveCard) zone.getZoneArray().get(i);
                y = UIUtil.ZoneIndexToYCoordinateTransform(1, world.getframeBufferHeight()) + 4;
                x = UIUtil.GridPositionToXCoordinateTransform(card.GridPosition().getGridIndex(),
                        world.getframeBufferWidht()) + 6;
                if (y != null && x != null) {
                    if (!GetUtil.IsTapped(card)) {
                        g.drawPixmap(Assets.cardbackside, x.intValue(), y.intValue());
                    } else {
                        g.drawPixmap(Assets.cardbackside, x.intValue(), y.intValue(), 90);
                    }
                }
            }
        }

        if (world.getMaze().getZoneList().get(3).zoneSize() > 0) {
            Zone zone = world.getMaze().getZoneList().get(3);
            Cards card;
            Integer x, y;
            for (int i = 0; i < zone.zoneSize(); i++) {
                card = zone.getZoneArray().get(i);
                y = UIUtil.ZoneIndexToYCoordinateTransform(3, world.getframeBufferHeight()) + 4;
                x = UIUtil.GridPositionToXCoordinateTransform(card.GridPosition().getGridIndex(),
                        world.getframeBufferWidht()) + 6;
                if (y != null && x != null) {
                    g.drawPixmap(Assets.cardbackside, x.intValue(), y.intValue());
                }
            }
        }

        if (world.getMaze().getZoneList().get(2).zoneSize() > 0) {
            Zone zone = world.getMaze().getZoneList().get(2);
            Cards card;
            Integer x, y;
            for (int i = 0; i < zone.zoneSize(); i++) {
                card = zone.getZoneArray().get(i);
                y = UIUtil.ZoneIndexToYCoordinateTransform(2, world.getframeBufferHeight()) + 4;
                x = UIUtil.GridPositionToXCoordinateTransform(card.GridPosition().getGridIndex(),
                        world.getframeBufferWidht()) + 6;
                if (x == null || x > (5 * w))
                    continue;
                if (y != null ) {
                    g.drawPixmap(Assets.cardbackside, x.intValue(), y.intValue());
                }
            }
        }
    }

    public static void presentOpponentCards(World world) {
        Graphics g = world.getGame().getGraphics();
        int w = world.getframeBufferWidht()/8;
        int h = world.getframeBufferHeight()/10;
        if (world.getMaze().getZoneList().get(12).zoneSize() > 0) {
            g.drawPixmap(Assets.cardbackside,(1 * w), (3 * h), 180 );
        }

        if (world.getMaze().getZoneList().get(11).zoneSize() > 0) {
            g.drawPixmap(Assets.cardbackside, 0, (3 * h), 180);
        }

        if (world.getMaze().getZoneList().get(7).zoneSize() > 0) {
            Zone zone = world.getMaze().getZoneList().get(7);
            InactiveCard card;
            Integer x, y;
            for (int i = 0; i < zone.zoneSize(); i++) {
                card = (InactiveCard) zone.getZoneArray().get(i);
                y = UIUtil.ZoneIndexToYCoordinateTransform(7, world.getframeBufferHeight()) + 4;
                x = UIUtil.GridPositionToXCoordinateTransform((7 - card.GridPosition().getGridIndex()),
                        world.getframeBufferWidht()) + 6;
                if (y != null && x != null) {
                    if (!GetUtil.IsTapped(card)) {
                        g.drawPixmap(Assets.cardbackside, x.intValue(), y.intValue(), 180);
                    } else {
                        g.drawPixmap(Assets.cardbackside, x.intValue(), y.intValue(), 270);
                    }
                }
            }
        }

        if (world.getMaze().getZoneList().get(8).zoneSize() > 0) {
            Zone zone = world.getMaze().getZoneList().get(8);
            InactiveCard card;
            Integer x, y;
            for (int i = 0; i < zone.zoneSize(); i++) {
                card = (InactiveCard) zone.getZoneArray().get(i);
                y = UIUtil.ZoneIndexToYCoordinateTransform(8, world.getframeBufferHeight()) + 4;
                x = UIUtil.GridPositionToXCoordinateTransform((7 - card.GridPosition().getGridIndex()),
                        world.getframeBufferWidht()) + 6;
                if (y != null && x != null) {
                    if (!GetUtil.IsTapped(card)) {
                        g.drawPixmap(Assets.cardbackside, x.intValue(), y.intValue(), 180);
                    } else {
                        g.drawPixmap(Assets.cardbackside, x.intValue(), y.intValue(), 270);
                    }
                }
            }
        }

        if (world.getMaze().getZoneList().get(10).zoneSize() > 0) {
            Zone zone = world.getMaze().getZoneList().get(10);
            Cards card;
            Integer x, y;
            for (int i = 0; i < zone.zoneSize(); i++) {
                card = zone.getZoneArray().get(i);
                y = UIUtil.ZoneIndexToYCoordinateTransform(10, world.getframeBufferHeight()) + 4;
                x = UIUtil.GridPositionToXCoordinateTransform((7 - card.GridPosition().getGridIndex()),
                        world.getframeBufferWidht()) + 6;
                if (y != null && x != null) {
                    g.drawPixmap(Assets.cardbackside, x.intValue(), y.intValue(), 180);
                }
            }
        }

        if (world.getMaze().getZoneList().get(9).zoneSize() > 0) {
            Zone zone = world.getMaze().getZoneList().get(9);
            Cards card;
            Integer x, y;
            for (int i = 0; i < zone.zoneSize(); i++) {
                card = zone.getZoneArray().get(i);
                y = UIUtil.ZoneIndexToYCoordinateTransform(9, world.getframeBufferHeight()) + 4;
                x = UIUtil.GridPositionToXCoordinateTransform((7 - card.GridPosition().getGridIndex()),
                        world.getframeBufferWidht()) + 6;
                if (x == null || x < (2 * w))
                    continue;
                if (y != null ) {
                    g.drawPixmap(Assets.cardbackside, x.intValue(), y.intValue(), 180);
                }
            }
        }
    }

    public static void presentInfoTab(World world) {
        if (BlockPresent(world)) {
            return;
        }
        Graphics g = world.getGame().getGraphics();
        int w = world.getframeBufferWidht()/8;
        int h = world.getframeBufferHeight()/10;
        if (world.getFetchCard() != null) {
            g.drawPixmap(Assets.Button, w * 4, h * 5);
        }

        if (world.getWorldFlag(WorldFlags.DisplayInfo)) {
            g.drawPixmap(Assets.Button, w * 7, 0);
            g.drawPixmap(Assets.InfoBackground, 0, h, 0, 0, 320, 432);
            InactiveCard card = (InactiveCard) world.getFetchCard();
            int h1 = h+8;
            g.drawText(card.getNameID(),0, h1, 8, Color.BLACK);
            Iterator<String> iterator = card.getflagAttributes().FlagAttrIterator();
            while (iterator.hasNext()) {
                String  flagattr = iterator.next();
                String attrAndvalue = flagattr + " " + card.getflagAttributes().GetAttribute(flagattr);
                h1 = h1 + 10;
                g.drawText(attrAndvalue,0, h1, 8, Color.BLACK);
            }
        }
    }

    public static void presentHighlightManaCard(World world) {
        if (!world.getWorldFlag(WorldFlags.ManaSelectMode)) {
            return;
        }
        Graphics g = world.getGame().getGraphics();
        int w = world.getframeBufferWidht()/8;
        g.drawPixmap(Assets.Button, w *4, 0);
        InactiveCard Icard = (InactiveCard) world.getFetchCard();
        if (world.getMaze().getZoneList().get(6).zoneSize() == GetUtil.getTotalManaCost(Icard)) {
            g.drawPixmap(Assets.Button, w *2, 0);
        }
        if (world.getMaze().getZoneList().get(6).zoneSize() > 0) {
            Zone zone = world.getMaze().getZoneList().get(6);
            Cards card;
            Integer x, y;
            for (int i = 0; i < zone.zoneSize(); i++) {
                card = zone.getZoneArray().get(i);
                y = UIUtil.ZoneIndexToYCoordinateTransform(card.GridPosition().getZone(), world.getframeBufferHeight());
                x = UIUtil.GridPositionToXCoordinateTransform((card.GridPosition().getGridIndex()),
                        world.getframeBufferWidht());
                if (x != null && y != null) {
                    g.drawRect(x,y,4,4, Color.BLACK);
                }
            }
        }
    }

    public static void presentAttackedCard(World world) {
        if (!world.getWorldFlag(WorldFlags.ShieldSelectMode) && !world.getWorldFlag(WorldFlags.AttackSelectMode)) {
            return;
        }
        Graphics g = world.getGame().getGraphics();
        int w = world.getframeBufferWidht()/8;
        g.drawPixmap(Assets.Button, w *4, 0);
        if (!world.getWorldFlag(WorldFlags.ShieldSelectMode)) {
            g.drawPixmap(Assets.Button, w * 6, 0);
        }
        InactiveCard Icard = (InactiveCard) world.getFetchCard();
        ArrayList<Cards> CollectedCardList = world.getMaze().getZoneList().get(6).getZoneArray();
        ArrayList<Cards> ShieldCards = world.getMaze().getZoneList().get(9).getZoneArray();
        int NumberOfBreakingShield = (GetUtil.Breaker(Icard) > ShieldCards.size()) ? ShieldCards.size()
                : GetUtil.Breaker(Icard);
        if ((CollectedCardList.size() == 1 &&((InactiveCard) CollectedCardList.get(0)).GridPosition().getZone() == 7)
                || (NumberOfBreakingShield != 0 && CollectedCardList.size() == NumberOfBreakingShield
                    && CollectedCardList.get(0).GridPosition().getZone() ==  9)) {
            g.drawPixmap(Assets.Button, w *2, 0);
        }
        if (CollectedCardList.size() > 0) {
            Cards card;
            Integer x, y;
            for (int i = 0; i < CollectedCardList.size(); i++) {
                card = CollectedCardList.get(i);
                y = UIUtil.ZoneIndexToYCoordinateTransform(card.GridPosition().getZone(), world.getframeBufferHeight());
                x = UIUtil.GridPositionToXCoordinateTransform((card.GridPosition().getGridIndex()),
                        world.getframeBufferWidht());
                if (x != null && y != null) {
                    int x2 = world.getframeBufferWidht() - w - x;
                    g.drawRect(x2,y,4,4, Color.BLACK);
                }
            }
        }
    }

    public static void presentSilentSkillOrShieldTrigger(World world) {
        if (!world.getWorldFlag(WorldFlags.SilentSkillMode) && !world.getWorldFlag(WorldFlags.ShieldTriggerMode))
            return;

        Graphics g = world.getGame().getGraphics();
        int w = world.getframeBufferWidht()/8;
        g.drawPixmap(Assets.Button, w * 4, 0);
        g.drawPixmap(Assets.Button, w * 2, 0);
    }

    public static void presentCardSelectMode(World world) {
        if (!world.getWorldFlag(WorldFlags.CardSelectingMode))
            return;

        Graphics g = world.getGame().getGraphics();
        int w = world.getframeBufferWidht()/8;
        ArrayList<Cards> CollectedCardList = world.getMaze().getZoneList().get(6).getZoneArray();
        if (world.getWorldFlag(WorldFlags.MaySkipCardSelectingMode)) {
            g.drawPixmap(Assets.Button, w * 4, 0);
        }
        if (world.getWorldFlag(WorldFlags.AcceptCardSelectingMode)) {
            g.drawPixmap(Assets.Button, w * 2, 0);
        }

        if (CollectedCardList.size() > 0) {
            Cards card;
            Integer x, y;
            for (int i = 0; i < CollectedCardList.size(); i++) {
                card = CollectedCardList.get(i);
                y = UIUtil.ZoneIndexToYCoordinateTransform(card.GridPosition().getZone(), world.getframeBufferHeight());
                x = UIUtil.GridPositionToXCoordinateTransform((card.GridPosition().getGridIndex()),
                        world.getframeBufferWidht());
                if (x != null && y != null) {
                    if (card.GridPosition().getZone() > 6) {
                        int x2 = world.getframeBufferWidht() - w - x;
                        g.drawRect(x2, y, 4, 4, Color.BLACK);
                    } else if (card.GridPosition().getZone() < 6) {
                        g.drawRect(x, y, 4, 4, Color.BLACK);
                    }
                }
            }
        }
    }

    public static void presentBlockerSelect(World world) {
        if (!world.getWorldFlag(WorldFlags.BlockerSelectMode) || world.getTurn())
            return;

        Graphics g = world.getGame().getGraphics();
        int w = world.getframeBufferWidht()/8;
        ArrayList<Cards> CollectedCardList = world.getMaze().getZoneList().get(6).getZoneArray();
        g.drawPixmap(Assets.Button, w * 6, 0);

        if (CollectedCardList.size() == 1) {
            g.drawPixmap(Assets.Button, w * 2, 0);
        }

        if (CollectedCardList.size() > 0) {
            Cards card;
            Integer x, y;
            for (int i = 0; i < CollectedCardList.size(); i++) {
                card = CollectedCardList.get(i);
                y = UIUtil.ZoneIndexToYCoordinateTransform(card.GridPosition().getZone(), world.getframeBufferHeight());
                x = UIUtil.GridPositionToXCoordinateTransform((card.GridPosition().getGridIndex()),
                        world.getframeBufferWidht());
                if (x != null && y != null) {
                    if (card.GridPosition().getZone() > 6) {
                        int x2 = world.getframeBufferWidht() - w - x;
                        g.drawRect(x2, y, 4, 4, Color.BLACK);
                    } else if (card.GridPosition().getZone() < 6) {
                        g.drawRect(x, y, 4, 4, Color.BLACK);
                    }
                }
            }
        }
    }

    public static void presentCardInfoUserSelect(World world) {
        if (!world.getWorldFlag(WorldFlags.DisplayInfoUserSelect)) {
            return;
        }

        Graphics g = world.getGame().getGraphics();
        int w = world.getframeBufferWidht()/8;
        int h = world.getframeBufferHeight()/10;
        g.drawPixmap(Assets.Button, w * 7, 0);
        g.drawPixmap(Assets.Button, w * 4, 0);
        g.drawPixmap(Assets.Button, w * 2, 0);
        g.drawPixmap(Assets.InfoBackground, 0, h, 0, 0, 320, 432);
        ArrayList<Cards> TempZone = world.getMaze().getZoneList().get(6).getZoneArray();
        InactiveCard card = (InactiveCard) TempZone.get(0);
        int h1 = h+8;
        g.drawText(card.getNameID(),0, h1, 8, Color.BLACK);
        Iterator<String> iterator = card.getflagAttributes().FlagAttrIterator();
        while (iterator.hasNext()) {
            String  flagattr = iterator.next();
            String attrAndvalue = flagattr + " " + card.getflagAttributes().GetAttribute(flagattr);
            h1 = h1 + 10;
            g.drawText(attrAndvalue,0, h1, 8, Color.BLACK);
        }
    }
}
