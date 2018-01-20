package ryan.game.entity.powerup;

import com.badlogic.gdx.controllers.PovDirection;
import org.omg.CORBA.MARSHAL;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.controls.Gamepad;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;
import ryan.game.entity.steamworks.Gear;

import java.util.HashMap;

public class HumanStation extends Entity {

    static float WIDTH = 1.7f;
    static float HEIGHT = 1;

    boolean blue;
    boolean left;

    int pixels;

    public HumanStation(float x, float y, float angle, boolean blue, boolean left) {
        super (new BodyFactory(x,y).setShapeRectangle(WIDTH, HEIGHT).setTypeStatic().create());
        setAngle(angle);
        this.blue = blue;
        this.left = left;
    }

    HashMap<Integer, Boolean> wasHeld = new HashMap<>();

    @Override
    public void tick() {
        super.tick();
        if (!Main.matchPlay) pixels=6;
        for (Robot r : Main.robots) {
            if (blue == r.blue) {
                Gamepad g = r.getControllers().get(Main.MANIPULATORS ? 1 : 0);
                if (g != null) {
                    boolean val;
                    val = left ? g.getDPad() == PovDirection.north : g.getDPad() == PovDirection.south;
                    wasHeld.putIfAbsent(g.id, false);
                    if (val && !wasHeld.get(g.id) && pixels > 0) {
                        wasHeld.put(g.id, val);
                        float distance = 1.2f; //1.85f
                        float xChange = distance * (float) Math.sin(Math.toRadians(getAngle()));
                        float yChange = -distance * (float) Math.cos(Math.toRadians(getAngle()));

                        Pixel p = new Pixel(getX() + xChange, getY() + yChange);
                        if (Main.matchPlay) {
                            pixels--;
                        }
                        Main.getInstance().spawnEntity(p);
                        synchronized (Main.WORLD_USE) {
                            p.getPrimary().applyForceToCenter(xChange * 50, yChange * 50, true);
                        }
                        break;
                    } else {
                        wasHeld.put(g.id, val);
                    }
                }
            }
        }
    }
}
