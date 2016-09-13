import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;

public class SensorManager {

	private LightSensor sensor;
	private int calibratedBlack;
	private double tolerance;

	public SensorManager(double tolerance, SensorPort sensorPort) {
		sensor = new LightSensor(sensorPort);
		this.tolerance = tolerance;
	}
	
	public int getCurrentValue() {
		return sensor.readValue();
	}
	
	public double getTolerance() {
		return tolerance;
	}

	public int calibrateBlack() {
		calibratedBlack = sensor.readValue();
		return calibratedBlack;
	}

	public boolean isUndergroundBlack() {
		int value = sensor.readValue();
		return tolerance * calibratedBlack > Math.abs(value - calibratedBlack);
	}

}
