package ryan.game.games;

import ryan.game.Main;
import ryan.game.screens.GameScreen;

public enum Game {

    STEAMWORKS,
    POWERUP,
    OVERBOARD;


    public static boolean isPlaying() {
        return GameScreen.matchPlay;
    }

    public static boolean isAutonomous() {
        return GameScreen.matchPlay && getMatchTime() > 135;
    }

    public static int getMatchTime() {
        int seconds;
        if (GameScreen.matchPlay) {
            long timeIn = Main.getTime() - GameScreen.matchStart;
            long timeLeft = (150 * 1000) - timeIn;
            seconds = Math.round(timeLeft / 1000f);
        } else {
            seconds = 150;
        }
        return seconds;
    }

    public enum ALLIANCE {
        BLUE,
        RED,
        NEUTRAL
    }
}
