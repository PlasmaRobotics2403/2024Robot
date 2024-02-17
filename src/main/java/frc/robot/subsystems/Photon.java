package frc.robot.subsystems;

import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Photon {
    PhotonPipelineResult result;

    PhotonTrackedTarget target;

    PhotonCamera camera;

    boolean hasTarget;
    double yaw;
    double pitch;
    double area;
    double skew;

    public Photon() {
        camera = new PhotonCamera("plasmacam");
    }

    public void logging() {
        if(hasTarget) {
            SmartDashboard.putBoolean("hasTarget", hasTarget);
            SmartDashboard.putNumber("camYaw", target.getYaw());
            SmartDashboard.putNumber("camPitch", target.getPitch());
            SmartDashboard.putNumber("camArea", target.getArea());
            SmartDashboard.putNumber("camSkew", target.getSkew());
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

            logging();
        }
    }

    
}
