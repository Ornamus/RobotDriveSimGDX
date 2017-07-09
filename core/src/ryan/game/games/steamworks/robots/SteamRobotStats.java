package ryan.game.games.steamworks.robots;

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
    public float maxFuel = 45;

    public float climbSpeed = 2f;


    public float gearScoreSuccess = 1f;
    public float gearDropOnCollide = 0f;
    public float climbSuccess = 1f;

    public SteamRobotStats() {
        super(Game.STEAMWORKS);
    }

    //TODO: more ai info

}
