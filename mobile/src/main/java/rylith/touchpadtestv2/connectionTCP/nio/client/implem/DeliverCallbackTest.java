package rylith.touchpadtestv2.connectionTCP.nio.client.implem;

import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import rylith.touchpadtestv2.MainActivity;
import rylith.touchpadtestv2.connectionTCP.nio.client.interfaces.Channel;
import rylith.touchpadtestv2.connectionTCP.nio.client.interfaces.DeliverCallback;

public class DeliverCallbackTest implements DeliverCallback {

    private PutDataRequest request;

	public void deliver(Channel channel, byte[] bytes) {

        if(request == null){
            request = PutDataRequest.create(MainActivity.MOBILE_DATA_PATH);
        }
        request.setData(bytes).setUrgent();
        if(MainActivity.mApiClient != null){
            Wearable.DataApi.putDataItem(MainActivity.mApiClient,request);
        }
	}

}
