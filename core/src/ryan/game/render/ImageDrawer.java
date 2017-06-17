package ryan.game.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ryan.game.image.Image;

public class ImageDrawer extends Drawable {

    Sprite sprite;

    public ImageDrawer(float x, float y, String tex) {
        sprite = new Sprite(new Texture(tex));
        sprite.setBounds(x, y, sprite.getWidth(), sprite.getHeight());
        sprite.setOriginCenter();
    }

    public ImageDrawer(float x, float y, float width, float height, String tex) {
        sprite = new Sprite(new Texture(tex));
        sprite.setBounds(x, y, width, height);
        sprite.setOriginCenter();
    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
