package ryan.game.games.pirate;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;
import ryan.game.entity.overboard.Chest;
import ryan.game.games.RobotMetadata;


public class PirateMetadata extends RobotMetadata {

    Entity intakeableChest = null;

    @Override
    public void tick(Robot r) {}

    @Override
    public void collideStart(Robot r, Entity e, Body self, Body other, Contact contact) {
        if (self == r.intake) {
            if (e instanceof Chest) {
                //contact.setEnabled(false);
                intakeableChest = e;
            }
        }
    }

    @Override
    public void onCollide(Robot r, Entity e, Body self, Body other, Contact contact) {}

    @Override
    public void collideEnd(Robot r, Entity e, Body self, Body other, Contact contact) {
        if (self == r.intake) {
            if (e == intakeableChest) {
                intakeableChest = null;
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch, Robot r) {}
}
