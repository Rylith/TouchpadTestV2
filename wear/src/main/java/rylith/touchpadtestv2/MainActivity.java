package rylith.touchpadtestv2;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.wearable.view.DismissOverlayView;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.w3c.dom.Text;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks{

    private DismissOverlayView mDismissOverlay;
    private GestureDetector mDetector;
    private TextView pos;

    private Canvas board;
    private Bitmap sheet;
    private Paint paint;
    private ImageView image;
    private float downx = 0, downy = 0, upx = 0, upy = 0;

    private static final String START_ACTIVITY ="/start_activity";
    private static final String WEAR_MESSAGE_PATH = "/message";
    private GoogleApiClient mApiClient;
    private ArrayAdapter<String> mAdapter;

    //private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new ArrayAdapter<String>( this, R.layout.list_item );

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                //mListView = (ListView) findViewById(R.id.list);
                //mListView.setAdapter( mAdapter );

                pos = (TextView) findViewById(R.id.pos);
                if (pos != null)
                {
                    Log.v("Debug","POS NON NULL");
                }
                else {
                    Log.v("Debug","POS NULLE");
                }
                pos.setText("Position:");
                image = (ImageView) findViewById(R.id.image);
                if (image != null)
                {
                    Log.v("Debug","IMAGE NON NULL");
                }
                else {
                    Log.v("Debug","IMAGE NULLE");
                }

                // Obtain the DismissOverlayView element
                mDismissOverlay = (DismissOverlayView) findViewById(R.id.dismiss_overlay);
                mDismissOverlay.setIntroText("Long Press to dismiss");
                mDismissOverlay.showIntroIfNecessary();

                //Creating canvas for drawing
                Point screenSize = new Point();
                getWindowManager().getDefaultDisplay().getRealSize(screenSize);
                sheet = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
                board = new Canvas(sheet);
                paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStrokeWidth(10);
                image.setImageBitmap(sheet);

            }
        });

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initGoogleApiClient();

        // Configure a gesture detector
        mDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public void onLongPress(MotionEvent event) {

                mDismissOverlay.show();
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                    float distanceY) {
                pos.setText("Scroll:\n" +"X: "+ e1.getX()+"\nY: "+e1.getY());
                board.drawLine(e1.getX(),e1.getY(),e2.getX(),e2.getY(),paint);
                image.invalidate();
                sendMessage(WEAR_MESSAGE_PATH,"SCROLL,"+e2.getX()+","+e2.getY()+","+distanceX+","+distanceY);
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent event) {
                pos.setText("Pos:\n" +"X: "+ event.getX()+"\nY: "+event.getY());
                paint.setColor(Color.BLUE);
                board.drawPoint(event.getX(),event.getY(),paint);
                paint.setColor(Color.GREEN);
                image.invalidate();
                sendMessage(WEAR_MESSAGE_PATH,"CLICK");
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                sendMessage(WEAR_MESSAGE_PATH,"DOWN");
                return true;
            }
        }
        );

        //Envoi taille écran"WINDOW,x,y"
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(screenSize);
        sendMessage(WEAR_MESSAGE_PATH,"WINDOW"+","+screenSize.x+","+screenSize.y);
    }


    private void initGoogleApiClient(){
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        if(mApiClient != null && !(mApiClient.isConnected() || mApiClient.isConnecting())){
            mApiClient.connect();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting() ) )
            mApiClient.connect();
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

   /* @Override
    public void onMessageReceived( final MessageEvent messageEvent ) {
        runOnUiThread( new Runnable() {
            @Override
            public void run() {
                if( messageEvent.getPath().equalsIgnoreCase( WEAR_MESSAGE_PATH ) ) {
                    mAdapter.add(new String(messageEvent.getData()));
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }*/

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        sendMessage(START_ACTIVITY, "");
    }

    /*@Override
    protected void onStop() {
        if ( mApiClient != null ) {
            Wearable.MessageApi.removeListener( mApiClient, this );
            if ( mApiClient.isConnected() ) {
                mApiClient.disconnect();
            }
        }
        super.onStop();
    }*/

    @Override
    protected void onDestroy() {
        if( mApiClient != null )
            mApiClient.unregisterConnectionCallbacks( this );
        super.onDestroy();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    // Capture long presses
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mDetector.onTouchEvent(ev) || super.onTouchEvent(ev);
    }

    private GoogleApiClient getGoogleApiClient(Context context){
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    private void sendMessage( final String path, final String text ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();
                }
                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        //mEditText.setText( "" );
                    }
                });
            }
        }).start();
    }
}
