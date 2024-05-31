package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystems.Climb;
import frc.robot.subsystems.Index;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.LEDs;
import frc.robot.subsystems.Photon;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Swerve;
import frc.robot.subsystems.Climb.climbState;
import frc.robot.subsystems.Index.indexState;
import frc.robot.subsystems.Intake.intakeState;
import frc.robot.subsystems.LEDs.LEDState;
import frc.robot.subsystems.Shooter.shooterState;


public class StateManager {
    private Intake intake;
    private Shooter shooter;
    private Index index;
    private Climb climb;
    private LEDs leds;
    private Photon photon;
    private Swerve swerve;

    private boolean hasGamePiece;
    private boolean gamePieceInPos;

    public robotState currentState;
    public enum robotState{
        INTAKE,
        IDLE,
        SHOOT,
        EJECT,
        AMP,
        STATICSHOOT,
        CLIMB_HOOKS_UP,
        CLIMB_HOOKS_DOWN,
        CLIMBFALSE,
        INDEXAUTO,
        SHOOTAUTO,
        TRAP,
        SHUTTLE

     }
    public StateManager(Intake intake, Shooter shooter, Index index, Climb climb, LEDs leds, Photon photon, Swerve swerve) {
        this.intake = intake;
        this.shooter = shooter;
        this.index = index;
        this.climb = climb;
        this.leds = leds;
        this.photon = photon;
        this.swerve = swerve;

        currentState = robotState.IDLE;
        hasGamePiece = false;
        gamePieceInPos = false;
        climb.setState(climbState.CLIMBFALSE);
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
        SmartDashboard.putBoolean("Game peice in position", gamePieceInPos);
    }

    public void periodic() {
        logging();
        if(DriverStation.isDisabled()) {
            leds.setState(LEDState.BOGO);
        }
        else if(photon.isAligned() && hasGamePiece) {
            leds.setState(LEDState.ALLIGNED);
        }
        else if(hasGamePiece) {
            leds.setState(LEDState.HASPEICE);
        }
        else if(!hasGamePiece) {
            leds.setState(LEDState.NOPEICE);
        }

        switch (currentState) {
            case IDLE:
                climb.setState(climbState.OFF);

                if(climb.climbRaised()) {
                    shooter.setState(shooterState.CLIMB);
                }
                else{
                    shooter.setState(shooterState.OFF);
                }

                // game piece in position
                if(gamePieceInPos) {
                    index.setState(indexState.OFF);
                    intake.setState(intakeState.STOW);
                    hasGamePiece = true;
                }
                else if(index.getShooterSensor()) {
                    intake.setState(intakeState.STOW);
                    index.setState(indexState.OFF);
                    gamePieceInPos = true;
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
            case STATICSHOOT:
                double angle;
                if(swerve.isFasingForward()) {
                    shooter.setState(shooterState.STATICSHOOTFRONT);
                    angle = Constants.ShooterConstants.staticFront;
                }
                else {
                    shooter.setState(shooterState.STATICSHOOTBACK);
                    angle = Constants.ShooterConstants.staticBack;
                }
                intake.setState(intakeState.STOW);

            
                if(shooter.readyToShoot(Constants.ShooterConstants.shooterRPS*.9, angle)) {
                    index.setState(indexState.SHOOT);
                    hasGamePiece = false;
                    gamePieceInPos = false;
                }
                break;
            case TRAP:
                shooter.setState(shooterState.TRAP);
                intake.setState(intakeState.STOW);
            
                if(shooter.readyToShoot(Constants.ShooterConstants.trapRPS, Constants.ShooterConstants.trapAngle)) {
                    index.setState(indexState.SHOOT);
                    hasGamePiece = false;
                    gamePieceInPos = false;
                }
                break;
            case INTAKE:
                shooter.setState(shooterState.OFF);

                // game piece in poition
                if(gamePieceInPos) {
                    index.setState(indexState.OFF);
                    intake.setState(intakeState.STOW);
                }

                // passing through
                else if(hasGamePiece) {
                    if(index.getShooterSensor()) {
                        gamePieceInPos = true;
                    }
                    intake.setState(intakeState.STOW);
                    index.setState(indexState.PASSTHROUGH);
                }

                // intaking
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
                gamePieceInPos = false;
                break;
            case SHOOT:
                shooter.setState(shooterState.RPS);
                intake.setState(intakeState.STOW);
            
                if(shooter.readyToShoot(Constants.ShooterConstants.shooterRPS*.9)) {
                    index.setState(indexState.SHOOT);
                    hasGamePiece = false;
                    gamePieceInPos = false;
                }
                break;
            case AMP:
                shooter.setState(shooterState.AMP);
                intake.setState(intakeState.STOW);
                if(shooter.readyToShoot(Constants.ShooterConstants.ampRPS*.97, Constants.ShooterConstants.ampAngle)) {
                    index.setState(indexState.SHOOT);
                    hasGamePiece = false;
                    gamePieceInPos = false;

                }
                else{
                    index.setState(indexState.OFF);
                }
                break;
            case CLIMB_HOOKS_UP:
                shooter.setState(shooterState.CLIMB);
                climb.setState(climbState.HOOKS_UP_PERCENT);
                intake.setState(intakeState.STOW);
                index.setState(indexState.OFF);
                break;
            case CLIMB_HOOKS_DOWN:
                shooter.setState(shooterState.CLIMB);
                climb.setState(climbState.HOOKS_DOWN_PERCENT);
                intake.setState(intakeState.STOW);
                index.setState(indexState.OFF);
                break;
            case CLIMBFALSE:
                climb.setState(climbState.CLIMBFALSE);
                break;
            case SHOOTAUTO:
                shooter.setState(shooterState.RPS);
                intake.setState(intakeState.STOW);
                hasGamePiece = false;
                gamePieceInPos = false;
                break;
            case INDEXAUTO:
                index.setState(indexState.SHOOT);
                hasGamePiece = false;
                gamePieceInPos = false;
                break;

            case SHUTTLE:
                shooter.setState(shooterState.SHUTTLE);
                if(shooter.readyToShoot(Constants.ShooterConstants.shooterRPS*.9)) {
                    index.setState(indexState.SHOOT);
                    hasGamePiece = false;
                    gamePieceInPos = false;
                }
        }

    }
}
