package rylith.touchpadtestv2.connectionTCP.nio.client.implem;

import rylith.touchpadtestv2.connectionTCP.nio.client.interfaces.Channel;
import rylith.touchpadtestv2.connectionTCP.nio.client.interfaces.ConnectCallback;

public class ConnectCallbackTest implements ConnectCallback {

	public void closed(Channel channel) {
		channel.close();
		
	}

	public void connected(Channel channel) {
		System.out.println("Connexion sur le channel : "+ channel.toString());
	}

}
