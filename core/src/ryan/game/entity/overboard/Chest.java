package ryan.game.entity.overboard;

import com.badlogic.gdx.graphics.Color;
import ryan.game.Utils;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;

public class Chest extends Entity {

    public static final float WIDTH = 6 * 0.0254f;
    public static final float HEIGHT = 12 * 0.0254f;
    public static final float DENSITY = .1f;

    public Chest(float x, float y, Color c) {
        super(BodyFactory.getRectangleDynamic(x, y, WIDTH, HEIGHT, DENSITY));
        initVisuals(WIDTH, HEIGHT, Utils.colorImage("core/assets/chest_recolor.png", c));
        //initVisuals(SIDE, SIDE, Utils.colorImage("core/assets/chest_recolor.png", c));
    }
}
