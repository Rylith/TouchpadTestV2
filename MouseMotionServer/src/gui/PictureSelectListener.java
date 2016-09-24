package gui;

import java.util.EventListener;

import gui.InterfaceGame.Doc;

public interface PictureSelectListener extends EventListener {
	
	/**
	 * @param doc the picture selected*/
	public void pictureSelected(Doc doc);

}
