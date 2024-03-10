package frc.robot.subsystems;

import javax.swing.text.Position;

import com.ctre.phoenix6.configs.HardwareLimitSwitchConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.Pigeon2;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.mechanisms.DifferentialMechanism;
import com.ctre.phoenix6.mechanisms.DifferentialMechanism.DifferentialPigeon2Source;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.ReverseLimitSourceValue;
import com.ctre.phoenix6.signals.ReverseLimitTypeValue;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.ClimbConstants;
import frc.robot.generated.TunerConstants;

public class Climb {
    private TalonFX leftClimbMotor;
    private TalonFX rightClimbMotor;
    private DifferentialMechanism diffMech;

    private climbState currentState;

    private DigitalInput leftLimitSwitch;
    private DigitalInput rightLimitSwitch;

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
        leftClimbMotor = new TalonFX(ClimbConstants.leftClimbMotorID, "swerve");
        rightClimbMotor = new TalonFX(ClimbConstants.rightClimbMotorID, "swerve");

        leftClimbMotor.setPosition(0);
        rightClimbMotor.setPosition(0);

        leftClimbMotor.setInverted(true);
        rightClimbMotor.setInverted(false);

        currentState = climbState.OFF;

        leftClimbMotor.setNeutralMode(NeutralModeValue.Brake);
        rightClimbMotor.setNeutralMode(NeutralModeValue.Brake);

        diffMech = new DifferentialMechanism(rightClimbMotor, leftClimbMotor, true, pigeon2, DifferentialPigeon2Source.Roll);
        leftLimitSwitch = new DigitalInput(3);
        rightLimitSwitch = new DigitalInput(2);

        diffMech.applyConfigs();
    }

    /**
     * sets the speed of the climb motor
     * @param speed
     */
    public void runClimb(double leftSpeed, double rightSpeed) {
        if(!leftLimitSwitch.get() && leftSpeed < 0){
            leftSpeed = 0;
            leftClimbMotor.setPosition(0);
        }
        else if (leftClimbMotor.getRotorPosition().getValueAsDouble() > 72) {
            leftSpeed = 0;
        }

        if(!rightLimitSwitch.get() && rightSpeed < 0) {
            rightSpeed = 0;
            rightClimbMotor.setPosition(0);
        }
        else if (rightClimbMotor.getRotorPosition().getValueAsDouble() > 72) {
            rightSpeed = 0;
        }
        leftClimbMotor.set(leftSpeed);
        rightClimbMotor.set(rightSpeed);
    }

    public void runClimb(double speed) {
        runClimb(speed, speed);
    }

    public boolean climbRaised() {
        return leftClimbMotor.getRotorPosition().getValueAsDouble() > 5;
        //return !limitSwitch.get();
    }

    private void diffController(double speed) {
        VoltageOut averageRequest = new VoltageOut(-12.0*speed);
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
        SmartDashboard.putString("Climb State", currentState.toString());
        SmartDashboard.putNumber("Left Climb Pos", leftClimbMotor.getRotorPosition().getValueAsDouble());
        SmartDashboard.putNumber("Right Climb Pos", rightClimbMotor.getRotorPosition().getValueAsDouble());
        SmartDashboard.putBoolean("Left Limit Swit", !leftLimitSwitch.get());
        SmartDashboard.putBoolean("Right Limit Swit", !rightLimitSwitch.get());
    }

    public void periodic() {
        logging();
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
