package gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.event.MouseInputAdapter;

import gui.ApplicationInterface.Doc;
import javafx.scene.shape.Rectangle;

public class DragListener extends MouseInputAdapter {
	
	Point location;
	Point begin;
    MouseEvent pressed;
	private Doc doc;
    ArrayList<Rectangle> rectList;
 
    public DragListener(Doc doc,ArrayList<Rectangle> list) {
		super();
		this.doc=doc;
		rectList = list;
	}

	
    public void mousePressed(MouseEvent me)
    {
        pressed = me;
        begin = me.getComponent().getLocation(begin);
    }
 
    @Override
    public void mouseReleased(MouseEvent me){
    	int x = location.x + me.getX();
        int y = location.y + me.getY();
        boolean change = false;
        for (int i = 0; i < rectList.size(); i++) {
        	Rectangle rectCurr = rectList.get(i);
        	if(rectCurr.contains(x,y)){
        		change = true;
        		if(doc.index != i){
	                ApplicationInterface.getListView().get(doc.index).getItems().remove(doc);
	                doc.index = i;
	                ApplicationInterface.getListView().get(doc.index).getItems().add(doc);
	                ApplicationInterface.getListView().get(doc.index).getSelectionModel().select(doc);
	                ApplicationInterface.getListTitles().get(doc.index).setExpanded(true);
        		}
        		else {
        			me.getComponent().setLocation(location.x + me.getX()-pressed.getX(),location.y + me.getY()- pressed.getY());
        		}
        	}
		}
        if(!change)me.getComponent().setLocation(begin);
        this.doc.setPercent();
    }
    
    public void mouseDragged(MouseEvent me)
    {
        Component component = me.getComponent();
        location = component.getLocation(location);
        int x = location.x - pressed.getX() + me.getX();
        int y = location.y - pressed.getY() + me.getY();
        component.setLocation(x, y);
     }
}
