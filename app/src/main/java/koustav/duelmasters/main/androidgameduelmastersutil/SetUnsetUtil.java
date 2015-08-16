package koustav.duelmasters.main.androidgameduelmastersutil;

import java.util.ArrayList;

import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionID;
import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionSet;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.ActiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.InactiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.World;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Zone;

/**
 * Created by Koustav on 4/24/2015.
 */
public class SetUnsetUtil {
    public static void SetSpreadFlagAttr(InactiveCard card, InstructionSet instruction) {
        String attr = instruction.getAttr().getFlag();
        if (attr.equals("MaskDestroyDst") && GetUtil.MaskDestroyDstVal(card) > 0)
            return;

        int val = instruction.getAttr().getValue();
        int val2;

        if (GetUtil.NotYetSpread(card)) {
            val2 = card.getflagAttributes().GetAttribute(attr);
            card.getflagAttributes().ClearAttribute(attr);
            val2 = val2 + val;
            card.getflagAttributes().SetAttribute(attr, val2);
        } else {
            if (!instruction.getResult()) {
                val2 = card.getflagAttributes().GetAttribute(attr);
                card.getflagAttributes().ClearAttribute(attr);
                val2 = val2 + val;
                card.getflagAttributes().SetAttribute(attr, val2);
            }
        }
    }

    public static void SetFlagAttr(World world, InactiveCard card, InstructionSet instruction) {
        String attr = instruction.getAttr().getFlag();
        if (attr.equals("MaskDestroyDst") && GetUtil.MaskDestroyDstVal(card) > 0)
            return;

        int val = instruction.getAttr().getValue();
        int val2;
        val2 = card.getflagAttributes().GetAttribute(attr);
        card.getflagAttributes().ClearAttribute(attr);
        val2 = val2 + val;
        card.getflagAttributes().SetAttribute(attr, val2);
        world.getEventLog().registerEvent(card,false, 0 ,attr, true, val);
    }

    public static void SetUnsetBoostFlagAttr(InactiveCard card, InstructionSet instruction, boolean boost) {
        String attr = instruction.getAttr().getFlag();
        if (attr.equals("MaskDestroyDst"))
            throw new IllegalArgumentException("Invalid attribute to boost");

        int val = instruction.getAttr().getValue();
        int val2;

        if (boost) {
            if (!instruction.getResult()) {
                val2 = card.getflagAttributes().GetAttribute(attr);
                card.getflagAttributes().ClearAttribute(attr);
                val2 = val2 + val;
                card.getflagAttributes().SetAttribute(attr, val2);
                instruction.setResult(true);
            }
        } else {
            if (instruction.getResult()) {
                val2 = card.getflagAttributes().GetAttribute(attr);
                card.getflagAttributes().ClearAttribute(attr);
                val2 = val2 - val;
                if (val2 > 0) {
                    card.getflagAttributes().SetAttribute(attr, val2);
                }
                instruction.setResult(false);
            }
        }
    }

    public static void SetUnsetBoostMultiplierFlagAttr(InactiveCard card, InstructionSet instruction, int boost){
        String attr = instruction.getAttr().getFlag();
        if (attr.equals("MaskDestroyDst"))
            throw new IllegalArgumentException("Invalid attribute to boost");

        int val = instruction.getAttr().getValue();
        int val2;

        if (boost > 0) {
            if (!instruction.getResult()) {
                val2 = card.getflagAttributes().GetAttribute(attr);
                card.getflagAttributes().ClearAttribute(attr);
                val2 = val2 + boost * val;
                instruction.setAttrCountOrIndex(boost);
                card.getflagAttributes().SetAttribute(attr, val2);
                instruction.setResult(true);
            } else {
                val2 = card.getflagAttributes().GetAttribute(attr);
                card.getflagAttributes().ClearAttribute(attr);
                val2 = val2 - (instruction.getAttrCountOrIndex() * val);
                val2 = val2 + (boost * val);
                instruction.setAttrCountOrIndex(boost);
                card.getflagAttributes().SetAttribute(attr, val2);
            }
        } else {
            if (instruction.getResult()) {
                val2 = card.getflagAttributes().GetAttribute(attr);
                card.getflagAttributes().ClearAttribute(attr);
                val2 = val2 - (instruction.getAttrCountOrIndex() * val);
                if (val2 > 0) {
                    card.getflagAttributes().SetAttribute(attr, val2);
                }
                instruction.setAttrCountOrIndex(0);
                instruction.setResult(false);
            }
        }
    }

