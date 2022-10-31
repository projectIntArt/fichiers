package tournoi;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;

public class Actionneurs extends Capteurs {
	
	private static EV3LargeRegulatedMotor roueDroite;
	private static EV3LargeRegulatedMotor roueGauche;
	private EV3MediumRegulatedMotor clamp;
	
	public Actionneurs(Port A, Port B, Port C, Port S2, Port S3, Port S4){
		super(S2, S3, S4);
		roueDroite = new EV3LargeRegulatedMotor(MotorPort.A);
		roueGauche = new EV3LargeRegulatedMotor(MotorPort.C);
		clamp = new EV3MediumRegulatedMotor(MotorPort.B);
	}
	
	public void rouler(){
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
	
    public void rotateClockwise(int angle)
    {
    	roueDroite.rotate(-angle);
    }
    
    public void rotateCounterClockwise(int angle)
    {    
    	roueDroite.rotate(angle);
    }
    
	public void stop_roues() {
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

}
