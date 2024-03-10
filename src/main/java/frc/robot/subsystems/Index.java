package frc.robot.subsystems;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.IndexConstants;

public class Index {

    public TalonFX indexer; 
    public DigitalInput shooterSensor;

    public boolean sensorState;

    private indexState currentState;
    public enum indexState{
        INTAKE,
        EJECT,
        SHOOT,
        PASSTHROUGH,
        OFF
    }
   public Index() {

        indexer = new TalonFX(IndexConstants.passthroughID);
        shooterSensor  = new DigitalInput(IndexConstants.shooterSensorID);

        indexer.setInverted(false);
        indexer.setNeutralMode(NeutralModeValue.Brake);
        //TODO set current limiting on indexer motor

        currentState = indexState.OFF;
    }

    private void runIndex(double speed) {
        DutyCycleOut request = new DutyCycleOut(speed);
        request.EnableFOC = true;
        indexer.setControl(request);
        
    }


    public boolean  getShooterSensor() {
        return sensorState;
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
        sensorState = !shooterSensor.get();
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
