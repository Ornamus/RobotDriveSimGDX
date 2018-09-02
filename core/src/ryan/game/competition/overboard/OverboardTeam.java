package ryan.game.competition.overboard;

import com.badlogic.gdx.graphics.Color;
import ryan.game.competition.Team;
import ryan.game.games.overboard.robots.OverRobotStats;

public class OverboardTeam extends Team {

    public final OverRobotStats stats;

    public OverboardTeam(int number, String name, Color primary, Color secondary, OverRobotStats s) {
        super(number, primary, secondary, s);
        stats = s;
    }
}
