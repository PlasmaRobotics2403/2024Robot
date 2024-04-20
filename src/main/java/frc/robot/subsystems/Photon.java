package frc.robot.subsystems;

import org.photonvision.PhotonCamera;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.Constants.PhotonConstants;

public class Photon {
    PhotonPipelineResult result;

    PhotonCamera camera;
    PhotonTrackedTarget target;

    boolean hasTarget = false;
    double yaw = 0.0;
    double pitch = 0.0;
    double area = 0.0;
    double skew = 0.0;
    double distance = 0.0;
    double calculatedAngle = 0.0;
    
    PIDController turnController = new PIDController(0.20, 0, 0.0156);
    PIDController trapController = new PIDController(0.1, 0, 0);

    public Photon() {
        camera = new PhotonCamera("plasmacam");
        calculatedAngle = 0;
    }

    public double trapAllignForward() {
        return -trapController.calculate(area, PhotonConstants.trapDitance);
    }

    /**
     * calculates the angle fok the shooter based off the camera
     * @return
     */
    public double calAngle() {
        calculatedAngle = 7072.78099*Math.pow(distance,-1.27315); 
        return calculatedAngle;
    }

    /**
     * finds the offset for the robot to ajust to make a shot 
     * @return
     */
    public double alignToTarget() {
        /*return 0 if cant find target */
        if(!hasTarget) {
            return 0;
        }
        else{
            return turnController.calculate(yaw, 4);
        }
    }

    public boolean isAligned() {
            return yaw >= 2.5 && yaw <= 5.5;
    }


    public void logging() {
            SmartDashboard.putBoolean("hasTarget", hasTarget);
            
            if(hasTarget) {
                SmartDashboard.putNumber("Target ID", target.getFiducialId());

                SmartDashboard.putNumber("camYaw", yaw);
                SmartDashboard.putNumber("camPitch", pitch);
                SmartDashboard.putNumber("camArea", area);
                SmartDashboard.putNumber("camSkew", skew);
                SmartDashboard.putBoolean("Is Alligned", isAligned());
                
                SmartDashboard.putNumber("Distance to apriltag", distance);
                SmartDashboard.putNumber("Calculated Angle for Shoooter", calAngle());
            }
        
    }

    public void periodic() {
        result = camera.getLatestResult();

        hasTarget = result.hasTargets();

        for(PhotonTrackedTarget target : result.targets) {
            this.target = target;
            if (target.getFiducialId() == 7 || target.getFiducialId() == 4 || target.getFiducialId() == 11) {
                yaw = target.getYaw();
                pitch = target.getPitch();
                area = target.getArea();
                skew = target.getSkew(); 
                distance = PhotonUtils.calculateDistanceToTargetMeters(Constants.PhotonConstants.camHeight, Constants.PhotonConstants.tagHeight, Constants.PhotonConstants.camPitch, Units.degreesToRadians(result.getBestTarget().getPitch()))+PhotonConstants.distanceOffset;
                distance = Units.metersToInches(distance);
            }
            else if(target.getFiducialId() == 3 || target.getFiducialId() == 8){
            }
            else{
                yaw = 1;
                pitch = -1;
                area = -1;
                skew = -1;
                distance = -1;
            }
        }
        logging();
    }

    
}
