package ryan.game.games;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;

public abstract class RobotMetadata {

    public RobotMetadata(){}

    public abstract void tick(Robot r);

    public abstract void collideStart(Robot r, Entity e, Body self, Body other, Contact contact);
    public abstract void onCollide(Robot r, Entity e, Body self, Body other, Contact contact);
    public abstract void collideEnd(Robot r, Entity e, Body self, Body other, Contact contact);

    public abstract void draw(SpriteBatch batch, Robot r);

    public RobotMetadata getNewInstance() {
        try {
            return this.getClass().getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
