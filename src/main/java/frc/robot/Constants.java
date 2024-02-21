package frc.robot;

import edu.wpi.first.math.util.Units;
import frc.robot.subsystems.Shooter.shooterState;

public class Constants {
    
    public class IntakeConstants {
    
        public static final int rollerID = 1;
        public static final int passthroughID = 6;

        public static final double rollerSpeed = 0.7;
        public static final double passthroughSpeed = 0.7;
        public static final int forwardChannelID = 0;
        public static final int backwardChannelID = 1;
    }

    public class ClimbConstants {
    
        public static final int climbMotorID = 1;
        public static final double climbSpeed = 0.1;
    }

    public class RobotConstants {

        public static final int driverJoystickID = 0;
    }

    public class ShooterConstants {

        public static final int shooterMotor1ID = 2;
        public static final int shooterMotor2ID = 3;
        public static final double shooterSpeed = .9;
        public static final double shooterRotSpeed = 0.1;
        public static final int ampMotorID = 4;
        public static final int rotMotorID = 5;

        //positions
        public static final double ampPos = 0;
        public static final double climbPos = 0;
        public static final double pos = 25;

        public static final double angleConversion = (70*(100/30))/360; //angle to rotation
        public static final double rotationConversion = (360/70*(100/30));
        //pid
        public static final double shooterPivotKS = 0.25;
        public static final double shooterPivotKV = 0.12;
        public static final double shooterPivotKP = 10;
        public static final double shooterPivotKD = 0;

        public static final double shooterPivotVel = 180;        //rps
        public static final double shooterPivotAccel = 360;     //rps/s
        public static final double shooterPivotJerk = 3600;        //rps/s/s

    }

    public class SwerveConstants {

        public static final double maxSpeed = 2; // desired top speed in meters per second;
        public static final double maxAngularRate = 0.5 * Math.PI; // max angular velocity in rotations per second 

        public static final double turnKp = 0;
        public static final double turnKd = 0;
    }

    public class PhotonConstants {
        public static final double camHeight = Units.inchesToMeters(24.5);
        public static final double tagHeight = Units.inchesToMeters(61.125);
        public static final double camPitch = Units.degreesToRadians(20);
        public static final double distanceOffset = Units.inchesToMeters(10);

        public static final double angleSlope = -0.0462;
        public static final double angleIntersept = 15.851;

        public static final double pivotHeight = Units.inchesToMeters(17);
        public static final double pivotOffset = Units.inchesToMeters(12);
        public static final double pivotRestAngle = Units.inchesToMeters(20);
        public static final double goalHeight = Units.inchesToMeters(87);
        
    }

    public class AutoConstants {

        public static final double kPXController = 0;
        public static final double kPYController = 0;
        public static final double kPThetaController = 0;

    }

}
