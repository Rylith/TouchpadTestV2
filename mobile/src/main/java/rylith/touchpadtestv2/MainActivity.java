package rylith.touchpadtestv2;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.ChannelApi;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import rylith.touchpadtestv2.connectionTCP.nio.client.implem.TCPClient;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ChannelApi.ChannelListener {

    //private static final String START_ACTIVITY ="/start_activity";
    public static final String WEAR_DATA_PATH = "/message";
    public static final String MOBILE_DATA_PATH = "/messageMobile";

    public static GoogleApiClient mApiClient;
    private static final String PREFS_SERV = "MyPrefsServ";

    private TCPClient mTcpClient;
    private NodeApi.GetConnectedNodesResult nodes;

    public static String SERVERIP = "192.168.43.43"; //your computer IP address
    public static int SERVERPORT = 4446;
    private Activity activity;
    private TextView response;
    private EditText editTextAddress;
    private EditText editTextPort;
    private Channel channel;

    private Canvas board;
    private Bitmap sheet;
    private Paint paint;
    private ImageView image;
    private TextView pos;
    private InputStream in;

    private ExecutorService task = Executors.newSingleThreadExecutor();
    private Runnable listenOnChannel = new Runnable() {
        @Override
        public void run() {
            Log.v("CHANNEL API MOBILE","Launch thread");
            byte[] byteBuffer = new byte[10];
            byte[] lengthBuf = new byte[4];
            boolean readLength = true;
            int length=0;
            int read=0;
            try {
                while(mApiClient.isConnected() && in != null){

                    if(readLength){
                        //Log.v("CHANNEL API MOBILE","beginning of reading length");
                        while(read >= 0 && read < 4){
                            read+=in.read(lengthBuf,read,lengthBuf.length-read);
                        }
                        if(read < 0){
                            return;
                        }
                        //Log.v("CHANNEL API MOBILE","end of reading length");
                        read=0;
                        length=readInt32(lengthBuf,0);
                        //Log.v("CHANNEL API MOBILE","length: "+length);
                        readLength=false;
                    }else{
                        if(length>byteBuffer.length){
                            byteBuffer=new byte[length];
                        }
                        //Log.v("CHANNEL API MOBILE","beginning of reading message");
                        while(read<length){
                            read+=in.read(byteBuffer,read,length-read);
                        }
                        //Log.v("CHANNEL API MOBILE","end of reading message");
                        read=0;
                        //Log.v("CHANNEL API MOBILE",new String(byteBuffer));
                        if(mTcpClient != null){
                            //Log.v("GESTURE",new String(data));
                            mTcpClient.sendMessage(ByteBuffer.wrap(byteBuffer,0,length));
                        }
                        /*String tra = new String(byteBuffer);
                        String[] mess = tra.split(",");
                        pos.setText(mess[0]);
                        if (mess.length>2){
                            board.drawPoint(Float.parseFloat(mess[1]),Float.parseFloat(mess[2]),paint);
                            image.invalidate();
                        }*/
                        readLength=true;
                    }
                }
            } catch (IOException e) {
                //e.printStackTrace();
            }
            Log.v("CHANNEL API MOBILE","End thread");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity=this;

        image = (ImageView) findViewById(R.id.image);
        pos = (TextView) findViewById(R.id.pos);
        sheet = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        board = new Canvas(sheet);
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(1);
        image.setImageBitmap(sheet);

        init();
        initGoogleApiClient();
    }

    private void initGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if( !( mApiClient.isConnected() || mApiClient.isConnecting() ) )
            mApiClient.connect();
    }

    @Override
    protected void onResume() {
        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting() ) ){
            mApiClient.connect();
        }
        editTextAddress.setText(SERVERIP);
        editTextPort.setText(Integer.toString(SERVERPORT));
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        if(channel != null){
            channel.close(mApiClient);
        }

        if( mApiClient != null ){
            Wearable.ChannelApi.removeListener(mApiClient,this);
            mApiClient.unregisterConnectionCallbacks(this);
            mApiClient.unregisterConnectionFailedListener(this);
            if ( mApiClient.isConnected() ) {
                mApiClient.disconnect();
            }
        }

        if(mTcpClient !=null){
            mTcpClient.closeConnection();
            mTcpClient.stopClient();
        }
        super.onDestroy();
    }

    private void init() {

        editTextAddress = (EditText) findViewById(R.id.addressEditText);
        editTextPort = (EditText) findViewById(R.id.portEditText);
        Button buttonConnect = (Button) findViewById(R.id.connectButton);
        Button buttonClear = (Button) findViewById(R.id.clearButton);
        //Button buttonDisconnect = (Button) findViewById(R.id.disconnectButton);
        response = (TextView) findViewById(R.id.responseTextView);

        buttonConnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                new connectTask().execute(editTextAddress.getText()
                        .toString(),editTextPort
                        .getText().toString());
            }
        });

        /*buttonDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTcpClient !=null){
                    mTcpClient.closeConnection();
                    mTcpClient.stopClient();
                }
            }
        });*/

        buttonClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editTextAddress.setText("");
                editTextPort.setText("");
                image.setImageBitmap(sheet);
            }
        });
        SharedPreferences settings = getSharedPreferences(PREFS_SERV, 0);
        SERVERIP=settings.getString("SERVERIP","192.168.43.43");
        SERVERPORT = settings.getInt("serverPort", 4446);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v("BLUETOOTH","Call of onConnected");
        Wearable.ChannelApi.addListener(mApiClient,this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Log.v("CHANNEL API MOBILE","BEGIN create channel");
                nodes=Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()){
                    ChannelApi.OpenChannelResult res = Wearable.ChannelApi.openChannel(mApiClient,node.getId(),WEAR_DATA_PATH).await();

                    channel = res.getChannel();

                    PendingResult<Channel.GetInputStreamResult> resu = channel.getInputStream(mApiClient);
                    resu.setResultCallback(new ResultCallback<Channel.GetInputStreamResult>() {
                        @Override
                        public void onResult(@NonNull Channel.GetInputStreamResult getInputStreamResult) {
                            in = getInputStreamResult.getInputStream();
                            task.execute(listenOnChannel);
                        }
                    });
                }
                //Log.v("CHANNEL API MOBILE","Launch of task listen");


            }
        }).start();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onStop() {
        SharedPreferences settings = getSharedPreferences(PREFS_SERV, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("SERVERIP", SERVERIP);
        editor.putInt("serverPort",SERVERPORT);
        // Commit the edits!
        editor.apply();

        super.onStop();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("ERROR","result: "+connectionResult.getErrorMessage());
        if(connectionResult.getErrorCode() == ConnectionResult.API_UNAVAILABLE){
            Log.e("ERROR","Wearable API is unavailable");
        }
    }


    @Override
    public void onChannelOpened(Channel channel) {
        Log.v("CHANNEL API","CHANNEL OPEN");
        if(this.channel != null ){
            this.channel.close(mApiClient);
        }
        this.channel = channel;
        PendingResult<Channel.GetInputStreamResult> res = channel.getInputStream(mApiClient);
        res.setResultCallback(new ResultCallback<Channel.GetInputStreamResult>() {
            @Override
            public void onResult(@NonNull Channel.GetInputStreamResult getInputStreamResult) {
                in = getInputStreamResult.getInputStream();
                task.execute(listenOnChannel);
            }
        });

    }

    @Override
    public void onChannelClosed(Channel channel, int i, int i1) {
        Log.v("CHANNEL API","CHANNEL CLOSE");
        /*try {
            if(in != null){
                in.close();
                in = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onInputClosed(Channel channel, int i, int i1) {
        Log.v("CHANNEL API","CHANNEL INPUT CLOSE");
        /*try {
            if(in != null){
                in.close();
                in = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onOutputClosed(Channel channel, int i, int i1) {
        Log.v("CHANNEL API","CHANNEL OUTPUT CLOSE");
    }

    public class connectTask extends AsyncTask<String,String,TCPClient> {

        @Override
        protected TCPClient doInBackground(String... message) {

            //we create a TCPClient object and
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    //publishProgress(message);
                }
            },response,activity);
            try{
                if(!message[0].equals("") ){
                    SERVERIP = message[0];
                }
                if(!message[1].equals("")){
                    SERVERPORT = Integer.parseInt(message[1]);
                }
                InetAddress address = InetAddress.getByName(SERVERIP);
                Log.v("Address", address.toString());
                Log.v("Port",Integer.toString(SERVERPORT));
                mTcpClient.connect(address,SERVERPORT);
                mTcpClient.run();
            }
            catch (java.io.IOException e) {
                e.printStackTrace();
            }
            //Log.v("NETWORK","DoInBackground finished");

            return null;
        }
    }

    /** Read a signed 32bit value */
    static public int readInt32(byte bytes[], int offset) {
        int val;
        val = ((bytes[offset] & 0xFF) << 24);
        val |= ((bytes[offset+1] & 0xFF) << 16);
        val |= ((bytes[offset+2] & 0xFF) << 8);
        val |= (bytes[offset+3] & 0xFF);
        return val;
    }
}
