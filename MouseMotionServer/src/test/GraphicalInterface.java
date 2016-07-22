package test;

import mouse.control.*;
import javax.swing.JFrame;
import javax.swing.JSlider;

public class GraphicalInterface extends JFrame{
	
	public GraphicalInterface(){
	    this.setTitle("Réglage des paramètres");
	    this.setSize(500, 600);
	    this.setLocationRelativeTo(null);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);             

	    JSlider slider1 = new JSlider();
	    slider1.setMaximum(100);
	    slider1.setMinimum(0);
	    
	  }
	
}
