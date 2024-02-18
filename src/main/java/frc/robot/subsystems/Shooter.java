package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.controls.StaticBrake;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.CANSparkLowLevel.MotorType;
import frc.robot.Constants.ShooterConstants;;

public class Shooter {
    private CANSparkMax shooterMotor1;
    private CANSparkMax shooterMotor2;
    private CANSparkMax ampMotor;
    private TalonFX rotMotor;

    private TalonFXConfiguration shooterRotConfigs;

    private shooterState currentState;
    private boolean rotUp;
    public enum shooterState {
        ON,
        OFF,
        AMP,
        ROT,
        CLIMB

    }

    /**
     * constructer for shooter
     */
    public Shooter() {

        shooterMotor1 = new CANSparkMax(ShooterConstants.shooterMotor1ID, MotorType.kBrushless);
        shooterMotor2 = new CANSparkMax(ShooterConstants.shooterMotor2ID, MotorType.kBrushless);
        ampMotor = new CANSparkMax(ShooterConstants.ampMotorID, MotorType.kBrushless);
        rotMotor = new TalonFX(ShooterConstants.rotMotorID);

        // motion magic configuration
        shooterRotConfigs = new TalonFXConfiguration();
        var slot0Configs = shooterRotConfigs.Slot0;

        slot0Configs.kS = ShooterConstants.shooterPivotKS;
        slot0Configs.kV = ShooterConstants.shooterPivotKV;
        slot0Configs.kP = ShooterConstants.shooterPivotKP;
        slot0Configs.kD = ShooterConstants.shooterPivotKD;

        var motionMagicConfigs = shooterRotConfigs.MotionMagic;
        motionMagicConfigs.MotionMagicCruiseVelocity = ShooterConstants.shooterPivotVel;    //rps
        motionMagicConfigs.MotionMagicAcceleration = ShooterConstants.shooterPivotAccel;    //rps/s
        motionMagicConfigs.MotionMagicJerk = ShooterConstants.shooterPivotJerk;             //rps/s/s

        rotMotor.getConfigurator().apply(shooterRotConfigs);
        rotMotor.setNeutralMode(NeutralModeValue.Brake);
        rotMotor.setPosition(0);

        currentState = shooterState.OFF;
        rotUp = true;
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

    public void runMotionMagic(double pos) {
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
        SmartDashboard.putNumber("Shooter Angle", rotMotor.getRotorPosition().getValueAsDouble());
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
                    rotateShooter(ShooterConstants.shooterRotSpeed);
                }
                //runMotionMagic();
                break;

            case CLIMB:
                runMotionMagic(ShooterConstants.pos);
                break;
        
            case OFF:
                runShooter(0);
                runAmp(0);
                runMotionMagic(0);
                break;
                
        }
    }

}
