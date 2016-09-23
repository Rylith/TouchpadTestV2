package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;

public class Chrono implements Runnable {
	private int x, y;
	private JComponent proprietaire;
	private Thread deroulement;
	private long tempsEcoule = 0; // exprime en millisecondes
	private long duree; // nombre de millisecondes pour un tour complet
	private long momentDebut = 0;
	private long momentSuspension;
	private boolean continuer;
	private boolean finir;

	/* - proprietaire donne le composant devant contenir l'image du chronometre.
	 * - duree donne le temps en secondes mis pour que le chronometre fasse un tour complet,
	 * apres ce temps, le chronometre s'arrete.
	 * - x et y indiquent  les coordonnees du coin superieur gauche du carre 
	 * circonscrit au chronometre
	 *- diametre indique le diametre du chronometre*/
	public Chrono(JComponent proprietaire, int duree, int x, int y, int diametre) {
		this.duree = duree * 1000;
		this.tempsEcoule = this.duree;
		this.x = x;
		this.y = y;
		this.proprietaire = proprietaire;
	}

	/* Demarre le chronometre */
	public void demarrer()  {   
		if (enFonctionnement()) {
			arreter();
			try {
				deroulement.join();
			}
			catch(InterruptedException exc) {
				exc.printStackTrace();
			}
		}
		deroulement = new Thread(this);
		deroulement.start();
	}

	/* Suspend le deroulement du temps ; ce deroulement pourra etre repris 
	 * dans l'etat ou il se trouvait par la methode reprendre */
	public void suspendre() {     
		if (enFonctionnement()  && continuer) {
			momentSuspension = System.currentTimeMillis();
			continuer = false;
		}
	}

		/* Si le chronometre est en fonctionnment mais a ete suspendu, 
	 * il recommence a tourne r*/ 
	public synchronized  void reprendre() { 
		if (enFonctionnement() && !continuer) {  
			momentDebut +=  System.currentTimeMillis() - momentSuspension;
			continuer = true;
			notifyAll();

		}
	}

	/* Arrete le chronometre. Une fois arrete, le chronometre ne peut
    repartir qu'avec la methode demarrer, au debut du d�compte du temps*/
	public synchronized void arreter() {
		if (enFonctionnement()) {
			finir = true;
			notifyAll();
		}
	}

	/* Fait tourner le chronometre */
	public void run() {
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		continuer = true;
		finir = false;
		momentDebut = System.currentTimeMillis();
		while((tempsEcoule > 0) && (!finir))
		{
			tempsEcoule = duree - (System.currentTimeMillis() - momentDebut);
			proprietaire.repaint();
			try {
				Thread.sleep(1);
				synchronized(this) {
					while (!continuer && !finir){
						wait();
					}
				}
			}
			catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}

	/* Retourne true si le chronometre est en fonctionnement,  eventuellement suspendu 
	 * et false si le chronometre n'est pas demarre, ou bien a ete arrete, ou bien a fini de tourner*/
	public boolean enFonctionnement() {
		return (deroulement!=null) && (deroulement.isAlive());
	}

	/* Dessine le chronometre selon le temps pendant lequel il a tourne  depuis qu'il a ete mis en fonctionnement */
	public void paint(Graphics g) {
		g.setColor(Color.black);
		long ms = tempsEcoule-(TimeUnit.MINUTES.toMillis(TimeUnit.MILLISECONDS.toMinutes(tempsEcoule)))-TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(tempsEcoule)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(tempsEcoule)));
		g.drawString(String.format("%d:%02d:%03d", TimeUnit.MILLISECONDS.toMinutes(tempsEcoule),TimeUnit.MILLISECONDS.toSeconds(tempsEcoule)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(tempsEcoule)),ms), x, y);
		g.drawString(String.format("%d:%02d:%03d", TimeUnit.MILLISECONDS.toMinutes(tempsEcoule),TimeUnit.MILLISECONDS.toSeconds(tempsEcoule)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(tempsEcoule)),ms), x, proprietaire.getHeight()-y-30);
	}
}