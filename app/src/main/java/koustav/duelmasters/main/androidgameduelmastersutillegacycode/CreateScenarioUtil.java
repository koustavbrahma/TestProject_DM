package koustav.duelmasters.main.androidgameduelmastersutillegacycode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import koustav.duelmasters.main.androidgameduelmastersdatastructure.InactiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.ActiveCard;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.GridPositionIndex;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.PackedCardInfo;
import koustav.duelmasters.main.androidgameduelmastersdatastructure.World;
import koustav.duelmasters.main.androidgameduelmastersutil.SetUnsetUtil;

/**
 * Created by Koustav on 5/20/2015.
 */
public class CreateScenarioUtil {
    public static void CreateTestScenario(World world) {
        try {
            InputStream DeckList = world.getGame().getFileIO().readAsset("testscenario.txt");
            BufferedReader bufferedReaderDeckList = new BufferedReader(new InputStreamReader(DeckList));
            String CardName;
            int gridindex = 0;
            while ((CardName = bufferedReaderDeckList.readLine()) != null ) {
                GridPositionIndex gridPosition = new GridPositionIndex(1, gridindex);
                gridindex++;
                Cards card = new Cards(new PackedCardInfo(), gridPosition);
                String CardTitle = "Begin";
                CardTitle = CardTitle.concat(CardName);
                InputStream CardLibList = world.getGame().getFileIO().readAsset("CardLib.txt");
                BufferedReader bufferedReaderCardLibList = new BufferedReader(new InputStreamReader(CardLibList));
                String CardPackedInfo;
                while (!CardTitle.equals(CardPackedInfo = bufferedReaderCardLibList.readLine()));
                if (CardPackedInfo != null){
                    if (CardTitle.equals(CardPackedInfo)) {
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndSlotAttributes")) {
                            card.cardInfo().SlotAttributes.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndFlagAttributes")) {
                            card.cardInfo().FlagAttributes.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndPrimaryInstructionIndex")) {
                            card.cardInfo().PrimaryInstructionIndex.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndPrimaryInstructions")) {
                            card.cardInfo().PrimaryInstruction.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndCrossInstructionIndex")) {
                            card.cardInfo().CrossInstructionIndex.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndCrossInstruction")) {
                            card.cardInfo().CrossInstruction.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                    }
                }
                bufferedReaderCardLibList.close();
                CardLibList.close();
                InactiveCard icard = new InactiveCard(card.ExtractCardInfo(), card.GridPosition());
                world.getMaze().getZoneList().get(1).getZoneArray().add(icard);
                world.getGridIndexTrackingTable().trackGridIndex(icard.GridPosition(), icard);
            }
            bufferedReaderDeckList.close();
            DeckList.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            InputStream DeckList = world.getGame().getFileIO().readAsset("testscenario.txt");
            BufferedReader bufferedReaderDeckList = new BufferedReader(new InputStreamReader(DeckList));
            String CardName;
            int gridindex = 0;
            while ((CardName = bufferedReaderDeckList.readLine()) != null ) {
                GridPositionIndex gridPosition = new GridPositionIndex(9, gridindex);
                gridindex++;
                Cards card = new Cards(new PackedCardInfo(), gridPosition);
                String CardTitle = "Begin";
                CardTitle = CardTitle.concat(CardName);
                InputStream CardLibList = world.getGame().getFileIO().readAsset("CardLib.txt");
                BufferedReader bufferedReaderCardLibList = new BufferedReader(new InputStreamReader(CardLibList));
                String CardPackedInfo;
                while (!CardTitle.equals(CardPackedInfo = bufferedReaderCardLibList.readLine()));
                if (CardPackedInfo != null){
                    if (CardTitle.equals(CardPackedInfo)) {
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndSlotAttributes")) {
                            card.cardInfo().SlotAttributes.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndFlagAttributes")) {
                            card.cardInfo().FlagAttributes.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndPrimaryInstructionIndex")) {
                            card.cardInfo().PrimaryInstructionIndex.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndPrimaryInstructions")) {
                            card.cardInfo().PrimaryInstruction.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndCrossInstructionIndex")) {
                            card.cardInfo().CrossInstructionIndex.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndCrossInstruction")) {
                            card.cardInfo().CrossInstruction.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                    }
                }
                bufferedReaderCardLibList.close();
                CardLibList.close();
                InactiveCard icard = new InactiveCard(card.ExtractCardInfo(), card.GridPosition());
                world.getMaze().getZoneList().get(9).getZoneArray().add(icard);
                world.getGridIndexTrackingTable().trackGridIndex(icard.GridPosition(), icard);
            }
            bufferedReaderDeckList.close();
            DeckList.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            InputStream DeckList = world.getGame().getFileIO().readAsset("OpponentCreatures.txt");
            BufferedReader bufferedReaderDeckList = new BufferedReader(new InputStreamReader(DeckList));
            String CardName;
            int gridindex = 0;
            while ((CardName = bufferedReaderDeckList.readLine()) != null ) {
                GridPositionIndex gridPosition = new GridPositionIndex(7, gridindex);
                gridindex++;
                Cards card = new Cards(new PackedCardInfo(), gridPosition);
                String CardTitle = "Begin";
                CardTitle = CardTitle.concat(CardName);
                InputStream CardLibList = world.getGame().getFileIO().readAsset("CardLib.txt");
                BufferedReader bufferedReaderCardLibList = new BufferedReader(new InputStreamReader(CardLibList));
                String CardPackedInfo;
                while (!CardTitle.equals(CardPackedInfo = bufferedReaderCardLibList.readLine()));
                if (CardPackedInfo != null){
                    if (CardTitle.equals(CardPackedInfo)) {
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndSlotAttributes")) {
                            card.cardInfo().SlotAttributes.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndFlagAttributes")) {
                            card.cardInfo().FlagAttributes.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndPrimaryInstructionIndex")) {
                            card.cardInfo().PrimaryInstructionIndex.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndPrimaryInstructions")) {
                            card.cardInfo().PrimaryInstruction.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndCrossInstructionIndex")) {
                            card.cardInfo().CrossInstructionIndex.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                        while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndCrossInstruction")) {
                            card.cardInfo().CrossInstruction.add(CardPackedInfo);
                        }
                        CardPackedInfo = bufferedReaderCardLibList.readLine();
                    }
                }
                bufferedReaderCardLibList.close();
                CardLibList.close();
                InactiveCard icard = new InactiveCard(card.ExtractCardInfo(), card.GridPosition());
                world.getMaze().getZoneList().get(7).getZoneArray().add(icard);
                world.getGridIndexTrackingTable().trackGridIndex(icard.GridPosition(), icard);
                SetUnsetUtil.SetTappedAttr(icard);
            }
            bufferedReaderDeckList.close();
            DeckList.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        public static void CreateTestScenario2(World world) {
            try {
                InputStream DeckList = world.getGame().getFileIO().readAsset("testscenario.txt");
                BufferedReader bufferedReaderDeckList = new BufferedReader(new InputStreamReader(DeckList));
                String CardName;
                int gridindex = 0;
                while ((CardName = bufferedReaderDeckList.readLine()) != null ) {
                    GridPositionIndex gridPosition = new GridPositionIndex(8, gridindex);
                    gridindex++;
                    Cards card = new Cards(new PackedCardInfo(), gridPosition);
                    String CardTitle = "Begin";
                    CardTitle = CardTitle.concat(CardName);
                    InputStream CardLibList = world.getGame().getFileIO().readAsset("CardLib.txt");
                    BufferedReader bufferedReaderCardLibList = new BufferedReader(new InputStreamReader(CardLibList));
                    String CardPackedInfo;
                    while (!CardTitle.equals(CardPackedInfo = bufferedReaderCardLibList.readLine()));
                    if (CardPackedInfo != null){
                        if (CardTitle.equals(CardPackedInfo)) {
                            CardPackedInfo = bufferedReaderCardLibList.readLine();
                            while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndSlotAttributes")) {
                                card.cardInfo().SlotAttributes.add(CardPackedInfo);
                            }
                            CardPackedInfo = bufferedReaderCardLibList.readLine();
                            while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndFlagAttributes")) {
                                card.cardInfo().FlagAttributes.add(CardPackedInfo);
                            }
                            CardPackedInfo = bufferedReaderCardLibList.readLine();
                            while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndPrimaryInstructionIndex")) {
                                card.cardInfo().PrimaryInstructionIndex.add(CardPackedInfo);
                            }
                            CardPackedInfo = bufferedReaderCardLibList.readLine();
                            while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndPrimaryInstructions")) {
                                card.cardInfo().PrimaryInstruction.add(CardPackedInfo);
                            }
                            CardPackedInfo = bufferedReaderCardLibList.readLine();
                            while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndCrossInstructionIndex")) {
                                card.cardInfo().CrossInstructionIndex.add(CardPackedInfo);
                            }
                            CardPackedInfo = bufferedReaderCardLibList.readLine();
                            while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndCrossInstruction")) {
                                card.cardInfo().CrossInstruction.add(CardPackedInfo);
                            }
                            CardPackedInfo = bufferedReaderCardLibList.readLine();
                        }
                    }
                    bufferedReaderCardLibList.close();
                    CardLibList.close();
                    InactiveCard icard = new InactiveCard(card.ExtractCardInfo(), card.GridPosition());
                    world.getMaze().getZoneList().get(8).getZoneArray().add(icard);
                    world.getGridIndexTrackingTable().trackGridIndex(icard.GridPosition(), icard);
                }
                bufferedReaderDeckList.close();
                DeckList.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                InputStream DeckList = world.getGame().getFileIO().readAsset("testscenario.txt");
                BufferedReader bufferedReaderDeckList = new BufferedReader(new InputStreamReader(DeckList));
                String CardName;
                int gridindex = 0;
                while ((CardName = bufferedReaderDeckList.readLine()) != null ) {
                    GridPositionIndex gridPosition = new GridPositionIndex(2, gridindex);
                    gridindex++;
                    Cards card = new Cards(new PackedCardInfo(), gridPosition);
                    String CardTitle = "Begin";
                    CardTitle = CardTitle.concat(CardName);
                    InputStream CardLibList = world.getGame().getFileIO().readAsset("CardLib.txt");
                    BufferedReader bufferedReaderCardLibList = new BufferedReader(new InputStreamReader(CardLibList));
                    String CardPackedInfo;
                    while (!CardTitle.equals(CardPackedInfo = bufferedReaderCardLibList.readLine()));
                    if (CardPackedInfo != null){
                        if (CardTitle.equals(CardPackedInfo)) {
                            CardPackedInfo = bufferedReaderCardLibList.readLine();
                            while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndSlotAttributes")) {
                                card.cardInfo().SlotAttributes.add(CardPackedInfo);
                            }
                            CardPackedInfo = bufferedReaderCardLibList.readLine();
                            while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndFlagAttributes")) {
                                card.cardInfo().FlagAttributes.add(CardPackedInfo);
                            }
                            CardPackedInfo = bufferedReaderCardLibList.readLine();
                            while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndPrimaryInstructionIndex")) {
                                card.cardInfo().PrimaryInstructionIndex.add(CardPackedInfo);
                            }
                            CardPackedInfo = bufferedReaderCardLibList.readLine();
                            while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndPrimaryInstructions")) {
                                card.cardInfo().PrimaryInstruction.add(CardPackedInfo);
                            }
                            CardPackedInfo = bufferedReaderCardLibList.readLine();
                            while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndCrossInstructionIndex")) {
                                card.cardInfo().CrossInstructionIndex.add(CardPackedInfo);
                            }
                            CardPackedInfo = bufferedReaderCardLibList.readLine();
                            while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndCrossInstruction")) {
                                card.cardInfo().CrossInstruction.add(CardPackedInfo);
                            }
                            CardPackedInfo = bufferedReaderCardLibList.readLine();
                        }
                    }
                    bufferedReaderCardLibList.close();
                    CardLibList.close();
                    InactiveCard icard = new InactiveCard(card.ExtractCardInfo(), card.GridPosition());
                    world.getMaze().getZoneList().get(2).getZoneArray().add(icard);
                    world.getGridIndexTrackingTable().trackGridIndex(icard.GridPosition(), icard);
                }
                bufferedReaderDeckList.close();
                DeckList.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                InputStream DeckList = world.getGame().getFileIO().readAsset("OpponentCreatures.txt");
                BufferedReader bufferedReaderDeckList = new BufferedReader(new InputStreamReader(DeckList));
                String CardName;
                int gridindex = 0;
                while ((CardName = bufferedReaderDeckList.readLine()) != null ) {
                    GridPositionIndex gridPosition = new GridPositionIndex(0, gridindex);
                    gridindex++;
                    Cards card = new Cards(new PackedCardInfo(), gridPosition);
                    String CardTitle = "Begin";
                    CardTitle = CardTitle.concat(CardName);
                    InputStream CardLibList = world.getGame().getFileIO().readAsset("CardLib.txt");
                    BufferedReader bufferedReaderCardLibList = new BufferedReader(new InputStreamReader(CardLibList));
                    String CardPackedInfo;
                    while (!CardTitle.equals(CardPackedInfo = bufferedReaderCardLibList.readLine()));
                    if (CardPackedInfo != null){
                        if (CardTitle.equals(CardPackedInfo)) {
                            CardPackedInfo = bufferedReaderCardLibList.readLine();
                            while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndSlotAttributes")) {
                                card.cardInfo().SlotAttributes.add(CardPackedInfo);
                            }
                            CardPackedInfo = bufferedReaderCardLibList.readLine();
                            while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndFlagAttributes")) {
                                card.cardInfo().FlagAttributes.add(CardPackedInfo);
                            }
                            CardPackedInfo = bufferedReaderCardLibList.readLine();
                            while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndPrimaryInstructionIndex")) {
                                card.cardInfo().PrimaryInstructionIndex.add(CardPackedInfo);
                            }
                            CardPackedInfo = bufferedReaderCardLibList.readLine();
                            while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndPrimaryInstructions")) {
                                card.cardInfo().PrimaryInstruction.add(CardPackedInfo);
                            }
                            CardPackedInfo = bufferedReaderCardLibList.readLine();
                            while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndCrossInstructionIndex")) {
                                card.cardInfo().CrossInstructionIndex.add(CardPackedInfo);
                            }
                            CardPackedInfo = bufferedReaderCardLibList.readLine();
                            while (!(CardPackedInfo = bufferedReaderCardLibList.readLine()).equals("EndCrossInstruction")) {
                                card.cardInfo().CrossInstruction.add(CardPackedInfo);
                            }
                            CardPackedInfo = bufferedReaderCardLibList.readLine();
                        }
                    }
                    bufferedReaderCardLibList.close();
                    CardLibList.close();
                    ActiveCard icard = new ActiveCard(card.ExtractCardInfo(), card.GridPosition());
                    world.getMaze().getZoneList().get(0).getZoneArray().add(icard);
                    world.getGridIndexTrackingTable().trackGridIndex(icard.GridPosition(), icard);
                    SetUnsetUtil.SetTappedAttr(icard);
                }
                bufferedReaderDeckList.close();
                DeckList.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}
