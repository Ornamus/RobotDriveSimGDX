package ryan.game.games.steamworks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.competition.Match;
import ryan.game.entity.steamworks.Gear;
import ryan.game.entity.Robot;
import ryan.game.games.Game;
import ryan.game.games.ScoreDisplay;
import ryan.game.games.steamworks.robots.SteamRobotStats;
import ryan.game.render.Fonts;
import ryan.game.screens.GameScreen;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.cos;
import static java.lang.StrictMath.sin;

public class SteamworksDisplay extends ScoreDisplay {

    Sprite blueBlob, redBlob;

    public SteamworksDisplay() {
        super("core/assets/score_display.png");
        blueBlob = new Sprite(new Texture("core/assets/blob_blue.png"));
        redBlob = new Sprite(new Texture("core/assets/blob_red.png"));
    }

    @Override
    public void tick() {
        super.tick();
        blueBlob.setSize(29.75f, 29.75f);
        redBlob.setSize(29.75f, 29.75f);
    }

    @Override
    public void draw(SpriteBatch batch) {
        Steamworks.blue.scoreUpdateReset();
        Steamworks.red.scoreUpdateReset();

        super.draw(batch);

        Fonts.drawCentered(Fonts.fmsBlack, Steamworks.blue.kPA + "", 450, getY() + 55, batch); //blue kpa
        Fonts.drawCentered(Fonts.fmsBlack, Steamworks.red.kPA + "", -450, getY() + 55, batch); //red kpa

        Fonts.drawCentered(Fonts.fmsBlack, Steamworks.blue.rotors + "", 630, getY() + 60, batch); //blue rotors
        Fonts.drawCentered(Fonts.fmsBlack, Steamworks.red.rotors + "", -630, getY() + 60, batch); //red rotors

        Fonts.drawCentered(Fonts.fmsBlack, Steamworks.blue.climbs + "", 795, getY() + 60, batch); //blue climbs
        Fonts.drawCentered(Fonts.fmsBlack, Steamworks.red.climbs + "", -795, getY() + 60, batch); //red climbs

        if (GameScreen.matchPlay) {
            drawGearDisplay(407, 82, Steamworks.blue.gears, Steamworks.blue.gears >= 13 ? Color.YELLOW : Color.WHITE, batch);
            drawGearDisplay(-502, 82, Steamworks.red.gears, Steamworks.red.gears >= 13 ? Color.YELLOW : Color.WHITE, batch);

            List<Long> blueProgresses = new ArrayList<>();
            List<Long> redProgresses = new ArrayList<>();
            Steamworks.humanPlayers.stream().filter(h -> h.scoreProgress != null).forEach(h -> {
                if (h.blue) blueProgresses.add(h.scoreProgress);
                else redProgresses.add(h.scoreProgress);
            });
            int loops = blueProgresses.size()+ Steamworks.blue.gearQueue;
            final float spacing = 37.5f;
            float hpX = 366.5f;
            float hpY = 76.5f + (spacing * (loops/2f));
            for (int i=0; i<loops; i++) {
                long progress = GameScreen.getTime();
                if (i <= blueProgresses.size()-1) progress = blueProgresses.get(i);
                Utils.drawUnscaledProgressBar(hpX + 68, hpY - (spacing * i), 102, 25.5f, (GameScreen.getTime()-progress)/ Steamworks.hpGearScoreSpeed, batch);
                batch.draw(Gear.TEXTURE, hpX - 20.5f, hpY - 6.8f - (spacing * i), 35, 35);
            }

            loops = redProgresses.size()+ Steamworks.red.gearQueue;
            hpX = -540;
            hpY = 76.5f + (spacing * (loops/2f));
            for (int i=0; i<loops; i++) {
                long progress = GameScreen.getTime();
                if (i <= redProgresses.size()-1) progress = redProgresses.get(i);
                Utils.drawUnscaledProgressBar(hpX + 68, hpY - (spacing * i), 102, 25.5f, (GameScreen.getTime()-progress)/ Steamworks.hpGearScoreSpeed, batch);
                batch.draw(Gear.TEXTURE, hpX - 20.5f, hpY - 6.8f - (spacing * i), 35, 35);
            }
            drawFuelProgress(475.8f, -459, true, batch);
            drawFuelProgress(-510, -462.4f, false, batch);
        }
    }

