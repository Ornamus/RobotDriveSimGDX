package ryan.game.competition;

import com.badlogic.gdx.graphics.Color;

public class Team {

    public int number;
    public String name;
    public final Color primary, secondary;
    public RobotStats robotStats;

    public Team(int number, Color primary, Color secondary, RobotStats robotStats) {
        this.number = number;
        this.primary = primary;
        this.secondary = secondary;
        this.robotStats = robotStats;
    }
}
