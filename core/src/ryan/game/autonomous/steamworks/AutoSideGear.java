package ryan.game.autonomous.steamworks;

import ryan.game.bcnlib_pieces.Command;
import ryan.game.bcnlib_pieces.Motor;
import ryan.game.bcnlib_pieces.PIDController;
import ryan.game.entity.Robot;
import ryan.game.games.Game;
import ryan.game.games.steamworks.SteamworksMetadata;

public class AutoSideGear extends Command {

    Motor pidOutput;
    PIDController rotatePID;
    float driveForwardSpeedOne = .5f;
    long driveForwardTimeOne = 1800;
    float driveForwardSpeedTwo = .7f;
    long driveForwardTimeTwo = 3500;

    public AutoSideGear(Robot r) {
        super(r);
        pidOutput = new Motor();
        rotatePID = new PIDController(pidOutput, robot.getGyro(), 0.01, 0.0008 * 2, 0.09 * 1.5, 0.15, 1).setRotational(true);
        rotatePID.setFinishedTolerance(1);
    }

    @Override
    public void onInit() {
        SteamworksMetadata meta = (SteamworksMetadata) robot.metadata;
        try {
            robot.getGyro().reset();
            robot.setMotors(driveForwardSpeedOne, driveForwardSpeedOne);
            Thread.sleep(driveForwardTimeOne);
            robot.setMotors(0, 0);
            double target;
            if (robot.getY() > 0) {
                target = robot.blue ? 300 : 60;
            } else {
                target = robot.blue ? 60 : 300;
            }
            rotatePID.setTarget(target);
            rotatePID.enable();
            while (!rotatePID.isDone() && Game.isAutonomous()) {
                robot.setMotors(pidOutput.getPower(), -pidOutput.getPower());
                Thread.sleep(10);
            }
            if (Game.isAutonomous()) {
                rotatePID.disable();
                robot.setMotors(0, 0);
                Thread.sleep(300);
                robot.setMotors(.4f, .4f);
                Thread.sleep(1750);
                meta.ejectGear(robot);
                Thread.sleep(250);
                robot.setMotors(-.4f, -.4f);
                Thread.sleep(1000);
                rotatePID.setTarget(0);
                rotatePID.enable();
                while (!rotatePID.isDone() && Game.isAutonomous()) {
                    robot.setMotors(pidOutput.getPower(), -pidOutput.getPower());
                    Thread.sleep(10);
                }
                if (Game.isAutonomous()) {
                    rotatePID.disable();
                    robot.setMotors(0, 0);
                    Thread.sleep(300);
                    robot.setMotors(driveForwardSpeedTwo, driveForwardSpeedTwo);
                    Thread.sleep(driveForwardTimeTwo);
                }
            }
            robot.setMotors(0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoop() {}

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return true;
    }
}
