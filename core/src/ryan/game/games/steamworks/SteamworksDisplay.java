package ryan.game.games.steamworks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ryan.game.Main;
import ryan.game.entity.steamworks.Gear;
import ryan.game.entity.Robot;
import ryan.game.games.ScoreDisplay;

public class SteamworksDisplay extends ScoreDisplay {

    int blueRots = 0, redRots = 0;
    int blueKPA = 0, redKPA = 0;
    int blueClimbs = 0, redClimbs = 0;

    public SteamworksDisplay() {
        super("core/assets/score_display.png");
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void draw(SpriteBatch batch) {
        blueRots = 0;
        redRots = 0;
        blueKPA = 0;
        redKPA = 0;
        blueClimbs = 0;
        redClimbs = 0;

        super.draw(batch);

        drawCentered(blueKPA + "", 257.5f, getY() + 35f, blackNormal, batch); //blue kpa
        drawCentered(redKPA + "", -257.5f, getY() + 35f, blackNormal, batch); //red kpa

        drawCentered(blueRots + "", 360.5f, getY() + 36f, blackNormal, batch); //blue rotors
        drawCentered(redRots + "", -360.5f, getY() + 36f, blackNormal, batch); //red rotors

        drawCentered(blueClimbs + "", 455, getY() + 36f, blackNormal, batch); //blue climbs
        drawCentered(redClimbs + "", -455, getY() + 36f, blackNormal, batch); //red climbs

        if (Main.matchPlay) {
            drawGearDisplay(232.5f, 45, SteamworksField.blueGears, SteamworksField.blueGears > 12 ? Color.YELLOW : Color.WHITE, batch);
            drawGearDisplay(-287.5f, 45, SteamworksField.redGears, SteamworksField.redGears > 12 ? Color.YELLOW : Color.WHITE, batch);
        }
    }

    @Override
    public int[] calculateScores() {
        int blueScore = 0;
        int redScore = 0;
        if (Main.matchPlay) {
            if (SteamworksField.blueGearsInAuto > 0) {
                blueScore += 20;
            }
            if (SteamworksField.blueGearsInAuto > 2) {
                blueScore += 20;
            }
            if (SteamworksField.blueGears > 0) {
                blueScore += 40;
                blueRots++;
            }
            if (SteamworksField.blueGears > 2) {
                blueScore += 40;
                blueRots++;
            }
            if (SteamworksField.blueGears > 6) {
                blueScore += 40;
                blueRots++;
            }
            if (SteamworksField.blueGears > 12) {
                blueScore += 140;
                blueRots++;
            }

            if (SteamworksField.redGearsInAuto > 0) {
                redScore += 20;
            }
            if (SteamworksField.redGearsInAuto > 2) {
                redScore += 20;
            }
            if (SteamworksField.redGears > 0) {
                redScore += 40;
                redRots++;
            }
            if (SteamworksField.redGears > 2) {
                redScore += 40;
                redRots++;
            }
            if (SteamworksField.redGears > 6) {
                redScore += 40;
                redRots++;
            }
            if (SteamworksField.redGears > 12) {
                redScore += 140;
                redRots++;
            }
            blueKPA = (int)Math.round(Math.floor(SteamworksField.blueFuel / 3.0));
            redKPA = (int)Math.round(Math.floor(SteamworksField.redFuel / 3.0));
            blueKPA += SteamworksField.blueFuelInAuto;
            redKPA += SteamworksField.redFuelInAuto;
            blueScore += blueKPA;
            redScore += redKPA;
            if (blueKPA >= 40) blueScore += 20;
            if (redKPA >= 40) redScore += 20;
            for (Robot r : Main.robots) {
                SteamworksMetadata meta = (SteamworksMetadata) r.metadata;
                if (meta.crossedBaseline) {
                    if (r.blue) blueScore += 5;
                    else redScore += 5;
                }
            }
            if (seconds <= 30) {
                for (Robot r : Main.robots) {
                    SteamworksMetadata meta = (SteamworksMetadata) r.metadata;
                    if (meta.onRope != null && System.currentTimeMillis() - meta.onRope > 1000) {
                        if (r.blue) blueClimbs++;
                        else redClimbs++;
                    }
                }
                blueScore += blueClimbs * 50;
                redScore += redClimbs * 50;
            }
        }
        return new int[]{blueScore, redScore};
    }

    public void drawGearDisplay(float x, float y, int gears, Color c, SpriteBatch b) {
        b.draw(Gear.TEXTURE, x, y, 30f, 30f);
        Main.smallFont.setColor(c);
        Main.smallFont.draw(b, gears + "", x + 15 - (getWidth(gears + "", Main.smallFont) / 2), y + 55);
    }
}
