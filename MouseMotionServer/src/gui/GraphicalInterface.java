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
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GraphicalInterface extends JFrame{
	
	private static final long serialVersionUID = -9002852749853545258L;
	private static final String prefixPercentScreen = "Taille marge bord: ";
	private static final String prefixTimerAFF = "Temps pour changer de mode: ";
	private static final String prefixTimerMovement = "Temps entre deux d�placements (V2 et V3):  ";
	private static final String prefixDivisionCoef = "Coefficient g�rant la distance de d�placement en Border Mode (V1, V2, V4): ";
	private static final String prefixCoefControl = "Multiplicateur de d�placement (quelque soit le mode): ";
	private static final String prefixTestFuildity = "Distance minimale pour activer le sous d�coupage: ";
	private static final String prefixMultiFluidity = "Coefficient multiplicateur pour la valeur de sous d�coupage: ";
	private static final JTextArea text = new JTextArea();
	private static final String prefixListLabel = "Choix du type de Souris: ";
	private static final String suffixPercent="%";
	private static final String suffixMS="ms";
	private static final String suffixPixel= "px";
	private static final String v1="Gestion d�placement avec vitesse";
	private static final String v2="Gestion Vitesse";
	private static final String v3="Blocage avant";
	private static final String v4="Gestion d�placement mapping";
	private Engine engine;
	
	public GraphicalInterface(){
		init();
		//createAndShowGUI();
	}
	
	public GraphicalInterface(Engine e){
		this.engine=e;
		init();
		//createAndShowGUI();
	}
	
	private void init(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
	    this.setTitle("R�glage des param�tres");
	    this.setSize(1000, 1000);
	    this.setLocationRelativeTo(null);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static JTextArea getText() {
		return text;
	}
	
	public void createAndShowGUI(){
		
	    JLabel labCombo = new JLabel(prefixListLabel);
	    Font fontOptionTitle = new Font(labCombo.getFont().getFamily(), Font.BOLD, labCombo.getFont().getSize()+3);
	    labCombo.setFont(fontOptionTitle);
	    labCombo.setAlignmentX(LEFT_ALIGNMENT);
	    
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
	    combo.setFont(new Font(combo.getFont().getFamily(),combo.getFont().getStyle(),combo.getFont().getSize()+4));
	    combo.setAlignmentX(LEFT_ALIGNMENT);

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
	    spinnerDivisionCOEF.setAlignmentX(LEFT_ALIGNMENT);
	    
	    text.setEditable(false);
	    text.setFont(new Font(text.getFont().getFamily(), text.getFont().getStyle(), 20));
	    //text.setPreferredSize(new Dimension(400,60));
	    
	    //sliderPercentScreen.setPreferredSize(new Dimension(400,100));
	    sliderPercentScreen.setAlignmentX(LEFT_ALIGNMENT);
	    sliderPercentScreen.setMinimum(0);
	    sliderPercentScreen.setMaximum(100);
	    sliderPercentScreen.setValue((int)(IMouseListener.getPercentScreenSize()*100));
	    sliderPercentScreen.setPaintTicks(true);
	    sliderPercentScreen.setPaintLabels(true);
	    sliderPercentScreen.setMinorTickSpacing(10);
	    sliderPercentScreen.setMajorTickSpacing(20);
	    
	    
	    //sliderTimerAff.setPreferredSize(new Dimension(400,100));
	    sliderTimerAff.setAlignmentX(LEFT_ALIGNMENT);
	    sliderTimerAff.setMinimum(10);
	    sliderTimerAff.setMaximum(1000);
	    sliderTimerAff.setValue((int)IMouseListener.getTimerAff());
	    sliderTimerAff.setPaintTicks(true);
	    sliderTimerAff.setPaintLabels(true);
	    sliderTimerAff.setMinorTickSpacing(45);
	    sliderTimerAff.setMajorTickSpacing(90);
	    
	    
	    //sliderTimerMovement.setPreferredSize(new Dimension(400,100));
	    sliderTimerMovement.setAlignmentX(LEFT_ALIGNMENT);
	    sliderTimerMovement.setMinimum(10);
	    sliderTimerMovement.setMaximum(1000);
	    sliderTimerMovement.setValue((int)IMouseListener.getTimerMovement());
	    sliderTimerMovement.setPaintTicks(true);
	    sliderTimerMovement.setPaintLabels(true);
	    sliderTimerMovement.setMinorTickSpacing(45);
	    sliderTimerMovement.setMajorTickSpacing(90);
	    

	    //sliderDivisionCOEF.setPreferredSize(new Dimension(400,100));
	    sliderDivisionCOEF.setAlignmentX(LEFT_ALIGNMENT);
	    sliderDivisionCOEF.setMinimum(1);
	    sliderDivisionCOEF.setMaximum(100);
	    sliderDivisionCOEF.setValue((int) IMouseListener.getDIVISION_COEF());
	    sliderDivisionCOEF.setPaintTicks(true);
	    sliderDivisionCOEF.setPaintLabels(true);
	    sliderDivisionCOEF.setMinorTickSpacing(1);
	    sliderDivisionCOEF.setMajorTickSpacing(10);
	    

	    //sliderCoeffControl.setPreferredSize(new Dimension(400,100));
	    sliderCoeffControl.setAlignmentX(LEFT_ALIGNMENT);
	    sliderCoeffControl.setMinimum(1);
	    sliderCoeffControl.setMaximum(10);
	    sliderCoeffControl.setValue(MouseControl.getCoeff());
	    sliderCoeffControl.setPaintTicks(true);
	    sliderCoeffControl.setPaintLabels(true);
	    sliderCoeffControl.setMinorTickSpacing(1);
	    sliderCoeffControl.setMajorTickSpacing(1);
	    
	    
	    //sliderTestFluidity.setPreferredSize(new Dimension(400,100));
	    sliderTestFluidity.setAlignmentX(LEFT_ALIGNMENT);
	    sliderTestFluidity.setMinimum(1);
	    sliderTestFluidity.setMaximum(30);
	    sliderTestFluidity.setValue(MouseControl.getTestF());
	    sliderTestFluidity.setPaintTicks(true);
	    sliderTestFluidity.setPaintLabels(true);
	    sliderTestFluidity.setMinorTickSpacing(1);
	    sliderTestFluidity.setMajorTickSpacing(1);
	    
	    
	    //sliderMultiFluidity.setPreferredSize(new Dimension(400,100));
	    sliderMultiFluidity.setAlignmentX(LEFT_ALIGNMENT);
	    sliderMultiFluidity.setMinimum(1);
	    sliderMultiFluidity.setMaximum(10);
	    sliderMultiFluidity.setValue(MouseControl.getMultiF());
	    sliderMultiFluidity.setPaintTicks(true);
	    sliderMultiFluidity.setPaintLabels(true);
	    sliderMultiFluidity.setMinorTickSpacing(1);
	    sliderMultiFluidity.setMajorTickSpacing(1);
	    
	    
	    final JLabel labPercentScreen = new JLabel(prefixPercentScreen + sliderPercentScreen.getValue() + suffixPercent);
	    labPercentScreen.setFont(fontOptionTitle);
	    labPercentScreen.setAlignmentX(LEFT_ALIGNMENT);
	    //labPercentScreen.setPreferredSize(new Dimension(400,50));
	    final JLabel labTimerAff = new JLabel(prefixTimerAFF + sliderTimerAff.getValue()+suffixMS);
	    labTimerAff.setFont(fontOptionTitle);
	    labTimerAff.setAlignmentX(LEFT_ALIGNMENT);
	    //labTimerAff.setPreferredSize(new Dimension(400, 50));
	    final JLabel labTimerMovement = new JLabel(prefixTimerMovement + sliderTimerMovement.getValue()+suffixMS);
	    labTimerMovement.setFont(fontOptionTitle);
	    labTimerMovement.setAlignmentX(LEFT_ALIGNMENT);
	    //labTimerMovement.setPreferredSize(new Dimension(400, 50));
	    final JLabel labCoeffControl = new JLabel(prefixCoefControl + sliderCoeffControl.getValue());
	    labCoeffControl.setFont(fontOptionTitle);
	    labCoeffControl.setAlignmentX(LEFT_ALIGNMENT);
	    //labCoeffControl.setPreferredSize(new Dimension(400, 50));
	    final JLabel labDivCoefControl = new JLabel(prefixDivisionCoef + spinnerDivisionCOEF.getValue());
	    labDivCoefControl.setFont(fontOptionTitle);
	    labDivCoefControl.setAlignmentX(LEFT_ALIGNMENT);
	    //labDivCoefControl.setPreferredSize(new Dimension(400, 50));
	    final JLabel labTestFluidity = new JLabel(prefixTestFuildity + sliderTestFluidity.getValue()+suffixPixel);
	    labTestFluidity.setFont(fontOptionTitle);
	    //labTestFluidity.setPreferredSize(new Dimension(400, 50));
	    final JLabel labMultiFluidity = new JLabel(prefixMultiFluidity + sliderMultiFluidity.getValue());
	    labMultiFluidity.setFont(fontOptionTitle);
	    labMultiFluidity.setAlignmentX(LEFT_ALIGNMENT);
	    //labMultiFluidity.setPreferredSize(new Dimension(400, 50));
	    
	    sliderPercentScreen.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				IMouseListener.setPercentScreenSize(sliderPercentScreen.getValue()/100.0f);
				labPercentScreen.setText(prefixPercentScreen + (IMouseListener.getPercentScreenSize()*100) +suffixPercent);
				for(Entry<SelectionKey, Channel> entry : ((OwnEngine) engine).getDelivers()){
	        		((DeliverCallbackTest) ((ChannelTest)entry.getValue()).getCallback()).resetMarge();
	        	}
			}
	    });
	    
	    sliderTimerAff.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				IMouseListener.setTimerAff((long)sliderTimerAff.getValue());
				labTimerAff.setText(prefixTimerAFF + IMouseListener.getTimerAff()+suffixMS);
			}
	    });
	    
	    sliderTimerMovement.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				IMouseListener.setTimerMovement(sliderTimerMovement.getValue());
				labTimerMovement.setText(prefixTimerMovement + IMouseListener.getTimerMovement()+suffixMS);
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
				labTestFluidity.setText(prefixTestFuildity + MouseControl.getTestF()+suffixPixel);
				
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
	    JPanel southEastList = new JPanel(new BorderLayout());
	    JPanel southEastCOEF = new JPanel(new BorderLayout());
	    JPanel boxpan = new JPanel();
	    
	    southEastList.add(combo,BorderLayout.WEST);
	    southEastList.setAlignmentX(LEFT_ALIGNMENT);
	    
	    southEastCOEF.add(spinnerDivisionCOEF,BorderLayout.WEST);
	    southEastCOEF.setAlignmentX(LEFT_ALIGNMENT);
	    
	    boxpan.setLayout(new BoxLayout(boxpan,BoxLayout.Y_AXIS));
	    //boxpan.add(text);
	    boxpan.add(labCombo);
	    //boxpan.add(combo);
	    boxpan.add(southEastList);
	    boxpan.add(new JSeparator(SwingConstants.HORIZONTAL));
	    boxpan.add(labPercentScreen);
	    boxpan.add(sliderPercentScreen);
	    boxpan.add(new JSeparator(SwingConstants.HORIZONTAL));
	    boxpan.add(labTimerAff);
	    boxpan.add(sliderTimerAff);
	    boxpan.add(new JSeparator(SwingConstants.HORIZONTAL));
	    boxpan.add(labTimerMovement);
	    boxpan.add(sliderTimerMovement);
	    boxpan.add(new JSeparator(SwingConstants.HORIZONTAL));
	    boxpan.add(labDivCoefControl);
	    boxpan.add(southEastCOEF);
	    boxpan.add(new JSeparator(SwingConstants.HORIZONTAL));
	    boxpan.add(labCoeffControl);
	    boxpan.add(sliderCoeffControl);
	    boxpan.add(new JSeparator(SwingConstants.HORIZONTAL));
	    boxpan.add(labTestFluidity);
	    boxpan.add(sliderTestFluidity);
	    boxpan.add(new JSeparator(SwingConstants.HORIZONTAL));
	    boxpan.add(labMultiFluidity);
	    boxpan.add(sliderMultiFluidity);
	    
	    JScrollPane scroll = new JScrollPane (text, 
	    		   JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    
	    pan.setLayout(new BorderLayout());
	    pan.add(boxpan, BorderLayout.NORTH);
	    pan.add(scroll,BorderLayout.CENTER);
	    
	    this.add(pan);
	    this.setVisible(true);
	}
	
	public static void showOnScreen( int screen, JFrame frame )
	{
	    GraphicsEnvironment ge = GraphicsEnvironment
	        .getLocalGraphicsEnvironment();
	    GraphicsDevice[] gs = ge.getScreenDevices();
	    if( screen > -1 && screen < gs.length )
	    {
	        gs[screen].setFullScreenWindow( frame );
	    }
	    else if( gs.length > 0 )
	    {
	        gs[0].setFullScreenWindow( frame );
	    }
	    else
	    {
	        throw new RuntimeException( "No Screens Found" );
	    }
	}
	
	public void createAndShowOrderedPictureFrame(){
		this.setVisible(true);
	}
}


