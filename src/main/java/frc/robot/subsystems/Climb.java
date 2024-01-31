package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.ControlModeValue;

import frc.robot.Constants.ClimbConstants;;

public class Climb {
    private TalonFX climbMotor;

    /**
     * constructer for climb
     */
    public Climb() {
        climbMotor = new TalonFX(ClimbConstants.climbMotorID);
    }

    /**
     * sets the speed of the climb motor
     * @param speed
     */
    public void runClimb(int speed) {
        climbMotor.set(speed);
    }

    
    

    public void logging() {
        climbMotor.get();
    }

    public void periodic() {

    }
}
