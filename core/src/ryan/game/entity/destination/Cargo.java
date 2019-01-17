package ryan.game.entity.destination;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.Main;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;
import ryan.game.games.steamworks.SteamworksMetadata;
import ryan.game.games.steamworks.robots.SteamRobotStats;
import ryan.game.screens.GameScreen;

public class Cargo extends Entity {

    public static final Texture TEXTURE = new Texture(Gdx.files.internal("core/assets/cargo.png"));
    static final float radius = (20) * 0.0254f;
    static final float density = .15f;

    boolean hopper;
    long creation;
    long timeOfShoot = 0;

    public Cargo(float x, float y) {
        super(radius, radius, new BodyFactory(x,y).setTypeDynamic().setDensity(density).setShapeCircle(radius).create());
        friction = 0.2f;
        setSprite(TEXTURE);
        setName("cargo");
        creation = GameScreen.getTime();
    }

    @Override
    public void collideStart(Entity e, Body self, Body other, Contact contact) {
        if (GameScreen.getTime() - timeOfShoot <= 100) {
            contact.setEnabled(false);
        }
    }

    @Override
    public void onCollide(Entity e, Body self, Body other, Contact contact) {
        if (hopper && e instanceof Robot && GameScreen.getTime() - creation <= 75) {
            Robot r = (Robot) e;
            SteamworksMetadata meta = (SteamworksMetadata) r.metadata;
            SteamRobotStats stats = (SteamRobotStats) r.stats;
            if (meta.fuel < stats.maxFuel) {
                meta.fuel++;
                Main.removeEntity(this);
            }
        }
    }
}
