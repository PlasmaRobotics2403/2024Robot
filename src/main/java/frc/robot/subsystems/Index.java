package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.IndexConstants;

public class Index {

    public CANSparkMax indexer;
    public DigitalInput shooterSensor;

    private indexState currentState;
    public enum indexState{
        INTAKE,
        EJECT,
        SHOOT,
        PASSTHROUGH,
        OFF
    }
   public Index() {

        indexer = new CANSparkMax(IndexConstants.passthroughID, MotorType.kBrushless);
        shooterSensor  = new DigitalInput(IndexConstants.shooterSensorID);

        indexer.setInverted(false);
        indexer.setIdleMode(IdleMode.kBrake);
        currentState = indexState.OFF;
    }

    private void runIndex(double speed) {
        indexer.set(speed);
    }


    public boolean getShooterSensor() {
        return !shooterSensor.get();
    }

    public void setState(indexState state) {
        currentState = state;
    }

    public indexState getState() {
        return currentState;
    }

    private void logging() {
        SmartDashboard.putBoolean("getShooterSensor", getShooterSensor());
        SmartDashboard.putString("Index State", currentState.toString());

    }

    public void periodic() {
        logging();
        
        switch (currentState) {
            case INTAKE:
                runIndex(IndexConstants.passthroughSpeed);
                break;
            case EJECT:
                runIndex(-IndexConstants.passthroughSpeed);
                break;
            case SHOOT:
                runIndex(IndexConstants.passthroughSpeed);
                break;
            case PASSTHROUGH:
                runIndex(IndexConstants.indexSpeed);
                break;
            case OFF:
                runIndex(0);
                break;
        }
    }
}
