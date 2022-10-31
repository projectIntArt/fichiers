package tournoi;

import java.util.ArrayList;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.utility.Delay;
import lejos.hardware.port.SensorPort;


public class Agent extends Actionneurs{ 
	

	private static EV3LargeRegulatedMotor roueDroite;
	private static EV3LargeRegulatedMotor roueGauche;
	private EV3MediumRegulatedMotor clamp;
	public static int speed = 400;
	public static int rot_speed = 200;
	public static int angleOriente = 0;
	private static float dist_critique = 0.326f;
	private static boolean palet = false;
	
	public Agent(Port A, Port B, Port C, Port S2, Port S3, Port S4) {
		super(A, B, C, S2, S3, S4);
		roueGauche.setSpeed(speed);
		roueDroite.setSpeed(speed);
		clamp.setSpeed(800);
	}
	
	public void allerVersPalet() {  // utiliser des linkedlist ?
		ArrayList<Float> distances = new ArrayList<Float>(); // par defaut, la liste est de taille 10
		float d = 0;
		float diff = 0;
		distances.add(dist());
		boolean abandon = false;
		while(!(palet || abandon)){
			while(diff < 0.1 || distances.get(distances.size()) > 0.32) {
				System.out.println("avance vers objet");
				this.rouler();
				d = dist();
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
				while(!isPressed()) {
					System.out.println("palet detecte");
					this.rouler();
				}
				this.mvt_pince(-800);
				palet = true;
			}
			
			else if(seuil > 0.35) { // on a perdu un palet : balayage
				int c = 0;
				distances.add(dist());
				speed = 100;
				while(distances.get(distances.size()-1) > seuil + 0.05 || c == 100) {
					System.out.println("palet perdu");
					roueDroite.rotate(5);
					distances.add(dist());
					c += 5;
				}
				c = 0;
				distances.add(dist());
				while(distances.get(distances.size()-1) > seuil + 0.05 || c == -200) {
					System.out.println("palet perdu");
					roueDroite.rotate(-5);
					distances.add(dist());
					c -= 5;
				}
				if(c == -200) 
					abandon = true;
				speed = 300;
			}
			
			for(int k = 0; k < distances.size(); k++) {
				distances.set(k, (float)0.0);
			}  // revoir ca ? peut etre tout supprimer au lieu de remplacer par des 0
		}
	}
	
	public void allerVersPalet2(){
		while(!isPressed()) {
			System.out.println("palet detecte");
			this.rouler();
		}
		this.stop_roues();
		this.mvt_pince(-800);
		palet = true;
	}
	
	public void AllerAuBut(){
	    double diff = 0;
	    ArrayList<Float> distances = new ArrayList<Float>();
	   // angleOriente = roueDroite.getLimitAngle();
	    if(angleOriente < 0)
	    	this.rotateClockwise(angleOriente);
	    else 
	        this.rotateCounterClockwise(-angleOriente);
	    while(rvb() != "blanc"){
	        this.rouler();
	    	float d = dist();
	        if(d < 0.4){
	        	distances.add(d);
	        	while(diff < 0.1 && rvb() != "blanc"){
	        		this.rouler();
		            distances.add(dist());
		            diff = distances.get(distances.size()-1) - distances.get(distances.size()-2);
	        	}
	        	if(rvb() != "blanc") {
	        		this.stop_roues();
		        	this.rotateClockwise(200);
		        	this.rouler();
		        	Delay.msDelay(700);
		        	this.stop_roues();
		        	this.rotateCounterClockwise(200);
		        	diff = 0;
	        	}
	        }
	    }
	    this.stop_roues();
	    // une fois qu’on est sorti du grand while, on ouvre les pinces
	    this.mvt_pince(800);
	    this.reculer();
	    Delay.msDelay(200);
	    this.stop_roues();

	// puis on fait une rotation de 90 degrés avant d’effectuer une recherche de palet
	    this.rotateClockwise(390); // quart de tour
	    angleOriente = roueDroite.getLimitAngle();
	}
	
	public void ChercherPalet(){
        ArrayList<Float> distances = new ArrayList<Float>();
        ArrayList<Float> distCritique = new ArrayList<Float>();
        double diff = 0;
        distances.add(dist());
        int i = 0;
        while(i < 172){  // avant 780
        	i++;      // avant +5 
        	//this.rotateClockwise(5);
        	Agent.roueDroite.backward();
        	Delay.msDelay(10);
        	distances.add(dist());
            if(Math.abs(diff) > 0.3) 
               distCritique.add(distances.get(distances.size()-1));
            diff = distances.get(distances.size()-1) - distances.get(distances.size()-2);
        }
        int angle_or = 1710;
        double min = distCritique.get(0);
        for(int j = 1; j<distCritique.size(); j++)
              if(distCritique.get(j) < min)
                 min = distCritique.get(j);
//        this.stop_roues();
//        System.out.println("nb de dist critiques : " + distCritique.size());
//        System.out.println("dist min : " + min);
//        Button.ENTER.waitForPressAndRelease();
        while(!(dist() > min -0.05 && dist() < min +0.05)) {
        	Agent.roueDroite.forward();
        	Delay.msDelay(3);
        	angle_or -= 3;
        }
        Agent.roueDroite.forward();
        Delay.msDelay(angle_or);
        this.stop_roues();
	}
	
	public static void main(String[] args) {
		
		
		
		Agent robot = new Agent(MotorPort.A, MotorPort.B, MotorPort.C, SensorPort.S2, SensorPort.S3, SensorPort.S4);
		
//		roueGauche.setSpeed(rot_speed);
//		roueDroite.setSpeed(rot_speed);
//		while(dist()>0.5) {
//			robot.rotateClockwise();
//		}
//		robot.rotateClockwise();
//		Delay.msDelay(150);
//		roueGauche.setSpeed(speed);
//		roueDroite.setSpeed(speed);
//		while(isPressed() == false) {
//			robot.rouler();	
//		}
//		robot.mvt_pince(-800);
//		while(rvb() != "blanc") {
//			robot.rouler();
//		}
//		robot.stop_roues();
//		robot.mvt_pince(800);
		
//		roueGauche.setSpeed(speed);
//		roueDroite.setSpeed(speed);
//		float[] dist = new float[1000];
//		dist[0] = dist();
//		robot.rouler();
//		dist[1] = dist();
//		int i = 1;
//		while(dist[i] <= dist[i-1]) {
//			robot.rouler();
//			dist[i+1] = dist();
//			i++;
//		}
//		robot.stop_roues();
//		System.out.println(dist[i+1]);
//		Button.ENTER.waitForPressAndRelease();
		
		// Un tour 
//		roueGauche.setSpeed(100);
//		roueDroite.setSpeed(100);
//		robot.rotateClockwise();
//		Delay.msDelay(7580);
		
//		roueDroite.rotate(360);
//		int pif = roueDroite.getLimitAngle();
//		roueDroite.rotate(-pif);
//		roueDroite.rotate(720);
//		int pof = roueDroite.getLimitAngle();
//		roueDroite.rotate(-pof);
		
		robot.mvt_pince(800);
		//robot.allerVersPalet2();
	
//		robot.allerVersPalet2();
//		robot.AllerAuBut();
		
//		robot.ChercherPalet();
//		angleOriente = roueDroite.getLimitAngle();
//		if(angleOriente < 0)
//			robot.rotateClockwise(angleOriente);
//	    else 
//	        robot.rotateCounterClockwise(-angleOriente);
		
//		robot.ChercherPalet();
//		robot.allerVersPalet2();
//		robot.AllerAuBut();
//		
	}
	
}
