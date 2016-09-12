
import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.util.Delay;

public class Robot {
	
	private SensorManager sensorManager;
	
	public Robot() {
		sensorManager = new SensorManager(0.1);
		Motor.C.setSpeed(100);
		Motor.B.setSpeed(100);
	}

	public void startMoveForward() {
		Motor.C.forward();
		Motor.B.forward();
	}
	
	public void stopMove() {
		Motor.C.stop();
		Motor.B.stop();
	}
	
	public void findLine() {
		startMoveForward();
		while(!sensorManager.isUndergroundBlack()) {
			//move
		}
		stopMove();
	}
	
	public void turnLeft(int degree) {
		//TODO
	}
	
	public void turnRight(int degree) {
		//TODO
	}
	
	public void calibrationProgress() {
		LCD.drawString("Please calibrate black path", 0, 0);
		LCD.drawString("Press ENTER to save", 0, 2);
		while(!Button.ENTER.isDown()) {
			LCD.drawString("light: " + sensorManager.getCurrentValue(), 0, 1);
			Delay.msDelay(500);
		}
		int calibratedBlack = sensorManager.calibrateBlack();
		LCD.drawString("Black value: " + calibratedBlack, 0, 0);
		LCD.drawString("Tolerance: " + sensorManager.getTolerance(), 0, 1);
		LCD.drawString("Lets go", 0, 2);
	}
	
	

	public static void main(String[] args) {
		Robot robot = new Robot();
		
		robot.calibrationProgress();
		Delay.msDelay(1000);
		waitForPress();
		
		robot.findLine();
		
	}

	private static void waitForPress() {
		while(!Button.ENTER.isDown()) {
			//run
		}
	}
}