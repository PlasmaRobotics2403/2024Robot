package frc.robot.subsystems;

import org.opencv.core.Mat;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.Constants.PhotonConstants;

public class Photon {
    PhotonPipelineResult result;

    PhotonTrackedTarget target;

    PhotonCamera camera;

    boolean hasTarget;
    double yaw;
    double pitch;
    double area;
    double skew;
    double distance;

    public Photon() {
        camera = new PhotonCamera("plasmacam");
    }

    public double calAngle() {
        return -7.65*Math.log(distance) + 46.209;
    }

    public void logging() {
        if(hasTarget) {
            SmartDashboard.putBoolean("hasTarget", hasTarget);
            SmartDashboard.putNumber("camYaw", target.getYaw());
            SmartDashboard.putNumber("camPitch", target.getPitch());
            SmartDashboard.putNumber("camArea", target.getArea());
            SmartDashboard.putNumber("camSkew", target.getSkew());
            
            SmartDashboard.putNumber("Distance to apriltag", distance);
            SmartDashboard.putNumber("Calculated Angle for Shoooter", calAngle());
        }

    }

    public void periodic() {
        result = camera.getLatestResult();

        hasTarget = result.hasTargets();



        if(hasTarget) {
            target = result.getBestTarget();
            yaw = target.getYaw();
            pitch = target.getPitch();
            area = target.getArea();
            skew = target.getSkew();
            distance = PhotonUtils.calculateDistanceToTargetMeters(Constants.PhotonConstants.camHeight, Constants.PhotonConstants.tagHeight, Constants.PhotonConstants.camPitch, Units.degreesToRadians(result.getBestTarget().getPitch()))+PhotonConstants.distanceOffset;
            
            distance = Units.metersToInches(distance);
            logging();
        }
    }

    
}
