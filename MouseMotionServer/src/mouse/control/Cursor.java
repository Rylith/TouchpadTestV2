package mouse.control;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import javafx.scene.Scene;

public class Cursor{

	private Point point;
	private JFrame originComponent;
	private Component currentComponent;
	private static Map<Component, Boolean> componentMap = new HashMap<Component, Boolean>();
	//private Scene scene;
	private Image image;
	private int ID;
	public static enum State{IDLE,PRESS};
	public static enum EventType{PRESS,RELEASE,CLICK,DRAG,ENTERED,EXIT};
	private State state = State.IDLE;
	
	public void paint(Graphics g){
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.drawImage(image, point.x-originComponent.getX(), point.y-originComponent.getY(), null);
	}
	
	public Cursor(JFrame frame,int id){
		this.originComponent=frame;
		this.ID=id;
		Toolkit kit = Toolkit.getDefaultToolkit();
        image = kit.createImage("Evolution Cursor.png").getScaledInstance(32, 32, Image.SCALE_DEFAULT);
        Dimension scrDim = Toolkit.getDefaultToolkit().getScreenSize();
        int centerComponentX = scrDim.width/2;
		int centerComponentY = scrDim.height/2;
		this.point = new Point(centerComponentX,centerComponentY);
	}
	
	public Point getPoint() {
		return point;
	}
	
	public void setPoint(int x,int y){
		if(x<=originComponent.getX()+originComponent.getWidth() && x>=originComponent.getX()){
			point.x = x;
			//System.out.println("point.x="+point.x);
		}else if(x<originComponent.getX()){
			point.x = originComponent.getX();
		}else if(x>originComponent.getX()+originComponent.getWidth()){
			point.x = originComponent.getX()+originComponent.getWidth();
		}
		if(y<=originComponent.getY()+originComponent.getHeight() && y>=originComponent.getY()){
			point.y = y;
			//System.out.println("point.y="+point.y);
		}else if(y<originComponent.getY()){
			point.y = originComponent.getY();
		}else if(y > originComponent.getY()+originComponent.getHeight()){
			point.y = originComponent.getY()+originComponent.getHeight();
		}
	}
	
	public void setPoint(Point point){
		this.point=point;
	}
		
