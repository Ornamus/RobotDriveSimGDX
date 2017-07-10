package ryan.game.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ImageDrawer extends Drawable {

    public Sprite sprite;

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
    public void tick() {
        setX(sprite.getX());
        setY(sprite.getY());
    }

    public float getCenterX() {
        return getX() + (sprite.getWidth() / 2);
    }

    public float getCenterY() {
        return getY() + (sprite.getHeight() / 2);
    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
