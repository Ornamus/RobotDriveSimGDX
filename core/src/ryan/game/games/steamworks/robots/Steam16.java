package ryan.game.games.steamworks.robots;

import ryan.game.autonomous.steamworks.Auto16;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.entity.Robot;

public class Steam16 extends SteamRobotStats {

    public Steam16() {
        super();
        maxMPS = 16.5f / 3.28084f;
        gearIntake = true;
        fuelIntake = true;
        timePerShoot = 200;
        shootPowerVariance = 2.5f;
        maxFuel = 35;
        fieldCentric = true;
        fieldCentricStrafeMult = 1;

        texture = "core/assets/16.png";
        recolorIndex = 1;
    }

    @Override
    public Command getAutonomous(Robot r) {
        return new Auto16(r);
    }
}
