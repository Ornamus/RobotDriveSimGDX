package ryan.game.entity.destination;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import javafx.scene.layout.Pane;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;

public class SpotToScore extends Entity {

    private static final Texture CHECK = new Texture("core/assets/check.png");
    public final boolean blue;

    public boolean hasPanel = false, canPanel = true;
    public int maxCargo = 1, numCargo = 0;

    public SpotToScore(float x, float y, boolean blue) {
        this(x, y, blue, 90);
    }

    public SpotToScore(float x, float y, boolean blue, float angle) {
        super(.4f, .12f,  BodyFactory.getRectangleStatic(x, y, .8f, .12f, 0));
        this.blue = blue;
        setAngle(angle);
        setName("peg");
        setSprite(new Texture("core/assets/peg.png"));
    }

    public SpotToScore configScoring(boolean canPanel, int maxCargo) {
        this.canPanel = canPanel;
        this.maxCargo = maxCargo;
        return this;
    }

    @Override
    public void draw(SpriteBatch b) {
        super.draw(b);
        if (hasPanel) b.draw(Panel.TEXTURE, getX() - 0.5f, getY() - 0.5f, 1, 1);
        if (numCargo > 0) b.draw(Cargo.TEXTURE, getX() - 0.4f, getY() - 0.4f, .8f, .8f);
        if (hasPanel || (numCargo == maxCargo && maxCargo > 0)) b.draw(CHECK, getX() - 0.4f, getY() - 0.4f, 0.8f, 0.8f);
    }

    @Override
    public void collideStart(Entity e, Body self, Body other, Contact contact) {
        contact.setEnabled(false);
    }
}
