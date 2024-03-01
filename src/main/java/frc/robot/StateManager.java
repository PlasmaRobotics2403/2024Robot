package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystems.Index;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Index.indexState;
import frc.robot.subsystems.Intake.intakeState;
import frc.robot.subsystems.Shooter.shooterState;

public class StateManager {
    private Intake intake;
    private Shooter shooter;
    private Index index;

    private boolean hasGamePiece;

    public robotState currentState;
    public enum robotState{
        INTAKE,
        IDLE,
        SHOOT,
        EJECT
    }
    public StateManager(Intake intake, Shooter shooter, Index index) {
        this.intake = intake;
        this.shooter = shooter;
        this.index = index;

        currentState = robotState.IDLE;
        hasGamePiece = false;
    }

    public void setState(robotState state) {
        currentState = state;
    }

    public robotState robotState() {
        return currentState;
    }

    public void logging() {
        SmartDashboard.putString("Robot State", currentState.toString());
        SmartDashboard.putBoolean("Has game peice", hasGamePiece);

    }

    public void periodic() {
        logging();
        switch (currentState) {
            case IDLE:
                shooter.setState(shooterState.OFF);

                if(index.getShooterSensor()) {
                    intake.setState(intakeState.STOW);
                    index.setState(indexState.OFF);
                    hasGamePiece = true;
                }

                else if(hasGamePiece) {
                    intake.setState(intakeState.STOW);
                    index.setState(indexState.PASSTHROUGH);
                }

                else {
                    intake.setState(intakeState.STOW);
                    index.setState(indexState.OFF);
                }
                break;
            case INTAKE:
                shooter.setState(shooterState.OFF);
                
                if(index.getShooterSensor()) {
                    index.setState(indexState.OFF); 
                    intake.setState(intakeState.STOW);
                }

                else if(hasGamePiece) {
                    intake.setState(intakeState.STOW);
                    index.setState(indexState.PASSTHROUGH);
                }
                else {
                    if(intake.getSensor()){
                        hasGamePiece = true;
                    }
                    intake.setState(intakeState.INTJECT);
                    index.setState(indexState.INTAKE);
                }
                break;
            case EJECT:
                intake.setState(intakeState.EJECT);
                index.setState(indexState.EJECT);
                shooter.setState(shooterState.OFF);
                hasGamePiece = false;
                break;
            case SHOOT:
                shooter.setState(shooterState.RPS);
                intake.setState(intakeState.STOW);

            
                if(shooter.readyToShoot()) {
                    index.setState(indexState.SHOOT);
                    hasGamePiece = false;
                }
                break;
        }
    }
}
