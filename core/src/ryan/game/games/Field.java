package ryan.game.games;

import ryan.game.Main;
import ryan.game.competition.Match;
import ryan.game.competition.RobotStats;
import ryan.game.games.steamworks.SteamResultDisplay;
import ryan.game.render.Drawable;
import java.util.List;

public abstract class Field extends Drawable {

    public abstract List<Drawable> generateField();

    public abstract void affectRobots();

    public abstract void onMatchStart();

    public abstract void onMatchEnd();

    public abstract void resetField(List<Drawable> field);

    public abstract ScoreDisplay getDisplay();

    public String getGameString(Game.ALLIANCE alliance) {
        return null;
    }

    public void updateMatchInfo() {
        if (Main.schedule != null) {
            Match m = Main.schedule.getCurrentMatch();
            if (m != null) {
                ScoreDisplay d = getDisplay();
                d.setBlueTeams(m.blue.teams[0], m.blue.teams[1], m.blue.teams[2]);
                d.setRedTeams(m.red.teams[0], m.red.teams[1], m.red.teams[2]);
                d.setMatchName(m.getName());
            }
        }
    }

    public void showResults(ResultDisplay d) {
        Main.getInstance().results = d;
        Main.getInstance().addDrawable(Main.getInstance().results);
        Main.getInstance().isShowingResults = true;
    }

    public abstract RobotStats getDefaultRobotStats();
}
