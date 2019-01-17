package ryan.game.games.destination;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.competition.Match;
import ryan.game.competition.RobotStats;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;
import ryan.game.entity.destination.Cargo;
import ryan.game.entity.destination.Panel;
import ryan.game.entity.destination.SpotToScore;
import ryan.game.games.Field;
import ryan.game.games.Game;
import ryan.game.games.RobotMetadata;
import ryan.game.games.ScoreDisplay;
import ryan.game.games.steamworks.AllianceScoreData;
import ryan.game.render.Drawable;
import ryan.game.render.ImageDrawer;
import ryan.game.screens.GameScreen;

import java.util.ArrayList;
import java.util.List;

public class Destination extends Field {

    public static List<HumanPlayer> humanPlayers = new ArrayList<>();

    public static final float hpGearScoreSpeed = 4000f;

    List<Sprite> blueRotors = new ArrayList<>();
    List<Sprite> redRotors = new ArrayList<>();

    public static ScoreDisplay display;

    public static AllianceScoreData blue;
    public static AllianceScoreData red;

    boolean addedBonusGears = false;
    boolean didRopeDropWhoop = false;

    Sprite[] blueHumans, redHumans;

    public Destination() {
        blue = new AllianceScoreData(true);
        red = new AllianceScoreData(false);
        humanPlayers.add(new HumanPlayer(true));
        humanPlayers.add(new HumanPlayer(true));
        humanPlayers.add(new HumanPlayer(false));
        humanPlayers.add(new HumanPlayer(false));
    }

    @Override
    public List<Drawable> generateField() {
        List<Drawable> drawables = new ArrayList<>();

        drawables.add(new ImageDrawer(-30, -30, 60, 60, "core/assets/carpet_square.png"));

        float oX = 0.6f;
        drawables.add(new ImageDrawer(-27.6f + oX, -13.5f, 54, 27, "core/assets/2019_field.png"));


        drawables.add(Entity.barrier(0 + oX, 13.5f, 28f, .5f)); //top wall
        drawables.add(Entity.barrier(0 + oX, -13.65f, 28f, .5f)); //bottom wall
        drawables.add(Entity.barrier(-27.5f + oX, 0, .5f, 30f)); //left wall
        drawables.add(Entity.barrier(26.5f + oX, 0, .5f, 30f)); //right wall

        drawables.add(Entity.barrier(24.55f, 0, 2f, 5.2f)); //right hab lvl 3
        drawables.add(Entity.barrier(-24.55f, 0, 2f, 5.2f)); //blue hab lvl 3

        drawables.add(Entity.barrier(0, 0, 8.25f, 2.15f)); //blue hab lvl 3

        // blue cargo ship sides
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                drawables.add(new SpotToScore(-5.19f + (x * 1.76f), (y == 0 ? 2.35f : -2.3f), true, y == 0 ? 270 : 90));
            }
        }

        // blue cargo ship front
        drawables.add(new SpotToScore(-8.6f, -0.85f, true,0));
        drawables.add(new SpotToScore(-8.6f, 0.9f, true,0));

        // red cargo ship front
        drawables.add(new SpotToScore(8.6f, -0.85f, false,180));
        drawables.add(new SpotToScore(8.6f, 0.9f, false,180));

        // red cargo ship sides
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                drawables.add(new SpotToScore(1.7f + (x * 1.76f), (y == 0 ? 2.35f : -2.3f), false, y == 0 ? 270 : 90));
            }
        }

        // bottom blue and red rockets
        for (int s = 0; s < 2; s++) {
            float x = s == 0 ? 6.2f : -9.4f;
            float y = -11.55f;
            drawables.add(new SpotToScore(x, y, s == 0, 180-30));
            drawables.add(new SpotToScore(x+3.2f, y, s == 0, 30));
        }

        drawables.addAll(generateGameElements());

        display = new ScoreDisplay() {
            @Override
            public int[] calculateScores() {
                return new int[0];
            }
        };
        drawables.add(display);
        return drawables;
    }

    public List<Drawable> generateGameElements() {
        List<Drawable> elements = new ArrayList<>();

        for (int s = 0; s < 2; s++) {
            for (int a = 0; a < 2; a++) {
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 2; y++) {
                        elements.add(new Cargo((23.5f + (1.1f * x)) * (a == 0 ? 1 : -1),
                                (s == 0 ? 6.0f : -6) + (y * 0.7f)));
                    }
                }
            }
        }

        elements.add(new Panel(25.5f,11, 0));
        elements.add(new Panel(25.5f,-11, 0));

        elements.add(new Panel(-25.5f,11, 0));
        elements.add(new Panel(-25.5f,-11, 0));

        return elements;
    }

    @Override
    public RobotMetadata generateMetadata() {
        return new DestinationMetadata();
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
            DestinationMetadata m = (DestinationMetadata) r.metadata;
            DestinationRobotStats stats = (DestinationRobotStats) r.stats;
            if (m == null || !(m instanceof DestinationMetadata)) {
                m = new DestinationMetadata();
                r.metadata = m;
            }
            m.hasPanel = false;
            m.hasCargo = false;
            m.crossedBaseline = false;
        }
    }

    @Override
    public void onMatchEnd() {
        Match current = GameScreen.schedule.getCurrentMatch();


        int winner = -1;

        GameScreen.schedule.completeCurrentMatch(blue.score, red.score, blue, red, winner);

        //showResults(new SteamResultDisplay(current));

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
                if (e.getName().equalsIgnoreCase("cargo") || e.getName().equalsIgnoreCase("panel")) {
                    for (Body b : e.getBodies()) {
                        Main.getInstance().world.destroyBody(b);
                    }
                    field.remove(e);
                }
            }
        }
        for (Drawable d : generateGameElements()) {
            field.add(d);
            if (d instanceof Entity) {
                Entity e = (Entity) d;
                Main.getInstance().addFriction(e.getPrimary(), e.friction);
            }
        }
    }

    @Override
    public ScoreDisplay getDisplay() {
        return display;
    }

    @Override
    public RobotStats getDefaultRobotStats() {
        return new DestinationRobotStats();
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
