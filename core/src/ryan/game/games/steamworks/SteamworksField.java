package ryan.game.games.steamworks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.competition.Schedule;
import ryan.game.competition.Team;
import ryan.game.entity.*;
import ryan.game.entity.steamworks.*;
import ryan.game.games.Field;
import ryan.game.games.Game;
import ryan.game.games.steamworks.robots.SteamRobotStats;
import ryan.game.render.Drawable;
import ryan.game.render.ImageDrawer;

import java.util.ArrayList;
import java.util.List;

public class SteamworksField extends Field {

    public static List<HumanPlayer> humanPlayers = new ArrayList<>();

    public static final float hpGearScoreSpeed = 4000f;

    List<Sprite> blueRotors = new ArrayList<>();
    List<Sprite> redRotors = new ArrayList<>();

    public static SteamworksDisplay display;

    public static int blueGears = 0;
    public static int blueGearsInAuto = 0;
    public static int redGears = 0;
    public static int redGearsInAuto = 0;
    public static int blueGearQueue = 0;
    public static int redGearQueue = 0;
    public static int blueFuel = 0;
    public static int blueFuelInAuto = 0;
    public static int redFuel = 0;
    public static int redFuelInAuto = 0;
    public static int blueFouls = 0;
    public static int redFouls = 0;
    public static int blueBonusClimbs = 0;
    public static int redBonusClimbs = 0;
    boolean addedBonusGears = false;
    boolean didRopeDropWhoop = false;

    public SteamworksField() {
        humanPlayers.add(new HumanPlayer(true));
        humanPlayers.add(new HumanPlayer(true));
        humanPlayers.add(new HumanPlayer(false));
        humanPlayers.add(new HumanPlayer(false));
    }

