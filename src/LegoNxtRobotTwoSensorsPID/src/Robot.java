import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.SoundSensor;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Delay;

public class Robot {

	private LightSensor lightSensorLeft;
	private LightSensor lightSensorRight;
	private UltrasonicSensor ultraSonicSensor;
	private TouchSensor touchSensor;

	public void followLine() {
		lightSensorLeft = new LightSensor(SensorPort.S4);
		lightSensorRight = new LightSensor(SensorPort.S1);
		ultraSonicSensor = new UltrasonicSensor(SensorPort.S2);
		touchSensor = new TouchSensor(SensorPort.S3);

		Motor.C.forward();
		Motor.B.forward();

		int oldDiff = 0;
		int speed = 150; // 150
		int addSpeed = 0;
		int straightCounter = 0;

		while (true) {

			LCD.drawString("pressed " + touchSensor.isPressed(), 1, 1);

			if (ultraSonicSensor.getDistance() < 15) {
				Motor.C.stop(true);
				Motor.B.stop();
				oldDiff = 0;
				addSpeed = 0;
				Delay.msDelay(500);
				continue;
			}

			if (touchSensor.isPressed()) {
				Motor.C.stop(true);
				Motor.B.stop();
				oldDiff = 0;
				addSpeed = 0;
				Delay.msDelay(500);
				continue;
			}

			int sensorLeft = lightSensorLeft.readValue();
			int sensorRight = lightSensorRight.readValue();

			int diff = sensorLeft - sensorRight;

			if (Math.abs(diff) < 2 && addSpeed < 330) {
				if (straightCounter++ > 8) {
					addSpeed += 20;
				}
			}
			if (Math.abs(diff) > 4 && addSpeed > -40) { // -40
				straightCounter = 0;
				addSpeed -= 30;
			}

			diff *= 2.8; // 3
			diff += 0.83 * oldDiff; // 75
			diff += 3.2 * (diff - oldDiff); // 3.2

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
