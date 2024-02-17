// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.lib.controllers.PlasmaJoystick;
import frc.robot.subsystems.Climb;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Photon;
import frc.robot.subsystems.Shooter;



/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  // subsystems
  Intake intake = new Intake();
  Climb climb = new Climb();
  Shooter shooter = new Shooter();
  Photon photon = new Photon();

  Compressor compressor;

  PlasmaJoystick driver = new PlasmaJoystick(Constants.RobotConstants.driverJoystickID);

  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    compressor = new Compressor(21, PneumaticsModuleType.REVPH);  
    compressor.enableDigital();
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    intake.periodic();
    climb.periodic();
    shooter.periodic();
    photon.periodic();
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {


    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {

  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    //creep mode
    if(driver.LT.isPressed()) {

    }
    //normal drive
    else{

    }

    // setting controls for intake
    if(driver.RT.isPressed()) {
      intake.setState(Intake.intakeState.INTJECT);
    }
    else if(driver.B.isPressed()) {
      intake.setState(Intake.intakeState.EJECT);
    }
    else{
      intake.setState(Intake.intakeState.STOW);
    }

    // setting controls for climb
    if(driver.dPad.getPOV() == 0) {
      climb.setState(Climb.climbState.UP);
    }
    else if(driver.dPad.getPOV() == 180) {
      climb.setState(Climb.climbState.DOWN);
    }
    else{
        climb.setState(Climb.climbState.NOTMOVING);
    }

    //setting up controls for shooter
    if(driver.LB.isPressed()) {
      shooter.setState(Shooter.shooterState.ON);
    }
    else if(driver.Y.isPressed()) {
      shooter.setState(Shooter.shooterState.AMP);;
    }
    else {
      shooter.setState(Shooter.shooterState.OFF);
    }

    
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}
