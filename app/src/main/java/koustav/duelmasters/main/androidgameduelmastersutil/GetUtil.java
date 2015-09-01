package koustav.duelmasters.main.androidgameduelmastersutil;

import koustav.duelmasters.main.androidgameduelmasterscardrulehandler.InstructionSet;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.InactiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.TypeOfCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.World;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Zone;

/**
 * Created by Koustav on 3/29/2015.
 */
public class GetUtil {
     public static int ComparePower(InactiveCard Acard, InactiveCard Dcard) {
        int powerA, powerB;

        powerA = Acard.getPower() + Acard.getflagAttributes().GetAttribute("Power")
                + Acard.getflagAttributes().GetAttribute("PowerAttacker");
        powerB = Dcard.getPower() + Dcard.getflagAttributes().GetAttribute("Power");

        if(powerA > powerB)
            return 1;
        else if (powerA == powerB)
            return 0;
        else
            return -1;
    }

    public static int getTotalPower(InactiveCard card) {
        return (card.getPower() + card.getflagAttributes().GetAttribute("Power"));
    }

    public static boolean RequiredCivilization(Cards card, int val) {
        if ( val >= 0 && val < 32)
            if ((card.getCivilization() & val) > 0)
                return true;
            else
                return false;
        else
            return false;
    }

    public static boolean CanAttackPlayer(InactiveCard card) {
        if(IgnoreAnyAttackPrevent(card))
            return true;
        if(CantAttack(card))
            return false;
        if(card.getflagAttributes().GetAttribute("ChangeableCanAttackPlayer") > 0)
            return true;
        if((card.getflagAttributes().GetAttribute("CanAttack") & 2) > 0)
            return true;

        return false;
    }

    public static boolean CanAttackShield(InactiveCard card) {
        if(IgnoreAnyAttackPrevent(card))
            return true;
        if(CantAttack(card))
            return false;
        if(card.getflagAttributes().GetAttribute("ChangeableCanAttackShield") > 0)
            return true;
        if((card.getflagAttributes().GetAttribute("CanAttack") & 1) > 0)
            return true;

        return false;
    }

    public static boolean IgnoreAnyAttackPrevent(InactiveCard card) {
        if(card.getflagAttributes().GetAttribute("IgnoreAnyAttackPrevent") > 0)
            return true;
        else
            return false;
    }

    public static boolean IsTapped(InactiveCard card) {
        if((card.getflagAttributes().GetAttribute("Tapped")) > 0)
            return true;
        else
            return false;
    }

    public static boolean IsDummyTapped(InactiveCard card) {
        if ((card.getflagAttributes().GetAttribute("DummyTapped")) > 0)
            return true;
        else
            return false;
    }

    public static boolean CantAttack(InactiveCard card) {
        if (card.getflagAttributes().GetAttribute("CantAttack") > 0)
            return true;
        else
            return false;
    }

    public static boolean CantBeAttacked(InactiveCard attacking, InactiveCard attacked) {
        String attr = "ThisCreatureCantAttackThisCreature" + attacked.GridPosition().getZone() + '_'
                + attacked.GridPosition().getGridIndex();
        if (attacked.getflagAttributes().GetAttribute("ChangeableCantBeAttacked") > 0)
            return true;
        if((attacked.getflagAttributes().GetAttribute("CantBeAttacked") &
                attacking.getCivilization()) > 0)
            return true;
        if ((attacking.getflagAttributes().GetAttribute(attr)) > 0)
            return true;

        return false;
    }

    public static boolean CantBeChooseByOpponent(InactiveCard card) {
        if ((card.getflagAttributes().GetAttribute("CantBeChooseByOpponent")) > 0)
            return true;
        else
            return false;
    }

    public static boolean DontUnTap(InactiveCard card) {
        if ((card.getflagAttributes().GetAttribute("DontUnTap")) > 0)
            return true;
        else
            return false;
    }

    public static boolean IsStealth(InactiveCard card, int typeofattack) {
        if ((typeofattack == 1) &&
                ((card.getflagAttributes().GetAttribute("StealthCreature")) > 0))
            return true;
        if ((typeofattack == 2) &&
                ((card.getflagAttributes().GetAttribute("StealthPlayer")) > 0))
            return true;
        if ((typeofattack == 4) &&
                ((card.getflagAttributes().GetAttribute("StealthShield")) > 0))
            return true;

        return false;
    }

