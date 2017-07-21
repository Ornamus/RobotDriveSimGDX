package ryan.game.games.steamworks.robots;

import com.badlogic.gdx.math.Vector2;
import ryan.game.competition.RobotStats;
import ryan.game.games.Game;

public abstract class SteamRobotStats extends RobotStats {

    public boolean gearIntake = true;
    public float gearIntakeRate = 650;
    public float gearIntakeStrength = 10f;

    public boolean fuelIntake = true;
    public float fuelIntakeRate = 250;
    public float fuelIntakeStrength = 1f;

    public boolean shooter = true;
    public float timePerShoot = 166f;
    public boolean shooterIsTurret = false;
    public float turretWidth = .275f;
    public float turretHeight = .275f;
    public Vector2 shooterTurretPivot = new Vector2(0, 0);
    public String turretTexture = "core/assets/118_turret.png";

    public float shootHeight = 1f;
    public float shootHeightVariance = .1f;
    public float shootPower = 24;
    public float shootPowerVariance = 3;
    public float shootAngleVariance = 2;
    public float maxFuel = 45;

    public boolean climber = true;
    public float climbSpeed = 2f;
    public float climbSuccess = 1f;

    public float gearScoreSuccess = 1f;
    public float gearDropOnCollide = 0f;

    public SteamRobotStats() {
        super(Game.STEAMWORKS);
        intakeWidth = robotWidth * .9f;
    }

    //TODO: more ai info

}
