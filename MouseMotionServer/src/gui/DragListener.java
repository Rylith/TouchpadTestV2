package gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.event.MouseInputAdapter;

import gui.ApplicationInterface.Doc;
import javafx.application.Platform;
import javafx.scene.shape.Rectangle;

public class DragListener extends MouseInputAdapter {
	
	Point location;
	Point begin;
    MouseEvent pressed;
	private Doc doc;
    List<Rectangle> rectList;
    private boolean change;
 
    public DragListener(Doc doc,List<Rectangle> rectList2) {
		super();
		this.doc=doc;
		rectList = rectList2;
	}

	@Override
    public void mousePressed(MouseEvent me)
    {
        pressed = me;
        begin = me.getComponent().getLocation(begin);
    }
 
    @Override
    public void mouseReleased(MouseEvent me){
    	int x = location.x + me.getX();
        int y = location.y + me.getY();
        change = false;
        for (int i = 0; i < rectList.size(); i++) {
        	Rectangle rectCurr = rectList.get(i);
        	if(rectCurr.contains(x,y)) {
				change = true;
        		final int fi = i;
        		final MouseEvent fme = me;
        		Platform.runLater(new Runnable() {
					@Override
					public void run() {
		        		if(doc.index != fi) {
			                ApplicationInterface.getListView().get(doc.index).getItems().remove(doc);
			                doc.index = fi;
			                ApplicationInterface.getListView().get(doc.index).getItems().add(doc);
			                ApplicationInterface.getListView().get(doc.index).getSelectionModel().select(doc);
			                ApplicationInterface.getListTitles().get(doc.index).setExpanded(true);
			                doc.positon(doc.index);
		        		}
		        		else {
		        			fme.getComponent().setLocation(location.x + fme.getX()-pressed.getX(),location.y + fme.getY()- pressed.getY());
		        			location = fme.getComponent().getLocation(location);
		        		}
		        		doc.setPercent();
					}
        		});
        	}
		}
        if(!change){
        	me.getComponent().setLocation(begin);
        }
        this.doc.setPercent();
        
    }
    
    @Override
    public void mouseDragged(MouseEvent me)
    {
        Component component = me.getComponent();
        location = component.getLocation(location);
        int x = location.x - pressed.getX() + me.getX();
        int y = location.y - pressed.getY() + me.getY();
        component.setLocation(x, y);
     }
}
