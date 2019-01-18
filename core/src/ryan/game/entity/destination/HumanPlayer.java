package ryan.game.entity.destination;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;

public class HumanPlayer extends Entity {

    public final boolean blue;


    public HumanPlayer(float x, float y, boolean blue) {
        this(x, y, blue, 90);
    }

    public HumanPlayer(float x, float y, boolean blue, float angle) {
        super(.2f, .12f,  BodyFactory.getRectangleStatic(x, y, .2f, .12f, 0));
        this.blue = blue;
        setAngle(angle);
        setName("human_player");
        setSprite(new Texture("core/assets/peg.png"));
    }

    @Override
    public void collideStart(Entity e, Body self, Body other, Contact contact) {
        contact.setEnabled(false);
    }
}
