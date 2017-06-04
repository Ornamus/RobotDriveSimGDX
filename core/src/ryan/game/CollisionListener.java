package ryan.game;

import com.badlogic.gdx.physics.box2d.*;
import ryan.game.entity.Entity;

public class CollisionListener implements ContactListener {

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void beginContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        Entity eA = null;
        Entity eB = null;

        for (Entity e : Main.entities) {
            for (Body body : e.getBodies()) {
                for (Fixture f : body.getFixtureList()) {
                    if (f == a) {
                        eA = e;
                        break;
                    }
                    if (f == b) {
                        eB = e;
                        break;
                    }
                }
                if (eA != null && eB != null) break;
            }
        }
        if (eA != null && eB != null) {
            Main.collisions.add(new Collision(eA, eB));
        }
    }

    class Collision {
        Entity a, b;

        public Collision(Entity a, Entity b) {
            this.a = a;
            this.b = b;
        }
    }
}