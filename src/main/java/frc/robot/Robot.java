// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.lib.autoUtil.AutoMode;
import frc.lib.autoUtil.AutoModeRunner;
import frc.lib.controllers.PlasmaJoystick;
import frc.robot.StateManager.robotState;
import frc.robot.auto.modes.DriveAndTurn;
import frc.robot.auto.modes.DriveY;
import frc.robot.auto.modes.FourNear;
import frc.robot.auto.modes.DriveX;
import frc.robot.auto.modes.Nothing;
import frc.robot.auto.modes.Shoot;
import frc.robot.auto.modes.Spin;
import frc.robot.auto.modes.ThreeFar;
import frc.robot.auto.modes.ThreeNear;
import frc.robot.auto.modes.TwoCenter;
import frc.robot.auto.modes.TwoFar;
import frc.robot.auto.modes.TwoNear;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.Climb;
import frc.robot.subsystems.Index;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.LEDs;
import frc.robot.subsystems.Photon;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Swerve;



/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  // subsystems
  Intake intake = new Intake();
  Index index = new Index();
  LEDs leds = new LEDs();
  int hue = 0;
  Swerve swerve = new Swerve(TunerConstants.DrivetrainConstants,
    TunerConstants.FrontRight,
    TunerConstants.FrontLeft,
    TunerConstants.BackLeft, 
    TunerConstants.BackRight);

  Climb climb = new Climb(swerve.getPigeon());
  Photon photon = new Photon();
  Shooter shooter = new Shooter(photon);
  StateManager stateManager = new StateManager(intake, shooter, index, climb, leds, photon, swerve);

  AutoModeRunner autoModeRunner = new AutoModeRunner();
  AutoMode[] autoModes = new AutoMode[20];

  Compressor compressor;

  boolean connectedToDriverStation = DriverStation.isDSAttached();
  PlasmaJoystick driver = new PlasmaJoystick(Constants.RobotConstants.driverJoystickID);

  private AutoMode m_autoSelected;
  private final SendableChooser<AutoMode> m_chooser = new SendableChooser<>();

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    /* initialize autos */
    for (int i = 0; i < 20; i++) {
      autoModes[i] = new Nothing();
    }

    /* wait until robot connects to FMS */
    
    do {
      Timer.delay(.30);
      connectedToDriverStation = DriverStation.isDSAttached();
    }
     while(!connectedToDriverStation);

    autoModes[1] = new DriveAndTurn(swerve);
    autoModes[2] = new DriveY(swerve);
    autoModes[3] = new DriveX(swerve);
    autoModes[4] = new Spin(swerve);
    autoModes[5] = new TwoCenter(swerve, stateManager);
    autoModes[6] = new TwoFar(swerve, stateManager, photon);
    autoModes[7] = new TwoNear(swerve, stateManager, photon);
    autoModes[8] = new ThreeNear(swerve, stateManager, photon);
    autoModes[9] = new ThreeFar(swerve, stateManager, photon);
    autoModes[10] = new FourNear(swerve, stateManager, photon);
    autoModes[11] = new Shoot(stateManager, photon);
    

    m_chooser.setDefaultOption("Nothing Auto", autoModes[0]); 
    m_chooser.addOption("Shoot", autoModes[11]);
    m_chooser.addOption("Middle Auto (2)", autoModes[5]);
    m_chooser.addOption("Far Auto (2)", autoModes[6]);
    m_chooser.addOption("Near Auto (2)", autoModes[7]);
    m_chooser.addOption("Near Auto (3)", autoModes[8]);
    m_chooser.addOption("Far Auto (3)", autoModes[9]);
    m_chooser.addOption("Near Auto (4)", autoModes[10]);

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
    index.periodic();
    leds.periodic();
    stateManager.periodic();

    connectedToDriverStation = DriverStation.isDSAttached();
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
    System.out.println("Auto selected: " + m_autoSelected.toString());

    autoModeRunner.chooseAutoMode(m_autoSelected);
    autoModeRunner.start();   
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {

  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    autoModeRunner.stop();
    stateManager.setState(robotState.IDLE);
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    //creep mode
    if(driver.LT.isPressed()) {
      swerve.driveFieldCentric(
          new ChassisSpeeds(
              driver.LeftY.getFilteredAxis()*Constants.SwerveConstants.creepSpeed,
              -driver.LeftX.getFilteredAxis()*Constants.SwerveConstants.creepSpeed,
              -driver.RightX.getFilteredAxis()*Constants.SwerveConstants.creepTurn*Constants.SwerveConstants.maxAngularRate));
    }
    // align to target
    else if(driver.LB.isPressed()) {
      swerve.driveFieldCentric(
          new ChassisSpeeds(
              driver.LeftY.getFilteredAxis()*Constants.SwerveConstants.maxSpeed,
              -driver.LeftX.getFilteredAxis()*Constants.SwerveConstants.maxSpeed,
              photon.alignToTarget()));
    }
    //normal drive
    else{
      swerve.driveFieldCentric(
          new ChassisSpeeds(
              driver.LeftY.getFilteredAxis()*Constants.SwerveConstants.maxSpeed,
              -driver.LeftX.getFilteredAxis()*Constants.SwerveConstants.maxSpeed,
              -driver.RightX.getFilteredAxis()*Constants.SwerveConstants.maxAngularRate));
    }

    // reset heading
    if (driver.BACK.isPressed()) {

      swerve.zeroHeading();
    }

    // setting controls for intake

    if(driver.RB.isPressed()) {
      stateManager.setState(robotState.SHOOT);
    }
    else if(driver.X.isPressed()) {
      stateManager.setState(robotState.STATICSHOOT);
    }
    else if(driver.B.isPressed()) {
      stateManager.setState(robotState.EJECT);
    }
    else if(driver.RT.isPressed()) {
      stateManager.setState(robotState.INTAKE);
    }
    else if(driver.A.isPressed()) {
      stateManager.setState(robotState.AMP);
    }
    else if(driver.dPad.getPOV() == 0) {
      stateManager.setState(robotState.CLIMB_HOOKS_UP);
    }
    else if(driver.dPad.getPOV() == 180) {
      stateManager.setState(robotState.CLIMB_HOOKS_DOWN);
    }
    else if(driver.Y.isPressed()) {
      stateManager.setState(robotState.CLIMBFALSE);
    }
    else{
      stateManager.setState(robotState.IDLE);
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
