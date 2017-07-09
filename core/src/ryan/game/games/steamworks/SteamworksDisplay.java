package ryan.game.games.steamworks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.competition.Schedule;
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

    public int blueScore = 0, redScore = 0;
    public int blueRots = 0, redRots = 0;
    public int blueRotorPoints = 0, redRotorPoints = 0;
    public int blueKPA = 0, redKPA = 0;
    public int blueClimbs = 0, redClimbs = 0;
    public int blueCrosses = 0, redCrosses = 0;

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
        if (!Main.isShowingResults) {
            blueScore = 0;
            redScore = 0;
            blueRots = 0;
            redRots = 0;
            blueRotorPoints = 0;
            redRotorPoints = 0;
            blueKPA = 0;
            redKPA = 0;
            blueClimbs = 0;
            redClimbs = 0;
            blueCrosses = 0;
            redCrosses = 0;
        }

        super.draw(batch);

        Fonts.drawCentered(blueKPA + "", 257.5f, getY() + 35f, Fonts.fmsBlack, batch); //blue kpa
        Fonts.drawCentered(redKPA + "", -257.5f, getY() + 35f, Fonts.fmsBlack, batch); //red kpa

        Fonts.drawCentered(blueRots + "", 360.5f, getY() + 36f, Fonts.fmsBlack, batch); //blue rotors
        Fonts.drawCentered(redRots + "", -360.5f, getY() + 36f, Fonts.fmsBlack, batch); //red rotors

        Fonts.drawCentered(blueClimbs + "", 455, getY() + 36f, Fonts.fmsBlack, batch); //blue climbs
        Fonts.drawCentered(redClimbs + "", -455, getY() + 36f, Fonts.fmsBlack, batch); //red climbs

        if (Main.matchPlay) {
            drawGearDisplay(232.5f, 45, SteamworksField.blueGears, SteamworksField.blueGears > 12 ? Color.YELLOW : Color.WHITE, batch);
            drawGearDisplay(-287.5f, 45, SteamworksField.redGears, SteamworksField.redGears > 12 ? Color.YELLOW : Color.WHITE, batch);
            int blueHPShown = 0;
            int redHPShown = 0;
            /*
            232.5f, 45
            -287.5f, 45
             */
            List<Long> progresses = new ArrayList<>();
            for (SteamworksField.HumanPlayer h : SteamworksField.humanPlayers) {
                if (h.blue && h.scoreProgress != null) {
                    progresses.add(h.scoreProgress);
                }
            }
            int loops = progresses.size()+SteamworksField.blueGearQueue;
            final float spacing = 22f;
            float hpX = 215.5f;
            float hpY = 45 + (spacing * (loops/2f));
            for (int i=0; i<loops; i++) {
                long progress = System.currentTimeMillis();
                if (i <= progresses.size()-1) progress = progresses.get(i);
                Utils.drawUnscaledProgressBar(hpX + 40, hpY - (spacing * i), 60, 15, (System.currentTimeMillis()-progress)/SteamworksField.hpGearScoreSpeed, batch);
                batch.draw(Gear.TEXTURE, hpX-12, hpY-4 - (spacing * i), 20f, 20f);
                blueHPShown++;
            }


            progresses = new ArrayList<>();
            for (SteamworksField.HumanPlayer h : SteamworksField.humanPlayers) {
                if (!h.blue && h.scoreProgress != null) {
                    progresses.add(h.scoreProgress);
                }
            }
            loops = progresses.size()+SteamworksField.redGearQueue;
            hpX = -287.5f-(232.5f-215.5f);
            hpY = 45 + (spacing * (loops/2f));
            for (int i=0; i<loops; i++) {
                long progress = System.currentTimeMillis();
                if (i <= progresses.size()-1) progress = progresses.get(i);
                Utils.drawUnscaledProgressBar(hpX + 40, hpY - (spacing * i), 60, 15, (System.currentTimeMillis()-progress)/SteamworksField.hpGearScoreSpeed, batch);
                batch.draw(Gear.TEXTURE, hpX-12, hpY-4 - (spacing * i), 20f, 20f);
                blueHPShown++;
            }
            drawFuelProgress(274, -273, true, batch);
            drawFuelProgress(-291, -275, false, batch);
        }
    }

    @Override
    public void drawInPixels(SpriteBatch b) {
        //drawFuelProgress(274, -273, true, b);
        //drawFuelProgress(-291, -275, false, b);
    }

    public void drawFuelProgress(float startX, float startY, boolean blue, SpriteBatch b) {
        Sprite s = blue ? blueBlob : redBlob;
        float radius = 38.25f;//38.5f;
        int totalBalls = 12;
        double angle = 0;
        int ballsMade = 0;

        int ballsNeeded = 0;
        if (Game.isPlaying()) {
            int fuel = (blue ? SteamworksField.blueFuel : SteamworksField.redFuel);
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

    @Override
    public int[] calculateScores() {
        if (Main.matchPlay) {
            Schedule.Match current = Main.schedule.getCurrentMatch();
            blueScore = 0;
            redScore = 0;
            if (SteamworksField.blueGearsInAuto > 0) {
                blueRotorPoints += 20;
            }
            if (SteamworksField.blueGearsInAuto > 2) {
                blueRotorPoints += 20;
            }
            if (SteamworksField.blueGears > 0) {
                blueRotorPoints += 40;
                blueRots++;
            }
            if (SteamworksField.blueGears > 2) {
                blueRotorPoints += 40;
                blueRots++;
            }
            if (SteamworksField.blueGears > 6) {
                blueRotorPoints += 40;
                blueRots++;
            }
            if (SteamworksField.blueGears > 12) {
                blueRotorPoints += 40;
                blueRots++;
                if (!current.qualifier) {
                    blueScore += 100;
                }
            }

            if (SteamworksField.redGearsInAuto > 0) {
                redRotorPoints += 20;
            }
            if (SteamworksField.redGearsInAuto > 2) {
                redRotorPoints += 20;
            }
            if (SteamworksField.redGears > 0) {
                redRotorPoints += 40;
                redRots++;
            }
            if (SteamworksField.redGears > 2) {
                redRotorPoints += 40;
                redRots++;
            }
            if (SteamworksField.redGears > 6) {
                redScore += 40;
                redRots++;
            }
            if (SteamworksField.redGears > 12) {
                redRotorPoints += 40;
                redRots++;
                if (!current.qualifier) {
                    redScore += 100;
                }
            }
            blueScore += blueRotorPoints;
            redScore += redRotorPoints;

            blueKPA = (int)Math.round(Math.floor(SteamworksField.blueFuel / 3.0));
            redKPA = (int)Math.round(Math.floor(SteamworksField.redFuel / 3.0));
            blueKPA += SteamworksField.blueFuelInAuto;
            redKPA += SteamworksField.redFuelInAuto;
            blueScore += blueKPA;
            redScore += redKPA;
            if (!current.qualifier) {
                if (blueKPA >= 40) blueScore += 20;
                if (redKPA >= 40) redScore += 20;
            }

            for (Robot r : Main.robots) {
                SteamworksMetadata meta = (SteamworksMetadata) r.metadata;
                if (meta.crossedBaseline) {
                    if (r.blue) blueCrosses++;
                    else redCrosses++;
                }
            }
            blueScore += (blueCrosses * 5);
            redScore += (redCrosses * 5);

            blueScore += SteamworksField.redFouls;
            redScore += SteamworksField.blueFouls;
            if (seconds <= 30) {
                for (Robot r : Main.robots) {
                    SteamworksMetadata meta = (SteamworksMetadata) r.metadata;
                    SteamRobotStats stats = (SteamRobotStats) r.stats;
                    if (meta.onRope != null && System.currentTimeMillis() - meta.onRope > (stats.climbSpeed * 1000)) {
                        if (r.blue) blueClimbs++;
                        else redClimbs++;
                    }
                }
                blueClimbs += SteamworksField.blueBonusClimbs;
                if (blueClimbs > 3) blueClimbs = 3;
                redClimbs += SteamworksField.redBonusClimbs;
                if (redClimbs > 3) redClimbs = 3;
                blueScore += blueClimbs * 50;
                redScore += redClimbs * 50;
            }
        }
        return new int[]{blueScore, redScore};
    }

    public void drawGearDisplay(float x, float y, int gears, Color c, SpriteBatch b) {
        b.draw(Gear.TEXTURE, x, y, 30f, 30f);
        Fonts.monoWhiteLarge.setColor(c);
        Fonts.monoWhiteLarge.draw(b, gears + "", x + 15 - (Fonts.getWidth(gears + "", Fonts.monoWhiteLarge) / 2), y + 55);
    }
}
