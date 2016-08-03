package gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.event.MouseInputAdapter;

import gui.ApplicationInterface.Doc;
import javafx.scene.shape.Rectangle;

public class DragListener extends MouseInputAdapter {
	
	Point location;
    MouseEvent pressed;
    ArrayList<Rectangle> rectList;
 
    public DragListener(ArrayList<Rectangle> list) {
		super();
		rectList = list;
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
        
        for (int i = 0; i < rectList.size(); i++) {
        	Rectangle rectCurr = rectList.get(i);
        	if(rectCurr.contains(x,y)){
                component.setLocation(x, y);
        	}
		}
     }
}
