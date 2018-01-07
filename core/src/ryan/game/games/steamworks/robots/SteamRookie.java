package ryan.game.games.steamworks.robots;

import ryan.game.autonomous.steamworks.AutoBaseline;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.entity.Robot;

public class SteamRookie extends SteamRobotStats {

    public SteamRookie() {
        super();

        hasIntake = false;

        gearHPStation = false;
        gearIntake = false;

        fuelIntake = false;
        shooter = false;
        maxFuel = 0;

        climbSpeed = 4;

        texture = "core/assets/rookie.png";
        recolorIndex = 1;
    }

    @Override
    public Command getAutonomous(Robot r) {
        return new AutoBaseline(r);
    }
}
