package ryan.game.entity.overboard;

import com.badlogic.gdx.graphics.Color;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;
import ryan.game.games.Game;

public class Chest extends Entity {

    public static final float WIDTH = 6 * 0.0254f;
    public static final float HEIGHT = 12 * 0.0254f;
    public static final float DENSITY = .5f;

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
        Color c;
        if (a == Game.ALLIANCE.BLUE) c = Main.BLUE;
        else if (a == Game.ALLIANCE.RED) c = Main.RED;
        else c = Utils.toColor(96, 64, 32);
        initVisuals(WIDTH, HEIGHT, Utils.colorImage("core/assets/chest_recolor.png", c));
        setAngle(angle);
    }

    public boolean isHeavy() {
        return heavy;
    }
}