    public static boolean IsBlockable(InactiveCard attacker, InactiveCard blocker, int typeofattack) {
        if(IsStealth(attacker,typeofattack))
            return false;
        int blockerpower = blocker.getPower() + blocker.getflagAttributes().GetAttribute("Power");
        int max_power = attacker.getflagAttributes().GetAttribute("UpperPowerLevelToBeBlocked");
        int min_power = attacker.getflagAttributes().GetAttribute("LowerPowerLevelToBeBlocked");
        if(max_power == 0) {
            if(min_power > blockerpower)
                return false;
        } else  {
            if (max_power < min_power)
                throw new IllegalArgumentException("max power cannot be less than lower power");
            if(min_power > blockerpower || max_power < blockerpower)
                return false;
        }

        int val = typeofattack << 5;
        if (((attacker.getflagAttributes().GetAttribute("AttackUnBlockable") & val) >0)
                && ((attacker.getflagAttributes().GetAttribute("AttackUnBlockable") &
                blocker.getCivilization()) > 0))
            return false;

        return true;
    }

    public static boolean IsBlocker(InactiveCard blocker, InactiveCard attacker) {
        if (GetUtil.IsTapped(blocker))
            return false;
        if (blocker.getflagAttributes().GetAttribute("ChangeableBlocker") > 0)
            return true;
        if ((blocker.getflagAttributes().GetAttribute("Blocker") & attacker.getCivilization()) > 0)
            return true;

        return false;
    }

    public static boolean IsHasTapAbility(InactiveCard card) {
        if (card.getflagAttributes().GetAttribute("HasTapAbility") > 0)
            return true;
        else
            return false;
    }

    public static boolean NewSummonedCreature(InactiveCard card) {
        if ((card.getflagAttributes().GetAttribute("NewSummonedCreature")) > 0)
            return true;
        else
            return false;
    }

    public static boolean NoSummoningSickness(InactiveCard card) {
        if (card.getflagAttributes().GetAttribute("NoSummoningSickness") > 0)
            return true;
        else
            return false;
    }

    public static boolean NotYetSpread(InactiveCard card) {
        if ((card.getflagAttributes().GetAttribute("NotYetSpread")) > 0)
            return true;
        else
            return false;
    }

    public static boolean GoingToSlay(InactiveCard Fcard, InactiveCard Scard) {
        if (Fcard.getflagAttributes().GetAttribute("ChangeableSlayer") > 0)
            return true;
        if ((Fcard.getflagAttributes().GetAttribute("Slayer") &
                Scard.getCivilization()) > 0)
            return true;

        return false;
    }

    public static boolean EachTurnIfAble(InactiveCard card) {
        if ((card.getflagAttributes().GetAttribute("EachTurnIfAble")) > 0)
            return true;
        else
            return false;
    }

    public static boolean IsSummonOnAllManaMatch(InactiveCard card) {
        if (card.getflagAttributes().GetAttribute("SummonOnAllManaMatch") > 0)
            return true;
        else
            return false;
    }

    public static int IsBreaksWheneverBlocked(InactiveCard card) {
        return card.getflagAttributes().GetAttribute("BreaksWheneverBlocked");
    }

    public static boolean CanAttackCard(InactiveCard attacking, InactiveCard attacked) {
        String attr;
        if(IgnoreAnyAttackPrevent(attacking))
            return true;
        if(CantAttack(attacking))
            return false;
        if(CantBeAttacked(attacking, attacked))
            return false;

        if (IsTapped(attacked) || IsDummyTapped(attacked)){
            attr = "AttackTappedCardCivilization" + attacked.getCivilization();
            if(attacking.getflagAttributes().GetAttribute("ChangeableAttackTappedCard") > 0)
                return true;
            if(attacking.getflagAttributes().GetAttribute(attr) > 0)
                return true;
            if ((attacking.getflagAttributes().GetAttribute("AttackTappedCard") &
                    attacked.getCivilization()) > 0)
                return true;
        } else {
            attr = "AttackUntappedCardCivilization" + attacked.getCivilization();
            if (attacking.getflagAttributes().GetAttribute("ChangeableAttackUntappedCard") > 0)
                return true;
            if (attacking.getflagAttributes().GetAttribute(attr) > 0)
                return true;
            if ((attacking.getflagAttributes().GetAttribute("AttackUntappedCard") &
                    attacked.getCivilization()) > 0)
                return true;
        }

        return false;
    }

