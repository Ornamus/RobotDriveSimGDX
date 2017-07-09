package ryan.game.games;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.entity.Robot;
import ryan.game.render.Drawable;

//TODO: make this a general-purpose display and then make SteamworksField extend it

public abstract class ScoreDisplay extends Drawable {

    GlyphLayout layout;
    public BitmapFont bigWhite;
    public BitmapFont blackNormal;
    public int seconds = 150;
    Sprite display;
    Sprite timerBacking;
    Sprite timerBar;

    int[] blueTeams = new int[]{1902, 254, 987};
    int[] redTeams = new int[]{118, 1986, 180};
    String matchName = "Semifinal 2 of 4";
    String eventName = "FIRST Championship";

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

        timerBar = new Sprite(Utils.colorImage("core/assets/whitepixel.png", Utils.toColor(39, 124, 28)));
        timerBar.setAlpha(1f);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("core/assets/fonts/Kozuka.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 52;
        param.borderColor = Color.BLACK;
        param.color = Color.WHITE;
        param.borderWidth = 2f;

        bigWhite = generator.generateFont(param);

        param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 20;
        param.color = Color.BLACK;
        param.shadowColor = Color.BLACK;
        param.borderWidth = .5f;
        param.borderColor = Color.BLACK;
        blackNormal = generator.generateFont(param);
        generator.dispose();

        layout = new GlyphLayout(bigWhite, "");
    }

    boolean wasMatch = false;
    boolean startedTeleop = false;

    //TODO: move this to main or somewhere more suitable
    @Override
    public void tick() {
        if (Main.matchPlay) {
            if (seconds > 135) {
                if (!wasMatch) {
                    for (Robot r : Main.robots) {
                        if (r.auto != null) {
                            r.auto.start();
                        }
                    }
                }
            } else {
                if (!startedTeleop) {
                    for (Robot r : Main.robots) {
                        if (r.auto != null && r.auto.isRunning()) {
                            r.auto.stop();
                        }
                    }
                    Main.getInstance().teleopStartSound.play(.45f);
                    startedTeleop = true;
                }
            }
        } else {
            startedTeleop = false;
        }
        wasMatch = Main.matchPlay;
    }

    @Override
    public void draw(SpriteBatch batch) {
        display.draw(batch);

        seconds = Game.getMatchTime();
        timerBar.setBounds(-127, getY() + 72, ((150f-seconds)/150f) * 254, 29);
        timerBar.draw(batch);
        timerBar.setTexture(Utils.colorImage("core/assets/whitepixel.png", (seconds <= 30 ? Color.YELLOW : Utils.toColor(39, 124, 28))));

        timerBacking.draw(batch);

        int[] scores = calculateScores();
        int blueScore = scores[0];
        int redScore = scores[1];

        drawCentered(blueScore + "", 65, getY()+61.5f, bigWhite, batch);
        drawCentered(redScore + "", -65, getY()+61.5f, bigWhite, batch);

        drawCentered(Main.matchPlay ? matchName : "Practice Match 1", -205, getY() + 130f, blackNormal, batch);
        drawCentered(Main.matchPlay ? eventName : "Breakfast of Champions", 205, getY() + 130f, blackNormal, batch);

        drawCentered(Main.matchPlay ? (seconds > 135 ? seconds - 135 : seconds) + "" : "Infinite", 0, getY() + 93f, blackNormal, batch);

        drawCentered(redTeams[0] + "", -166, getY() + 92f, blackNormal, batch);
        drawCentered(redTeams[1] + "", -166, getY() + 71f, blackNormal, batch);
        drawCentered(redTeams[2] + "", -166, getY() + 71f - 21f, blackNormal, batch);

        drawCentered(blueTeams[0] + "", 166, getY() + 92f, blackNormal, batch);
        drawCentered(blueTeams[1] + "", 166, getY() + 71f, blackNormal, batch);
        drawCentered(blueTeams[2] + "", 166, getY() + 71f - 21f, blackNormal, batch);
    }

    public void drawInPixels(SpriteBatch b) {

    }

    /**
     * Calculates the Blue and Red Alliance's scores.
     * @return An int[] of the scores. Index 0 is Blue's score, Index 1 is Red's score.
     */
    public abstract int[] calculateScores();

    public void drawCentered(String s, float x, float y, BitmapFont f, SpriteBatch b) {
        f.draw(b, s, x - (getWidth(s, f) / 2), y);
    }

    public float getWidth(String s, BitmapFont f) {
        layout.setText(f, s);
        return layout.width;
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
