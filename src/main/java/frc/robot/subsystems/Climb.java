package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
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

public class Climb {
    private TalonFX leftClimbMotor;
    private TalonFX rightClimbMotor;
    private DifferentialMechanism diffMech;

    private climbState currentState;
    
    private TalonFXConfiguration currentConfigs;

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

        // current limiting
        currentConfigs = new TalonFXConfiguration();

        currentConfigs.CurrentLimits.StatorCurrentLimit = 40;
        currentConfigs.CurrentLimits.StatorCurrentLimitEnable = true;

        rightClimbMotor.getConfigurator().apply(currentConfigs);
        leftClimbMotor.getConfigurator().apply(currentConfigs);

        //motor configuration
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
        else if (leftClimbMotor.getRotorPosition().getValueAsDouble() > 72 && leftSpeed > 0) {
            leftSpeed = 0;
        }

        if(!rightLimitSwitch.get() && rightSpeed < 0) {
            rightSpeed = 0;
            rightClimbMotor.setPosition(0);
        }
        else if (rightClimbMotor.getRotorPosition().getValueAsDouble() > 72 && rightSpeed > 0) {
            rightSpeed = 0;
        }
        leftClimbMotor.set(leftSpeed);
        rightClimbMotor.set(rightSpeed);
    }

    /**
     * runs both climb motors a one set speed of a percentage;
     * @param speed
     */
    public void runClimb(double speed) {
        runClimb(speed, speed);
    }

    /**
     * checks if the climb is raised
     * @return if climb raised
     */
    public boolean climbRaised() {
        if(leftClimbMotor.getRotorPosition().getValueAsDouble() > 5 
        || !leftLimitSwitch.get() || !rightLimitSwitch.get() 
        || leftClimbMotor.getRotorPosition().getValueAsDouble() > 5) {
            return true;
        }
        else {
            return false;
        }
    }
        

    /**
     * runs the climb based off of the the gyro to compensate if the robot is tilted
     * @param speed
     */
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
