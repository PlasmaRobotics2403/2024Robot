package frc.robot.auto.actions;

import java.util.Optional;

import com.choreo.lib.Choreo;
import com.choreo.lib.ChoreoControlFunction;
import com.choreo.lib.ChoreoTrajectory;

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
    private Optional<Alliance> ally;
    private boolean isRedAlliance = true;

    private PIDController xPid;
    private PIDController yPid;
    private PIDController thetaPid;
    private ChassisSpeeds speeds;
    private ChoreoControlFunction controller;
    private Timer timer;

    private Rotation2d robotRot;

    public FollowTrejectory(String pathName, Swerve swerve) {
        ally = DriverStation.getAlliance();
    
        if(ally.get() == Alliance.Red) {
            isRedAlliance = true;
        }
        else {
            isRedAlliance = false;
        }
        path = Choreo.getTrajectory(pathName);
        this.swerve = swerve;

        robotRot = new Rotation2d(Math.toRadians(-90));
        xPid =  new PIDController(Constants.AutoConstants.kPXController, 0.0, 0.0);
        yPid = new PIDController(Constants.AutoConstants.kPYController, 0.0, 0.0);
        thetaPid = new PIDController(Constants.AutoConstants.kPThetaController, 0.0, 0.0);
        controller = Choreo.choreoSwerveController(xPid, yPid, thetaPid);

        timer = new Timer();
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
        speeds = controller.apply(swerve.getPoseMeters(), path.sample(timer.get(), isRedAlliance));
        ChassisSpeeds robotSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(speeds, robotRot);
        DriverStation.reportWarning("Before Speeds: " + speeds.toString(), false);
        DriverStation.reportWarning("After Speeds: " + robotSpeeds.toString(), false);
        //swerve.driveRobotCentric(robotSpeeds);
    }

    @Override
    public void end() {
        timer.stop();
        swerve.driveRobotCentric(new ChassisSpeeds());
    }
    
}
