package ryan.game.games;

import ryan.game.Main;

public enum Game {

    STEAMWORKS,
    OVERBOARD;


    public static boolean isPlaying() {
        return Main.matchPlay;
    }

    public static boolean isAutonomous() {
        return Main.matchPlay && getMatchTime() > 135;
    }

    public static int getMatchTime() {
        int seconds = 0;
        if (Main.matchPlay) {
            long timeIn = System.currentTimeMillis() - Main.matchStart;
            long timeLeft = (150 * 1000) - timeIn;
            seconds = Math.round(timeLeft / 1000f);
        } else {
            seconds = 150;
        }
        return seconds;
    }
}
