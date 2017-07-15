package ryan.game.entity;

import com.badlogic.gdx.physics.box2d.*;
import ryan.game.Main;
import ryan.game.Utils;

public class BodyFactory {

    float x, y;
    BodyDef.BodyType type = null;
    Shape shape = null;
    float density = 0;

    public BodyFactory(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static Body getRectangleDynamic(float x, float y, float width, float height, float density) {
        return getRectangle(x, y, width, height, density, BodyDef.BodyType.DynamicBody);
    }

    public static Body getRectangleStatic(float x, float y, float width, float height, float density) {
        return getRectangle(x, y, width, height, density, BodyDef.BodyType.StaticBody);
    }

    public static Body getRectangle(float x, float y, float width, float height, float density, BodyDef.BodyType type) {
        BodyFactory f = new BodyFactory(x, y).setType(type).setShapeRectangle(width, height).setDensity(density);
        return f.create();
    }

    public BodyFactory setTypeDynamic() {
        return setType(BodyDef.BodyType.DynamicBody);
    }

    public BodyFactory setTypeStatic() {
        return setType(BodyDef.BodyType.StaticBody);
    }

    public BodyFactory setType(BodyDef.BodyType type) {
        this.type = type;
        return this;
    }

    public BodyFactory setShapeRectangle(float width, float height) {
        PolygonShape newShape = new PolygonShape();
        newShape.setAsBox(width, height);
        return setShape(newShape);
    }

    public BodyFactory setShapeCircle(float radius) {
        CircleShape newShape = new CircleShape();
        newShape.setRadius(radius);
        return setShape(newShape);
    }

    public BodyFactory setShape(Shape s) {
        shape = s;
        return this;
    }

    public BodyFactory setDensity(float density) {
        this.density = density;
        return this;
    }

    public Body create() {
        if (type != null && shape != null) {
            BodyDef def = new BodyDef();
            def.type = type;
            def.position.set(x, y);

            Body body;
            synchronized (Main.WORLD_USE) {
                body = Main.getInstance().world.createBody(def);
            }

            FixtureDef fixDef = new FixtureDef();
            fixDef.shape = shape;
            fixDef.density = density;
            fixDef.restitution = 0f;

            Fixture f = body.createFixture(fixDef);
            shape.dispose();
            return body;
        } else {
            Utils.log("TYPE OR SHAPE IS NULL");
            //TODO: error message
            return null;
        }
    }
}
