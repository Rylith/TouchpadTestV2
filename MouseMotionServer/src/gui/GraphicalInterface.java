package gui;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GraphicalInterface extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9002852749853545258L;

	public GraphicalInterface(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    this.setTitle("Réglage des paramètres");
	    this.setSize(1600, 900);
	    this.setLocationRelativeTo(null);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    JTextArea text = new JTextArea();
	    text.setEditable(false);
	    text.setText("blaaaaaaa");
	    
	    JSlider slider1 = new JSlider();
	    final JSlider slider2 = new JSlider();
	    slider2.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				slider2.getValue();
			}
		});
	    slider1.setMaximum(100);
	    slider1.setMinimum(0);
	    
	    SpinnerModel model = new SpinnerNumberModel();
	    JSpinner spinner = new JSpinner(model);
	    
	    JPanel south = new JPanel();
	    south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
	    south.add(new JSeparator());
	    south.add(slider1);
	    south.add(slider2);
	    south.add(spinner);
	    
	    JPanel panel = new JPanel(new BorderLayout());
	    panel.add(south,BorderLayout.SOUTH);
	    panel.add(text,BorderLayout.CENTER);
	    
	    this.add(panel);
	    //this.pack();
	    this.setVisible(true);
	    
	  }
	
}
