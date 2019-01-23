package ryan.game.entity.destination;

import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;

public class Hab extends Entity {

    public final boolean blue;
    public final int level;

    public Hab(float x, float y, int level, boolean blue) {
        super(BodyFactory.getRectangleStatic(x, y, 2f, level == 3 ? 2f : 1.7f, 0));
        this.level = level;
        this.blue = blue;
    }
}
