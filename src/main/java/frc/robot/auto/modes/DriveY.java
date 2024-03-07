package frc.robot.auto.modes;

import edu.wpi.first.wpilibj.DriverStation;
import frc.lib.autoUtil.AutoMode;
import frc.lib.autoUtil.AutoModeEndedException;
import frc.robot.auto.actions.FollowTrejectory;
import frc.robot.subsystems.Swerve;

public class DriveY extends AutoMode {
    private Swerve swerve;
	private String path;

	public DriveY(Swerve swerve) {
		this.swerve = swerve;
		path = "driveY";
	}



	@Override
	protected void routine() throws AutoModeEndedException {
		DriverStation.reportWarning("Starting Auto run", false);
		runAction(new FollowTrejectory(path, swerve));

		DriverStation.reportWarning("Ending Auto run", false);

	}
}
