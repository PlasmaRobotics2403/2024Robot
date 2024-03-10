
/**
 * 
 */
package frc.robot.auto.actions;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.*;
import frc.lib.autoUtil.Action;
import frc.robot.Constants;
import frc.robot.StateManager;
import frc.robot.StateManager.robotState;
import frc.robot.subsystems.Photon;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Swerve;
import frc.robot.subsystems.Shooter.shooterState;
/**
 *
 */
public class AutoAllign implements Action {

    Swerve swerve;
    Photon photon;

    public AutoAllign(Swerve swerve, Photon photon) {
        this.swerve = swerve;
        this.photon = photon;
    }
		
	@Override
	public boolean isFinished() {
        return photon.isAligned();
	}

	@Override
	public void start() {

	}

	@Override
	public void update() {
        swerve.driveFieldCentric(
          new ChassisSpeeds(0,0, photon.alignToTarget()));
	}

	@Override
	public void end() {
        swerve.driveFieldCentric(
            new ChassisSpeeds(0,0,0));
	}

}