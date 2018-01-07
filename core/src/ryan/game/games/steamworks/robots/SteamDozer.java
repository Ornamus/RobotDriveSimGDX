package ryan.game.games.steamworks.robots;

import com.badlogic.gdx.math.Vector2;
import ryan.game.autonomous.steamworks.AutoHopper;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.entity.Robot;

public class SteamDozer extends SteamRobotStats {

    public SteamDozer() {
        super();
        gearIntake = false;
        fuelIntake = true;

        shootHeight = 1.1f;
        shootPower = 24.4f;
        timePerShoot = 200;
        shooterPosition = new Vector2(0, -.5f);
        texture = "core/assets/dozer_recolor.png";
    }

    @Override
    public Command getAutonomous(Robot r) {
        return new AutoHopper(r);
    }
}
