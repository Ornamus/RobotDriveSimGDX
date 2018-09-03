package ryan.game.games;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ryan.game.Utils;
import ryan.game.competition.RobotStats;
import ryan.game.render.Fonts;
import java.util.function.BiConsumer;

public class RobotStatSlider {

    public static Texture plusTexture = new Texture("core/assets/ui/plus.png"), minusTexture = new Texture("core/assets/ui/minus.png");

    private int x, y;
    public int max, current = 0;
    public String label;
    public Sprite plus, minus;
    private BiConsumer<RobotStatSlider, RobotStats> affectStats;

    public RobotStatSlider(String label, int max, BiConsumer<RobotStatSlider, RobotStats> affectStats) {
        this(0,0, label, max, affectStats);
    }

    public RobotStatSlider(int x, int y, String label, int max, BiConsumer<RobotStatSlider, RobotStats> affectStats) {
        this.max = max;
        this.label = label;
        this.affectStats = affectStats;

        setPosition(x, y);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;

        plus = new Sprite(plusTexture);
        plus.setBounds(x-200, y, 40, 40);

        minus = new Sprite(minusTexture);
        minus.setBounds(x-150, y, 40, 40);
    }

    public float getProgress() {
        return (current*1f)/(max*1f);
    }

    public void draw(SpriteBatch b) {
        Utils.drawUnscaledProgressBar(x, y, 200, 40, getProgress(), b);
        Fonts.draw(Fonts.fmsWhiteSmall, label + " (" + current + "/" + max + ")", x+110, y + 28.5f, b);
        plus.draw(b);
        minus.draw(b);
    }

    public void affectStats(RobotStats s) {
        affectStats.accept(this, s);
    }
}