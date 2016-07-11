package rylith.touchpadtestv2;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.net.InetAddress;

import rylith.touchpadtestv2.connectionTCP.nio.client.implem.TCPClient;

public class MainActivity extends Activity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks {

    private static final String START_ACTIVITY ="/start_activity";
    private static final String WEAR_MESSAGE_PATH = "/message";

    private GoogleApiClient mApiClient;
    public static final String PREFS_SERV = "MyPrefsServ";

    private TCPClient mTcpClient;
    public static String SERVERIP = "192.168.43.43"; //your computer IP address
    public static int SERVERPORT = 4446;
    private Activity activity;
    private TextView response;
    private EditText editTextAddress;
    private EditText editTextPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity=this;
        init();
        initGoogleApiClient();
    }

    private void initGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();
        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting() ) )
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
        if( mApiClient != null ){
            Wearable.MessageApi.removeListener( mApiClient, this );
            if ( mApiClient.isConnected() ) {
                mApiClient.disconnect();
            }

            mApiClient.unregisterConnectionCallbacks( this );
        }
        if(mTcpClient !=null){
            mTcpClient.closeConnection();
            mTcpClient.stopClient();
        }
        super.onDestroy();
    }

    private void init() {
        /*ListView mListView = (ListView) findViewById(R.id.list_view);
        mEditText = (EditText) findViewById( R.id.input );

        mAdapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1 );
        mListView.setAdapter( mAdapter );*/

        editTextAddress = (EditText) findViewById(R.id.addressEditText);
        editTextPort = (EditText) findViewById(R.id.portEditText);
        Button buttonConnect = (Button) findViewById(R.id.connectButton);
        Button buttonClear = (Button) findViewById(R.id.clearButton);
        response = (TextView) findViewById(R.id.responseTextView);

        buttonConnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                new connectTask().execute(editTextAddress.getText()
                        .toString(),editTextPort
                        .getText().toString());
            }
        });
        buttonClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editTextAddress.setText("");
                editTextPort.setText("");
            }
        });
        SharedPreferences settings = getSharedPreferences(PREFS_SERV, 0);
        SERVERIP=settings.getString("SERVERIP","192.168.43.43");
        SERVERPORT = settings.getInt("serverPort", 4446);
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
    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener( mApiClient, this );
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
    public void onMessageReceived(final MessageEvent messageEvent) {
        if(messageEvent.getPath().equalsIgnoreCase(WEAR_MESSAGE_PATH)){
            if (mTcpClient != null) {
                //Log.v("Coordinates",message);
                //new sendTask().execute(message);
                new Thread(){public void run() {mTcpClient.sendMessage(messageEvent.getData(),0, messageEvent.getData().length);}}.start();
            }
        }
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
}
