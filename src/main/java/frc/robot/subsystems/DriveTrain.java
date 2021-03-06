/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import java.util.Date;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;




public class DriveTrain extends SubsystemBase {
    
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
  public static AHRS gyro = new AHRS(I2C.Port.kMXP);
  private CANSparkMax frontLeft = new CANSparkMax(Constants.FRONT_LEFT_CAN_ID, MotorType.kBrushless);
  private CANSparkMax backLeft = new CANSparkMax(Constants.BACK_LEFT_CAN_ID, MotorType.kBrushless);
  private CANSparkMax frontRight = new CANSparkMax(Constants.FRONT_RIGHT_CAN_ID, MotorType.kBrushless);
  private CANSparkMax backRight = new CANSparkMax(Constants.BACK_RIGHT_CAN_ID, MotorType.kBrushless);

  // following variable are used in turnToHeading and driveAlongAngle
  final double MaxDriveSpeed = 0.3;//was .15
  final double MaxTurnSpeed = 0.25;
  public final int EncoderUnitsPerFeet = 14500;

  public DriveTrain(){
    configureMotors();
    resetPosition();
    gyro.calibrate();
  }

  int frameCount = 0;
  int bufferWidth = 25;
  double[] frontLeftBuffer = new double[bufferWidth];
  double[] backLeftBuffer = new double[bufferWidth];
  double[] frontRightBuffer = new double[bufferWidth];
  double[] backRightBuffer = new double[bufferWidth];

  double getArrayAverage(double[] array) {
    double sum = 0;
    for (int i = 0; i < array.length; i++) {
        sum += array[i];
    }
    return sum / array.length;
  }

  //Date date = new Date();
  //long lastTime = date.getTime();
  //long lastTime = System.currentTimeMillis();
  //double sumOfSum = 0;

  @Override
  public void periodic() {
    frameCount++;
    //frontLeftBuffer[frameCount % bufferWidth] = frontLeft.getOutputCurrent();
    //backLeftBuffer[frameCount % bufferWidth] = backLeft.getOutputCurrent();
    //frontRightBuffer[frameCount % bufferWidth] = frontRight.getOutputCurrent();
    //backRightBuffer[frameCount % bufferWidth] = backRight.getOutputCurrent();


    // This method will be called once per scheduler run
    SmartDashboard.putNumber("Actual Gyro Heading: ", gyro.getAngle());
    SmartDashboard.putNumber("Acual Drive Position: ", getPosition());
    //SmartDashboard.putNumber("Front left current", getArrayAverage(frontLeftBuffer));
    //SmartDashboard.putNumber("Back left current ", getArrayAverage(backLeftBuffer));
    //SmartDashboard.putNumber("Front right current  ", getArrayAverage(frontRightBuffer));
    //SmartDashboard.putNumber("Back right current ", getArrayAverage(backRightBuffer));

    //SmartDashboard.putNumber("Front left PDH", RobotContainer.pdh.getCurrent(Constants.FRONT_LEFT_PDH_PORT));
    //SmartDashboard.putNumber("Back left PDH ", RobotContainer.pdh.getCurrent(Constants.BACK_LEFT_PDH_PORT));
    //SmartDashboard.putNumber("Front right PDH  ", RobotContainer.pdh.getCurrent(Constants.FRONT_RIGHT_PDH_PORT));
    //SmartDashboard.putNumber("Back right PDH ", RobotContainer.pdh.getCurrent(Constants.BACK_RIGHT_PDH_PORT));
    //double sum = (RobotContainer.pdh.getCurrent(Constants.FRONT_LEFT_PDH_PORT) + RobotContainer.pdh.getCurrent(Constants.BACK_LEFT_PDH_PORT) + RobotContainer.pdh.getCurrent(Constants.FRONT_RIGHT_PDH_PORT) + RobotContainer.pdh.getCurrent(Constants.BACK_RIGHT_PDH_PORT));
    //sum *= (double)(date.getTime() - lastTime);
   // sum *= (double)(System.currentTimeMillis() - lastTime);
    //sum *= (1/50);
    //sumOfSum += sum;

   // SmartDashboard.putNumber("Ah", sumOfSum/1000/60/60);
    //SmartDashboard.putNumber("Ah", sumOfSum);

    if (frameCount == 10) {
      frameCount = 0;
      SmartDashboard.putNumber("Front left SPARK", frontLeft.getOutputCurrent());
      SmartDashboard.putNumber("Back left SPARK", backLeft.getOutputCurrent());
      SmartDashboard.putNumber("Front right SPARK", frontRight.getOutputCurrent());
      SmartDashboard.putNumber("Back right SPARK", backRight.getOutputCurrent());

      SmartDashboard.putNumber("Front left PDH", RobotContainer.pdh.getCurrent(Constants.FRONT_LEFT_PDH_PORT));
      SmartDashboard.putNumber("Back left PDH", RobotContainer.pdh.getCurrent(Constants.BACK_LEFT_PDH_PORT));
      SmartDashboard.putNumber("Front right PDH", RobotContainer.pdh.getCurrent(Constants.FRONT_RIGHT_PDH_PORT));
      SmartDashboard.putNumber("Back right PDH", RobotContainer.pdh.getCurrent(Constants.BACK_RIGHT_PDH_PORT));
    }
    //lastTime = date.getTime();
    //lastTime = System.currentTimeMillis();
  }

