package ryan.game.autonomous;

import ryan.game.bcnlib_pieces.Command;
import ryan.game.bcnlib_pieces.Motor;
import ryan.game.bcnlib_pieces.PIDController;
import ryan.game.bcnlib_pieces.PIDSource;
import ryan.game.entity.Robot;
import ryan.game.games.ScoreDisplay;
import ryan.game.games.steamworks.SteamworksMetadata;

import java.util.ServiceConfigurationError;

public class AutoHopper extends Command {

    Motor pidOutput;
    PIDSource gyro;
    PIDController rotatePID;

    public AutoHopper(Robot r) {
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
        SteamworksMetadata m = (SteamworksMetadata) robot.metadata;
        try {
            gyro.reset();
            robot.setMotors(1f, 1f);
            Thread.sleep(1350);
            rotatePID.setTarget(robot.blue ? 270 : 90);
            rotatePID.enable();
            while (!rotatePID.isDone()) {
                robot.setMotors(pidOutput.getPower(), -pidOutput.getPower());
                Thread.sleep(10);
            }
            rotatePID.disable();
            robot.setMotors(1f, 1f);
            Thread.sleep(750);
            robot.setMotors(0, 0);
            Thread.sleep(1000);
            robot.setMotors(-1, -1);
            Thread.sleep(200);
            robot.setMotors(0, 0);
            rotatePID.setTarget(robot.blue ? 188 : 360-188);
            rotatePID.enable();
            while (!rotatePID.isDone()) {
                robot.setMotors(pidOutput.getPower(), -pidOutput.getPower());
                Thread.sleep(10);
            }
            rotatePID.disable();
            robot.setMotors(0, 0);
            while (ScoreDisplay.getMatchTime() > 135) {
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
