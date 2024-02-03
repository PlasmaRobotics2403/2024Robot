package frc.robot;

public class Constants {
    
    public class IntakeConstants {
    
        public static final int rollerID = 1;
        public static final double rollerSpeed = 0.5;
        public static final int forwardChannelID = 1;
        public static final int backwardChannelID = 2;
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
    }

    public class SwerveConstants {

        public static final double maxSpeed = 2; // desired top speed in meters per second;
        public static final double maxAngularRate = 0.5 * Math.PI; // max angular velocity in rotations per second 
    }

}
