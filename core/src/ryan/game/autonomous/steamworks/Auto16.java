package ryan.game.autonomous.steamworks;

import ryan.game.Main;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.bcnlib_pieces.Motor;
import ryan.game.bcnlib_pieces.PIDController;
import ryan.game.entity.Robot;
import ryan.game.games.steamworks.SteamworksMetadata;

public class Auto16 extends Command {

    Motor pidOutput;
    PIDController rotatePID;

    public Auto16(Robot r) {
        super(r);
        pidOutput = new Motor();
        rotatePID = new PIDController(pidOutput, r.getGyro(), 0.01, 0.0008 * 2, 0.09 * 1.5, 0.15, 1).setRotational(true);
        rotatePID.setFinishedTolerance(1);
    }

    @Override
    public void onInit() {
        SteamworksMetadata meta = (SteamworksMetadata) robot.metadata;
        try {
            long start = Main.getTime();
            while (Main.getTime()- start < 3000) {
                meta.shootFuel(robot);
            }
            robot.setMotors(.5f, .5f);
            robot.setMiddleMotor(robot.blue ? .85f : -.85f);
            Thread.sleep(1100);
            robot.setMotors(0, 0);
            robot.setMiddleMotor(0);
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
