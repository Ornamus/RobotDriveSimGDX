package ryan.game.entity.powerup;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;

public class Pixel extends Entity {

    public static final float WIDTH = 13 * 0.0254f;
    public static final float HEIGHT = 13 * 0.0254f;
    public static final float DENSITY = .8f;

    public static final Texture TEXTURE = new Texture("core/assets/pixel.png");

    public long ejected = 0;
    public Robot owner = null;
    public boolean inTall = false;

    public Pixel(float x, float y) {
        this(x,y,0);
    }

    public Pixel(float x, float y, float angle) {
        super(BodyFactory.getRectangleDynamic(x, y, WIDTH, HEIGHT, DENSITY));
        friction = 5f;

        initVisuals(WIDTH, HEIGHT, TEXTURE);
        setAngle(angle);
    }

    @Override
    public void collideStart(Entity e, Body self, Body other, Contact contact) {
        if (inTall && !(e instanceof Switch || (e instanceof Pixel && ((Pixel)e).inTall))) {
            contact.setEnabled(false);
        }
    }
}
