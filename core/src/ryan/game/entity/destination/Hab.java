package ryan.game.entity.destination;

import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;

public class Hab extends Entity {

    public final boolean blue;
    public final int level;

    public Hab(float x, float y, int level, boolean blue) {
        super(BodyFactory.getRectangleStatic(x, y, level == 1 ? 1.5f : 2f, level == 3 ? 2f : (level == 2 ? 1.7f : 5.2f), 0));
        this.level = level;
        this.blue = blue;
    }
}
