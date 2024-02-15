package frc.robot.auto.modes;

import frc.lib.autoUtil.AutoMode;
import frc.lib.autoUtil.AutoModeEndedException;
import frc.robot.auto.actions.FollowTrejectory;
import frc.robot.subsystems.Swerve;
import edu.wpi.first.wpilibj.DriverStation;

/**
 *
 */
public class DriveAndTurn extends AutoMode {

	private Swerve swerve;
	private String path;

	public DriveAndTurn(Swerve swerve) {
		this.swerve = swerve;
		path = "driveAndTurn";
	}



	@Override
	protected void routine() throws AutoModeEndedException {
		DriverStation.reportWarning("Starting Auto run", false);
		runAction(new FollowTrejectory(path, swerve));

		DriverStation.reportWarning("Ending Auto run", false);

	}

}