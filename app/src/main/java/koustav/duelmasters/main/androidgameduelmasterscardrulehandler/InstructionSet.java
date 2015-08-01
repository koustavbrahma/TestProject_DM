package koustav.duelmasters.main.androidgameduelmasterscardrulehandler;

import android.graphics.Color;
import android.widget.Switch;

import koustav.duelmasters.main.androidgameduelmastersdatastructure.FlagAttribute;
import koustav.duelmasters.main.androidgameduelmastersutil.NetworkUtil;

/**
 * Created by Koustav on 4/13/2015.
 */
public class InstructionSet {
    InstructionType type;
    int[] ActionZone;
    int Count;
    int ConditionCount;
    Condition condition;
    Action action;
    int ChangeDestination;
    int AttrCountOrIndex;
    FlagAttribute Attr;
    CleanUpPlacement cleanUpPlacement;
    int CleanUpIndex;
    CascadeType CascadeCondition;
    int CascadeIndex;
    boolean Result;

    InstructionSet NextInst;

    public InstructionSet(String InstructionString) {
        String Temp = new String(InstructionString);
        String[] InstructionArray = Temp.split(" ");
        if (InstructionArray.length != 18) {
            throw new IllegalArgumentException("Invalid Instruction");
        } else {
            switch (InstructionArray[0]) {
                case "1":
                    type = InstructionType.Choose;
                    break;
                case "2":
                    type = InstructionType.ChooseFromBegin;
                    break;
                case "3":
                    type = InstructionType.SelfChangeZone;
                    break;
                case "4":
                    type = InstructionType.ChangeZoneAll;
                    break;
                case "5":
                    type = InstructionType.SelfBooster;
                    break;
                case "6":
                    type = InstructionType.SelfBoosterMultiplier;
                    break;
                case "7":
                    type = InstructionType.Aura;
                    break;
                case "8":
                    type = InstructionType.SelfCleanUp;
                    break;
                case "9":
                    type = InstructionType.CleanUp;
                    break;
                case "10":
                    type = InstructionType.Shuffle;
                    break;
                case "11":
                    type = InstructionType.MayChoose;
                    break;
                case "12":
                    type = InstructionType.SelfSetAttr;
                    break;
                case "13":
                    type = InstructionType.Copy;
                    break;
                case "14":
                    type = InstructionType.SetAttr;
                    break;
                case "15":
                    type = InstructionType.SetTempSpreadInst;
                    break;
                case "16":
                    type = InstructionType.SelfCleanUpMultiplier;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid instruction type");
            }

            try {
                ActionZone = new int[7];
                int val = Integer.parseInt(InstructionArray[1]);
                for (int i = 0; i < 7; i++) {
                    ActionZone[i] = val % 10;
                    val = val / 10;
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid Actionzone field");
            }

            try {
                Count = Integer.parseInt(InstructionArray[2]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid Count field");
            }

            try {
                ConditionCount = Integer.parseInt(InstructionArray[3]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid ConditionCount field");
            }

            try {
                int lpower = Integer.parseInt(InstructionArray[6]);
                int upower = Integer.parseInt(InstructionArray[7]);
                if (upower == 0)
                    upower = -1;
                if (upower != -1 && upower < lpower )
                    throw new IllegalArgumentException("upper power cannot be less than lower power");
                switch (InstructionArray[4]) {
                    case "0":
                        condition = new Condition(ConditionType.Nil, InstructionArray[5], lpower, upower);
                        break;
                    case "1":
                        condition = new Condition(ConditionType.Civilization, InstructionArray[5], lpower, upower);
                        break;
                    case "2":
                        condition = new Condition(ConditionType.CivilizationFromTmpCard, InstructionArray[5], lpower, upower);
                        break;
                    case "3":
                        condition = new Condition(ConditionType.Race, InstructionArray[5], lpower, upower);
                        break;
                    case "4":
                        condition = new Condition(ConditionType.RaceFromTmpCard, InstructionArray[5], lpower, upower);
                        break;
                    case "5":
                        condition = new Condition(ConditionType.TypeOfCard, InstructionArray[5], lpower, upower);
                        break;
                    case "6":
                        condition = new Condition(ConditionType.Attribute, InstructionArray[5], lpower, upower);
                        break;
                    case "7":
                        condition = new Condition(ConditionType.NotOfAttribute, InstructionArray[5], lpower, upower);
                        break;
                    case "8":
                        condition = new Condition(ConditionType.Power, InstructionArray[5], lpower, upower);
                        break;
                    case "9":
                        condition = new Condition(ConditionType.WorldFlag, InstructionArray[5], lpower, upower);
                        break;
                    case "10":
                        condition = new Condition(ConditionType.CollectCardEqualToTempZoneCount, InstructionArray[5], lpower, upower);
                        break;
                    case "11":
                        condition = new Condition(ConditionType.NameId, InstructionArray[5], lpower, upower);
                        break;
                    case "12":
                        condition = new Condition(ConditionType.RaceContainSubStringForEvolutionOrFlagSpread, InstructionArray[5], lpower, upower);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid condition type");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid power value");
            }


            switch (InstructionArray[8]) {
                case "0":
                    action = Action.Nil;
                    break;
                case "1":
                    action = Action.Move;
                    break;
                case "2":
                    action = Action.SetAttr;
                    break;
                case "3":
                    action = Action.Copy;
                    break;
                case "4":
                    action = Action.Show;
                    break;
                case "5":
                    action = Action.Evolution;
                    break;
                case "6":
                    action = Action.SetTmpSpreadInst;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid Action type");
            }

            try {
                ChangeDestination = Integer.parseInt(InstructionArray[9]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid Destination");
            }

            try {
                AttrCountOrIndex = Integer.parseInt(InstructionArray[10]);
                int val = Integer.parseInt(InstructionArray[12]);
                Attr = new FlagAttribute(InstructionArray[11], val);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid Attribute setting");
            }

            switch (InstructionArray[13]) {
                case "0":
                    cleanUpPlacement = CleanUpPlacement.Nil;
                    break;
                case "1":
                    cleanUpPlacement = CleanUpPlacement.PostCleanUp;
                    break;
                case "2":
                    cleanUpPlacement = CleanUpPlacement.PreCleanUp;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid Cleanup placement");
            }

            try {
                CleanUpIndex = Integer.parseInt(InstructionArray[14]) - 1;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid cleanup index");
            }

             switch (InstructionArray[15]) {
                 case "0":
                     CascadeCondition = CascadeType.Nil;
                     break;
                 case "1":
                     CascadeCondition = CascadeType.IfTempZoneIsNonEmptyWithLessValue;
                     break;
                 case "2":
                     CascadeCondition = CascadeType.IfTempZoneIsEmpty;
                     break;
                 case "3":
                     CascadeCondition = CascadeType.AlwaysCascade;
                     break;
                 case "4":
                     CascadeCondition = CascadeType.IfTempZoneIsNonEmptyWithMoreValue;
                     break;
                 default:
                     throw new IllegalArgumentException("Invalid Cascade type");
            }

            try {
                CascadeIndex = Integer.parseInt(InstructionArray[16]) - 1;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid cascade index");
            }

            if (InstructionArray[17].equals("0"))
                Result = false;
            else
                Result = true;
        }

        NextInst = null;
    }

    public InstructionType getInstructionType(){
        return type;
    }

    public int[] getActionZone() {
        return ActionZone;
    }

    public int getCount(){
        return Count;
    }

    public int getConditionCount() {
        return ConditionCount;
    }

    public Condition getCondition() {
        return condition;
    }

    public Action getAction() {
        return action;
    }

    public FlagAttribute getAttr() {
        return Attr;
    }

    public int getActionDestination() {
        return ChangeDestination;
    }

    public int getAttrCountOrIndex() {
        return AttrCountOrIndex;
    }

    public void setAttrCountOrIndex (int val) {
        AttrCountOrIndex = val;
    }

    public boolean getResult() {
        return Result;
    }

    public CleanUpPlacement getCleanUpPlacement() {
        return cleanUpPlacement;
    }

    public int getCleanUpIndex() {
        return CleanUpIndex;
    }

    public CascadeType getCascadeCondition() {
        return CascadeCondition;
    }

    public int getCascadeIndex() {
        return CascadeIndex;
    }

    public void setResult(boolean val) {
        Result = val;
    }

    public void setNextInst(InstructionSet inst) {
        NextInst = inst;
    }

    public InstructionSet getNextInst() {
        return NextInst;
    }
}
