package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.IndexConstants;

public class Index {

    public TalonFX indexer; 
    public DigitalInput shooterSensor;
    public TalonFXConfiguration configs;
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

        // current limiting
        configs = new TalonFXConfiguration();

        configs.CurrentLimits.StatorCurrentLimit = 60;
        configs.CurrentLimits.StatorCurrentLimitEnable = true;

        indexer.getConfigurator().apply(configs);

        //initializes the indxer state
        currentState = indexState.OFF;
    }

    /**
     * runs the indexer 
     * @param speed
     */
    private void runIndex(double speed) {
        DutyCycleOut request = new DutyCycleOut(speed);
        request.EnableFOC = true;
        indexer.setControl(request);
        
    }

    /**
     * checks if the index sensor is realised
     * @return
     */
    public boolean  getShooterSensor() {
        return sensorState;
    }

    /**
     * sets the indexer state
     * @param state
     */
    public void setState(indexState state) {
        currentState = state;
    }

    /**
     * gets the indexer state
     * @return
     */
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
