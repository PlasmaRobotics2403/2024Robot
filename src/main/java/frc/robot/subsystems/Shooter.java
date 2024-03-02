package frc.robot.subsystems;

import java.sql.Driver;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.MotionMagicVelocityVoltage;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.CANSparkLowLevel.MotorType;
import frc.robot.Constants.ShooterConstants;;

public class Shooter {
    Photon photon;

    private TalonFX shooterMotor1;
    private TalonFX shooterMotor2;
    private CANSparkMax ampMotor;
    private TalonFX rotMotor;


    private double testAngle;

    private TalonFXConfiguration shooterRotConfigs;

    private shooterState currentState;
    public enum shooterState {
        OFF,
        RPS,
        PERCENT,
        TEST,
        AMP,
        STATICSHOOT,
        CLIMB
    }

    /**
     * constructer for shooter
     */
    public Shooter(Photon photon) {
        this.photon = photon;

        shooterMotor1 = new TalonFX(ShooterConstants.shooterMotor1ID);
        shooterMotor2 = new TalonFX(ShooterConstants.shooterMotor2ID);
        ampMotor = new CANSparkMax(ShooterConstants.ampMotorID, MotorType.kBrushless);
        rotMotor = new TalonFX(ShooterConstants.rotMotorID);

        // shooter velocity motion magic config
        var shooterVelocityConfigs = new TalonFXConfiguration();

        var velocitySlot0Configs = shooterVelocityConfigs.Slot0;
        velocitySlot0Configs.kS = ShooterConstants.shooterVelocityKS;
        velocitySlot0Configs.kV = ShooterConstants.shooterVelocityKV;
        velocitySlot0Configs.kA = ShooterConstants.shooterVelocityKA;
        velocitySlot0Configs.kP = ShooterConstants.shooterVelocityKP;
        velocitySlot0Configs.kI = ShooterConstants.shooterVelocityKI;
        velocitySlot0Configs.kD = ShooterConstants.shooterVelocityKD;

        var velocityMotionMagicConfigs = shooterVelocityConfigs.MotionMagic;
        velocityMotionMagicConfigs.MotionMagicAcceleration = ShooterConstants.shooterVelocityAccel;    //rps/s
        velocityMotionMagicConfigs.MotionMagicJerk = ShooterConstants.shooterVelocityJerk;             //rps/s/s

        shooterMotor1.getConfigurator().apply(shooterVelocityConfigs);
        shooterMotor2.getConfigurator().apply(shooterVelocityConfigs);

        // pivot motion magic configuration
        shooterRotConfigs = new TalonFXConfiguration();
        var pivotSlot0Configs = shooterRotConfigs.Slot0;

        pivotSlot0Configs.kS = ShooterConstants.shooterPivotKS;
        pivotSlot0Configs.kV = ShooterConstants.shooterPivotKV;
        pivotSlot0Configs.kP = ShooterConstants.shooterPivotKP;
        pivotSlot0Configs.kD = ShooterConstants.shooterPivotKD;

        var pivotMotionMagicConfigs = shooterRotConfigs.MotionMagic;
        pivotMotionMagicConfigs.MotionMagicCruiseVelocity = ShooterConstants.shooterPivotVel;    //rps
        pivotMotionMagicConfigs.MotionMagicAcceleration = ShooterConstants.shooterPivotAccel;    //rps/s
        pivotMotionMagicConfigs.MotionMagicJerk = ShooterConstants.shooterPivotJerk;             //rps/s/s
        rotMotor.getConfigurator().apply(shooterRotConfigs);

        rotMotor.setNeutralMode(NeutralModeValue.Brake);
        rotMotor.setPosition(0);

        currentState = shooterState.OFF;
        testAngle = 0;
    }

    private void runRPS(double rps) {
        final MotionMagicVelocityVoltage r_request = new MotionMagicVelocityVoltage(0);
        r_request.Acceleration = 0;
        r_request.EnableFOC = true;

        shooterMotor1.setControl(r_request.withVelocity(rps));
        shooterMotor2.setControl(r_request.withVelocity(rps));

        //DriverStation.reportWarning(shooterMotor1.get, false);
    }

    private void runMotionMagicAngle(double pos) {
        if(pos>105||pos<0) {
            DriverStation.reportWarning("DONT DO THAT", true);
            rotMotor.set(0);
        }
        else{
            final MotionMagicVoltage m_request = new MotionMagicVoltage(0);
            rotMotor.setControl(m_request.withPosition(pivotSetpointCalc(pos)));
        }
    }

    private double pivotSetpointCalc(double angle) {
        return angle*ShooterConstants.angleConversion;
    }

    private double photonAngle() {
        return photon.calAngle();
    }

    public void runAmp(double speed) {
        ampMotor.set(speed);
    }

    /**
     * sets speed for shooter
     * @param speed
     */
    private void runShooter(double speed) {
        shooterMotor1.set(speed);
        shooterMotor2.set(speed);
    }

    private void rotateShooter(double speed) {  
        rotMotor.set(speed);
    }  

    /**
     * sets the intake state
     * @param state
     */
    public void setState(shooterState state) {
        currentState = state;
    }
    /**
     * gets the current intake state
     * @return currentState
     */
    public shooterState getState() {
        return currentState;
    }

    public double[] getShooterVel() {
        double[] shooterVelocities = {
            shooterMotor1.getVelocity().getValueAsDouble(),
            shooterMotor2.getVelocity().getValueAsDouble()};

        return shooterVelocities;
    }

    public boolean readyToShoot(double desiredRPM) {
        return readyToShoot(desiredRPM, photonAngle());
    }

    public boolean readyToShoot(double desiredRPM, double desiredAngle) {
        double[] shooterVelocities = getShooterVel();
        double angle = rotMotor.getRotorPosition().getValueAsDouble()*ShooterConstants.rotationConversion;

        boolean velocitysGood = shooterVelocities[0] > desiredRPM && shooterVelocities[1] > desiredRPM;
        boolean anlgeGood = (angle > desiredAngle-2 && angle < desiredAngle+2);

        return velocitysGood && anlgeGood;
    }



    private void logging() {
        SmartDashboard.putNumber("Shooter Speed", shooterMotor1.get());
        SmartDashboard.putNumber("Shooter Angle", rotMotor.getRotorPosition().getValueAsDouble()*ShooterConstants.rotationConversion);
        testAngle = (Double) SmartDashboard.getNumber("Test Angle", 0.0);
        SmartDashboard.putNumber("Test Angle", testAngle);
        SmartDashboard.putString("Shooter State", currentState.toString());

    }

    public void periodic() {
        logging();

        switch (currentState) {

            case OFF:
                runShooter(0);
                runAmp(0);
                runMotionMagicAngle(0);
                break;
            case CLIMB:
                runMotionMagicAngle(ShooterConstants.climbPos);
            case RPS:
                runRPS(ShooterConstants.shooterRPS);
                runMotionMagicAngle(photonAngle());
                break;
            case STATICSHOOT:
                runRPS(ShooterConstants.shooterRPS);
                runMotionMagicAngle(ShooterConstants.pos);
                break;
            case PERCENT:
                runShooter(ShooterConstants.shooterSpeed);
                break;
            case TEST:
                runRPS(ShooterConstants.shooterRPS);
                runMotionMagicAngle(testAngle);
                break;
            case AMP:
                runRPS(ShooterConstants.ampRPS);
                runMotionMagicAngle(ShooterConstants.ampAngle);
        }
    }

}
