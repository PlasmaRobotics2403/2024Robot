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

public class ThreeFar extends AutoMode {
    private Swerve swerve;
    private StateManager manager;
	private String pathRed1;
    private String pathRed2;
    private String pathBlue1;
    private String pathBlue2;
    private Photon photon;

	public ThreeFar(Swerve swerve, StateManager manager, Photon photon) {
		this.swerve = swerve;
        this.manager = manager;
        this.photon = photon;

		pathBlue1 = "ThreeFarBlue1";
        pathRed1 = "ThreeFarRed1";
        pathBlue2 = "ThreeFarBlue2";
        pathRed2 = "ThreeFarRed2";
	}

	@Override
	protected void routine() throws AutoModeEndedException {
		DriverStation.reportWarning("Starting Auto run", false);

        String selectedPath1;
        String selectedPath2;

        if(DriverStation.getAlliance().get() == Alliance.Blue) {
            selectedPath1 = pathBlue1;
            selectedPath2 = pathBlue2;
        }
        else {
            selectedPath1 = pathRed1;
            selectedPath2 = pathRed2;
        }


        //runAction(new AutoAllign(swerve, photon));
		runAction(new AutoRobotState(manager, robotState.SHOOTAUTO));
        runAction(new Wait(1));
        runAction(new AutoRobotState(manager, robotState.INDEXAUTO));
        runAction(new Wait(1));
        runAction(new AutoRobotState(manager, robotState.INTAKE));
        runAction(new FollowTrejectory(selectedPath1, swerve));
        runAction(new Wait(1));
        runAction(new AutoAllign(swerve, photon));
        runAction(new Wait(0.5));
		runAction(new AutoRobotState(manager, robotState.SHOOTAUTO));
        runAction(new Wait(0.5));
        runAction(new AutoRobotState(manager, robotState.INDEXAUTO));
        runAction(new Wait(0.5));
        runAction(new AutoAllign(swerve, photon));
        runAction(new Wait(1));
        runAction(new AutoRobotState(manager, robotState.INTAKE));
        runAction(new FollowTrejectory(selectedPath2, swerve));
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
