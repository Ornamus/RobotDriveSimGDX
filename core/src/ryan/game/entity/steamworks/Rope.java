package ryan.game.entity.steamworks;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.Main;
import ryan.game.competition.Match;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;
import ryan.game.games.Game;
import ryan.game.render.Fonts;

public class Rope extends Entity {

    static final float side = .5f;

    public final boolean blue;
    public int id = -1;

    public boolean robotTouching = false;

    public Rope(float x, float y, float angle, boolean blue) {
        super(BodyFactory.getRectangleStatic(x, y, side, side, 0));
        setAngle(angle);
        this.blue = blue;
    }

    public Rope setId(int i) {
        id = i;
        return this;
    }

    @Override
    public void drawUnscaled(SpriteBatch b) {
        super.drawUnscaled(b);

        int num = id;
        Match m = Main.schedule.getCurrentMatch();
        if (m != null) {
            if (blue) num = m.blue.teams[id];
            else num = m.red.teams[id];
        }

        Fonts.fmsWhiteVerySmall.setColor(255, 255, 255, robotTouching || (Game.isPlaying() && Game.getMatchTime() > 30) ? .2f : 1);
        Fonts.drawCentered(num + "", getX() * Main.meterToPixelWidth, (getY() * Main.meterToPixelHeight) + (Main.meterToPixelHeight * 4f), Fonts.fmsWhiteVerySmall, b);
        Fonts.fmsWhiteVerySmall.setColor(255, 255, 255, 1);
    }

    @Override
    public void collideStart(Entity e, Body self, Body other, Contact contact) {
        contact.setEnabled(false);
    }
}
