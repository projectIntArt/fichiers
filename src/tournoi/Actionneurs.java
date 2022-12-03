package tournoi;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.utility.Delay;

public class Actionneurs {
	
	protected static EV3LargeRegulatedMotor roueDroite;
	protected static EV3LargeRegulatedMotor roueGauche;
	protected EV3MediumRegulatedMotor clamp;
	
	public Actionneurs(Port A, Port B, Port C, Port S2, Port S3, Port S4){
		roueDroite = new EV3LargeRegulatedMotor(MotorPort.A);
		roueGauche = new EV3LargeRegulatedMotor(MotorPort.C);
		clamp = new EV3MediumRegulatedMotor(MotorPort.B);
	}
	
	public static void rouler(){
		roueDroite.startSynchronization();
		roueDroite.synchronizeWith(new EV3LargeRegulatedMotor[]{roueGauche});
		roueGauche.forward();
		roueDroite.forward();
		roueDroite.endSynchronization();
	}
	
	public void reculer(){
		roueDroite.startSynchronization();
		roueDroite.synchronizeWith(new EV3LargeRegulatedMotor[]{roueGauche});
		roueGauche.backward();
		roueDroite.backward();
		roueDroite.endSynchronization();
	}
	
    public static void rotateClockwise(int angle)
    {
    	roueDroite.rotate(-angle);
    }
    
    public static void rotateCounterClockwise(int angle)
    {    
    	roueDroite.rotate(angle);
    }
    
	public static void stop_roues() {
		roueDroite.startSynchronization();
		roueDroite.synchronizeWith(new EV3LargeRegulatedMotor[]{roueGauche});
		roueGauche.stop();
		roueDroite.stop();
		roueDroite.endSynchronization();
	}

	public void close_roues() {
		roueGauche.close();
		roueDroite.close();
	}
	
	public void mvt_pince(int angle) {
		clamp.rotate(angle);
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

}
