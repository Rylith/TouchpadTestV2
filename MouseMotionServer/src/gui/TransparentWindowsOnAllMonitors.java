package gui;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

public class TransparentWindowsOnAllMonitors {
	
	public TransparentWindowsOnAllMonitors(){
		Rectangle virtualBounds = new Rectangle();
		GraphicsEnvironment ge = GraphicsEnvironment
		        .getLocalGraphicsEnvironment();
		    GraphicsDevice[] gsd = ge.getScreenDevices();
		  for(GraphicsDevice gd : gsd){
			 GraphicsConfiguration[] gcs = gd.getConfigurations();
			 for(GraphicsConfiguration gc :gcs){
				 
				 virtualBounds = virtualBounds.union(gc.getBounds());
			 }
			 
		  }
		  
		  new TransparentWindow(virtualBounds);
	}

}
