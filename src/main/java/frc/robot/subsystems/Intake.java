package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeConstants;

public class Intake extends SubsystemBase{
    
    private CANSparkMax roller;

    private intakeState currentState;
    private DigitalInput sensor;
    private boolean sensorState;
    public enum intakeState {
        INTJECT,
        EJECT,
        STOW
        
    }
    private DoubleSolenoid intakeSolenoid;
    /**
     * constructor for intake
     */
    public Intake() {
        // roller config
        roller = new CANSparkMax(IntakeConstants.rollerID, MotorType.kBrushless);
        roller.setIdleMode(IdleMode.kBrake);
        roller.setInverted(true);
        intakeSolenoid = new DoubleSolenoid(21, PneumaticsModuleType.REVPH, IntakeConstants.forwardChannelID, IntakeConstants.backwardChannelID);
        sensor = new DigitalInput(0);

        currentState = intakeState.STOW;
    }

    public boolean getSensor() {
        return sensorState;
    }
    /**
     * sets the speed of the roller motor
     */
    private void runIntake(double speed) {
        roller.set(speed);
    }

    /**
     * sets the intake state
     * @param state
     */
    public void setState(intakeState state) {
        currentState = state;
    }
    /**
     * gets the current intake state
     * @return currentState
     */
    public intakeState getState() {
        return currentState;
    }

    private void extendIntake() {
        intakeSolenoid.set(DoubleSolenoid.Value.kForward);
    }

    private void retractIntake() {
        intakeSolenoid.set(DoubleSolenoid.Value.kReverse);
    }

        /**
     * periodiclly logs information to
     * smartdashboard
     */
    private void logging() {
        SmartDashboard.putNumber("Intake Roller Speed", roller.get());
        SmartDashboard.putString("Intake State", currentState.toString());
        SmartDashboard.putBoolean("intake beam brake", getSensor());
    }

    public void periodic() {
        logging();

        sensorState = !sensor.get();
        switch (currentState) {
            case STOW:
                runIntake(0);
                retractIntake();
                break;

            case INTJECT:
                extendIntake();
                runIntake(IntakeConstants.rollerSpeed);
                break;
                
            case EJECT:
                extendIntake();
                runIntake(-IntakeConstants.rollerSpeed);
                break;
        }
    }
}
