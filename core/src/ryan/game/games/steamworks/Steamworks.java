package ryan.game.games.steamworks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.competition.Match;
import ryan.game.competition.RobotStats;
import ryan.game.competition.Schedule;
import ryan.game.entity.*;
import ryan.game.entity.steamworks.*;
import ryan.game.games.Field;
import ryan.game.games.Game;
import ryan.game.games.RobotMetadata;
import ryan.game.games.ScoreDisplay;
import ryan.game.games.steamworks.robots.SteamDefault;
import ryan.game.games.steamworks.robots.SteamRobotStats;
import ryan.game.render.Drawable;
import ryan.game.render.ImageDrawer;
import ryan.game.screens.GameScreen;

import java.util.ArrayList;
import java.util.List;

public class Steamworks extends Field {

    public static List<HumanPlayer> humanPlayers = new ArrayList<>();

    public static final float hpGearScoreSpeed = 4000f;

    List<Sprite> blueRotors = new ArrayList<>();
    List<Sprite> redRotors = new ArrayList<>();

    public static SteamworksDisplay display;

    public static AllianceScoreData blue;
    public static AllianceScoreData red;

    boolean addedBonusGears = false;
    boolean didRopeDropWhoop = false;

    Sprite[] blueHumans, redHumans;

    public Steamworks() {
        blue = new AllianceScoreData(true);
        red = new AllianceScoreData(false);
        humanPlayers.add(new HumanPlayer(true));
        humanPlayers.add(new HumanPlayer(true));
        humanPlayers.add(new HumanPlayer(false));
        humanPlayers.add(new HumanPlayer(false));
    }

    public void updateHumanSprites() {
        for (int i = 0; i < 2; i++) {
            Sprite[] array = new Sprite[3];
            int[] teams = i == 0 ? display.getRedTeams() : display.getBlueTeams();
            for (int h = 0; h < 3; h++) {
                Color primary = i == 0 ? Main.RED : Main.BLUE;
                Color secondary = Color.GRAY;
                int team = teams[h];
                boolean robotExists = false;
                for (Robot r : GameScreen.robots) {
                    if (r.getNumber() == team) {
                        robotExists = true;
                        if (r.stats.recolorIndex == -1) {  //TODO: do this better
                            primary = r.stats.custom_primary;
                            secondary = r.stats.custom_secondary;
                        }
                    }
                }
                if (robotExists) {
                    Sprite p = new Sprite(Utils.colorImage("core/assets/person.png", primary, Color.BROWN, Utils.toColor(255, 204, 165), secondary));
                    p.setPosition((i == 0 ? -31.3f : 20f) + 0.6f, -13 - (7 * h));
                    p.setScale(0.08f);
                    if (i == 0) p.setSize(-p.getWidth(), p.getHeight());
                    array[h] = p;
                } else {
                    array[h] = null;
                }
            }
            if (i == 0) redHumans = array;
            else blueHumans = array;
        }
    }

