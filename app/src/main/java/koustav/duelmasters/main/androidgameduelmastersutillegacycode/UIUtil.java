package koustav.duelmasters.main.androidgameduelmastersutillegacycode;

import java.util.ArrayList;
import java.util.List;

import koustav.duelmasters.main.androidgameduelmastersdatastructure.Cards;
import koustav.duelmasters.main.androidgameduelmasterswidgetscoordinator.GridPositionIndex;
import koustav.duelmasters.main.androidgameduelmastersworlds.PvPWorld;
import koustav.duelmasters.main.androidgamesframework.Input.TouchEvent;

/**
 * Created by Koustav on 5/3/2015.
 */
public class UIUtil {
    public static GridPositionIndex TouchedGridPositionIndex(PvPWorld world) {
        List<TouchEvent> touchEvents = world.getTouchEvents();
        int w = world.getframeBufferWidht()/8;
        int h = world.getframeBufferHeight()/10;
        int length = touchEvents.size();
        int x = 0;
        int y = 0;
        for (int i = 0; i<length; i++) {
            TouchEvent event = touchEvents.get(i);
            if(event.type == TouchEvent.TOUCH_UP) {
                if(event.y > (h * 9))
                    y = 1;
                else if (event.y > (h * 8))
                    y = 2;
                else if (event.y > (h * 7))
                    y = 3;
                else if (event.y > (h * 6))
                    y = 4;
                else if (event.y > (h * 5))
                    y = 5;
                else if (event.y > (h * 4))
                    y = 6;
                else if (event.y > (h * 3))
                    y = 7;
                else if (event.y > (h * 2))
                    y = 8;
                else if (event.y > (h * 1))
                    y = 9;
                else if (event.y > 0)
                    y = 10;
                else
                    y = 0;

                if (event.x < w)
                    x = 1;
                else if (event.x < (w *2))
                    x = 2;
                else if (event.x < (w * 3))
                    x = 3;
                else if (event.x < (w * 4))
                    x = 4;
                else if (event.x < (w * 5))
                    x = 5;
                else if (event.x < (w *6))
                    x = 6;
                else if (event.x < (w * 7))
                    x = 7;
                else if (event.x < (w * 8))
                    x = 8;
                else
                    x = 0;
            }
        }
        GridPositionIndex gridPositionIndex = null;
        switch (y) {
            case 1:
                gridPositionIndex = new GridPositionIndex(3 , x-1);
                break;
            case 2:
                gridPositionIndex = new GridPositionIndex(1, x-1);
                break;
            case 3:
                if (x >= 1 && x <= 6) {
                    gridPositionIndex = new GridPositionIndex(2, x-1);
                } else if (x == 7) {
                    gridPositionIndex = new GridPositionIndex(5, 0);
                } else if (x == 8) {
                    gridPositionIndex = new GridPositionIndex(4, 0);
                }
                break;
            case 4:
                gridPositionIndex = new GridPositionIndex(0 , x-1);
                break;
            case 6:
                gridPositionIndex = new GridPositionIndex(7, 7 - (x-1));
                break;
            case 7:
                if (x >= 3 && x <= 8) {
                    gridPositionIndex = new GridPositionIndex(9,7 - (x-1));
                }
                break;
            case 8:
                gridPositionIndex = new GridPositionIndex(8,7 - (x-1));
                break;
            case 9:
                gridPositionIndex = new GridPositionIndex(10,7 - (x-1));
                break;
            default:
                gridPositionIndex = null;
        }
        return gridPositionIndex;

    }

