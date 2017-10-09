package ryan.game.entity.overboard;

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

public class Cannonball extends Entity {

    public static final Texture TEXTURE = new Texture(Gdx.files.internal("core/assets/cannonball.png"));
    static final float radius = .3f;
    static final float density = .2f;

    long creation;
    long timeOfShoot = 0;

    public Cannonball(float x, float y) {
        super(radius, radius, new BodyFactory(x,y).setTypeDynamic().setDensity(density).setShapeCircle(radius).create());
        setSprite(TEXTURE);
        setName("Cannonball");
        friction = 0.15f;
        creation = Main.getTime();
    }

    public void setShot() {
        timeOfShoot = Main.getTime();
    }

    @Override
    public void collideStart(Entity e, Body self, Body other, Contact contact) {
        /*if (Main.getTime() - timeOfShoot <= 100) {
            contact.setEnabled(false);
        }*/
    }

    @Override
    public void onCollide(Entity e, Body self, Body other, Contact contact) {
        /*if (hopper && e instanceof Robot && Main.getTime() - creation <= 75) {
            Robot r = (Robot) e;
            SteamworksMetadata meta = (SteamworksMetadata) r.metadata;
            SteamRobotStats stats = (SteamRobotStats) r.stats;
            if (meta.fuel < stats.maxFuel) {
                meta.fuel++;
                Main.getInstance().removeEntity(this);
            }
        }*/
    }
}
