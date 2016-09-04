package rylith.touchpadtestv2;

import android.app.Activity;
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
        String msg = "DOUBLECLICK";
        activity.sendMessage(MainActivity.WEAR_DATA_PATH,msg);
        //Log.v("DOUBLECLICK","double tap send: "+msg);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        if(!MainActivity.detectMovement){
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            MainActivity.brightness = -1f;
            lp.screenBrightness = MainActivity.brightness;
            activity.getWindow().setAttributes(lp);
            MainActivity.mDismissOverlay.show();
        }
    }


    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        activity.sendMessage(MainActivity.WEAR_DATA_PATH,"CLICK");
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }
}
