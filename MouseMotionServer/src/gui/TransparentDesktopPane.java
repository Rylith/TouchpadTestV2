package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JDesktopPane;

public class TransparentDesktopPane extends JDesktopPane {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4079925195238463570L;

	@Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        this.setOpaque(false);
        Color db = new Color(0,0,0,0);
        g2d.setBackground(db);
        //g2d.setComposite(AlphaComposite.SrcOver.derive(0.1f));
        super.paint(g2d);
        g2d.finalize();
        this.setOpaque(true);
    }

}
