package frc.robot.subsystems;

import javax.swing.text.Position;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.Pigeon2;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.mechanisms.DifferentialMechanism;
import com.ctre.phoenix6.mechanisms.DifferentialMechanism.DifferentialPigeon2Source;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.ClimbConstants;
import frc.robot.generated.TunerConstants;;

public class Climb {
    private TalonFX leftClimbMotor;
    private TalonFX rightClimbMotor;
    private climbState currentState;
    private DifferentialMechanism diffMech;
    private Pigeon2 pigeon2;
    private DigitalInput limitSwitch;
    public enum climbState {
        HOOKS_UP_PERCENT,
        HOOKS_DOWN_PERCENT,
        HOOKS_UP_DIFF,
        HOOKS_DOWN_DIFF,
        OFF
    }

    /**
     * constructer for climb
     */
    public Climb(Pigeon2 pigeon2) {
        this.pigeon2 = pigeon2;
        leftClimbMotor = new TalonFX(ClimbConstants.leftClimbMotorID, "swerve");
        rightClimbMotor = new TalonFX(ClimbConstants.rightClimbMotorID, "swerve");
        currentState = climbState.OFF;

        leftClimbMotor.setNeutralMode(NeutralModeValue.Brake);
        rightClimbMotor.setNeutralMode(NeutralModeValue.Brake);

        diffMech = new DifferentialMechanism(rightClimbMotor, leftClimbMotor, true, pigeon2, DifferentialPigeon2Source.Roll);
        limitSwitch = new DigitalInput(3);

        diffMech.applyConfigs();
    }

    /**
     * sets the speed of the climb motor
     * @param speed
     */
    public void runClimb(double leftSpeed, double rightSpeed) {
        leftClimbMotor.set(leftSpeed);
        rightClimbMotor.set(rightSpeed);
    }

    public void runClimb(double speed) {
        runClimb(speed, speed);
    }

    public boolean climbRaised() {
        return !limitSwitch.get();
    }

    private void diffController(double speed) {
        VoltageOut averageRequest = new VoltageOut(12.0*speed);
        averageRequest.EnableFOC = true;
        PositionVoltage differentialRequest = new PositionVoltage(0);
        differentialRequest.EnableFOC = true;

        diffMech.setControl(averageRequest, differentialRequest);
          
    }

    /**
     * sets the intake state
     * @param state
     */
    public void setState(climbState state) {
        currentState = state;
    }
    /**
     * gets the current intake state
     * @return currentState
     */
    public climbState getState() {
        return currentState;
    }


    public void logging() {
        
    }

    public void periodic() {
      switch (currentState) {
        case HOOKS_UP_PERCENT:
            runClimb(ClimbConstants.climbSpeed);
            break;
        case HOOKS_DOWN_PERCENT:
            runClimb(-ClimbConstants.climbSpeed);
            break;
        case HOOKS_UP_DIFF:
            diffController(0.5);
            break;
        case HOOKS_DOWN_DIFF:
            diffController(-0.5);
            break;
        case OFF:
            runClimb(0);
            break;
      }
    }
}
