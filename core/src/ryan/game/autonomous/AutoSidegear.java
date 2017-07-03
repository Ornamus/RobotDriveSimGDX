package ryan.game.autonomous;

import ryan.game.Utils;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.bcnlib_pieces.Motor;
import ryan.game.bcnlib_pieces.PIDController;
import ryan.game.bcnlib_pieces.PIDSource;
import ryan.game.entity.Robot;
import ryan.game.games.steamworks.SteamworksMetadata;

public class AutoSidegear extends Command {

    Motor pidOutput;
    PIDSource gyro;
    PIDController rotatePID;

    public AutoSidegear(Robot r) {
        super(r);
        pidOutput = new Motor();
        gyro = new PIDSource() {
            double fakeReset = 0;
            @Override
            public double getForPID() {
                double adjustAngle = -robot.getAngle() + fakeReset;

                if (adjustAngle < 0) {
                    adjustAngle = 360 + adjustAngle;
                }
                while (adjustAngle > 360) {
                    adjustAngle -= 360;
                }
                //Utils.log("ANGLE: " + adjustAngle);
                return adjustAngle;
            }

            @Override
            public void reset() {
                fakeReset = (double) robot.getAngle();
            }
        };
        rotatePID = new PIDController(pidOutput, gyro, 0.01, 0.0008 * 2, 0.09 * 1.5, 0.15, 1).setRotational(true);
        rotatePID.setFinishedTolerance(1);
    }

    @Override
    public void onInit() {
        SteamworksMetadata meta = (SteamworksMetadata) robot.metadata;
        try {
            gyro.reset();
            robot.setMotors(.5f, .5f);
            Thread.sleep(1800);
            robot.setMotors(0, 0);
            rotatePID.setTarget(robot.blue ? 300 : 60);
            rotatePID.enable();
            while (!rotatePID.isDone()) {
                robot.setMotors(pidOutput.getPower(), -pidOutput.getPower());
                Thread.sleep(10);
            }
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
            while (!rotatePID.isDone()) {
                robot.setMotors(pidOutput.getPower(), -pidOutput.getPower());
                Thread.sleep(10);
            }
            rotatePID.disable();
            robot.setMotors(0, 0);
            Thread.sleep(300);
            robot.setMotors(.7f, .7f);
            Thread.sleep(3500);
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
