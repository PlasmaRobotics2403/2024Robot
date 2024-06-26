package frc.robot.subsystems;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.Utils;
import com.ctre.phoenix6.hardware.Pigeon2;
import com.ctre.phoenix6.mechanisms.swerve.SwerveDrivetrainConstants;
import com.ctre.phoenix6.mechanisms.swerve.SwerveModuleConstants;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.filter.LinearFilter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.lib.SwerveModule;
import frc.robot.Constants;

public class Swerve {
    private final int ModuleCount;

    private SwerveModule[] m_modules;
    private Pigeon2 m_pigeon2;
    private SwerveDriveKinematics m_kinematics;
    private SwerveDriveOdometry m_odometry;
    private SwerveModulePosition[] m_modulePositions;
    private Translation2d[] m_moduleLocations;
    private OdometryThread m_odometryThread;
    private Field2d m_field;
    private PIDController m_turnPid;
    private Notifier m_telemetry;

    public Swerve(
            SwerveDrivetrainConstants driveTrainConstants, SwerveModuleConstants... modules) {
        ModuleCount = modules.length;

        m_pigeon2 = new Pigeon2(driveTrainConstants.Pigeon2Id, driveTrainConstants.CANbusName);
        m_modules = new SwerveModule[ModuleCount];
        m_modulePositions = new SwerveModulePosition[ModuleCount];
        m_moduleLocations = new Translation2d[ModuleCount];

        int iteration = 0;
        for (SwerveModuleConstants module : modules) {
            m_modules[iteration] = new SwerveModule(module, driveTrainConstants.CANbusName);
            m_moduleLocations[iteration] = new Translation2d(module.LocationX, module.LocationY);
            m_modulePositions[iteration] = m_modules[iteration].getPosition(true);

            iteration++;
        }
        m_kinematics = new SwerveDriveKinematics(m_moduleLocations);
        m_odometry =
                new SwerveDriveOdometry(m_kinematics, m_pigeon2.getRotation2d(), getSwervePositions());
        m_field = new Field2d();
        SmartDashboard.putData("Field", m_field);

        m_turnPid = new PIDController(Constants.SwerveConstants.turnKp, 0, Constants.SwerveConstants.turnKd);
        m_turnPid.enableContinuousInput(-Math.PI, Math.PI);

        m_odometryThread = new OdometryThread();
        m_odometryThread.start();

        m_telemetry = new Notifier(this::logging);
        m_telemetry.startPeriodic(0.1); // Telemeterize every 100ms
    }

    /* Put smartdashboard calls in separate thread to reduce performance impact */
    private void logging() {
        //SmartDashboard.putNumber("Successful Daqs", m_odometryThread.getSuccessfulDaqs());
        //SmartDashboard.putNumber("Failed Daqs", m_odometryThread.getFailedDaqs());
        SmartDashboard.putNumber("X Pos", m_odometry.getPoseMeters().getX());
        SmartDashboard.putNumber("Y Pos", m_odometry.getPoseMeters().getY());
        SmartDashboard.putNumber("Angle", m_odometry.getPoseMeters().getRotation().getDegrees());
        SmartDashboard.putBoolean("Robot Faseing Forward", isFasingForward());
        SmartDashboard.putNumber("Field Angle", m_odometry.getPoseMeters().getRotation().getDegrees() % 360);
        //SmartDashboard.putNumber("Odometry Loop Time", m_odometryThread.getTime());
    }

    /* Perform swerve module updates in a separate thread to minimize latency */
    private class OdometryThread extends Thread {
        private BaseStatusSignal[] m_allSignals;
        public int SuccessfulDaqs = 0;
        public int FailedDaqs = 0;

        private LinearFilter lowpass = LinearFilter.movingAverage(50);
        private double lastTime = 0;
        private double currentTime = 0;
        private double averageLoopTime = 0;

        public OdometryThread() {
            super();
            // 4 signals for each module + 2 for Pigeon2
            m_allSignals = new BaseStatusSignal[(ModuleCount * 4) + 2];
            for (int i = 0; i < ModuleCount; ++i) {
                var signals = m_modules[i].getSignals();
                m_allSignals[(i * 4) + 0] = signals[0];
                m_allSignals[(i * 4) + 1] = signals[1];
                m_allSignals[(i * 4) + 2] = signals[2];
                m_allSignals[(i * 4) + 3] = signals[3];
            }
            m_allSignals[m_allSignals.length - 2] = m_pigeon2.getYaw();
            m_allSignals[m_allSignals.length - 1] = m_pigeon2.getAngularVelocityZDevice();
        }

