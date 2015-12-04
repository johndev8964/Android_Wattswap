package com.citrusbug.wattswap.util;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Class swipe detection to View
 */
public class SwipeDetector implements View.OnTouchListener {

    public static enum Action {
        LR, // Left to right
        RL, // Right to left
        TB, // Top to bottom
        BT, // Bottom to top
        None // Action not found
    }

    private static final int HORIZONTAL_MIN_DISTANCE = 20; // The minimum
    // distance for
    // horizontal swipe
    private static final int VERTICAL_MIN_DISTANCE = 50; // The minimum distance
    // for vertical
    // swipe
    private float downX, downY, upX, upY; // Coordinates
    private Action mSwipeDetected = Action.None; // Last action

    public boolean swipeDetected() {
        return mSwipeDetected != Action.None;
    }

    public Action getAction() {
        return mSwipeDetected;
    }

    /**
     * Swipe detection
     */@Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            {
                downX = event.getX();
                downY = event.getY();
                Log.w("Tag","downX:"+downX+";downY:"+downY);
                mSwipeDetected = Action.None;
                return false; // allow other events like Click to be processed
            }
        case MotionEvent.ACTION_MOVE:
            {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;
                Log.w("Tag","deltaX:"+deltaX+";deltaY:"+deltaY+";Math.abs(deltaX)"+Math.abs(deltaX));
                // horizontal swipe detection
                if (Math.abs(deltaX) > HORIZONTAL_MIN_DISTANCE) {
                    // left or right
                    if (deltaX < 0) {
                        mSwipeDetected = Action.LR;
                        onSwipeRight();
                        return true;
                    }
                    if (deltaX > 0) {
                        mSwipeDetected = Action.RL;
                        onSwipeLeft();
                        return true;
                    }
                } else

                // vertical swipe detection
                if (Math.abs(deltaY) > VERTICAL_MIN_DISTANCE) {
                    // top or down
                    if (deltaY < 0) {

                        mSwipeDetected = Action.TB;
                        return false;
                    }
                    if (deltaY > 0) {

                        mSwipeDetected = Action.BT;
                        return false;
                    }
                }
                return true;
            }
        case MotionEvent.ACTION_UP:
        {
            onActionUp();
            return false; // allow other events like Click to be processed
        }
        }
        return false;
    }
     public void onSwipeRight() {}
     public void onSwipeLeft() {}
     public void onActionUp(){}
}