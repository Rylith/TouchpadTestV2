package mouse.control;

import java.awt.Point;

import network.Impl.Util;

public class PiloteListener extends IMouseListener {
	
	private static final AngleEvent event = new AngleEvent();
	
	@Override
	public float onScroll(float x, float y, float distanceX, float distanceY) {
		return 0;
	}
	
	@Override
	public void resetBuffers(float x, float y) {
		sendFeedBack();
		double angle = Util.angle(center, new Point(Math.round(x),Math.round(y)));
		event.sendAngle(angle);
	}
	
	@Override
	public void click() {
	}
	
	@Override
	public void doubleClick() {
	}
	
	
	

}
