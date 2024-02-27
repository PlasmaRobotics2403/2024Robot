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

    PhotonCamera camera;
    PhotonTrackedTarget target;

    boolean hasTarget;
    double yaw;
    double pitch;
    double area;
    double skew;
    double distance;
    double calculatedAngle;

    public Photon() {
        camera = new PhotonCamera("plasmacam");
        calculatedAngle = 0;
    }

    public double calAngle() {
        calculatedAngle = -27.34*Math.log(distance) + 151.66;
        return calculatedAngle;
    }

    public void logging() {
        SmartDashboard.putBoolean("hasTarget", hasTarget);
        SmartDashboard.putNumber("Target ID", target.getFiducialId());

        SmartDashboard.putNumber("camYaw", yaw);
        SmartDashboard.putNumber("camPitch", pitch);
        SmartDashboard.putNumber("camArea", area);
        SmartDashboard.putNumber("camSkew", skew);
            
        SmartDashboard.putNumber("Distance to apriltag", distance);
        SmartDashboard.putNumber("Calculated Angle for Shoooter", calAngle());
    }

    public void periodic() {
        result = camera.getLatestResult();

        hasTarget = result.hasTargets();

        for(PhotonTrackedTarget target : result.targets) {
            if (target.getFiducialId() == 7 || target.getFiducialId() == 4) {
                this.target = target;
                yaw = target.getYaw();
                pitch = target.getPitch();
                area = target.getArea();
                skew = target.getSkew();
                distance = PhotonUtils.calculateDistanceToTargetMeters(Constants.PhotonConstants.camHeight, Constants.PhotonConstants.tagHeight, Constants.PhotonConstants.camPitch, Units.degreesToRadians(result.getBestTarget().getPitch()))+PhotonConstants.distanceOffset;
                distance = Units.metersToInches(distance);
            }
        }
        logging();
    }

    
}
