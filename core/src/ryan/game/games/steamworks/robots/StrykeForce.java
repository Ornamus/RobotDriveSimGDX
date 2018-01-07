package ryan.game.games.steamworks.robots;

import ryan.game.autonomous.steamworks.AutoStrykeForce;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.entity.Robot;

public class StrykeForce extends SteamRobotStats {

    public StrykeForce() {
        super();
        //maxMPS = 15.7f / 3.28084f;

        gearIntake = true;
        gearIntakeRate = 800f;
        gearIntakeStrength = 10f;

        fuelIntake = true;
        timePerShoot = 164;
        shootPowerVariance = 2.5f;
        maxFuel = 40;

        fieldCentric = true;
        fieldCentricStrafeMult = 1;

        texture = "core/assets/2767.png";
        recolorIndex = 1;
    }

    @Override
    public Command getAutonomous(Robot r) {
        return new AutoStrykeForce(r);
    }
}
