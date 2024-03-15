package frc.robot.auto.modes;

import java.sql.DriverAction;
import java.util.Optional;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.lib.autoUtil.AutoMode;
import frc.lib.autoUtil.AutoModeEndedException;
import frc.robot.StateManager;
import frc.robot.StateManager.robotState;
import frc.robot.auto.actions.AutoRobotState;
import frc.robot.auto.actions.FollowTrejectory;
import frc.robot.auto.actions.Wait;
import frc.robot.subsystems.Swerve;

public class TwoCenter extends AutoMode {
    private Swerve swerve;
    private StateManager manager;
	private String pathBlue;
    private String pathRed;

	public TwoCenter(Swerve swerve, StateManager manager) {
		this.swerve = swerve;
        this.manager = manager;

		pathBlue = "TwoCenterBlue";
        pathRed = "TwoCenterRed";
        
	}

	@Override
	protected void routine() throws AutoModeEndedException {
		DriverStation.reportWarning("Starting Auto run", false);

        String selectedPath;
        if(DriverStation.getAlliance().get() == Alliance.Blue){
            selectedPath = pathBlue;
        }
        else{
            selectedPath = pathRed;
        }

		runAction(new AutoRobotState(manager, robotState.SHOOT));
        runAction(new Wait(1));
        runAction(new AutoRobotState(manager, robotState.INTAKE));
        runAction(new Wait(0.5));
        runAction(new FollowTrejectory(selectedPath, swerve));
        runAction(new AutoRobotState(manager, robotState.SHOOT));
        runAction(new Wait(1));
        runAction(new AutoRobotState(manager, robotState.IDLE));
		DriverStation.reportWarning("Ending Auto run", false);

	}
}
