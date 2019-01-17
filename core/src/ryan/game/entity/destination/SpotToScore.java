package ryan.game.entity.destination;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;

public class SpotToScore extends Entity {

    public final boolean blue;

    public boolean hasPanel = false, hasCargo = false, canPanel = true;
    public int maxCargo = 1, numCargo = 0;

    public SpotToScore(float x, float y, boolean blue) {
        this(x, y, blue, 90);
    }

    public SpotToScore(float x, float y, boolean blue, float angle) {
        super(.4f, .12f,  BodyFactory.getRectangleStatic(x, y, .8f, .12f, 0));
        this.blue = blue;
        setAngle(angle);
        setName("peg");
        setSprite(new Texture("core/assets/peg.png"));
    }

    @Override
    public void collideStart(Entity e, Body self, Body other, Contact contact) {
        contact.setEnabled(false);
    }
}
