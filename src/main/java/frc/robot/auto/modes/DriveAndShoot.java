package frc.robot.auto.modes;

import edu.wpi.first.wpilibj.DriverStation;
import frc.lib.autoUtil.AutoMode;
import frc.lib.autoUtil.AutoModeEndedException;
import frc.robot.StateManager;
import frc.robot.StateManager.robotState;
import frc.robot.auto.actions.AutoRobotState;
import frc.robot.auto.actions.FollowTrejectory;
import frc.robot.auto.actions.Wait;
import frc.robot.subsystems.Swerve;
import frc.robot.subsystems.Shooter.shooterState;

public class DriveAndShoot extends AutoMode {
    private Swerve swerve;
	private String path;
    private StateManager manager;

	public DriveAndShoot(Swerve swerve, StateManager manager) {
		this.swerve = swerve;
        this.manager = manager;
		path = "driveForward";
	}



	@Override
	protected void routine() throws AutoModeEndedException {
		DriverStation.reportWarning("Starting Auto run", false);
        runAction(new AutoRobotState(manager, robotState.SHOOT));
        runAction(new Wait(0.5));
        runAction(new AutoRobotState(manager, robotState.IDLE));
		runAction(new FollowTrejectory(path, swerve));
		DriverStation.reportWarning("Ending Auto run", false);

	}
}
