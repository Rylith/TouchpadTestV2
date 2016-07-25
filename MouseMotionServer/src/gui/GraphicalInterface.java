package gui;

import mouse.control.*;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GraphicalInterface extends JFrame{
	
	private static final long serialVersionUID = -9002852749853545258L;
	
	public GraphicalInterface(){
		
	    this.setTitle("Réglage des paramètres");
	    this.setSize(500, 600);
	    this.setLocationRelativeTo(null);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);             

	    final JSlider sliderPercentScreen = new JSlider();  
	    final JSlider sliderTimerAff = new JSlider();
	    final JSlider sliderCoeffControl = new JSlider();
	    final JSlider sliderTestFluidity = new JSlider();
	    final JSlider sliderDivFluidity = new JSlider();
	    
	    sliderPercentScreen.setPreferredSize(new Dimension(400,100));
	    sliderPercentScreen.setMinimum(0);
	    sliderPercentScreen.setMaximum(100);
	    sliderPercentScreen.setValue(20);
	    sliderPercentScreen.setPaintTicks(true);
	    sliderPercentScreen.setPaintLabels(true);
	    sliderPercentScreen.setMinorTickSpacing(10);
	    sliderPercentScreen.setMajorTickSpacing(20);
	    
	    
	    sliderTimerAff.setPreferredSize(new Dimension(400,100));
	    sliderTimerAff.setMinimum(10);
	    sliderTimerAff.setMaximum(1000);
	    sliderTimerAff.setValue(500);
	    sliderTimerAff.setPaintTicks(true);
	    sliderTimerAff.setPaintLabels(true);
	    sliderTimerAff.setMinorTickSpacing(45);
	    sliderTimerAff.setMajorTickSpacing(90);
	    
	    
	    sliderCoeffControl.setPreferredSize(new Dimension(400,100));
	    sliderCoeffControl.setMinimum(1);
	    sliderCoeffControl.setMaximum(10);
	    sliderCoeffControl.setValue(2);
	    sliderCoeffControl.setPaintTicks(true);
	    sliderCoeffControl.setPaintLabels(true);
	    sliderCoeffControl.setMinorTickSpacing(1);
	    sliderCoeffControl.setMajorTickSpacing(1);
	    
	    
	    sliderTestFluidity.setPreferredSize(new Dimension(400,100));
	    sliderTestFluidity.setMinimum(1);
	    sliderTestFluidity.setMaximum(10);
	    sliderTestFluidity.setValue(6);
	    sliderTestFluidity.setPaintTicks(true);
	    sliderTestFluidity.setPaintLabels(true);
	    sliderTestFluidity.setMinorTickSpacing(1);
	    sliderTestFluidity.setMajorTickSpacing(1);
	    
	    
	    sliderDivFluidity.setPreferredSize(new Dimension(400,100));
	    sliderDivFluidity.setMinimum(1);
	    sliderDivFluidity.setMaximum(10);
	    sliderDivFluidity.setValue(2);
	    sliderDivFluidity.setPaintTicks(true);
	    sliderDivFluidity.setPaintLabels(true);
	    sliderDivFluidity.setMinorTickSpacing(1);
	    sliderDivFluidity.setMajorTickSpacing(1);
	    
	    
	    final JLabel labPercentScreen = new JLabel("Taille marge bord: " + sliderPercentScreen.getValue());
	    labPercentScreen.setPreferredSize(new Dimension(400,50));
	    final JLabel labTimerAff = new JLabel("Temps pour changer de mode: " + sliderTimerAff.getValue());
	    labTimerAff.setPreferredSize(new Dimension(400, 50));
	    final JLabel labCoeffControl = new JLabel("Multiplicatuer déplacement: " + sliderCoeffControl.getValue());
	    labCoeffControl.setPreferredSize(new Dimension(400, 50));
	    final JLabel labTestFluidity = new JLabel("Test fluidity: " + sliderTestFluidity.getValue());
	    labTestFluidity.setPreferredSize(new Dimension(400, 50));
	    final JLabel labDiviFluidity = new JLabel("Valeur fluidity: " + sliderDivFluidity.getValue());
	    labDiviFluidity.setPreferredSize(new Dimension(400, 50));
	    
	    sliderPercentScreen.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				labPercentScreen.setText("Taille marge bord: " + (float)sliderPercentScreen.getValue()/100);
			}
	    	
	    });
	    
	    sliderTimerAff.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				labTimerAff.setText("Temps pour changer de mode: " + sliderTimerAff.getValue());
			}
	    	
	    });
	    
	    sliderCoeffControl.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				MouseControl.setCoeff(sliderCoeffControl.getValue());
				labCoeffControl.setText("Multiplicatuer déplacement: " + sliderCoeffControl.getValue());
			}
	    	
	    });
	    
	    sliderTestFluidity.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				labTestFluidity.setText("Test fluidity: " + sliderTestFluidity.getValue());
				
			}
	    	
	    });
	    
	    sliderDivFluidity.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				labDiviFluidity.setText("Valeur fluidity: " + sliderDivFluidity.getValue());
			}
	    	
	    });
	    
	    //JPanel pan = new JPanel();
	    this.setLayout(new BoxLayout(this.getContentPane(),BoxLayout.Y_AXIS));

	    this.getContentPane().add(labPercentScreen);
	    this.getContentPane().add(sliderPercentScreen);
	    this.getContentPane().add(labTimerAff);
	    this.getContentPane().add(sliderTimerAff);
	    this.getContentPane().add(labCoeffControl);
	    this.getContentPane().add(sliderCoeffControl);
	    this.getContentPane().add(labTestFluidity);
	    this.getContentPane().add(sliderTestFluidity);
	    this.getContentPane().add(labDiviFluidity);
	    this.getContentPane().add(sliderDivFluidity);
	    
	  }
	
}
