package ryan.game;

import com.badlogic.gdx.physics.box2d.*;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;

public class CollisionListener implements ContactListener {

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Entity a = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity b = (Entity) contact.getFixtureB().getBody().getUserData();
        if (a != null || b != null) {
            if ((a != null && a.getName().equalsIgnoreCase("peg")) || (b != null && b.getName().equalsIgnoreCase("peg"))) {
                contact.setEnabled(false);
                if (a != null && a instanceof Robot) {
                    ((Robot) a).onPeg = true;
                } else if (b != null && b instanceof Robot) {
                    ((Robot) b).onPeg = true;
                }
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    @Override
    public void beginContact(Contact contact) {
        Entity a = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity b = (Entity) contact.getFixtureB().getBody().getUserData();

        if (a != null && b != null) {
            Main.collisions.add(new Collision(a, b));
        }
    }

    class Collision {
        Entity a, b;

        public Collision(Entity a, Entity b) {
            this.a = a;
            this.b = b;
        }
    }

    @Override
    public void endContact(Contact contact) {
        Entity a = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity b = (Entity) contact.getFixtureB().getBody().getUserData();
        if (a != null || b != null) {
            if ((a != null && a.getName().equalsIgnoreCase("peg")) || (b != null && b.getName().equalsIgnoreCase("peg"))) {
                contact.setEnabled(false);
                if (a != null && a instanceof Robot) {
                    ((Robot) a).onPeg = false;
                } else if (b != null && b instanceof Robot) {
                    ((Robot) b).onPeg = false;
                }
            }
        }
    }
}