	private void createMouseEvent(EventType eventType,Component component, boolean popupTrigger){
		MouseEvent me =null;
		if(component !=null ){
			switch (eventType) {
			case PRESS:
				me = new MouseEvent(component, MouseEvent.MOUSE_PRESSED, Instant.now().getEpochSecond(), 0,  point.x, point.y, 1, popupTrigger,MouseEvent.BUTTON1);
				Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(me);
				//component.dispatchEvent(me);
				System.out.println("Action PRESS: on component: "+ component+" on point : " +point);
				//Event.fireEvent(ApplicationInterface.listTitle.get(0), new javafx.scene.input.MouseEvent(javafx.scene.input.MouseEvent.MOUSE_CLICKED,point.x-component.getX(),point.y,scene.getX(),scene.getY(),javafx.scene.input.MouseButton.PRIMARY,1,false,false,false,false,true,false,false,false,false,false,null));
				//Event.fireEvent(scene, new javafx.scene.input.MouseEvent(javafx.scene.input.MouseEvent.MOUSE_PRESSED,point.x-component.getX(),point.y,scene.getX(),scene.getY(),javafx.scene.input.MouseButton.PRIMARY,1,false,false,false,false,true,false,false,false,false,false,null));
				state = State.PRESS;
				break;
			case CLICK:
				me = new MouseEvent(component, MouseEvent.MOUSE_CLICKED, Instant.now().getEpochSecond(), 0,  point.x, point.y, 1, popupTrigger,MouseEvent.BUTTON1);
				Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(me);
				System.out.println("Action CLICK: on component: "+ component+" on point : " +point);
				break;
			case RELEASE: 
				me = new MouseEvent(component, MouseEvent.MOUSE_RELEASED, Instant.now().getEpochSecond(), 0,  point.x, point.y, 0, popupTrigger,MouseEvent.BUTTON1);
				Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(me);
				//component.dispatchEvent(me);
				//Event.fireEvent(ApplicationInterface.listTitle.get(0), new javafx.scene.input.MouseEvent(javafx.scene.input.MouseEvent.MOUSE_RELEASED,point.x-component.getX(),point.y,scene.getX(),scene.getY(),javafx.scene.input.MouseButton.PRIMARY,1,false,false,false,false,false,false,false,false,false,false,null));
				System.out.println("Action RELEASE: on component: "+ component+" on point : " +point);
				state = State.IDLE;
				break;
			
			case DRAG: 
				switch(state){
				case PRESS:
					me = new MouseEvent(component, MouseEvent.MOUSE_DRAGGED, Instant.now().getEpochSecond(), 0,  point.x, point.y, 1, popupTrigger);
					Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(me);
					component.dispatchEvent(me);
					break;
				case IDLE:
					break;
				default:
					break;
				}
				break;
			case ENTERED:
				me = new MouseEvent(component, MouseEvent.MOUSE_ENTERED, Instant.now().getEpochSecond(), 0, point.x, point.y, 0, popupTrigger);
				Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(me);
				//component.dispatchEvent(me);
				//System.out.println("Action ENTERED: on component: "+ component+" on point : " +point);
				break;
			case EXIT:
				me = new MouseEvent(component, MouseEvent.MOUSE_EXITED, Instant.now().getEpochSecond(), 0, point.x, point.y, 0, false);
				Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(me);
				//component.dispatchEvent(me);
				//System.out.println("Action EXITED: on component: "+ component+" on point : " +point);
				break;
			default:
				break;
			}
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
		synchronized (componentMap) {
			
			for(Entry<Component,Boolean> entry : componentMap.entrySet()){
				Component compo = entry.getKey();
				if(compo.isShowing()){
					boolean popuptrigger=false;
					Point positionOnScreen = compo.getLocationOnScreen();
					boolean isInside = entry.getValue().booleanValue();
					if(point.x<=compo.getWidth()+positionOnScreen.x && point.x>=positionOnScreen.x && point.y>=positionOnScreen.y && point.y<=compo.getHeight()+positionOnScreen.y){
						if(!isInside){
							//compo.requestFocus();
							currentComponent=compo;
							
							if(compo instanceof JMenuItem || compo instanceof JPopupMenu){
								popuptrigger=true;
							}
							createMouseEvent(EventType.ENTERED, compo,popuptrigger);
							componentMap.replace(compo, new Boolean(true));
						}
						
					}else if(isInside){
							if(compo instanceof JMenuItem){
								popuptrigger=true;
							}
							createMouseEvent(EventType.EXIT, compo,popuptrigger);
							componentMap.replace(compo, new Boolean(false));
					}
				}
			}
		}
		createMouseEvent(EventType.DRAG,currentComponent,false);
		originComponent.repaint();
	}
	
	public void mousePress(){
		if(currentComponent instanceof AbstractButton && !(currentComponent instanceof JMenu)){
			((JMenuItem) currentComponent).doClick();
		}
		createMouseEvent(EventType.PRESS, currentComponent,false);
	}

	public void mouseRelease() {
		if(!(currentComponent instanceof JMenu)){
			createMouseEvent(EventType.RELEASE, currentComponent,false);
		}
	}
	
	private static Map<Component, Boolean> getAllComponents(final Container c){
		Component[] comps = c.getComponents();
		Map<Component, Boolean> componentMap = new HashMap<>();
		for(Component comp : comps){
			if(comp.getMouseListeners().length>0 || comp.getMouseMotionListeners().length>0){
				componentMap.put(comp, new Boolean(false));
			}
			if(comp instanceof JMenu){
				int lenght = ((JMenu)comp).getItemCount();
				for(int i = 0; i < lenght ;i++){
					componentMap.put(((JMenu)comp).getItem(i), new Boolean(false));
				}
			}
			if(comp instanceof Container){
				componentMap.putAll(getAllComponents((Container) comp));
			}
		}
		return componentMap;
	}
	
	public static void setComponents(final Container c){
		synchronized (componentMap) {
			componentMap.putAll(getAllComponents(c));
		}
	}
	
	public static void addComponent(final Component c){
		synchronized (componentMap) {
			componentMap.put(c, new Boolean(false));
		}
	}
		
	@Override
	public boolean equals(Object obj) {
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
