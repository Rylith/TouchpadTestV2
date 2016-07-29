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

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
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

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
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
 
    private static JDesktopPane dp = new JDesktopPane();
    private DefaultListModel<Doc> listModel = new DefaultListModel<Doc>();
    private JList<Doc> list = new JList<Doc>(listModel);
    private static int left;
    private static int top;
    private JCheckBoxMenuItem copyItem;
    private JCheckBoxMenuItem nullItem;
    private JCheckBoxMenuItem thItem;
    private static Engine engine;
    private Timeline timeline;
  
    public class Doc extends InternalFrameAdapter implements ActionListener {
        String name;
        JInternalFrame frame;
        TransferHandler th;
        JTextArea area;
        File file;
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
            listModel.add(listModel.size(), this);
 
            area = new JTextArea();
            area.setMargin(new Insets(5, 5, 5, 5));
            ImagePanel im;
            try {
                //BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                //String in;
                BufferedImage image = null;
                InputStream is = new FileInputStream(url.getFile());
                ImageInputStream iis = ImageIO.createImageInputStream(is);
                image = ImageIO.read(iis);
                Image pic = image.getScaledInstance(400, 300, Image.SCALE_DEFAULT);
                im = new ImagePanel(pic); 
                /*while ((in = reader.readLine()) != null) {
                    area.append(in);
                    area.append("\n");
                }*/
                //reader.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
 
            th = im.getTransferHandler();
            area.setFont(new Font("monospaced", Font.PLAIN, 12));
            area.setCaretPosition(0);
            area.setDragEnabled(true);
            area.setDropMode(DropMode.INSERT);
			//frame.getContentPane().add(new JScrollPane(area));
            BasicInternalFrameUI bi = (BasicInternalFrameUI)frame.getUI();
            DragListener drag = new DragListener();
            frame.addMouseMotionListener(drag);
            frame.addMouseListener(drag);
            frame.getContentPane().add(im);
            dp.add(frame);
            frame.show();
            if (DEMO) {
                frame.setSize(300, 200);
            } else {
                frame.setSize(400, 300);
            }
            //frame.setResizable(true);
            //frame.setClosable(true);
            //frame.setIconifiable(true);
            //frame.setMaximizable(true);
            frame.setLocation(left, top);
            frame.setBorder(null);
            bi.setNorthPane(null);
            incr();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    select();
                }
            });
            nullItem.addActionListener(this);
            setNullTH();
        }
 
        public void internalFrameClosing(InternalFrameEvent event) {
            listModel.removeElement(this);
            nullItem.removeActionListener(this);
        }
 
        public void internalFrameOpened(InternalFrameEvent event) {
            int index = listModel.indexOf(this);
            list.getSelectionModel().setSelectionInterval(index, index);
        }
 
        public void internalFrameActivated(InternalFrameEvent event) {
            int index = listModel.indexOf(this);
            list.getSelectionModel().setSelectionInterval(index, index);
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
            setNullTH();
        }
         
        public void setNullTH() {
            if (nullItem.isSelected()) {
                area.setTransferHandler(null);
            } else {
                area.setTransferHandler(th);
            }
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

	private JToolBar toolBar;

	private static JMenuBar men;
 
    private static void incr() {
        left += 30;
        top += 30;
        if (top == 150) {
            top = 0;
        }
    }
 
    public ApplicationInterface() {
        super("TopLevelTransferHandlerDemo");
        setJMenuBar(createDummyMenuBar());
        toolBar = createDummyToolBar();
        getContentPane().add(toolBar, BorderLayout.NORTH);

        final JFXPanel fxPanel = new JFXPanel();
        dp.addComponentListener(new ComponentAdapter() {
        	public void componentResized(ComponentEvent e) {
        		fxPanel.setSize(getWidth(), getHeight());
        		Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        initFX(fxPanel);
                    }
                });
        	}
        });
        dp.add(fxPanel);
        JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, list, dp);
        sp.setDividerLocation(120);
        getContentPane().add(sp);
        
        //new Doc("sample.txt");
        //new Doc("sample.txt");
        //new Doc("sample.txt");
 
        list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                Doc val = (Doc)list.getSelectedValue();
                if (val != null) {
                    val.select();
                }
             }
        });
         
        final TransferHandler th = list.getTransferHandler();
 
        nullItem.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ae) {
                if (nullItem.isSelected()) {
                    list.setTransferHandler(null);
                } else {
                    list.setTransferHandler(th);
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
        dp.setTransferHandler(handler);
    }
 
    public static void createAndShowGUI(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
 
        final ApplicationInterface test = new ApplicationInterface();
        test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (DEMO) {
            test.setSize(493, 307);
        } else {
        	test.setSize(800, 600);
        }
        test.setLocationRelativeTo(null);
        test.setVisible(true);
        test.list.requestFocus();
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

	private Scene createScene(double width,double height) {
		Group root = new Group();
		Group circles = new Group();
		Scene scene = new Scene(root, width, height, Color.BLACK);
		scene.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent mouseEvent) {
		    	if(mouseEvent.getClickCount() >=2 ){
		    		System.out.println("double clicked");
					if(getExtendedState() != JFrame.MAXIMIZED_BOTH){
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
                    for (File file:db.getFiles()) {
                    	new Doc(file);
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
		
		root.getChildren().add(blendModeGroup);
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
			        new KeyFrame(new Duration(8000), // set end position at 8
			            new KeyValue(circle.translateXProperty(), random() * scene.getWidth()),
			            new KeyValue(circle.translateYProperty(), random() * scene.getHeight())
			        )
			    );
		}
		// play 40s of animation
		timeline.setCycleCount(Animation.INDEFINITE);
		//timeline.setAutoReverse(true);
		timeline.play();
		return scene;
	}

	public static void setEngine(Engine engine) {
		ApplicationInterface.engine = engine;
	}
}