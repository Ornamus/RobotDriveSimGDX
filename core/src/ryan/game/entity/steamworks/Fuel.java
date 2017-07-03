package ryan.game.entity.steamworks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.*;
import ryan.game.Main;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;
import ryan.game.games.steamworks.SteamworksMetadata;

public class Fuel extends Entity {

    public static final Texture TEXTURE = new Texture(Gdx.files.internal("core/assets/fuel.png"));
    public static final Texture TEXTURE_MAX = new Texture(Gdx.files.internal("core/assets/fuel_max.png"));
    static final float radius = .2f;
    static final float density = .2f;

    boolean loadingStation;
    long creation;

    private Fuel(boolean loading, Body b) {
        super(radius, radius, b);
        loadingStation = loading;
        creation = System.currentTimeMillis();
    }

    @Override
    public void onCollide(Entity e, Body self, Body other, Contact contact) {
        if (loadingStation && e instanceof Robot && System.currentTimeMillis() - creation <= 175) {
            Robot r = (Robot) e;
            SteamworksMetadata meta = (SteamworksMetadata) r.metadata;
            if (meta.fuel < 50) {
                meta.fuel++;
                Main.getInstance().removeEntity(this);
            }
        }
    }

    public static Fuel create(float x, float y, boolean loadingStation) {
        BodyDef rightDef = new BodyDef();
        rightDef.type = BodyDef.BodyType.DynamicBody;
        rightDef.position.set(x, y);

        Body right = null;
        synchronized (Main.getInstance().world) {
            right = Main.getInstance().world.createBody(rightDef);
        }
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef rightFix = new FixtureDef();
        rightFix.shape = shape;
        rightFix.density = density;
        rightFix.restitution = 0f;

        Fixture fixture = right.createFixture(rightFix);
        shape.dispose();

        Fuel f = (Fuel) new Fuel(loadingStation, right).setName("Fuel");
        f.setSprite(TEXTURE);
        return f;
    }
}
