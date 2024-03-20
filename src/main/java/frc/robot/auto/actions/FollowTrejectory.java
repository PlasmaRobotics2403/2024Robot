package frc.robot.auto.actions;

import java.util.Optional;

import com.choreo.lib.Choreo;
import com.choreo.lib.ChoreoControlFunction;
import com.choreo.lib.ChoreoTrajectory;
import com.choreo.lib.ChoreoTrajectoryState;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;
import frc.lib.autoUtil.Action;
import frc.robot.Constants;
import frc.robot.subsystems.Swerve;

public class FollowTrejectory implements Action{

    private ChoreoTrajectory path;
    private Swerve swerve;

    private PIDController xPid;
    private PIDController yPid;
    private PIDController thetaPid;
    private ChassisSpeeds speeds;
    private ChoreoControlFunction controller;
    private Timer timer;

    public FollowTrejectory(String pathName, Swerve swerve) {
        DriverStation.reportWarning(pathName, false);
        path = Choreo.getTrajectory(pathName);
        this.swerve = swerve;

        xPid =  new PIDController(Constants.AutoConstants.kPXController, 0.0, 0.0);
        yPid = new PIDController(Constants.AutoConstants.kPYController, 0.0, 0.0);
        thetaPid = new PIDController(Constants.AutoConstants.kPThetaController, 0.0, 0.0);
        controller = Choreo.choreoSwerveController(xPid, yPid, thetaPid);

        timer = new Timer();
        swerve.setOdometry(path.getInitialPose());
    }

    @Override
    public boolean isFinished() {
        return timer.hasElapsed(path.getTotalTime());
    }

    @Override
    public void start() {
        timer.restart();
    }

    @Override
    public void update() {
        /* current code */
        //speeds = controller.apply(swerve.getPoseMeters(), path.sample(timer.get(), isRedAlliance));
        //ChassisSpeeds robotSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(speeds, robotRot);
        //DriverStation.reportWarning("Before Speeds: " + speeds.toString(), false);
        //DriverStation.reportWarning("After Speeds: " + robotSpeeds.toString(), false);
        //swerve.driveRobotCentric(robotSpeeds);

        Pose2d currentPose = swerve.getPoseMeters();
        ChoreoTrajectoryState state = path.sample(timer.get(), false);
        speeds = controller.apply(currentPose, state);
        swerve.driveRobotCentric(speeds);
    }

    @Override
    public void end() {
        timer.stop();
        swerve.driveRobotCentric(new ChassisSpeeds());
    }
    
}
