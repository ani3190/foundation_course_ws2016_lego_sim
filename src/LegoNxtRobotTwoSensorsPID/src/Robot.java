import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.util.Delay;

public class Robot {

	private LightSensor lightSensorLeft;
	private LightSensor lightSensorRight;
	private long straightCounter = 0;

	public void followLine() {
		lightSensorLeft = new LightSensor(SensorPort.S4);
		lightSensorRight = new LightSensor(SensorPort.S1);

		Motor.C.forward();
		Motor.B.forward();

		int oldDiff = 0;
		int speed = 100;
		int addSpeed = 0;

		while (true) {

			int sensorLeft = lightSensorLeft.readValue();
			int sensorRight = lightSensorRight.readValue();

			int diff = sensorLeft - sensorRight;

//			if (Math.abs(diff) > 7) {
//				addSpeed = -9 * Math.abs(diff);
//			}

//			addSpeed = -8 * Math.abs(diff);

			if (Math.abs(diff) < 2 && addSpeed < 150) {
				addSpeed += 10;
			}
			if (Math.abs(diff) > 5 && addSpeed > -35) {
				addSpeed -= 20;
			}
			
			diff *= 4;
			diff += 0.7 * oldDiff;

			int motorLeft = speed + addSpeed + diff;
			int motorRight = speed + addSpeed - diff;

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

			Delay.msDelay(20);
			oldDiff = diff;
		}
	}

	public static void main(String[] args) {
		Robot robot = new Robot();
		robot.followLine();
	}
}
