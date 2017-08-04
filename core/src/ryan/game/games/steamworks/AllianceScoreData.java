package ryan.game.games.steamworks;

import java.io.Serializable;

public class AllianceScoreData implements Serializable {

    public final boolean blue;

    public int gears = 0;
    public int gearsInAuto = 0;
    public int gearQueue = 0;
    public int fuel = 0;
    public int fuelInAuto = 0;
    public int fouls = 0;
    public int bonusClimbs = 0;
    public int rankingPoints = 0;

    public int score = 0;
    public int rotors = 0;
    public int rotorPoints = 0;
    public int kPA = 0;
    public int climbs = 0;
    public int crosses = 0;

    public AllianceScoreData(boolean b) {
        blue = b;
    }

    public void scoreUpdateReset() {
        score = 0;
        rotors = 0;
        rotorPoints = 0;
        kPA = 0;
        climbs = 0;
        crosses = 0;
    }
}
