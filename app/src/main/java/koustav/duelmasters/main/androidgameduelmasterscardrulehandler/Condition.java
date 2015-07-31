package koustav.duelmasters.main.androidgameduelmasterscardrulehandler;

/**
 * Created by Koustav on 4/14/2015.
 */
public class Condition {
    ConditionType type;
    String Value;
    int LowerPower;
    int UpperPower;

    public Condition(ConditionType type, String Value, int LowerPower, int UpperPower) {
        this.type = type;
        this.Value = Value;
        this.LowerPower = LowerPower;
        this.UpperPower = UpperPower;
    }

    public ConditionType getConditionType() {
        return type;
    }

    public String getValue() {
        return Value;
    }

    public int getLowerPower() {
        return LowerPower;
    }

    public int getUpperPower() {
        return UpperPower;
    }

}
