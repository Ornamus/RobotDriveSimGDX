package ryan.game.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ryan.game.Constants;
import ryan.game.Utils;
import ryan.game.render.Drawable;

import java.util.ArrayList;
import java.util.List;

public class Entity extends Drawable {

    private String name = "entity";
    public float width = -1, height = -1;
    private float angle = 0;
    private float airDistance = 1;
    private float airMomentum = 0;
    public float friction = 8f;
    private List<Body> bodies = new ArrayList<>();
    private Body primary = null;
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

        airDistance += airMomentum;
        if (airMomentum > -1f) airMomentum -=.1f;
        if (airDistance < 1) airDistance = 1;
        float sizeChanger = airDistance / 3f; //2,5
        if (sizeChanger < 1) sizeChanger = 1;
        if (s != null) {
            s.setBounds(pos.x - s.getWidth()/2, pos.y - s.getHeight()/2, width * 2 * sizeChanger, height * 2 * sizeChanger);
            // Set origin center for the sprite to guarantee proper rotation with physicsBody.
            s.setOriginCenter();
            s.setRotation(angle);
        }
        previousPos = new Vector2(pos.x, pos.y);
    }

    public Body getPrimary() {
        return primary;
    }

    public void setPrimary(Body b) {
        if (bodies.contains(b) || b == null) {
            primary = b;
        } else {
            Utils.log("Primary body is not even one of the bodies??");
        }
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

    public void collideStart(Entity e, Body self, Body other, Contact contact) {}
    public void onCollide(Entity e, Body self, Body other, Contact contact) {}
    public void collideEnd(Entity e, Body self, Body other, Contact contact) {}

    public float getAngle() {
        float smartAngle = getPhysicsAngle();
        while (smartAngle < 0) smartAngle = 360 + smartAngle;
        while (smartAngle >= 360) smartAngle -=360;
        return smartAngle;
    }

    @Override
    public void draw(SpriteBatch b) {
        if (s != null) {
            s.draw(b);
        }
    }

    public void drawUnscaled(SpriteBatch b) {}

    public List<Body> getBodies() {
        return new ArrayList<>(bodies);
    }

    public static Entity peg(float x, float y, float angle) {
        Entity e = new Entity(.8f, .12f, BodyFactory.getRectangleStatic(x, y, .8f, .12f, 0)).setName("peg");
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

    public Entity setAngle(float angle) {
        this.angle = angle;
        for (Body body : bodies) {
            body.setTransform(body.getPosition(), (float) Math.toRadians(angle));
        }
        return this;
    }

    public float getAirDistance() {
        return airDistance;
    }

    public float getAirMomentum() {
        return airMomentum;
    }

    public void setAirMomentum(float airMomentum) {
        this.airMomentum = airMomentum;
    }
}
