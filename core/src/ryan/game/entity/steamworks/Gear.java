package ryan.game.entity.steamworks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.*;
import ryan.game.Main;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;
import ryan.game.games.steamworks.Steamworks;
import ryan.game.games.steamworks.SteamworksMetadata;
import ryan.game.games.steamworks.robots.SteamRobotStats;

public class Gear extends Entity {

    public static final Texture TEXTURE = new Texture(Gdx.files.internal("core/assets/gear.png"));
    static final float radius = .5f;
    static final float density = .25f;

    LoadingStation loadingStation = null;
    long creation;

    public Gear(float x, float y, float angle) {
        this(x, y, angle, null);
    }

    public Gear(float x, float y, float angle, LoadingStation loading) {
        super(radius, radius, new BodyFactory(x,y).setTypeDynamic().setDensity(density).setShapeCircle(radius).create());
        setAngle(angle);
        setName("Gear");
        setSprite(TEXTURE);
        loadingStation = loading;
        creation = Main.getTime();
    }

    @Override
    public void onCollide(Entity e, Body self, Body other, Contact contact) {
        if (loadingStation != null && e instanceof Robot && Main.getTime() - creation <= 150) {
            float diff = Math.abs(e.getAngle() - loadingStation.getAngle());
            if (diff <= 9.5) {
                Robot r = (Robot) e;
                SteamworksMetadata meta = (SteamworksMetadata) r.metadata;
                SteamRobotStats stats = (SteamRobotStats) r.stats;
                if (r.isPart("intake", other) && !meta.hasGear && stats.gearHPStation) {
                    meta.hasGear = true;
                    Main.getInstance().removeEntity(this);
                }
            }
        }
    }
}
