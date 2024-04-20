package frc.robot;

import edu.wpi.first.math.util.Units;

public class Constants {
    
    public class IntakeConstants {
    
        public static final int rollerID = 1;

        public static final double rollerSpeed = 0.9;
        public static final int forwardChannelID = 0;
        public static final int backwardChannelID = 1;
    }

    public class IndexConstants {
    
        public static final int passthroughID = 6;
        public static final int indexSensorID = 0;
        public static final int shooterSensorID = 1;

        public static final double passthroughSpeed = 0.9;
        public static final double indexSpeed = 0.15;

    }

    public class ClimbConstants {
    
        public static final int leftClimbMotorID = 9;
        public static final int rightClimbMotorID = 10;
        public static final double climbSpeed = 0.45;
    }

    public class RobotConstants {

        public static final int driverJoystickID = 0;
    }

    public class ShooterConstants {

        public static final int shooterMotor1ID = 2;
        public static final int shooterMotor2ID = 3;
        public static final double shooterSpeed = .9;
        public static final double ampSpeed = .2;
        public static final double shooterRotSpeed = 0.1;
        public static final int ampMotorID = 4;
        public static final int rotMotorID = 5;
        public static final double shooterRPS = 85;
        public static final double ampRPS = 4.5;
        public static final double trapRPS = 35;

        //positions
        public static final double ampAngle = 103;
        public static final double climbPos = 103;
        public static final double staticFront = 37;
        public static final double staticBack = 100;
        public static final double trapAngle = 36;

        public static final double angleConversion = (35.0/54); //angle to rotation 70*(100/30)/360
        public static final double rotationConversion = (360.0/70*(100/30)) /10;
        //pid
        public static final double shooterPivotKS = 0.25;
        public static final double shooterPivotKV = 0.12;
        public static final double shooterPivotKP = 10;
        public static final double shooterPivotKD = 0;

        public static final double shooterPivotVel = 180;        //rps
        public static final double shooterPivotAccel = 360;     //rps/s
        public static final double shooterPivotJerk = 3600;        //rps/s/s

        public static final double shooterVelocityKS = 0.25;
        public static final double shooterVelocityKV = 0.12;
        public static final double shooterVelocityKA = 0.01;
        public static final double shooterVelocityKP = 0.11;
        public static final double shooterVelocityKI = 0;
        public static final double shooterVelocityKD = 0;

        public static final double shooterVelocityAccel = 400;     //rps/s
        public static final double shooterVelocityJerk = 4000;        //rps/s/s

    }

    public class SwerveConstants {

        public static final double maxSpeed = 5.5; // desired top speed in meters per second;
        public static final double maxAngularRate = 1.5 * Math.PI; // max angular velocity in rotations per second 

        public static final double turnKp = 0;
        public static final double turnKd = 0;
        public static final double creepSpeed = 0.75;
        public static final double creepTurn = 0.4;
    }

    public class PhotonConstants {
        public static final double camHeight = Units.inchesToMeters(24.5);
        public static final double tagHeight = Units.inchesToMeters(61.125);
        public static final double camPitch = Units.degreesToRadians(25);
        public static final double distanceOffset = Units.inchesToMeters(10.5);

        public static final double angleSlope = -0.0462;
        public static final double angleIntersept = 15.851;

        public static final double pivotHeight = Units.inchesToMeters(17);
        public static final double pivotOffset = Units.inchesToMeters(12);
        public static final double pivotRestAngle = Units.inchesToMeters(27);
        public static final double goalHeight = Units.inchesToMeters(55);
        public static final double trapDitance = Units.inchesToMeters(49);
        
    }

    public class AutoConstants {

        public static final double kPXController = 0.05;
        public static final double kPYController = 0.0;
        public static final double kPThetaController = 0.0;

    }

}
