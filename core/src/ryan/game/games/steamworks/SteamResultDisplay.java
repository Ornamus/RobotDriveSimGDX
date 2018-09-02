package ryan.game.games.steamworks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ryan.game.Main;
import ryan.game.competition.Match;
import ryan.game.competition.Rankings;
import ryan.game.games.ResultDisplay;
import ryan.game.render.Fonts;
import ryan.game.render.ImageDrawer;
import ryan.game.screens.GameScreen;

public class SteamResultDisplay extends ResultDisplay {

    Sprite blueWin, redWin;
    Sprite arrowUp, arrowDown;
    Match match;

    public SteamResultDisplay(Match m) {
        super(0, 0, m.qualifier ? "core/assets/results_quals.png" : "core/assets/results_elims.png");
        match = m;
        sprite.setSize(1188, 631);
        sprite.setPosition(0-sprite.getWidth() /2, 0-sprite.getHeight()/2);
        setDrawScaled(false);

        blueWin = new Sprite(new Texture("core/assets/winner_blue.png"));
        redWin = new Sprite(new Texture("core/assets/winner_red.png"));

        arrowUp = new Sprite(new Texture("core/assets/arrow.png"));
        arrowUp.setSize(28,28);

        arrowDown = new Sprite(new Texture("core/assets/arrow.png"));
        arrowDown.flip(false, true);
        arrowDown.setSize(28,28);
    }

    @Override
    public void draw(SpriteBatch b) {
        sprite.setSize(Main.screenWidth, Main.screenHeight);
        sprite.setPosition(0-sprite.getWidth() /2, 0-sprite.getHeight()/2);
        super.draw(b);

        float adjust = 15;

        Fonts.drawCentered(Fonts.fmsBlack, match.getName(), getCenterX(), getCenterY(), 0, 305-adjust, b);
        Fonts.drawCentered(Fonts.fmsBlackSmall, Steamworks.display.getEventName(), getCenterX(), getCenterY(), 0, 280-adjust, b);
        Fonts.drawCentered(Fonts.fmsBlackSmall, "Scoring System powered by Bacon", getCenterX(), getCenterY(), 0, 252-adjust, b);

        drawAlliance(getCenterX() - (Main.widthScale*405), getCenterY(), match.red, b);
        drawAlliance(getCenterX() + (Main.widthScale*70), getCenterY(), match.blue, b);

        blueWin.setBounds(getCenterX() + (206*Main.widthScale), getCenterY() - (295*Main.heightScale), 294*Main.widthScale, 77*Main.heightScale);
        if (match.blue.score > match.red.score) blueWin.draw(b);

        redWin.setBounds(getCenterX() + ((-206 - 294)*Main.widthScale), getCenterY() - (295*Main.heightScale), 294*Main.widthScale, 77*Main.heightScale);
        if (match.red.score > match.blue.score) redWin.draw(b);
    }

    public void drawAlliance(float x, float y, Match.MatchAlliance a, SpriteBatch b) {
        boolean blue = x > getCenterX();
        float teamYAdjust = 167.5f;
        if (match.qualifier) {
            Rankings r = GameScreen.schedule.getRankings();
            for (int i = 0; i < a.teams.length; i++) {
                int team = a.teams[i];
                float increment = (i * 40);

                Fonts.draw(Fonts.fmsWhiteNormal, team + "", x, y, 53, teamYAdjust-increment, b);
                Fonts.drawCentered(Fonts.fmsWhiteNormal, r.getRank(team) + "", x, y, 243, teamYAdjust-increment, b);

                boolean first = r.getPreviousRank(team) == -1;
                if (!first) {
                    int rankDiff = r.getRank(team) - r.getPreviousRank(team);
                    if (rankDiff != 0) {
                        Sprite s = rankDiff < 0 ? arrowUp : arrowDown;
                        s.setPosition(x + 263 + 5, y+teamYAdjust - increment - 24);
                        s.draw(b);
                    }
                }
            }
        } else {
            String teams = "";
            for (int t : a.teams) {
                teams += "-" + t;
            }
            teams = teams.substring(1, teams.length());

            Fonts.draw(Fonts.fmsWhiteSmall, teams, x, y, 70, 156, b);
            //Fonts.fmsWhiteSmall.draw(b, teams, x+70, y + 156);

            Fonts.draw(Fonts.fmsBlackSmall, "" + (match.blue == a ? GameScreen.schedule.getSeed(match.blue.teams) : GameScreen.schedule.getSeed(match.red.teams)), x, y, 22.5f, 156, b);
            //Fonts.fmsBlackSmall.draw(b, "" + (match.blue == a ? Main.schedule.getSeed(match.blue.teams) : Main.schedule.getSeed(match.red.teams)), x+22.5f, y + 156);
        }

        AllianceScoreData d = (AllianceScoreData) a.breakdown;
        AllianceScoreData oppD = (AllianceScoreData) (match.blue == a ? match.red.breakdown : match.blue.breakdown);

        Fonts.draw(Fonts.fmsBlack, "Auto Mobility\nPressure\nRotor\nReady for Takeoff\n" + (blue ? "Red" : "Blue") + " Penalty", x, y, 0, 0, b);
        //Fonts.fmsBlack.draw(b, "Auto Mobility\nPressure\nRotor\nReady for Takeoff\n" + (blue ? "Red" : "Blue") + " Penalty", x, y);


        Fonts.draw(Fonts.fmsBlack,
                (d.crosses*5) + "\n" +
                        d.kPA + "\n" +
                        d.rotorPoints + "\n" +
                        (d.climbs*50) + "\n" +
                        oppD.fouls,
                x,  y, 310, 0, b);

        String bonusText;
        if (match.qualifier) {
            int rp = d.rankingPoints;
            if (a.winner) rp += 2;
            else if (!match.getOther(a).winner) rp += 1;
            bonusText = rp + " RP";
        } else {
            int points = 0;
            if (d.kPA >= 40) points += 20;
            if (d.rotors == 4) points += 100;
            bonusText = points + "";
        }

        Fonts.draw(Fonts.fmsBlack, bonusText, x, y, 310, 40, b);

        Fonts.drawCentered(Fonts.fmsScore, a.score + "", getCenterX(), getCenterY(), 100 * (blue ? 1 : -1), -225, b);
    }
}
