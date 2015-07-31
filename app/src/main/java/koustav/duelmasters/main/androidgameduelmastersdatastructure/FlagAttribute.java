package koustav.duelmasters.main.androidgameduelmastersdatastructure;

/**
 * Created by Koustav on 4/15/2015.
 */
public class FlagAttribute {
    String Flag;
    int Value;

    public FlagAttribute(String Flag, int value) {
        this.Flag = new String(Flag);
        this.Value = value;
    }

    public String getFlag() {
        return Flag;
    }

    public int getValue() {
        return Value;
    }

    public void setValue(int val) {
        Value = val;
    }
}
