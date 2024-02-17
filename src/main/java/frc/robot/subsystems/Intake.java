package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeConstants;

public class Intake extends SubsystemBase{
    
    private CANSparkMax roller;
    private CANSparkMax passthrough;

    private intakeState currentState;
    public enum intakeState {
        INTJECT,
        EJECT,
        STOW,
        
    }
    private DoubleSolenoid intakeSolenoid;
    /**
     * constructor for intake
     */
    public Intake() {
        // roller config
        roller = new CANSparkMax(IntakeConstants.rollerID, MotorType.kBrushless);
        passthrough = new CANSparkMax(IntakeConstants.passthroughID, MotorType.kBrushless);
        roller.setIdleMode(IdleMode.kBrake);
        roller.setInverted(true);
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

    /**
     * periodiclly logs information to
     * smartdashboard
     */
    public void logging() {
        SmartDashboard.putNumber("Intake Roller Speed", roller.get());
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
                runIntake(IntakeConstants.rollerSpeed);
                runPassthrough(IntakeConstants.passthroughSpeed);
                intakeSolenoid.set(DoubleSolenoid.Value.kForward);
                break;
            case EJECT:
                runIntake(-IntakeConstants.rollerSpeed);
                intakeSolenoid.set(DoubleSolenoid.Value.kForward);
                break;
                
            case STOW:
                runIntake(0);
                intakeSolenoid.set(DoubleSolenoid.Value.kReverse);

                Timer.delay(0.5);
                runPassthrough(0);
                break;
        }
    }
}
