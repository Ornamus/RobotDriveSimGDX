package ryan.game.games.destination;

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
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;
import ryan.game.entity.destination.Cargo;
import ryan.game.entity.destination.HumanPlayer;
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

    public static ScoreDisplay display;

    public static AllianceScoreData blue;
    public static AllianceScoreData red;

    public List<SpotToScore> scoringSpots = new ArrayList<>();

    boolean didRopeDropWhoop = false;

    Sprite sandstorm;
    float sandstormY;

    public Destination() {
        blue = new AllianceScoreData(true);
        red = new AllianceScoreData(false);
        sandstorm = new Sprite(new Texture("core/assets/sandstorm.jpg"));
        sandstorm.setBounds(-29, 0, 58, 40);
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
                scoringSpots.add(new SpotToScore(-5.19f + (x * 1.76f), (y == 0 ? 2.35f : -2.3f), true, y == 0 ? 270 : 90));
            }
        }

        // blue cargo ship front
        scoringSpots.add(new SpotToScore(-8.6f, -0.85f, true,0));
        scoringSpots.add(new SpotToScore(-8.6f, 0.9f, true,0));

        // red cargo ship front
        scoringSpots.add(new SpotToScore(8.6f, -0.85f, false,180));
        scoringSpots.add(new SpotToScore(8.6f, 0.9f, false,180));

        // red cargo ship sides
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                scoringSpots.add(new SpotToScore(1.7f + (x * 1.76f), (y == 0 ? 2.35f : -2.3f), false, y == 0 ? 270 : 90));
            }
        }

        // bottom blue and red rockets
        for (int s = 0; s < 2; s++) {
            float x = s == 0 ? 6.2f : -9.4f;
            float y = -11.55f;

            SpotToScore left = new SpotToScore(x, y, s == 1, 180-30).configScoring(true, 0);
            SpotToScore right = new SpotToScore(x+3.2f, y, s == 1, 30).configScoring(true, 0);
            scoringSpots.add(left);
            scoringSpots.add(right);

            scoringSpots.add(new SpotToScore(x + 1.615f, y + 1f, s == 1, 270)
                    .configScoring(false, 2).setPanelRequirements(left, right));
        }

        // top blue and red rockets
        for (int s = 0; s < 2; s++) {
            float x = s == 0 ? 6.2f : -9.4f;
            float y = 11.55f;

            SpotToScore left = new SpotToScore(x, y, s == 1, 180+30).configScoring(true, 0);
            SpotToScore right = new SpotToScore(x+3.2f, y, s == 1, 180-30).configScoring(true, 0);
            scoringSpots.add(left);
            scoringSpots.add(right);

            scoringSpots.add(new SpotToScore(x + 1.615f, y - 1f, s == 1, 90)
                    .configScoring(false, 2).setPanelRequirements(left, right));
        }

        drawables.addAll(scoringSpots);

        drawables.add(new HumanPlayer(-25.95f, 11.2f, true,180));
        drawables.add(new HumanPlayer(-25.95f, -11.1f, true,180));


        PolygonShape s = new PolygonShape();
        PolygonShape s2 = new PolygonShape();


        Vector2[] bottom_rocket = new Vector2[] {
                new Vector2(0, 0),
                new Vector2(0, 1),
                new Vector2(.8f, 2.2f),
                new Vector2(2.5f, 2.2f),
                new Vector2(3.3f, 1),
                new Vector2(3.3f, 0),
                new Vector2(0, 0)
        };
        s.set(bottom_rocket);
        s2.set(bottom_rocket);

        drawables.add(Entity.barrier(-9.45f, -13.15f, s)); //blue rocket bottom
        drawables.add(Entity.barrier(6.15f, -13.15f, s2)); //red rocket bottom

        PolygonShape s3 = new PolygonShape();
        PolygonShape s4 = new PolygonShape();

        Vector2[] top_rocket = new Vector2[] {
                new Vector2(0, 0),
                new Vector2(0, -1),
                new Vector2(.8f, -2.2f),
                new Vector2(2.5f, -2.2f),
                new Vector2(3.3f, -1),
                new Vector2(3.3f, 0),
                new Vector2(0, 0)
        };

        s3.set(top_rocket);
        s4.set(top_rocket);

        drawables.add(Entity.barrier(-9.45f, 13.15f, s3)); //blue rocket top
        drawables.add(Entity.barrier(6.15f, 13.15f, s4)); //red rocket top

        drawables.addAll(generateGameElements());

        display = new ScoreDisplay() {
            @Override
            public int[] calculateScores() {
               int[] scores = new int[2];
                scores[0] = 0;
                scores[1] = 0;

               for (int i = 0; i < 2; i++) {
                   for (SpotToScore s : scoringSpots) {
                       if (s.blue == (i == 0)) {
                           if (s.hasPanel) scores[i] += 2;
                           if (s.numCargo > 0) scores[i] += 3 * s.numCargo;
                       }
                   }
               }
               return scores;
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

        /*elements.add(new Panel(25.5f,11, 0));
        elements.add(new Panel(25.5f,-11, 0));

        elements.add(new Panel(-25.5f,11, 0));
        elements.add(new Panel(-25.5f,-11, 0));*/

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

        didRopeDropWhoop = false;
        sandstormY = 20;

        for (SpotToScore s : scoringSpots) {
            s.numCargo = 0;
            s.hasPanel = false;
        }

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

    @Override
    public void tick() {
        super.tick();

        if (Game.isPlaying()) {
            if (Game.getMatchTime() == 30 && !didRopeDropWhoop) {
                GameScreen.ropeDropSound.play(.35f);
                didRopeDropWhoop = true;
            }
        }

        if (Game.isPlaying()) {
            if (Game.isAutonomous() && sandstormY > - 20) {
                sandstormY -= 1;
                if (sandstormY < -20) sandstormY = -20;
            } else if (!Game.isAutonomous() && sandstormY <= 20) {
                sandstormY += 1;
            }
        }
    }

    @Override
    public void draw(SpriteBatch b) {
        if (Game.isPlaying() && (sandstormY <= 40 || Game.isAutonomous())) {
            sandstorm.setY(sandstormY);
            sandstorm.draw(b);
        }
    }
}
