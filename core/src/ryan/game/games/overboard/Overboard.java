package ryan.game.games.overboard;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.competition.Match;
import ryan.game.competition.Schedule;
import ryan.game.competition.overboard.OverboardTeam;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;
import ryan.game.entity.overboard.*;
import ryan.game.games.Field;
import ryan.game.games.Game;
import ryan.game.games.ScoreDisplay;
import ryan.game.games.steamworks.SteamResultDisplay;
import ryan.game.render.Drawable;
import ryan.game.render.ImageDrawer;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Overboard extends Field {

    public PirateDisplay display;
    public static Ship[] ships = new Ship[2];

    public HP_Receive[] blue_hp = new HP_Receive[2];
    public HP_Receive[] red_hp = new HP_Receive[2];

    float width = 51 * .915f;
    float height = 26 * .915f;

    @Override
    public List<Drawable> generateField() {
        List<Drawable> drawables = new ArrayList<>();

        drawables.add(new ImageDrawer(-width/2, -height/2 - .5f, width, height, "core/assets/overboard.png"));

        Entity ent = Entity.barrier(0, 11.75f, width, .5f); //Top wall
        ent.initVisuals(width, .5f);
        drawables.add(ent);

        ent = Entity.barrier(0, -12.75f, width, .5f); //Bottom wall
        ent.initVisuals(width, .5f);
        drawables.add(ent);

        ent = Entity.barrier((width / 2)-3.2f, 0, .5f, height); //Right wall
        ent.initVisuals(.5f, height);
        drawables.add(ent);

        ent = Entity.barrier((-width / 2)+2.9f, 0, .5f, height); //Left wall
        ent.initVisuals(.5f, height);
        drawables.add(ent);

        ships[0] = new Ship(12.3f, -0.45f, true);
        drawables.add(ships[0]);

        ships[1] = new Ship(-12.65f, -0.45f, false);
        drawables.add(ships[1]);

        drawables.add(new Barricade(-0.14f, 0));

        blue_hp[0] = new HP_Receive(20.1f, 5.5f, true);
        blue_hp[1] = new HP_Receive(20.1f, 9f, true);
        Collections.addAll(drawables, blue_hp);

        red_hp[0] = new HP_Receive(-20.1f, -5.5f, false);
        red_hp[1] = new HP_Receive(-20.1f, -9f, false);
        Collections.addAll(drawables, red_hp);

        for (Chest c : generateChests()) {
            Main.getInstance().addFriction(c.getPrimary(), c.friction);
            drawables.add(c);
        }

        for (Cannonball b : generateCannonballs()) {
            Main.getInstance().addFriction(b.getPrimary(), b.friction);
            drawables.add(b);
        }

        display = new PirateDisplay();
        drawables.add(display);

        return drawables;
    }

    public List<Chest> generateChests() {
        List<Chest> chests = new ArrayList<>();

        int heavy = Utils.randomInt(0, 11);
        int current = 0;
        for (int bX=0; bX<4; bX++) {
            for (int bY=0; bY<3; bY++) {
                chests.add(new Chest(bX + 16, bY - 11, current == heavy, Game.ALLIANCE.RED));
                current++;
            }
        }

        heavy = Utils.randomInt(0, 11);
        current = 0;
        for (int bX=0; bX<4; bX++) {
            for (int bY=0; bY<3; bY++) {
                chests.add(new Chest(bX - 19.5f, bY + 8, current == heavy, Game.ALLIANCE.BLUE));
                current++;
            }
        }

        heavy = Utils.randomInt(0, 11);
        current = 0;
        for (int bX=0; bX<2; bX++) {
            for (int bY=0; bY<6; bY++) {
                chests.add(new Chest(bX - 0.7f, bY - 3.5f, current == heavy, Game.ALLIANCE.NEUTRAL));
                current++;
            }
        }
        return chests;
    }

    public List<Cannonball> generateCannonballs() {
        List<Cannonball> balls = new ArrayList<>();

        for (int x=0; x<2; x++) {
            float bX = 6f * (x == 0 ? -1 : 1);
            for (int y=0; y<10; y++) {
                float bY = 10 - (y * 2f);
                balls.add(new Cannonball(bX, bY));
            }
        }
        return balls;
    }

    @Override
    public void affectRobots() {
        for (Robot r : Main.robots) {
            r.metadata = new PirateMetadata();
        }
    }

    @Override
    public void onMatchStart() {
        for (Robot r : Main.robots) {
            r.auto = r.stats.getAutonomous(r);
            PirateMetadata m = (PirateMetadata) r.metadata;
            m.chests.clear();
        }
    }

    @Override
    public void onMatchEnd() {
        Match current = Main.schedule.getCurrentMatch();
        Main.schedule.completeCurrentMatch(0,0,null,null,0);

        //showResults(new SteamResultDisplay(current));

        updateMatchInfo();
    }

    @Override
    public void updateMatchInfo() {
        super.updateMatchInfo();
        Match m = Main.schedule.getCurrentMatch();
        blue_hp[0].updateStats((OverboardTeam)m.blue.getTeams().get(0));
        blue_hp[1].updateStats((OverboardTeam)m.blue.getTeams().get(1));

        red_hp[0].updateStats((OverboardTeam)m.red.getTeams().get(0));
        red_hp[1].updateStats((OverboardTeam)m.red.getTeams().get(1));
    }

    @Override
    public void resetField(List<Drawable> field) {
        for (Drawable d : new ArrayList<>(field)) {
            if (d instanceof Chest) {
                for (Body b : (((Chest) d).getBodies())) {
                    Main.getInstance().world.destroyBody(b);
                }
                field.remove(d);
            }
        }
        for (Chest c : generateChests()) {
            field.add(c);
            Main.getInstance().addFriction(c.getPrimary(), c.friction);
        }
    }

    @Override
    public ScoreDisplay getDisplay() {
        return display;
    }


    boolean did30SecWhoop = false;

    @Override
    public void tick() {
        super.tick();
        if (Game.getMatchTime() == 30 && !did30SecWhoop) {
            Main.getInstance().ropeDropSound.play(.35f);
            did30SecWhoop = true;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {}

    public static List<OverboardTeam> createBaconTeams() {
        List<OverboardTeam> teams = new ArrayList<>();
        Collections.addAll(teams,
                new OverboardTeam(1738, "Team 1738", Color.BLACK, Color.WHITE, null),
                new OverboardTeam(5916, "Baking Bot", Color.PINK, Color.BLUE, null),
                new OverboardTeam(452, "The Aluminuns", Color.LIGHT_GRAY, Color.BLACK, null),
                new OverboardTeam(1776, "Freedom Fighters", Color.RED, Color.BLUE, null),
                new OverboardTeam(72, "Team Cody", Color.YELLOW, Color.RED, null),
                new OverboardTeam(404, "Team Name Not Found", Color.GREEN, Color.PINK, null),
                new OverboardTeam(1432, "Sheared Sprockets", Color.BLACK, Color.LIGHT_GRAY, null),
                new OverboardTeam(4010, "Merlin's Mechanics", Color.BLUE, Color.GOLD, null),
                new OverboardTeam(62, "DinoBotz", Color.GREEN, Color.BROWN, null),
                new OverboardTeam(5827, "Milkshake Ninjas", Color.PINK, Color.BLACK, null),
                new OverboardTeam(6868, "Team Tofu", Color.GREEN, Color.WHITE, null),
                new OverboardTeam(2450, "Larr-E", Color.BLACK, Color.YELLOW, null),
                new OverboardTeam(4200, "Team Turnip", Color.PURPLE, Color.GREEN, null),
                new OverboardTeam(2051, "U.F.B.", Color.GREEN, Color.GRAY, null),
                new OverboardTeam(4769, "That OTHER Team", Color.WHITE, Color.RED, null),
                new OverboardTeam(4664, "The Riddlers", Color.BLACK, Color.WHITE, null),
                new OverboardTeam(964, "Las Harambes", Color.WHITE, Color.BLACK, null),
                new OverboardTeam(6809, "Burning Butterflies", Color.YELLOW, Color.ORANGE, null),
                new OverboardTeam(130, "Cincinnati's Team", Color.YELLOW, Color.BLUE, null),
                new OverboardTeam(4502, "Garglesquid", Color.BLUE, Color.PURPLE, null),
                new OverboardTeam(1880, "L.O.O.F.A.H.", Color.BLUE, Color.WHITE, null),
                new OverboardTeam(6015, "Death Star Robotics", Color.RED, Color.BLACK, null),
                new OverboardTeam(332, "Cerulean", Color.TEAL, Color.WHITE, null),
                new OverboardTeam(5567, "Space Pirates", Color.GOLD, Color.RED, null),
                new OverboardTeam(1687, "Citric Acid", Color.YELLOW, Color.BLACK, null),
                new OverboardTeam(1264, "ARES (Arizona Robotics Engineering School)", Color.GOLD, Color.WHITE, null),
                new OverboardTeam(4862, "Renegade", Color.ORANGE, Color.BLACK, null),
                new OverboardTeam(2977, "Team H.E.C.K.", Color.GREEN, Color.GREEN, null),
                new OverboardTeam(76, "Team Scorch", Color.YELLOW, Color.RED, null),
                new OverboardTeam(2213, "Aftershock", Color.RED, Color.BROWN, null)
        );

        return teams;
    }
}
