package koustav.duelmasters.main.androidgameduelmastersdatastructure;

import java.util.ArrayList;
import java.util.Hashtable;

import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionSet;

/**
 * Created by Koustav on 2/22/2015.
 */
public class InactiveCard extends Cards {
    protected String nameID;
    protected int civilization;
    protected String race;
    protected int cost;
    protected TypeOfCard type;
    protected int power;
    protected int Breaker;
    protected String EvolutionCompareString;
    protected FlagAttributes flags;
    protected Hashtable<String, ArrayList<Integer>> CrossInstructionTrackingTable;
    protected ArrayList<InstructionSet> CrossInstructions;

    ArrayList<InstructionSet> TemporaryPreCleanup;
    ArrayList<InstructionSet> TemporaryPostCleanup;
    ArrayList<InstructionSet> TemporarySpreadingInst;

    public InactiveCard(PackedCardInfo cardinfo, GridPositionIndex GridPosition) {
        super(cardinfo, GridPosition);
        this.flags = new FlagAttributes();
        this.CrossInstructionTrackingTable = new Hashtable<String, ArrayList<Integer>>();
        this.CrossInstructions = new ArrayList<InstructionSet>();
        unpackCardInfo();

        TemporaryPreCleanup = new ArrayList<InstructionSet>();
        TemporaryPostCleanup = new ArrayList<InstructionSet>();
        TemporarySpreadingInst = new ArrayList<InstructionSet>();
    }

    @Override
    public String getNameID(){
        return nameID;
    }

    public int getCivilization() {
        return civilization;
    }

    public String getRace() {
        return race;
    }

    public int getCost() {
        return cost;
    }

    public TypeOfCard getType() {
        return type;
    }

    public int getPower() {
        return power;
    }

    public int getBreaker() {
        return Breaker;
    }

    public String getEvolutionCompareString () {
        return EvolutionCompareString;
    }

    public FlagAttributes getflagAttributes(){
        return flags;
    }

    protected void unpackCardInfo() {
        unpackSlotAttribute();
        unpackFlagAttribute();
        unpackCrossInstructionTrackingTable();
        unpackCrossInstruction();
    }

    protected void unpackSlotAttribute() {
        int size = cardinfo.SlotAttributes.size();

        if(size != 7) {
            throw new IllegalArgumentException("SlotAttributes should be equal to 7.");
        } else {
            nameID = new String(cardinfo.SlotAttributes.get(0));
            try {
                civilization = Integer.parseInt(cardinfo.SlotAttributes.get(1));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid civilization type" + nameID);
            }
            race = new String(cardinfo.SlotAttributes.get(2));

            try {
                cost = Integer.parseInt(cardinfo.SlotAttributes.get(3));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid cost value for " + nameID);
            }

            switch (cardinfo.SlotAttributes.get(4)) {
                case "1":
                    type = TypeOfCard.Creature;
                    break;
                case "2":
                    type = TypeOfCard.Evolution;
                    break;
                case "3":
                    type = TypeOfCard.Spell;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid Type for " + nameID);

            }

            try {
                power = Integer.parseInt(cardinfo.SlotAttributes.get(5));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid power value for " + nameID);
            }

            try {
                Breaker = Integer.parseInt(cardinfo.SlotAttributes.get(6));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid breaker value for " + nameID);
            }
        }
    }

    protected void unpackFlagAttribute() {

        if (cardinfo.FlagAttributes.size() > 0) {
            for (int i = 0; i < cardinfo.FlagAttributes.size(); i++) {
                String packedflag = new String(cardinfo.FlagAttributes.get(i));
                String[] attrvalue = packedflag.split(" ");
                if(attrvalue.length != 2) {
                    throw new IllegalArgumentException("Invalid flag attribute for " + nameID);
                } else {
                    flags.SetAttribute(attrvalue[0], Integer.parseInt(attrvalue[1]));
                }
            }
        }
    }

    protected void unpackCrossInstructionTrackingTable() {
        if (cardinfo.CrossInstructionIndex.size() > 0) {
            for (int i = 0; i < cardinfo.CrossInstructionIndex.size(); i++) {
                String PrimaryInstructionIndexString = new String(cardinfo.CrossInstructionIndex.get(i));
                String[] SplitString = PrimaryInstructionIndexString.split(" ");
                if(SplitString.length != 2) {
                    throw new IllegalArgumentException("Invalid instruction index for " + nameID);
                } else {
                    if (CrossInstructionTrackingTable.get(SplitString[0]) == null) {
                        ArrayList<Integer> list = new ArrayList<Integer>();
                        list.add(Integer.parseInt(SplitString[1]) - 1);
                        CrossInstructionTrackingTable.put(SplitString[0], list);
                    } else {
                        ArrayList<Integer> list = CrossInstructionTrackingTable.get(SplitString[0]);
                        list.add(Integer.parseInt(SplitString[1]) - 1);
                    }
                }
            }
        }
    }

    protected void unpackCrossInstruction() {
        if (cardinfo.CrossInstruction.size() > 0) {
            for (int i = 0; i < cardinfo.CrossInstruction.size(); i++) {
                InstructionSet instruction = new InstructionSet(cardinfo.CrossInstruction.get(i));
                CrossInstructions.add(instruction);
            }
        }
    }

    public ArrayList<InstructionSet> getCrossInstructionForTheInstructionID(String Id) {
        ArrayList<Integer> list = CrossInstructionTrackingTable.get(Id);
        ArrayList<InstructionSet> instructions = new ArrayList<InstructionSet>();
        if (list == null)
            return null;
        for (int i = 0; i < list.size(); i++) {
            int index = list.get(i);
            if ((index < 0) || (index > CrossInstructions.size() - 1))
                continue;
            InstructionSet instruction = CrossInstructions.get(index);
            if ((instruction != null) && !instructions.contains(instruction)){
                instructions.add(instruction);
            }
        }

        if (instructions.size() > 0)
            return instructions;

        return null;
    }

    public ArrayList<InstructionSet> getTemporaryPreCleanup() {
        if (this.TemporaryPreCleanup.size() > 0)
            return this.TemporaryPreCleanup;

        return null;
    }

    public void AddTemporaryPreCleanup(InstructionSet instruction) {
        this.TemporaryPreCleanup.add(instruction);
    }

    public void ClearTemporaryPreCleanup() {
        this.TemporaryPreCleanup.clear();
    }

    public ArrayList<InstructionSet> getTemporaryPostCleanup() {
        if (this.TemporaryPostCleanup.size() > 0)
            return this.TemporaryPostCleanup;

        return null;
    }

    public void AddTemporaryPostCleanup(InstructionSet instruction) {
        this.TemporaryPostCleanup.add(instruction);
    }

    public void ClearTemporaryPostCleanup() {
        this.TemporaryPostCleanup.clear();
    }

    public ArrayList<InstructionSet> getTemporarySpreadingInst() {
        if (this.TemporarySpreadingInst.size() > 0)
            return this.TemporarySpreadingInst;

        return null;
    }

    public void AddTemporarySpreadingInst(InstructionSet instruction) {
        this.TemporarySpreadingInst.add(instruction);
    }

    public void ClearTemporarySpreadingInst() {
        this.TemporarySpreadingInst.clear();
    }
}
