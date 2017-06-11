package ryan.game;

import com.badlogic.gdx.physics.box2d.*;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;

public class CollisionListener implements ContactListener {

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Body bA = contact.getFixtureA().getBody();
        Body bB = contact.getFixtureB().getBody();
        Body[] bothBodies = new Body[]{bA, bB};
        Entity a = (Entity) bA.getUserData();
        Entity b = (Entity) bB.getUserData();
        Entity[] both = new Entity[]{a, b};

        if (a != null || b != null) {
            for (int i=0; i<both.length; i++) {
                int otherI = i == 0 ? 1 : 0;
                Entity e = both[i];
                Entity other = both[otherI];
                Body bE = bothBodies[i];
                Body bOther = bothBodies[otherI];
                if (e != null) {
                    if (other != null) {
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
                                    //r.intakeableFuel = other;
                                    contact.setEnabled(false);
                                }
                            }
                        }
                    }
                }
            }
        }

        /*
        if (a != null || b != null) {
            if ((a != null && a.getName().equalsIgnoreCase("peg")) || (b != null && b.getName().equalsIgnoreCase("peg"))) {
                contact.setEnabled(false);
                if (a != null && a instanceof Robot) {
                    ((Robot) a).onPeg = true;
                } else if (b != null && b instanceof Robot) {
                    ((Robot) b).onPeg = true;
                }
            } else {
                for (int i=0; i<both.length; i++) {
                    Entity e = both[i];
                    if (e != null && (e.getName().equalsIgnoreCase("gear")) || e.getName().equalsIgnoreCase("fuel")) {
                        Entity other = i == 0 ? both[1] : both[0];
                        if (other != null && other instanceof Robot) {
                            Body robotPart = bothBodies[i == 0 ? 1 : 0];
                            if (robotPart == ((Robot) other).intake) {
                                if (e.getName().equalsIgnoreCase("gear")) {
                                    ((Robot) other).intakeableGear = e;
                                } else {
                                    //TODO: intakeable fuel
                                }
                                contact.setEnabled(false);
                            }
                        }
                    }
                }
            }
        }*/
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
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
        if (a != null || b != null) {
            if ((a != null && a.getName().equalsIgnoreCase("peg")) || (b != null && b.getName().equalsIgnoreCase("peg"))) {
                contact.setEnabled(false);
                if (a != null && a instanceof Robot) {
                    ((Robot) a).peg = null;
                } else if (b != null && b instanceof Robot) {
                    ((Robot) b).peg = null;
                }
            } else {
                for (int i=0; i<both.length; i++) {
                    Entity e = both[i];
                    if (e != null && (e.getName().equalsIgnoreCase("gear")) || e.getName().equalsIgnoreCase("fuel")) {
                        Entity other = i == 0 ? both[1] : both[0];
                        if (other != null && other instanceof Robot) {
                            Body robotPart = bothBodies[i == 0 ? 1 : 0];
                            if (robotPart == ((Robot) other).intake) {
                                if (e.getName().equalsIgnoreCase("gear")) {
                                    ((Robot) other).intakeableGear = null;
                                } else {
                                    //TODO: fuel intakeable
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}