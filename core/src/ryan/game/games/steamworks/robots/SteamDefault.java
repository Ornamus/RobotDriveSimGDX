package ryan.game.games.steamworks.robots;

import com.badlogic.gdx.physics.box2d.Body;
import ryan.game.autonomous.steamworks.AutoSideGear;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Robot;
import ryan.game.entity.parts.Intake;

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
