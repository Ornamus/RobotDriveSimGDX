package ryan.game.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ryan.game.Constants;
import ryan.game.Main;
import ryan.game.Utils;

import javax.xml.soap.Text;
import java.util.ArrayList;
import java.util.List;

public class Entity {

    private String name = "entity";
    private float width = -1, height = -1;
    private float x = 0, y = 0;
    private float angle = 0;
    private List<Body> bodies = new ArrayList<Body>();
    private Body primary = null;
    private Sprite s = null;

    private static final Texture defaultTexture = new Texture("core/assets/default_block.png");
    private static final Texture fuelTex = new Texture("core/assets/fuel.png");

    protected Entity(Body... b) {
        for (Body body : b) {
            bodies.add(body);
        }
        primary = bodies.get(0);
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

    public void tick() {
        Vector2 pos = getPhysicsPosition();
        if (previousPos != null) {
            speed = new Vector2(pos.x, pos.y).sub(previousPos);
            speed.scl(1000f * Constants.TIME_STEP);
        }
        angle = getPhysicsAngle();
        x = pos.x;
        y = pos.y;
        if (s != null) {
            s.setBounds(pos.x - s.getWidth()/2, pos.y - s.getHeight()/2, width * 2, height * 2);
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

    public void onCollide(Entity e) {}

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getAngle() {
        return angle;
    }

    public void draw(SpriteBatch b) {
        if (s != null) s.draw(b);
    }

    public List<Body> getBodies() {
        return new ArrayList<Body>(bodies);
    }

    public static Entity circleEntity(float x, float y, float radius, float density, World w) {
        BodyDef rightDef = new BodyDef();
        rightDef.type = BodyDef.BodyType.DynamicBody;
        rightDef.position.set(x, y);

        Body right = w.createBody(rightDef);
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef rightFix = new FixtureDef();
        rightFix.shape = shape;
        rightFix.density = density;
        rightFix.restitution = 0f;

        Fixture fixture = right.createFixture(rightFix);
        shape.dispose();
        Entity e = new Entity(radius, radius, right);
        right.setUserData(e);
        return e;
    }

    public static Entity peg(float x, float y, float angle) {
        Entity e = Entity.rectangleEntity(x, y, .8f, .12f, Main.getInstance().world).setName("peg");
        e.setAngle(angle);
        e.setSprite(new Texture("core/assets/peg.png"));
        return e;
    }

    public static Entity rectangleEntity(float x, float y, float width, float height, World w) {
        Body body = rectangleDynamicBody(x, y, width, height, w);
        Entity e = new Entity(width, height, body);
        body.setUserData(e);
        return e;
    }

    protected static Body rectangleDynamicBody(float x, float y, float width, float height, World w) {
        return rectangleBody(x, y, width, height, BodyDef.BodyType.DynamicBody, w);
    }

    protected static Body rectangleStaticBody(float x, float y, float width, float height, World w) {
        return rectangleBody(x, y, width, height, BodyDef.BodyType.StaticBody, w);
    }

    protected static Body rectangleBody(float x, float y, float width, float height, BodyDef.BodyType type, World w) {
        BodyDef rightDef = new BodyDef();
        rightDef.type = type;
        rightDef.position.set(x, y);

        Body right = w.createBody(rightDef);
        PolygonShape rightShape = new PolygonShape();
        rightShape.setAsBox(width, height);

        FixtureDef rightFix = new FixtureDef();
        rightFix.shape = rightShape;
        rightFix.density = width * height;
        rightFix.restitution = 0f;

        Fixture fixture = right.createFixture(rightFix);
        rightShape.dispose();
        return right;
    }

    public static Entity barrier(float x, float y, float width, float height, World w) {
        PolygonShape rightShape = new PolygonShape();
        rightShape.setAsBox(width, height);
        Entity e = barrier(x, y, rightShape, w);
        //e.initVisuals(width, height);
        rightShape.dispose();
        return e;
    }

    public static Entity barrier(float x, float y, Shape s, World w) {
        BodyDef rightDef = new BodyDef();
        rightDef.type = BodyDef.BodyType.StaticBody;
        rightDef.position.set(x, y);

        Body right = w.createBody(rightDef);

        FixtureDef rightFix = new FixtureDef();
        rightFix.shape = s;
        rightFix.density = (float) Math.pow(s.getRadius(), 2);
        rightFix.restitution = 0f;

        Fixture fixture = right.createFixture(rightFix);
        Entity e = new Entity(right);
        right.setUserData(e);
        return e;
    }

    public static List<Entity> generateFuelStack(float x, float y, World w) {
        List<Entity> fuel = new ArrayList<Entity>();
        for (int i=0; i<100;i++) {
            fuel.add(generateFuelBall(x, y, w));
        }
        return fuel;
    }

    public static Entity generateFuelBall(float x, float y, World w) {
        Entity e = Entity.circleEntity(x, y, 0.2f, .4f, w);
        e.setSprite(fuelTex);
        e.setName("fuel");
        return e;
    }

    public Entity setAngle(float angle) {
        this.angle = angle;
        for (Body body : bodies) {
            body.setTransform(body.getPosition(), (float) Math.toRadians(angle));
        }
        return this;
    }
}
