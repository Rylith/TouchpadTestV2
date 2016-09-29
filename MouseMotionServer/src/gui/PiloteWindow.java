package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.Arc2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.swing.JFrame;

import mouse.control.AngleEvent;
import mouse.control.AngleEventListener;

public class PiloteWindow extends JFrame {
	
	private Rectangle bounds;
	private double radius;
	private static boolean polarZone = false;
	private final double rotationAngle = 45; //in degrees, rotation of axis
	private static double[] areaDelimeters = new double[]{0,90,180,270,360};
	private static double extent = -90;//size of each part in degrees
	private static PrintWriter out;
	private static final String PATH = "logs/pilote/";
	
	private static enum Area{NORTH,SOUTH,EAST,WEST,NONE;
		
		private static Random rand = new Random();
		
		public static Area getRandom(){
			
			int pick = rand.nextInt(values().length);
			while(values()[pick] == NONE){
				pick = rand.nextInt(values().length);
			}
			return values()[pick];
			
		}
	};
	private Area clickedArea = Area.NONE;
	private Area choosenArea = Area.NONE;
	private boolean isPolar=false;
	private boolean color;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7302679554266629234L;
	
	private class MyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
            	if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            		out.close();
    				System.exit(0);
    			}
            }
            return false;
        }
    }
	
	@Override
	public void paint(Graphics g) {
		//this.toFront();
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		Arc2D arc;
		g2.setColor(Color.BLACK);
		arc = new Arc2D.Double(0, 0, getWidth(), radius, areaDelimeters[2],360, Arc2D.PIE);
		g2.fill(arc);
		if(color){
			g2.setColor(Color.RED);
		}else{
			g2.setColor(Color.GREEN);
		}
		switch(choosenArea){
			case NORTH:
				arc = new Arc2D.Double(0, 0, getWidth(), radius, areaDelimeters[1],extent, Arc2D.PIE);
				g2.fill(arc);
				break;
			case SOUTH:
				arc = new Arc2D.Double(0, 0, getWidth(), radius, areaDelimeters[3],extent, Arc2D.PIE);
				g2.fill(arc);
				break;
			case EAST:
				arc = new Arc2D.Double(0, 0, getWidth(), radius, areaDelimeters[0],extent, Arc2D.PIE);
				g2.fill(arc);
				break;
			case WEST:
				arc = new Arc2D.Double(0, 0, getWidth(), radius, areaDelimeters[2],extent, Arc2D.PIE);
				g2.fill(arc);
				break;
			default:
		}
	}
	
	public PiloteWindow() {
		//this.setType(JFrame.Type.UTILITY);
		//this.setAlwaysOnTop(true);
		this.setUndecorated(true);
		if(bounds == null){
			bounds = getGraphicsConfiguration().getBounds();
		}
		setBounds(bounds);
		radius = Math.min(getWidth(), getHeight());
		setBackground(new Color(0,true));
		
		new AngleEvent().addAngleEventListener(new AngleEventListener() {
			
			@Override
			public void angleReceived(double theta) {
				polarZone();
				boolean inEastArea;
				if(polarZone){
					inEastArea = theta>=areaDelimeters[0] || theta < areaDelimeters[1];
				}else{
					inEastArea= theta < areaDelimeters[1];
				}
				if(inEastArea){
					clickedArea = Area.EAST;
				}else if(theta>=areaDelimeters[1] && theta < areaDelimeters[2]){
					clickedArea = Area.SOUTH;
				}else if(theta >= areaDelimeters[2] && theta < areaDelimeters[3]){
					clickedArea = Area.WEST;
				}else if(theta >= areaDelimeters[3] && theta < areaDelimeters[4]){
					clickedArea = Area.NORTH;
				}
				validArea();
				chooseArea();
				repaint();
				
			}
		});
		choosenArea = Area.getRandom();
		choosenArea = Area.SOUTH;
		polarZone();
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());
        double surface = (Math.PI*(getWidth()/2.0)*(getHeight()/2.0))/(getWidth()*getHeight())*100;
        System.out.println("% surface atteignable: "+surface);
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        try {
			FileWriter fw = new FileWriter(PATH+dateFormat.format(date)+".txt", true);
			BufferedWriter bw = new BufferedWriter(fw);
			out = new PrintWriter(bw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		setVisible(true);
	}
	
	public static boolean isPolarZone() {
		return polarZone;
	}

	public static void setPolarZone(boolean polarZone) {
		PiloteWindow.polarZone = polarZone;
	}

	private void validArea() {
		if(color){
			color = false;
		}else{
			color = true;
		}
		if(clickedArea == choosenArea){
			writeLog(true);
		}else{
			writeLog(false);
		}
		
	}
	private void writeLog(boolean b) {
		System.out.println("The choosen area was : "+ choosenArea + ", the clicked area was: "+clickedArea + ", result: " + b +", in polar mode: "+polarZone);
		out.println(String.format("%s, %s, %b, %b", choosenArea, clickedArea, b, polarZone));
		out.flush();
	}
	
	private void polarZone(){
		if(polarZone && !isPolar){
			int length = areaDelimeters.length;
			extent*=-1;
			for (int i=0; i<length;i++) {
				areaDelimeters[i]-=rotationAngle;
				if(areaDelimeters[i]<0){
					areaDelimeters[i]+=360;
				}
			}
			isPolar=true;
		}else if(isPolar && !polarZone){
			extent*=-1;
			int length = areaDelimeters.length;
			for (int i=0; i<length;i++) {
				areaDelimeters[i]+=rotationAngle;
				areaDelimeters[0]=0;
			}
			isPolar=false;
		}
	}
	
	private void chooseArea(){
		choosenArea=Area.getRandom();
	};
}
