package gui;

import java.awt.Graphics;

import javafx.embed.swing.JFXPanel;

public class JFXPanelWithCustomCursor extends JFXPanel {

	private static final long serialVersionUID = -5498569285766941604L;
	private int yOffset;
	private int originalYOffset;
	private int xOffset;
	
	public void setyOffset(int yOffset) {
		this.yOffset = yOffset;
		originalYOffset = yOffset;
	}

	public JFXPanelWithCustomCursor(int xOffset,int yOffset){
		this.xOffset=xOffset;
		this.yOffset=yOffset;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		for(mouse.control.Cursor cursor : ApplicationInterface.listCursor ){
			if(this.contains(cursor.getPoint().x-xOffset,cursor.getPoint().y-yOffset)){
				//cursor.setComponent(dp);
				cursor.setoffsetY(yOffset);
				cursor.setOffsetX(xOffset);
				cursor.paint(g);
			}
		}
	}
	
	public void resetYOffset(){
		yOffset=originalYOffset;
	}

}
