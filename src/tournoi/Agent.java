package tournoi;

import java.util.ArrayList;
import lejos.hardware.Button;
import lejos.utility.Delay;

public class Agent{ 
	
	private static Capteurs capteur;
	private static Actionneurs actionneur;
	public static int speed = 500;
	public static int rot_speed = 200;
	public static int angleOriente = 0;
	private static float dist_critique = 0.326f;
	private static boolean palet = false;

	public float[] val = new float[1];
	
	public Agent(Actionneurs actionneur, Capteurs capteur) {
		Agent.actionneur = actionneur;
		Agent.capteur = capteur;
	}
	
	public void allerVersPalet() { // premiere version
		ArrayList<Float> distances = new ArrayList<Float>(); // par defaut, la liste est de taille 10
		float d = 0;
		float diff = 0;
		distances.add(capteur.dist());
		boolean abandon = false;
		while(!(palet || abandon)){
			while(diff < 0.1 || distances.get(distances.size()) > 0.32) {
				System.out.println("avance vers objet");
				Actionneurs.rouler();
				d = capteur.dist();
				if(d >= 2.0)
					distances.add((float)2.0);
				else
					distances.add(d);
				diff = distances.get(distances.size()-1) - distances.get(distances.size()-2); // size renvoie la taille de la liste, element nulls compris ou pas ?
			}
			float seuil = (float)2.0;
			for(int i=0; i < distances.size(); i++){
				if(distances.get(i) < seuil && distances.get(i) > (float)0.01)
					seuil = distances.get(i);
			}
			// seuil : derniere distance mesuree de l'objet (on s'assure qu'il soit non nul)
			if(seuil < 0.35 && seuil > 0.3) { // on a trouve un palet  (on a modifie distances[i-2] par seuil
				while(!capteur.isPressed()) {
					System.out.println("palet detecte");
					Actionneurs.rouler();
				}
				Agent.actionneur.mvt_pince(-800);
				palet = true;
			}
			
			else if(seuil > 0.35) { // on a perdu un palet : balayage
				int c = 0;
				distances.add(capteur.dist());
				speed = 100;
				while(distances.get(distances.size()-1) > seuil + 0.05 || c == 100) {
					System.out.println("palet perdu");
					Actionneurs.roueDroite.rotate(5);
					distances.add(capteur.dist());
					c += 5;
				}
				c = 0;
				distances.add(capteur.dist());
				while(distances.get(distances.size()-1) > seuil + 0.05 || c == -200) {
					System.out.println("palet perdu");
					Actionneurs.roueDroite.rotate(-5);
					distances.add(capteur.dist());
					c -= 5;
				}
				if(c == -200) 
					abandon = true;
				speed = 300;
			}
			
			for(int k = 0; k < distances.size(); k++) {
				distances.set(k, (float)0.0);
			}  // A la fin de chaque boucle, on supprime toutes les distances mesurees precedemment pour qu'elle n'affectent pas la suite
		}
	}
	
	public void allerVersPalet2(){ // deuxieme version beaucoup plus simple
		while(!capteur.isPressed()) {
			System.out.println("palet detecte");
			Actionneurs.rouler();
		}
		Actionneurs.stop_roues();
		Agent.actionneur.mvt_pince(-1500);
		palet = true;
	}
	
	public void AllerAuBut(){
	    double diff = 0;
	    ArrayList<Float> distances = new ArrayList<Float>();
	   // angleOriente = roueDroite.getLimitAngle();
	    if(angleOriente < 0)
	    	Actionneurs.rotateClockwise(angleOriente);
	    else 
	        Actionneurs.rotateCounterClockwise(-angleOriente);
	    while(Capteurs.rvb() != "blanc"){
	        Actionneurs.rouler();
	    	float d = capteur.dist();
	        if(d < 0.4){
	        	distances.add(d);
	        	while(diff < 0.1 && Capteurs.rvb() != "blanc"){
	        		Actionneurs.rouler();
		            distances.add(capteur.dist());
		            diff = distances.get(distances.size()-1) - distances.get(distances.size()-2);
	        	}
	        	if(Capteurs.rvb() != "blanc") {
	        		Actionneurs.stop_roues();
		        	Actionneurs.rotateClockwise(200);
		        	Actionneurs.rouler();
		        	Delay.msDelay(700);
		        	Actionneurs.stop_roues();
		        	Actionneurs.rotateCounterClockwise(200);
		        	diff = 0;
	        	}
	        }
	    }
	    Actionneurs.stop_roues();
	    // une fois qu’on est sorti du grand while, on ouvre les pinces
	    Agent.actionneur.mvt_pince(800);
	    Agent.actionneur.reculer();
	    Delay.msDelay(200);
	    Actionneurs.stop_roues();

	// puis on fait une rotation de 90 degrés avant d’effectuer une recherche de palet
	    Actionneurs.rotateClockwise(390); // quart de tour
	    angleOriente = Actionneurs.roueDroite.getLimitAngle();
	}
	
