package ryan.game.games.steamworks.robots;

import ryan.game.autonomous.AutoCenterGear;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.entity.Robot;

public class SteamGearGod extends SteamRobotStats {

    public SteamGearGod() {
        gearIntake = false;
        fuelIntake = false;
        shooter = false;
        maxMPS = 18f / 3.28084f;
        texture = "core/assets/robot_onetrick.png";
    }

    @Override
    public Command getAutonomous(Robot r) {
        return new AutoCenterGear(r);
    }
}