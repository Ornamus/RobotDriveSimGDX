package ryan.game.autonomous.steamworks;

import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.bcnlib_pieces.Motor;
import ryan.game.bcnlib_pieces.PIDController;
import ryan.game.entity.Robot;
import ryan.game.games.Game;
import ryan.game.games.steamworks.SteamworksMetadata;

public class Auto1902 extends Command {

    Motor pidOutput;
    PIDController rotatePID;

    public Auto1902(Robot r) {
        super(r);
        pidOutput = new Motor();
        rotatePID = new PIDController(pidOutput, r.getGyro(), 0.01, 0.0008 * 2, 0.09 * 1.5, 0.15, 1).setRotational(true);
        rotatePID.setFinishedTolerance(1);
    }

    @Override
    public void onInit() {
        SteamworksMetadata meta = (SteamworksMetadata) robot.metadata;
        float startAngle = (float) robot.getGyro().getForPID();
        try {
            robot.setMotors(1, 1);
            Thread.sleep(600);
            robot.setMotors(0, 0);
            long start = Main.getTime();
            while (Main.getTime() - start < 3000) {
                meta.shootFuel(robot);
                Utils.log("shoot");
            }
            Utils.log("releas me");
            robot.setMotors(-1, -1);
            Thread.sleep(600);
            robot.setMotors(0, 0);
            Thread.sleep(300);
            robot.setMiddleMotor(robot.blue ? 1 : -1);
            Thread.sleep(500);
            robot.setMiddleMotor(0);
            Thread.sleep(400);
            rotatePID.setTarget(startAngle + (robot.blue ? 90 : -90));
            rotatePID.enable();
            while (!rotatePID.isDone() && Game.isAutonomous()) {
                robot.setMotors(pidOutput.getPower(), -pidOutput.getPower());
                Thread.sleep(10);
            }
            rotatePID.disable();
            robot.setMotors(.5f, .5f);
            Thread.sleep(2000);
            robot.setMotors(0, 0);
            meta.ejectGear(robot);
            robot.setMotors(-1f, 1f);
            Thread.sleep(250);
            robot.setMotors(0, 0);
            robot.setMiddleMotor(robot.blue ? 1 : -1);
            Thread.sleep(1400);
            robot.setMiddleMotor(0);
            robot.setMotors(1, 1);
            Thread.sleep(2000);
            robot.setMotors(0, 0);
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