	public void allerAuBut2() {
		double diff = 0;
	    ArrayList<Float> distances = new ArrayList<Float>();
	    while(Capteurs.rvb() != "blanc"){
	        Actionneurs.rouler();
	    	float d = capteur.dist();
	        if(d < 0.4){
	        	distances.add(d);
	        	while(diff < 0.1 && Capteurs.rvb() != "blanc"){
	        		Actionneurs.rouler();
		            distances.add(capteur.dist());
		            diff = distances.get(distances.size()-1) - distances.get(distances.size()-2);
	        	}
	        	if(Capteurs.rvb() != "blanc") {
	        		Actionneurs.stop_roues();
	        		Actionneurs.rotateCounterClockwise(200);
		        	Actionneurs.rouler();
		        	Delay.msDelay(1500);
		        	Actionneurs.stop_roues();
		        	Actionneurs.rotateClockwise(170);
		        	diff = 0;
	        	}
	        }
	    }
	    Actionneurs.stop_roues();
	    // une fois qu’on est sorti du grand while, on ouvre les pinces
	    Agent.actionneur.mvt_pince(1500);
	    Agent.actionneur.reculer();
	    Delay.msDelay(400);
	    Actionneurs.stop_roues();
	}
	
	
	public void chercherPalet(){
        ArrayList<Float> distances = new ArrayList<Float>();
        ArrayList<Float> distCritique = new ArrayList<Float>();
        ArrayList<Integer> angleCritique = new ArrayList<Integer>();
        double diff = 0;
        distances.add(capteur.dist());
        int angle = 0;
        while(angle < 750) {
        	Actionneurs.roueDroite.backward();
        	distances.add(capteur.dist());
            if(diff < -0.3 || diff > 0.3) {
            	distCritique.add(distances.get(distances.size()-1));
            	angleCritique.add(angle);
            }
            diff = distances.get(distances.size()-1) - distances.get(distances.size()-2);
            angle = angle + Actionneurs.roueDroite.getLimitAngle();
        }
        Actionneurs.stop_roues();
        double min = distCritique.get(0);
        int angleMin = 0;
        for(int j = 1; j<distCritique.size(); j++)
             if(distCritique.get(j) < min) {
                min = distCritique.get(j);
        		angleMin = angleCritique.get(j);
             }
        while(angle > angleMin) {
        	Actionneurs.roueDroite.forward();
        	angle = angle + Actionneurs.roueDroite.getLimitAngle();
        	Actionneurs.stop_roues();
        	System.out.println("angle limited : " + Actionneurs.roueDroite.getLimitAngle());
        	Button.ENTER.waitForPressAndRelease();
        }
	}
        
	public void chercherPalet2(){
        ArrayList<Float> distances = new ArrayList<Float>();
        double diff = 0;
        distances.add(capteur.dist());
        Actionneurs.roueDroite.backward();
        while(Math.abs(diff) < 0.4) {
        	Delay.msDelay(1);
        	float somme = 0;
        	int c = 0;
        	for(int i=0; i<5;i++) {
        		float d = capteur.dist();
	            if(d != Float.POSITIVE_INFINITY) {
	                somme += d;
	                c+=1;
	                System.out.println("d : " + d);
	            }
	            else {
	            	System.out.println("infini");
	            }
        	}
        	distances.add(somme/c);
            diff = distances.get(distances.size()-1) - distances.get(distances.size()-2);
        }
    }
	
	public void tourneD90(){
		Actionneurs.rotateClockwise(400);
		}
	
	public void tourneG90(){
		Actionneurs.rotateCounterClockwise(420);
		}
    
	
	public void tourne180(){
		Actionneurs.rotateClockwise(800);
	}
	
	public void avancerVers(String couleur){
		while(Capteurs.rvb() != couleur) {
			Actionneurs.rouler();
		}
		Actionneurs.rouler();
		Delay.msDelay(40);
		Actionneurs.stop_roues();
	}
	
