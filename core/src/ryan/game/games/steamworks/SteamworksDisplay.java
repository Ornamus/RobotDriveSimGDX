package ryan.game.games.steamworks;

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
import ryan.game.entity.Gear;
import ryan.game.entity.Robot;
import ryan.game.render.Drawable;

public class SteamworksDisplay extends Drawable {

    GlyphLayout layout;
    BitmapFont bigWhite;
    BitmapFont blackNormal;
    Sprite display;
    Sprite timerBacking;
    Sprite timerBar;

    public SteamworksDisplay() {
        display = new Sprite(new Texture("core/assets/score_display.png"));
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
        //param.shadowColor = Color.BLACK;
        //param.shadowOffsetX = 1;
        //param.shadowOffsetY = 1;
        bigWhite = generator.generateFont(param);

        param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 20;
        param.color = Color.BLACK;
        param.shadowColor = Color.BLACK;
        param.borderWidth = .25f;
        param.borderColor = Color.BLACK;
        blackNormal = generator.generateFont(param);
        generator.dispose();

        layout = new GlyphLayout(bigWhite, "");
    }

    @Override
    public void tick() {

    }

    @Override
    public void draw(SpriteBatch batch) {
        display.draw(batch);

        int seconds = 135;
        if (Main.matchPlay) {
            long timeIn = System.currentTimeMillis() - Main.matchStart;
            long timeLeft = (((2 * 60) + 15) * 1000) - timeIn;
            seconds = Math.round(timeLeft / 1000f);
        }
        timerBar.setBounds(-127, getY() + 72, ((135f-seconds)/135f) * 254, 29);
        timerBar.draw(batch);
        timerBar.setTexture(Utils.colorImage("core/assets/whitepixel.png", (seconds <= 30 ? Color.YELLOW : Utils.toColor(39, 124, 28))));

        timerBacking.draw(batch);

        int blueScore = 0;
        int redScore = 0;
        int blueRots = 0, redRots = 0;
        int blueClimbs = 0, redClimbs = 0;
        if (Main.matchPlay) {
            if (Main.blueGears > 0) {
                blueScore += 40;
                blueRots++;
            }
            if (Main.blueGears > 2) {
                blueScore += 40;
                blueRots++;
            }
            if (Main.blueGears > 6) {
                blueScore += 40;
                blueRots++;
            }
            if (Main.blueGears > 12) {
                blueScore += 140;
                blueRots++;
            }

            if (Main.redGears > 0) {
                redScore += 40;
                redRots++;
            }
            if (Main.redGears > 2) {
                redScore += 40;
                redRots++;
            }
            if (Main.redGears > 6) {
                redScore += 40;
                redRots++;
            }
            if (Main.redGears > 12) {
                redScore += 140;
                redRots++;
            }
            if (seconds <= 30) {
                for (Robot r : Main.robots) {
                    if (r.onRope != null && System.currentTimeMillis() - r.onRope > 1000) {
                        if (r.blue) blueClimbs++;
                        else redClimbs++;
                    }
                }
                blueScore += blueClimbs * 50;
                redScore += redClimbs * 50;
            }
        }
        drawCentered(blueScore + "", 65, getY()+61.5f, bigWhite, batch);
        drawCentered(redScore + "", -65, getY()+61.5f, bigWhite, batch);

        drawCentered(Main.matchPlay ? "Semifinal 3 of 4" : "Practice Match 1", -205, getY() + 130f, blackNormal, batch);
        drawCentered(Main.matchPlay ? "FIRST Championship" : "Breakfast of Champions", 205, getY() + 130f, blackNormal, batch);

        drawCentered("0", 257.5f, getY() + 35f, blackNormal, batch); //blue kpa
        drawCentered("0", -257.5f, getY() + 35f, blackNormal, batch); //red kpa

        drawCentered(blueRots + "", 360.5f, getY() + 36f, blackNormal, batch); //blue rotors
        drawCentered(redRots + "", -360.5f, getY() + 36f, blackNormal, batch); //red rotors

        drawCentered(blueClimbs + "", 455, getY() + 36f, blackNormal, batch); //blue climbs
        drawCentered(redClimbs + "", -455, getY() + 36f, blackNormal, batch); //red climbs

        drawCentered(Main.matchPlay ? seconds + "" : "Infinite", 0, getY() + 93f, blackNormal, batch);

        drawCentered("1902", -166, getY() + 92f, blackNormal, batch);
        drawCentered("254", -166, getY() + 71f, blackNormal, batch);
        drawCentered("987", -166, getY() + 71f - 21f, blackNormal, batch);

        drawCentered("118", 166, getY() + 92f, blackNormal, batch);
        drawCentered("1986", 166, getY() + 71f, blackNormal, batch);
        drawCentered("180", 166, getY() + 71f - 21f, blackNormal, batch);

        if (Main.matchPlay) {
            drawGearDisplay(232.5f, 45, Main.blueGears, Main.blueGears > 12 ? Color.YELLOW : Color.WHITE, batch);
            drawGearDisplay(-287.5f, 45, Main.redGears, Main.redGears > 12 ? Color.YELLOW : Color.WHITE, batch);
        }
    }

    public void drawCentered(String s, float x, float y, BitmapFont f, SpriteBatch b) {
        f.draw(b, s, x - (getWidth(s, f) / 2), y);
    }

    private float getWidth(String s, BitmapFont f) {
        layout.setText(f, s);
        return layout.width;
    }

    public void drawGearDisplay(float x, float y, int gears, Color c, SpriteBatch b) {
        b.draw(Gear.TEXTURE, x, y, 30f, 30f);
        Main.smallFont.setColor(c);
        layout.setText(Main.smallFont, gears + "");
        Main.smallFont.draw(b, gears + "", x + 15 - (layout.width / 2), y + 55);
    }
}
