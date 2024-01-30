package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeConstants;

public class Intake extends SubsystemBase{
    
    private CANSparkMax roller;

    /**
     * constructor for intake
     */
    public Intake() {
        // roller config
        roller = new CANSparkMax(IntakeConstants.rollerID, MotorType.kBrushless);
        roller.setInverted(true);
    }

    /**
     * sets the speed of the roller motor
     */
    public void runIntake(double speed) {
        roller.set(speed);
    }

    /**
     * periodiclly logs information to
     * smartdashboard
     */
    public void logging() {
        SmartDashboard.putNumber("Roller Speed", roller.get());
    }
}
