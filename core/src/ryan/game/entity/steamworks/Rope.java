package ryan.game.entity.steamworks;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.Main;
import ryan.game.entity.Entity;

public class Rope extends Entity {

    static final float side = .5f;
    public final boolean blue;

    public Rope(float x, float y, float angle, boolean blue) {
        super(Entity.rectangleStaticBody(x, y, side, side));
        setAngle(angle);
        this.blue = blue;
    }

    @Override
    public void collideStart(Entity e, Body self, Body other, Contact contact) {
        contact.setEnabled(false);
    }
}