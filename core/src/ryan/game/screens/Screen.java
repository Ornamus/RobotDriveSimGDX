package ryan.game.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Screen {

    public abstract void init();
    public abstract void tick();
    public abstract void draw(SpriteBatch b);

}
