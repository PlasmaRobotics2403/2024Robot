package frc.robot.subsystems;

import com.ctre.phoenix6.mechanisms.swerve.SwerveDrivetrain;
import com.ctre.phoenix6.mechanisms.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.Constants.SwerveConstants;
import frc.robot.generated.TunerConstants;

public class Swerve {
    private SwerveDrivetrain drivetrain;

    // request types
    private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
    private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();
    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
        .withDriveRequestType(DriveRequestType.OpenLoopVoltage);    // I want field-centric
                                                                    // driving in open loop

    /**
     * constructor for swerve utilizing values from 
     * tuner X swerve generator.
     */
    public Swerve() {
        drivetrain = new SwerveDrivetrain(
            TunerConstants.DrivetrainConstants,
            TunerConstants.FrontLeft,
            TunerConstants.FrontRight,
            TunerConstants.BackLeft,
            TunerConstants.BackRight
        );
    }

    /**
     * set swerve modules to X pattern
     */
    public void swerveBrake() {
        drivetrain.setControl(brake);
    }

    /**
     * point wheels in given direction
     * @param xComponent x direction to point
     * @param yComponent y direction to point
     */
    public void swervePoint(double xComponent, double yComponent) {
        drivetrain.setControl(point.withModuleDirection(new Rotation2d(-xComponent, -yComponent)));
    }

    /**
     * field centric swerve drive
     * @param xSpeed speed the robot drives forward
     * @param ySpeed speed the robot strafes
     * @param rotation speed the robot rotates
     */
    public void swerveFieldCentric(double xSpeed, double ySpeed, double rotation) {
        drivetrain.setControl(drive.withVelocityX(-xSpeed * SwerveConstants.maxSpeed)
                                   .withVelocityY(-ySpeed * SwerveConstants.maxSpeed)
                                   .withRotationalRate(-rotation * SwerveConstants.maxAngularRate)
        );
    }

    /**
     * re-orient field centric to current heading
     */
    public void resetHeading() {
        drivetrain.seedFieldRelative();
    }

    /**
     * SmartDashboard logging info
     */
    private void logging() {
    }

    /**
     * goes in robot periodic to trigger any
     * important updates.
     */
    public void periodic() {
        logging();
    }
}
