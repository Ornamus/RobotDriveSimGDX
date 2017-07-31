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
    public static BitmapFont fmsBlackSmall = null;
    public static BitmapFont fmsWhiteVerySmall = null;
    public static BitmapFont fmsWhiteSmall = null;
    public static BitmapFont fmsWhiteNormal = null;

    private Fonts() {}

    public static void init() {
        init(1);
    }

    public static void init(float scale) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("core/assets/fonts/DTM-Mono.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = Math.round(26f*scale);
        param.borderWidth = 2f*scale;
        param.borderColor = Color.BLACK;
        monoWhiteLarge = generator.generateFont(param);

        param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = Math.round(15f*scale);
        param.color = Color.WHITE;
        param.borderWidth = .75f*scale;
        param.borderColor = Color.BLACK;
        monoWhiteSmall = generator.generateFont(param);
        generator.dispose();

        generator = new FreeTypeFontGenerator(Gdx.files.internal("core/assets/fonts/Kozuka.otf"));
        param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = Math.round(20f*scale);
        param.color = Color.BLACK;
        param.shadowColor = Color.BLACK;
        param.borderWidth = .5f*scale;
        param.borderColor = Color.BLACK;

        fmsBlack = generator.generateFont(param);

        param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = Math.round(15f*scale);
        param.color = Color.BLACK;
        param.shadowColor = Color.BLACK;
        param.borderWidth = .5f*scale;
        param.borderColor = Color.BLACK;

        fmsBlackSmall = generator.generateFont(param);

        generator.dispose();

        generator = new FreeTypeFontGenerator(Gdx.files.internal("core/assets/fonts/segoe-ui.ttf")); //generator = new FreeTypeFontGenerator(Gdx.files.internal("core/assets/fonts/Kozuka.otf"));
        param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = Math.round(52f*scale);
        param.borderColor = Color.BLACK;
        param.color = Color.WHITE;
        param.borderWidth = 2f*scale;

        fmsScore = generator.generateFont(param);

        param.size = Math.round(15f*scale);
        param.color = Color.WHITE;
        param.borderWidth = 1.5f*scale;
        param.borderColor = Color.BLACK;

        fmsWhiteVerySmall = generator.generateFont(param);

        param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = Math.round(30f*scale);
        param.color = Color.WHITE;
        param.borderWidth = 1f*scale;
        param.borderColor = Color.BLACK;

        fmsWhiteNormal = generator.generateFont(param);

        param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = Math.round(20f*scale);
        param.color = Color.WHITE;
        param.borderWidth = 1f*scale;
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
