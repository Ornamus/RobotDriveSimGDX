package ryan.game.entity.powerup;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;

public class NullTerritory extends Entity {

    public boolean blue = false;

    public NullTerritory(float x, float y) {
        super(BodyFactory.getRectangleStatic(x, y, 2.35f, 3f, 0));
    }

    public void collideStart(Entity e, Body self, Body other, Contact contact) {
        contact.setEnabled(false);
    }
}
