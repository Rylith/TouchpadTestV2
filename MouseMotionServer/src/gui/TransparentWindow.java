package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import mouse.control.PreviewEvent;
import mouse.control.PreviewEventListener;

public class TransparentWindow implements PreviewEventListener {

	private List<Point> pointList = new ArrayList<>();
	private final int ARR_SIZE = 7;
	private final int ARROW_LENGHT = 25;
	private final int POINT_DIAMETER = 16;
	private final int CONE_ANGLE = 60;//in degrees
	
	private static JFrame w;
	private boolean DRAW_FINAL_POINT = true;
	private boolean DRAW_LINE = true;
	private boolean DRAW_ARROW = true;
	private boolean DRAW_PATH=true;
	private boolean DRAW_CONE=true;
	
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
				synchronized (pointList) {
					if(pointList.size()>1){
						//final Font font = getFont().deriveFont(48f);
						//g.setFont(font);
						Graphics2D g2 = (Graphics2D) g;
						BasicStroke line = new BasicStroke(2.5f);
						g2.setStroke(line);
						Point firstPoint = pointList.get(0);
						Point lastPoint = pointList.get(pointList.size()-1);
						if(DRAW_LINE){
							g2.setColor(Color.RED);
							g2.drawLine(firstPoint.x, firstPoint.y, lastPoint.x, lastPoint.y);
						}
						if(DRAW_PATH){
							g2.setColor(Color.BLUE);
							for(int i=0; i<pointList.size()-1;i++){
								g2.drawLine(pointList.get(i).x, pointList.get(i).y, pointList.get(i+1).x, pointList.get(i+1).y);
							}
						}
						if(DRAW_FINAL_POINT){
							g2.setColor(Color.DARK_GRAY);
							g2.fillOval(lastPoint.x-(POINT_DIAMETER/2), lastPoint.y-(POINT_DIAMETER/2), POINT_DIAMETER, POINT_DIAMETER);
						}
						if(DRAW_ARROW){
							g2.setColor(Color.ORANGE);
							drawArrow(g2, firstPoint.x, firstPoint.y, lastPoint.x, lastPoint.y, ARROW_LENGHT);
						}
						
						if(DRAW_CONE){
							drawCone(g2, firstPoint.x, firstPoint.y, lastPoint.x, lastPoint.y, ARROW_LENGHT);
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
	
	private void drawArrow(Graphics g, int x1, int y1, int x2, int y2,int lenght){
		Graphics2D g2 = (Graphics2D) g.create();
		
		double dx = x2 - x1, dy = y2 -y1;
		double angle = Math.atan2(dy, dx);
		AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
		at.concatenate(AffineTransform.getRotateInstance(angle));
		g2.transform(at);
		
		g2.drawLine(0, 0, lenght, 0);
		g2.fillPolygon(new int[]{lenght,lenght-ARR_SIZE, lenght-ARR_SIZE,lenght},new int[]{0,-ARR_SIZE,ARR_SIZE,0},4);
	}
	
	private void drawCone(Graphics g, int x1, int y1, int x2, int y2,int lenght){
		Graphics2D g2 = (Graphics2D) g.create();
		
		double dx = x2 - x1, dy = y2 -y1;
		double angle = Math.atan2(dy, dx);
		AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
		at.concatenate(AffineTransform.getRotateInstance(angle));
		g2.transform(at);
		
		g2.setPaint(new GradientPaint(lenght, Math.round(lenght*Math.cos(Math.toRadians(CONE_ANGLE))), new Color(0,true), 0, 0,Color.WHITE));
		g2.fillPolygon(new int[]{0,lenght,lenght}, new int[]{0,0,(int) Math.round(lenght*Math.cos(Math.toRadians(CONE_ANGLE)))},3);
		
		//g2.drawLine(0, 0, lenght, (int) Math.round(lenght*Math.cos(Math.toRadians(CONE_ANGLE))));
		g2.setPaint(new GradientPaint(lenght, Math.round(-lenght*Math.cos(Math.toRadians(CONE_ANGLE))), new Color(0,true), 0, 0,Color.WHITE));
		g2.fillPolygon(new int[]{0,lenght,lenght}, new int[]{0,0,(int) Math.round(-lenght*Math.cos(Math.toRadians(CONE_ANGLE)))},3);
		//g2.drawLine(0, 0, lenght, (int) Math.round(-lenght*Math.cos(Math.toRadians(CONE_ANGLE))));
	}
		

	@Override
	public void drawPreview(int x, int y) {
		if(!(x>=0 && x<=w.getWidth())){
			x = pointList.get(pointList.size()-1).x;
				
		}
		if(!(y>=0 && y<=w.getHeight())){
			y = pointList.get(pointList.size()-1).y;
		}
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
