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

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.cos;
import static java.lang.StrictMath.sin;

public class SteamworksDisplay extends ScoreDisplay {

    Sprite blueBlob, redBlob;

    public SteamworksDisplay() {
        super("core/assets/score_display.png");
        blueBlob = new Sprite(new Texture("core/assets/blob_blue.png"));
        blueBlob.setSize(17.5f, 17.5f);
        redBlob = new Sprite(new Texture("core/assets/blob_red.png"));
        redBlob.setSize(17.5f, 17.5f);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void draw(SpriteBatch batch) {
        //if (!Main.isShowingResults) {
            Steamworks.blue.scoreUpdateReset();
            Steamworks.red.scoreUpdateReset();
        //}

        super.draw(batch);

        Fonts.drawCentered(Steamworks.blue.kPA + "", 257.5f, getY() + 35f, Fonts.fmsBlack, batch); //blue kpa
        Fonts.drawCentered(Steamworks.red.kPA + "", -257.5f, getY() + 35f, Fonts.fmsBlack, batch); //red kpa

        Fonts.drawCentered(Steamworks.blue.rotors + "", 360.5f, getY() + 36f, Fonts.fmsBlack, batch); //blue rotors
        Fonts.drawCentered(Steamworks.red.rotors + "", -360.5f, getY() + 36f, Fonts.fmsBlack, batch); //red rotors

        Fonts.drawCentered(Steamworks.blue.climbs + "", 455, getY() + 36f, Fonts.fmsBlack, batch); //blue climbs
        Fonts.drawCentered(Steamworks.red.climbs + "", -455, getY() + 36f, Fonts.fmsBlack, batch); //red climbs

        if (Main.matchPlay) {
            drawGearDisplay(232.5f, 45, Steamworks.blue.gears, Steamworks.blue.gears > 12 ? Color.YELLOW : Color.WHITE, batch);
            drawGearDisplay(-287.5f, 45, Steamworks.red.gears, Steamworks.red.gears > 12 ? Color.YELLOW : Color.WHITE, batch);

            List<Long> blueProgresses = new ArrayList<>();
            List<Long> redProgresses = new ArrayList<>();
            Steamworks.humanPlayers.stream().filter(h -> h.scoreProgress != null).forEach(h -> {
                if (h.blue) blueProgresses.add(h.scoreProgress);
                else redProgresses.add(h.scoreProgress);
            });
            int loops = blueProgresses.size()+ Steamworks.blue.gearQueue;
            final float spacing = 22f;
            float hpX = 215.5f;
            float hpY = 45 + (spacing * (loops/2f));
            for (int i=0; i<loops; i++) {
                long progress = Main.getTime();
                if (i <= blueProgresses.size()-1) progress = blueProgresses.get(i);
                Utils.drawUnscaledProgressBar(hpX + 40, hpY - (spacing * i), 60, 15, (Main.getTime()-progress)/ Steamworks.hpGearScoreSpeed, batch);
                batch.draw(Gear.TEXTURE, hpX-12, hpY-4 - (spacing * i), 20f, 20f);
            }

            loops = redProgresses.size()+ Steamworks.red.gearQueue;
            hpX = -287.5f-(232.5f-215.5f);
            hpY = 45 + (spacing * (loops/2f));
            for (int i=0; i<loops; i++) {
                long progress = Main.getTime();
                if (i <= redProgresses.size()-1) progress = redProgresses.get(i);
                Utils.drawUnscaledProgressBar(hpX + 40, hpY - (spacing * i), 60, 15, (Main.getTime()-progress)/ Steamworks.hpGearScoreSpeed, batch);
                batch.draw(Gear.TEXTURE, hpX-12, hpY-4 - (spacing * i), 20f, 20f);
            }
            drawFuelProgress(274, -273, true, batch);
            drawFuelProgress(-291, -275, false, batch);
        }
    }

    public void drawFuelProgress(float startX, float startY, boolean blue, SpriteBatch b) {
        Sprite s = blue ? blueBlob : redBlob;
        float radius = 38.25f;//38.5f;
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
        if (Main.matchPlay) {
            Match current = Main.schedule.getCurrentMatch();
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

            for (Robot r : Main.robots) {
                if (a.blue == r.blue) {
                    SteamworksMetadata meta = (SteamworksMetadata) r.metadata;
                    SteamRobotStats stats = (SteamRobotStats) r.stats;
                    if (meta.crossedBaseline) {
                        a.crosses++;
                    }
                    if (meta.onRope != null && Main.getTime() - meta.onRope > (stats.climbSpeed * 1000)) {
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
        b.draw(Gear.TEXTURE, x, y, 30f, 30f);
        Fonts.monoWhiteLarge.setColor(c);
        Fonts.monoWhiteLarge.draw(b, gears + "", x + 15 - (Fonts.getWidth(gears + "", Fonts.monoWhiteLarge) / 2), y + 55);
    }
}
