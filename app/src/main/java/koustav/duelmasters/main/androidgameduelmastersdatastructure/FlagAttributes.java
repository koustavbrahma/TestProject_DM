package koustav.duelmasters.main.androidgameduelmastersdatastructure;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Koustav on 2/22/2015.
 */
public class FlagAttributes {
    Hashtable<String, Integer> flags;

    public FlagAttributes() {
        flags = new Hashtable<String , Integer>();
    }

    public Integer SetAttribute (String attr, int value) {
        return flags.put(new String(attr), new Integer(value));
    }

    public int GetAttribute (String attr) {
        if (flags.get(attr) == null)
            return 0;
        return flags.get(attr).intValue();
    }

    public Integer ClearAttribute (String attr) {
        if (flags.get(attr) == null)
            return new Integer(0);
        return flags.remove(attr);
    }

    public boolean IsEmptyAttribute() {
        return flags.isEmpty();
    }

    public int SizeAttribute() {
        return flags.size();
    }

    public void ResetAttribute() {
        flags.clear();
    }

    public Iterator<String> FlagAttrIterator() {
        return flags.keySet().iterator();
    }
}
