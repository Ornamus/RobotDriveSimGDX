package ryan.game.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import ryan.game.Utils;
import ryan.game.render.Drawable;
import ryan.game.render.Fonts;

public class Button extends Drawable {

    int width, height;
    String text;
    Color color = Utils.toColor(0, 101, 179);
    Sprite background;
    Runnable onClick;
    float alpha = 1;

    public Button(int x, int y, int width, int height, String text, Runnable onClick) {
        super(x, y);
        this.width = width;
        this.height = height;
        this.text = text;
        this.onClick = onClick;

        setColor(color);
    }

    public void setColor(Color c) {
        color = c;

        background = new Sprite(Utils.colorImage("core/assets/white_square.png", color));
        background.setBounds(getX()-width/2, getY()-height*.75f, width, height);
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        background.setAlpha(alpha);
    }

    @Override
    public void draw(SpriteBatch batch) {
        background.draw(batch);
        Color old = Fonts.monoWhiteLarge.getColor();
        Fonts.monoWhiteLarge.setColor(old.r, old.g, old.b, alpha);
        Fonts.drawCentered(Fonts.monoWhiteLarge, text, getX(), getY(), batch);
        Fonts.monoWhiteLarge.setColor(old);
    }

    public void click(Vector3 pos, int button) {
        if (background.getBoundingRectangle().contains(pos.x, pos.y)) {
            onClick.run();
        }
    }
}