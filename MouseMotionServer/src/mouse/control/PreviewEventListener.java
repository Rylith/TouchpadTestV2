package mouse.control;

import java.util.EventListener;

public interface PreviewEventListener extends EventListener{
	
	/**
	 * @param : x coordinate of the next position of mouse  
	 * @param : y coordinate of the next position of mouse */
	public void drawPreview(int x, int y);
	
	public void removePreview();
	
	/**
	 * Give the information to draw the regression line
	 * @param a slope of the regression line  
	 * @param b intercept of the regression line
	 * @param isVertical if the line is not a function but a vertical line */
	public void drawRegressionLine(float a, float b, boolean isVertical);

}
