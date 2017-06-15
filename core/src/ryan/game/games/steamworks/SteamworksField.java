package ryan.game.games.steamworks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.entity.*;
import ryan.game.games.Field;
import ryan.game.render.Drawable;

import java.util.ArrayList;
import java.util.List;

public class SteamworksField extends Field {

    List<Sprite> blueRotors = new ArrayList<Sprite>();
    List<Sprite> redRotors = new ArrayList<Sprite>();

    @Override
    public List<Drawable> generateField() {
        List<Drawable> drawables = new ArrayList<>();
        World world = Main.getInstance().world;

        drawables.add(Hopper.create(-9.5f, 12.25f, true, world)); //left top hopper
        drawables.add(Hopper.create(8.35f, 12.25f, true, world)); //right top hopper


        drawables.add(Hopper.create(-16f, -13.45f, false, world)); //left bottom hopper
        drawables.add(Hopper.create(-0.6f, -13.45f, false, world)); //middle bottom hopper
        drawables.add(Hopper.create(16f - 1.15f, -13.45f, false, world)); //right bottom hopper


        drawables.add(Entity.barrier(0, 12, 28f, .5f, world)); //top wall
        drawables.add(Entity.barrier(0, -13.2f, 28f, .5f, world)); //bottom wall
        drawables.add(Entity.barrier(-25.5f, 0, .5f, 10f, world)); //left wall
        drawables.add(Entity.barrier(24.5f, 0, .5f, 10f, world)); //right wall

        drawables.add(LoadingStation.create(true, true, -24.8f, 11, 26)); //blue load left
        drawables.add(LoadingStation.create(true, false, -21.6f, 12.65f, 26)); //blue load right

        drawables.add(LoadingStation.create(false, true, 20.4f, 12.65f, -26)); //red load left
        drawables.add(LoadingStation.create(false, false, 23.6f, 11, -26)); //red load right

        drawables.add(Entity.barrier(-23, 12, 3f, 2f, world).setAngle(26)); //Blue load barrier
        drawables.add(Entity.barrier(22, 12, 3f, 2f, world).setAngle(-26)); //Red load barrier

        drawables.add(Entity.barrier(-24.8f, -12.9f, 2f, 2f, world).setAngle(46)); //Red boiler
        drawables.add(Entity.barrier(23.8f, -12.9f, 2f, 2f, world).setAngle(-46)); //Blue boiler

        PolygonShape s = new PolygonShape();

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

        drawables.add(Entity.barrier(8.9f, -2.75f, s, world)); //blue airship
        drawables.add(Entity.barrier(-17.65f, -2.75f, s, world)); //red airship

        drawables.add(Entity.peg(-18.7f, -.57f, 0));
        drawables.add(Entity.peg(-16.25f, 3.25f, 360-60));
        drawables.add(Entity.peg(-16f, -4.25f, 60));

        float xFix = 1.55f;

        drawables.add(Entity.peg(18.9f - xFix, -.57f, 0));
        drawables.add(Entity.peg(16.25f - xFix, 3.25f, 60));
        drawables.add(Entity.peg(16.25f - xFix, -4.25f, 360-60));

        float ropeFix = 1.25f;
        drawables.add(new Rope(11.6f - ropeFix, 3.25f, 300, true)); //blue top
        drawables.add(new Rope(18.35f - ropeFix, -.5f, 0, true)); //blue middle
        drawables.add(new Rope(11.6f - ropeFix, -4.45f, 60, true)); //blue bottom

        drawables.add(new Rope(-11.6f, 3.25f, 60, false)); //red top
        drawables.add(new Rope(-18.35f, -.5f, 0, false)); //red middle
        drawables.add(new Rope(-11.6f, -4.45f, 300, false)); //red bottom

        drawables.add(new Boiler(-24.6f, -12.25f, false)); //red boiler
        drawables.add(new Boiler(23.4f, -12.25f, true));


        float sideSpace = 1f;

        for (int i=0; i<2; i++) {

            Color color = i == 0 ? Main.RED : Main.BLUE;
            List<Sprite> rotors = i == 0 ? redRotors : blueRotors;
            float startX = i == 0 ? -18 : 8.5f;
            float startY = i == 0 ? -3.5f : .4f;

            for (int r=0; r<3; r++) {
                Sprite sprite = new Sprite(Utils.colorImage("core/assets/rotor.png", color));

                float x = r == 0 ? startX : (r == 1 ? startX + 7f : startX + 3.5f);
                float y = r == 0 ? startY : (r == 1 ? startY : startY + (6f * (i == 0 ? 1 : -1)));
                sprite.setBounds(x, y, 2f, 2f);
                sprite.setOrigin(.7f, 1f);
                //sprite.setOriginCenter();
                if (i == 1) sprite.setRotation(180);
                rotors.add(sprite);
            }
        }

        drawables.add(new SteamworksDisplay());

        return drawables;
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

    int blueSpinning, redSpinning;

    @Override
    public void tick() {
        blueSpinning = 0;
        if (Main.blueGears > 12) blueSpinning = 3;
        else if (Main.blueGears > 6) blueSpinning = 2;
        else if (Main.blueGears > 2) blueSpinning = 1;

        redSpinning = 0;
        if (Main.redGears > 12) redSpinning = 3;
        else if (Main.redGears > 6) redSpinning = 2;
        else if (Main.redGears > 2) redSpinning = 1;
    }

    @Override
    public void draw(SpriteBatch b) {
        int red = 0;
        for (Sprite s : redRotors) {
            if (Main.matchPlay) {
                if (red < redSpinning) s.setRotation(s.getRotation() + 4f);
            } else s.setRotation(0);
            s.draw(b);
            red++;
        }

        int blue = 0;
        for (Sprite s : blueRotors) {
            if (Main.matchPlay) {
                if (blue < blueSpinning) s.setRotation(s.getRotation() - 4f);
            } else s.setRotation(180);
            s.draw(b);
            blue++;
        }
    }
}
