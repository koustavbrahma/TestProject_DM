package koustav.duelmasters.main.androidgameduelmastersutil;

import java.util.Iterator;

import koustav.duelmasters.main.androidgameduelmastersdatastructure.InactiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.World;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Zone;

/**
 * Created by Koustav on 7/18/2015.
 */
public class NetworkUtil {

    public static boolean sendDirectiveUpdates(World world, String Header, String trunk, String tail) {
        String msg;
        msg = new String(Header);
        msg = msg.concat("@");
        if (trunk != null) {
            msg = msg.concat(trunk);
            msg = msg.concat("@");
        }

        if (tail != null) {
            msg = msg.concat(tail);
            msg = msg.concat("@");
        }

        msg = msg.concat("YHS");

        if (world.getGame().getNetwork().getSocket() != null && !world.getGame().getNetwork().getSocket().isClosed()) {
            world.getGame().getNetwork().getOutStream().println(msg);
            return true;
        } else {
            return false;
        }
    }

/*    public static String PackFlagAttributeForDataTransfer(InactiveCard card) {
        int cardzone = card.GridPosition().getZone();
        if (cardzone < 4) {
            cardzone = cardzone +7;
        } else {
            throw new IllegalArgumentException("Invalid zone to pack card flag attr");
        }

        String packData;
        packData = cardzone + " " + card.GridPosition().getGridIndex() + " " + card.getNameID();
        Iterator<String> itr = card.getflagAttributes().FlagAttrIterator();
        String key;
        while (itr.hasNext()) {
            key = itr.next();
            packData = packData.concat(" ");
            int val = card.getflagAttributes().GetAttribute(key);
            packData = packData.concat(key);
            packData = packData.concat(" ");
            packData = packData.concat(Integer.toString(val));
        }
        return packData;
    }

    public static void UnpackFlagAttributeAfterDataTransfer(World world, String packData) {
        String[] splitData = packData.split(" ");
        int Cardzone = Integer.parseInt(splitData[0]);
        int GridIndex = Integer.parseInt(splitData[1]);
        if (Cardzone < 7 || Cardzone > 10) {
            throw new IllegalArgumentException("Invalid zone to unpack card flag attr");
        }

        InactiveCard card = (InactiveCard) world.getGridIndexTrackingTable().getCardMappedToGivenGridPosition(Cardzone,GridIndex);
        if (!card.getNameID().equals(splitData[2]))
            throw new IllegalArgumentException("Data inconsistency");

        card.getflagAttributes().ResetAttribute();
        int c = (splitData.length - 3) % 2;
        if (c != 0)
            throw new IllegalArgumentException("Invalid data to unpack");
        int i = 3;
        while (i < splitData.length) {
            card.getflagAttributes().SetAttribute(splitData[i], Integer.parseInt(splitData[i+1]));
            i = i+2;
        }
    }
*/
    public static String GenerateTappedCardInfo(World world, int zone) {
        if ( zone > 1)
            throw new IllegalArgumentException("Invalid Zone passed");
        int z = zone + 7;
        Zone Azone = world.getMaze().getZoneList().get(zone);
        String msg = new String();
        for (int i = 0; i < Azone.zoneSize(); i++) {
            InactiveCard card = (InactiveCard) Azone.getZoneArray().get(i);
            String tmp = z + " " + card.GridPosition().getGridIndex() + " " + card.getNameID();
            tmp = tmp.concat(" ");
            tmp = tmp.concat("Tapped");
            tmp = tmp.concat(" ");
            if (GetUtil.IsTapped(card))
                tmp = tmp.concat("1");
            else
                tmp = tmp.concat("0");
            msg = msg.concat(tmp);
            if (i < (Azone.zoneSize() - 1))
                msg = msg.concat("#");
        }
        if (msg.length() == 0)
            msg = null;

        return msg;
    }
}
