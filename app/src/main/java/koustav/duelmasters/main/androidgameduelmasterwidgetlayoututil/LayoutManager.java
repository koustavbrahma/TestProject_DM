package koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil;

import java.util.ArrayList;
import java.util.Hashtable;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.PvPWidgetCoordinator;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.BattleZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.CardStackZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.ControllerLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.FixedButtonsLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.HandZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels.ManaZoneLayout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardStackWidget;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.RectangleButtonWidget;

/**
 * Created by Koustav on 1/21/2017.
 */
public class LayoutManager {
    PvPWidgetCoordinator coordinator;

    // Zone Layout
    public BattleZoneLayout battleZoneLayout;
    public BattleZoneLayout opponentBattleZoneLayout;
    public ManaZoneLayout manaZoneLayout;
    public ManaZoneLayout opponentManaZoneLayout;
    public CardStackZoneLayout deckLayout;
    public CardStackZoneLayout opponentDeckLayout;
    public CardStackZoneLayout graveyardLayout;
    public CardStackZoneLayout opponentGraveyardLayout;
    public HandZoneLayout handZoneLayout;

    // Menu Buttons
    public FixedButtonsLayout fixedButtonsLayout;

    // Control Buttons
    public ControllerLayout controllerLayout;

    public LayoutManager(PvPWidgetCoordinator coordinator, CardStackWidget Deck, CardStackWidget Opponent_Deck,
                         CardStackWidget Graveyard, CardStackWidget Opponent_Graveyard,
                         RectangleButtonWidget pauseButton, RectangleButtonWidget EndTurnButton,
                         RectangleButtonWidget PlayerButton, RectangleButtonWidget OpponentButton,
                         RectangleButtonWidget AcceptButton, RectangleButtonWidget DeclineButton,
                         RectangleButtonWidget SummonOrCastButton, RectangleButtonWidget AddToManaButton,
                         RectangleButtonWidget AttackButton, RectangleButtonWidget BlockButton,
                         RectangleButtonWidget TapAbilityButton, RectangleButtonWidget ZoomButton) {
        this.coordinator = coordinator;

        battleZoneLayout = new BattleZoneLayout();
        opponentBattleZoneLayout = new BattleZoneLayout();
        manaZoneLayout = new ManaZoneLayout();
        opponentManaZoneLayout = new ManaZoneLayout();
        deckLayout = new CardStackZoneLayout(coordinator);
        opponentDeckLayout = new CardStackZoneLayout(coordinator);
        graveyardLayout = new CardStackZoneLayout(coordinator);
        opponentGraveyardLayout = new CardStackZoneLayout(coordinator);
        handZoneLayout = new HandZoneLayout();

        battleZoneLayout.InitializeBattleZoneLayout(AssetsAndResource.MazeHeight/10, AssetsAndResource.MazeWidth,
                AssetsAndResource.MazeHeight/5, HeadOrientation.North, false, 4f, 4f);
        opponentBattleZoneLayout.InitializeBattleZoneLayout(-AssetsAndResource.MazeHeight/10, AssetsAndResource.MazeWidth,
                AssetsAndResource.MazeHeight/5, HeadOrientation.South, true, 4f, 4f);
        manaZoneLayout.InitializeBattleZoneLayout((3f * AssetsAndResource.MazeHeight)/10, AssetsAndResource.MazeWidth,
                AssetsAndResource.MazeHeight/5, HeadOrientation.North, false, 4f, 4f);
        opponentManaZoneLayout.InitializeBattleZoneLayout(-(3f * AssetsAndResource.MazeHeight)/10, AssetsAndResource.MazeWidth,
                AssetsAndResource.MazeHeight/5, HeadOrientation.South, true, 4f, 4f);
        deckLayout.InitializeCardStackZoneLayout(0.68f * AssetsAndResource.MazeWidth, AssetsAndResource.CardLength * 20f,
                0.15f * AssetsAndResource.MazeHeight, 30f, 0, 1f, 0f, Deck);
        opponentDeckLayout.InitializeCardStackZoneLayout(-(0.68f * AssetsAndResource.MazeWidth), AssetsAndResource.CardLength * 20f,
                -(0.15f * AssetsAndResource.MazeHeight), 210f, 0, 1f, 0f, Opponent_Deck);
        graveyardLayout.InitializeCardStackZoneLayout(0.65f * AssetsAndResource.MazeWidth, AssetsAndResource.CardLength * 20f,
                0.35f * AssetsAndResource.MazeHeight, 30f, 0, 1f, 0f, Graveyard);
        opponentGraveyardLayout.InitializeCardStackZoneLayout(-(0.65f * AssetsAndResource.MazeWidth), AssetsAndResource.CardLength * 20f,
                -(0.35f * AssetsAndResource.MazeHeight), 210f, 0, 1f, 0f, Opponent_Graveyard);
        handZoneLayout.InitializeHandZoneLayout(AssetsAndResource.MazeWidth, -0.8f, AssetsAndResource.CameraPosition.x/4,
                AssetsAndResource.CameraPosition.y/4, AssetsAndResource.CameraPosition.z/4, 4f, 4f);

        // Initialize Menu Button
        fixedButtonsLayout = new FixedButtonsLayout();
        fixedButtonsLayout.InitializeFixedButtonLayout(pauseButton, EndTurnButton, PlayerButton, OpponentButton);

        // Initialize Control Button
        controllerLayout = new ControllerLayout();
        controllerLayout.setControllerOrientation(true);

        // Added Button To the Controller Layout
        controllerLayout.AddButtonWidget(ControllerButton.Accept, AcceptButton);
        controllerLayout.AddButtonWidget(ControllerButton.Decline, DeclineButton);
        controllerLayout.AddButtonWidget(ControllerButton.SummonOrCast, SummonOrCastButton);
        controllerLayout.AddButtonWidget(ControllerButton.AddToMana, AddToManaButton);
        controllerLayout.AddButtonWidget(ControllerButton.Attack, AttackButton);
        controllerLayout.AddButtonWidget(ControllerButton.Block, BlockButton);
        controllerLayout.AddButtonWidget(ControllerButton.TapAbility, TapAbilityButton);
        controllerLayout.AddButtonWidget(ControllerButton.Zoom, ZoomButton);

    }

    public void update(float deltaTime, float totalTime) {
        battleZoneLayout.update(deltaTime, totalTime);
        opponentBattleZoneLayout.update(deltaTime, totalTime);
        manaZoneLayout.update(deltaTime, totalTime);
        opponentManaZoneLayout.update(deltaTime, totalTime);
        deckLayout.update(deltaTime, totalTime);
        opponentDeckLayout.update(deltaTime, totalTime);
        graveyardLayout.update(deltaTime, totalTime);
        opponentGraveyardLayout.update(deltaTime, totalTime);
        handZoneLayout.update(deltaTime, totalTime);

        controllerLayout.update(deltaTime, totalTime);
    }

    public void drawPerspectiveProObj() {
        battleZoneLayout.draw();
        opponentBattleZoneLayout.draw();
        manaZoneLayout.draw();
        opponentManaZoneLayout.draw();
        deckLayout.draw();
        opponentDeckLayout.draw();
        graveyardLayout.draw();
        opponentGraveyardLayout.draw();
        handZoneLayout.draw();
    }

    public void drawOrthoProObj() {
        fixedButtonsLayout.draw();
        controllerLayout.draw();
    }
}