    @Override
    public List<Drawable> generateField() {
        List<Drawable> drawables = new ArrayList<>();

        drawables.add(new ImageDrawer(-27.5f, -15, 54, 30, "core/assets/steamworks_norotors.png"));

        drawables.add(new Hopper(-9.5f, 12.25f, true)); //left top hopper
        drawables.add(new Hopper(8.35f, 12.25f, true)); //right top hopper


        drawables.add(new Hopper(-16f, -13.45f, false)); //left bottom hopper
        drawables.add(new Hopper(-0.6f, -13.45f, false)); //middle bottom hopper
        drawables.add(new Hopper(16f - 1.15f, -13.45f, false)); //right bottom hopper


        drawables.add(Entity.barrier(0, 12, 28f, .5f)); //top wall
        drawables.add(Entity.barrier(0, -13.2f, 28f, .5f)); //bottom wall
        drawables.add(Entity.barrier(-25.75f, 0, .5f, 10f)); //left wall
        drawables.add(Entity.barrier(24.5f, 0, .5f, 10f)); //right wall

        drawables.add(new LoadingStation(-24.8f, 11, 26, true, true)); //blue load left
        drawables.add(new LoadingStation(-21.6f, 12.65f, 26, true, false)); //blue load right

        drawables.add(new LoadingStation(20.4f, 12.65f, -26, false, true)); //red load left
        drawables.add(new LoadingStation(23.6f, 11, -26, false, false)); //red load right

        drawables.add(Entity.barrier(-23, 12, 3f, 2f).setAngle(26)); //Blue load barrier
        drawables.add(Entity.barrier(22, 12, 3f, 2f).setAngle(-26)); //Red load barrier

        drawables.add(Entity.barrier(-24.8f, -12.9f, 2f, 2f).setAngle(46)); //Red boiler
        drawables.add(Entity.barrier(23.8f, -12.9f, 2f, 2f).setAngle(-46)); //Blue boiler

        PolygonShape s = new PolygonShape();
        PolygonShape s2 = new PolygonShape();

        float acrossDistance = 3.77f;
        float pointDistance = 2.2f;
        float riseDistance = 4.4f;

        Vector2[] vertices = new Vector2[] {
                new Vector2(0, 0),
                new Vector2(acrossDistance, -pointDistance),
                new Vector2(acrossDistance * 2, 0),
                new Vector2(acrossDistance * 2, riseDistance),
                new Vector2(acrossDistance, riseDistance + pointDistance),
                new Vector2(0, riseDistance)
        };
        s.set(vertices);
        s2.set(vertices);

        drawables.add(Entity.barrier(8.9f, -2.75f, s)); //blue airship
        drawables.add(Entity.barrier(-17.65f, -2.75f, s2)); //red airship

        drawables.add(Entity.peg(-18.7f, -.57f, 0));
        drawables.add(Entity.peg(-16.25f, 3.25f, 360-60));
        drawables.add(Entity.peg(-16f, -4.25f, 60));

        float xFix = 1.55f;

        drawables.add(Entity.peg(18.9f - xFix, -.57f, 0));
        drawables.add(Entity.peg(16.25f - xFix, 3.25f, 60));
        drawables.add(Entity.peg(16.25f - xFix, -4.25f, 360-60));

        float ropeFix = 1.25f;
        drawables.add(new Rope(11.6f - ropeFix, 3.25f, 300, true)); //blue top
        drawables.add(new Rope(18.35f - ropeFix, -.5f, 0, true)); //blue middle
        drawables.add(new Rope(11.6f - ropeFix, -4.45f, 60, true)); //blue bottom

        drawables.add(new Rope(-11.6f, 3.25f, 60, false)); //red top
        drawables.add(new Rope(-18.35f, -.5f, 0, false)); //red middle
        drawables.add(new Rope(-11.6f, -4.45f, 300, false)); //red bottom

        drawables.add(new Boiler(-24.6f, -12.25f, false)); //red boiler
        drawables.add(new Boiler(23.4f, -12.25f, true));

        drawables.add(new Baseline(16.75f, 0, true)); //red baseline
        drawables.add(new Baseline(-18f, 0, false)); //blue baseline

        drawables.add(new LoadingZone(-21, 9.75f, 26, true)); //blue loading zone
        drawables.add(new LoadingZone(19.5f, 9.75f, -26, false)); //red loading zone

        for (int i=0; i<2; i++) {

            Color color = i == 0 ? Main.RED : Main.BLUE;
            List<Sprite> rotors = i == 0 ? redRotors : blueRotors;
            float startX = i == 0 ? -18 : 8.5f;
            float startY = i == 0 ? -3.5f : .4f;

            for (int r=0; r<3; r++) {
                Sprite sprite = new Sprite(Utils.colorImage("core/assets/rotor.png", color));

                float x = r == 0 ? startX : (r == 1 ? startX + 7f : startX + 3.5f);
                float y = r == 0 ? startY : (r == 1 ? startY : startY + (6f * (i == 0 ? 1 : -1)));
                sprite.setBounds(x, y, 2f, 2f);
                sprite.setOrigin(.7f, 1f);
                if (i == 1) sprite.setRotation(180);
                rotors.add(sprite);
            }
        }

        display = new SteamworksDisplay();
        drawables.add(display);
        return drawables;
    }

    @Override
    public void affectRobots() {
        for (Robot r : Main.robots) {
            r.metadata = new SteamworksMetadata();
        }
    }

    @Override
    public void onMatchStart() {
        blueGears = 0;
        blueGearsInAuto = 0;
        redGears = 0;
        redGearsInAuto = 0;
        blueGearQueue = 0;
        redGearQueue = 0;
        blueFuel = 0;
        blueFuelInAuto = 0;
        redFuel = 0;
        redFuelInAuto = 0;
        blueBonusClimbs = 0;
        redBonusClimbs = 0;
        blueFouls = 0;
        redFouls = 0;
        addedBonusGears = false;
        didRopeDropWhoop = false;
        for (Robot r : Main.robots) {
            r.auto = r.stats.getAutonomous(r);
            SteamworksMetadata m = (SteamworksMetadata) r.metadata;
            m.hasGear = true;
            m.fuel = 10;
            m.crossedBaseline = false;
        }
    }

    @Override
    public void onMatchEnd() {
        Main.schedule.completeCurrentMatch(0, 0); //TODO: pass in scores
        updateMatchInfo();
    }

