/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 
package gui;

import static java.lang.Math.random;
 
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle; 
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import network.Interface.Engine;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.datatransfer.*;
 
/**
 * Demonstration of the top-level {@code TransferHandler}
 * support on {@code JFrame}.
 *
 * @author Shannon Hickey
 */
public class ApplicationInterface extends JFrame {
     
    /**
	 * 
	 */
	private static final long serialVersionUID = -3733033957725377148L;

	private static boolean DEMO = false;
	
	private ArrayList<Rectangle> rectList = new ArrayList<Rectangle>();
    private static final int COL = 3;
    private static final int LIGNE = 2;
	private static final double ECART = 50;
	private static final int NB_DIVISION=(COL*LIGNE);
	private static final double PERCENT_X_FRAME_SIZE=0.75;
	private static final double PERCENT_Y_FRAME_SIZE=0.75;
	
    private static JDesktopPane dp = new TransparentDesktopPane();
    private static List<TitledPane> listTitle = new ArrayList<TitledPane>();
    private static List<ListView<Doc>> listViews = new ArrayList<ListView<Doc>>();
    private static int left=0;
    private static int top;

    private JCheckBoxMenuItem copyItem;
    private JCheckBoxMenuItem nullItem;
    private JCheckBoxMenuItem thItem;
    private static Engine engine;
    private Timeline timeline;
    private JToolBar toolBar;
	private static JMenuBar men;
    
    
  
    public class Doc extends InternalFrameAdapter implements ActionListener {
        String name;
        JInternalFrame frame;
        TransferHandler th;
        int index = 0;
		private double percentX;
		private double percentY;
		ImagePanel im = null;
		BufferedImage image = null;
        
        public Doc(File file,int index){
        	this.name = file.getName();
            this.index=index;
            try {
                init(file.toURI().toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        
        public Doc(File file) {
            this(file,0);
        }
         
        public Doc(String name) {
            this.name = name;
            this.index=0;
            init(getClass().getResource(name));
        }
         
        private void init(URL url) {
            frame = new JInternalFrame(name);
            frame.addInternalFrameListener(this);
            //listModel.add(listModel.size(), this);
            listViews.get(index).getItems().add(this);
            listTitle.get(index).setExpanded(true);
            
            Rectangle rect = rectList.get(index);
            int x_size = (int) Math.round(rect.getWidth()*PERCENT_X_FRAME_SIZE);
            int y_size = (int) Math.round(rect.getHeight()*PERCENT_Y_FRAME_SIZE);
            Image pic;
            try {
                //BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                
                InputStream is = new FileInputStream(URLDecoder.decode(url.getFile(), "UTF-8"));
                //System.out.println(URLDecoder.decode(url.getFile(), "UTF-8"));
                ImageInputStream iis = ImageIO.createImageInputStream(is);
                image = ImageIO.read(iis);
                pic = Scalr.resize(image, x_size, y_size,(BufferedImageOp)null);
                im = new ImagePanel(pic);
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            th = im.getTransferHandler();
            BasicInternalFrameUI bi = (BasicInternalFrameUI)frame.getUI();
            DragListener drag = new DragListener(this,rectList);
            frame.addMouseMotionListener(drag);
            frame.addMouseListener(drag);
            frame.getContentPane().add(im);
            dp.add(frame);
            if (DEMO) {
                frame.setSize(300, 200);
            } else {
            	setInternalFrameSize(pic, frame);
            }
            
            frame.setLocation(left-(frame.getWidth()/2), top-(frame.getHeight()/2));
            frame.setBorder(null);
            bi.setNorthPane(null);
            frame.show();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    select();
                }
            });
            //incr();
        }
 
        public void internalFrameClosing(InternalFrameEvent event) {
        	listViews.get(index).getItems().remove(this);
        }
 
        public void internalFrameOpened(InternalFrameEvent event) {
            listViews.get(index).getSelectionModel().select(this);
            listTitle.get(index).setExpanded(true);
        }
 
        public void internalFrameActivated(InternalFrameEvent event) {
            listViews.get(index).getSelectionModel().select(this);
            listTitle.get(index).setExpanded(true);
        }
 
        public String toString() {
            return name;
        }
         
        public void select() {
            try {
                frame.toFront();
                frame.setSelected(true);
            } catch (java.beans.PropertyVetoException e) {}
        }
         
        public void actionPerformed(java.awt.event.ActionEvent ae) {
        }

		public void setPercent() {
			Rectangle rect = rectList.get(index);
			double width = rect.getWidth();
			double height = rect.getHeight();
			double x = rect.getX();
			double y = rect.getY();
			
			if(width != 0){
				this.percentX=1-(width-(frame.getX()-x))/width;
			}
			if(height != 0){
				this.percentY=1-(height-(frame.getY()-y))/height;
			}
		}

		public double getPercentY() {
			return percentY;
		}
		
		public double getPercentX() {
			return percentX;
		}
         
    }
 
