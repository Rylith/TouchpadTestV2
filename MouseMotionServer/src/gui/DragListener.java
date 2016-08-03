package gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import gui.ApplicationInterface.Doc;

public class DragListener extends MouseInputAdapter {
	
	Point location;
    MouseEvent pressed;
	private Doc doc;
	
	public DragListener(){
		
	}
	
	public DragListener(Doc doc){
		this.doc=doc;
	}
	
    public void mousePressed(MouseEvent me)
    {
        pressed = me;
    }
 
    public void mouseDragged(MouseEvent me)
    {
        Component component = me.getComponent();
        location = component.getLocation(location);
        int x = location.x - pressed.getX() + me.getX();
        int y = location.y - pressed.getY() + me.getY();
        component.setLocation(x, y);
        this.doc.setPercent();
     }

}
