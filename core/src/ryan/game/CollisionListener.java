package ryan.game;

import com.badlogic.gdx.physics.box2d.*;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;
import ryan.game.entity.Rope;

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
        Body[] bothBodies = new Body[]{bA, bB};
        Entity a = (Entity) bA.getUserData();
        Entity b = (Entity) bB.getUserData();
        Entity[] both = new Entity[]{a, b};
        if (!colliding.contains(bA)) colliding.add(bA);
        if (!colliding.contains(bB)) colliding.add(bB);
        if (Main.drawablesRemove.contains(a) || Main.drawablesRemove.contains(b)) {
            contact.setEnabled(false);
            return;
        }

        if (a != null  || b != null) {
            for (int i = 0; i < both.length; i++) {
                int otherI = i == 0 ? 1 : 0;
                Entity e = both[i];
                Entity other = both[otherI];
                Body bE = bothBodies[i];
                Body bOther = bothBodies[otherI];
                if (e != null) {
                    if (e instanceof Rope) contact.setEnabled(false);
                    if (other != null) {
                        if (Math.abs(e.getAirDistance() - other.getAirDistance()) >= 1) {
                            contact.setEnabled(false);
                            break;
                        }
                        if (e instanceof Robot) {
                            Robot r = (Robot) e;
                            boolean intakeCollision = bE == r.intake;
                            if (other.getName().equalsIgnoreCase("peg")) {
                                if (intakeCollision) {
                                    r.peg = other;
                                    contact.setEnabled(false);
                                }
                            } else if (other.getName().equalsIgnoreCase("gear")) {
                                if (intakeCollision) {
                                    r.intakeableGear = other;
                                    contact.setEnabled(false);
                                }
                            } else if (other.getName().equalsIgnoreCase("fuel")) {
                                if (intakeCollision) {
                                    r.intakeableFuel = other;
                                    contact.setEnabled(false);
                                }
                            } else if (other instanceof Rope) {
                                if (r.onRope == null && r.blue == ((Rope)other).blue) r.onRope = System.currentTimeMillis();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        Body bA = contact.getFixtureA().getBody();
        Body bB = contact.getFixtureB().getBody();
        colliding.remove(bA);
        colliding.remove(bB);
    }

    @Override
    public void beginContact(Contact contact) {
        Entity a = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity b = (Entity) contact.getFixtureB().getBody().getUserData();

        if (a != null && b != null) {
            Main.collisions.add(new Collision(a, b, contact.getFixtureA().getBody(), contact.getFixtureB().getBody()));
        }
    }

    class Collision {
        Entity a, b;
        Body bA, bB;

        public Collision(Entity a, Entity b, Body bA, Body bB) {
            this.a = a;
            this.b = b;
            this.bA = bA;
            this.bB = bB;
        }
    }

    @Override
    public void endContact(Contact contact) {
        Body bA = contact.getFixtureA().getBody();
        Body bB = contact.getFixtureB().getBody();
        Body[] bothBodies = new Body[]{bA, bB};
        Entity a = (Entity) bA.getUserData();
        Entity b = (Entity) bB.getUserData();
        Entity[] both = new Entity[]{a, b};

        if ((a != null && Main.drawables.contains(a)) || (b != null && Main.drawables.contains(b))) {
            for (int i = 0; i < both.length; i++) {
                int otherI = i == 0 ? 1 : 0;
                Entity e = both[i];
                Entity other = both[otherI];
                Body bE = bothBodies[i];
                Body bOther = bothBodies[otherI];
                if (e != null) {
                    if (e instanceof Rope) contact.setEnabled(false);
                    if (other != null) {
                        if (e instanceof Robot) {
                            Robot r = (Robot) e;
                            boolean intakeCollision = bE == r.intake;
                            if (other.getName().equalsIgnoreCase("peg")) {
                                if (intakeCollision) {
                                    r.peg = null;
                                }
                            } else if (other.getName().equalsIgnoreCase("gear")) {
                                if (intakeCollision) {
                                    r.intakeableGear = null;
                                }
                            } else if (other.getName().equalsIgnoreCase("fuel")) {
                                if (intakeCollision) {
                                    r.intakeableFuel = null;
                                }
                            } else if (other instanceof Rope) {
                                r.onRope = null;
                            }
                        }
                    }
                }
            }
        }
    }
}