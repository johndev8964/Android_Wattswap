package com.citrusbug.wattswap.util;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class OnSwipeTouchListener implements OnTouchListener {
  Context mContext;
  @SuppressWarnings("unused")
private Action mSwipeDetected = Action.None; // Last action
  
	public OnSwipeTouchListener(Context mContext){
	  this.mContext=mContext;	
	}
	
	@SuppressWarnings("deprecation")
	private final GestureDetector gestureDetector = new GestureDetector(new GestureListener());

    @Override
	public boolean onTouch(final View view, final MotionEvent motionEvent) {

        /// super.onTouch(view, motionEvent);
        return gestureDetector.onTouchEvent(motionEvent);

    }

    private final class GestureListener extends SimpleOnGestureListener {
      private static final int SWIPE_MIN_DISTANCE = 30;
      //private static final int SWIPE_MAX_OFF_PATH = 100;
      private static final int SWIPE_THRESHOLD_VELOCITY = 25;
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            boolean result = false;
            try {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();

                if (Math.abs(diffX) > Math.abs(diffY)) {
                  //  if (Math.abs(diffX) > MainActivity.SWIPE_THRESHOLD && Math.abs(velocityX) >  MainActivity.SWIPE_VELOCITY_THRESHOLD) {
                	  if (Math.abs(diffX) >SWIPE_MIN_DISTANCE && Math.abs(velocityX) >SWIPE_THRESHOLD_VELOCITY){ 	
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                }{
                	
                }
//                else {
//                    if (Math.abs(diffY) >  MainActivity.SWIPE_THRESHOLD && Math.abs(velocityY) >  MainActivity.SWIPE_VELOCITY_THRESHOLD) {
//                        if (diffY > 0) {
//                            onSwipeBottom();
//                        } else {
//                            onSwipeTop();
//                        }
//                    }
//                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }
    public void onSwipeRight() {}
    public void onSwipeLeft() {}
    public void onSwipeTop() {}
    public void onSwipeBottom() {}
    
    public static enum Action {
        LR, // Left to right
        RL, // Right to left
        TB, // Top to bottom
        BT, // Bottom to top
        None // Action not found
    }

}
