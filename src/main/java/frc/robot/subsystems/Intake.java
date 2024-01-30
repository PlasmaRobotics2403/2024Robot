package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.IntakeConstants;

public class Intake {
    
    private CANSparkMax roller;

    /**
     * constructor for intake
     */
    public Intake() {
        roller = new CANSparkMax(IntakeConstants.rollerID, MotorType.kBrushless);
    }

    /**
     * sets the speed of the roller motor
     */
    public void runIntake(double speed) {
        roller.set(speed);
    }

    private void logging() {
        SmartDashboard.putNumber("Roller Speed", roller.get());
    }

     /**
     * goes in robot periodic to trigger any
     * important updates.
     */
    public void periodic() {
        logging();
    }
}
