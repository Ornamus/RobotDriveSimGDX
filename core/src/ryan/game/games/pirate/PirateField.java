package ryan.game.games.pirate;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.entity.Entity;
import ryan.game.entity.overboard.Chest;
import ryan.game.games.Field;
import ryan.game.games.ScoreDisplay;
import ryan.game.render.Drawable;
import ryan.game.render.ImageDrawer;

import java.util.ArrayList;
import java.util.List;

public class PirateField extends Field {
    @Override
    public List<Drawable> generateField() {
        List<Drawable> drawables = new ArrayList<>();

        drawables.add(new ImageDrawer(-27, -14f, 54, 27, "core/assets/overboard.png")); //8.2296f

        drawables.addAll(generateChests());

        drawables.add(new ScoreDisplay("core/assets/score_display_overboard.png") {
            @Override
            public int[] calculateScores() {
                return new int[]{0, 0};
            }
        });

        return drawables;
    }

    public List<Drawable> generateChests() {
        List<Drawable> chests = new ArrayList<>();
        for (int bX=0; bX<5; bX++) {
            for (int bY=0; bY<3; bY++) {
                chests.add(new Chest(bX + 20, bY - 12, Main.BLUE));
            }
        }
        for (int bX=0; bX<5; bX++) {
            for (int bY=0; bY<3; bY++) {
                chests.add(new Chest(bX - 23.5f, bY + 9, Main.RED));
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

    }

    @Override
    public void matchStart() {

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
    public void tick() {

    }

    @Override
    public void draw(SpriteBatch batch) {

    }
}