    @Override
    public void updateMatchInfo() {
        Schedule.Match m = Main.schedule.getCurrentMatch();
        display.setBlueTeams(m.blue[0].number, m.blue[1].number, m.blue[2].number);
        display.setRedTeams(m.red[0].number, m.red[1].number, m.red[2].number);
        if (m.qualifier) {
            display.setMatchName("Qualification " + m.number + " of " + Main.schedule.matches.size());
        } else {
            display.setMatchName("Elimination Match"); //TODO: when the match object provides more elimination data, show it here
        }
    }

    @Override
    public void resetField(List<Drawable> field) {
        for (Drawable d : new ArrayList<>(field)) {
            if (d instanceof Entity) {
                Entity e = (Entity) d;
                if (e.getName().equalsIgnoreCase("fuel") || e.getName().equalsIgnoreCase("gear")) {
                    for (Body b : e.getBodies()) {
                        Main.getInstance().world.destroyBody(b);
                    }
                    field.remove(e);
                } else if (e instanceof Hopper) {
                    ((Hopper) e).reset();
                }
            }
        }
    }

    int blueSpinning, redSpinning;

    class HumanPlayer {
        Long scoreProgress = null;
        boolean blue;

        public HumanPlayer(boolean blue) {
            this.blue = blue;
        }
    }

    @Override
    public void tick() {
        super.tick();
        for (HumanPlayer h : humanPlayers) {
            if (h.scoreProgress == null) {
                if (h.blue && blueGearQueue > 0) {
                    blueGearQueue--;
                    h.scoreProgress = System.currentTimeMillis();
                } else if (!h.blue && redGearQueue > 0) {
                    redGearQueue--;
                    h.scoreProgress = System.currentTimeMillis();
                }
            } else {
                if (System.currentTimeMillis() - h.scoreProgress >= hpGearScoreSpeed) {
                    if (h.blue) blueGears++;
                    else redGears++;
                    if (Game.isAutonomous()) {
                        if (h.blue) blueGearsInAuto++;
                        else redGearsInAuto++;
                    }
                    h.scoreProgress = null;
                }
            }
        }

        if (Game.isPlaying()) {
            if (Game.getMatchTime() == 135 && !addedBonusGears) {
                blueGearQueue++;
                redGearQueue++;
                addedBonusGears = true;
            }

            if (Game.getMatchTime() == 30 && !didRopeDropWhoop) {
                Main.getInstance().ropeDropSound.play(.35f);
                didRopeDropWhoop = true;
            }
        }
        blueSpinning = 0;
        if (blueGears > 12) blueSpinning = 3;
        else if (blueGears > 6) blueSpinning = 2;
        else if (blueGears > 2) blueSpinning = 1;

        redSpinning = 0;
        if (redGears > 12) redSpinning = 3;
        else if (redGears > 6) redSpinning = 2;
        else if (redGears > 2) redSpinning = 1;
    }

    @Override
    public void draw(SpriteBatch b) {
        int red = 0;
        for (Sprite s : redRotors) {
            if (Main.matchPlay) {
                if (red < redSpinning) s.setRotation(s.getRotation() + 4f);
            } else s.setRotation(0);
            s.draw(b);
            red++;
        }

        int blue = 0;
        for (Sprite s : blueRotors) {
            if (Main.matchPlay) {
                if (blue < blueSpinning) s.setRotation(s.getRotation() - 4f);
            } else s.setRotation(180);
            s.draw(b);
            blue++;
        }
    }

    public static int[] predictScore(Schedule.Match m) {
        return new int[]{predictScore(m.blue), predictScore(m.red)};
    }

    private static int predictScore(Team[] alliance) {
        int gears = 0;
        int climbs = 0;
        int fuel = 0;
        for (Team t : alliance) {
            if (t.stats != null && t.stats instanceof SteamRobotStats) {
                SteamRobotStats stats = (SteamRobotStats) t.stats;
                //TODO
            }
        }
        return 0; //TODO
    }
}
