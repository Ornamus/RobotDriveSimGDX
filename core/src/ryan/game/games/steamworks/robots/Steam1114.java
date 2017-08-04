package ryan.game.games.steamworks.robots;

import com.badlogic.gdx.math.Vector2;
import ryan.game.autonomous.steamworks.AutoCenterGear;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.entity.Robot;

public class Steam1114 extends SteamRobotStats {

    public Steam1114() {
        super();
        maxMPS = 17.5f / 3.28084f; //18f originally
        gearIntake = false;

        fuelIntake = false;
        shooterIsTurret = true;
        shooterPosition = new Vector2(.6f, 0);
        timePerShoot = 170;
        shootHeight = 1.2f;
        shootPower = 20;
        shootPowerVariance = 2.4f;
        shootAngleVariance = 1.4f;

        texture = "core/assets/1114.png";
        recolorIndex = 2;
        turretTexture = "core/assets/1114_turret.png";
    }

    @Override
    public Command getAutonomous(Robot r) {
        return new AutoCenterGear(r);
    }
}