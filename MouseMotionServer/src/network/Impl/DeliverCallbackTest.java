package network.Impl;

import java.awt.MouseInfo;
import java.nio.channels.SelectionKey;

import gui.Log;
import mouse.control.IMouseListener;
import network.Interface.Channel;
import network.Interface.DeliverCallback;


public class DeliverCallbackTest implements DeliverCallback {
	private static boolean DEBUG = false;
	private IMouseListener listener;
	public static String defaultListener="mouse.control.MouseListenerV1";
	private SelectionKey key;
	private Channel channel;
	private int xC=0;
	private int yC=0;
	
	public DeliverCallbackTest(){
		try {
			Class<?> cl = Class.forName(defaultListener);
			listener = (IMouseListener) cl.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public DeliverCallbackTest(SelectionKey key, Channel channel){
		this();
		this.key=key;
		this.channel=channel;
		listener.setKey(key);
		listener.setChannel(channel);
	}
	
	public void deliver(Channel channel, byte[] bytes) {
		String msg=new String(bytes);
		String[] x_y = msg.split(",");
		//System.out.println(msg);
		switch (x_y[0]) {
			case "SCROLL":
				//Coordinates of current point
				float x = Float.parseFloat(x_y[1]);
				float y = Float.parseFloat(x_y[2]);
				//Pre-calculated "distance" in x (could be negative to inform direction)
				float distanceX = Float.parseFloat(x_y[3]);
				if(DEBUG)
					System.out.println("receive this quantity in x : " + x);
				//Pre-calculated distance in y
				float distanceY = Float.parseFloat(x_y[4]);
				if(DEBUG)
					System.out.println("receive this quantity in y : " + y);
				//Move the cursor with the distance
				if(DEBUG)
					System.out.println("Before motion : " + MouseInfo.getPointerInfo().getLocation());
				
				listener.onScroll(x, y, distanceX, distanceY);
				break;
			case "PRESS":
				listener.press();
				break;
			case "RELEASE":
				listener.release();
				break;
			case "CLICK":
				listener.click();
				break;
			case "WINDOW":
				System.out.println(msg);
				Log.println(msg);
				xC = Integer.parseInt(x_y[1]);
				yC = Integer.parseInt(x_y[2]);
				listener.setCenter(xC,yC);
				break;
			case "DOWN":
				//Set the first previous point
				float xD = Float.parseFloat(x_y[1]);
				float yD = Float.parseFloat(x_y[2]);
				listener.resetBuffers(xD, yD);
				break;
			case "DOUBLECLICK":
				listener.doubleClick();
				break;
			default:
				break;
		}
		if(DEBUG){
			System.out.println("After motion : " + MouseInfo.getPointerInfo().getLocation());
			System.out.println("Message : "+msg +" on channel " + channel.toString());
		}
	}
	
	public void setListener(String className){
		try {
			Class<?> cl = Class.forName(className);
			listener = (IMouseListener) cl.newInstance();
			listener.setKey(key);
			listener.setChannel(channel);
			listener.setCenter(xC, yC);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void resetMarge(){
		listener.setCenter(xC, yC);
	}

}
