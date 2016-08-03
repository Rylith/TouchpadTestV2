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
        		if(doc.index != i){
	                ApplicationInterface.getListView().get(doc.index).getItems().remove(doc);
	                doc.index = i;
	                ApplicationInterface.getListView().get(doc.index).getItems().add(doc);
	                ApplicationInterface.getListView().get(doc.index).getSelectionModel().select(doc);
	                ApplicationInterface.getListTitles().get(doc.index).setExpanded(true);
        		}
                component.setLocation(x, y);
        	}
		}
        //component.setLocation(x, y);
        this.doc.setPercent();
     }
}