	public static void main(String[] args) {
		
		Agent robot = new Agent(actionneur, capteur);
		
// ___________________ Droite _____________________
	
		System.out.println("go");
		Button.ENTER.waitForPressAndRelease();
		
		robot.allerVersPalet2();
		robot.allerAuBut2();
		robot.tourne180();
		
		
		while(Capteurs.rvb() != "jaune") {
			robot.avancerVers("bleu");
			robot.tourneG90();
			Actionneurs.rotateClockwise(15);
			//robot.allerVersPalet2();
			while(!capteur.isPressed() && Capteurs.rvb() != "jaune") {
				Actionneurs.rouler();
			}
			if(Capteurs.rvb() == "jaune")
				continue;
			Actionneurs.stop_roues();
			actionneur.mvt_pince(-1500);
			robot.tourneG90();
			Actionneurs.rotateCounterClockwise(40);
			robot.allerAuBut2();
			// au lieu de faire un 180, on fait un 90 a gauche 2 fois
			// pour reprendre la ligne bleue a la base. et donc pas se faire 
			// avoir par une autre couleur qui superpose le bleu.
			robot.tourneG90();
			Actionneurs.rouler();
			Delay.msDelay(200);
			Actionneurs.stop_roues();
			robot.tourneG90();
		}
		Actionneurs.rouler();
		Delay.msDelay(1500);
		Actionneurs.stop_roues();
		robot.tourneD90();
		
		while(capteur.dist() > 0.25) {
			robot.avancerVers("noir");
			Actionneurs.rouler();
			Delay.msDelay(500);
			Actionneurs.stop_roues();
			robot.tourneD90();
			Actionneurs.rotateCounterClockwise(70);
			while(!capteur.isPressed() && capteur.dist() >=0.25) {
				Actionneurs.rouler();
			}
			if(capteur.dist() < 0.25)
				continue;
			Actionneurs.stop_roues();
			actionneur.mvt_pince(-1500);
			actionneur.tourneD90();
			robot.allerAuBut2();
			// meme probleme
			robot.tourneD90();
			Actionneurs.rouler();
			Delay.msDelay(30);
			Actionneurs.stop_roues();
			robot.tourneD90();
		}
		
		robot.tourneG90();
		
		while(Capteurs.rvb() != "jaune") {
			robot.avancerVers("vert");
			robot.tourneG90();
			while(!capteur.isPressed() && Capteurs.rvb() != "jaune") {
				Actionneurs.rouler();
			}
			if(Capteurs.rvb() == "jaune")
				continue;
			Actionneurs.stop_roues();
			actionneur.mvt_pince(-1500);
			robot.tourneG90();
			robot.allerAuBut2();
			//meme probleme
			robot.tourneG90();
			Actionneurs.rouler();
			Delay.msDelay(30);
			robot.tourneG90();
		}
		
// ___________________ Gauche _____________________

		System.out.println("go");
		Button.ENTER.waitForPressAndRelease();
		
		robot.allerVersPalet2();
		robot.allerAuBut2();
		robot.tourne180();
		
		
		while(Capteurs.rvb() != "rouge") {
			robot.avancerVers("vert");
			robot.tourneG90();
			Actionneurs.rotateClockwise(15);
			//robot.allerVersPalet2();
			while(!capteur.isPressed() && Capteurs.rvb() != "rouge") {
				Actionneurs.rouler();
			}
			if(Capteurs.rvb() == "rouge")
				continue;
			Actionneurs.stop_roues();
			actionneur.mvt_pince(-1500);
			robot.tourneG90();
			Actionneurs.rotateCounterClockwise(40);
			robot.allerAuBut2();
			// au lieu de faire un 180, on fait un 90 a gauche 2 fois
			// pour reprendre la ligne bleue a la base. et donc pas se faire 
			// avoir par une autre couleur qui superpose le bleu.
			robot.tourneG90();
			Actionneurs.rouler();
			Delay.msDelay(200);
			Actionneurs.stop_roues();
			robot.tourneG90();
		}
		Actionneurs.rouler();
		Delay.msDelay(1500);
		Actionneurs.stop_roues();
		robot.tourneD90();
		
		while(capteur.dist() > 0.25) {
			robot.avancerVers("noir");
			Actionneurs.rouler();
			Delay.msDelay(500);
			Actionneurs.stop_roues();
			robot.tourneD90();
			Actionneurs.rotateCounterClockwise(70);
			while(!capteur.isPressed() && capteur.dist() >=0.25) {
				Actionneurs.rouler();
			}
			if(capteur.dist() < 0.25)
				continue;
			Actionneurs.stop_roues();
			actionneur.mvt_pince(-1500);
			actionneur.tourneD90();
			robot.allerAuBut2();
			// meme probleme
			robot.tourneD90();
			Actionneurs.rouler();
			Delay.msDelay(30);
			Actionneurs.stop_roues();
			robot.tourneD90();
		}
		
		robot.tourneG90();
		
		while(Capteurs.rvb() != "rouge") {
			robot.avancerVers("bleu");
			robot.tourneG90();
			while(!capteur.isPressed() && Capteurs.rvb() != "rouge") {
				Actionneurs.rouler();
			}
			if(Capteurs.rvb() == "rouge")
				continue;
			Actionneurs.stop_roues();
			actionneur.mvt_pince(-1500);
			robot.tourneG90();
			robot.allerAuBut2();
			//meme probleme
			robot.tourneG90();
			Actionneurs.rouler();
			Delay.msDelay(30);
			robot.tourneG90();
		}
	}
}