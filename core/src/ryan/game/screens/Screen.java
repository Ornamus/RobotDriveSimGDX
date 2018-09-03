package ryan.game.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public abstract class Screen {

    public abstract void init();
    public abstract void tick();
    public boolean click(Vector3 pos, int button) { return false; }
    public abstract void draw(SpriteBatch b);
    public abstract void drawUnscaled(SpriteBatch b);

}
