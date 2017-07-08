package ryan.game.games.steamworks.robots;

import ryan.game.competition.RobotStats;
import ryan.game.games.Game;

public abstract class SteamRobotStats extends RobotStats {

    public boolean gearIntake = true;

    public boolean fuelIntake = true;
    public boolean shooter = true;
    public float timePerShoot = 166f;
    public float maxFuel = 45;

    public float climbSpeed = 1f;


    public float gearScoreSuccess = 1f;
    public float gearDropOnCollide = 0f;
    public float fuelPickupRate = 0.000001f;
    public float climbSuccess = 1f;

    public SteamRobotStats() {
        super(Game.STEAMWORKS);
    }

    //TODO: more ai info

}
