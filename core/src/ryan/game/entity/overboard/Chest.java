package ryan.game.entity.overboard;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;
import ryan.game.games.Game;

public class Chest extends Entity {

    public static final float WIDTH = 6 * 0.0254f;
    public static final float HEIGHT = 12 * 0.0254f;
    public static final float DENSITY = .5f;

    public static final Texture TEX_BLUE = Utils.colorImage("core/assets/chest_recolor.png", Main.BLUE);
    public static final Texture TEX_RED = Utils.colorImage("core/assets/chest_recolor.png", Main.RED);
    public static final Texture TEX_NEUTRAL = Utils.colorImage("core/assets/chest_recolor.png", Utils.toColor(96, 64, 32));

    public final Game.ALLIANCE alliance;
    private boolean heavy = false;

    public Chest(float x, float y, boolean heavy, Game.ALLIANCE a) {
        this(x, y, 0, heavy, a);
    }

    public Chest(float x, float y, float angle, boolean heavy, Game.ALLIANCE a) {
        super(BodyFactory.getRectangleDynamic(x, y, WIDTH, HEIGHT, DENSITY));
        this.heavy = heavy;
        if (heavy) friction = 5.5f;
        else friction = 3f;
        alliance = a;
        Texture t;
        if (a == Game.ALLIANCE.BLUE) t = TEX_BLUE;
        else if (a == Game.ALLIANCE.RED) t = TEX_RED;
        else t = TEX_NEUTRAL;
        initVisuals(WIDTH, HEIGHT, t);
        setAngle(angle);
    }

    public boolean isHeavy() {
        return heavy;
    }
}
