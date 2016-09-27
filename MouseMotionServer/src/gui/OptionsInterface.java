package gui;

import mouse.control.*;
import network.Impl.ChannelTest;
import network.Impl.DeliverCallbackTest;
import network.Impl.OwnEngine;
import network.Interface.Channel;
import network.Interface.Engine;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.channels.SelectionKey;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
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

public class OptionsInterface extends JFrame{
	
	private static final long serialVersionUID = -9002852749853545258L;
	private static final String prefixPercentScreen = "Taille marge bord: ";
	private static final String prefixTimerAFF = "Temps pour changer de mode: ";
	private static final String prefixTimerMovement = "Temps entre deux déplacements (V2, V3, V5):  ";
	private static final String prefixDivisionCoef = "Coefficient diviseur de l'angle gérant la distance de déplacement en Border Mode (V1, V2, V4, V5): ";
	private static final String prefixPreview = "Activation prévisualisation en Border Mode";
	private static final String prefixDRAWLINE = "Ligne";
	private static final String prefixDRAWPATH = "Chemin parcouru";
	private static final String prefixDRAWCONE = "Cone";
	private static final String prefixDRAWPOINT = "Dernier Point";
	private static final String prefixDRAWARROW = "Flèche";
	private static final String prefixDRAWREGLINE = "Droite de regression";
	private static final JTextArea text = new JTextArea();
	private static final String prefixListLabel = "Choix du type de Souris: ";
	private static final String suffixPercent="%";
	private static final String suffixMS="ms";
	//private static final String suffixPixel= "px";
	private static final String v1="Accroissement Quadratique";
	private static final String v2="Vitesse = f(angle)";
	private static final String v3="Blocage avant";
	private static final String v4="Mapping direct delta_angle -> delta_déplacement";
	private static final String v5 ="Blocage avant avec changement de direction";
	private Engine engine;
	private boolean killProgramOnClose=false;
	
	public OptionsInterface(){
		init();
		//createAndShowGUI();
	}
	
	public OptionsInterface(Engine e){
		this(e,false);
		//createAndShowGUI();
	}
	
	public OptionsInterface(Engine e, boolean killProgramOnClose){
		this.killProgramOnClose=killProgramOnClose;
		this.engine=e;
		init();
	}
	
	private void init(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
	    this.setTitle("Réglage des paramètres");
	    this.setSize(1000, 1000);
	    this.setLocationRelativeTo(null);
	    Toolkit kit = Toolkit.getDefaultToolkit();
        Image img = kit.createImage("Evolution Cursor.png");
		this.setIconImage(img);
	    if(killProgramOnClose){
	    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    }
	}

	public static JTextArea getText() {
		return text;
	}
	
	public void createAndShowGUI(){
		
	    JLabel labCombo = new JLabel(prefixListLabel);
	    Font fontOptionTitle = new Font(labCombo.getFont().getFamily(), Font.BOLD, labCombo.getFont().getSize()+3);
	    labCombo.setFont(fontOptionTitle);
	    labCombo.setAlignmentX(LEFT_ALIGNMENT);
	    
	    
	    final Map<String,String> mouseChoice = new LinkedHashMap<String,String>();
	    //Add your listener here
	    mouseChoice.put(v1, "mouse.control.MouseListenerV1");//v1
	    mouseChoice.put(v2, "mouse.control.MouseListenerV2");//V2
	    mouseChoice.put(v3, "mouse.control.MouseListenerV3");//V3
	    mouseChoice.put(v4, "mouse.control.MouseListenerV4");//V4
	    mouseChoice.put(v5, "mouse.control.MouseListenerV5");//V5
	    
	    final JComboBox<?> combo = new JComboBox<>(mouseChoice.keySet().toArray());
	   
	    combo.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				DeliverCallbackTest.defaultListener=mouseChoice.get(combo.getSelectedItem().toString());
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
	    final JCheckBox boxPreview = new JCheckBox(prefixPreview);
	    final JCheckBox boxPreviewPoint = new JCheckBox(prefixDRAWPOINT);
	    final JCheckBox boxPreviewLine = new JCheckBox(prefixDRAWLINE);
	    final JCheckBox boxPreviewPath = new JCheckBox(prefixDRAWPATH);
	    final JCheckBox boxPreviewArrow = new JCheckBox(prefixDRAWARROW);
	    final JCheckBox boxPreviewCone = new JCheckBox(prefixDRAWCONE);
	    final JCheckBox boxPreviewRegLine = new JCheckBox(prefixDRAWREGLINE);
	    
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
	    
	    
	    JPanel boxPanel = new JPanel(new FlowLayout());
	    
	    boxPreview.setSelected(MouseControl.isEnablePreview());
	    
	    boxPreviewArrow.setSelected(TransparentWindow.isDRAW_ARROW());
	    boxPreviewCone.setSelected(TransparentWindow.isDRAW_CONE());
	    boxPreviewLine.setSelected(TransparentWindow.isDRAW_LINE());
	    boxPreviewPath.setSelected(TransparentWindow.isDRAW_PATH());
	    boxPreviewPoint.setSelected(TransparentWindow.isDRAW_FINAL_POINT());
	    boxPreviewRegLine.setSelected(TransparentWindow.isDRAW_REGRESSION_LINE());
	    
	    boxPanel.setAlignmentX(LEFT_ALIGNMENT);
	    boxPanel.add(boxPreview);
	    boxPanel.add(boxPreviewPoint);
	    boxPanel.add(boxPreviewArrow);
	    boxPanel.add(boxPreviewCone);
	    boxPanel.add(boxPreviewLine);
	    boxPanel.add(boxPreviewPath);
	    boxPanel.add(boxPreviewRegLine);
	    
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
	    final JLabel labDivCoefControl = new JLabel(prefixDivisionCoef + spinnerDivisionCOEF.getValue());
	    labDivCoefControl.setFont(fontOptionTitle);
	    labDivCoefControl.setAlignmentX(LEFT_ALIGNMENT);
	    final JLabel labEnablePreview = new JLabel(prefixPreview);
	    labEnablePreview.setFont(fontOptionTitle);
	    labEnablePreview.setAlignmentX(LEFT_ALIGNMENT);
	    
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
	    
	    
	    boxPreview.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				MouseControl.setEnablePreview(boxPreview.isSelected());
			}
		});
	    
	    boxPreviewArrow.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				TransparentWindow.setDRAW_ARROW(boxPreviewArrow.isSelected());
				
			}
		});
	    
	    boxPreviewCone.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				TransparentWindow.setDRAW_CONE(boxPreviewCone.isSelected());
				
			}
		});
	    
	    boxPreviewLine.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				TransparentWindow.setDRAW_LINE(boxPreviewLine.isSelected());
				
			}
		});
	    
	    boxPreviewPath.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				TransparentWindow.setDRAW_PATH(boxPreviewPath.isSelected());
				
			}
		});
	    
	    boxPreviewPoint.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				TransparentWindow.setDRAW_FINAL_POINT(boxPreviewPoint.isSelected());
				
			}
		});
	    
	    boxPreviewRegLine.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				TransparentWindow.setDRAW_REGRESSION_LINE(boxPreviewRegLine.isSelected());
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
	    boxpan.add(labEnablePreview);
	    boxpan.add(boxPanel);
	    
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
}


