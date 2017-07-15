package ryan.game.games.pirate;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
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

        float width = 51 * .915f;
        float height = 26 * .915f;
        drawables.add(new ImageDrawer(-width/2, -height/2 - .5f, width, height, "core/assets/overboard.png"));


        Entity ent = Entity.barrier(0, 11.75f, width, .5f); //Top wall
        drawables.add(ent);

        ent = Entity.barrier(0, -12.75f, width, .5f); //Bottom wall
        drawables.add(ent);

        ent = Entity.barrier((width / 2)-3.25f, 0, .5f, height); //Right wall
        drawables.add(ent);

        ent = Entity.barrier((-width / 2)+2.9f, 0, .5f, height); //Left wall
        drawables.add(ent);

        PolygonShape s = new PolygonShape();
        Vector2[] vertices = new Vector2[] {
                new Vector2(0, 0),
                new Vector2(1.25f, 2),
                new Vector2(1.875f, 2.75f),
                new Vector2(3, 3.4f),
                new Vector2(4, 3.75f),
                new Vector2(5.2f, 3.9f),
                new Vector2(5.2f, -3.9f),
        };
        s.set(vertices);
        ent = Entity.barrier(12.3f, -0.45f, s);
        drawables.add(ent);

        s = new PolygonShape();
        vertices = new Vector2[] {
                new Vector2(0, 0),
                new Vector2(1.25f, -2),
                new Vector2(1.875f, -2.75f),
                new Vector2(3, -3.4f),
                new Vector2(4, -3.75f),
                new Vector2(5.2f, -3.9f),
                new Vector2(5.2f, 3.9f),
        };
        s.set(vertices);
        ent = Entity.barrier(12.3f, -0.45f, s);
        drawables.add(ent);

        for (Entity e : generateChests()) {
            Main.getInstance().addFriction(e.getPrimary(), 6f);
            drawables.add(e);
        }

        drawables.add(new PirateDisplay());

        return drawables;
    }

    public List<Entity> generateChests() {
        List<Entity> chests = new ArrayList<>();
        for (int bX=0; bX<4; bX++) {
            for (int bY=0; bY<3; bY++) {
                chests.add(new Chest(bX + 16, bY - 11, Main.BLUE));
            }
        }
        for (int bX=0; bX<4; bX++) {
            for (int bY=0; bY<3; bY++) {
                chests.add(new Chest(bX - 19.5f, bY + 8, Main.RED));
        }
        }
        for (int bX=0; bX<2; bX++) {
            for (int bY=0; bY<6; bY++) {
                chests.add(new Chest(bX - 0.7f, bY - 3.5f, Utils.toColor(96, 64, 32)));
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
