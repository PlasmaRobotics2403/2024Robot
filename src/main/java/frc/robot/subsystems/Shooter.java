package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.CANSparkLowLevel.MotorType;
import frc.robot.Constants.ShooterConstants;;

public class Shooter {
    private CANSparkMax shooterMotor1;
    private CANSparkMax shooterMotor2;
    private CANSparkMax ampMotor;

    private shooterState currentState;
    public enum shooterState {
        ON,
        OFF,
        AMP
    }

    /**
     * constructer for shooter
     */
    public Shooter() {

        shooterMotor1 = new CANSparkMax(ShooterConstants.shooterMotor1ID, MotorType.kBrushless);
        shooterMotor2 = new CANSparkMax(ShooterConstants.shooterMotor2ID, MotorType.kBrushless);
        ampMotor = new CANSparkMax(ShooterConstants.ampMotorID, MotorType.kBrushless);


        currentState = shooterState.OFF;
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

    public void logging() {
        SmartDashboard.putNumber("Shooter Speed", shooterMotor1.get());
    }

    public void periodic() {
        logging();

        switch (currentState) {
            case ON:
                runShooter(ShooterConstants.shooterSpeed);
                break;

            case AMP:
                runAmp(0.1);
                break;
        
            case OFF:
                runShooter(0);
                break;
                
        }
    }

}
