package ryan.game.entity.powerup;

import com.badlogic.gdx.graphics.Texture;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;

public class Pixel extends Entity {

    public static final float WIDTH = 13 * 0.0254f;
    public static final float HEIGHT = 13 * 0.0254f;
    public static final float DENSITY = .8f;

    public static final Texture TEXTURE = new Texture("core/assets/pixel.png");

    public Pixel(float x, float y) {
        super(BodyFactory.getRectangleDynamic(x, y, WIDTH, HEIGHT, DENSITY));
        friction = 5f;

        initVisuals(WIDTH, HEIGHT, TEXTURE);
        setAngle(0);
    }
}
