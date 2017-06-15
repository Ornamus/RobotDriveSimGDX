package ryan.game.entity;

import com.badlogic.gdx.physics.box2d.Body;
import ryan.game.Main;
import ryan.game.controls.ControllerManager;
import ryan.game.controls.Gamepad;

import java.util.HashMap;

public class LoadingStation extends Entity {

    private boolean blue;
    private boolean left;

    static final float width = 1f, height = 2f;

    private LoadingStation(boolean blue, boolean left, Body b) {
        super(b); //3f, 2f
        this.blue = blue;
        this.left = left;
    }

    HashMap<Integer, Boolean> wasHeld = new HashMap<Integer, Boolean>();

    @Override
    public void tick() {
        super.tick();
        for (Robot r : Main.getInstance().robots) {
            if (blue == r.blue) {
                Gamepad g = ControllerManager.getGamepad(r.id);
                boolean val = g.getButton(left ? 2 : 3).get();
                if (wasHeld.get(g.id) == null) wasHeld.put(g.id, false);
                if (val && !wasHeld.get(g.id)) {

                    float distance = 1.85f;
                    float xChange = distance * (float) Math.sin(Math.toRadians(getAngle()));
                    float yChange = -distance * (float) Math.cos(Math.toRadians(getAngle()));

                    Gear gear = Gear.create(getX() + xChange, getY() + yChange, 0, true);
                    Main.getInstance().spawnEntity(gear);
                    gear.getPrimary().applyForceToCenter(xChange * 50, yChange * 50, true);
                }
                wasHeld.put(g.id, val);
            }
        }
    }

    public static LoadingStation create(boolean blue, boolean left, float x, float y, float angle) {
        Body b = Entity.rectangleStaticBody(x, y, width, height, Main.getInstance().world);
        LoadingStation l = new LoadingStation(blue, left, b);
        l.setAngle(angle);
        return l;
    }
}
