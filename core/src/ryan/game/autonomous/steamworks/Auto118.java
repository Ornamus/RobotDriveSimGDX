package ryan.game.autonomous.steamworks;

import ryan.game.bcnlib_pieces.Command;
import ryan.game.bcnlib_pieces.Motor;
import ryan.game.bcnlib_pieces.PIDController;
import ryan.game.entity.Robot;
import ryan.game.games.Game;
import ryan.game.games.steamworks.SteamworksMetadata;

public class Auto118 extends Command {

    Motor pidOutput;
    PIDController rotatePID;

    public Auto118(Robot r) {
        super(r);
        pidOutput = new Motor();
        rotatePID = new PIDController(pidOutput, r.getGyro(), 0.01, 0.0008 * 2, 0.09 * 1.5, 0.15, 1).setRotational(true);
        rotatePID.setFinishedTolerance(1);
    }

    @Override
    public void onInit() {
        SteamworksMetadata m = (SteamworksMetadata) robot.metadata;
        try {
            robot.getGyro().reset();
            robot.setMotors(1f, 1f);
            Thread.sleep(robot.blue ? 1350 : 1100);
            rotatePID.setTarget(robot.blue ? 270 : 90);
            rotatePID.enable();
            while (!rotatePID.isDone() && Game.isAutonomous()) {
                robot.setMotors(pidOutput.getPower(), -pidOutput.getPower());
                Thread.sleep(10);
            }
            rotatePID.disable();
            robot.setMotors(1f, 1f);
            Thread.sleep(750);
            robot.setMotors(0, 0);
            Thread.sleep(200);
            while (Game.isAutonomous()) {
                m.shootFuel(robot);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoop() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
