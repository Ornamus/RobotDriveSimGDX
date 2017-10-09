package ryan.game.games.overboard;

import ryan.game.entity.overboard.Chest;
import ryan.game.entity.overboard.Ship;
import ryan.game.games.Game;
import ryan.game.games.ScoreDisplay;

public class PirateDisplay extends ScoreDisplay {

    public PirateDisplay() {
        super("core/assets/score_display_overboard.png");
    }

    @Override
    public int[] calculateScores() {
        return new int[]{calculateScore(Game.ALLIANCE.BLUE), calculateScore(Game.ALLIANCE.RED)};
    }

    public int calculateScore(Game.ALLIANCE alliance) {
        int score = 0;
        boolean blue = alliance == Game.ALLIANCE.BLUE;
        for (Ship s : Overboard.ships) {
            if (s.blue == blue) {
                for (Chest c : s.scoredChests.keySet()) {
                    int secondsOfScore = s.scoredChests.get(c);
                    if (secondsOfScore > 135) {
                        if (c.isHeavy()) score += 50;
                        else score += 6;
                    } else {
                        if (c.isHeavy()) score += 20;
                        else score += 3;
                    }
                }
                if (Game.getMatchTime() <= 30) {
                    if (s.bottomRampRobots.size() > 0) score += 10;
                    if (s.topRampRobots.size() > 0) score += 10;
                }
            }
        }
        return score;
    }
}
