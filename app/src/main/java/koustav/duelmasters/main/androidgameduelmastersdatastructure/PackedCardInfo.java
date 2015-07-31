package koustav.duelmasters.main.androidgameduelmastersdatastructure;

import java.security.PublicKey;
import java.util.ArrayList;


/**
 * Created by Koustav on 2/19/2015.
 */
public class PackedCardInfo {
    public ArrayList<String> SlotAttributes;
    public ArrayList<String> FlagAttributes;
    public ArrayList<String> PrimaryInstructionIndex;
    public ArrayList<String> PrimaryInstruction;
    public ArrayList<String> CrossInstructionIndex;
    public ArrayList<String> CrossInstruction;

    public PackedCardInfo (){
        SlotAttributes = new ArrayList<String>();
        FlagAttributes = new ArrayList<String>();
        PrimaryInstructionIndex = new ArrayList<String>();
        PrimaryInstruction = new ArrayList<String>();
        CrossInstructionIndex = new ArrayList<String>();
        CrossInstruction = new ArrayList<String>();
    }



}
