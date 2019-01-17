package ryan.game.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ryan.game.Constants;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.entity.parts.Part;
import ryan.game.render.Drawable;

import java.util.ArrayList;
import java.util.List;

public class Entity extends Drawable {

    private String name = "entity";
    public float width = -1, height = -1;
    public float actual3DHeight = 1;
    private float angle = 0;
    private float airDistance = 1;
    private boolean dontMoveAirDistance = false;
    private float airMomentum = 0;
    public float friction = 8f;
    private List<Body> bodies = new ArrayList<>();
    private Body primary = null;
    protected List<Part> parts = new ArrayList<>();
    private Sprite s = null;

    private static final Texture defaultTexture = new Texture("core/assets/default_block.png");

    protected Entity(Body... b) {
        super(-999, -999);
        if (b != null) {
            for (Body body : b) {
                addBody(body);
            }
        }
        if (!bodies.isEmpty()) primary = bodies.get(0);
    }

    protected Entity(float width, float height, Body... b) {
        this(b);
        initVisuals(width, height);
    }

    public void initVisuals(float width, float height) {
        initVisuals(width, height, defaultTexture);
    }

    public void initVisuals(float width, float height, Texture t) {
        this.width = width;
        this.height = height;
        setSprite(t);
    }

    public void addBody(Body b) {
        bodies.add(b);
        b.setUserData(this);
    }

    public void addPart(Part p) {
        for (Body b : p.bodies) {
            addBody(b);
        }
        parts.add(p);
    }

    public void removePart(Part p) {
        for (Body b : p.bodies) {
            bodies.remove(b);
        }
        parts.remove(p);
    }

    public List<Part> getPart(String partTag) {
        List<Part> matches = new ArrayList<>();
        for (Part p : parts) {
            for (String s : p.tags) {
                if (s.equalsIgnoreCase(partTag)) {
                    matches.add(p);
                }
            }
        }
        return matches;
    }

    public Part getPart(Body b) {
        for (Part p : parts) {
            if (p.belongsTo(b)) {
                return p;
            }
        }
        return null;
    }

    public List<Part> getParts() {
        return new ArrayList<>(parts);
    }

    public boolean isPart(String partTag, Body b) {
        for (Part p : parts) {
            if (p.belongsTo(b)) {
                for (String s : p.tags) {
                    if (s.equalsIgnoreCase(partTag)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void setSprite(Texture t) {
        s = new Sprite(t);
        s.setPosition(-999, -999);
    }

    public String getName() {
        return name;
    }

    public Entity setName(String s) {
        name = s;
        return this;
    }

    Vector2 previousPos = null;
    protected Vector2 speed = new Vector2(0, 0);

    @Override
    public void tick() {
        super.tick();
        Vector2 pos = getPhysicsPosition();
        if (previousPos != null) {
            speed = new Vector2(pos.x, pos.y).sub(previousPos);
            speed.scl(1000f * Constants.TIME_STEP);
        }
        angle = getPhysicsAngle();
        setX(pos.x);
        setY(pos.y);

        if (!dontMoveAirDistance) {
            airDistance += airMomentum;
            if (airMomentum > -1f) airMomentum -= .1f;
        }
        if (airDistance < 1) airDistance = 1;
        previousPos = new Vector2(pos.x, pos.y);
    }

    public Body getPrimary() {
        return primary;
    }

    public void setPrimary(Body b) {
        if (!bodies.contains(b) && b != null) {
            bodies.add(b);
        }
        primary = b;
    }

    public Vector2 getPhysicsPosition() {
        if (primary != null) {
            return primary.getPosition();
        }
        Utils.log("Entity.getPhysicsPosition() called with a null primary Body!");
        return null;
    }

    public float getPhysicsAngle() {
        if (primary != null) {
            return (float)Math.toDegrees(primary.getAngle());
        }
        Utils.log("Entity.getPhysicsAngle() called with a null primary Body!");
        return 0;
    }

    public void collideStart(Entity e, Body self, Body other, Contact contact) {
        Part p = getPart(self);
        boolean usingOther = false;
        if (p == null) {
            p = getPart(other);
            usingOther = true;
        }
        boolean cancel = false;
        if (p != null) {
            if (bodies.contains(usingOther ? self : other)) {
                if (!p.collideWithSelf) {
                    contact.setEnabled(false);
                    cancel = true;
                }
            }
            if (p.hasTag("wheel")) {
                Utils.log("the hwheel has started touch. status: " + cancel);
            }
        }
    }
    public void onCollide(Entity e, Body self, Body other, Contact contact) {
        Part p = getPart(self);
        boolean usingOther = false;
        if (p == null) {
            p = getPart(other);
            usingOther = true;
        }
        boolean cancel = false;
        if (p != null) {
            if (bodies.contains(usingOther ? self : other)) {
                if (!p.collideWithSelf) {
                    contact.setEnabled(false);
                    cancel = true;
                }
            }
        }
    }
    public void collideEnd(Entity e, Body self, Body other, Contact contact) {}

    public float getAngle() {
        return Utils.fixAngle(getPhysicsAngle());
    }

    @Override
    public void draw(SpriteBatch b) {
        if (s != null) {
            Vector2 pos = getPhysicsPosition();

            float sizeChanger = airDistance / 3.2f;
            if (sizeChanger < 1) sizeChanger = 1;

            s.setBounds(pos.x - s.getWidth()/2, pos.y - s.getHeight()/2, width * 2 * sizeChanger, height * 2 * sizeChanger);
                // Set origin center for the sprite to guarantee proper rotation with physicsBody.
            s.setOriginCenter();
            s.setRotation(angle);
            s.draw(b);
        }
    }

    public void drawUnscaled(SpriteBatch b) {}

    public List<Body> getBodies() {
        return new ArrayList<>(bodies);
    }

    public List<Body> getFrictionlessBodies() {
        return new ArrayList<>();
    }

    public Entity setAngle(float angle) {
        this.angle = angle;
        synchronized (Main.WORLD_USE) {
            for (Body body : bodies) {
                body.setTransform(body.getPosition(), (float) Math.toRadians(angle));
            }
        }
        return this;
    }

    public float getAirDistance() {
        return airDistance;
    }

    public void setAirDistance(float f) {
        airDistance = f;
        dontMoveAirDistance = true;
    }

    public float getAirMomentum() {
        return airMomentum;
    }

    public void setAirMomentum(float airMomentum) {
        this.airMomentum = airMomentum;
    }

    public Vector2 getSpeed() {
        return speed;
    }

    //TODO: move out of this class
    public static Entity peg(float x, float y, float angle) {
        Entity e = new Entity(.8f, .12f, BodyFactory.getRectangleStatic(x, y, .8f, .12f, 0)).setName("peg");
        e.setAngle(angle);
        e.setSprite(new Texture("core/assets/peg.png"));
        return e;
    }

    //TODO: move out of this class
    public static Entity shortPeg(float x, float y, float angle) {
        Entity e = new Entity(.4f, .12f, BodyFactory.getRectangleStatic(x, y, .8f, .12f, 0)).setName("peg");
        e.setAngle(angle);
        e.setSprite(new Texture("core/assets/peg.png"));
        return e;
    }

    public static Entity barrier(float x, float y, float width, float height) {
        return new Entity(new BodyFactory(x,y).setShapeRectangle(width, height).setTypeStatic().create());
    }

    public static Entity barrier(float x, float y, Shape s) {
        return new Entity(new BodyFactory(x,y).setShape(s).setTypeStatic().create());
    }
}
