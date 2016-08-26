package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import mouse.control.PreviewEvent;
import mouse.control.PreviewEventListener;

public class TransparentWindow implements PreviewEventListener {

	private List<Point> pointList = new ArrayList<>();	
	private static JFrame w;
	
	public TransparentWindow() {
		initNewWindow();
	}
	
	private void initNewWindow(){
		w = new JFrame(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 2854572850480717034L;
			
			
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				
				if(pointList.size()>1){
					final Font font = getFont().deriveFont(48f);
					g.setFont(font);
					g.setColor(Color.RED);
					Point firstPoint = pointList.get(0);
					Point lastPoint = pointList.get(pointList.size()-1);
					g.drawLine(firstPoint.x, firstPoint.y, lastPoint.x, lastPoint.y);
					g.setColor(Color.BLUE);
					synchronized (pointList) {
			
					for(int i=0; i<pointList.size()-1;i++){
						g.drawLine(pointList.get(i).x, pointList.get(i).y, pointList.get(i+1).x, pointList.get(i+1).y);
					}
					}
				}
			}
			
			@Override
			public void update(Graphics arg0) {
				paint(arg0);
			}
		};
		w.setType(JFrame.Type.UTILITY);
		w.setAlwaysOnTop(true);
		w.setUndecorated(true);
		w.setBounds(w.getGraphicsConfiguration().getBounds());
		w.setBackground(new Color(0,true));
		new PreviewEvent().addPreviewEventListener(this);
		w.setVisible(true);
	}
		

	@Override
	public void drawPreview(int x, int y) {
		synchronized (pointList) {
			pointList.add(new Point(x,y));
		}
		w.repaint();
	}

	@Override
	public void removePreview() {
		synchronized (pointList) {
			pointList.clear();
		}
		w.repaint();
	}
}
