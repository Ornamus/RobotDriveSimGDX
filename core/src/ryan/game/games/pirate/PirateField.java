package ryan.game.games.pirate;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;
import ryan.game.entity.overboard.Chest;
import ryan.game.games.Field;
import ryan.game.render.Drawable;
import ryan.game.render.ImageDrawer;

import java.util.ArrayList;
import java.util.List;

public class PirateField extends Field {
    @Override
    public List<Drawable> generateField() {
        List<Drawable> drawables = new ArrayList<>();

        float width = 54 * .915f;
        float height = 27 * .915f;
        drawables.add(new ImageDrawer(-width/2, -height/2 - .5f, width, height, "core/assets/overboard.png"));

        Entity ent = Entity.barrier(0, 12.25f, width, .5f);
        ent.initVisuals(width, .5f);
        drawables.add(ent);

        ent = Entity.barrier(0, -13.25f, width, .5f);
        ent.initVisuals(width, .5f);
        drawables.add(ent);

        ent = Entity.barrier(width / 2, 0, .5f, height);
        ent.initVisuals(.5f, height);
        drawables.add(ent);

        ent = Entity.barrier(-width / 2, 0, .5f, height);
        ent.initVisuals(.5f, height);
        drawables.add(ent);

        for (Entity e : generateChests()) {
            Main.getInstance().addFriction(e.getPrimary());
            drawables.add(e);
        }

        drawables.add(new PirateDisplay());

        return drawables;
    }

    public List<Entity> generateChests() {
        List<Entity> chests = new ArrayList<>();
        for (int bX=0; bX<5; bX++) {
            for (int bY=0; bY<3; bY++) {
                chests.add(new Chest(bX + 19, bY - 11, Main.BLUE));
            }
        }
        for (int bX=0; bX<5; bX++) {
            for (int bY=0; bY<3; bY++) {
                chests.add(new Chest(bX - 22.5f, bY + 8, Main.RED));
            }
        }
        for (int bX=0; bX<3; bX++) {
            for (int bY=0; bY<5; bY++) {
                chests.add(new Chest(bX, bY - 2, Utils.toColor(96, 64, 32)));
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
        field.addAll(generateChests());
    }

    @Override
    public void tick() {}

    @Override
    public void draw(SpriteBatch batch) {}
}
