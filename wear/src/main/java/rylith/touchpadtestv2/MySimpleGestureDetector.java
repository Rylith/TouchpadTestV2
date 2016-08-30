
package rylith.touchpadtestv2;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;

import java.util.Random;

/**
 * Created by viallet on 15/07/2016.
 */
public class MySimpleGestureDetector extends GestureDetector.SimpleOnGestureListener {
    private MainActivity activity;

    public MySimpleGestureDetector(Activity activity){
        this.activity = (MainActivity) activity;
    }


    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Random rand = new Random();
        activity.sendMessage(MainActivity.WEAR_DATA_PATH,"DOUBLECLICK,"+rand.nextFloat());
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        MainActivity.brightness = -1f;
        lp.screenBrightness = MainActivity.brightness;
        activity.getWindow().setAttributes(lp);
        MainActivity.mDismissOverlay.show();
    }

    /*@Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        MainActivity.pos.setText("Scroll:\n" +"X: "+ e1.getX()+"\nY: "+e1.getY());
        activity.sendMessage(MainActivity.WEAR_DATA_PATH,"SCROLL,"+e2.getX()+","+e2.getY()+","+distanceX+","+distanceY);
        //board.drawLine(e1.getX(),e1.getY(),e2.getX(),e2.getY(),paint);
        //image.invalidate();
        return true;
    }*/

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        activity.sendMessage(MainActivity.WEAR_DATA_PATH,"CLICK");
        /*if(MainActivity.brightness !=0.0f && !MainActivity.PositionMode){
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            MainActivity.brightness = 0.0f;
            lp.screenBrightness = MainActivity.brightness;
            activity.getWindow().setAttributes(lp);
        }*/
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }
}
