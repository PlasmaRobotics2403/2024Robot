package frc.robot.auto.modes;

import java.sql.DriverAction;
import java.util.Optional;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.lib.autoUtil.AutoMode;
import frc.lib.autoUtil.AutoModeEndedException;
import frc.robot.StateManager;
import frc.robot.StateManager.robotState;
import frc.robot.auto.actions.AutoAllign;
import frc.robot.auto.actions.AutoRobotState;
import frc.robot.auto.actions.FollowTrejectory;
import frc.robot.auto.actions.Wait;
import frc.robot.subsystems.Photon;
import frc.robot.subsystems.Swerve;

public class AimShoot extends AutoMode {
    private StateManager manager;
	private String pathRed;
    private String pathBlue;
    private Photon photon;

	public AimShoot(StateManager manager, Photon photon) {
        this.manager = manager;
        this.photon = photon;


    
	}

	@Override
	protected void routine() throws AutoModeEndedException {
		DriverStation.reportWarning("Starting Auto run", false);
		runAction(new AutoRobotState(manager, robotState.SHOOTAUTO));
		runAction(new Wait(1));
        runAction(new AutoRobotState(manager, robotState.INDEXAUTO));
        runAction(new Wait(1));
		runAction(new AutoRobotState(manager, robotState.IDLE));

		DriverStation.reportWarning("Ending Auto run", false);

	}
}
