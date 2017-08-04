package ryan.game.games.steamworks.robots;

import ryan.game.autonomous.steamworks.AutoSideGear;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.entity.Robot;

public class SteamDefault extends SteamRobotStats {

    public SteamDefault() {
        super();
        gearIntake = true;
        fuelIntake = false;
        timePerShoot = 250;
        shootPower = 9;
        shootPowerVariance = 1;
    }

    @Override
    public Command getAutonomous(Robot r) {
        return new AutoSideGear(r);
    }
}
