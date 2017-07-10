package ryan.game.entity.steamworks;

import ryan.game.Main;
import ryan.game.controls.Gamepad;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;

import java.util.HashMap;

public class LoadingStation extends Entity {

    private boolean blue;
    private boolean left;

    static final float width = 1f, height = 2f;

    public LoadingStation(float x, float y, float angle, boolean blue, boolean left) {
        super(BodyFactory.getRectangleStatic(x, y, width, height, 0));
        setAngle(angle);
        this.blue = blue;
        this.left = left;
    }

    HashMap<Integer, Boolean> wasHeld = new HashMap<>();

    @Override
    public void tick() {
        super.tick();
        for (Robot r : Main.robots) {
            if (blue == r.blue) {
                Gamepad g = r.getController();
                if (g != null) {
                    boolean val;
                    if (g.hasZAxis()) val = left ? g.isLeftTriggerPressed() : g.isRightTriggerPressed();//g.getButton(left ? 2 : 3).get();
                    else val = left ? g.getButton(98).get() : g.getButton(99).get();
                    wasHeld.putIfAbsent(g.id, false);
                    if (val && !wasHeld.get(g.id)) {

                        float distance = 1.85f;
                        float xChange = distance * (float) Math.sin(Math.toRadians(getAngle()));
                        float yChange = -distance * (float) Math.cos(Math.toRadians(getAngle()));

                        Gear gear = new Gear(getX() + xChange, getY() + yChange, 0, this);
                        Main.getInstance().spawnEntity(gear);
                        gear.getPrimary().applyForceToCenter(xChange * 50, yChange * 50, true);
                    }
                    wasHeld.put(g.id, val);
                }
            }
        }
    }
}
