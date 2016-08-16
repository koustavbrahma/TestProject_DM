package koustav.duelmasters.main.androidgameduelmasterwidgetlayoutmodels;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import koustav.duelmasters.main.androidgameassetsandresourcesallocator.AssetsAndResource;
import koustav.duelmasters.main.androidgameduelmasterswidgetutil.WidgetTouchEvent;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.ControllerButton;
import koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil.Layout;
import koustav.duelmasters.main.androidgameduelmasterwidgetmodels.RectangleButtonWidget;
import koustav.duelmasters.main.androidgamesframework.Input;

/**
 * Created by Koustav on 8/5/2016.
 */
public class ControllerLayout implements Layout {
    Hashtable<ControllerButton, ButtonSlotLayout> ControllerTypeToLayout;
    ArrayList<ControllerButton> Buttons;

    ControllerButton selectedButton;

    float k1;
    float k2;

    float gap;
    float Y;

    ArrayList<ControllerButton> TouchedButtons;
    ArrayList<WidgetTouchEvent> widgetTouchEventList;

    public ControllerLayout() {
        ControllerTypeToLayout = new Hashtable<ControllerButton, ButtonSlotLayout>();
        Buttons = new ArrayList<ControllerButton>();

        selectedButton = null;

        k1 = 4f;
        k2 = 4f;

        gap = 0.4f;
        Y = 0.85f;

        TouchedButtons = new ArrayList<ControllerButton>();
        widgetTouchEventList = new ArrayList<WidgetTouchEvent>();
    }

    @Override
    public void update(float deltaTime, float totalTime) {
        for (int i = 0; i < Buttons.size(); i++) {
            ControllerButton button = Buttons.get(i);
            ButtonSlotLayout layout = ControllerTypeToLayout.get(button);
            if (layout != null) {
                layout.update(deltaTime, totalTime);
            }
        }
    }

    @Override
    public void draw() {
        int index = Buttons.indexOf(selectedButton);
        ButtonSlotLayout layout;

        for (int i = 0; i <index; i++) {
            ControllerButton button = Buttons.get(i);
            layout = ControllerTypeToLayout.get(button);
            if (layout != null) {
                layout.draw();
            }
        }

        for (int i = Buttons.size() - 1; i >= index; i--) {
            ControllerButton button = Buttons.get(i);
            layout = ControllerTypeToLayout.get(button);
            if (layout != null) {
                layout.draw();
            }
        }
    }

