package ryan.game.entity.powerup;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.Main;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;
import ryan.game.games.Game;
import ryan.game.games.power.robots.PowerRobotBase;
import ryan.game.render.Fonts;
import ryan.game.screens.GameScreen;

import java.util.ArrayList;
import java.util.List;

public class Switch extends Entity {

    private static float WIDTH = 65 * 0.0254f;
    private static float HEIGHT = 45 * 0.0254f;

    public Game.ALLIANCE alliance = Game.ALLIANCE.NEUTRAL;
    public List<Entity> pixels = new ArrayList<>();
    public boolean tall = false;

    public Switch(float x, float y) {
        this(x, y, false);
    }

    public Switch(float x, float y, boolean tall) {
        super(BodyFactory.getRectangleStatic(x, y, WIDTH, HEIGHT, 0.2f));
        this.tall = tall;
    }

    @Override
    public void tick() {
        super.tick();
        if (!GameScreen.matchPlay) {
            alliance = Game.ALLIANCE.NEUTRAL;
        }
    }

    @Override
    public void drawUnscaled(SpriteBatch b) {
        super.drawUnscaled(b);
        if (alliance == Game.ALLIANCE.BLUE) {
            Fonts.monoWhiteSmall.setColor(Main.BLUE);
        } else if (alliance == Game.ALLIANCE.RED) {
            Fonts.monoWhiteSmall.setColor(Main.RED);
        }

        Vector2 pos = getPhysicsPosition();
        Fonts.drawCentered(Fonts.monoWhiteSmall, pixels.size() + "", pos.x* Main.mtpW, (pos.y*Main.mtpH)+40, 0, 53, b);

        Fonts.monoWhiteSmall.setColor(255,255,255,1);
    }

    @Override
    public void collideStart(Entity e, Body self, Body other, Contact contact) {
        if (e instanceof Pixel) {
            Pixel p = (Pixel) e;
            if (!pixels.contains(e) && (!tall || (tall && System.currentTimeMillis()-((Pixel)e).ejected <= 250 && ((PowerRobotBase)p.owner.stats).tallPixelScore))) {
                pixels.add(e);
                if (tall) {
                    ((Pixel)e).inTall = true;
                }
            }
            contact.setEnabled(false);
        } else if (e instanceof Robot) {
            if (tall) {
                contact.setEnabled(false);
            } else {
                Robot r = (Robot) e;
                if (r.isPart("intake", other)) {
                    contact.setEnabled(false);
                }
            }
        }
    }

    @Override
    public void collideEnd(Entity e, Body self, Body other, Contact contact) {
        if (e instanceof Pixel && pixels.contains(e)) {
            pixels.remove(e);
            if (tall) {
                ((Pixel)e).inTall = false;
            }
        }
    }
}
