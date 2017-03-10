package koustav.duelmasters.main.androidgameduelmastersutil;

/**
 * Created by Koustav on 5/12/2015.
 */
public class InstSetUtil {
    public static String GenerateSelfChangeZoneInstruction(int zone) {
        String instructionString = "3 0 0 0 0 0 0 0 0 " + zone +" 0 0 0 0 0 0 0 0";
        return instructionString;
    }

    public static String GenerateDrawCardInstruction() {
        String instructionString = "24 100000 1 0 0 0 0 0 0 3 0 0 0 0 0 0 0 0";
        return instructionString;
    }

    public static String GenerateAttributeCleanUpInstruction(int ActionZone, String Attribute, int val) {
        String instructionString = "9 " + ActionZone + " 0 1 6 " + Attribute + " 0 0 0 0 0 " + Attribute + " " +val +" 0 0 0 0 0";
        return instructionString;
    }

    public static String GenerateAttributeCleanUpBasedOnOtherAttributeInstruction(int ActionZone, String OtherAttribute,
                                                                                  String Attribute, int val) {
        String instructionString = "9 " + ActionZone + " 0 1 6 " + OtherAttribute + " 0 0 0 0 0 " + Attribute + " " +val +" 0 0 0 0 0";
        return instructionString;
    }

    public static String GenerateAttributeCleanUpBasedOnNotOfOtherAttributeInstruction(int ActionZone, String OtherAttribute,
                                                                                       String Attribute, int val) {
        String instructionString = "9 " + ActionZone +" 0 1 7 " + OtherAttribute+ " 0 0 0 0 0 " + Attribute+ " " + val +" 0 0 0 0 0";
        return instructionString;
    }

    public static String GenerateSelfSetAttributeInstruction(String Attribute, int val){
        String instructionString = "12 0 0 0 0 0 0 0 0 0 0 " + Attribute + " " + val + " 0 0 0 0 0";
        return instructionString;
    }

    public static String GenerateSelfCleanUpAttributeInstruction(String Attribute, int val) {
        String instructionString = "8 0 0 0 0 0 0 0 0 0 0 " + Attribute + " " + val + " 0 0 0 0 0";
        return instructionString;
    }

    public static String GenerateChangeZoneInstructionBasedOnNameId(int ActionZone, String nameId, int zone) {
        String instructionString = "2 " + ActionZone + " 0 1 11 " + nameId + " 0 0 1 " + zone + " 0 0 0 0 0 0 0 0";
        return instructionString;
    }

    public static String GenerateEvolutionInstruction(String RaceSubString) {
        String instructionString = "1 1 0 1 12 " + RaceSubString + " 0 0 5 0 0 0 0 0 0 0 0 0";
        return instructionString;
    }

    public static String GenerateCopyCardToTempZoneBasedOnAttribute(String Attribute, int ActionZone) {
        String instructionString = "13 " + ActionZone + " 0 1 6 " + Attribute + " 0 0 0 0 0 0 0 0 0 0 0 0";
        return instructionString;
    }

    public static String GenerateSetAttributeBasedOnPresenceOfOtherAttribute(String OtherAttribute, String Attribute, int val,
                                                                             int ActionZone) {
        String instructionString = "14 " + ActionZone +" 0 1 6 " + OtherAttribute+ " 0 0 0 0 0 " + Attribute+ " " + val +" 0 0 0 0 0";
        return instructionString;
    }

    public static String GenerateSetAttributeOnRandomSelectedCardInstruction(int ActionZone, String Attribute, int val, int Count) {
        String instructionString = "17 " + ActionZone + " " + Count +" 0 0 0 0 0 2 0 0 " + Attribute+ " " + val +" 0 0 0 0 0";
        return instructionString;
    }
}