        @Override
        public void run() {
            /* Make sure all signals update at around 250hz */
            for (var sig : m_allSignals) {
                sig.setUpdateFrequency(250);
            }
            /* Run as fast as possible, our signals will control the timing */
            while (true) {
                /* Synchronously wait for all signals in drivetrain */
                var status = BaseStatusSignal.waitForAll(0.1, m_allSignals);
                lastTime = currentTime;
                currentTime = Utils.getCurrentTimeSeconds();
                averageLoopTime = lowpass.calculate(currentTime - lastTime);

                /* Get status of the waitForAll */
                if (status.isOK()) {
                    SuccessfulDaqs++;
                } else {
                    FailedDaqs++;
                }

                /* Now update odometry */
                for (int i = 0; i < ModuleCount; ++i) {
                    /* No need to refresh since it's automatically refreshed from the waitForAll() */
                    m_modulePositions[i] = m_modules[i].getPosition(false);
                }
                // Assume Pigeon2 is flat-and-level so latency compensation can be performed
                double yawDegrees =
                        BaseStatusSignal.getLatencyCompensatedValue(
                                m_pigeon2.getYaw(), m_pigeon2.getAngularVelocityZDevice());

                m_odometry.update(Rotation2d.fromDegrees(yawDegrees), m_modulePositions);
                m_field.setRobotPose(m_odometry.getPoseMeters());
            }
        }
        /*
        public double getTime() {
            return averageLoopTime;
        }

        public int getSuccessfulDaqs() {
            return SuccessfulDaqs;
        }

        public int getFailedDaqs() {
            return FailedDaqs;
        }*/
    }

    private SwerveModulePosition[] getSwervePositions() {
        return m_modulePositions;
    }

    public void driveRobotCentric(ChassisSpeeds speeds) {
        var swerveStates = m_kinematics.toSwerveModuleStates(speeds);
        for (int i = 0; i < ModuleCount; ++i) {
            m_modules[i].apply(swerveStates[i]);
        }
    }

    public void driveFieldCentric(ChassisSpeeds speeds) {
        var roboCentric = ChassisSpeeds.fromFieldRelativeSpeeds(speeds, m_pigeon2.getRotation2d());
        driveRobotCentric(roboCentric);
    }

    public Pigeon2 getPigeon() {
        return m_pigeon2;
    }
    public void driveFullyFieldCentric(double xSpeeds, double ySpeeds, Rotation2d targetAngle) {
        var currentAngle = m_pigeon2.getRotation2d();
        double rotationalSpeed =
                m_turnPid.calculate(currentAngle.getRadians(), targetAngle.getRadians());

        var roboCentric =
                ChassisSpeeds.fromFieldRelativeSpeeds(
                        xSpeeds, ySpeeds, rotationalSpeed, m_pigeon2.getRotation2d());
        var swerveStates = m_kinematics.toSwerveModuleStates(roboCentric);
        for (int i = 0; i < ModuleCount; ++i) {
            m_modules[i].apply(swerveStates[i]);
        }
    }

    public void driveStopMotion() {
        /* Point every module toward (0,0) to make it close to a X configuration */
        for (int i = 0; i < ModuleCount; ++i) {
            var angle = m_moduleLocations[i].getAngle();
            m_modules[i].apply(new SwerveModuleState(0, angle));
        }
    }

    public void seedFieldRelative() {
        m_pigeon2.setYaw(0);
    }

    public Pose2d getPoseMeters() {
        return m_odometry.getPoseMeters();
    }

    public void resetOdometry() {
        m_odometry.resetPosition(m_pigeon2.getRotation2d(), getSwervePositions(), new Pose2d());
    }

    public void setOdometry(Pose2d pose) {
        m_odometry.resetPosition(m_pigeon2.getRotation2d(), getSwervePositions(), pose);
    }

    public double getSuccessfulDaqs() {
        return m_odometryThread.SuccessfulDaqs;
    }

    public double getFailedDaqs() {
        return m_odometryThread.FailedDaqs;
    }

    public boolean isFasingForward() {
        double angle = m_odometry.getPoseMeters().getRotation().getDegrees() % 360;
        return Math.abs(angle) < 90;
    }

    public void zeroHeading() {
        m_pigeon2.reset();
    }

}