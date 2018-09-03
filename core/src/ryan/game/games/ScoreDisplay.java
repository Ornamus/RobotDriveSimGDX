package ryan.game.games;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.entity.Robot;
import ryan.game.render.Drawable;
import ryan.game.render.Fonts;
import ryan.game.screens.GameScreen;

public abstract class ScoreDisplay extends Drawable {

    public int seconds = 150;
    Sprite display;
    Sprite timerBacking;
    Sprite timerBar;

    int[] blueTeams = new int[]{1902, 254, 987};
    int[] redTeams = new int[]{118, 1986, 180};
    String matchName = "Semifinal 2 of 4";
    String eventName = GameScreen.EVENT_NAME;

    float timerBackingWidth = 0;
    float timerBackingHeight = 0;

    public ScoreDisplay() {
        this("score_display_gamedefault.png");
    }

    public ScoreDisplay(String texture) {
        display = new Sprite(new Texture(texture));
        display.setBounds(0, 0, 1100, 145);
        display.setPosition(0 - (display.getWidth() / 2), -320);

        setDrawScaled(false);
        setX(display.getX());
        setY(display.getY());

        timerBacking = new Sprite(new Texture("core/assets/timer_backing.png"));
        timerBacking.setSize(timerBacking.getWidth() * .75f, timerBacking.getHeight() * .75f);
        timerBacking.setAlpha(.75f);
        timerBacking.setPosition(0 - (timerBacking.getWidth() / 2), getY() + 72);

        timerBackingWidth = timerBacking.getWidth();
        timerBackingHeight = timerBacking.getHeight();

        timerBar = new Sprite(Utils.colorImage("core/assets/whitepixel.png", Utils.toColor(39, 124, 28)));
        timerBar.setAlpha(1f);
    }

    boolean wasMatch = false;
    boolean startedTeleop = false;

    @Override
    public void tick() {
        display.setBounds(0, 0, Main.screenWidth, Main.screenHeight*0.23015873f);
        display.setPosition(0 - (display.getWidth() / 2), -Main.screenHeight/2);

        setX(display.getX());
        setY(display.getY());

        timerBacking.setSize(timerBackingWidth * Main.fontScale, timerBackingHeight * Main.fontScale);
        timerBacking.setAlpha(.75f);
        timerBacking.setPosition(-timerBacking.getWidth() / 2, getY() + 128);

        timerBar.setBounds(-222, getY() + 123.5f, ((150f-seconds)/150f) * 431.8f, 49f);

        if (GameScreen.matchPlay) {
            if (seconds > 135) {
                if (!wasMatch) {
                    for (Robot r : GameScreen.robots) {
                        if (r.auto != null) {
                            r.auto.start();
                        }
                    }
                }
            } else {
                if (!startedTeleop) {
                    for (Robot r : GameScreen.robots) {
                        if (r.auto != null && r.auto.isRunning()) {
                            r.auto.stop();
                        }
                    }
                   GameScreen.teleopStartSound.play(.45f);
                    startedTeleop = true;
                }
            }
        } else {
            startedTeleop = false;
        }
        wasMatch = GameScreen.matchPlay;
    }

    @Override
    public void draw(SpriteBatch batch) {
        display.draw(batch);

        seconds = Game.getMatchTime();

        timerBar.draw(batch);
        timerBar.setTexture(Utils.colorImage("core/assets/whitepixel.png", (seconds <= 30 ? Color.YELLOW : Utils.toColor(39, 124, 28))));

        timerBacking.draw(batch);

        int[] scores = {0, 0};
        if (Game.isPlaying()) scores = calculateScores();
        int blueScore = scores[0];
        int redScore = scores[1];

        Fonts.drawCentered(Fonts.fmsScore, blueScore + "", 115, getY() + 95, batch); //blue score
        Fonts.drawCentered(Fonts.fmsScore, redScore + "", -115, getY() + 95, batch); //red score

        Fonts.drawCentered(Fonts.fmsBlack, matchName, -307.5f, getY()+221, batch);
        Fonts.drawCentered(Fonts.fmsBlack, eventName, 307.5f, getY()+221, batch);

        Fonts.drawCentered(Fonts.fmsBlack, GameScreen.matchPlay ? (seconds > 135 ? seconds - 135 : seconds) + "" : "Infinite",
                0, getY() + 158 , batch);

        for (int i=0; i<redTeams.length; i++) {
            int team = redTeams[i];
            Fonts.drawCentered(Fonts.fmsBlack, team + "", -282.2f, getY()+((92 - (i*21)) * 1.7f), batch);
        }
        if (GameScreen.schedule.elims) Fonts.drawCentered(Fonts.fmsBlackSmall, GameScreen.schedule.getSeed(redTeams) + "", 0, getY(), -166, 25, batch);

        for (int i=0; i<blueTeams.length; i++) {
            int team = blueTeams[i];
            Fonts.drawCentered(Fonts.fmsBlack, team + "", 282.2f, getY()+((92 - (i*21)) * 1.7f), batch);
        }
        if (GameScreen.schedule.elims) Fonts.drawCentered(Fonts.fmsBlackSmall, GameScreen.schedule.getSeed(blueTeams) + "", 0, getY(), 166, 25, batch);
    }

    /**
     * Calculates the Blue and Red AllianceScoreData's scores.
     * @return An int[] of the scores. Index 0 is Blue's score, Index 1 is Red's score.
     */
    public abstract int[] calculateScores();

    public int[] getBlueTeams() {
        return blueTeams;
    }

    public int[] getRedTeams() {
        return redTeams;
    }

    public void setBlueTeams(int...b) {
        blueTeams = b;
    }

    public void setRedTeams(int...r) {
        redTeams = r;
    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String m) {
        matchName = m;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String e) {
        eventName = e;
    }
}
