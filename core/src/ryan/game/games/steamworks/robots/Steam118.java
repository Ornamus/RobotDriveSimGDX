package ryan.game.games.steamworks.robots;

import com.badlogic.gdx.math.Vector2;
import ryan.game.autonomous.steamworks.Auto118;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.entity.Robot;

public class Steam118 extends SteamRobotStats {

    public Steam118() {
        shooter = true;
        shooterIsTurret = true;
        shooterPosition = new Vector2(-.5f, -.5f);
        timePerShoot = 163f;

        shootHeight = 1.15f;
        shootPower = 23;
        shootPowerVariance = 2.5f;
        shootAngleVariance = 1;

        texture = "core/assets/118.png";
        recolorIndex = 2;
    }

    @Override
    public Command getAutonomous(Robot r) {
        return new Auto118(r);
    }
}
