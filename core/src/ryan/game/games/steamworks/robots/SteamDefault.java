package ryan.game.games.steamworks.robots;

import ryan.game.autonomous.Auto254;
import ryan.game.autonomous.AutoSideGear;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.entity.Robot;

public class SteamDefault extends SteamRobotStats {

    public SteamDefault() {
        gearIntake = true;
        fuelIntake = false;
    }

    @Override
    public Command getAutonomous(Robot r) {
        return new AutoSideGear(r);
    }
}
