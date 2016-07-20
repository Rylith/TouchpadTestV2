package mouse.control;

public interface IMouseListener {
	
	/**Initialize the coordinates of the center of the device*/
	public void setCenter(int x, int y);
	
	/**Called when a continues movement is done on the screen of the device*/
	public float onScroll(float x, float y, float distanceX, float distanceY);
	
	/**Reset data to calculate the line*/
	public void resetBuffers(float x,float y);
	
	/**Simulate a continue left click*/
	public void press();
	
	/**Simulate the release of left click*/
	public void release();
	
	/**Simulation of a click (press then release)*/
	public void click();
	
	/**Simulation of double click*/
	public void doubleClick();
	
}
