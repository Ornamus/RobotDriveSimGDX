package ryan.game.games.steamworks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import ryan.game.competition.Schedule;
import ryan.game.competition.Team;
import ryan.game.games.steamworks.SteamworksDisplay;
import ryan.game.games.steamworks.SteamworksField;
import ryan.game.render.ImageDrawer;

public class SteamResultDisplay extends ImageDrawer {

    GlyphLayout layout;
    public BitmapFont whiteBig, whiteNormal;
    public BitmapFont blackNormal, blackSmall;
    Sprite blueWin, redWin;
    Sprite redX;

    public SteamResultDisplay(float x, float y) {
        super(x, y, "core/assets/results_elims.png");
        sprite.setSize(1188, 631);
        sprite.setPosition(0-sprite.getWidth() /2, 0-sprite.getHeight()/2);
        setDrawScaled(false);

        redX = new Sprite(new Texture("core/assets/redx.png"));
        redX.setSize(90, 90);
        redX.setOriginCenter();

        blueWin = new Sprite(new Texture("core/assets/winner_blue.png"));
        redWin = new Sprite(new Texture("core/assets/winner_red.png"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("core/assets/fonts/Kozuka.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 52;
        param.borderColor = Color.BLACK;
        param.color = Color.WHITE;
        param.borderWidth = 2f;

        whiteBig = generator.generateFont(param);

        param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 20;
        param.color = Color.BLACK;
        param.shadowColor = Color.BLACK;
        param.borderWidth = .5f;
        param.borderColor = Color.BLACK;

        blackNormal = generator.generateFont(param);

        param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 15;
        param.color = Color.BLACK;
        param.shadowColor = Color.BLACK;
        param.borderWidth = .5f;
        param.borderColor = Color.BLACK;

        blackSmall = generator.generateFont(param);

        param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 20;
        param.color = Color.WHITE;
        //param.shadowColor = Color.BLACK;
        param.borderWidth = 1f;
        param.borderColor = Color.BLACK;

        whiteNormal = generator.generateFont(param);

        generator.dispose();

        layout = new GlyphLayout(whiteBig, "");
    }

    @Override
    public void draw(SpriteBatch b) {
        super.draw(b);

        drawCentered(SteamworksField.display.getMatchName(), getCenterX(), getCenterY() + 305, blackNormal, b);
        drawCentered(SteamworksField.display.getEventName(), getCenterX(), getCenterY() + 280, blackSmall, b);
        //drawCentered("Scoring System powered by Hopes and Dreams", getCenterX(), getCenterY() + 252, blackSmall, b);

        String blueAlliance = "", redAlliance = "";
        Schedule.Match m = SteamworksField.currentMatch;
        for (Team t : m.blue) {
            blueAlliance += "-" + t.number;
        }
        for (Team t : m.red) {
            redAlliance += "-" + t.number;
        }
        blueAlliance = blueAlliance.substring(1, blueAlliance.length());
        redAlliance = redAlliance.substring(1, redAlliance.length());

        whiteNormal.draw(b, redAlliance, getCenterX() - 365, getCenterY() + 164);
        whiteNormal.draw(b, blueAlliance, getCenterX() + 150, getCenterY() + 164);

        SteamworksDisplay display = SteamworksField.display;

        blackNormal.draw(b, "Auto Mobility\nPressure\nRotor\nReady for Takeoff\nBlue Penalty", getCenterX() - 430, getCenterY() - 0);
        blackNormal.draw(b,
                        (display.redCrosses*5) + "\n" +
                        display.redKPA + "\n" +
                        display.redRotorPoints + "\n" +
                        (display.redClimbs*50) + "\n" +
                        SteamworksField.blueFouls,
                (getCenterX() - 430) + 310,  getCenterY());

        String bonusText;
        if (SteamworksField.currentMatch.qualifier) {
            int rp = 0;
            if (display.redKPA >= 40) rp++;
            if (display.redRots == 4) rp++;
            bonusText = rp + " RP";
        } else {
            int points = 0;
            if (display.redKPA >= 40) points += 20;
            if (display.redRots == 4) points += 100;
            bonusText = points + "";
        }
        blackNormal.draw(b, bonusText, (getCenterX() - 430) + 310,  getCenterY() + 70);
        if (display.redKPA < 40) {
            redX.setPosition(getCenterX() - 310, getCenterY() + 30);
            redX.draw(b);
        }
        if (display.redRots < 4) {
            redX.setPosition(getCenterX() - 435, getCenterY() + 30);
            redX.draw(b);
        }

        float adjust = -225;
        if (display.blueKPA < 40) {
            redX.setPosition(getCenterX() + 310 + adjust, getCenterY() + 30);
            redX.draw(b);
        }
        if (display.blueRots < 4) {
            redX.setPosition(getCenterX() + 435 +adjust, getCenterY() + 30);
            redX.draw(b);
        }

        adjust = 85;
        blackNormal.draw(b, "Auto Mobility\nPressure\nRotor\nReady for Takeoff\nRed Penalty", getCenterX() + adjust, getCenterY() - 0);
        blackNormal.draw(b,
                (display.blueCrosses*5) + "\n" +
                        display.blueKPA + "\n" +
                        display.blueRotorPoints + "\n" +
                        (display.blueClimbs*50) + "\n" +
                        SteamworksField.redFouls,
                (getCenterX() + adjust) + 310,  getCenterY());

        if (SteamworksField.currentMatch.qualifier) {
            int rp = 0;
            if (display.blueKPA >= 40) rp++;
            if (display.blueRots == 4) rp++;
            bonusText = rp + " RP";
        } else {
            int points = 0;
            if (display.blueKPA >= 40) points += 20;
            if (display.blueRots == 4) points += 100;
            bonusText = points + "";
        }
        blackNormal.draw(b, bonusText, (getCenterX() + adjust) + 310,  getCenterY() + 70);

        drawCentered(display.redScore + "", getCenterX() - 106, getCenterY() - 235, whiteBig, b);
        drawCentered(display.blueScore + "", getCenterX() + 106, getCenterY() - 235, whiteBig, b);

        blueWin.setBounds(getCenterX() + 206, getCenterY() - 295, 294, 77);
        if (display.blueScore > display.redScore) blueWin.draw(b);

        redWin.setBounds(getCenterX() - 206 - 294, getCenterY() - 295, 294, 77);
        if (display.redScore > display.blueScore)redWin.draw(b);
    }

    public void drawCentered(String s, float x, float y, BitmapFont f, SpriteBatch b) {
        f.draw(b, s, x - (getWidth(s, f) / 2), y);
    }

    public float getWidth(String s, BitmapFont f) {
        layout.setText(f, s);
        return layout.width;
    }
}
