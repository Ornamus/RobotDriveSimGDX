package ryan.game.autonomous.steamworks;

import ryan.game.bcnlib_pieces.Command;
import ryan.game.entity.Robot;
import ryan.game.games.steamworks.SteamworksMetadata;

public class AutoCenterGear extends Command {

    boolean spam = false;

    public AutoCenterGear(Robot r, boolean doSpam) {
        super(r);
        spam = doSpam;
    }

    public AutoCenterGear(Robot r) {
        super(r);
    }

    @Override
    public void onInit() {
        try {
            robot.setMotors(.5f, .5f);
            Thread.sleep(2000);
            robot.setMotors(0, 0);
            SteamworksMetadata m = (SteamworksMetadata) robot.metadata;
            m.ejectGear(robot);
            if (spam) {
                robot.setMotors(-1, -1);
                Thread.sleep(400);
                if (robot.blue) robot.setMotors(1, -1);
                else robot.setMotors(-1, 1);
                Thread.sleep(600);
                robot.setMotors(1, 1);
                Thread.sleep(1100);
                if (robot.blue) robot.setMotors(-1, 1);
                else robot.setMotors(1, -1);
                Thread.sleep(600);
                robot.setMotors(1, 1);
                Thread.sleep(2000);
                robot.setMotors(0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoop() {
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
