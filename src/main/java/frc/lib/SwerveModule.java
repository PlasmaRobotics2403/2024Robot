package frc.lib;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.TorqueCurrentConfigs;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.TorqueCurrentFOC;
import com.ctre.phoenix6.controls.VelocityTorqueCurrentFOC;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.mechanisms.swerve.SwerveModuleConstants;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;

public class SwerveModule {
    private TalonFX m_driveMotor;
    private TalonFX m_steerMotor;
    private CANcoder m_cancoder;

    private StatusSignal<Double> m_drivePosition;
    private StatusSignal<Double> m_driveVelocity;
    private StatusSignal<Double> m_steerPosition;
    private StatusSignal<Double> m_steerVelocity;
    private BaseStatusSignal[] m_signals;
    private double m_driveRotationsPerMeter = 0;

    private PositionVoltage m_angleSetter = new PositionVoltage(0);
    private VelocityTorqueCurrentFOC m_velocitySetter = new VelocityTorqueCurrentFOC(0);

    private SwerveModulePosition m_internalState = new SwerveModulePosition();

    public SwerveModule(SwerveModuleConstants constants, String canbusName) {
        m_driveMotor = new TalonFX(constants.DriveMotorId, canbusName);
        m_steerMotor = new TalonFX(constants.SteerMotorId, canbusName);
        m_cancoder = new CANcoder(constants.CANcoderId, canbusName);

        TalonFXConfiguration talonConfigs = new TalonFXConfiguration();
        
          talonConfigs.MotorOutput.Inverted = 
                constants.DriveMotorInverted
                        ? InvertedValue.Clockwise_Positive
                        : InvertedValue.CounterClockwise_Positive;

        talonConfigs.Slot0 = constants.DriveMotorGains;
        talonConfigs.TorqueCurrent.PeakForwardTorqueCurrent = 60;
        talonConfigs.TorqueCurrent.PeakReverseTorqueCurrent = -60;
        talonConfigs.MotorOutput.NeutralMode = NeutralModeValue.Brake;

        m_driveMotor.getConfigurator().apply(talonConfigs);

        

        /* Undo changes for torqueCurrent */
        talonConfigs.TorqueCurrent = new TorqueCurrentConfigs();

        /* change current limit for steer */
        talonConfigs.TorqueCurrent.PeakForwardTorqueCurrent = 60;
        talonConfigs.TorqueCurrent.PeakReverseTorqueCurrent = -60;
        talonConfigs.CurrentLimits.StatorCurrentLimitEnable = false;
        talonConfigs.CurrentLimits.SupplyCurrentLimit = 60;
        talonConfigs.CurrentLimits.SupplyCurrentLimitEnable = true;

        talonConfigs.Slot0 = constants.SteerMotorGains;
        // Modify configuration to use remote CANcoder fused
        talonConfigs.Feedback.FeedbackRemoteSensorID = constants.CANcoderId;
        talonConfigs.Feedback.FeedbackSensorSource = FeedbackSensorSourceValue.FusedCANcoder;
        talonConfigs.Feedback.RotorToSensorRatio = constants.SteerMotorGearRatio;

        talonConfigs.ClosedLoopGeneral.ContinuousWrap =
                true; // Enable continuous wrap for swerve modules

        talonConfigs.MotorOutput.Inverted =
                constants.SteerMotorInverted
                        ? InvertedValue.Clockwise_Positive
                        : InvertedValue.CounterClockwise_Positive;
        m_steerMotor.getConfigurator().apply(talonConfigs);

        CANcoderConfiguration cancoderConfigs = new CANcoderConfiguration();
        cancoderConfigs.MagnetSensor.MagnetOffset = constants.CANcoderOffset;
        m_cancoder.getConfigurator().apply(cancoderConfigs);

        m_drivePosition = m_driveMotor.getPosition();
        m_driveVelocity = m_driveMotor.getVelocity();
        m_steerPosition = m_cancoder.getPosition();
        m_steerVelocity = m_cancoder.getVelocity();

        m_signals = new BaseStatusSignal[4];
        m_signals[0] = m_drivePosition;
        m_signals[1] = m_driveVelocity;
        m_signals[2] = m_steerPosition;
        m_signals[3] = m_steerVelocity;

        /* Calculate the ratio of drive motor rotation to meter on ground */
        double rotationsPerWheelRotation = constants.DriveMotorGearRatio;
        double metersPerWheelRotation = 2 * Math.PI * Units.inchesToMeters(constants.WheelRadius);
        m_driveRotationsPerMeter = rotationsPerWheelRotation / metersPerWheelRotation;
    }

    public SwerveModulePosition getPosition(boolean refresh) {
        if (refresh) {
            /* Refresh all signals */
            m_drivePosition.refresh();
            m_driveVelocity.refresh();
            m_steerPosition.refresh();
            m_steerVelocity.refresh();
        }

        /* Now latency-compensate our signals */
        double drive_rot =
            BaseStatusSignal.getLatencyCompensatedValue(m_drivePosition, m_driveVelocity);
        double angle_rot =
            BaseStatusSignal.getLatencyCompensatedValue(m_steerPosition, m_steerVelocity);

        /* And push them into a SwerveModuleState object to return */
        m_internalState.distanceMeters = drive_rot / m_driveRotationsPerMeter;
        /* Angle is already in terms of steer rotations */
        m_internalState.angle = Rotation2d.fromRotations(angle_rot);

        return m_internalState;
    }

    public void apply(SwerveModuleState state) {
        var optimized = SwerveModuleState.optimize(state, m_internalState.angle);

        double angleToSetDeg = optimized.angle.getRotations();
        m_steerMotor.setControl(m_angleSetter.withPosition(angleToSetDeg).withEnableFOC(true));
        double velocityToSet = optimized.speedMetersPerSecond * m_driveRotationsPerMeter;
        m_driveMotor.setControl(m_velocitySetter.withVelocity(velocityToSet));
    }

    public BaseStatusSignal[] getSignals() {
        return m_signals;
    }
}