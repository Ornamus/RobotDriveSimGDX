package ryan.game.entity;

import com.badlogic.gdx.physics.box2d.*;

public class Entity {

    public Body body;

    private Entity(Body b) {
        body = b;
    }

    public static Entity rectangleEntity(float x, float y, float width, float height, World w) {
        BodyDef rightDef = new BodyDef();
        rightDef.type = BodyDef.BodyType.DynamicBody;
        rightDef.position.set(x, y);

        Body right = w.createBody(rightDef);
        PolygonShape rightShape = new PolygonShape();
        rightShape.setAsBox(width, height);

        FixtureDef rightFix = new FixtureDef();
        rightFix.shape = rightShape;
        rightFix.density = width * height;
        rightFix.restitution = 0f; // Make it bounce a little bit

        Fixture fixture = right.createFixture(rightFix);
        rightShape.dispose();
        return new Entity(right);
    }

    public static Entity barrier(float x, float y, float width, float height, World w) {
        PolygonShape rightShape = new PolygonShape();
        rightShape.setAsBox(width, height);
        Entity e = barrier(x, y, rightShape, w);
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
        rightFix.restitution = 0f; // Make it bounce a little bit

        Fixture fixture = right.createFixture(rightFix);
        return new Entity(right);
    }

    public Entity setAngle(float angle) {
        body.setTransform(body.getPosition(), (float) Math.toRadians(angle));
        return this;
    }
}