    public void drawFuelProgress(float startX, float startY, boolean blue, SpriteBatch b) {
        Sprite s = blue ? blueBlob : redBlob;
        float radius = 38.25f * 1.7f;
        int totalBalls = 12;
        double angle = 0;
        int ballsMade = 0;

        int ballsNeeded = 0;
        if (Game.isPlaying()) {
            int fuel = (blue ? Steamworks.blue.fuel : Steamworks.red.fuel);
            float percentIncomplete = (fuel / 3f) - ((float)Math.floor(fuel / 3));
            if (percentIncomplete > .65) ballsNeeded = 6;
            else if (percentIncomplete > .32) ballsNeeded = 3;
        }
        boolean[] show = new boolean[12];
        for (int i=0; i<12; i++) {
            show[i] = false;
        }
        if (blue) {
            if (ballsNeeded >= 3) {
                show[10] = true;
                show[11] = true;
                show[0] = true;
            }
            if (ballsNeeded >= 6) {
                show[1] = true;
                show[2] = true;
                show[3] = true;
            }
        } else {
            for (int i=0; i<12; i++) {
                show[i] = i < ballsNeeded;
            }
        }
        //s.setPosition(startX, startY);
        //s.draw(b);
        int skipStart = blue ? 7 : 9;

        while (angle < 360) {
            if (ballsMade != skipStart && ballsMade != skipStart + 1 && ballsMade != skipStart + 2) {
                if (show.length > ballsMade && show[ballsMade]) {
                    float x = startX + ((float) cos(Math.toRadians(angle))) * radius;
                    float y = startY + ((float) sin(Math.toRadians(angle))) * radius;
                    s.setPosition(x, y);
                    s.draw(b);
                }
            }
            ballsMade++;
            angle += (360 / totalBalls);
        }
    }

    public int calculateScore(AllianceScoreData a) {
        if (GameScreen.matchPlay) {
            Match current = GameScreen.schedule.getCurrentMatch();
            a.score = 0;
            if (a.gearsInAuto > 0) {
                a.rotorPoints += 20;
            }
            if (a.gearsInAuto > 2) {
                a.rotorPoints += 20;
            }
            if (a.gears > 0) {
                a.rotorPoints += 40;
                a.rotors++;
            }
            if (a.gears > 2) {
                a.rotorPoints += 40;
                a.rotors++;
            }
            if (a.gears > 6) {
                a.rotorPoints += 40;
                a.rotors++;
            }
            if (a.gears > 12) {
                a.rotorPoints += 40;
                a.rotors++;
                if (!current.qualifier) {
                    a.score += 100;
                }
            }

            a.score += a.rotorPoints;

            a.kPA = (int)Math.round(Math.floor(a.fuel / 3.0));
            a.kPA += a.fuelInAuto;
            a.score += a.kPA;
            if (!current.qualifier && a.kPA >= 40) a.score += 20;

            for (Robot r : GameScreen.robots) {
                if (a.blue == r.blue) {
                    SteamworksMetadata meta = (SteamworksMetadata) r.metadata;
                    SteamRobotStats stats = (SteamRobotStats) r.stats;
                    if (meta.crossedBaseline) {
                        a.crosses++;
                    }
                    if (meta.onRope != null && GameScreen.getTime() - meta.onRope > (stats.climbSpeed * 1000)) {
                        a.climbs++;
                    }
                }
            }
            a.score += (a.crosses * 5);

            a.score += a.blue ? Steamworks.red.fouls : Steamworks.blue.fouls;

            if (seconds <= 30) {
                a.climbs += a.bonusClimbs;
                if (a.climbs > 3) a.climbs = 3;
                a.score += a.climbs * 50;
            }
        }
        return a.score;
    }

    @Override
    public int[] calculateScores() {
        return new int[]{calculateScore(Steamworks.blue), calculateScore(Steamworks.red)};
    }

    public void drawGearDisplay(float x, float y, int gears, Color c, SpriteBatch b) {
        b.draw(Gear.TEXTURE, x, y, 50, 50);

        Color old = Fonts.monoWhiteLarge.getColor();
        Fonts.monoWhiteLarge.setColor(c);

        Fonts.drawCentered(Fonts.monoWhiteLarge, gears + "", x + 25, y + 90, b);

        Fonts.monoWhiteLarge.setColor(old);
    }
}
