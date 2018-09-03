package ryan.game.entity.powerup;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.Main;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;
import ryan.game.games.power.PowerMetadata;
import ryan.game.games.power.robots.PowerRobotBase;
import ryan.game.screens.GameScreen;

public class ClimbingBar extends Entity {

    public static float WIDTH = .5f;
    public static float HEIGHT = .6f;

    public boolean blue;

    public ClimbingBar(float x, float y, boolean blue) {
        super(BodyFactory.getRectangleStatic(x, y, WIDTH, HEIGHT, 0.1f));
        this.blue = blue;
    }

    @Override
    public void collideStart(Entity e, Body self, Body other, Contact contact) {
        contact.setEnabled(false);
        if (e instanceof Robot) {
            PowerMetadata r = (PowerMetadata) ((Robot)e).metadata;
            PowerRobotBase stats = (PowerRobotBase) ((Robot)e).stats;
            if (((Robot)e).blue == blue && r.climb == null && stats.canClimb) {
                r.climb = GameScreen.getTime();
            }
        }
    }

    @Override
    public void collideEnd(Entity e, Body self, Body other, Contact contact) {
        if (e instanceof Robot) {
            PowerMetadata r = (PowerMetadata) ((Robot)e).metadata;
            if (((Robot)e).blue == blue) {
                r.climb = null;
            }
        }
    }
}
