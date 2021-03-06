package rylith.touchpadtestv2.connectionTCP.nio.client.implem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class WriteAutomata {

	SocketChannel sock;
	List<ByteBuffer> messages = new ArrayList<ByteBuffer>(); // messages to
																	// send"
	ByteBuffer lenBuf = ByteBuffer.allocate(4); // for writing the length of a
												// message"
	ByteBuffer msgBuf = null; // for writing a message"
	static final int WRITING_LENGTH = 1;
	static final int WRITING_MSG = 2;
	int currentState = WRITING_LENGTH; // initial state "

	public WriteAutomata(SocketChannel socketChannel) {
		this.sock = socketChannel;
	}

	public SocketChannel getSock() {
		return sock;
	}

	public void write(byte[] data, int offset, int length) {
		messages.add(ByteBuffer.wrap(data, offset, length));
	}

	public void write(ByteBuffer byteBuffer){
		messages.add(byteBuffer);
	}

	public boolean handleWrite() throws IOException {
		boolean finish = false;
		if(!messages.isEmpty()){
			if (currentState == WRITING_LENGTH) {
				msgBuf = messages.get(0);
				lenBuf.position(0);
				if(msgBuf!=null){
					lenBuf=lenBuf.putInt(0,msgBuf.remaining());
					sock.write(lenBuf);
				}
				if (lenBuf.remaining() == 0) {
					currentState = WRITING_MSG;
				}
			}
			if (currentState == WRITING_MSG) {
				if (msgBuf.remaining() > 0) {
					sock.write(msgBuf);
				}
				if (msgBuf.remaining() == 0) { // the message has been fully sent"
					msgBuf = messages.remove(0);
					currentState = WRITING_LENGTH;
					finish = true;
				}
			}
		}
		return finish;
	}

	public boolean isEmpty() {
		return messages.isEmpty();
	}
}
