package ryan.game;

import com.badlogic.gdx.physics.box2d.*;
import ryan.game.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class CollisionListener implements ContactListener {

    public List<Body> colliding = new ArrayList<>();

    public static CollisionListener instance;

    public CollisionListener() {
        instance = this;
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Body bA = contact.getFixtureA().getBody();
        Body bB = contact.getFixtureB().getBody();

        Entity a = (Entity) bA.getUserData();
        Entity b = (Entity) bB.getUserData();

        if (Main.drawablesRemove.contains(a) || Main.drawablesRemove.contains(b)) {
            contact.setEnabled(false);
            return;
        }

        if (a != null && b != null) {
            if (heightTouchCheck(a, b)) {
                contact.setEnabled(false);
                return;
            }
        }

        if (!colliding.contains(bA)) colliding.add(bA);
        if (!colliding.contains(bB)) colliding.add(bB);

        if (a != null && b != null) {
            a.collideStart(b, bA, bB, contact);
            b.collideStart(a, bB, bA, contact);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}

    @Override
    public void beginContact(Contact contact) {
        Body bA = contact.getFixtureA().getBody();
        Body bB = contact.getFixtureB().getBody();
        Entity a = (Entity) bA.getUserData();
        Entity b = (Entity) bB.getUserData();

        if (a != null && b != null) {
            if (heightTouchCheck(a, b)) {
                contact.setEnabled(false);
                return;
            }
        }

        if (a != null && b != null) {
            Main.collisions.add(new Collision(a, b, contact.getFixtureA().getBody(), contact.getFixtureB().getBody(), contact));
        }
    }

    @Override
    public void endContact(Contact contact) {
        Body bA = contact.getFixtureA().getBody();
        Body bB = contact.getFixtureB().getBody();
        Entity a = (Entity) bA.getUserData();
        Entity b = (Entity) bB.getUserData();
        colliding.remove(bA);
        colliding.remove(bB);
        if (a != null && b != null) {
            a.collideEnd(b, bA, bB, contact);
            b.collideEnd(a, bB, bA, contact);
        }
    }

    public boolean heightTouchCheck(Entity a, Entity b) {
        //return Math.abs(a.getAirDistance() - b.getAirDistance()) > 1;
        return (a.getAirDistance() > b.actual3DHeight || b.getAirDistance() > a.actual3DHeight);
    }

    class Collision {
        Entity a, b;
        Body bA, bB;
        Contact c;

        public Collision(Entity a, Entity b, Body bA, Body bB, Contact c) {
            this.a = a;
            this.b = b;
            this.bA = bA;
            this.bB = bB;
            this.c = c;
        }
    }
}