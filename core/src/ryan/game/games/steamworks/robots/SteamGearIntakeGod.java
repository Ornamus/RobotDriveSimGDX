package ryan.game.games.steamworks.robots;

import ryan.game.autonomous.steamworks.AutoSideGear;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.entity.Robot;

public class SteamGearIntakeGod extends SteamRobotStats {

    public SteamGearIntakeGod() {
        super();

        maxMPS = 16.5f / 3.28084f;
        maxAccel = 15.8448f;

        gearIntake = true;
        gearIntakeRate = 650f/2f;
        gearIntakeStrength = 11f;

        fuelIntake = false;
        shooter = false;
        maxFuel = 0;

        texture = "core/assets/gearpro.png";
        recolorIndex = 1;
    }

    @Override
    public Command getAutonomous(Robot r) {
        return new AutoSideGear(r);
    }
}
