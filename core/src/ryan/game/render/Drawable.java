package ryan.game.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Drawable {

    private float x = 0, y = 0;

    boolean drawScaled = true;

    public Drawable(){}

    public Drawable(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void tick(){}

    public abstract void draw(SpriteBatch batch);

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isDrawScaled() {
        return drawScaled;
    }

    public void setDrawScaled(boolean drawScaled) {
        this.drawScaled = drawScaled;
    }
}
