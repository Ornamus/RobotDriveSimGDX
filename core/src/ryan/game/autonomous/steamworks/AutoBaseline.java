package ryan.game.autonomous.steamworks;

import ryan.game.bcnlib_pieces.Command;
import ryan.game.entity.Robot;

public class AutoBaseline extends Command {

    long start = 0;

    public AutoBaseline(Robot r) {
        super(r);
    }

    @Override
    public void onInit() {
        robot.setMotors(.7f, .7f);
        start = System.currentTimeMillis();
    }

    @Override
    public void onLoop() {
        if (System.currentTimeMillis() - start >= 1500) {
            robot.setMotors(0, 0);
        }
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