    public static boolean TouchedInfoTabEnterButton(PvPWorld world) {
        boolean status = false;
        int w = world.getframeBufferWidht()/8;
        int h = world.getframeBufferHeight()/10;
        List<TouchEvent> touchEvents = world.getTouchEvents();
        for(int i = 0; i<touchEvents.size(); i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if ((event.y > (h  * 5) && event.y <= (h * 6)) && (event.x >= (w * 4) && event.x < (w * 5))) {
                    status = true;
                }
            }
        }
        return status;
    }

    public static boolean TouchedInfoTabBackButton(PvPWorld world) {
        boolean status = false;
        int w = world.getframeBufferWidht()/8;
        int h = world.getframeBufferHeight()/10;
        List<TouchEvent> touchEvents = world.getTouchEvents();
        for(int i = 0; i<touchEvents.size(); i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if ((event.y <= h) && (event.x >= (w * 7) && event.x < (w * 8))) {
                    status = true;
                }
            }
        }
        return status;
    }

    public static boolean TouchedSkippedButton(PvPWorld world) {
        boolean status = false;
        int w = world.getframeBufferWidht()/8;
        int h = world.getframeBufferHeight()/10;
        List<TouchEvent> touchEvents = world.getTouchEvents();
        for(int i = 0; i<touchEvents.size(); i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if ((event.y > (h * 5) && event.y <= (h * 6)) && (event.x >= (w * 7) && event.x < (w * 8))) {
                    status = true;
                }
            }
        }
        return status;
    }

    public static boolean TouchedLeftPlayButton(PvPWorld world) {
        boolean status = false;
        int w = world.getframeBufferWidht()/8;
        int h = world.getframeBufferHeight()/10;
        List<TouchEvent> touchEvents = world.getTouchEvents();
        for(int i = 0; i<touchEvents.size(); i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if ((event.y > (h * 5) && event.y <= (h * 6)) && (event.x < w)) {
                    status = true;
                }
            }
        }
        return status;
    }

    public static boolean TouchedRightPlayButton(PvPWorld world) {
        boolean status = false;
        int w = world.getframeBufferWidht()/8;
        int h = world.getframeBufferHeight()/10;
        List<TouchEvent> touchEvents = world.getTouchEvents();
        for(int i = 0; i<touchEvents.size(); i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if ((event.y > (h * 5) && event.y <= (h * 6)) && (event.x >= (w * 2) && event.x < (w * 3))) {
                    status = true;
                }
            }
        }
        return status;
    }

    public static boolean TouchedDrawCardButton(PvPWorld world) {
        boolean status = false;
        int w = world.getframeBufferWidht()/8;
        int h = world.getframeBufferHeight()/10;
        List<TouchEvent> touchEvents = world.getTouchEvents();
        for(int i = 0; i<touchEvents.size(); i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if ((event.y > (h * 5) && event.y <= (h * 6)) && (event.x >= (w * 6) && event.x < (w * 7))) {
                    status = true;
                }
            }
        }
        return status;
    }

    public static boolean TouchedAcceptButton(PvPWorld world) {
        boolean status = false;
        int w = world.getframeBufferWidht()/8;
        int h = world.getframeBufferHeight()/10;
        List<TouchEvent> touchEvents = world.getTouchEvents();
        for(int i = 0; i<touchEvents.size(); i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if ((event.y <= h) && (event.x >= (w * 2) && event.x < (w * 3))) {
                    status = true;
                }
            }
        }
        if (status) {
            touchEvents = world.getGame().getInput().getTouchEvents();
            world.setTouchEvents(touchEvents);
        }
        return status;
    }

    public static boolean TouchedDeclineButton(PvPWorld world) {
        boolean status = false;
        int w = world.getframeBufferWidht()/8;
        int h = world.getframeBufferHeight()/10;
        List<TouchEvent> touchEvents = world.getTouchEvents();
        for(int i = 0; i<touchEvents.size(); i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if ((event.y <= h) && (event.x >= (w * 4) && event.x < (w * 5))) {
                    status = true;
                }
            }
        }
        return status;
    }

    public static boolean TouchedAttackShieldOrPlayerButton(PvPWorld world) {
        boolean status = false;
        int w = world.getframeBufferWidht()/8;
        int h = world.getframeBufferHeight()/10;
        List<TouchEvent> touchEvents = world.getTouchEvents();
        for(int i = 0; i<touchEvents.size(); i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if ((event.y <= h) && (event.x >= (w * 6) && event.x < (w * 7))) {
                    status = true;
                }
            }
        }
        if (status) {
            touchEvents = world.getGame().getInput().getTouchEvents();
            world.setTouchEvents(touchEvents);
        }
        return status;
    }

    public static boolean TouchedMaze(PvPWorld world) {
        boolean status = false;
        int h = world.getframeBufferHeight()/10;
        List<TouchEvent> touchEvents = world.getTouchEvents();
        for(int i = 0; i<touchEvents.size(); i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if (event.y > h) {
                    status = true;
                }
            }
        }
        return status;
    }

    public static Integer ZoneIndexToYCoordinateTransform(int zone, int height) {
        int h = height / 10;
        if (zone == 0)
            return new Integer(h * 6);
        if (zone == 1)
            return new Integer(h * 8);
        if (zone == 2)
            return new Integer(h * 7);
        if (zone == 3)
            return new Integer(h * 9);
        if (zone == 7)
            return new Integer(h * 4);
        if (zone == 8)
            return new Integer(h * 2);
        if (zone == 9)
            return new Integer(h * 3);
        if (zone == 10)
            return new Integer(h * 1);

        return null;
    }

    public static Integer GridPositionToXCoordinateTransform(int gridIndex, int width) {
        int w = width/8;
        if (gridIndex >= 0 && gridIndex <=7)
            return new Integer(w * gridIndex);

        return null;
    }

    public static boolean WorldFetchCard(PvPWorld world) {
        GridPositionIndex gridPositionIndex = TouchedGridPositionIndex(world);

        /*if (gridPositionIndex != null) {
            Cards card = world.getGridIndexTrackingTable().getCardMappedToGivenGridPosition(gridPositionIndex.getZone(),
                    gridPositionIndex.getGridIndex());
            world.setFetchCard(card);
            return true;
        } */

        return false;
    }

    public static Cards GetTouchedTrackCard(PvPWorld world) {
        GridPositionIndex gridPositionIndex = TouchedGridPositionIndex(world);

        if (gridPositionIndex != null) {
         //   Cards card = world.getGridIndexTrackingTable().getCardMappedToGivenGridPosition(gridPositionIndex.getZone(),
             //       gridPositionIndex.getGridIndex());
           // return card;
        }

        return null;
    }

    public static void TrackSelectedCardsWhenUserIsChoosing(ArrayList<Cards> trackArray, ArrayList<Cards> ChooseList, Cards card) {
        if (ChooseList.contains(card)) {
            if (trackArray.contains(card)) {
                int i = trackArray.indexOf(card);
                trackArray.remove(i);
            } else {
                trackArray.add(card);
            }
        }
    }
}
