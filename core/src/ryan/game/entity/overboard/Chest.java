package ryan.game.entity.overboard;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.BodyDef;
import ryan.game.Utils;
import ryan.game.entity.Entity;

public class Chest extends Entity {

    public static final float SIDE = .45f;
    public static final float DENSITY = .1f;

    public Chest(float x, float y, Color c) {
        super(Entity.rectangleBody(x, y, SIDE, SIDE, DENSITY, BodyDef.BodyType.DynamicBody));
        initVisuals(SIDE, SIDE, Utils.colorImage("core/assets/chest_recolor.png", c));
    }
}