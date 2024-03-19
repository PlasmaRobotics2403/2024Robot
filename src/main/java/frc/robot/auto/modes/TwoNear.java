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

public class TwoNear extends AutoMode {
    private Swerve swerve;
    private StateManager manager;
	private String pathRed;
    private String pathBlue;
    private Photon photon;

	public TwoNear(Swerve swerve, StateManager manager, Photon photon) {
		this.swerve = swerve;
        this.manager = manager;
        this.photon = photon;

		pathBlue = "TwoNearBlue";
        pathRed = "TwoNearRed";
    
	}

	@Override
	protected void routine() throws AutoModeEndedException {
		DriverStation.reportWarning("Starting Auto run", false);

        String selectedPath;

        if(DriverStation.getAlliance().get() == Alliance.Blue) {
            selectedPath = pathBlue;
        }
        else {
            selectedPath = pathRed;
        }

        runAction(new AutoAllign(swerve, photon));
		runAction(new AutoRobotState(manager, robotState.SHOOTAUTO));
        runAction(new Wait(1));
        runAction(new AutoRobotState(manager, robotState.INDEXAUTO));
        runAction(new Wait(1));
        runAction(new AutoRobotState(manager, robotState.INTAKE));
        runAction(new FollowTrejectory(selectedPath, swerve));
        runAction(new Wait(1));
        runAction(new AutoAllign(swerve, photon));
        runAction(new Wait(1));
		runAction(new AutoRobotState(manager, robotState.SHOOTAUTO));
        runAction(new Wait(1));
        runAction(new AutoRobotState(manager, robotState.INDEXAUTO));
        runAction(new Wait(1));
        runAction(new AutoRobotState(manager, robotState.IDLE));

		DriverStation.reportWarning("Ending Auto run", false);

	}
}
