package rylith.touchpadtestv2.connectionTCP.nio.client.implem;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import rylith.touchpadtestv2.connectionTCP.nio.client.interfaces.Channel;
import rylith.touchpadtestv2.connectionTCP.nio.client.interfaces.DeliverCallback;

public class ChannelTest extends Channel {
	
	private SocketChannel m_ch;
	private ReadAutomata readAutomata;
	private WriteAutomata writeAutomata;
	private DeliverCallback callback;
	
	public WriteAutomata getWriteAutomata() {
		return writeAutomata;
	}

	public ReadAutomata getReadAutomata() {
		return readAutomata;
	}

	public ChannelTest(SocketChannel m_ch) {
		this.m_ch = m_ch;
		readAutomata = new ReadAutomata(m_ch);
		writeAutomata = new WriteAutomata(m_ch);
	}

	@Override
	public void setDeliverCallback(DeliverCallback callback) {
		this.callback=callback;
	}
	
	public DeliverCallback getCallback() {
		return callback;
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		return null;

	}

	@Override
	public void send(byte[] bytes, int offset, int length) {
		writeAutomata.write(bytes,offset,length);
	}

	public void send(ByteBuffer byteBuffer) {
		writeAutomata.write(byteBuffer);
	}

	@Override
	public void close() {
		try {
			m_ch.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
