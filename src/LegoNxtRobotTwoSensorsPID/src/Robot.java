import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Delay;

public class Robot {

	private LightSensor lightSensorLeft;
	private LightSensor lightSensorRight;
	private UltrasonicSensor ultraSonicSensor;
	private TouchSensor touchSensor;
	private int straightCounter = 0;
	private int oldDiff = 0;
	private int addSpeed = 0;

	//Adjust the following parameters -->
	private int speed = 150;
	private double p_value = 3;
	private double i_value = 0.75;
	private double d_value = 3.2;
	private int delay = 20;
	private int ultrasonicMinDistance = 15;
	private int goFastMaxDifference = 2;
	private int goSlowMinDifference = 4;
	private int maxSpeedAddition = 330;
	private int minSpeedAddition = -40;
	private int minStraightCounter = 8;
	private int acceleration = 20;
	private int deacceleration = -30;
	//<--
	
	public Robot() {
		lightSensorLeft = new LightSensor(SensorPort.S4);
		lightSensorRight = new LightSensor(SensorPort.S1);
		ultraSonicSensor = new UltrasonicSensor(SensorPort.S2);
		touchSensor = new TouchSensor(SensorPort.S3);
	}

	public void followLine() {

		Motor.C.forward();
		Motor.B.forward();

		while (true) {

			if (checkRestrictions()) {
				stopMovement();
				continue;
			}

			int sensorLeft = lightSensorLeft.readValue();
			int sensorRight = lightSensorRight.readValue();

			int diff = sensorLeft - sensorRight;

			adjustSpeed(diff);

			diff *= p_value;
			diff += i_value * oldDiff;
			diff += d_value * (diff - oldDiff);

			int motorLeft = speed + addSpeed + diff;
			int motorRight = speed + addSpeed - diff;

			moveRobot(motorLeft, motorRight);

			Delay.msDelay(delay);
			oldDiff = diff;
		}
	}
	
	private boolean checkRestrictions() {
		return ultraSonicSensor.getDistance() < ultrasonicMinDistance || touchSensor.isPressed();
	}

	private void adjustSpeed(int diff) {
		if (Math.abs(diff) < goFastMaxDifference && addSpeed < maxSpeedAddition) {
			if (straightCounter++ > minStraightCounter) {
				addSpeed += acceleration;
			}
		}
		if (Math.abs(diff) > goSlowMinDifference && addSpeed > minSpeedAddition) {
			straightCounter = 0;
			addSpeed += deacceleration;
		}
	}

	private void stopMovement() {
		Motor.C.stop(true);
		Motor.B.stop();
		oldDiff = 0;
		addSpeed = 0;
		Delay.msDelay(500);
	}

	private void moveRobot(int motorLeft, int motorRight) {
		if (motorLeft < 0) {
			Motor.C.backward();
		} else {
			Motor.C.forward();
		}
		Motor.C.setSpeed(motorLeft);

		if (motorRight < 0) {
			Motor.B.backward();
		} else {
			Motor.B.forward();
		}
		Motor.B.setSpeed(motorRight);
	}

	public static void main(String[] args) {
		Robot robot = new Robot();
		robot.followLine();
	}
}
