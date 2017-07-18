package ryan.game.games.overboard;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;
import ryan.game.entity.overboard.Barricade;
import ryan.game.entity.overboard.Chest;
import ryan.game.entity.overboard.Ship;
import ryan.game.games.Field;
import ryan.game.games.Game;
import ryan.game.games.ScoreDisplay;
import ryan.game.games.steamworks.SteamworksMetadata;
import ryan.game.render.Drawable;
import ryan.game.render.ImageDrawer;
import java.util.ArrayList;
import java.util.List;

public class PirateField extends Field {

    public PirateDisplay display;
    public static Ship[] ships = new Ship[2];

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

        for (Chest c : generateChests()) {
            Main.getInstance().addFriction(c.getPrimary(), c.friction);
            drawables.add(c);
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

    @Override
    public void affectRobots() {
        for (Robot r : Main.robots) {
            r.metadata = new PirateMetadata();
        }
    }

    @Override
    public void updateMatchInfo() {

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

    @Override
    public void tick() {}

    @Override
    public void draw(SpriteBatch batch) {}
}
