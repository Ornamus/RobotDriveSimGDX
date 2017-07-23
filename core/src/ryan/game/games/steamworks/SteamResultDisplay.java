package ryan.game.games.steamworks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.competition.Match;
import ryan.game.competition.Rankings;
import ryan.game.competition.Schedule;
import ryan.game.render.Fonts;
import ryan.game.render.ImageDrawer;

import java.util.Arrays;

public class SteamResultDisplay extends ImageDrawer {

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
        super.draw(b);

        float adjust = 15;
        Fonts.drawCentered(match.getName(), getCenterX(), getCenterY() + 305-adjust, Fonts.fmsBlack, b);
        Fonts.drawCentered(Steamworks.display.getEventName(), getCenterX(), getCenterY() + 280-adjust, Fonts.fmsBlackSmall, b);
        Fonts.drawCentered("Scoring System powered by Bacon", getCenterX(), getCenterY() + 252-adjust, Fonts.fmsBlackSmall, b);

        drawAlliance(getCenterX() - 430, getCenterY(), match.red, b);
        drawAlliance(getCenterX() + 85, getCenterY(), match.blue, b);

        blueWin.setBounds(getCenterX() + 206, getCenterY() - 295, 294, 77);
        if (match.blue.score > match.red.score) blueWin.draw(b);

        redWin.setBounds(getCenterX() - 206 - 294, getCenterY() - 295, 294, 77);
        if (match.red.score > match.blue.score)redWin.draw(b);
    }

    public void drawAlliance(float x, float y, Match.MatchAlliance a, SpriteBatch b) {
        boolean blue = x > getCenterX();
        float teamY = y + 167.5f;
        if (match.qualifier) {
            Rankings r = Main.getInstance().schedule.getRankings();
            for (int i = 0; i < a.teams.length; i++) {
                int team = a.teams[i];
                float increment = (i * 40);
                Fonts.fmsWhiteNormal.draw(b, team + "", x + 53, teamY - increment);
                Fonts.drawCentered(r.getRank(team) + "", x + 243, teamY - increment, Fonts.fmsWhiteNormal, b);

                boolean first = r.getPreviousRank(team) == -1;
                if (!first) {
                    int rankDiff = r.getRank(team) - r.getPreviousRank(team);
                    if (rankDiff != 0) {
                        Sprite s = rankDiff < 0 ? arrowUp : arrowDown;
                        s.setPosition(x + 263 + 5, teamY - increment - 24);
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
            Fonts.fmsWhiteSmall.draw(b, teams, x+70, y + 156);

            Fonts.fmsBlackSmall.draw(b, "" + (match.blue == a ? Main.schedule.getSeed(match.blue.teams) : Main.schedule.getSeed(match.red.teams)), x+22.5f, y + 156); //TODO: handle seeds
        }

        AllianceScoreData d = (AllianceScoreData) a.breakdown;

        Fonts.fmsBlack.draw(b, "Auto Mobility\nPressure\nRotor\nReady for Takeoff\n" + (blue ? "Red" : "Blue") + " Penalty", x, y);
        Fonts.fmsBlack.draw(b,
                (d.crosses*5) + "\n" +
                        d.kPA + "\n" +
                        d.rotorPoints + "\n" +
                        (d.climbs*50) + "\n" +
                        d.fouls,
                x + 310,  y);

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
        Fonts.fmsBlack.draw(b, bonusText, x + 310,  y + 40);

        Fonts.drawCentered(a.score + "", getCenterX() + (blue? 106 : -106), getCenterY() - 225, Fonts.fmsScore, b);
    }
}
