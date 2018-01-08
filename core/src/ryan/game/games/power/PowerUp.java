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
import ryan.game.entity.powerup.*;
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
    public static Switch blue_top, blue_bottom, red_top, red_bottom, tall_top, tall_bottom;
    public static PixelReceiver blue_receiver = null, red_receiver = null;

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

        ent = new HumanStation((-width / 2)+2.5f, 9.5f, 37f, true, true); //Top Left HP
        //ent.setAngle(37);
        drawables.add(ent);

        ent = new HumanStation((-width / 2)+2.5f, -10.5f, 180-37, true, false); //Bottom Left HP
        drawables.add(ent);

        ent = new HumanStation((width / 2)-2.5f, 9.5f, 360-37, false, true); //Top Right HP
        drawables.add(ent);

        ent = new HumanStation((width / 2)-2.5f, -10.5f, 270-37-15, false, false); //Bottom Right HP
        drawables.add(ent);

        drawables.add(new Baseline(13.4f, 0, true));
        drawables.add(new Baseline(-13.25f, 0, false));

        //(width/2)+10f for blue line

        float wall =0.1f;
        float height = 4.8f;

        for (int i=0; i<2; i++) {
            float x = 0;
            if (i == 0) {
                x = (width / 2) - 14.75f;
            } else {
                x = (-width/2) + 14.75f - 3.6f;
            }
            drawables.add(new NonCubeBarrier(x, -0.5f, wall, height)); //blue left switch wall
            drawables.add(new NonCubeBarrier(x + 3.6f, -0.5f, wall, height)); //blue right switch wall
            drawables.add(new NonCubeBarrier(x + 1.8f, 4.3f, 1.8f, wall)); //blue top switch wall
            drawables.add(new NonCubeBarrier(x + 1.8f, -5.2f, 1.8f, wall)); //blue top switch wall
        }


        blue_top = new Switch(10.4f,3); //blue top
        drawables.add(blue_top);
        blue_bottom = new Switch(10.4f,-4); //blue bottom
        drawables.add(blue_bottom);

        red_top = new Switch(-10.4f,3); //red top
        drawables.add(red_top);
        red_bottom = new Switch(-10.4f,-4); //red bottom
        drawables.add(red_bottom);

        tall_top = new Switch(0,4.3f, true); //tall top
        drawables.add(tall_top);
        tall_bottom = new Switch(0,-5.3f, true); //tall bottom
        drawables.add(tall_bottom);

        blue_receiver = new PixelReceiver(22.1f,-2.4f, true); //blue pixel receiver
        drawables.add(blue_receiver);

        red_receiver = new PixelReceiver(-22,1.35f, false); //blue pixel receiver
        drawables.add(red_receiver);

        drawables.add(new ClimbingBar(.9f,-0.5f,true));
        drawables.add(new ClimbingBar(-.7f,-0.5f,false));

        drawables.add(Entity.barrier(0,-0.5f,.6f, 4.2f)); //.5f

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

        for (int i=0; i<2; i++) {
            for (int pY=0; pY<10; pY++) {
                pixels.add(new Pixel((13+(pY<4?1:0)) * (i==0?-1:1), ((pY-(pY>4?4:0))*0.5f)-1.5f-(pY>=4?.5f:0)));
            }
        }
        return pixels;
    }

    @Override
    public String getGameString(Game.ALLIANCE alliance) {
        String s = "";
        if (alliance == Game.ALLIANCE.BLUE) {
            s += blue_bottom.alliance == alliance ? "L" : "R";
            s += tall_bottom.alliance == alliance ? "L" : "R";
            s += red_bottom.alliance == alliance ? "L" : "R";
        } else {
            s += red_top.alliance == alliance ? "L" : "R";
            s += tall_top.alliance == alliance ? "L" : "R";
            s += blue_top.alliance == alliance ? "L" : "R";
        }
        return s;
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
            m.pixels=1;
        }
        int ran = Utils.randomInt(0,1);
        blue_bottom.alliance = ran == 0 ? Game.ALLIANCE.BLUE : Game.ALLIANCE.RED;
        blue_top.alliance = ran == 1 ? Game.ALLIANCE.BLUE : Game.ALLIANCE.RED;

        ran = Utils.randomInt(0,1);
        red_bottom.alliance = ran == 0 ? Game.ALLIANCE.BLUE : Game.ALLIANCE.RED;
        red_top.alliance = ran == 1 ? Game.ALLIANCE.BLUE : Game.ALLIANCE.RED;

        ran = Utils.randomInt(0,1);
        tall_top.alliance = ran == 0 ? Game.ALLIANCE.BLUE : Game.ALLIANCE.RED;
        tall_bottom.alliance = ran == 1 ? Game.ALLIANCE.BLUE : Game.ALLIANCE.RED;


        PowerDisplay.blueTimeAcc = 0;
        PowerDisplay.redTimeAcc = 0;
        PowerDisplay.blue_vault = 0;
        PowerDisplay.red_vault = 0;
        PowerDisplay.powerUp = PowerDisplay.LiteralPowerUp.NONE;
        PowerDisplay.powerUpForBlue = false;
        PowerDisplay.blue_powLevel = 0;
        PowerDisplay.red_powLevel = 0;
        PowerDisplay.powerUpLevel = 0;
        PowerDisplay.powerUpStart = 0;
        PowerDisplay.blue_forceClimbs = 0;
        PowerDisplay.red_forceClimbs = 0;
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
        for (Drawable d : new ArrayList<>(field)) {
            if (d instanceof Pixel) {
                for (Body b : (((Pixel) d).getBodies())) {
                    Main.getInstance().world.destroyBody(b);
                }
                field.remove(d);
            }
        }
        for (Entity c : generatePixels()) {
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
}
