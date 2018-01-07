package ryan.game.entity.powerup;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;

public class NonCubeBarrier extends Entity {

    public NonCubeBarrier(float x, float y, float width, float height) {
        super(new BodyFactory(x,y).setShapeRectangle(width, height).setTypeStatic().create());
    }

    @Override
    public void collideStart(Entity e, Body self, Body other, Contact contact) {
        if (e instanceof Pixel && System.currentTimeMillis()-((Pixel)e).ejected <= 250) {
            contact.setEnabled(false);
        } else if (e instanceof Robot) {
            Robot r = (Robot) e;
            if (r.isPart("intake", other)) {
                contact.setEnabled(false);
            }
        }
    }
}
