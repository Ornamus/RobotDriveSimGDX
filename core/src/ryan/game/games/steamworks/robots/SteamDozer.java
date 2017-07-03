package ryan.game.games.steamworks.robots;

import ryan.game.autonomous.AutoHopper;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.entity.Robot;

public class SteamDozer extends SteamRobotStats {

    public SteamDozer() {
        gearIntake = false;
        fuelIntake = true;
        texture = "core/assets/dozer_recolor.png";
    }

    @Override
    public Command getAutonomous(Robot r) {
        return new AutoHopper(r);
    }
}
