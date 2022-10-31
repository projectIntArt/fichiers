package tournoi;

import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MeanFilter;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public class Capteurs {
	
	private static EV3TouchSensor touch;
	private static EV3UltrasonicSensor ultrason;
	private static EV3ColorSensor color;
	private static float[] bleu = {0.024506f, 0.03412f, 0.0586f};
	private static float[] rouge = {0.14334f, 0.0292f, 0.02098f};
	private static float[] vert = {0.0604f, 0.10274f, 0.0357f};
	private static float[] noir = {0.02296f, 0.02294f, 0.0198f};
	private static float[] jaune = {0.27098f, 0.19198f, 0.04724f};
	private static float[] blanc = {0.30706f, 0.24374f, 0.19784f};
	private static float[] gris = {0.10136f, 0.09136f, 0.07946f};
	
	public Capteurs(Port S2, Port S3, Port S4){
		touch = new EV3TouchSensor(SensorPort.S2);
		ultrason = new EV3UltrasonicSensor(SensorPort.S4);
		color = new EV3ColorSensor(SensorPort.S3);
	}
	
	public boolean isPressed() {
		SampleProvider sp_touch = touch.getTouchMode();
		float [] sample = new float[1];
        sp_touch.fetchSample(sample, 0);
        return (int)sample[0] == 1;
	}
	
	public float dist() {
		SampleProvider sp_dist = ultrason.getDistanceMode();
		float[] sample = new float[sp_dist.sampleSize()];
		sp_dist.fetchSample(sample, 0);
		return sample[0];
	}
	
	public double scalaire(float[] v1, float[] v2) {
		return Math.sqrt (Math.pow(v1[0] - v2[0], 2.0) +
				Math.pow(v1[1] - v2[1], 2.0) +
				Math.pow(v1[2] - v2[2], 2.0));
	}
	
	public String rvb() {
		SampleProvider rvb = new MeanFilter(color.getRGBMode(), 1);
		float[] sample = new float[3];
		rvb.fetchSample(sample, 0);
		double dist_min = Double.MAX_VALUE;
		String color = "";
		
		if(scalaire(sample, bleu) < dist_min) {
			dist_min = scalaire(sample, bleu);
			color = "bleu";
		}
		if(scalaire(sample, rouge) < dist_min) {
			dist_min = scalaire(sample, rouge);
			color = "rouge";
		}
		if(scalaire(sample, vert) < dist_min) {
			dist_min = scalaire(sample, vert);
			color = "vert";
		}
		if(scalaire(sample, noir) < dist_min) {
			dist_min = scalaire(sample, noir);
			color = "noir";
		}
		if(scalaire(sample, jaune) < dist_min) {
			dist_min = scalaire(sample, jaune);
			color = "jaune";
		}
		if(scalaire(sample, blanc) < dist_min) {
			dist_min = scalaire(sample, blanc);
			color = "blanc";
		}
		if(scalaire(sample, gris) < dist_min) {
			dist_min = scalaire(sample, gris);
			color = "gris";
		}
		return color;
	}

}
