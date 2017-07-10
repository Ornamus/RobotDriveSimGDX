package ryan.game.games.steamworks.robots;

import ryan.game.autonomous.steamworks.AutoHopper;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.entity.Robot;

public class SteamDozer extends SteamRobotStats {

    public SteamDozer() {
        super();
        gearIntake = false;
        fuelIntake = true;
        timePerShoot = 200;
        texture = "core/assets/dozer_recolor.png";
    }

    @Override
    public Command getAutonomous(Robot r) {
        return new AutoHopper(r);
    }
}
