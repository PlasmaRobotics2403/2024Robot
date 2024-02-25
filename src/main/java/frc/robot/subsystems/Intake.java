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
    private CANSparkMax passthrough;
    private DigitalInput passthroughBeamBreak;

    private intakeState currentState;
    public enum intakeState {
        INTJECT,
        EJECT,
        STOW,
        INDEX,
        SHOOT
        
    }
    private DoubleSolenoid intakeSolenoid;
    /**
     * constructor for intake
     */
    public Intake() {
        // roller config
        passthroughBeamBreak = new DigitalInput(0);
        roller = new CANSparkMax(IntakeConstants.rollerID, MotorType.kBrushless);
        passthrough = new CANSparkMax(IntakeConstants.passthroughID, MotorType.kBrushless);
        roller.setIdleMode(IdleMode.    kBrake);
        roller.setInverted(true);
        passthrough.setInverted(false);
        passthrough.setIdleMode(IdleMode.kBrake);
        currentState = intakeState.STOW;
        intakeSolenoid = new DoubleSolenoid(21, PneumaticsModuleType.REVPH, IntakeConstants.forwardChannelID, IntakeConstants.backwardChannelID);
    }

    /**
     * sets the speed of the roller motor
     */
    public void runIntake(double speed) {
        roller.set(speed);
    }

    public void runPassthrough(double speed) {
        passthrough.set(speed);
    }

    public boolean getBeamBreak() {
        return !passthroughBeamBreak.get();
    }

    /**
     * periodiclly logs information to
     * smartdashboard
     */
    public void logging() {
        SmartDashboard.putNumber("Intake Roller Speed", roller.get());
        SmartDashboard.putBoolean("Beam Break", getBeamBreak());
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

    public void extendIntake() {
        intakeSolenoid.set(DoubleSolenoid.Value.kForward);
    }

    public void retractIntake() {
        intakeSolenoid.set(DoubleSolenoid.Value.kReverse);
    }

    
    public void periodic() {
        logging();

        switch (currentState) {
            case INTJECT:
            if(getBeamBreak()) {
                runIntake(0);
                intakeSolenoid.set(DoubleSolenoid.Value.kReverse);
                runPassthrough(0);
            }
            else{
                intakeSolenoid.set(DoubleSolenoid.Value.kForward);
                runIntake(IntakeConstants.rollerSpeed);
                runPassthrough(IntakeConstants.passthroughSpeed);
            }
                break;

            case SHOOT:
                runPassthrough(IntakeConstants.passthroughSpeed);
                break;
                
            case EJECT:
                intakeSolenoid.set(DoubleSolenoid.Value.kForward);
                runIntake(-IntakeConstants.rollerSpeed);
                break;

            case INDEX:
                if(getBeamBreak()) {
                    runPassthrough(0.5);
                }
                else{
                    runPassthrough(0);
                }
                break;
                
            case STOW:
                runIntake(0);
                runPassthrough(0);
                intakeSolenoid.set(DoubleSolenoid.Value.kReverse);
                break;
        }
    }
}
