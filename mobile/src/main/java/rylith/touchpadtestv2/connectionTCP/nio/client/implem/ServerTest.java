package rylith.touchpadtestv2.connectionTCP.nio.client.implem;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

import rylith.touchpadtestv2.connectionTCP.nio.client.interfaces.Server;

public class ServerTest extends Server {
	private int port;
	private ServerSocketChannel m_sch;
	private String m_localhost = "localhost";
	
	public ServerTest(int port) throws IOException {
		this.port=port;
		m_sch= ServerSocketChannel.open();
		m_sch.configureBlocking(false);
		m_sch.socket().bind(new InetSocketAddress(m_localhost, port));
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public void close() {
		try {
			m_sch.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ServerSocketChannel getSocket() {
		return m_sch;
	}
	
	

}