    public static boolean IsShieldTrigger(InactiveCard card) {
        if(card.getflagAttributes().GetAttribute("ShieldTrigger") > 0)
            return true;
        else
            return false;
    }

    public static int Breaker(InactiveCard card) {
        if (card.getflagAttributes().GetAttribute("Breaker") > 0)
            return card.getflagAttributes().GetAttribute("Breaker");
        else
            return card.getBreaker();
    }

    public static boolean IsSmashShieldBreaker(InactiveCard card) {
        if (card.getflagAttributes().GetAttribute("SmashShieldBreaker") > 0)
            return true;
        else
            return false;
    }

    public static int MaskDestroyDstVal(InactiveCard card) {
        return card.getflagAttributes().GetAttribute("MaskDestroyDst");
    }

    public static boolean IsSurvivor(InactiveCard card) {
        if (card.getflagAttributes().GetAttribute("Survivor") > 0)
            return true;
        else
            return false;
    }

    public static boolean IsWaveStriker(InactiveCard card) {
        if (card.getflagAttributes().GetAttribute("WaveStriker") > 0)
            return true;
        else
            return false;
    }

    public static boolean IsActiveWaveStriker(InactiveCard card) {
        if (card.getflagAttributes().GetAttribute("ActiveWaveStriker") > 0)
            return true;
        else
            return false;
    }

    public static boolean IsSilentSkill(InactiveCard card) {
        if (card.getflagAttributes().GetAttribute("SilentSkill") > 0)
            return true;
        else
            return false;
    }

    public static int ManaCost(InactiveCard card) {
        return card.getflagAttributes().GetAttribute("ManaCost");
    }

    public static int NManaCost(InactiveCard card) {
        return card.getflagAttributes().GetAttribute("NManaCost");
    }

    public static boolean UnTapAfterBattle(InactiveCard card) {
        if (card.getflagAttributes().GetAttribute("UnTapAfterBattle") > 0)
            return true;
        else
            return false;
    }

    public static boolean AttackIfAble(InactiveCard card) {
        if (card.getflagAttributes().GetAttribute("AttackIfAble") > 0)
            return true;
        else
            return false;
    }

    public static boolean EvolutionCompatible(InactiveCard card) {
        if (card.getflagAttributes().GetAttribute("EvolutionCompatible") > 0)
            return true;
        else
            return false;
    }

    public static boolean CantUseTapAbility(InactiveCard card) {
        if (card.getflagAttributes().GetAttribute("CantUseTapAbility") > 0)
            return true;
        else
            return false;
    }

    public static boolean SummonTapped(InactiveCard card) {
        if (card.getflagAttributes().GetAttribute("SummonTapped") > 0)
            return true;
        else
            return false;
    }

    public static boolean AddToManaTapped(InactiveCard card) {
        if(card.getflagAttributes().GetAttribute("AddToManaTapped") > 0)
            return true;
        else
            return false;
    }

    public static boolean DontDestroy(InactiveCard card) {
        if (card.getflagAttributes().GetAttribute("DontDestroy") > 0)
            return true;
        else
            return false;
    }

    public static boolean DestroyAfterBattle(InactiveCard card) {
        if (card.getflagAttributes().GetAttribute("DestroyAfterBattle") > 0)
            return true;
        else
            return false;
    }

    public static boolean IsActiveTurboRush(InactiveCard card) {
        if (card.getflagAttributes().GetAttribute("ActiveTurboRush") > 0)
            return true;
        else
            return false;
    }

    public static boolean IsUsedTurboRushSetAttr(InactiveCard card) {
        if (card.getflagAttributes().GetAttribute("UsedTurboRushSetAttr") > 0)
            return true;
        else
            return false;
    }

    public static boolean IsUsedBlockedSetAttrAbility(InactiveCard card) {
        if (card.getflagAttributes().GetAttribute("UsedBlockedSetAttrAbility") > 0)
            return true;
        else
            return false;
    }

    public static boolean IsSlayerWhenBlocked(InactiveCard card) {
        if (card.getflagAttributes().GetAttribute("SlayerWhenBlocked") > 0)
            return true;
        else
            return false;
    }

    public static int getTotalManaCost(InactiveCard card) {
        return (card.getCost() + ManaCost(card) - NManaCost(card));
    }

