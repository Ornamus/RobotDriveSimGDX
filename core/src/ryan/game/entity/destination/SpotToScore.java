package ryan.game.entity.destination;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.Main;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;
import ryan.game.render.Fonts;

public class SpotToScore extends Entity {

    private static final Texture CHECK = new Texture("core/assets/check.png");
    public final boolean blue;

    public int maxPanel = 1, numPanel = 0;
    public int maxCargo = 1, numCargo = 0;

    public SpotToScore[] panelRequirements = null;

    public SpotToScore(float x, float y, boolean blue) {
        this(x, y, blue, 90);
    }

    public SpotToScore(float x, float y, boolean blue, float angle) {
        super(.2f, .12f,  BodyFactory.getRectangleStatic(x, y, .2f, .12f, 0));
        this.blue = blue;
        setAngle(angle);
        setName("peg");
        setSprite(new Texture("core/assets/peg.png"));
    }

    public SpotToScore configScoring(int maxPanel, int maxCargo) {
        this.maxPanel = maxPanel;
        this.maxCargo = maxCargo;
        return this;
    }

    public SpotToScore setPanelRequirements(SpotToScore... s) {
        panelRequirements = s;
        return this;
    }

    @Override
    public void draw(SpriteBatch b) {
        super.draw(b);
        if (numPanel > 0) b.draw(Panel.TEXTURE, getX() - 0.5f, getY() - 0.5f, 1, 1);
        //if (numCargo > 0) b.draw(Cargo.TEXTURE, getX() - 0.4f, getY() - 0.4f, .8f, .8f);
        if (numPanel > 0 || (numCargo == maxCargo && maxCargo > 0)) b.draw(CHECK, getX() - 0.4f, getY() - 0.4f, 0.8f, 0.8f);
    }

    @Override
    public void drawUnscaled(SpriteBatch b) {
        boolean dependentAndReady = false;
        if (panelRequirements != null) {
            for (SpotToScore s : panelRequirements) {
                if (s.numPanel > 0) {
                    dependentAndReady = true;
                    break;
                }
            }
        }
        if ((numPanel > 0 && maxCargo > 0) || dependentAndReady) {
            if (numCargo == maxCargo) Fonts.monoWhiteLarge.setColor(Color.YELLOW);
            Fonts.drawCentered(Fonts.monoWhiteLarge, "" + numCargo, getX() * Main.mtpW, (getY() * Main.mtpH) + 150, b);
            if (numCargo == maxCargo) Fonts.monoWhiteLarge.setColor(Color.WHITE);
        }
        if (maxPanel > 1 && numPanel > 0) {
            if (numPanel == maxPanel) Fonts.monoWhiteLarge.setColor(Color.YELLOW);
            Fonts.drawCentered(Fonts.monoWhiteLarge, "" + numPanel, getX() * Main.mtpW, (getY() * Main.mtpH) + 150, b);
            if (numPanel == maxPanel) Fonts.monoWhiteLarge.setColor(Color.WHITE);
        }
    }

    @Override
    public void collideStart(Entity e, Body self, Body other, Contact contact) {
        contact.setEnabled(false);
    }
}
