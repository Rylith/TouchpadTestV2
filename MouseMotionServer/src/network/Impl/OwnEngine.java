package network.Impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import gui.Log;
import network.Interface.AcceptCallback;
import network.Interface.Channel;
import network.Interface.ConnectCallback;
import network.Interface.Engine;
import network.Interface.Server;


public class OwnEngine extends Engine implements Runnable{
	
	private Selector m_selector;
	private Map<SelectionKey, Channel> listKey= new HashMap<SelectionKey, Channel>();
	private Map<SelectionKey, Server> mapServer = new HashMap<SelectionKey, Server>();
	
	// The message to send to the server
	private byte[] msg="0".getBytes();
	
	private ConnectCallback connectCallback;
	private AcceptCallback acceptCallback;
	//public static double width;
	//public static double height;
	
	public OwnEngine() throws IOException {
		 m_selector = SelectorProvider.provider().openSelector();
		 //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		 //OwnEngine.width = screenSize.getWidth();
		 //OwnEngine.height = screenSize.getHeight();
	}
	//Boucle pour NIO, redirige vers l'action suivant la clé
	public void mainloop() {
		while (true) {
			try {
				m_selector.select();

				Iterator<?> selectedKeys = this.m_selector.selectedKeys().iterator();

				while (selectedKeys.hasNext()) {

					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();

					if (!key.isValid()) {
						continue;
					} else if (key.isAcceptable()) {
						handleAccept(key);

					} else if (key.isReadable()) {
						handleRead(key);

					} else if (key.isWritable()) {
						handleWrite(key);

					} else if (key.isConnectable()) {
						handleConnect(key);
					} else 
						System.out.println("  ---> unknown key=");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	//Utilisé quand la connexion est acceptée
	private void handleConnect(SelectionKey key) {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		try {
			socketChannel.finishConnect();
		} catch (IOException e) {
			e.printStackTrace();
			key.cancel();
			listKey.remove(key);
			return;
		}
		//Callback avertissant de la connection
		Channel channel = listKey.get(key);
		connectCallback.connected(channel);
		//channel.send(msg, 0, msg.length);
		key.interestOps(SelectionKey.OP_READ /*| SelectionKey.OP_WRITE*/);
	}

	private void handleWrite(SelectionKey key) {
		boolean finish = true;
		//System.out.println("call of handleWrite");
		Channel channel = listKey.get(key);
		try {
			finish=((ChannelTest) channel).getWriteAutomata().handleWrite();
			if(finish && ((ChannelTest) channel).getWriteAutomata().isEmpty()){
				key.interestOps(SelectionKey.OP_READ);
			}else{
				key.interestOps(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleRead(SelectionKey key) {
		SocketChannel socketChannel = (SocketChannel) key.channel();
	    Channel channel = listKey.get(key);
	    try {
	    	msg=((ChannelTest) channel).getReadAutomata().handleRead();
	    } catch (IOException e) {
	      // the connection as been closed unexpectedly, cancel the selection and close the channel
	      key.cancel();
	      try {
			socketChannel.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	      return;
	    }
	    if (((ChannelTest) channel).getReadAutomata().isConnectionClosed()) {
	      // the socket has been shutdown remotely cleanly"
	      try {
			key.channel().close();
			listKey.remove(key);
			channel.close();
			System.out.println("Connection closed by client : "+channel);
			Log.println("Connection closed by client : "+channel);
		} catch (IOException e) {
			e.printStackTrace();
		}
	      key.cancel();
	      return;
	    }
	    //Si le message est non null c'est qu'il a été lu entièrement
	    if(msg != null){
	    	((ChannelTest) channel).getCallback().deliver(channel, msg);
	    	if(!((ChannelTest) channel).getWriteAutomata().isEmpty()){
	    		key.interestOps(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
	    	}
	    	//msg = String.valueOf(Integer.valueOf(new String(msg))+1).getBytes();
			//channel.send(msg, 0, msg.length);
	    }
		
	}

	private void handleAccept(SelectionKey key) {
		SocketChannel socketChannel = null;
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		try {
			socketChannel = serverSocketChannel.accept();
			socketChannel.configureBlocking(false);
		} catch (IOException e) {
			// as if there was no accept done
			return;
		}
		// be notified when there is incoming data 
		try {
			SelectionKey m_key = socketChannel.register(this.m_selector, SelectionKey.OP_READ /*| SelectionKey.OP_WRITE*/);
			//Création du channel
			Channel channel = new ChannelTest(socketChannel);
			channel.setDeliverCallback(new DeliverCallbackTest(m_key,channel));
			//Callback d'acceptation
			Server server = mapServer.get(key);
			acceptCallback.accepted(server, channel);
			listKey.put(m_key, channel);
		} catch (ClosedChannelException e) {
			handleClose(socketChannel);
		}
		
		
	}

	private void handleClose(SocketChannel socketChannel) {
		socketChannel.keyFor(m_selector).cancel();
		try{
			socketChannel.close();
		} catch (IOException e) {
			//nothing to do, the channel is already closed
		}
		
	}

	public Server listen(int port, AcceptCallback callback) throws IOException {
		//Création de la socketserveur dans le constructeur
		ServerTest server = new ServerTest(port);
		SelectionKey key = server.getSocket().register(m_selector, SelectionKey.OP_ACCEPT);
		acceptCallback=callback;
		//Pour retrouver le serveur lors de l'appel du callback d'acceptation
		mapServer.put(key,server);
		return server;
	}

	public void connect(InetAddress hostAddress, int port,
		ConnectCallback callback) throws UnknownHostException,
		SecurityException, IOException {
		SocketChannel m_ch;
		//Création de la socket de connexion
		m_ch = SocketChannel.open();
	    m_ch.configureBlocking(false);
	    m_ch.socket().setTcpNoDelay(true);
	    
	    SelectionKey m_key;
	    // be notified when the connection to the server will be accepted
	    m_key = m_ch.register(m_selector, SelectionKey.OP_CONNECT);
	    
	    // request to connect to the server
	    m_ch.connect(new InetSocketAddress(hostAddress, port));
	    Channel channel = new ChannelTest(m_ch);
	    channel.setDeliverCallback(new DeliverCallbackTest(m_key,channel));
	    listKey.put(m_key, channel);
	    connectCallback=callback;
	}

	public void run() {
		mainloop();
	}
	
	//Use to send to every member of group
	public void send(String msg, int offset, int length) {
		for(Entry<SelectionKey, Channel> mapEntry : listKey.entrySet()){
			SelectionKey key = mapEntry.getKey();		
			Channel channel = mapEntry.getValue();
			channel.send(msg.getBytes(), offset, length);
			key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			m_selector.wakeup();
		}
	}
	
	public Set<Entry<SelectionKey, Channel>> getDelivers(){
		return listKey.entrySet();
	}

}
