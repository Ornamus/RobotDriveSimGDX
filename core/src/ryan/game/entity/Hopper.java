package ryan.game.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import ryan.game.Main;
import ryan.game.Utils;

public class Hopper extends Entity {

    float x, y;
    boolean dumping = false;
    boolean dumped = false;
    boolean dropDown;

    private static final Texture full = new Texture("core/assets/hopper_full.png");
    private static final Texture empty = new Texture("core/assets/hopper_empty.png");

    protected Hopper(float x, float y, boolean dropDown, Body b) {
        super(.9f, .9f, b);
        this.x = x;
        this.y = y;
        this.dropDown = dropDown;
        setSprite(full);
    }

    public void reset() {
        fuelInHopper = 100;
        timeOfLastDump = 0;
        dumping = false;
        dumped = false;
        setSprite(full);
    }

    long timeOfLastDump = 0;
    int dumpRate = 30;
    int fuelInHopper = 100;

    @Override
    public void tick() {
        super.tick();
        if (dumping) {
            if (System.currentTimeMillis() - timeOfLastDump > dumpRate) {
                for (int i=0; 2>i; i++) {
                    Entity e = Entity.generateFuelBall(x + (i == 0 ? 2 : -2), y + (dropDown ? -1 : 1), Main.getInstance().world);
                    for (Body b : e.getBodies()) {
                        b.applyForceToCenter((Utils.randomInt(200, 1500) / 100f) * (Utils.randomInt(0, 1) == 0 ? -1 : 1), (Utils.randomInt(1000, 1500) / 100f) * (Utils.randomInt(0, 1) == 0 ? -1 : 1), true);
                    }
                    Main.getInstance().spawnEntity(.2f, e);
                    if (fuelInHopper > 0) {
                        fuelInHopper--;
                    } else {
                        break;
                    }
                }
                timeOfLastDump = System.currentTimeMillis();
                if (fuelInHopper <= 0) {
                    dumping = false;
                    dumped = true;
                    setSprite(empty);
                }
            }
        }
    }

    @Override
    public void onCollide(Entity e) {
        if (e instanceof Robot && !dumped) {
            dumping = true;
            timeOfLastDump = System.currentTimeMillis();
        }
    }

    public static Hopper create(float x, float y, boolean down, World w) {
        return new Hopper(x, y, down, Entity.rectangleStaticBody(x, y, .9f, .9f, w));
    }
}