    public static boolean IsAllowedToSummonOrCast(InactiveCard card, World world) {
        if (card.getflagAttributes().GetAttribute("CantSummon") > 0)
            return false;

        if (card.getType() == TypeOfCard.Evolution && !GetUtil.IsEvolutionBaseExist(card, world)) {
            return false;
        }

        if (GetUtil.IsSummonOnAllManaMatch(card)) {
            InactiveCard mcard;
            boolean status = true;
            for (int i =0; i < world.getMaze().getZoneList().get(1).zoneSize(); i++) {
                mcard = (InactiveCard) world.getMaze().getZoneList().get(1).getZoneArray().get(i);
                if (!RequiredCivilization(mcard, card.getCivilization())) {
                    status = false;
                    break;
                }
            }

            if (!status)
                return false;
        }

        int ManaCost = getTotalManaCost(card);
        int FreeMana = 0;
        int CivilizationMatched = 0;
        InactiveCard ManaCard;
        for (int i =0; i < world.getMaze().getZoneList().get(1).zoneSize(); i++) {
            ManaCard = (InactiveCard) world.getMaze().getZoneList().get(1).getZoneArray().get(i);
            if (!IsTapped(ManaCard)) {
                FreeMana++;
                if (RequiredCivilization(ManaCard, card.getCivilization()))
                    CivilizationMatched++;
            }
        }

        if (FreeMana < ManaCost)
            return false;

        if (!(CivilizationMatched > 0))
            return false;


        return true;
    }

    public static boolean IsAllowedToAttack(InactiveCard card, World world) {
        boolean status = false;
        InactiveCard Ocard;
        if (IgnoreAnyAttackPrevent(card))
            return true;
        if (CantAttack(card))
            return false;
        if (NewSummonedCreature(card) && !NoSummoningSickness(card))
            return false;

        if (world.getMaze().getZoneList().get(9).zoneSize() > 0) {
            if (CanAttackShield(card))
                return true;
        }

        if (world.getMaze().getZoneList().get(9).zoneSize() == 0) {
            if (CanAttackPlayer(card))
                return true;
        }

        for (int i = 0; i < world.getMaze().getZoneList().get(7).zoneSize(); i++) {
            Ocard = (InactiveCard) world.getMaze().getZoneList().get(7).getZoneArray().get(i);
            if (CanAttackCard(card, Ocard)) {
                status = true;
                break;
            }
        }

        return status;
    }

    public static int AttackEvaluation(InactiveCard AttackingCard, InactiveCard AttackedCard, boolean IsBlocked) {
        int powerCompare = GetUtil.ComparePower(AttackingCard, AttackedCard);
        /*
        1 destroy Attacked card 0 means both -1 means attacking card 2 means nothing happened after battle
         */

        if (powerCompare == 1) {
            if (GetUtil.GoingToSlay(AttackedCard,AttackingCard) || GetUtil.DestroyAfterBattle(AttackingCard)) {
                return 0;
            } else {
                return 1;
            }
        } else if (powerCompare == 0) {
            return 0;
        } else {
            if (GetUtil.GoingToSlay(AttackingCard,AttackedCard) || GetUtil.DestroyAfterBattle(AttackedCard) ||
                    (IsBlocked && GetUtil.IsSlayerWhenBlocked(AttackingCard))) {
                return 0;
            }else {
                return -1;
            }
        }
    }

    public static boolean IsEvolutionBaseExist(InactiveCard card, World world) {
        InactiveCard Bcard;
        boolean status = false;
        Zone zone = world.getMaze().getZoneList().get(0);
        for (int i = 0; i < zone.zoneSize(); i++) {
            Bcard = (InactiveCard) zone.getZoneArray().get(i);
            if ((Bcard.getRace().contains(card.getEvolutionCompareString()) || GetUtil.EvolutionCompatible(Bcard)) && !GetUtil.IsTapped(Bcard)) {
                status = true;
                break;
            }
        }

        return status;
    }

    public static boolean OpponentHasABlocker(World world, InactiveCard Attacker, InactiveCard Attacked) {
        InactiveCard card;
        boolean status = false;
        Zone zone = world.getMaze().getZoneList().get(7);
        for (int i = 0; i < zone.zoneSize(); i++) {
            card = (InactiveCard) zone.getZoneArray().get(i);
            if (GetUtil.IsBlocker(card, Attacker) && (Attacked == null || Attacked != card)) {
                status = true;
                break;
            }
        }

        return status;
    }
}
