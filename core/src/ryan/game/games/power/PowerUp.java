package ryan.game.games.power;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.competition.Match;
import ryan.game.competition.overboard.OverboardTeam;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;
import ryan.game.entity.overboard.Cannonball;
import ryan.game.entity.overboard.Chest;
import ryan.game.entity.powerup.DrivableWall;
import ryan.game.entity.powerup.Pixel;
import ryan.game.entity.steamworks.LoadingStation;
import ryan.game.games.Field;
import ryan.game.games.Game;
import ryan.game.games.ScoreDisplay;
import ryan.game.render.Drawable;
import ryan.game.render.ImageDrawer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PowerUp extends Field {

    float width = 51 * .915f;
    float height = 26 * .915f;

    PowerDisplay display;

    @Override
    public List<Drawable> generateField() {
        List<Drawable> drawables = new ArrayList<>();

        drawables.add(new ImageDrawer(-width/2, -height/2 - .5f, width, height, "core/assets/powerup_nocubes.png"));

        Entity ent = Entity.barrier(0, 10.2f, width, .5f); //Top wall
        //ent.initVisuals(width, .5f);
        drawables.add(ent);

        ent = Entity.barrier(0, -11.2f, width, .5f); //Bottom wall
        //ent.initVisuals(width, .5f);
        drawables.add(ent);

        ent = Entity.barrier((width / 2)-1.4f, 0, .5f, height); //Right wall
        //ent.initVisuals(.5f, height);
        drawables.add(ent);

        ent = Entity.barrier((-width / 2)+1.5f, 0, .5f, height); //Left wall
        //ent.initVisuals(.5f, height);
        drawables.add(ent);

        ent = Entity.barrier((-width / 2)+2.5f, 9.5f, 1.7f, 1); //Top Left HP
        ent.setAngle(37);
        drawables.add(ent);

        ent = Entity.barrier((-width / 2)+2.5f, -10.5f, 1.7f, 1); //Bottom Left HP
        ent.setAngle(180-37);
        drawables.add(ent);

        ent = Entity.barrier((width / 2)-2.5f, 9.5f, 1.7f, 1); //Top Right HP
        ent.setAngle(360-37);
        drawables.add(ent);

        ent = Entity.barrier((width / 2)-2.5f, -10.5f, 1.7f, 1); //Bottom Right HP
        ent.setAngle(37);
        drawables.add(ent);

        //(width/2)+10f for blue line

        float wall =0.1f;

        drawables.add(new DrivableWall((width / 2)-14.75f, -0.5f, wall, 5f, true)); //blue left wallthing
        drawables.add(new DrivableWall((width / 2)-14.75f+3.6f, -0.5f, wall, 5f, true)); //blue right wallthing
        drawables.add(new DrivableWall((width / 2)-14.75f+1.8f, 4.3f, 1.8f, wall, false)); //blue top wallthing

        drawables.addAll(generatePixels());

        display = new PowerDisplay();
        drawables.add(display);

        return drawables;
    }

    public List<Entity> generatePixels() {
        List<Entity> pixels = new ArrayList<>();

        for (int i=0; i<2; i++) {
            for (int pY=0; pY<6; pY++) {
                pixels.add(new Pixel(8 * (i==0?-1:1), (pY*1.75f)-5));
            }
        }
        return pixels;
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
            r.metadata = new PowerMetadata();
        }
    }

    @Override
    public void onMatchStart() {
        for (Robot r : Main.robots) {
            r.auto = r.stats.getAutonomous(r);
            PowerMetadata m = (PowerMetadata) r.metadata;
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
    }

    @Override
    public void resetField(List<Drawable> field) {
        /*
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
        }*/
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
}
