package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.MotionMagicVelocityVoltage;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.controls.StaticBrake;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.fasterxml.jackson.databind.introspect.ConcreteBeanPropertyBase;
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
    private TalonFXConfiguration shooterVelocityConfigs;

    private shooterState currentState;
    private boolean rotUp;
    public enum shooterState {
        ON,
        OFF,
        AMP,
        ROT,
        CLIMB,
        RPS,
        TEST

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
        shooterVelocityConfigs = new TalonFXConfiguration();
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
        shooterMotor1.getConfigurator().apply(velocityMotionMagicConfigs);
        shooterMotor2.getConfigurator().apply(velocityMotionMagicConfigs);

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
        rotUp = true;
        testAngle = 0;
    }

    public void runRPS(double rps) {
        final MotionMagicVelocityVoltage r_request = new MotionMagicVelocityVoltage(0);
        r_request.Acceleration = 0;
        r_request.EnableFOC = false;

        shooterMotor1.setControl(r_request.withVelocity(rps));
        shooterMotor2.setControl(r_request.withVelocity(rps));

        //DriverStation.reportWarning(shooterMotor1.get, false);
    }

    public void runMotionMagicAngle(double pos) {
        if(pos>90||pos<0) {
            DriverStation.reportWarning("DONT DO THAT", true);
            rotMotor.set(0);
        }
        else{
            final MotionMagicVoltage m_request = new MotionMagicVoltage(0);
            rotMotor.setControl(m_request.withPosition(pivotSetpointCalc(pos)));
        }
    }

    public double pivotSetpointCalc(double angle) {
        return angle*ShooterConstants.angleConversion;
    }

    public double photonAngle() {
        return photon.calAngle();
    }

    public void runAmp(double speed) {
        ampMotor.set(speed);
    }

    /**
     * sets speed for shooter
     * @param speed
     */
    public void runShooter(double speed) {
        shooterMotor1.set(speed);
        shooterMotor2.set(speed);
    }

    public void rotateShooter(double speed) {
        rotMotor.set(speed);
    }

    public void runMotionMagicRotations(double pos) {
        final MotionMagicVoltage request = new MotionMagicVoltage(0);
        rotMotor.setControl(request.withPosition(pos));
    }

  

    /**
     * sets the intake state
     * @param state
     */
    public void setState(shooterState state) {
        currentState = state;
    }

    public void setDirection(boolean dir) {
        rotUp = dir;
    }

    public boolean getDirection() {
        return rotUp;
    }
    /**
     * gets the current intake state
     * @return currentState
     */
    public shooterState getState() {
        return currentState;
    }



    public void logging() {
        SmartDashboard.putNumber("Shooter Speed", shooterMotor1.get());
        SmartDashboard.putNumber("Shooter Angle", rotMotor.getRotorPosition().getValueAsDouble()*ShooterConstants.angleConversion);
        testAngle = (Double) SmartDashboard.getNumber("Test Angle", 0.0);
        SmartDashboard.putNumber("Test Angle", testAngle);
    }

    public void periodic() {
        logging();

        switch (currentState) {
            case ON:
                runShooter(ShooterConstants.shooterSpeed);
                break;

            case AMP:
                runAmp(0.1);
                //runMotionMagic(ShooterConstants.ampPos);
                break;

            case ROT:
                if(getDirection()){
                    rotateShooter(ShooterConstants.shooterRotSpeed);
                }
                else {
                    rotateShooter(-ShooterConstants.shooterRotSpeed);
                }
                //runMotionMagic();
                break;

            case CLIMB:
                /*if(photonAngle() > 25 || photonAngle() < 0){
                    DriverStation.reportWarning("Photon angle to high or low", false);
                    //runMotionMagic(0);
                }*/
                
                runMotionMagicAngle(photonAngle());
                
                break;
            case RPS:
                runRPS(ShooterConstants.shooterRPS);
                break;

            case TEST:
                runMotionMagicAngle(testAngle);
                break;
        
            case OFF:
                runShooter(0);
                runAmp(0);
                runMotionMagicAngle(0);
                break;
                
        }
    }

}
