package ryan.game.games.power;

import ryan.game.games.Game;
import ryan.game.games.ScoreDisplay;

public class PowerDisplay extends ScoreDisplay {

    public PowerDisplay() {
        super("core/assets/score_display_overboard.png");
    }

    @Override
    public int[] calculateScores() {
        return new int[]{calculateScore(Game.ALLIANCE.BLUE), calculateScore(Game.ALLIANCE.RED)};
    }

    public int calculateScore(Game.ALLIANCE alliance) {
        int score = 0;
        boolean blue = alliance == Game.ALLIANCE.BLUE;

        return score;
    }
}