  /**
   * Drives the robot using the arcade drive style,
   * @param drive is "speed" to move forward (positive) or backward (negative)
   * @param steer is "amount" to turn right (positive) or left (negative)
   * best to pass in normalized variables from 1 to -1 
   */
  public void arcadeDrive(double drive, double steer) {
     //if steer and drive are both too low, stop the motors and end
     if ((Math.abs(drive) <= 0.05) && (Math.abs(steer) <= 0.05)) {
      stop();
      return;
    }

    double leftSpeed = drive + steer;
    double rightSpeed = drive - steer;
    
     
   
     frontLeft.set(leftSpeed);
     frontRight.set(rightSpeed);
  }

    /**
	  * Stops all drivetrain wheels.
	  */
  public void stop() {
    frontLeft.set(0);
    frontRight.set(0);
  }

  //functions to support 
  public void driveAlongAngle(double distance, double alongAngle)
  {
    int direction = (distance > 0) ? 1 : -1;
    driveAlongAngle(Math.abs(distance), direction, alongAngle);
  }

  public void driveAlongAngle(double distanceInFeet, int direction, double alongAngle)
  {
    double kF = 0.1;  //kF is essentially minimal amount to drive
    double kP = 0.75;
    double tolerance = 100; // this would not be roughly 1 inch

    double angleKP = .005; //this is not .006
    
    double driveSpeed;
    double turnSpeed = 0.0;
    double distanceError = distanceInFeet * EncoderUnitsPerFeet * direction;    
    double endPosition = getPosition() + distanceError;

    double angleError = alongAngle - getGyroAngle();
    
   // this code can be uncommented if we want to make sure we turn to Heading first
   // if (Math.abs(angleError) > 1) {
   //   turnToHeading(alongAngle);
   // }
   SmartDashboard.putNumber("Current distanceError", distanceError);

      while (Math.abs(distanceError) > tolerance){

        driveSpeed = distanceError / EncoderUnitsPerFeet / 5 * kP + Math.copySign(kF,distanceError);
        // make sure we go no faster than MaxDriveSpeed
        driveSpeed = ((Math.abs(driveSpeed) > MaxDriveSpeed) ? Math.copySign(MaxDriveSpeed, driveSpeed) :  driveSpeed);
        angleError = alongAngle + getGyroAngle();
        turnSpeed = angleError * angleKP;
        // make sure turnSpeed is not greater than MaxTurnSpeed
        turnSpeed = ((Math.abs(turnSpeed) > MaxTurnSpeed ? Math.copySign(MaxTurnSpeed, angleError): turnSpeed));
        arcadeDrive(driveSpeed, turnSpeed);
        distanceError = endPosition + getPosition();
        SmartDashboard.putNumber("Current distanceError", distanceError);
      }
    
    stop();
  }

  public void turnToHeading(double intendedHeading) {  
    double kF = 0.05;
    double kP = 0.02; 
    double angleError;
    double turnSpeed;
    double tolerance = 3;

    angleError = intendedHeading - getGyroAngle();
    while (Math.abs(angleError) > tolerance) {    
        turnSpeed = angleError * kP + Math.copySign(kF, angleError);
        // make sure turnSpeed is not greater than MaxTurnSpeed
        turnSpeed = ((Math.abs(turnSpeed) > MaxTurnSpeed ? Math.copySign(MaxTurnSpeed, angleError): turnSpeed));
        arcadeDrive(0, turnSpeed);
        angleError = intendedHeading + getGyroAngle();
      }

    stop();
  }


  /**
	 * Gets the angle of drive train from its initial position.
	 * @return	a double containing the drive train's current heading
	 */
	public double getGyroAngle() {
		return gyro.getAngle();
	}
	
	/**
	 * Resets the drive train's gyroscope position to the zero value.
	 */
	public void resetGyro() {
		gyro.reset();
	}
  
