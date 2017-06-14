package ryan.game.games;

import ryan.game.render.Drawable;
import java.util.List;

public abstract class Field extends Drawable {

    public abstract List<Drawable> generateField();

    public abstract void resetField(List<Drawable> field);

    public abstract void tick();

}
