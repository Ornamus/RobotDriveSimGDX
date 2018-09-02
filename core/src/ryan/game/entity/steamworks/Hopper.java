package ryan.game.entity.steamworks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;

public class Hopper extends Entity {

    float x, y;
    boolean dumping = false;
    boolean dumped = false;
    boolean dropDown;

    private static final Texture full = new Texture("core/assets/hopper_full.png");
    private static final Texture empty = new Texture("core/assets/hopper_empty.png");

    public Hopper(float x, float y, boolean dropDown) {
        super(.9f, .9f, BodyFactory.getRectangleStatic(x, y, .9f, .9f, 0));
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
    int dumpRate = 33;
    int fuelInHopper = 100;

    @Override
    public void tick() {
        super.tick();
        if (dumping) {
            if (Main.getTime() - timeOfLastDump > dumpRate) {
                for (int i=0; 2>i; i++) {
                    Entity e = new Fuel(x + (i == 0 ? 2 : -2), y + (dropDown ? -1 : 1), true);
                    synchronized (Main.WORLD_USE) {
                        e.getPrimary().applyForceToCenter((Utils.randomInt(500, 900) / 100f) * (Utils.randomInt(0, 1) == 0 ? -1 : 1), (Utils.randomInt(500, 900) / 100f) * (Utils.randomInt(0, 1) == 0 ? -1 : 1), true);
                    }
                    Main.spawnEntity(.2f, e);
                    if (fuelInHopper > 0) {
                        fuelInHopper--;
                    } else {
                        break;
                    }
                }
                timeOfLastDump = Main.getTime();
                if (fuelInHopper <= 0) {
                    dumping = false;
                    dumped = true;
                    setSprite(empty);
                }
            }
        }
    }

    @Override
    public void onCollide(Entity e, Body self, Body other, Contact contact) {
        if (e instanceof Robot && !dumped) {
            dumping = true;
            timeOfLastDump = Main.getTime();
        }
    }
}
