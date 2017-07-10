package ryan.game.entity.steamworks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.*;
import ryan.game.Main;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;
import ryan.game.games.steamworks.SteamworksMetadata;

public class Gear extends Entity {

    public static final Texture TEXTURE = new Texture(Gdx.files.internal("core/assets/gear.png"));
    static final float radius = .5f;
    static final float density = .25f;

    LoadingStation loadingStation = null;
    long creation;

    private Gear(LoadingStation loading, Body b) {
        super(radius, radius, b);
        loadingStation = loading;
        creation = System.currentTimeMillis();
    }

    @Override
    public void onCollide(Entity e, Body self, Body other, Contact contact) {
        if (loadingStation != null && e instanceof Robot && System.currentTimeMillis() - creation <= 150) {
            float diff = Math.abs(e.getAngle() - loadingStation.getAngle());
            if (diff <= 9.5) {

                Robot r = (Robot) e;
                SteamworksMetadata meta = (SteamworksMetadata) r.metadata;
                if (r.intake == other && !meta.hasGear) {
                    meta.hasGear = true;
                    Main.getInstance().removeEntity(this);
                }
            }
        }
    }

    public static Gear create(float x, float y, float angle) {
        return create(x, y, angle, null);
    }

    public static Gear create(float x, float y, float angle, LoadingStation loadingStation) {
        synchronized (Main.getInstance().world) {
            BodyDef rightDef = new BodyDef();
            rightDef.type = BodyDef.BodyType.DynamicBody;
            rightDef.position.set(x, y);

            Body right = Main.getInstance().world.createBody(rightDef);
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
}
