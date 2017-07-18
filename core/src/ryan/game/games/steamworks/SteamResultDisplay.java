package ryan.game.games.steamworks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import ryan.game.Main;
import ryan.game.competition.Match;
import ryan.game.competition.Schedule;
import ryan.game.competition.Team;
import ryan.game.render.Fonts;
import ryan.game.render.ImageDrawer;

public class SteamResultDisplay extends ImageDrawer {

    public BitmapFont whiteNormal;
    public BitmapFont blackSmall;
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
        param.size = 15;
        param.color = Color.BLACK;
        param.shadowColor = Color.BLACK;
        param.borderWidth = .5f;
        param.borderColor = Color.BLACK;

        blackSmall = generator.generateFont(param);

        param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 20;
        param.color = Color.WHITE;
        param.borderWidth = 1f;
        param.borderColor = Color.BLACK;

        whiteNormal = generator.generateFont(param);

        generator.dispose();
    }

    @Override
    public void draw(SpriteBatch b) {
        super.draw(b);

        Fonts.drawCentered(Steamworks.display.getMatchName(), getCenterX(), getCenterY() + 305, Fonts.fmsBlack, b);
        Fonts.drawCentered(Steamworks.display.getEventName(), getCenterX(), getCenterY() + 280, blackSmall, b);
        //drawCentered("Scoring System powered by Hopes and Dreams", getCenterX(), getCenterY() + 252, blackSmall, b);

        String blueAlliance = "", redAlliance = "";
        Match m = Main.schedule.getCurrentMatch();
        for (int t : m.blue.teams) {
            blueAlliance += "-" + t;
        }
        for (int t : m.red.teams) {
            redAlliance += "-" + t;
        }
        blueAlliance = blueAlliance.substring(1, blueAlliance.length());
        redAlliance = redAlliance.substring(1, redAlliance.length());

        whiteNormal.draw(b, redAlliance, getCenterX() - 365, getCenterY() + 164);
        whiteNormal.draw(b, blueAlliance, getCenterX() + 150, getCenterY() + 164);


        Fonts.fmsBlack.draw(b, "Auto Mobility\nPressure\nRotor\nReady for Takeoff\nBlue Penalty", getCenterX() - 430, getCenterY() - 0);
        Fonts.fmsBlack.draw(b,
                        (Steamworks.red.crosses*5) + "\n" +
                        Steamworks.red.kPA + "\n" +
                        Steamworks.red.rotorPoints + "\n" +
                        (Steamworks.red.climbs*50) + "\n" +
                        Steamworks.blue.fouls,
                (getCenterX() - 430) + 310,  getCenterY());

        String bonusText;
        if (m.qualifier) {
            bonusText = Steamworks.red.rankingPoints + " RP";
        } else {
            int points = 0;
            if (Steamworks.red.kPA >= 40) points += 20;
            if (Steamworks.red.rotors == 4) points += 100;
            bonusText = points + "";
        }
        Fonts.fmsBlack.draw(b, bonusText, (getCenterX() - 430) + 310,  getCenterY() + 70);
        if (Steamworks.red.kPA < 40) {
            redX.setPosition(getCenterX() - 435, getCenterY() + 30);
            redX.draw(b);
        }
        if (Steamworks.red.rotors < 4) {
            redX.setPosition(getCenterX() - 310, getCenterY() + 30);
            redX.draw(b);
        }

        float adjust = -225;
        if (Steamworks.blue.kPA < 40) {
            redX.setPosition(getCenterX() + 310 + adjust, getCenterY() + 30);
            redX.draw(b);
        }
        if (Steamworks.blue.rotors < 4) {
            redX.setPosition(getCenterX() + 435 +adjust, getCenterY() + 30);
            redX.draw(b);
        }

        adjust = 85;
        Fonts.fmsBlack.draw(b, "Auto Mobility\nPressure\nRotor\nReady for Takeoff\nRed Penalty", getCenterX() + adjust, getCenterY() - 0);
        Fonts.fmsBlack.draw(b,
                (Steamworks.blue.crosses*5) + "\n" +
                        Steamworks.blue.kPA + "\n" +
                        Steamworks.blue.rotorPoints + "\n" +
                        (Steamworks.blue.climbs*50) + "\n" +
                        Steamworks.red.fouls,
                (getCenterX() + adjust) + 310,  getCenterY());

        if (m.qualifier) {
            bonusText = Steamworks.blue.rankingPoints + " RP";
        } else {
            int points = 0;
            if (Steamworks.blue.kPA >= 40) points += 20;
            if (Steamworks.blue.rotors == 4) points += 100;
            bonusText = points + "";
        }
        Fonts.fmsBlack.draw(b, bonusText, (getCenterX() + adjust) + 310,  getCenterY() + 70);

        Fonts.drawCentered(Steamworks.red.score + "", getCenterX() - 106, getCenterY() - 235, Fonts.fmsScore, b);
        Fonts.drawCentered(Steamworks.blue.score + "", getCenterX() + 106, getCenterY() - 235, Fonts.fmsScore, b);

        blueWin.setBounds(getCenterX() + 206, getCenterY() - 295, 294, 77);
        if (Steamworks.blue.score > Steamworks.red.score) blueWin.draw(b);

        redWin.setBounds(getCenterX() - 206 - 294, getCenterY() - 295, 294, 77);
        if (Steamworks.red.score > Steamworks.blue.score)redWin.draw(b);
    }
}
