package gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class InterfaceGame extends JFrame{
	
	private Doc selectedObj = null;
	private static JDesktopPane dp;
	private int score=0;
	private Chrono chrono;
	private Rectangle chest;
	private List<Doc> topDocs = new ArrayList<>();
	private List<Doc> botDocs = new ArrayList<>();
	private List<Doc> allPics = new ArrayList<>();
	private boolean up=true;
	private final Random rand = new Random();
	private final static boolean DEMO = false;
	private final static int OFFSETY=30;
	private static Font scoreFont;
	private static final float COEF_FONT_SIZE = 0.03f;
	private static final String RESOURCE_DIR = "resources/game/";
	private Image background;
	private Image backgroundChest;
	private static enum State{RUN,PAUSE,STOP,BEGIN};
	private State state = State.BEGIN;
	/**
	 * 
	 */
	private static final long serialVersionUID = -1107785068760209899L;
	
	public class Doc extends InternalFrameAdapter implements ActionListener {
        String name;
        JInternalFrame frame;
        TransferHandler th;
		ImagePanel im = null;
		BufferedImage image = null;
        
        public Doc(File file) {
        	this.name = file.getName();
            try {
                init(file.toURI().toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
         
        public Doc(String name) {
            this.name = name;
            init(getClass().getResource(name));
        }
         
        private void init(URL url) {
            frame = new JInternalFrame(name);
            frame.addInternalFrameListener(this);
            //listModel.add(listModel.size(), this);
            int x_size;
            int y_size;
            
            x_size = 200;
            y_size = 300;
            try {
                //BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                
                InputStream is = new FileInputStream(URLDecoder.decode(url.getFile(), "UTF-8"));
                //System.out.println(URLDecoder.decode(url.getFile(), "UTF-8"));
                ImageInputStream iis = ImageIO.createImageInputStream(is);
                image = ImageIO.read(iis);
                image = Scalr.resize(image, x_size, y_size,(BufferedImageOp)null);
                im = new ImagePanel(image);
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            th = im.getTransferHandler();
            im.setOpaque(false);
            BasicInternalFrameUI bi = (BasicInternalFrameUI)frame.getUI();
            DragListener drag = new DragListener(this);
            frame.addMouseMotionListener(drag);
            frame.addMouseListener(drag);
            frame.getContentPane().add(im);
            dp.add(frame);
            if (DEMO) {
                frame.setSize(300, 200);
            } else {
            	setInternalFrameSize(image, frame);
            }
            frame.setBorder(null);
            frame.setBackground(new Color(0,true));
            bi.setNorthPane(null);
            frame.show();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    select();
                }
            });
        }
  
        public void internalFrameClosing(InternalFrameEvent event) {
        }
 
        public void internalFrameOpened(InternalFrameEvent event) {
        }
 
        public void internalFrameActivated(InternalFrameEvent event) {
        }
 
        public String toString() {
            return name;
        }
         
        public void select() {
            try {
                frame.toFront();
                frame.setSelected(true);
            } catch (java.beans.PropertyVetoException e) {
            	
            }
        }
         
        public void actionPerformed(java.awt.event.ActionEvent ae) {
        }         
    }
 
	private class MyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
            	if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
    				System.exit(0);
    			}else if(e.getKeyCode() == KeyEvent.VK_SPACE){
    				if(state == State.STOP){
    					reset();
    					chooseElements();
    					chrono.demarrer();
    				}else if(state == State.BEGIN){
    					chooseElements();
    					chrono.demarrer();
    				}
    			}else if(e.getKeyCode() == KeyEvent.VK_R){
    				reset();
    			}else if(e.getKeyCode() == KeyEvent.VK_P){
    				pause();
    			}
            }
            return false;
        }
    }
	
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
        background = kit.createImage(RESOURCE_DIR+"background/room-bedroom-background-cartoon-mickey-mouse-wallpaper-wallpaper-41tgHg-clipart.jpg");
        background=background.getScaledInstance(virtualBounds.width, virtualBounds.height, Image.SCALE_DEFAULT);
		setIconImage(img);
		setBounds(virtualBounds);
		setUndecorated(true);
		initComponents();
		new PictureSeletecEvent().addPictureListener(new PictureSelectListener() {
			
			@Override
			public void pictureSelected(Doc doc) {
				if(putInChest(doc)){
					chooseElements();
				}
				
			}
		});
		new ChronoEvent().addChronoEventListener(new ChronoEventListener() {
			
			@Override
			public void stop(Chrono chrono) {
				state = State.STOP;
			}
			
			@Override
			public void start(Chrono chrono) {
				state = State.RUN;
			}
		});
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public void setInternalFrameSize(Image image, final JInternalFrame frame) {
			ImageObserver observer = new ImageObserver() {
			
			@Override
			public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
				frame.setSize(width,height);
				int somme = ImageObserver.WIDTH | ImageObserver.HEIGHT;
				return !((infoflags & somme) == somme) ;
			}
		};
        frame.setSize(image.getWidth(observer), image.getHeight(observer));
		
	}

	private void initComponents(){
		try {
			scoreFont = Font.createFont(Font.TRUETYPE_FONT, new File(RESOURCE_DIR+"font/neuropol-x-free.regular.ttf"));
			scoreFont = scoreFont.deriveFont(Font.PLAIN,1080*COEF_FONT_SIZE);
	        GraphicsEnvironment ge =
	            GraphicsEnvironment.getLocalGraphicsEnvironment();
		        ge.registerFont(scoreFont);
		} catch (FontFormatException | IOException e) {	
			e.printStackTrace();
		}
		dp = new JDesktopPane(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 6513125228446990633L;
			
			@Override
			public void paint(Graphics g) {
				
				g.drawImage(background, 0, 0, null);
				if(backgroundChest != null ){
					g.drawImage(backgroundChest, chest.x,chest.y,chest.width,chest.height, null);
				}
				if(state == State.RUN || state == State.STOP || state == State.BEGIN){
					super.paint(g);
				}
				chrono.paint(g);
				g.setColor(Color.red);
				Font temp = g.getFont();
				g.setFont(scoreFont);
				g.drawString("Score: " + score, 0, 65);
				g.drawString("Score: " + score, 0, getHeight()-OFFSETY+15);
				g.setFont(temp);
				g.setColor(Color.black);
				g.drawRect(chest.x, chest.y, chest.width, chest.height);
				if(state == State.RUN || state == State.STOP || state == State.BEGIN){
					if(selectedObj != null){
						Graphics2D g2 = (Graphics2D) g;
						BasicStroke line = new BasicStroke(3.0f);
						g2.setStroke(line);
						g2.drawRect(getWidth()-selectedObj.frame.getWidth()-2, getY(), selectedObj.frame.getWidth()+1,selectedObj.frame.getHeight()+1);
						g.drawImage(selectedObj.image, getWidth()-selectedObj.frame.getWidth()-1, getY()+1, selectedObj.frame.getWidth(),selectedObj.frame.getHeight(), null);
						
						g2.drawRect(getWidth()-selectedObj.frame.getWidth()-2, chest.y+chest.height+1, selectedObj.frame.getWidth()+1,selectedObj.frame.getHeight()+1);
						g.drawImage(selectedObj.image, getWidth()-selectedObj.frame.getWidth()-1, chest.y+chest.height+2, selectedObj.frame.getWidth(),selectedObj.frame.getHeight(), null);
					}
				}
			}
		};
		dp.setOpaque(false);
		chest = new Rectangle(0,this.getHeight()/3,getWidth(),this.getHeight()/3);
		try {
			backgroundChest = ImageIO.read(new File(RESOURCE_DIR+"background/background_chest.jpg")).getScaledInstance(chest.width, chest.height, Image.SCALE_DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		loadAndPutElements();
		chrono= new Chrono(dp, 120, 0, 10, OFFSETY);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(dp, BorderLayout.CENTER);
		
	}
	
	private void loadAndPutElements(){
		File folder = new File("resources/game/img");
		File[] listOfFiles = folder.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg") || name.endsWith(".PNG");
			}
		});
			int length = listOfFiles.length;
			int cpt = rand.nextInt(length);
			boolean upperLeftContained;
    		boolean bottomRightContained;
    		
		    for(int i = 0; i<length ;i++){
		    	while(listOfFiles[cpt] == null){
		    		cpt = rand.nextInt(length);
		    	}
		    	if(up){
		    		Doc pict = new Doc(listOfFiles[cpt]);
		    		allPics.add(pict);
		    		int x = rand.nextInt(chest.width-pict.frame.getWidth());
		    		int y = rand.nextInt(chest.y-pict.frame.getHeight());
		    		int bottomRightX = x+pict.frame.getWidth();
		    		int bottomRightY = y+pict.frame.getHeight();
		    		boolean contained=true;
		    		while(contained){
		    			contained=false;
		    			x = rand.nextInt(chest.width-pict.frame.getWidth());
			    		y = rand.nextInt(chest.y-pict.frame.getHeight());
			    		bottomRightX = x+pict.frame.getWidth();
			    		bottomRightY = y+pict.frame.getHeight();
		    			for(Doc doc : topDocs){
		    				upperLeftContained = x>=doc.frame.getLocation().x && x<=doc.frame.getLocation().x+doc.frame.getWidth() && y>=doc.frame.getLocation().y && y<=(doc.frame.getLocation().y+doc.frame.getHeight()) || (doc.frame.getLocation().x >= x && doc.frame.getLocation().x<=bottomRightX && doc.frame.getLocation().y >= y && doc.frame.getLocation().y<=bottomRightY );
		    				bottomRightContained = bottomRightX>=doc.frame.getLocation().x && bottomRightX<=doc.frame.getLocation().x+doc.frame.getWidth() && bottomRightY>=doc.frame.getLocation().y && bottomRightY<=(doc.frame.getLocation().y+doc.frame.getHeight()) || (doc.frame.getLocation().x+doc.frame.getWidth()>=x && (doc.frame.getLocation().x+doc.frame.getWidth())<=bottomRightX && doc.frame.getLocation().y + doc.frame.getHeight() >=y &&  doc.frame.getLocation().y + doc.frame.getHeight()<=bottomRightY );
		    				if(upperLeftContained || bottomRightContained){
		    					contained=true;
		    				}
		    			}
		    			//System.out.println("Picture : " + pict + " at x, y, xb, yb : "+ x+", "+y+", "+bottomRightX+", "+bottomRightY);
		    			//System.out.println(contained);
		    			
		    		}
		    		topDocs.add(pict);
		    		pict.frame.setLocation(x,y);
		    		up=false;
		    	}else{
		    		Doc pict = new Doc(listOfFiles[cpt]);
		    		allPics.add(pict);
		    		int x = rand.nextInt(chest.width-pict.frame.getWidth());
		    		int y = chest.y+chest.height+rand.nextInt(getHeight()-(chest.height+chest.y)-pict.frame.getHeight());
		    		int bottomRightX = x+pict.frame.getWidth();
		    		int bottomRightY = y+pict.frame.getHeight();
		    		boolean contained=true;
		    		while(contained){
		    			contained=false;
		    			x = rand.nextInt(chest.width-pict.frame.getWidth());
			    		y = chest.y+chest.height+rand.nextInt(getHeight()-(chest.height+chest.y)-pict.frame.getHeight());
			    		bottomRightX = x+pict.frame.getWidth();
			    		bottomRightY = y+pict.frame.getHeight();
		    			for(Doc doc : botDocs){
		    				upperLeftContained = x>=doc.frame.getLocation().x && x<=doc.frame.getLocation().x+doc.frame.getWidth() && y>=doc.frame.getLocation().y && y<=(doc.frame.getLocation().y+doc.frame.getHeight()) || (doc.frame.getLocation().x>=x && doc.frame.getLocation().x<=bottomRightX && doc.frame.getLocation().y >= y && doc.frame.getLocation().y<=bottomRightY );
		    				bottomRightContained = bottomRightX>=doc.frame.getLocation().x && bottomRightX<=doc.frame.getLocation().x+doc.frame.getWidth() && bottomRightY>=doc.frame.getLocation().y && bottomRightY<=(doc.frame.getLocation().y+doc.frame.getHeight()) || (doc.frame.getLocation().x+doc.frame.getWidth()>=x && (doc.frame.getLocation().x+doc.frame.getWidth())<=bottomRightX && doc.frame.getLocation().y + doc.frame.getHeight() >=y &&  doc.frame.getLocation().y + doc.frame.getHeight()<=bottomRightY );
		    				if(upperLeftContained || bottomRightContained){
		    					contained=true;
		    				}
		    			}
		    			//System.out.println("Picture : " + pict + " at x, y, xb, yb : "+ x+", "+y+", "+bottomRightX+", "+bottomRightY);
		    			//System.out.println(contained);
		    			
		    		}
		    		botDocs.add(pict);
		    		pict.frame.setLocation(x,y);
		    		up=true;
		    	}
		    	listOfFiles[cpt]=null;
		    }
		    up=rand.nextBoolean();
	}
	
	private boolean putInChest(Doc doc){
		boolean inChest = false;
		if(selectedObj != null && state == State.RUN){
			if(selectedObj.equals(doc)){
				score+=(100-((doc.frame.getWidth()*doc.frame.getHeight())/10000));
				int offsetX = rand.nextInt(getWidth()-selectedObj.frame.getWidth());
				int offsetY = rand.nextInt(getHeight()/3-selectedObj.frame.getHeight());
				selectedObj.frame.setLocation(chest.x+offsetX, chest.y+offsetY);
				inChest=true;
			}
		}
		return inChest;
	}
	
	private void pause(){
		if(chrono.enFonctionnement()){
			chrono.suspendre();
			state = State.PAUSE;
		}else if(state == State.PAUSE){
			chrono.reprendre();
			state = State.RUN;
		}
	}
	
	private void reset(){
		selectedObj = null;
		score = 0;
		chrono.suspendre();
		state=State.BEGIN;
		botDocs.clear();
		topDocs.clear();
		int length = allPics.size();
		int cpt = rand.nextInt(length);
		boolean upperLeftContained;
		boolean bottomRightContained;
		List<Doc> tempPict = new ArrayList<>(allPics);
		
		for(int i = 0; i<length ;i++){
			cpt = rand.nextInt(tempPict.size());
			if(up){
				Doc pict = tempPict.get(cpt);
	    		
	    		int x = rand.nextInt(chest.width-pict.frame.getWidth());
	    		int y = rand.nextInt(chest.y-pict.frame.getHeight());
	    		int bottomRightX = x+pict.frame.getWidth();
	    		int bottomRightY = y+pict.frame.getHeight();
	    		//Algo to avoid recovery
	    		boolean contained=true;
	    		while(contained){
	    			contained=false;
	    			x = rand.nextInt(chest.width-pict.frame.getWidth());
		    		y = rand.nextInt(chest.y-pict.frame.getHeight());
		    		bottomRightX = x+pict.frame.getWidth();
		    		bottomRightY = y+pict.frame.getHeight();
	    			for(Doc doc : topDocs){
	    				upperLeftContained = x>=doc.frame.getLocation().x && x<=doc.frame.getLocation().x+doc.frame.getWidth() && y>=doc.frame.getLocation().y && y<=(doc.frame.getLocation().y+doc.frame.getHeight()) || (doc.frame.getLocation().x>=x && doc.frame.getLocation().x<=bottomRightX && doc.frame.getLocation().y >= y && doc.frame.getLocation().y<=bottomRightY );
	    				bottomRightContained = bottomRightX>=doc.frame.getLocation().x && bottomRightX<=doc.frame.getLocation().x+doc.frame.getWidth() && bottomRightY>=doc.frame.getLocation().y && bottomRightY<=(doc.frame.getLocation().y+doc.frame.getHeight()) || (doc.frame.getLocation().x+doc.frame.getWidth()>=x && (doc.frame.getLocation().x+doc.frame.getWidth())<=bottomRightX && doc.frame.getLocation().y + doc.frame.getHeight() >=y &&  doc.frame.getLocation().y + doc.frame.getHeight()<=bottomRightY );
	    				if(upperLeftContained || bottomRightContained){
	    					contained=true;
	    				}
	    			}
	    			//System.out.println("Picture : " + pict + " at x, y, xb, yb : "+ x+", "+y+", "+bottomRightX+", "+bottomRightY);
	    			//System.out.println(contained);
	    			
	    		}
	    		topDocs.add(pict);
	    		pict.frame.setLocation(x,y);
	    		tempPict.remove(cpt);
	    		up=false;
	    	}else{
	    		Doc pict = tempPict.get(cpt);
	    		
	    		int x = rand.nextInt(chest.width-pict.frame.getWidth());
	    		int y = chest.y+chest.height+rand.nextInt(getHeight()-(chest.height+chest.y)-pict.frame.getHeight());
	    		int bottomRightX = x+pict.frame.getWidth();
	    		int bottomRightY = y+pict.frame.getHeight();
	    		//Algo to avoid recovery
	    		boolean contained=true;
	    		while(contained){
	    			contained=false;
	    			x = rand.nextInt(chest.width-pict.frame.getWidth());
		    		y = chest.y+chest.height+rand.nextInt(getHeight()-(chest.height+chest.y)-pict.frame.getHeight());
		    		bottomRightX = x+pict.frame.getWidth();
		    		bottomRightY = y+pict.frame.getHeight();
	    			for(Doc doc : botDocs){
	    				//top Left point inside another pic
	    				upperLeftContained = x>=doc.frame.getLocation().x && x<=doc.frame.getLocation().x+doc.frame.getWidth() && y>=doc.frame.getLocation().y && y<=(doc.frame.getLocation().y+doc.frame.getHeight()) || (doc.frame.getLocation().x>=x && doc.frame.getLocation().x<=bottomRightX && doc.frame.getLocation().y >= y && doc.frame.getLocation().y<=bottomRightY );
	    				//bottom right inside another pic
	    				bottomRightContained = bottomRightX>=doc.frame.getLocation().x && bottomRightX<=doc.frame.getLocation().x+doc.frame.getWidth() && bottomRightY>=doc.frame.getLocation().y && bottomRightY<=(doc.frame.getLocation().y+doc.frame.getHeight()) || (doc.frame.getLocation().x+doc.frame.getWidth()>=x && (doc.frame.getLocation().x+doc.frame.getWidth())<=bottomRightX && doc.frame.getLocation().y + doc.frame.getHeight() >=y &&  doc.frame.getLocation().y + doc.frame.getHeight()<=bottomRightY );
	    				if(upperLeftContained || bottomRightContained){
	    					contained=true;
	    				}
	    			}
	    			//System.out.println("Picture : " + pict + " at x, y, xb, yb : "+ x+", "+y+", "+bottomRightX+", "+bottomRightY);
	    			//System.out.println(contained);
	    			
	    		}
	    		botDocs.add(pict);
	    		pict.frame.setLocation(x,y);
	    		tempPict.remove(cpt);
	    		up=true;
	    	}
		}
		
	}
	
	private void chooseElements(){
		if((up || botDocs.isEmpty()) && !topDocs.isEmpty()){
			//first select event
			if(selectedObj != null){
				if(botDocs.isEmpty()){
					topDocs.remove(selectedObj);
				}else{
					botDocs.remove(selectedObj);
				}
			}
			if(!topDocs.isEmpty()){
				selectedObj=topDocs.get(rand.nextInt(topDocs.size()));
				selectedObj.select();
			}
			up=false;
		}else{
			//first select event
			if(selectedObj != null){
				if(topDocs.isEmpty()){
					botDocs.remove(selectedObj);
				}else{
					topDocs.remove(selectedObj);
				}
			}
			if(!botDocs.isEmpty()){
				selectedObj=botDocs.get(rand.nextInt(botDocs.size()));
				selectedObj.select();
			}
			up=true;
		}
		if(topDocs.size() == 0 && botDocs.size() == 0){
			chrono.arreter();
			selectedObj=null;
		}
		dp.repaint();
	}

}
