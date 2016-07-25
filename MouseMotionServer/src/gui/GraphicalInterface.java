package gui;

import mouse.control.*;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GraphicalInterface extends JFrame{
	
	private static final long serialVersionUID = -9002852749853545258L;
	
	public GraphicalInterface(){
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
	    this.setTitle("Réglage des paramètres");
	    this.setSize(500, 600);
	    this.setLocationRelativeTo(null);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);             

	    final JSlider sliderPercentScreen = new JSlider();  
	    final JSlider sliderTimerAff = new JSlider();
	    final JSlider sliderTimerMovement = new JSlider();
	    final JSlider sliderCoeffControl = new JSlider();
	    final JSlider sliderTestFluidity = new JSlider();
	    final JSlider sliderMultiFluidity = new JSlider();
	    
	    sliderPercentScreen.setPreferredSize(new Dimension(400,100));
	    sliderPercentScreen.setMinimum(0);
	    sliderPercentScreen.setMaximum(100);
	    sliderPercentScreen.setValue((int)IMouseListener.getPercentScreenSize()*100);
	    sliderPercentScreen.setPaintTicks(true);
	    sliderPercentScreen.setPaintLabels(true);
	    sliderPercentScreen.setMinorTickSpacing(10);
	    sliderPercentScreen.setMajorTickSpacing(20);
	    
	    
	    sliderTimerAff.setPreferredSize(new Dimension(400,100));
	    sliderTimerAff.setMinimum(10);
	    sliderTimerAff.setMaximum(1000);
	    sliderTimerAff.setValue((int)IMouseListener.getTimerAff());
	    sliderTimerAff.setPaintTicks(true);
	    sliderTimerAff.setPaintLabels(true);
	    sliderTimerAff.setMinorTickSpacing(45);
	    sliderTimerAff.setMajorTickSpacing(90);
	    
	    
	    sliderTimerMovement.setPreferredSize(new Dimension(400,100));
	    sliderTimerMovement.setMinimum(10);
	    sliderTimerMovement.setMaximum(1000);
	    sliderTimerMovement.setValue((int)IMouseListener.getTimerMovement());
	    sliderTimerMovement.setPaintTicks(true);
	    sliderTimerMovement.setPaintLabels(true);
	    sliderTimerMovement.setMinorTickSpacing(45);
	    sliderTimerMovement.setMajorTickSpacing(90);
	    
	    
	    sliderCoeffControl.setPreferredSize(new Dimension(400,100));
	    sliderCoeffControl.setMinimum(1);
	    sliderCoeffControl.setMaximum(10);
	    sliderCoeffControl.setValue(MouseControl.getCoeff());
	    sliderCoeffControl.setPaintTicks(true);
	    sliderCoeffControl.setPaintLabels(true);
	    sliderCoeffControl.setMinorTickSpacing(1);
	    sliderCoeffControl.setMajorTickSpacing(1);
	    
	    
	    sliderTestFluidity.setPreferredSize(new Dimension(400,100));
	    sliderTestFluidity.setMinimum(1);
	    sliderTestFluidity.setMaximum(10);
	    sliderTestFluidity.setValue(MouseControl.getTestF());
	    sliderTestFluidity.setPaintTicks(true);
	    sliderTestFluidity.setPaintLabels(true);
	    sliderTestFluidity.setMinorTickSpacing(1);
	    sliderTestFluidity.setMajorTickSpacing(1);
	    
	    
	    sliderMultiFluidity.setPreferredSize(new Dimension(400,100));
	    sliderMultiFluidity.setMinimum(1);
	    sliderMultiFluidity.setMaximum(10);
	    sliderMultiFluidity.setValue(MouseControl.getMultiF());
	    sliderMultiFluidity.setPaintTicks(true);
	    sliderMultiFluidity.setPaintLabels(true);
	    sliderMultiFluidity.setMinorTickSpacing(1);
	    sliderMultiFluidity.setMajorTickSpacing(1);
	    
	    
	    final JLabel labPercentScreen = new JLabel("Taille marge bord: " + sliderPercentScreen.getValue());
	    labPercentScreen.setPreferredSize(new Dimension(400,50));
	    final JLabel labTimerAff = new JLabel("Temps pour changer de mode: " + sliderTimerAff.getValue());
	    labTimerAff.setPreferredSize(new Dimension(400, 50));
	    final JLabel labTimerMovement = new JLabel("Temps entre deux déplacements: " + sliderTimerMovement.getValue());
	    labTimerMovement.setPreferredSize(new Dimension(400, 50));
	    final JLabel labCoeffControl = new JLabel("Multiplicatuer déplacement: " + sliderCoeffControl.getValue());
	    labCoeffControl.setPreferredSize(new Dimension(400, 50));
	    final JLabel labTestFluidity = new JLabel("Test fluidity: " + sliderTestFluidity.getValue());
	    labTestFluidity.setPreferredSize(new Dimension(400, 50));
	    final JLabel labMultiFluidity = new JLabel("Valeur fluidity: " + sliderMultiFluidity.getValue());
	    labMultiFluidity.setPreferredSize(new Dimension(400, 50));
	    
	    sliderPercentScreen.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				IMouseListener.setPercentScreenSize((float)sliderPercentScreen.getValue()/100);
				labPercentScreen.setText("Taille marge bord: (En %)" + IMouseListener.getPercentScreenSize());
			}
	    });
	    
	    sliderTimerAff.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				IMouseListener.setTimerAff((long)sliderTimerAff.getValue());
				labTimerAff.setText("Temps pour changer de mode: " + IMouseListener.getTimerAff());
			}
	    });
	    
	    sliderTimerMovement.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				IMouseListener.setTimerMovement(sliderTimerMovement.getValue());
				labTimerAff.setText("Temps entre deux déplacements: " + IMouseListener.getTimerMovement());
			}
	    });
	    
	    sliderCoeffControl.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {

				MouseControl.setCoeff(sliderCoeffControl.getValue());
				labCoeffControl.setText("Multiplicateur déplacement: " + MouseControl.getCoeff());

			}
	    });
	    
	    sliderTestFluidity.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				MouseControl.setTestF(sliderTestFluidity.getValue());
				labTestFluidity.setText("Valeur test pour la methode fluidity: " + MouseControl.getTestF());
				
			}
	    });
	    
	    sliderMultiFluidity.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				MouseControl.setMultiF(sliderMultiFluidity.getValue());
				labMultiFluidity.setText("Valeur pour la methode fluidity: " + MouseControl.getMultiF());
			}
	    });
	    
	    //JPanel pan = new JPanel();
	    this.setLayout(new BoxLayout(this.getContentPane(),BoxLayout.Y_AXIS));

	    this.getContentPane().add(labPercentScreen);
	    this.getContentPane().add(sliderPercentScreen);
	    this.getContentPane().add(labTimerAff);
	    this.getContentPane().add(sliderTimerAff);
	    this.getContentPane().add(labTimerMovement);
	    this.getContentPane().add(sliderTimerMovement);
	    this.getContentPane().add(labCoeffControl);
	    this.getContentPane().add(sliderCoeffControl);
	    this.getContentPane().add(labTestFluidity);
	    this.getContentPane().add(sliderTestFluidity);
	    this.getContentPane().add(labMultiFluidity);
	    this.getContentPane().add(sliderMultiFluidity);
	    
	  }
	
}
