package ryan.game.games.pirate;

import ryan.game.games.ScoreDisplay;

public class PirateDisplay extends ScoreDisplay {

    public PirateDisplay() {
        super("core/assets/score_display_overboard.png");
    }

    @Override
    public int[] calculateScores() {
        return new int[2];
    }
}