    @Override
    public List<Drawable> generateField() {
        List<Drawable> drawables = new ArrayList<>();

        drawables.add(new ImageDrawer(-30, -30, 60, 60, "core/assets/carpet_square.png"));

        float oX = 0.6f;
        drawables.add(new ImageDrawer(-27.5f + oX, -15, 54, 30, "core/assets/steamworks_norotors.png"));

        drawables.add(new Hopper(-9.5f + oX, 12.25f, true)); //left top hopper
        drawables.add(new Hopper(8.35f + oX, 12.25f, true)); //right top hopper


        drawables.add(new Hopper(-16f + oX, -13.45f, false)); //left bottom hopper
        drawables.add(new Hopper(-0.6f + oX, -13.45f, false)); //middle bottom hopper
        drawables.add(new Hopper(16f - 1.15f + oX, -13.45f, false)); //right bottom hopper


        drawables.add(Entity.barrier(0 + oX, 12, 28f, .5f)); //top wall
        drawables.add(Entity.barrier(0 + oX, -13.2f, 28f, .5f)); //bottom wall
        drawables.add(Entity.barrier(-25.75f + oX, 0, .5f, 10f)); //left wall
        drawables.add(Entity.barrier(24.5f + oX, 0, .5f, 10f)); //right wall

        drawables.add(new LoadingStation(-24.8f + oX, 11, 26, true, true)); //blue load left
        drawables.add(new LoadingStation(-21.6f + oX, 12.65f, 26, true, false)); //blue load right

        drawables.add(new LoadingStation(20.4f + oX, 12.65f, -26, false, true)); //red load left
        drawables.add(new LoadingStation(23.6f + oX, 11, -26, false, false)); //red load right

        drawables.add(Entity.barrier(-23 + oX, 12, 3f, 2f).setAngle(26)); //Blue load barrier
        drawables.add(Entity.barrier(22 + oX, 12, 3f, 2f).setAngle(-26)); //Red load barrier

        drawables.add(Entity.barrier(-24.8f + oX, -12.9f, 2f, 2f).setAngle(46)); //Red boiler
        drawables.add(Entity.barrier(23.8f + oX, -12.9f, 2f, 2f).setAngle(-46)); //Blue boiler

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

        drawables.add(Entity.barrier(8.9f + oX, -2.75f, s)); //blue airship
        drawables.add(Entity.barrier(-17.65f + oX, -2.75f, s2)); //red airship

        drawables.add(Entity.peg(-18.7f + oX, -.57f, 0));
        drawables.add(Entity.peg(-16.25f + oX, 3.25f, 360-60));
        drawables.add(Entity.peg(-16f + oX, -4.25f, 60));

        float xFix = 1.55f;

        drawables.add(Entity.peg(18.9f - xFix + oX, -.57f, 0));
        drawables.add(Entity.peg(16.25f - xFix + oX, 3.25f, 60));
        drawables.add(Entity.peg(16.25f - xFix + oX, -4.25f, 360-60));

        float ropeFix = 1.25f;
        drawables.add(new Rope(11.6f - ropeFix + oX, 3.25f, 300, true).setId(0)); //blue top
        drawables.add(new Rope(18.35f - ropeFix + oX, -.5f, 0, true).setId(1)); //blue middle
        drawables.add(new Rope(11.6f - ropeFix + oX, -4.45f, 60, true).setId(2)); //blue bottom

        drawables.add(new Rope(-11.6f + oX, 3.25f, 60, false).setId(0)); //red top
        drawables.add(new Rope(-18.35f + oX, -.5f, 0, false).setId(1)); //red middle
        drawables.add(new Rope(-11.6f + oX, -4.45f, 300, false).setId(2)); //red bottom

        drawables.add(new Boiler(-24.6f + oX, -12.25f, false)); //red boiler
        drawables.add(new Boiler(23.4f + oX, -12.25f, true));

        drawables.add(new Baseline(16.75f + oX, 0, true)); //red baseline
        drawables.add(new Baseline(-18f + oX, 0, false)); //blue baseline

        drawables.add(new LoadingZone(-21 + oX, 9.75f, 26, true)); //blue loading zone
        drawables.add(new LoadingZone(19.5f + oX, 9.75f, -26, false)); //red loading zone

        for (int i=0; i<2; i++) {

            Color color = i == 0 ? Main.RED : Main.BLUE;
            List<Sprite> rotors = i == 0 ? redRotors : blueRotors;
            float startX = i == 0 ? -18 + oX : 8.5f + oX;
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
    public RobotMetadata generateMetadata() {
        return new SteamworksMetadata();
    }

    @Override
    public void affectRobots() {
        for (Robot r : GameScreen.robots) {
            r.metadata = generateMetadata();
        }
    }

    @Override
    public void onMatchStart() {
        blue = new AllianceScoreData(true);
        red = new AllianceScoreData(false);

        addedBonusGears = false;
        didRopeDropWhoop = false;

        for (Robot r : GameScreen.robots) {
            r.auto = r.stats.getAutonomous(r);
            SteamworksMetadata m = (SteamworksMetadata) r.metadata;
            SteamRobotStats stats = (SteamRobotStats) r.stats;
            if (m == null || !(m instanceof SteamworksMetadata)) {
                m = new SteamworksMetadata();
                r.metadata = m;
            }
            m.hasGear = stats.gearHPStation || stats.gearIntake;
            m.fuel = stats.shooter ? 10 : 0;
            m.crossedBaseline = false;
        }
    }

    @Override
    public void onMatchEnd() {
        if (blue.kPA >= 40) blue.rankingPoints++;
        if (blue.rotors == 4) blue.rankingPoints++;

        if (red.kPA >= 40) red.rankingPoints++;
        if (red.rotors == 4) red.rankingPoints++;

        Match current = GameScreen.schedule.getCurrentMatch();


        int winner = -1;
        if (blue.score > red.score) winner = 0;
        else if (red.score > blue.score) winner = 1;
        else {
            //TODO: add auto points tiebreaker here
            if (blue.fouls < red.fouls) winner = 0;
            else if (red.fouls < blue.fouls) winner = 1;
            else {
                if (blue.rotorPoints > red.rotorPoints) winner = 0;
                else if (red.rotorPoints > blue.rotorPoints) winner = 1;
                else {
                    if (blue.climbs > red.climbs) winner = 0;
                    else if (red.climbs > blue.climbs) winner = 1;
                    else {
                        if (blue.kPA > red.kPA) winner = 0;
                        else if (red.kPA > blue.kPA) winner = 1;
                    }
                }
            }
        }

        GameScreen.schedule.completeCurrentMatch(blue.score, red.score, blue, red, winner);

        showResults(new SteamResultDisplay(current));

        updateMatchInfo();
        blue = new AllianceScoreData(true);
        red = new AllianceScoreData(false);
        updateHumanSprites();
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

    @Override
    public ScoreDisplay getDisplay() {
        return display;
    }

    @Override
    public RobotStats getDefaultRobotStats() {
        return new SteamDefault();
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
                if (h.blue && blue.gearQueue > 0) {
                    blue.gearQueue--;
                    h.scoreProgress = GameScreen.getTime();
                } else if (!h.blue && red.gearQueue > 0) {
                    red.gearQueue--;
                    h.scoreProgress = GameScreen.getTime();
                }
            } else {
                if (GameScreen.getTime() - h.scoreProgress >= hpGearScoreSpeed) {
                    if (h.blue) blue.gears++;
                    else red.gears++;
                    if (Game.isAutonomous()) {
                        if (h.blue) blue.gearsInAuto++;
                        else red.gearsInAuto++;
                    }
                    h.scoreProgress = null;
                }
            }
        }

        if (Game.isPlaying()) {
            if (Game.getMatchTime() == 135 && !addedBonusGears) {
                blue.gearQueue++;
                red.gearQueue++;
                addedBonusGears = true;
            }

            if (Game.getMatchTime() == 30 && !didRopeDropWhoop) {
                GameScreen.ropeDropSound.play(.35f);
                didRopeDropWhoop = true;
            }
        }
        blueSpinning = Math.round(Utils.deadzone(blue.rotors-1, .1f));
        redSpinning = Math.round(Utils.deadzone(red.rotors-1, .1f));
    }

    @Override
    public void draw(SpriteBatch b) {
        int red = 0;
        for (Sprite s : redRotors) {
            if (GameScreen.matchPlay) {
                if (red < redSpinning) s.setRotation(s.getRotation() + 4f);
            } else s.setRotation(0);
            s.draw(b);
            red++;
        }

        int blue = 0;
        for (Sprite s : blueRotors) {
            if (GameScreen.matchPlay) {
                if (blue < blueSpinning) s.setRotation(s.getRotation() - 4f);
            } else s.setRotation(180);
            s.draw(b);
            blue++;
        }

        if (blueHumans != null) {
            for (Sprite s : blueHumans) {
                if (s != null) s.draw(b);
            }
        }
        if (redHumans != null) {
            for (Sprite s : redHumans) {
                if (s != null) s.draw(b);
            }
        }
    }
}