    public static void CleanFlagAttr(InactiveCard card, InstructionSet instruction) {
        String attr = instruction.getAttr().getFlag();
        int val = instruction.getAttr().getValue();
        int val2;

        val2 = card.getflagAttributes().GetAttribute(attr);
        card.getflagAttributes().ClearAttribute(attr);
        val2 = val2 - val;
        if (val2 > 0) {
            card.getflagAttributes().SetAttribute(attr, val2);
        }
    }

    public static void  CleanFlagAttrMultiplier(InactiveCard card, InstructionSet instruction, int boost) {
        String attr = instruction.getAttr().getFlag();
        int val = instruction.getAttr().getValue();
        int val2;

        val2 = card.getflagAttributes().GetAttribute(attr);
        card.getflagAttributes().ClearAttribute(attr);
        val2 = val2 - (val * boost);
        if (val2 > 0) {
            card.getflagAttributes().SetAttribute(attr, val2);
        }
    }

    public static void SetTappedAttr(InactiveCard card) {
        card.getflagAttributes().ClearAttribute("Tapped");
        card.getflagAttributes().SetAttribute("Tapped", 1);
    }

    public static void UnSetTappedAttr(InactiveCard card) {
        card.getflagAttributes().ClearAttribute("Tapped");
    }

    public static void SetMarkedCard(InactiveCard card) {
        card.getflagAttributes().ClearAttribute("MarkedCard");
        card.getflagAttributes().SetAttribute("MarkedCard", 1);
    }

    public static void UnSetMarkedCard(InactiveCard card) {
        card.getflagAttributes().ClearAttribute("MarkedCard");
    }

    public static void SetUsedTurboRushSetAttr(InactiveCard card) {
        card.getflagAttributes().ClearAttribute("UsedTurboRushSetAttr");
        card.getflagAttributes().SetAttribute("UsedTurboRushSetAttr", 1);
    }

    public static void SetTurboRushSetAttr(World world) {
        Zone zone = world.getMaze().getZoneList().get(0);

        for (int i = 0; i < zone.zoneSize(); i++) {
            ActiveCard card = (ActiveCard) zone.getZoneArray().get(i);
            if (GetUtil.IsActiveTurboRush(card) && !GetUtil.IsUsedTurboRushSetAttr(card)) {
                ArrayList<InstructionSet> instructions = card.getPrimaryInstructionForTheInstructionID(InstructionID.TurboRushSetAttrAbility);
                world.getInstructionIteratorHandler().setCard(card);
                world.getInstructionIteratorHandler().setInstructions(instructions);
                while (!world.getInstructionIteratorHandler().update()) ;
                if (instructions != null)
                    SetUnsetUtil.SetUsedTurboRushSetAttr(card);
            }
        }
    }

    public static void SpreadingFlagAttr(World world) {
        Zone MyBattleZone = world.getMaze().getZoneList().get(0);
        Zone OpponentBattleZone = world.getMaze().getZoneList().get(7);

        for (int i = 0; i <MyBattleZone.zoneSize(); i++) {
            InactiveCard card = (InactiveCard) MyBattleZone.getZoneArray().get(i);
            ArrayList<InstructionSet> instructions = card.getCrossInstructionForTheInstructionID(InstructionID.FlagSpreading);
            world.getInstructionIteratorHandler().setCard(card);
            world.getInstructionIteratorHandler().setInstructions(instructions);
            while (!world.getInstructionIteratorHandler().update());
        }

        for (int i = 0; i < OpponentBattleZone.zoneSize(); i++) {
            InactiveCard card = (InactiveCard) OpponentBattleZone.getZoneArray().get(i);
            ArrayList<InstructionSet> instructions = card.getCrossInstructionForTheInstructionID(InstructionID.FlagSpreading);
            world.getInstructionIteratorHandler().setCard(card);
            world.getInstructionIteratorHandler().setInstructions(instructions);
            while (!world.getInstructionIteratorHandler().update());
        }

        for (int i = 0; i <MyBattleZone.zoneSize(); i++) {
            InactiveCard card = (InactiveCard) MyBattleZone.getZoneArray().get(i);
            ArrayList<InstructionSet> instructions = card.getTemporarySpreadingInst();
            world.getInstructionIteratorHandler().setCard(card);
            world.getInstructionIteratorHandler().setInstructions(instructions);
            while (!world.getInstructionIteratorHandler().update());
        }

        for (int i = 0; i < OpponentBattleZone.zoneSize(); i++) {
            InactiveCard card = (InactiveCard) OpponentBattleZone.getZoneArray().get(i);
            ArrayList<InstructionSet> instructions = card.getTemporarySpreadingInst();
            world.getInstructionIteratorHandler().setCard(card);
            world.getInstructionIteratorHandler().setInstructions(instructions);
            while (!world.getInstructionIteratorHandler().update());
        }
    }
}