    @Override
    public WidgetTouchEvent TouchResponse(List<Input.TouchEvent> touchEvents) {
        if (selectedButton == null) {
            return null;
        }

        if (Buttons.size() == 0) {
            return null;
        }
        TouchedButtons.clear();
        widgetTouchEventList.clear();

        WidgetTouchEvent widgetTouchEvent = null;
        int size = Buttons.size();
        float totalLength = gap * (size);
        float startingPoint = (gap * (size - 1)) /2;

        Input input = AssetsAndResource.game.getInput();
        if (input.isTouchDown(0)) {
            float relative_x = input.getNormalizedX(0);
            float relative_y = input.getNormalizedY(0) - Y;

            float width = Math.abs(relative_x);
            float length = Math.abs(relative_y);

            float x;
            int index;

            if (width <= totalLength/2 && length <= gap/2) {
                x = (relative_x + startingPoint) / gap;

                if (x > Buttons.size()) {
                    index = Buttons.size() - 1;
                } else if (x > 0){
                    index = (int) Math.floor(x);
                } else {
                    index = 0;
                }

                for (int i = index; i>= 0 ; i--) {
                    ControllerButton button = Buttons.get(index);
                    ButtonSlotLayout slotLayout = ControllerTypeToLayout.get(button);

                    if (slotLayout != null) {
                        widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                        if (widgetTouchEvent.isTouched) {
                            TouchedButtons.add(0, button);
                            widgetTouchEventList.add(0, widgetTouchEvent);

                            if (button != widgetTouchEvent.object) {
                                throw new RuntimeException("Invaild Condition");
                            }
                        } else {
                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                            break;
                        }
                    } else {
                        break;
                    }
                }

                for (int i = index + 1; i < Buttons.size(); i++) {
                    ControllerButton button = Buttons.get(i);
                    ButtonSlotLayout slotLayout = ControllerTypeToLayout.get(button);

                    if (slotLayout != null) {
                        widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                        if (widgetTouchEvent.isTouched) {
                            TouchedButtons.add(button);
                            widgetTouchEventList.add(widgetTouchEvent);
                            if (button != widgetTouchEvent.object) {
                                throw new RuntimeException("Invaild Condition");
                            }
                        } else {
                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                            break;
                        }
                    } else {
                        break;
                    }
                }

                if (TouchedButtons.size() > 0) {
                    index = Buttons.indexOf(selectedButton);
                    if (index < Buttons.indexOf(TouchedButtons.get(0))) {
                        selectedButton = TouchedButtons.get(0);
                        widgetTouchEvent = widgetTouchEventList.remove(0);

                        for (int i = 0; i < widgetTouchEventList.size(); i++) {
                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                        }

                        widgetTouchEventList.clear();
                        TouchedButtons.clear();
                        return widgetTouchEvent;
                    } else if (index > Buttons.indexOf(TouchedButtons.get(TouchedButtons.size() -1))) {
                        selectedButton = TouchedButtons.get(TouchedButtons.size() - 1);
                        widgetTouchEvent = widgetTouchEventList.remove(TouchedButtons.size() - 1);

                        for (int i = 0; i < widgetTouchEventList.size(); i++) {
                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                        }

                        widgetTouchEventList.clear();
                        TouchedButtons.clear();
                        return widgetTouchEvent;
                    } else {
                        int i = TouchedButtons.indexOf(selectedButton);
                        widgetTouchEvent = widgetTouchEventList.remove(i);

                        for (i = 0; i < widgetTouchEventList.size(); i++) {
                            AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                        }

                        widgetTouchEventList.clear();
                        TouchedButtons.clear();
                        return widgetTouchEvent;
                    }
                }
                return null;
            }
        } else {
            WidgetTouchEvent widgetTouchEventOutCome = null;
            Input.TouchEvent event = null;
            for (int j = 0; j < touchEvents.size(); j++) {
                event = touchEvents.get(j);
                widgetTouchEvent = null;
                if (event.type == Input.TouchEvent.TOUCH_UP) {
                    float relative_x = event.normalizedX;
                    float relative_y = event.normalizedY - Y;

                    float width = Math.abs(relative_x);
                    float length = Math.abs(relative_y);

                    float x;
                    int index;

                    if (width <= totalLength/2 && length <= gap/2) {
                        x = (relative_x + startingPoint) / gap;

                        if (x > Buttons.size()) {
                            index = Buttons.size() - 1;
                        } else if (x > 0){
                            index = (int) Math.floor(x);
                        } else {
                            index = 0;
                        }

                        for (int i = index; i>= 0 ; i--) {
                            ControllerButton button = Buttons.get(index);
                            ButtonSlotLayout slotLayout = ControllerTypeToLayout.get(button);

                            if (slotLayout != null) {
                                widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                                if (widgetTouchEvent.isTouched) {
                                    TouchedButtons.add(0, button);
                                    widgetTouchEventList.add(0, widgetTouchEvent);

                                    if (button != widgetTouchEvent.object) {
                                        throw new RuntimeException("Invaild Condition");
                                    }
                                } else {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                    break;
                                }
                            } else {
                                break;
                            }
                        }

                        for (int i = index + 1; i < Buttons.size(); i++) {
                            ControllerButton button = Buttons.get(i);
                            ButtonSlotLayout slotLayout = ControllerTypeToLayout.get(button);

                            if (slotLayout != null) {
                                widgetTouchEvent = slotLayout.TouchResponse(touchEvents);
                                if (widgetTouchEvent.isTouched) {
                                    TouchedButtons.add(button);
                                    widgetTouchEventList.add(widgetTouchEvent);
                                    if (button != widgetTouchEvent.object) {
                                        throw new RuntimeException("Invaild Condition");
                                    }
                                } else {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEvent);
                                    break;
                                }
                            } else {
                                break;
                            }
                        }

                        if (TouchedButtons.size() > 0) {
                            index = Buttons.indexOf(selectedButton);
                            if (index < Buttons.indexOf(TouchedButtons.get(0))) {
                                selectedButton = TouchedButtons.get(0);
                                widgetTouchEvent = widgetTouchEventList.remove(0);

                                for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                }

                                widgetTouchEventList.clear();
                                TouchedButtons.clear();
                                widgetTouchEventOutCome = widgetTouchEvent;
                            } else if (index > Buttons.indexOf(TouchedButtons.get(TouchedButtons.size() -1))) {
                                selectedButton = TouchedButtons.get(TouchedButtons.size() - 1);
                                widgetTouchEvent = widgetTouchEventList.remove(TouchedButtons.size() - 1);

                                for (int i = 0; i < widgetTouchEventList.size(); i++) {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                }

                                widgetTouchEventList.clear();
                                TouchedButtons.clear();
                                widgetTouchEventOutCome = widgetTouchEvent;
                            } else {
                                int i = TouchedButtons.indexOf(selectedButton);
                                widgetTouchEvent = widgetTouchEventList.remove(i);

                                for (i = 0; i < widgetTouchEventList.size(); i++) {
                                    AssetsAndResource.widgetTouchEventPool.free(widgetTouchEventList.get(i));
                                }

                                widgetTouchEventList.clear();
                                TouchedButtons.clear();
                                widgetTouchEventOutCome = widgetTouchEvent;
                            }
                        }
                    }
                }
            }

