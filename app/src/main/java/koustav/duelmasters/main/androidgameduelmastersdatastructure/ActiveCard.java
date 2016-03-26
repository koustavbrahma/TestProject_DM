package koustav.duelmasters.main.androidgameduelmastersdatastructure;

import java.util.ArrayList;
import java.util.Hashtable;

import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionSet;
import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionType;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.GridPositionIndex;

/**
 * Created by Koustav on 2/19/2015.
 */
public class ActiveCard extends InactiveCard {
    ArrayList<InstructionSet> PrimaryInstructions;
    Hashtable<String, ArrayList<Integer>> PrimaryInstructionTrackingTable;
    ArrayList<String> PassControlCache;


    public ActiveCard (PackedCardInfo cardinfo, GridPositionIndex GridPosition) {
        super(cardinfo, GridPosition);
        PrimaryInstructions = new ArrayList<InstructionSet>();
        PrimaryInstructionTrackingTable = new Hashtable<String, ArrayList<Integer>>();
        PassControlCache = new ArrayList<String>();
        unpackInstruction();
    }

    protected void unpackInstruction() {
        unpackPrimaryInstructionTrackingTable();
        unpackPrimaryInstruction();
        LinkCascadeInst();
        CachePassControlInst();
    }

    public ArrayList<String> getPassControlCache() {
        return PassControlCache;
    }

    protected void unpackPrimaryInstructionTrackingTable() {
        if (cardinfo.PrimaryInstructionIndex.size() > 0) {
            for (int i = 0; i < cardinfo.PrimaryInstructionIndex.size(); i++) {
                String PrimaryInstructionIndexString = new String(cardinfo.PrimaryInstructionIndex.get(i));
                String[] SplitString = PrimaryInstructionIndexString.split(" ");
                if(SplitString.length != 2) {
                    throw new IllegalArgumentException("Invalid instruction index for " + nameID);
                } else {
                    try {
                        if (PrimaryInstructionTrackingTable.get(SplitString[0]) == null) {
                            ArrayList<Integer> list = new ArrayList<Integer>();
                            list.add(Integer.parseInt(SplitString[1]) - 1);
                            PrimaryInstructionTrackingTable.put(SplitString[0], list);
                        } else {
                            ArrayList<Integer> list = PrimaryInstructionTrackingTable.get(SplitString[0]);
                            list.add(Integer.parseInt(SplitString[1]) - 1);
                        }
                    }  catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid instruction index for " + nameID);
                    }
                }
            }
        }
    }

    protected void unpackPrimaryInstruction() {
        if (cardinfo.PrimaryInstruction.size() > 0) {
            for (int i = 0; i < cardinfo.PrimaryInstruction.size(); i++) {
                InstructionSet instruction = new InstructionSet(cardinfo.PrimaryInstruction.get(i));
                PrimaryInstructions.add(instruction);
            }
        }
    }

    public ArrayList<InstructionSet> getPrimaryInstructionForTheInstructionID(String Id) {
        ArrayList<Integer> list = PrimaryInstructionTrackingTable.get(Id);
        ArrayList<InstructionSet> instructions = new ArrayList<InstructionSet>();
        if (list == null)
            return null;
        for (int i = 0; i < list.size(); i++) {
            int index = list.get(i);
            if ((index < 0) || (index > PrimaryInstructions.size() - 1))
                continue;
            InstructionSet instruction = PrimaryInstructions.get(index);
            if ((instruction != null) && !instructions.contains(instruction)){
                instructions.add(instruction);
            }
        }

        if (instructions.size() > 0)
            return instructions;

        return null;
    }

    public InstructionSet getPrimaryInstructionBasedOnIndex(int index) {
        return PrimaryInstructions.get(index);
    }

    protected void LinkCascadeInst() {
        for (int i = 0; i < PrimaryInstructions.size(); i++) {
            InstructionSet instruction = PrimaryInstructions.get(i);
            if (instruction.getCascadeIndex() > -1) {
                instruction.setNextInst(getPrimaryInstructionBasedOnIndex(instruction.getCascadeIndex()));
            }
        }
    }

    protected void CachePassControlInst() {
        for (int i = 0; i < PrimaryInstructions.size(); i++) {
            InstructionSet instruction = PrimaryInstructions.get(i);
            if (instruction.getInstructionType() == InstructionType.PassControlToOpponent) {
                String inst = cardinfo.PrimaryInstruction.get(instruction.getAttrCountOrIndex() - 1);
                for (int j = PassControlCache.size(); j < instruction.getAttrCountOrIndex() - 1; j++) {
                    PassControlCache.add("Nil");
                }
                PassControlCache.add(new String(inst));
            }
        }
    }
}