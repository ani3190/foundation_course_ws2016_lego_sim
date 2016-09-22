
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.util.Delay;

public class Robot {

	private SensorManager sensorManager;
	private int movingspeed;
	private int turningspeed;

	public Robot() {
		sensorManager = new SensorManager(0.2);
		movingspeed = 350;
		turningspeed = 150;
		Motor.C.setSpeed(movingspeed);
		Motor.B.setSpeed(movingspeed);
	}

	public void startMoveForward() {
		Motor.C.forward();
		Motor.B.forward();
	}

	public void stopMove() {
		Motor.C.stop();
		Motor.B.stop();
		Motor.C.resetTachoCount();
		Motor.B.resetTachoCount();
	}

	public void findLine() {
		startMoveForward();
		while (!sensorManager.isUndergroundBlack()) {
			// move
		}
		stopMove();
	}

	public void turnLeft(int maxDegree) {
		Motor.C.setSpeed(turningspeed);
		Motor.B.setSpeed(turningspeed);
		Motor.C.rotateTo(-maxDegree, true);
		Motor.B.rotateTo(maxDegree, true);
		while (!sensorManager.isUndergroundBlack() && Motor.C.isMoving() && Motor.B.isMoving()) {
			// turning
		}
		stopMove();
		Motor.C.setSpeed(movingspeed);
		Motor.B.setSpeed(movingspeed);
	}

	public void turnRight(int maxDegree) {
		Motor.C.setSpeed(turningspeed);
		Motor.B.setSpeed(turningspeed);
		Motor.C.rotateTo(maxDegree, true);
		Motor.B.rotateTo(-maxDegree, true);
		while (!sensorManager.isUndergroundBlack() && Motor.C.isMoving() && Motor.B.isMoving()) {
			// turning
		}
		stopMove();
		Motor.C.setSpeed(movingspeed);
		Motor.B.setSpeed(movingspeed);
	}

	public void calibrationProgress() {
		LCD.drawString("Please calibrate black path", 0, 0);
		LCD.drawString("Press ENTER", 0, 2);
		while (!Button.ENTER.isDown()) {
			LCD.drawString("light: " + sensorManager.getCurrentValue(), 0, 1);
			Delay.msDelay(500);
		}
		int calibratedBlack = sensorManager.calibrateBlack();
		LCD.drawString("Black value: " + calibratedBlack + "               ", 0, 0);
		LCD.drawString("Tolerance: " + sensorManager.getTolerance() + "          ", 0, 1);
		waitForPress();
	}

	private void followLine() {
		int maxDegree = 180;
		boolean lastTurnLeft = true;
		while (!Button.ENTER.isDown()) {
			startMoveForward();
			while (sensorManager.isUndergroundBlack()) {
				// run
			}
			// Sound.systemSound(true, 0);
			stopMove();

			// Delay.msDelay(1000);

			if (lastTurnLeft) {
				turnLeft(maxDegree);
			} else {
				turnRight(maxDegree);
			}

			// Delay.msDelay(1000);

			if (!sensorManager.isUndergroundBlack()) {
				// Sound.systemSound(true, 1);
				if (lastTurnLeft) {
					turnRight(2 * maxDegree);
				} else {
					turnLeft(2 * maxDegree);
				}
				if (sensorManager.isUndergroundBlack()) {
					lastTurnLeft = !lastTurnLeft;
				} else {
					break;
				}
			} else {
				// Sound.systemSound(true, 4);
			}
			// Delay.msDelay(1000);
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

		robot.calibrationProgress();

		robot.findLine();

		robot.followLine();
	}

}