package test;

import java.io.IOException;

import javax.swing.SwingUtilities;

import gui.Log;
import gui.OptionsInterface;
import gui.PiloteWindow;
import gui.TransparentWindowsOnAllMonitors;
import gui.ApplicationInterface;
import gui.InterfaceGame;
import network.Impl.AcceptCallbackTest;
import network.Impl.OwnEngine;
import network.Interface.Engine;

public class Pong{
	
	private static final String GAME="--game";
	private static final String TEST="--test";
	private static final String OPTIONS="--options";
	private static final String PILOTE = "--pilote";
	
	public static void main(final String[] args) {
		try {
			int portInitial=4444;
			//Il est possible de changer le port de départ
			if(args !=null && args.length != 0){
				for(String arg : args){
					if(isNumeric(arg)){
						portInitial = Integer.parseInt(arg);
					}
				}
			}
			int port = portInitial;
			
			final Engine engine = new OwnEngine();
			int i;
			for(i=0; i< 10 ;i++){
				/*Server contract =*/ engine.listen(port, new AcceptCallbackTest());
				System.out.println("Server is listening on port: "+ port);
				Log.println("Server is listening on port: "+ port);
				port++;
			}
			//pong.startEcho();
			new Thread((OwnEngine)engine).start();
			
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					new TransparentWindowsOnAllMonitors();
					if(args !=null && args.length != 0){
						for(String arg : args){
							switch (arg) {
							case PILOTE:
								new PiloteWindow();
								break;
							case GAME: 
								new InterfaceGame();
								break;
							case TEST:
								ApplicationInterface.createAndShowGUI(args);
								ApplicationInterface.setEngine(engine);
								break;
							case OPTIONS:
								new OptionsInterface(engine,true).createAndShowGUI();
								break;
							default:
								break;
							}
							
						}
					}else{
						//Default when arguments are empty
						new OptionsInterface(engine,true).createAndShowGUI();
					}
				}	
				//GraphicalInterface.showOnScreen(0, graph);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isNumeric(String str)
	{
	  return str.matches("\\d+");  //match an unsigned integer.
	}
}
