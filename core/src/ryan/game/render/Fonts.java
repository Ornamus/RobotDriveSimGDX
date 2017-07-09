package ryan.game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Fonts {

    private static GlyphLayout layout;

    public static BitmapFont monoWhiteLarge = null;
    public static BitmapFont monoWhiteSmall = null;
    public static BitmapFont fmsScore = null;
    public static BitmapFont fmsBlack = null;
    public static BitmapFont fmsWhiteSmall = null;

    private Fonts() {}

    public static void init() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("core/assets/fonts/DTM-Mono.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 26;
        param.borderWidth = 2f;
        param.borderColor = Color.BLACK;
        monoWhiteLarge = generator.generateFont(param);

        param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 15;
        param.color = Color.WHITE;
        param.borderWidth = .75f;
        param.borderColor = Color.BLACK;
        monoWhiteSmall = generator.generateFont(param);
        generator.dispose();

        generator = new FreeTypeFontGenerator(Gdx.files.internal("core/assets/fonts/Kozuka.otf"));
        param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 52;
        param.borderColor = Color.BLACK;
        param.color = Color.WHITE;
        param.borderWidth = 2f;
        fmsScore = generator.generateFont(param);

        param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 20;
        param.color = Color.BLACK;
        param.shadowColor = Color.BLACK;
        param.borderWidth = .5f;
        param.borderColor = Color.BLACK;
        fmsBlack = generator.generateFont(param);
        generator.dispose();


        generator = new FreeTypeFontGenerator(Gdx.files.internal("core/assets/fonts/Kozuka.otf"));
        param.size = 15;
        param.color = Color.WHITE;
        param.borderWidth = 1.5f;
        param.borderColor = Color.BLACK;
        fmsWhiteSmall = generator.generateFont(param);

        generator.dispose();

        layout = new GlyphLayout(monoWhiteLarge, "");
    }

    public static void drawCentered(String s, float x, float y, BitmapFont f, SpriteBatch b) {
        f.draw(b, s, x - (getWidth(s, f) / 2), y);
    }

    public static float getWidth(String s, BitmapFont f) {
        layout.setText(f, s);
        return layout.width;
    }
}
