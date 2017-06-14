package ryan.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.*;
import ryan.game.Main;

public class Gear extends Entity {

    public static final Texture TEXTURE = new Texture(Gdx.files.internal("core/assets/gear.png"));
    static final float radius = .5f;
    static final float density = .25f;

    boolean loadingStation;
    long creation;

    private Gear(boolean loading, Body b) {
        super(radius, radius, b);
        loadingStation = loading;
        creation = System.currentTimeMillis();
    }

    @Override
    public void onCollide(Entity e, Body self, Body other) {
        if (loadingStation && e instanceof Robot && System.currentTimeMillis() - creation <= 250) {
            Robot r = (Robot) e;
            if (r.intake == other && !r.hasGear) {
                r.hasGear = true;
                Main.getInstance().removeEntity(this);
            }
        }
    }

    public static Gear create(float x, float y, float angle, boolean loadingStation) {
        World w = Main.getInstance().world;
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

        Gear g = (Gear) new Gear(loadingStation, right).setName("Gear").setAngle(angle);
        g.setSprite(TEXTURE);
        return g;
    }
}
