package ryan.game.entity;

import ryan.game.Main;

public class Rope extends Entity {

    static final float side = .5f;
    public final boolean blue;

    public Rope(float x, float y, float angle, boolean blue) {
        super(Entity.rectangleStaticBody(x, y, side, side, Main.getInstance().world));
        setAngle(angle);
        this.blue = blue;
    }
}