  /**
	 * Gets the current position of the drive train 
	 * @return	a double containing the drive train's current position;
   *                        as an average of left and right position.
	 */
	public double getPosition() {
      return (0/*frontLeft.getSelectedSensorPosition()+frontRight.getSelectedSensorPosition())/2*/); 
  }

  // NOTE THIS FUNCTION CALL IS NON-BLOCKING; TRY TO AVOID USING
  public void resetPosition() {
    //frontLeft.setSelectedSensorPosition(0, 0, 50); 
    //frontRight.setSelectedSensorPosition(0, 0, 50); 
  }

  
/*  POSITION CONTROL DRIVING UNUSED CURRENTLY
  public void drivePositionControl(double distanceInEncoderValues)
  {
    System.out.println("Starting at left position: " +frontLeft.getSelectedSensorPosition());
    System.out.println("Starting at right position: " +frontRight.getSelectedSensorPosition());
    frontLeft.set(ControlMode.Position, frontLeft.getSelectedSensorPosition()+distanceInEncoderValues);
    frontRight.set(ControlMode.Position, frontRight.getSelectedSensorPosition()+distanceInEncoderValues);
//    Timer.delay(2);
//    When we take out this timer delay, we get odd behavior.  Won't work, one motor will move and the other 
//  won't, etc.
    System.out.println("Ending at left position: " +frontLeft.getSelectedSensorPosition());
    System.out.println("Ending at right position: " +frontRight.getSelectedSensorPosition());
  }
*/

   // public void simpleDriveForward(double distanceInFeet) {
   // double distanceInEncoderUnits = distanceInFeet * EncoderUnitsPerFeet; 
   // drivePositionControl(distanceInEncoderUnits);  
 // }

 public void setPosition(int distance){
  //frontLeft.set(ControlMode.Position,distance);
  //sfrontRight.set(ControlMode.Position,distance);
 }


  private void configureMotors() {

    double rampRate = 0.1875; //time in seconds to go from 0 to full throttle; Lower this number and tune current limits
    int currentLimit = 30;
    //currentLimitThreshold represents the current that the motor needs to sustain for the currentLimitThresholdTime to then be limited to the currentLimit
    int currentLimitThreshold = 35; 
    double currentLimitThresholdTime = 1.0;
  

    frontLeft.restoreFactoryDefaults();
    frontRight.restoreFactoryDefaults();
    backLeft.restoreFactoryDefaults();
    backRight.restoreFactoryDefaults();


    gyro.enableLogging(false);

    //Enables motors to follow commands sent to front and left 
    
    backLeft.follow(frontLeft);
    backRight.follow(frontRight);
  
    /*
    frontLeft.configOpenloopRamp(rampRate);
    backLeft.configOpenloopRamp(rampRate);
    frontRight.configOpenloopRamp(rampRate);
    backRight.configOpenloopRamp(rampRate);
    */
    frontLeft.setInverted(false);
    backLeft.setInverted(false);
    frontRight.setInverted(true);
    backRight.setInverted(true);

    frontLeft.setIdleMode(IdleMode.kBrake);
    backLeft.setIdleMode(IdleMode.kBrake);
    frontRight.setIdleMode(IdleMode.kBrake);
    backRight.setIdleMode(IdleMode.kBrake);
    /*
    frontLeft.setNeutralMode(NeutralMode.Brake);
    backLeft.setNeutralMode(NeutralMode.Brake);
    frontRight.setNeutralMode(NeutralMode.Brake);
    backRight.setNeutralMode(NeutralMode.Brake);

    
    frontLeft.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, currentLimit, currentLimitThreshold, currentLimitThresholdTime));
    backLeft.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, currentLimit, currentLimitThreshold, currentLimitThresholdTime));
    frontRight.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, currentLimit, currentLimitThreshold, currentLimitThresholdTime));
    backRight.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, currentLimit, currentLimitThreshold, currentLimitThresholdTime));
    */




// no current limit set on drivetrain    
// int currentLimit = 30; //int because .setSmartCurrentLimit takes only ints, not doubles. Which makes sense programmatically. 
//    frontLeft.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, currentLimit, triggerThresholdCurrent, triggerThresholdTime));

  //Use if we start to do drive by POSITION Closed Loop
   double kF = .00070;
    double kP = 0.0032;
    //double kI = 0;
    //double kD = 0;
    /*
    frontLeft.config_kP(0, kP);
    frontRight.config_kP(0, kP);
    frontLeft.config_kF(0, kF);
    frontRight.config_kF(0, kF);
    */

  //  frontLeft.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, 0);
  //  frontRight.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, 0);


  }
}