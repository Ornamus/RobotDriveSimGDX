package ryan.game.entity.steamworks;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.entity.Entity;

public class LoadingZone extends Entity {

    public boolean blue;

    public LoadingZone(float x, float y, float angle, boolean blue) {
        super(Entity.rectangleStaticBody(x, y, 8f, 2f));
        setAngle(angle);
        this.blue = blue;
    }

    public void collideStart(Entity e, Body self, Body other, Contact contact) {
        contact.setEnabled(false);
    }
}
