package ryan.game;

import com.badlogic.gdx.physics.box2d.*;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;

public class CollisionListener implements ContactListener {

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Entity a = getEntity(contact.getFixtureA());
        Entity b = getEntity(contact.getFixtureB());
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
            if (eA.getName().equalsIgnoreCase("peg") || eB.getName().equalsIgnoreCase("peg")) {
                //contact.setEnabled(false);
            }
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
        Entity a = getEntity(contact.getFixtureA());
        Entity b = getEntity(contact.getFixtureB());
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

    public Entity getEntity(Fixture f) {
        for (Entity e : Main.entities) {
            for (Body body : e.getBodies()) {
                for (Fixture fix : body.getFixtureList()) {
                    if (f == fix) return e;
                }
            }
        }
        return null;
    }
}