    private TransferHandler handler = new TransferHandler() {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public boolean canImport(TransferHandler.TransferSupport support) {
            if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                return false;
            }
 
            if (copyItem.isSelected()) {
                boolean copySupported = (COPY & support.getSourceDropActions()) == COPY;
 
                if (!copySupported) {
                    return false;
                }
 
                support.setDropAction(COPY);
            }
 
            return true;
        }
 
        public boolean importData(TransferHandler.TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
             
            Transferable t = support.getTransferable();
 
            try {
                @SuppressWarnings("unchecked")
				java.util.List<File> l =
                    (java.util.List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
 
                for (File f : l) {
                    new Doc(f);
                }
            } catch (UnsupportedFlavorException e) {
                return false;
            } catch (IOException e) {
                return false;
            }
 
            return true;
        }
    };
 
    private void incr() {
        left += 30;
        top += 30;
        if (top == 150) {
            top = 0;
        }
    }
 
    public ApplicationInterface() {
        super("Pictures Sort");
        setJMenuBar(createDummyMenuBar());
        toolBar = createDummyToolBar();
        getContentPane().add(toolBar, BorderLayout.NORTH);
        
        final JFXPanel fxPanelDrop = new JFXPanel();
        final JFXPanel fxPanelList = new JFXPanel();
        final JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fxPanelList, dp);
        dp.addComponentListener(new ComponentAdapter() {
        	public void componentResized(ComponentEvent e) {
        		
        		JDesktopPane desktop = (JDesktopPane) e.getComponent();
        		fxPanelDrop.setSize(desktop.getWidth(), desktop.getHeight());
        		fxPanelList.setSize((getWidth()-(desktop.getWidth())),desktop.getHeight());
        		sp.setDividerLocation(0.25f);
        		Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                    	initFXList(fxPanelList);
                    	initFX(fxPanelDrop);
             
                        ChangeListener<Doc> chListener = new ChangeListener<Doc>(){
            				@Override
            				public void changed(ObservableValue<? extends Doc> observable, Doc oldValue, Doc newValue) {
            					if(newValue != null){
            						newValue.select();
            					}
            				}
                    	};
                        for(ListView<Doc> lis : listViews){
                        	lis.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                        	lis.getSelectionModel().selectedItemProperty().addListener(chListener);
                        	for(Doc doc : lis.getItems()){
                        		Rectangle rect = rectList.get(doc.index);
                        		int x_size = (int) Math.round(rect.getWidth()*PERCENT_X_FRAME_SIZE);
                                int y_size = (int) Math.round(rect.getHeight()*PERCENT_Y_FRAME_SIZE);
                                doc.frame.remove(doc.im);
                                Image pic = Scalr.resize(doc.image, x_size, y_size,(BufferedImageOp)null);
                                doc.im = new ImagePanel(pic);
                                doc.frame.add(doc.im);
                        		setInternalFrameSize(pic, doc.frame);
                        		doc.frame.setLocation((int)(rect.getX()+rect.getWidth()*doc.getPercentX()), (int) (rect.getY()+rect.getHeight()*doc.getPercentY()));
                        	}
                        }
                  }
        		});
        	}	
        });
        dp.add(fxPanelDrop);
        sp.setDividerLocation(120);
        getContentPane().add(sp);
       
        //final TransferHandler th = list.getTransferHandler();
 
        nullItem.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ae) {
                if (nullItem.isSelected()) {
                    //list.setTransferHandler(null);
                } else {
                    //list.setTransferHandler(th);
                }
            }
        });
        thItem.addActionListener(new ActionListener() {
        	@Override
			public void actionPerformed(java.awt.event.ActionEvent arg0) {
			    if (thItem.isSelected()) {
                    setTransferHandler(handler);
                } else {
                    setTransferHandler(null);
                }
            }
        });
        //dp.setTransferHandler(handler);
    }
    
    private void initFXList(JFXPanel fxPanelList) {
    	Scene scene = createListScene(fxPanelList.getWidth(),fxPanelList.getHeight());
        fxPanelList.setScene(scene);
	}
 
    public static void createAndShowGUI(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
 
        ApplicationInterface test = new ApplicationInterface();
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image img = kit.createImage("watch_2-512.png");
		test.setIconImage(img);
        test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (DEMO) {
            test.setSize(493, 307);
        } else {
        	test.setSize(800, 600);
        }
        test.setLocationRelativeTo(null);
        test.setVisible(true);
    }
     
    private JToolBar createDummyToolBar() {
        JToolBar tb = new JToolBar();
        JButton b;
        b = new JButton("New");
        b.setRequestFocusEnabled(false);
        tb.add(b);
        b = new JButton("Open");
        b.setRequestFocusEnabled(false);
        tb.add(b);
        b = new JButton("Save");
        b.setRequestFocusEnabled(false);
        tb.add(b);
        b = new JButton("Print");
        b.setRequestFocusEnabled(false);
        tb.add(b);
        b = new JButton("Preview");
        b.setRequestFocusEnabled(false);
        tb.add(b);
        tb.setFloatable(false);
        return tb;
    }
     
    private JMenuBar createDummyMenuBar() {
        JMenuBar mb = new JMenuBar();
        mb.add(createDummyMenu("File"));
        mb.add(createDummyMenu("Edit"));
        mb.add(createDummyMenu("Search"));
        mb.add(createDummyMenu("View"));
        JMenu menu = new JMenu("Tools");
        JMenuItem item = new JMenuItem("Preference");
        item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						new GraphicalInterface(engine).createAndShowGUI();
					}
				});
			}
		});
        menu.add(item);
        mb.add(menu);
        mb.add(createDummyMenu("Help"));
         
        JMenu demo = new JMenu("Demo");
        demo.setMnemonic(KeyEvent.VK_D);
        mb.add(demo);
 
        thItem = new JCheckBoxMenuItem("Use Top-Level TransferHandler");
        thItem.setMnemonic(KeyEvent.VK_T);
        demo.add(thItem);
 
        nullItem = new JCheckBoxMenuItem("Remove TransferHandler from List and Text");
        nullItem.setMnemonic(KeyEvent.VK_R);
        demo.add(nullItem);
 
        copyItem = new JCheckBoxMenuItem("Use COPY Action");
        copyItem.setMnemonic(KeyEvent.VK_C);
        copyItem.setSelected(true);
        demo.add(copyItem);
        
        men = mb;
        return mb;
    }
     
    private JMenu createDummyMenu(String str) {
        JMenu menu = new JMenu(str);
        JMenuItem item = new JMenuItem("[Empty]");
        item.setEnabled(false);
        menu.add(item);
        return menu;
    }
    
    private void initFX(JFXPanel fxPanel) {
        // This method is invoked on the JavaFX thread
        Scene scene = createScene(fxPanel.getWidth(),fxPanel.getHeight());
        fxPanel.setScene(scene);
    }
    
    private Scene createListScene(double width,double height){
    	BorderPane root=null;
		try {
			root = FXMLLoader.load(getClass().getResource("Sample.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		SplitPane split = null;
		
		for(Node node : root.getChildren()){
			if(node instanceof SplitPane){
				 split = (SplitPane) node;
			}
		}
		
		VBox box=null;
		for(Node node : split.getItems()){
			if(node instanceof VBox){
				box=(VBox) node;
			}
		}
		Scene scene = new Scene(box, width, height, Color.BLACK);
		
		for(Node node : box.getChildren()){
			if(listTitle.size() >= NB_DIVISION){
				if(listTitle.get(0).isExpanded()){
					((TitledPane) node).setExpanded(true);
				}
				listTitle.remove(0);
			}
			listTitle.add((TitledPane) node);
		}
		
		for(TitledPane title : listTitle){
			@SuppressWarnings("unchecked")
			ListView<Doc> list = (ListView<Doc>) title.getContent();
			if(listViews.size()>=NB_DIVISION){
				//Transfer data from previous list
				list.setItems(listViews.get(0).getItems());
				//Select docs selected before resize
				for(int i = 0;i<list.getItems().size();i++){
					if(listViews.get(0).getSelectionModel().isSelected(i)){
						list.getSelectionModel().select(i);
					}
				}
				listViews.remove(0);
			}else{
				ObservableList<Doc> obsvervableList = FXCollections.observableArrayList();
				list.setItems(obsvervableList);
			}
			listViews.add(list);
		}
		return scene;
    }
    
	private Scene createScene(double width,double height) {
		Group dropArea = new Group();
		Group circles = new Group();	
		Scene scene = new Scene(dropArea, width, height, Color.BLACK);
		scene.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent mouseEvent) {
		    	if(mouseEvent.getClickCount() >=2 ){
		    		System.out.println("double clicked");
					if(getJMenuBar() != null){
						setExtendedState(JFrame.MAXIMIZED_BOTH); 
						dispose();
						setUndecorated(true);
						setJMenuBar(null);
						remove(toolBar);
						setVisible(true);
					}else{
						setSize(800, 600);
						dispose();
						setJMenuBar(men);
						getContentPane().add(toolBar, BorderLayout.NORTH);
						setUndecorated(false);
						setLocationRelativeTo(null);
						setVisible(true);
					}
		    		
		    	}
		    		//System.out.println("mouse click detected! " + mouseEvent.getSource());
		    }
		});		
		
        /*Experimenting with rectangles*/
		/*100+250*/
		Group rectGroup = new Group();
		double rectWidth = (int)(width-(ECART+COL*ECART))/COL;
		//System.out.println("LONGUEUR: " + rectWidth);
		double rectHeight = (int)(height-(ECART+LIGNE*ECART))/LIGNE;
		//System.out.println("HAUTEUR: " + rectHeight);
		double x = 0;
		double y = 0;
		int index = 0;
		
		rectList.clear();
		for (int l= 1; l <= LIGNE; l++) {
			//System.out.println("COLONNE: " + c);
			for (int c = 1; c <= COL; c++){
				//System.out.println("LIGNE: " + l);
				//x y
				x = c*ECART+((c-1)*rectWidth);
				//System.out.println("CALCUL DE X: " + c +" * ECART + " + "(("+c+"-1) * rectWidth)");
				//System.out.println("X: " + x);
				y = l*ECART+((l-1)*rectHeight);
				//System.out.println("CALCUL DE Y: " + l +" * ECART + ((" + l +"-1)*rectHeight)");
				//System.out.println("Y: " + y);
				Rectangle tempRect = new Rectangle(x,y,rectWidth,rectHeight);
				tempRect.setId(Integer.toString(index++));
				tempRect.setVisible(true);
				//tempRect.setFill(Color.RED);
				tempRect.setFill(Color.TRANSPARENT);
				tempRect.setStroke(Color.WHITE);
				tempRect.setArcHeight(15);
		        tempRect.setArcWidth(15);
				tempRect.setStrokeWidth(5);
				rectList.add(tempRect);
				rectGroup.getChildren().add(tempRect);
			}
		}
		final Color shadowColor = Color.WHITE.deriveColor(1, 1, 1, 1); 
        final DropShadow dropShadow = new DropShadow(BlurType.THREE_PASS_BOX, shadowColor, 20, 0, 10, 10); 
        
        rectGroup.setEffect(dropShadow);
		
		scene.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                } else {
                    event.consume();
                }
            }
        });
		// Dropping over surface
        scene.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    success = true;
                    for (int i = 0; i < rectList.size(); i++) {
                    	Rectangle rectCurr = rectList.get(i);
                    	if(rectCurr.contains(event.getSceneX(), event.getSceneY())){
                    		left = (int)event.getSceneX();
	                        top = (int)event.getSceneY();
	                        /*left = (int)rectCurr.getX();
	                        top = (int)rectCurr.getY();*/
		                    for (File file:db.getFiles()) {
		                    	new Doc(file,i).setPercent();
		                    }
                    	}
					}
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
        
		for (int i = 0; i < 30; i++) {
			   Circle circle = new Circle(150, Color.web("white", 0.05));
			   circle.setStrokeType(StrokeType.OUTSIDE);
			   circle.setStroke(Color.web("white", 0.16));
			   circle.setStrokeWidth(4);
			   circles.getChildren().add(circle);
			}
		circles.setEffect(new BoxBlur(10, 10, 3));
		Rectangle colors = new Rectangle(scene.getWidth(), scene.getHeight(),
			     new LinearGradient(0f, 1f, 1f, 0f, true, CycleMethod.NO_CYCLE, new 
			         Stop[]{
			            new Stop(0, Color.web("#f8bd55")),
			            new Stop(0.14, Color.web("#c0fe56")),
			            new Stop(0.28, Color.web("#5dfbc1")),
			            new Stop(0.43, Color.web("#64c2f8")),
			            new Stop(0.57, Color.web("#be4af7")),
			            new Stop(0.71, Color.web("#ed5fc2")),
			            new Stop(0.85, Color.web("#ef504c")),
			            new Stop(1, Color.web("#f2660f")),}));
		colors.widthProperty().bind(scene.widthProperty());
		colors.heightProperty().bind(scene.heightProperty());
		Group blendModeGroup = 
			    new Group(new Group(new Rectangle(scene.getWidth(), scene.getHeight(),
			        Color.BLACK), circles), colors);
		colors.setBlendMode(BlendMode.OVERLAY);
		
		dropArea.getChildren().add(blendModeGroup);
		dropArea.getChildren().add(rectGroup);
		
		if(timeline !=null){
			timeline.stop();
		}
		timeline = new Timeline();
		
		for (Node circle: circles.getChildren()) {
			timeline.getKeyFrames().addAll(
			        new KeyFrame(Duration.ZERO, // set start position at 0
			            new KeyValue(circle.translateXProperty(), random() * scene.getWidth()),
			            new KeyValue(circle.translateYProperty(), random() * scene.getHeight())
			        ),
			        new KeyFrame(new Duration(40000), // set end position at 40
			            new KeyValue(circle.translateXProperty(), random() * scene.getWidth()),
			            new KeyValue(circle.translateYProperty(), random() * scene.getHeight())
			        )
			    );
		}
		// play 40s of animation
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
		return scene;
	}

	public static void setEngine(Engine engine) {
		ApplicationInterface.engine = engine;
	}
	
	public static List<ListView<Doc>> getListView(){
		return listViews;
	}
	
	public static List<TitledPane> getListTitles(){
		return listTitle;
	}
	
	private void setInternalFrameSize(Image image,final JInternalFrame frame){
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
}