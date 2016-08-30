package rylith.touchpadtestv2;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
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
    private GestureDetectorCompat mDetector;
    private Vibrator vibrator;

    public static TextView pos;
    private NodeApi.GetConnectedNodesResult nodes;

    public static Canvas board;
    public static Bitmap sheet;
    public static Paint paint;
    public static ImageView image;
    public static float brightness=-1f;
    //private float downx = 0, downy = 0, upx = 0, upy = 0;

    //private static final String START_ACTIVITY ="/start_activity";
    public static final String WEAR_DATA_PATH = "/message";
    public static final String MOBILE_DATA_PATH = "/messageMobile";

    private GoogleApiClient mApiClient;
    private ArrayAdapter<String> mAdapter;
    private int i=0;
    private Point screenSize;
    private PutDataRequest request;
    private MySimpleGestureDetector listener;
    private float[] origin=new float[2],current = new float[2];
    private boolean PositionMode = true;//To decide if it needs to ask the user position
    private boolean enableEvent = false;
    private Rect rectN, rectS,rectE,rectO;
    private boolean InversionAxe = false;//To decide if it needs to switch x & y depending on user position.
    private boolean InversionX=false,InversionY = false;

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


                sheet = Bitmap.createBitmap(screenSize.x, screenSize.y, Bitmap.Config.ARGB_8888);
                board = new Canvas(sheet);
                //paint = new Paint();
                //paint.setColor(Color.RED);
                //paint.setStrokeWidth(10);
                image.setImageBitmap(sheet);
                initZone();

            }
        });

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initGoogleApiClient();

        // Configure a gesture detector
        mDetector = new GestureDetectorCompat(MainActivity.this, listener=new MySimpleGestureDetector(this));

        //Envoi taille écran"WINDOW,x,y"
        //send = (sendTask) new sendTask().execute("");
        screenSize = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(screenSize);
        boolean prevEnableEvent = enableEvent;
        enableEvent = true;
        sendMessage(WEAR_DATA_PATH,"WINDOW,"+screenSize.x+","+screenSize.y);
        enableEvent = prevEnableEvent;
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

        boolean prevEnableEvent = enableEvent;
        enableEvent = true;
        sendMessage(WEAR_DATA_PATH,"WINDOW,"+screenSize.x+","+screenSize.y);
        enableEvent = prevEnableEvent;
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
    public void onConnected(Bundle bundle) {
        //sendMessage(START_ACTIVITY, "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                nodes=Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
            }
        });
        Wearable.DataApi.addListener(mApiClient,this);
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
        PositionMode = true;
        enableEvent=false;
        super.onDestroy();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    // Capture long presses
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if(!PositionMode){

            setCoord(ev);
            //current[0] = ev.getX();
            //current[1] = ev.getY();
            switch (MotionEventCompat.getActionMasked(ev)) {
                case (MotionEvent.ACTION_DOWN):
                    if(brightness != 0.0f){
                        WindowManager.LayoutParams lp = getWindow().getAttributes();
                        brightness = 0.0f;
                        lp.screenBrightness = brightness;
                        getWindow().setAttributes(lp);
                    }
                    enableEvent=true;
                    //origin[0] = ev.getX();
                    //origin[1] = ev.getY();
                    origin[0] = current[0];
                    origin[1] = current[1];
                    sendMessage(MainActivity.WEAR_DATA_PATH, "DOWN," + current[0] + "," + current[1]);
                    break;
                case (MotionEvent.ACTION_MOVE):
                    float distX = -current[0] + origin[0];
                    float distY = -current[1] + origin[1];
                    sendMessage(MainActivity.WEAR_DATA_PATH, "SCROLL," + current[0] + "," + current[1] + "," + distX + "," + distY);
                    //Log.v("GESTURE","SCROLL,"+current[0]+","+current[1]+","+distX+","+distY);
                    origin[0] = current[0];
                    origin[1] = current[1];
                    break;
                case (MotionEvent.ACTION_UP):
                    sendMessage(MainActivity.WEAR_DATA_PATH, "RELEASE");
                    //isUp = true;
                    /*if (vibrator != null) {
                        vibrator.cancel();
                    }*/
                    break;
                default:

            }

        }
        else {
            int evX = Math.round(ev.getX());
            int evY = Math.round(ev.getY());

            if(rectN.contains(evX,evY)){
                pos.setText("Position Nord selectionnée");
                //Inverser x et y
                InversionX = true;
                InversionY = true;
                PositionMode=false;
            } else if(rectS.contains(evX,evY)){
                pos.setText("Position Sud selectionnée");
                //Default position, no need for change
                PositionMode=false;
            }
            else if(rectO.contains(evX,evY)){
                pos.setText("Position Ouest selectionnée");
                //Inverser y + switch
                //Log.v("Inverse","Position Ouest selectionnée");
                InversionY = true;
                InversionAxe = true;
                PositionMode=false;
            }
            else if(rectE.contains(evX,evY)){
                pos.setText("Position Est selectionnée");
                //Log.v("Inverse","Position Est selectionnée");
                //Inverser x + switch
                InversionX=true;
                InversionAxe = true;
                PositionMode=false;
            }

            if(!PositionMode){
                board.drawColor(0, PorterDuff.Mode.CLEAR);
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                brightness = 0.0f;
                lp.screenBrightness = brightness;
                getWindow().setAttributes(lp);
            }

        }

        return mDetector.onTouchEvent(ev) || super.onTouchEvent(ev);
    }

    public void sendMessage(final String path, final String text) {
        //Log.v("BLUETOOTH","Inside sendMessage NOT THREAD");
        if(enableEvent){
            if(request == null){
                request = PutDataRequest.create(path);
            }
            request.setData(text.getBytes()).setUrgent();
            Wearable.DataApi.putDataItem(mApiClient,request);
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for(DataEvent event : dataEventBuffer){
            if(event.getType() == DataEvent.TYPE_CHANGED){
                String path = event.getDataItem().getUri().getPath();
                if(path.equals(MOBILE_DATA_PATH)){
                    String msg = new String (event.getDataItem().getData());
                    //Log.v("CALLBACK",msg);
                    String[] m = msg.split(",");

                    if(vibrator == null){
                        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    }
                    float intensity = Float.parseFloat(m[1]);
                    //if(!isUp){
                        if(intensity <= 0.25){
                            long[] pattern = genVibratorPattern(Float.parseFloat(m[1]),20);
                            vibrator.vibrate(pattern,-1);
                        }else{
                            long[] pattern = genVibratorPattern(Float.parseFloat(m[1]),60);
                            vibrator.vibrate(pattern,-1);
                        }
                    //}
                }
            }
        }
    }

    public long[] genVibratorPattern( float intensity, long duration )
    {
        float dutyCycle = Math.abs( ( intensity * 2.0f ) - 1.0f );
        long lWidth = dutyCycle == 1.0f ? 0 : 1;
        long hWidth = (long) ( dutyCycle * ( duration - 1 ) ) + 1;

        //Log.v("PATTERN","hWidth: "+hWidth+", lWidth: "+lWidth);
        int pulseCount = (int) ( 2.0f * ( (float) duration / (float) ( hWidth + lWidth ) ) );
        long[] pattern = new long[ pulseCount ];

        //Log.v("PATTERN","======Begin table======");
        for( int i = 0; i < pulseCount; i++ )
        {
            pattern[i] = intensity < 0.5f ? ( i % 2 == 0 ? hWidth : lWidth ) : ( i % 2 == 0 ? lWidth : hWidth );
            //Log.v("PATTERN",Long.toString(pattern[i]));
        }

        return pattern;
    }


    private void initZone(){

        int ecartX = (int)(0.25*screenSize.x);
        int ecartY = (int)(0.30*screenSize.y);

        Paint rectPaint = new Paint();
        Bitmap bitmap = decodeSampledBitmapFromResource(getResources(),R.drawable.maxresdefault,(screenSize.x-2*ecartX),(screenSize.y-2*ecartY));
        Rect frameToDraw = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF whereToDraw = new RectF(ecartX, ecartY, (screenSize.x-ecartX), (screenSize.y-ecartY));
        board.drawBitmap(bitmap,frameToDraw,whereToDraw,rectPaint);

        rectPaint.setColor(Color.GREEN);
        rectN = new Rect(ecartX,0,screenSize.x-ecartX,ecartY);
        board.drawRect(rectN,rectPaint);

        rectPaint.setColor(Color.RED);
        rectS = new Rect(ecartX,screenSize.y-ecartY,screenSize.x-ecartX,screenSize.x);
        board.drawRect(rectS,rectPaint);

        rectPaint.setColor(Color.BLUE);
        rectO = new Rect(0,ecartY,ecartX,screenSize.y-ecartY);
        board.drawRect(rectO,rectPaint);

        rectPaint.setColor(Color.YELLOW);
        rectE = new Rect(screenSize.x-ecartX,ecartY,screenSize.x,screenSize.y-ecartY);
        board.drawRect(rectE,rectPaint);
    }

    private void setCoord(MotionEvent ev){
        if(InversionAxe){
            if(InversionX){
                current[1] = screenSize.x-ev.getX();
            }
            else {
                current[1] = ev.getX();
            }
            if(InversionY){
                current[0] = screenSize.y-ev.getY();
            }else {
                current[0] = ev.getY();
            }
        }else{
            if(InversionX){
                current[0] = screenSize.x-ev.getX();
            }
            else {
                current[0] = ev.getX();
            }
            if(InversionY){
                current[1] = screenSize.y-ev.getY();
            }else {
                current[1] = ev.getY();
            }
        }

    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((height / inSampleSize) >= reqHeight
                    && (width / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        //options.outHeight=reqHeight;
        //options.outWidth=reqWidth;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

}


