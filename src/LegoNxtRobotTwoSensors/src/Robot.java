
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Delay;

public class Robot {

	private SensorManager sensorManagerLeft;
	private SensorManager sensorManagerRight;
	private int movingspeed;
	private int turningspeed;
	private int lowerSpeed;
	private double difference = 0.15;
	private UltrasonicSensor ultraSonic;

	public Robot() {
		ultraSonic = new UltrasonicSensor(SensorPort.S2);
		ultraSonic.continuous();
		sensorManagerLeft = new SensorManager(0.2, SensorPort.S4);
		sensorManagerRight = new SensorManager(0.2, SensorPort.S1);
		movingspeed = 350;
		turningspeed = 150;
		lowerSpeed = 130;
		Motor.C.setSpeed(movingspeed);
		Motor.B.setSpeed(movingspeed);
	}

	public void startMoveForward() {
		Motor.C.forward();
		Motor.B.forward();
	}

	public void startMoveBackward() {
		Motor.C.setSpeed(turningspeed);
		Motor.B.setSpeed(turningspeed);
		Motor.C.backward();
		Motor.B.backward();
	}

	public void stopMove() {
		Motor.C.stop();
		Motor.B.stop();
		Motor.C.resetTachoCount();
		Motor.B.resetTachoCount();
	}

	public void turnLeftDegree(int degree) {
		Motor.C.setSpeed(turningspeed);
		Motor.B.setSpeed(turningspeed);
		Motor.C.rotateTo(-degree, true);
		Motor.B.rotateTo(degree, true);
		while (Motor.B.isMoving() && Motor.C.isMoving()) {

		}
		stopMove();
	}

	public void turnRightDegree(int degree) {
		Motor.C.setSpeed(turningspeed);
		Motor.B.setSpeed(turningspeed);
		Motor.C.rotateTo(degree, true);
		Motor.B.rotateTo(-degree, true);
		while (Motor.B.isMoving() && Motor.C.isMoving()) {

		}
		stopMove();
	}

	public int turnLeft(int maxDegree) {
		Motor.C.resetTachoCount();
		Motor.B.resetTachoCount();
		Motor.C.setSpeed(turningspeed);
		Motor.B.setSpeed(turningspeed);
		Motor.C.rotateTo(-maxDegree, true);
		Motor.B.rotateTo(maxDegree, true);
		while (sensorManagerLeft.getCurrentValue() <= 46 && sensorManagerRight.getCurrentValue() <= 46
				&& Motor.B.isMoving() && Motor.C.isMoving()) {
			// turning
		}
		int tachoCount = Motor.B.getTachoCount();
		stopMove();
		Motor.C.setSpeed(movingspeed);
		Motor.B.setSpeed(movingspeed);
		return tachoCount;
	}

	public int turnRight(int maxDegree) {
		Motor.C.resetTachoCount();
		Motor.B.resetTachoCount();
		Motor.C.setSpeed(turningspeed);
		Motor.B.setSpeed(turningspeed);
		Motor.C.rotateTo(maxDegree, true);
		Motor.B.rotateTo(-maxDegree, true);
		while (sensorManagerLeft.getCurrentValue() <= 46 && sensorManagerRight.getCurrentValue() <= 46
				&& Motor.B.isMoving() && Motor.C.isMoving()) {
			// turning
		}
		int tachoCount = Motor.C.getTachoCount();
		stopMove();
		Motor.C.setSpeed(movingspeed);
		Motor.B.setSpeed(movingspeed);
		return tachoCount;
	}

	public void calibrationProgress() {
		LCD.drawString("Please calibrate black path", 0, 0);
		LCD.drawString("Press ENTER", 0, 3);
		while (!Button.ENTER.isDown()) {
			LCD.drawString("left: " + sensorManagerLeft.getCurrentValue() + "              ", 0, 1);
			LCD.drawString("right: " + sensorManagerRight.getCurrentValue() + "             ", 0, 2);
			Delay.msDelay(500);
		}
		int calibratedBlackLeft = sensorManagerLeft.calibrateBlack();
		int calibratedBlackRight = sensorManagerRight.calibrateBlack();
		LCD.drawString("Black left: " + calibratedBlackLeft + "               ", 0, 0);
		LCD.drawString("Black right: " + calibratedBlackRight + "               ", 0, 1);
		LCD.drawString("Tolerance: " + sensorManagerLeft.getTolerance() + "          ", 0, 2);
		LCD.drawString("Difference: " + difference + "          ", 0, 3);
		waitForPress();
	}

	private void followLine() {
		int maxDegree = 300;

		while (true) {
			if (sensorManagerLeft.getCurrentValue() >= 46 && sensorManagerRight.getCurrentValue() >= 46) {
				stopMove();
				startMoveBackward();
				while (sensorManagerLeft.getCurrentValue() > 46 && sensorManagerRight.getCurrentValue() > 46) {
					// go back
				}
				int left = turnLeft(maxDegree);
				LCD.drawString("left: " + left + "         ", 0, 7);
				if (left < 50) {
					turnRightDegree(2*left);
					turnRight(maxDegree);
				}
			}
			
			while ((sensorManagerLeft.getCurrentValue() < 46 || sensorManagerRight.getCurrentValue() < 46)
					&& sensorManagerLeft.getCurrentValue() - sensorManagerRight.getCurrentValue() > 0) {
				// go right
				Motor.C.setSpeed(movingspeed);
				Motor.C.forward();
				Motor.B.setSpeed(lowerSpeed);
				Motor.B.forward();
				Delay.msDelay(50);
			}
			while ((sensorManagerLeft.getCurrentValue() < 46 || sensorManagerRight.getCurrentValue() < 46)
					&& sensorManagerLeft.getCurrentValue() - sensorManagerRight.getCurrentValue() < 0) {
				// go left
				Motor.B.setSpeed(movingspeed);
				Motor.B.forward();
				Motor.C.setSpeed(lowerSpeed);
				Motor.C.forward();
				Delay.msDelay(50);
			}
			while(ultraSonic.getDistance() < 35) {
				stopMove();
				Sound.systemSound(true, 4);
			}
		}
	}

	private void waitForPress() {
		Delay.msDelay(1000);
		while (!Button.ENTER.isDown()) {
			// wait
		}
		Delay.msDelay(1000);
	}

	public static void main(String[] args) {
		Robot robot = new Robot();

		// while(true) {
		// LCD.drawString("left: " + robot.sensorManagerLeft.getCurrentValue(),
		// 0, 0);
		// LCD.drawString("right: " +
		// robot.sensorManagerRight.getCurrentValue(), 0, 1);
		// Delay.msDelay(200);
		// }

		robot.calibrationProgress();

		robot.followLine();
	}

}