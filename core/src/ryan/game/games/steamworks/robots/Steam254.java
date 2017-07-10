package ryan.game.games.steamworks.robots;

import ryan.game.autonomous.steamworks.Auto254;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.entity.Robot;

public class Steam254 extends SteamRobotStats {

    public Steam254() {
        super();
        //intakeWidth = robotWidth; //TODO: reeneable when this is redone when switched to
        gearIntake = true;
        fuelIntake = true;
        texture = "core/assets/254.png";
    }

    @Override
    public Command getAutonomous(Robot r) {
        return new Auto254(r);
    }
}
