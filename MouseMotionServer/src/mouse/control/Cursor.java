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
import javax.swing.JMenu;

import javafx.scene.Scene;

public class Cursor extends java.awt.Cursor{

	private Point point;
	private Component component;
	private Component originComponent;
	//private Scene scene;
	private Image image;
	private int ID;
	public static enum State{IDLE,PRESS};
	public static enum EventType{PRESS,RELEASE,DRAG,ENTERED,EXIT};
	private State state = State.IDLE;
	private int offsetX;
	private int yOffset =0;
	private JMenu mComponent;
	private boolean find=false;
	/**
	 * 
	 */
	private static final long serialVersionUID = -417141145656113947L;
	
	
    public void paint(Graphics g){
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.drawImage(image, point.x-offsetX, point.y-yOffset, null);
		g2d.finalize();
	}
	
	public Cursor(Component component,int id){
		super(id);
		this.originComponent=component;
		this.component=component;
		this.ID=id;
		Toolkit kit = Toolkit.getDefaultToolkit();
        image = kit.createImage("Evolution Cursor.png").getScaledInstance(30, 30, Image.SCALE_DEFAULT);
		int centerComponentX = component.getWidth()/2;
		int centerComponentY = component.getHeight()/2;
		this.point = new Point(centerComponentX,centerComponentY);
	}
	
	public Point getPoint() {
		return point;
	}
	
	public void setPoint(int x,int y){
		if(x<=originComponent.getX()+originComponent.getWidth() && x>=0){
			point.x=x;
			//System.out.println("point.x="+point.x);
		}
		if(y<=originComponent.getY()+originComponent.getHeight() && y>=0){
			//System.out.println("point.y="+point.y);
			point.y=y;
		}
	}
	
	public void setPoint(Point point){
		this.point=point;
	}
	
	public void setComponent(Component component) {
		this.component = component;
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
	
	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}


	public int getID() {
		return ID;
	}
	
	public void mouseMove(int x,int y){
		setPoint(x, y);
		
		/*if(component instanceof JMenuBar){
			//createMouseEvent(EventType.ENTERED, component);
			for(int i=0;i<((JMenuBar) component).getMenuCount();i++){
				JMenu menu = ((JMenuBar) component).getMenu(i);
				Point pos = menu.getLocation();
				if(pos.x<=x && (pos.x+menu.getWidth())>=x){
					if((mComponent == null || !mComponent.equals(menu))){
						createMouseEvent(EventType.ENTERED, menu);
						mComponent=menu;
					}
					find = true;
				}else if((mComponent == null || !mComponent.equals(menu))){
					createMouseEvent(EventType.EXIT, menu);
				}
			}
		}else{
			if(find){
				createMouseEvent(EventType.EXIT, mComponent);
				find=false;
			}
		}
		createMouseEvent(EventType.DRAG,component);*/
		
		originComponent.repaint();
		
	}
	
	public void mousePress(){
		if(!find){
			createMouseEvent(EventType.PRESS,component);
		}else
			createMouseEvent(EventType.PRESS, mComponent);
	}

	public void mouseRelease() {
		if(!find){
		createMouseEvent(EventType.RELEASE,component);
		}else
			createMouseEvent(EventType.RELEASE,mComponent);
	}

	public void setoffsetY(int yOffset) {
		this.yOffset = yOffset;
		
	}
	
	public boolean possessCursor(){
		return point.equals(MouseInfo.getPointerInfo().getLocation());
	}
	
	public void repaint(){
		originComponent.repaint();
	}
}
