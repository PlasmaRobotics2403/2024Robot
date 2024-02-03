package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.ClimbConstants;;

public class Climb {
    private TalonFX climbMotor;
    private climbState currentState;
    public enum climbState {
        UP,
        DOWN,
        NOTMOVING
    }

    /**
     * constructer for climb
     */
    public Climb() {
        climbMotor = new TalonFX(ClimbConstants.climbMotorID);
        currentState = climbState.NOTMOVING;
    }

    /**
     * sets the speed of the climb motor
     * @param speed
     */
    public void runClimb(double speed) {
        climbMotor.set(speed);
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
        SmartDashboard.putNumber("Climb Speed", climbMotor.get());
    }

    public void periodic() {
      switch (currentState) {
        case UP:
            runClimb(ClimbConstants.climbSpeed);
            break;
        case DOWN:
            runClimb(-ClimbConstants.climbSpeed);
            break;
        case NOTMOVING:
            runClimb(0);
            break;
      }
    }
}
