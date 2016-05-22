package koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels;

import java.util.List;

import koustav.duelmasters.main.androidgameduelmastersdatastructure.InactiveCard;
import koustav.duelmasters.main.androidgameduelmastersutil.GetUtil;
import koustav.duelmasters.main.androidgameduelmasterswidget.WidgetPosition;
import koustav.duelmasters.main.androidgameduelmasterswidget.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayout.HeadOrientation;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayout.Layout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.CardWidget;
import koustav.duelmasters.main.androidgameopenglanimation.DriftSystem;
import koustav.duelmasters.main.androidgamesframework.Input;

/**
 * Created by Koustav on 5/1/2016.
 */
public class CardSlotLayout implements Layout {
    float headOrientationAngle;

    WidgetPosition slotPosition;
    DriftSystem driftSystem;
    CardWidget cardWidget;
    boolean Disturbed;
    boolean running;
    boolean TappedPreviousValue;

    public CardSlotLayout() {
        slotPosition = new WidgetPosition();
        driftSystem = new DriftSystem();
        Disturbed = false;
        running = false;
        TappedPreviousValue = false;
        cardWidget = null;
        headOrientationAngle = 0f;
    }

    @Override
    public void update(float deltaTime, float totalTime) {
        if (cardWidget == null) {
            return;
        }

        InactiveCard card = (InactiveCard) cardWidget.getLogicalObject();

        boolean tapped = GetUtil.IsTapped(card);

        if (tapped != TappedPreviousValue) {
            TappedPreviousValue = tapped;
            if (tapped) {
                slotPosition.rotaion.angle = 90f * (headOrientationAngle - 1f);
            } else {
                slotPosition.rotaion.angle = 90f * headOrientationAngle;
            }
            Disturbed = true;
        }

        if (Disturbed) {
            driftSystem.setDriftInfo(cardWidget.getPosition(), slotPosition, 2f, 2f, totalTime);
            Disturbed = false;
            running = true;
        }

        if (running) {
            WidgetPosition widgetPosition = driftSystem.getUpdatePosition(totalTime);
            cardWidget.setTranslateRotateScale(widgetPosition);

            float percentageComplete = driftSystem.getPercentageComplete(totalTime);
            if (percentageComplete == 1.0f) {
                running = false;
            }
        } else {
            cardWidget.setTranslateRotateScale(slotPosition);
        }

    }

    @Override
    public void draw() {
        if (cardWidget != null) {
            cardWidget.draw();
        }
    }

    @Override
    public WidgetTouchEvent TouchResponse(List<Input.TouchEvent> touchEvents) {
        if (cardWidget != null) {
            return cardWidget.isTouched(touchEvents);
        }

        return null;
    }

    public void initializeSlot(float x, float y, float z, CardWidget cardWidget, HeadOrientation headOrientation) {
        if (headOrientation == HeadOrientation.North) {
            headOrientationAngle = 0f;
        } else if (headOrientation == HeadOrientation.West) {
            headOrientationAngle = 1f;
        } else if (headOrientation == HeadOrientation.South) {
            headOrientationAngle = 2f;
        } else {
            headOrientationAngle = 3f;
        }

        slotPosition.Centerposition.x = x;
        slotPosition.Centerposition.y = y;
        slotPosition.Centerposition.z = z;
        slotPosition.rotaion.angle = 90f * headOrientationAngle;
        slotPosition.rotaion.x = 0f;
        slotPosition.rotaion.y = 1f;
        slotPosition.rotaion.z = 0f;
        slotPosition.X_scale = 1f;
        slotPosition.Y_scale = 1f;
        slotPosition.Z_scale = 1f;

        this.cardWidget = cardWidget;

        Disturbed = true;
    }

    public void resetSlot() {
        this.cardWidget = null;
    }

    public void setSlotXPosition(float x) {
        slotPosition.Centerposition.x = x;
        Disturbed = true;
    }

    public CardWidget getCardWidget() {
        return cardWidget;
    }
}
