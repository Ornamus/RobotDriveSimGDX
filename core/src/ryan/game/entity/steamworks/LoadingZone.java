package ryan.game.entity.steamworks;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;

public class LoadingZone extends Entity {

    public boolean blue;

    public LoadingZone(float x, float y, float angle, boolean blue) {
        super(BodyFactory.getRectangleStatic(x, y, 8f, 2f, 0));
        setAngle(angle);
        this.blue = blue;
    }

    public void collideStart(Entity e, Body self, Body other, Contact contact) {
        contact.setEnabled(false);
    }
}
