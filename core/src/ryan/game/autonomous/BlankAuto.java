package ryan.game.autonomous;

import ryan.game.bcnlib_pieces.Command;
import ryan.game.entity.Robot;

public class BlankAuto extends Command {


    public BlankAuto(Robot r) {
        super(r);
    }

    @Override
    public void onInit() {

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
