package mouse.control;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.time.Instant;

import javax.swing.JFrame;

import javafx.scene.Scene;

public class Cursor{

	private Point point;
	private JFrame originComponent;
	//private Scene scene;
	private Image image;
	private int ID;
	public static enum State{IDLE,PRESS};
	public static enum EventType{PRESS,RELEASE,DRAG,ENTERED,EXIT};
	private State state = State.IDLE;
	
	public void paint(Graphics g){
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.drawImage(image, point.x-originComponent.getX(), point.y-originComponent.getY(), null);
	}
	
	public Cursor(JFrame frame,int id){
		this.originComponent=frame;
		this.ID=id;
		Toolkit kit = Toolkit.getDefaultToolkit();
        image = kit.createImage("Evolution Cursor.png").getScaledInstance(30, 30, Image.SCALE_DEFAULT);
		int centerComponentX = 1920/2;
		int centerComponentY = 1080/2;
		this.point = new Point(centerComponentX,centerComponentY);
	}
	
	public Point getPoint() {
		return point;
	}
	
	public void setPoint(int x,int y){
		if(x<=originComponent.getX()+originComponent.getWidth() && x>=originComponent.getX()){
			point.x=x;
			//System.out.println("point.x="+point.x);
		}else if(y<originComponent.getX()){
			point.x=originComponent.getX();
		}
		if(y<=originComponent.getY()+originComponent.getHeight() && y>=originComponent.getY()){
			point.y=y;
			//System.out.println("point.y="+point.y);
		}else if(y<originComponent.getY()){
			point.y=originComponent.getY();
		}
	}
	
	public void setPoint(Point point){
		this.point=point;
	}
		
	private void createMouseEvent(EventType eventType,Component component){
		MouseEvent me =null;
		switch (eventType) {
		case PRESS:
			me = new MouseEvent(component, MouseEvent.MOUSE_PRESSED, Instant.now().getEpochSecond(), 0,  point.x, point.y, 1, true,MouseEvent.BUTTON1);
			Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(me);
			System.out.println("Action PRESS: on component: "+ component+" on point : " +point);
			//Event.fireEvent(ApplicationInterface.listTitle.get(0), new javafx.scene.input.MouseEvent(javafx.scene.input.MouseEvent.MOUSE_CLICKED,point.x-component.getX(),point.y,scene.getX(),scene.getY(),javafx.scene.input.MouseButton.PRIMARY,1,false,false,false,false,true,false,false,false,false,false,null));
			//Event.fireEvent(scene, new javafx.scene.input.MouseEvent(javafx.scene.input.MouseEvent.MOUSE_PRESSED,point.x-component.getX(),point.y,scene.getX(),scene.getY(),javafx.scene.input.MouseButton.PRIMARY,1,false,false,false,false,true,false,false,false,false,false,null));
			state = State.PRESS;
			break;
		case RELEASE: 
			me = new MouseEvent(component, MouseEvent.MOUSE_RELEASED, Instant.now().getEpochSecond(), 0,  point.x, point.y, 0, false,MouseEvent.BUTTON1);
			Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(me);
			//Event.fireEvent(ApplicationInterface.listTitle.get(0), new javafx.scene.input.MouseEvent(javafx.scene.input.MouseEvent.MOUSE_RELEASED,point.x-component.getX(),point.y,scene.getX(),scene.getY(),javafx.scene.input.MouseButton.PRIMARY,1,false,false,false,false,false,false,false,false,false,false,null));
			System.out.println("Action RELEASE: on component: "+ component+" on point : " +point);
			state = State.IDLE;
			break;
		
		case DRAG: 
			switch(state){
			case PRESS:
				me = new MouseEvent(component, MouseEvent.MOUSE_DRAGGED, Instant.now().getEpochSecond(), 0,  point.x, point.y, 1, false);
				Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(me);
				break;
			case IDLE:
				break;
			default:
				break;
			}
			break;
		case ENTERED:
			me = new MouseEvent(component, MouseEvent.MOUSE_ENTERED, Instant.now().getEpochSecond(), 0, point.x, point.y, 0, false);
			Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(me);
			System.out.println("Action ENTERED: on component: "+ component+" on point : " +point);
			break;
		case EXIT:
			me = new MouseEvent(component, MouseEvent.MOUSE_EXITED, Instant.now().getEpochSecond(), 0, point.x, point.y, 0, false);
			Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(me);
			System.out.println("Action EXITED: on component: "+ component+" on point : " +point);
			break;
		default:
			break;
		}
	}
	
	public void setScene(Scene scene) {
		//this.scene = scene;
	}
	

	public int getID() {
		return ID;
	}
	
	public void mouseMove(int x,int y){
		setPoint(x, y);
		createMouseEvent(EventType.DRAG,originComponent);
		originComponent.repaint();
	}
	
	public void mousePress(){
		createMouseEvent(EventType.PRESS, originComponent);
	}

	public void mouseRelease() {
		createMouseEvent(EventType.RELEASE, originComponent);
	}
	
	public boolean possessCursor(){
		return point.equals(MouseInfo.getPointerInfo().getLocation());
	}
	
	public void repaint(){
		originComponent.repaint();
	}

	@Override
	public boolean equals(Object obj) {
		System.out.println("call to equals of cursor");
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		Cursor other = (Cursor) obj;
		if (this.ID != other.getID())
			return false;
		return true;
	}
	
	
}
