package rylith.touchpadtestv2;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener{

    public static DismissOverlayView mDismissOverlay;
    private GestureDetector mDetector;
    public static TextView pos;
    private NodeApi.GetConnectedNodesResult nodes;

    public static Canvas board;
    public static Bitmap sheet;
    public static Paint paint;
    public static ImageView image;
    //private float downx = 0, downy = 0, upx = 0, upy = 0;

    //private static final String START_ACTIVITY ="/start_activity";
    public static final String WEAR_DATA_PATH = "/message";

    private GoogleApiClient mApiClient;
    private ArrayAdapter<String> mAdapter;
    private int i=0;
    private Point screenSize;
    private PutDataRequest request;
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
        mDetector = new GestureDetector(MainActivity.this, new MySimpleGestureDetector(this));

        //Envoi taille écran"WINDOW,x,y"
        //send = (sendTask) new sendTask().execute("");
        screenSize = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(screenSize);
        sendMessage(WEAR_DATA_PATH,"WINDOW,"+screenSize.x+","+screenSize.y);
    }



    private void initGoogleApiClient(){
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if(mApiClient != null && !(mApiClient.isConnected() || mApiClient.isConnecting())){
            mApiClient.connect();
            //Log.v("API GOOGLE", "Try to connect");
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting() ) )
            mApiClient.connect();

        sendMessage(WEAR_DATA_PATH,"WINDOW,"+screenSize.x+","+screenSize.y);
        //Log.v("API GOOGLE", "Try to send: "+"WINDOW,"+screenSize.x+","+screenSize.y );
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
        //sendMessage(START_ACTIVITY, "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                nodes=Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
            }
        });
        Log.v("BLUETOOTH","call to OnConnected");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result){
        Log.e("ERROR","result: "+result.getErrorMessage());
        if(result.getErrorCode() == ConnectionResult.API_UNAVAILABLE){
            Log.e("ERROR","Wearable API is unavailable");
        }
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
        if( mApiClient != null ) {
            mApiClient.unregisterConnectionCallbacks(this);
            mApiClient.unregisterConnectionFailedListener(this);
            mApiClient.disconnect();
        }
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

    public void sendMessage(final String path, final String text) {
        //Log.v("BLUETOOTH","Inside sendMessage NOT THREAD");
        if(request == null){
            request = PutDataRequest.create(path);
        }
        request.setData(text.getBytes()).setUrgent();
        Wearable.DataApi.putDataItem(mApiClient,request);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for(DataEvent event : dataEventBuffer){

            if(event.getType() == DataEvent.TYPE_CHANGED){

                String path = event.getDataItem().getUri().getPath();
                if(path.equals(WEAR_DATA_PATH)){

                    event.getDataItem().getData();
                }
            }
        }
    }
}


