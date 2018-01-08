package ryan.game.entity.powerup;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;
import ryan.game.games.Game;
import ryan.game.games.power.PowerMetadata;

public class Baseline extends Entity {

    public final boolean blue;

    public Baseline(float x, float y, boolean blue) {
        super(BodyFactory.getRectangleStatic(x, y, .1f, 30, 0));
        this.blue = blue;
    }

    @Override
    public void collideStart(Entity e, Body self, Body other, Contact c) {
        c.setEnabled(false);
        if (e instanceof Robot && ((Robot)e).blue == blue) {
            if (Game.isPlaying() && Game.isAutonomous()) {
                ((PowerMetadata) ((Robot) e).metadata).crossedBaseline = true;
            }
        }
    }
}
