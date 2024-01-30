package frc.robot;

public class Constants {
    public class RobotConstants {
        public static final int driverJoystickID = 0;
    }
    
    public class IntakeConstants {
        public static final int rollerID = 0;
        public static final double rollerSpeed = 0.5;
    }

    public class SwerveConstants {
        public static final double maxSpeed = 2; // desired top speed in meters per second;
        public static final double maxAngularRate = 0.5 * Math.PI; // max angular velocity in rotations per second 
    }
}
