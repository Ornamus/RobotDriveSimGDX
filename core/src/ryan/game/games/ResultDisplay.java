package ryan.game.games;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import ryan.game.render.ImageDrawer;

public class ResultDisplay extends ImageDrawer {

    GlyphLayout layout;
    public BitmapFont whiteBig;
    public BitmapFont whiteNormal;
    public BitmapFont blackNormal;


    public ResultDisplay(float x, float y) {
        super(x, y, "core/assets/results_elims.png");
        sprite.setSize(1188, 631);
        sprite.setPosition(0-sprite.getWidth() /2, 0-sprite.getHeight()/2);
        setDrawScaled(false);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("core/assets/fonts/Kozuka.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 52;
        param.borderColor = Color.BLACK;
        param.color = Color.WHITE;
        param.borderWidth = 2f;

        whiteBig = generator.generateFont(param);

        param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 20;
        param.color = Color.BLACK;
        param.shadowColor = Color.BLACK;
        param.borderWidth = .5f;
        param.borderColor = Color.BLACK;

        blackNormal = generator.generateFont(param);

        param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 20;
        param.color = Color.WHITE;
        //param.shadowColor = Color.BLACK;
        param.borderWidth = 1f;
        param.borderColor = Color.BLACK;

        whiteNormal = generator.generateFont(param);

        generator.dispose();

        layout = new GlyphLayout(whiteBig, "");
    }

    @Override
    public void draw(SpriteBatch b) {
        super.draw(b);

        drawCentered("Einstein 4 of 15", getCenterX(), getCenterY() + 305, blackNormal, b);
        drawCentered("Scoring Powered by Magic", getCenterX(), getCenterY() + 252, blackNormal, b);

        whiteNormal.draw(b, "118-254-1902-1557", getCenterX() - 365, getCenterY() + 164);
        whiteNormal.draw(b, "987-3132-27-597", getCenterX() + 150, getCenterY() + 164);

        blackNormal.draw(b, "Auto Mobility\nPressure\nRotor\nReady for Takeoff\nBlue Penalty", getCenterX() - 430, getCenterY() - 0);

        blackNormal.draw(b, "15\n46\n180\n150\n50", (getCenterX() - 430) + 310,  getCenterY());
    }

    public void drawCentered(String s, float x, float y, BitmapFont f, SpriteBatch b) {
        f.draw(b, s, x - (getWidth(s, f) / 2), y);
    }

    public float getWidth(String s, BitmapFont f) {
        layout.setText(f, s);
        return layout.width;
    }
}
