import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.util.Delay;

public class Robot {

	private int black;
	private int white;
	private LightSensor lightSensorLeft;
	private LightSensor lightSensorRight;
	
	public void followLine() {
		lightSensorLeft = new LightSensor(SensorPort.S4);
		lightSensorRight = new LightSensor(SensorPort.S1);
		
		white = lightSensorLeft.readValue();
		black = lightSensorLeft.readValue();
		
		Motor.C.forward();
		Motor.B.forward();
		
		while(true) {
			
			updateBlackWhite();
			
			int sensorLeft = lightSensorLeft.readValue();
			int sensorRight = lightSensorRight.readValue();
			
			int diff = sensorLeft - sensorRight;
			
			if(Math.abs(diff) < 3) {
				diff = 0;
			}
			
			int speed = 250;
			diff *= Math.abs(diff) * speed / 270;
			//diff *= 250 / 30;
			
			int motorLeft = speed + diff;
			int motorRight = speed - diff;
			
			Motor.C.setSpeed(motorLeft);
			Motor.B.setSpeed(motorRight);
			
			Delay.msDelay(50);
		}
	}
	
	public void updateBlackWhite() {
		if(lightSensorLeft.readValue() > white) {
			white = lightSensorLeft.readValue();
		}
		if(lightSensorLeft.readValue() < black) {
			black = lightSensorLeft.readValue();
		}
		
		if(lightSensorRight.readValue() > white) {
			white = lightSensorRight.readValue();
		}
		if(lightSensorRight.readValue() < black) {
			black = lightSensorRight.readValue();
		}
	}

	public static void main(String[] args) {
		Robot robot = new Robot();
		robot.followLine();
	}
}
