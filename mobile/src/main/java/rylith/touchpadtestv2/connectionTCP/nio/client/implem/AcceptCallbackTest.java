package rylith.touchpadtestv2.connectionTCP.nio.client.implem;

import rylith.touchpadtestv2.connectionTCP.nio.client.interfaces.AcceptCallback;
import rylith.touchpadtestv2.connectionTCP.nio.client.interfaces.Channel;
import rylith.touchpadtestv2.connectionTCP.nio.client.interfaces.Server;

public class AcceptCallbackTest implements AcceptCallback {

	public void accepted(Server server, Channel channel) {
		System.out.println("Acceptation connexion sur le port : " +server.getPort()+ " pour le channel "+channel.toString());

	}

	public void closed(Channel channel) {
		channel.close();
	}

}
