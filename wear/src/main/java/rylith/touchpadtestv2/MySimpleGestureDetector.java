package rylith.touchpadtestv2;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

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
        activity.sendMessage(MainActivity.WEAR_DATA_PATH,"DOUBLECLICK");
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
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
        MainActivity.pos.setText("Pos:\n" +"X: "+ event.getX()+"\nY: "+event.getY());
        MainActivity.paint.setColor(Color.BLUE);
        MainActivity.board.drawPoint(event.getX(),event.getY(),MainActivity.paint);
        MainActivity.paint.setColor(Color.GREEN);
        MainActivity.image.invalidate();
        activity.sendMessage(MainActivity.WEAR_DATA_PATH,"CLICK");
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }
}
