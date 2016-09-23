package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import mouse.control.PreviewEvent;
import mouse.control.PreviewEventListener;

public class TransparentWindow implements PreviewEventListener {

	private List<Point> pointList = new ArrayList<>();
	private Point precPoint;
	private final int ARR_SIZE = 7;
	private final static int ARROW_LENGHT = 25;
	//private final int POINT_DIAMETER = 16;
	private final int CONE_ANGLE = 60;//in degrees
	private Image cursorPreview;
	
	private JFrame w;
	private Rectangle bounds;
	private float a;
	private float b;
	private boolean drawRegressionLine=false;
	private boolean isVertical=false;
	private static boolean DRAW_REGRESSION_LINE=true;
	private static boolean DRAW_FINAL_POINT = true;
	private static boolean DRAW_LINE = false;
	private static boolean DRAW_ARROW = true;
	private static boolean DRAW_PATH=false;
	private static boolean DRAW_CONE=false;
	private static final int  MINIMAL_DISTANCE_DRAW_ARROW_OR_CONE= ARROW_LENGHT;
	
	public TransparentWindow() {
		initNewWindow();
	}
	
	public TransparentWindow(Rectangle bounds){
		this.bounds = bounds;
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
				this.toFront();
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
							//g2.setColor(Color.DARK_GRAY);
							g2.drawImage(cursorPreview, lastPoint.x, lastPoint.y, null);
							//g2.fillOval(lastPoint.x-(POINT_DIAMETER/2), lastPoint.y-(POINT_DIAMETER/2), POINT_DIAMETER, POINT_DIAMETER);
						}
						if(firstPoint.distance(lastPoint)>MINIMAL_DISTANCE_DRAW_ARROW_OR_CONE){
							if(DRAW_CONE){
								drawCone(g2, firstPoint.x, firstPoint.y, lastPoint.x, lastPoint.y, ARROW_LENGHT);
							}
							
							if(DRAW_ARROW){
								g2.setColor(Color.ORANGE);
								drawArrow(g2, firstPoint.x, firstPoint.y, lastPoint.x, lastPoint.y, ARROW_LENGHT);
							}
						}
						
						
					}
				}
				if(drawRegressionLine){
					float y1;
					float y2;
					Graphics2D g2 = (Graphics2D) g;
					BasicStroke line = new BasicStroke(2.5f);
					g2.setStroke(line);
					g2.setColor(Color.MAGENTA);
					if(!isVertical){
						for(int x=0;x<this.getWidth()-1;x++){
							y1 = a * x + b;
							y2 = a * (x+1) + b;
							g2.drawLine(x-getX(), Math.round(y1)-getY(), (x+1)-getX(), Math.round(y2)-getY());
						}
					}else if(pointList.size()>0){
						Point firstPoint = pointList.get(0);
						g2.drawLine( firstPoint.x, getY(), firstPoint.x, getHeight());
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
		if(bounds == null){
			bounds = w.getGraphicsConfiguration().getBounds();
		}
		Toolkit kit = Toolkit.getDefaultToolkit();
		cursorPreview = kit.createImage("Windows_Cursor.png").getScaledInstance(12, 19, Image.SCALE_DEFAULT);
		w.setBounds(bounds);
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
		
		if(x<bounds.x){
			x = bounds.x;
		}else if(x>(w.getWidth()+bounds.x)){
			x = w.getWidth()+bounds.x;
		}
		if(y<bounds.y){
			y=bounds.y;
		}else if(y>(w.getHeight()+bounds.y)){
			y=w.getHeight()+bounds.y;
		}
		
		Point point = new Point(x-bounds.x,y-bounds.y);
		if(precPoint== null || !precPoint.equals(point)){
			synchronized (pointList) {
				pointList.add(point);
			}
			precPoint=point;
		}
		w.repaint();
	}
	
	@Override
	public void drawRegressionLine(float a, float b, boolean isVertical) {
		this.a = a;
		this.b = b;
		this.isVertical = isVertical;
		drawRegressionLine=DRAW_REGRESSION_LINE;
		w.repaint();
	}

	@Override
	public void removePreview() {
		synchronized (pointList) {
			pointList.clear();
		}
		drawRegressionLine=false;
		w.repaint();
	}

	public static boolean isDRAW_FINAL_POINT() {
		return DRAW_FINAL_POINT;
	}

	public static void setDRAW_FINAL_POINT(boolean dRAW_FINAL_POINT) {
		DRAW_FINAL_POINT = dRAW_FINAL_POINT;
	}

	public static boolean isDRAW_LINE() {
		return DRAW_LINE;
	}

	public static void setDRAW_LINE(boolean dRAW_LINE) {
		DRAW_LINE = dRAW_LINE;
	}

	public static boolean isDRAW_ARROW() {
		return DRAW_ARROW;
	}

	public static void setDRAW_ARROW(boolean dRAW_ARROW) {
		DRAW_ARROW = dRAW_ARROW;
	}

	public static boolean isDRAW_PATH() {
		return DRAW_PATH;
	}

	public static void setDRAW_PATH(boolean dRAW_PATH) {
		DRAW_PATH = dRAW_PATH;
	}

	public static boolean isDRAW_CONE() {
		return DRAW_CONE;
	}

	public static void setDRAW_CONE(boolean dRAW_CONE) {
		DRAW_CONE = dRAW_CONE;
	}

	public static boolean isDRAW_REGRESSION_LINE() {
		return DRAW_REGRESSION_LINE;
	}

	public static void setDRAW_REGRESSION_LINE(boolean dRAW_REGRESSION_LINE) {
		DRAW_REGRESSION_LINE = dRAW_REGRESSION_LINE;
	}
	
}