            return widgetTouchEventOutCome;
        }

        return null;
    }

    public void AddButtonWidget(ControllerButton button, RectangleButtonWidget widget) {
        ButtonSlotLayout layout = new ButtonSlotLayout();
        layout.intializeButton(0, 1.1f, 0, widget, k1, k2);
        ControllerTypeToLayout.remove(button);
        ControllerTypeToLayout.put(button, layout);
    }

    public void setControllerButton(Object ...obj) {
        Buttons.clear();
        for (int i = 0; i < obj.length; i++) {
            ControllerButton button = (ControllerButton) obj[i];
            Buttons.add(button);
        }

        selectedButton = Buttons.get(0);

        Set<ControllerButton> keys = ControllerTypeToLayout.keySet();
        for (ControllerButton key: keys){
            if (!Buttons.contains(key)) {
                ButtonSlotLayout layout = ControllerTypeToLayout.get(key);
                if (layout != null) {
                    layout.setButtonPosition(0, 1.1f);
                    layout.forceLoadButtonPosition();
                }
            }
        }

        int size = Buttons.size();
        float length = gap * (size - 1);

        for (int i = 0; i < Buttons.size(); i++) {
            ControllerButton button = Buttons.get(i);
            ButtonSlotLayout layout = ControllerTypeToLayout.get(button);
            if (layout != null) {
                layout.setButtonPosition((-length / 2) + (gap * i), Y);
            }
        }
    }

    public void unsetControllerButton() {
        Buttons.clear();

        selectedButton = null;

        Set<ControllerButton> keys = ControllerTypeToLayout.keySet();
        for (ControllerButton key: keys){
            ButtonSlotLayout layout = ControllerTypeToLayout.get(key);
            if (layout != null) {
                layout.setButtonPosition(0, 1.1f);
                layout.forceLoadButtonPosition();
            }
        }
    }
}
