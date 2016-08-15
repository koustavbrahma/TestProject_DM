package koustav.duelmasters.main.androidgameduelmasterwidgetlayoututil;

import java.util.List;

import koustav.duelmasters.main.androidgameduelmasterswidget.WidgetTouchEvent;
import koustav.duelmasters.main.androidgamesframework.Input;

/**
 * Created by Koustav on 4/23/2016.
 */
public interface Layout {
    public void update(float deltaTime, float totalTime);
    public void draw();
    public WidgetTouchEvent TouchResponse(List<Input.TouchEvent> touchEvents);
}
