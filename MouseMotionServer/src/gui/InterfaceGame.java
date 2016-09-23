package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class InterfaceGame extends JFrame{
	
	private ApplicationInterface.Doc selectedObj = null;
	private JPanel panel;
	private int score=0;
	private Chrono chrono;
	private Rectangle chest;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1107785068760209899L;
	
	public InterfaceGame(){
		Rectangle virtualBounds = new Rectangle();
		GraphicsEnvironment ge = GraphicsEnvironment
		        .getLocalGraphicsEnvironment();
		    GraphicsDevice[] gsd = ge.getScreenDevices();
		  for(GraphicsDevice gd : gsd){
			 GraphicsConfiguration[] gcs = gd.getConfigurations();
			 for(GraphicsConfiguration gc :gcs){
				 virtualBounds = virtualBounds.union(gc.getBounds());
			 }
			 
		 }
		Toolkit kit = Toolkit.getDefaultToolkit();
        Image img = kit.createImage("watch_2-512.png");
		setIconImage(img);
		setBounds(virtualBounds);
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				putInChest();
				
			}
		});
		setUndecorated(true);
		initComponents();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private void initComponents(){
		panel = new JPanel(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 6513125228446990633L;

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				chrono.paint(g);
				g.setColor(Color.black);
				g.drawString("Score: " + score, 0, 20);
				g.drawString("Score: " + score, 0, getHeight()-30);
				g.drawRect(chest.x, chest.y, chest.width, chest.height);
			}
		};
		JDesktopPane dp = new TransparentDesktopPane();
		loadAndPutElements(dp);
		panel.add(dp);
		chrono= new Chrono(panel, 120, 0, 10, 12);
		chest = new Rectangle(0,this.getHeight()/3,getWidth(),this.getHeight()/3);
		this.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode() == KeyEvent.VK_ESCAPE){
					System.exit(0);
				}
				if(arg0.getKeyCode() == KeyEvent.VK_SPACE){
					chrono.demarrer();
				}
				
			}
			
		});
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(panel, BorderLayout.CENTER);
		
	}
	
	private void loadAndPutElements(JDesktopPane dp){
		
	}
	
	private void putInChest(){
		if(selectedObj !=null){
			Random rand = new Random();
			int offsetX = rand.nextInt(getWidth()-selectedObj.frame.getWidth());
			int offsetY = rand.nextInt(2*getHeight()/3-selectedObj.frame.getHeight());
			selectedObj.frame.setLocation(chest.x+offsetX, chest.y+offsetY);
		}
	}
	
	private void chooseElements(){
		
	}

}
