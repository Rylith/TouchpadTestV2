package gui;

import mouse.control.*;
import network.Impl.ChannelTest;
import network.Impl.DeliverCallbackTest;
import network.Impl.OwnEngine;
import network.Interface.Channel;
import network.Interface.Engine;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.channels.SelectionKey;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GraphicalInterface extends JFrame{
	
	private static final long serialVersionUID = -9002852749853545258L;
	private static final String prefixPercentScreen = "Taille marge bord: (En %) ";
	private static final String prefixTimerAFF = "Temps pour changer de mode: (En ms) ";
	private static final String prefixTimerMovement = "Temps entre deux déplacements (En ms, V2 et V3):  ";
	private static final String prefixDivisionCoef = "Coefficient gérant la distance de déplacement en Border Mode (V1, V2, V4): ";
	private static final String prefixCoefControl = "Multiplicateur de déplacement (quelque soit le mode): ";
	private static final String prefixTestFuildity = "Distance minimale pour activer le sous découpage: ";
	private static final String prefixMultiFluidity = "Coefficient multiplicateur pour la valeur de sous découpage: ";
	private static final JTextArea text = new JTextArea();
	private Engine engine;
	
	public GraphicalInterface(){
	}
	
	public GraphicalInterface(Engine engine){
		this.engine=engine;
	}

	public static JTextArea getText() {
		return text;
	}
	
	public void createAndShowGUI(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
	    this.setTitle("Réglage des paramètres");
	    this.setSize(1000, 1000);
	    this.setLocationRelativeTo(null);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    
	    String v1 = "Gestion déplacement avec vitesse";
	    String v2 = "Gestion Vitesse";
	    String v3 = "Blocage avant";
	    String v4 = "Gestion déplacement mapping";
	    
	    JLabel labCombo = new JLabel("Choix du type de Souris:");
	    final JComboBox<String> combo = new JComboBox<String>();
	    final Map<String,String> mouseChoice = new HashMap<String,String>();
	    mouseChoice.put(v1, "mouse.control.MouseListenerV1");//v1
	    mouseChoice.put(v2, "mouse.control.MouseListenerV2");//V2
	    mouseChoice.put(v3, "mouse.control.MouseListenerV3");
	    mouseChoice.put(v4, "mouse.control.MouseListenerV4");//V4
	    combo.addItem(v1);
	    combo.addItem(v2);
	    combo.addItem(v3);
	    combo.addItem(v4);
	    combo.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
	        	for(Entry<SelectionKey, Channel> entry : ((OwnEngine) engine).getDelivers()){
	        		((DeliverCallbackTest) ((ChannelTest)entry.getValue()).getCallback()).setListener(mouseChoice.get(combo.getSelectedItem().toString()));
	        	}
	        }
	    });
	    

	    final JSlider sliderPercentScreen = new JSlider();  
	    final JSlider sliderTimerAff = new JSlider();
	    final JSlider sliderTimerMovement = new JSlider();
	    final JSlider sliderDivisionCOEF = new JSlider();
	    final JSlider sliderCoeffControl = new JSlider();
	    final JSlider sliderTestFluidity = new JSlider();
	    final JSlider sliderMultiFluidity = new JSlider();
	    SpinnerNumberModel modelDivisionCOEF = new SpinnerNumberModel(IMouseListener.getDIVISION_COEF(),0.1,100,0.1);
	    final JSpinner spinnerDivisionCOEF = new JSpinner(modelDivisionCOEF);
	    NumberEditor editor = new JSpinner.NumberEditor(spinnerDivisionCOEF);
	    spinnerDivisionCOEF.setEditor(editor);
	    
	    text.setEditable(false);
	    text.setFont(new Font(text.getFont().getFamily(), text.getFont().getStyle(), 20));
	    //text.setPreferredSize(new Dimension(400,60));
	    
	    //sliderPercentScreen.setPreferredSize(new Dimension(400,100));
	    sliderPercentScreen.setMinimum(0);
	    sliderPercentScreen.setMaximum(100);
	    sliderPercentScreen.setValue((int)(IMouseListener.getPercentScreenSize()*100));
	    sliderPercentScreen.setPaintTicks(true);
	    sliderPercentScreen.setPaintLabels(true);
	    sliderPercentScreen.setMinorTickSpacing(10);
	    sliderPercentScreen.setMajorTickSpacing(20);
	    
	    
	    //sliderTimerAff.setPreferredSize(new Dimension(400,100));
	    sliderTimerAff.setMinimum(10);
	    sliderTimerAff.setMaximum(1000);
	    sliderTimerAff.setValue((int)IMouseListener.getTimerAff());
	    sliderTimerAff.setPaintTicks(true);
	    sliderTimerAff.setPaintLabels(true);
	    sliderTimerAff.setMinorTickSpacing(45);
	    sliderTimerAff.setMajorTickSpacing(90);
	    
	    
	    //sliderTimerMovement.setPreferredSize(new Dimension(400,100));
	    sliderTimerMovement.setMinimum(10);
	    sliderTimerMovement.setMaximum(1000);
	    sliderTimerMovement.setValue((int)IMouseListener.getTimerMovement());
	    sliderTimerMovement.setPaintTicks(true);
	    sliderTimerMovement.setPaintLabels(true);
	    sliderTimerMovement.setMinorTickSpacing(45);
	    sliderTimerMovement.setMajorTickSpacing(90);
	    

	    //sliderDivisionCOEF.setPreferredSize(new Dimension(400,100));
	    sliderDivisionCOEF.setMinimum(1);
	    sliderDivisionCOEF.setMaximum(100);
	    sliderDivisionCOEF.setValue((int) IMouseListener.getDIVISION_COEF());
	    sliderDivisionCOEF.setPaintTicks(true);
	    sliderDivisionCOEF.setPaintLabels(true);
	    sliderDivisionCOEF.setMinorTickSpacing(1);
	    sliderDivisionCOEF.setMajorTickSpacing(10);
	    

	    //sliderCoeffControl.setPreferredSize(new Dimension(400,100));
	    sliderCoeffControl.setMinimum(1);
	    sliderCoeffControl.setMaximum(10);
	    sliderCoeffControl.setValue(MouseControl.getCoeff());
	    sliderCoeffControl.setPaintTicks(true);
	    sliderCoeffControl.setPaintLabels(true);
	    sliderCoeffControl.setMinorTickSpacing(1);
	    sliderCoeffControl.setMajorTickSpacing(1);
	    
	    
	    //sliderTestFluidity.setPreferredSize(new Dimension(400,100));

	    sliderTestFluidity.setMinimum(1);
	    sliderTestFluidity.setMaximum(10);
	    sliderTestFluidity.setValue(MouseControl.getTestF());
	    sliderTestFluidity.setPaintTicks(true);
	    sliderTestFluidity.setPaintLabels(true);
	    sliderTestFluidity.setMinorTickSpacing(1);
	    sliderTestFluidity.setMajorTickSpacing(1);
	    
	    
	    //sliderMultiFluidity.setPreferredSize(new Dimension(400,100));
	    sliderMultiFluidity.setMinimum(1);
	    sliderMultiFluidity.setMaximum(10);
	    sliderMultiFluidity.setValue(MouseControl.getMultiF());
	    sliderMultiFluidity.setPaintTicks(true);
	    sliderMultiFluidity.setPaintLabels(true);
	    sliderMultiFluidity.setMinorTickSpacing(1);
	    sliderMultiFluidity.setMajorTickSpacing(1);
	    
	    
	    final JLabel labPercentScreen = new JLabel(prefixPercentScreen + sliderPercentScreen.getValue());
	    //labPercentScreen.setPreferredSize(new Dimension(400,50));
	    final JLabel labTimerAff = new JLabel(prefixTimerAFF + sliderTimerAff.getValue());
	    //labTimerAff.setPreferredSize(new Dimension(400, 50));
	    final JLabel labTimerMovement = new JLabel(prefixTimerMovement + sliderTimerMovement.getValue());
	    //labTimerMovement.setPreferredSize(new Dimension(400, 50));
	    final JLabel labCoeffControl = new JLabel(prefixCoefControl + sliderCoeffControl.getValue());
	    //labCoeffControl.setPreferredSize(new Dimension(400, 50));
	    final JLabel labDivCoefControl = new JLabel(prefixDivisionCoef + spinnerDivisionCOEF.getValue());
	    //labDivCoefControl.setPreferredSize(new Dimension(400, 50));
	    final JLabel labTestFluidity = new JLabel(prefixTestFuildity + sliderTestFluidity.getValue());
	    //labTestFluidity.setPreferredSize(new Dimension(400, 50));
	    final JLabel labMultiFluidity = new JLabel(prefixMultiFluidity + sliderMultiFluidity.getValue());
	    //labMultiFluidity.setPreferredSize(new Dimension(400, 50));
	    
	    sliderPercentScreen.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				IMouseListener.setPercentScreenSize(sliderPercentScreen.getValue()/100.0f);
				labPercentScreen.setText(prefixPercentScreen + (IMouseListener.getPercentScreenSize()*100));
				for(Entry<SelectionKey, Channel> entry : ((OwnEngine) engine).getDelivers()){
	        		((DeliverCallbackTest) ((ChannelTest)entry.getValue()).getCallback()).resetMarge();
	        	}
			}
	    });
	    
	    sliderTimerAff.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				IMouseListener.setTimerAff((long)sliderTimerAff.getValue());
				labTimerAff.setText(prefixTimerAFF + IMouseListener.getTimerAff());
			}
	    });
	    
	    sliderTimerMovement.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				IMouseListener.setTimerMovement(sliderTimerMovement.getValue());
				labTimerMovement.setText(prefixTimerMovement + IMouseListener.getTimerMovement());
			}
	    });
	    
	    sliderDivisionCOEF.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				IMouseListener.setDIVISION_COEF(sliderDivisionCOEF.getValue()/10.0f);
				labDivCoefControl.setText(prefixDivisionCoef + IMouseListener.getDIVISION_COEF());
			}
	    });
	    
	    spinnerDivisionCOEF.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				IMouseListener.setDIVISION_COEF(((Number) spinnerDivisionCOEF.getValue()).floatValue());
				labDivCoefControl.setText(prefixDivisionCoef + IMouseListener.getDIVISION_COEF());
			}
		});
	    
	    sliderCoeffControl.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				MouseControl.setCoeff(sliderCoeffControl.getValue());
				labCoeffControl.setText(prefixCoefControl + MouseControl.getCoeff());

			}
	    });
	    
	    sliderTestFluidity.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				MouseControl.setTestF(sliderTestFluidity.getValue());
				labTestFluidity.setText(prefixTestFuildity + MouseControl.getTestF());
				
			}
	    });
	    
	    sliderMultiFluidity.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				MouseControl.setMultiF(sliderMultiFluidity.getValue());
				labMultiFluidity.setText(prefixMultiFluidity + MouseControl.getMultiF());
			}
	    });
	    
	    JPanel pan = new JPanel();
	    JPanel boxpan = new JPanel();
	    
	    boxpan.setLayout(new BoxLayout(boxpan,BoxLayout.Y_AXIS));
	    //boxpan.add(text);
	    boxpan.add(labCombo);
	    boxpan.add(combo);
	    boxpan.add(labPercentScreen);
	    boxpan.add(sliderPercentScreen);
	    boxpan.add(labTimerAff);
	    boxpan.add(sliderTimerAff);
	    boxpan.add(labTimerMovement);
	    boxpan.add(sliderTimerMovement);
	    boxpan.add(labDivCoefControl);
	    boxpan.add(spinnerDivisionCOEF);
	    boxpan.add(labCoeffControl);
	    boxpan.add(sliderCoeffControl);
	    boxpan.add(labTestFluidity);
	    boxpan.add(sliderTestFluidity);
	    boxpan.add(labMultiFluidity);
	    boxpan.add(sliderMultiFluidity);
	    
	    JScrollPane scroll = new JScrollPane (text, 
	    		   JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    
	    pan.setLayout(new BorderLayout());
	    pan.add(boxpan, BorderLayout.SOUTH);
	    pan.add(scroll,BorderLayout.CENTER);
	    
	    this.add(pan);
	    this.setVisible(true);
	}
	
}
