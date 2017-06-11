package ryan.game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import ryan.game.Main;

public class ScoreDisplay extends Drawable {

    GlyphLayout layout;
    BitmapFont bigWhite;
    BitmapFont blackNormal;
    Sprite display;

    public ScoreDisplay() {
        display = new Sprite(new Texture("core/assets/score_display.png"));
        display.setBounds(0, 0, 1100, 145);
        display.setPosition(0 - (display.getWidth() / 2), -320);

        setDrawScaled(false);
        setX(display.getX());
        setY(display.getY());

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("core/assets/fonts/Kozuka.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 52;
        param.borderColor = Color.BLACK;
        param.color = Color.WHITE;
        param.borderWidth = 2f;
        param.shadowColor = Color.BLACK;
        param.shadowOffsetX = 1;
        param.shadowOffsetY = 1;
        bigWhite = generator.generateFont(param);

        param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 19;
        param.color = Color.BLACK;
        param.shadowColor = Color.BLACK;
        param.borderWidth = .25f;
        param.borderColor = Color.BLACK;
        blackNormal = generator.generateFont(param);
        generator.dispose();

        layout = new GlyphLayout(bigWhite, "");
    }

    @Override
    public void tick() {

    }

    @Override
    public void draw(SpriteBatch batch) {
        display.draw(batch);
        int blueScore = 0;
        int redScore = 0;
        if (Main.matchPlay) {
            blueScore += Main.getInstance().blueSpinning * 40;
            if (Main.getInstance().blueSpinning > 3)
        }
        drawCentered(blueScore + "", 65, getY()+61.5f, bigWhite, batch);
        drawCentered(redScore + "", -65, getY()+61.5f, bigWhite, batch);

        drawCentered("Quarterfinal 1 of 8", -205, getY() + 130f, blackNormal, batch);

        drawCentered("0", -257.5f, getY() + 35f, blackNormal, batch);
    }

    public void drawCentered(String s, float x, float y, BitmapFont f, SpriteBatch b) {
        f.draw(b, s, x - (getWidth(s, f) / 2), y);
    }

    private float getWidth(String s, BitmapFont f) {
        layout.setText(f, s);
        return layout.width;
    }
}
