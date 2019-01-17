package ryan.game.entity.destination;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.Main;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;
import ryan.game.entity.steamworks.LoadingStation;
import ryan.game.games.destination.DestinationMetadata;
import ryan.game.games.destination.DestinationRobotStats;
import ryan.game.games.steamworks.SteamworksMetadata;
import ryan.game.games.steamworks.robots.SteamRobotStats;
import ryan.game.screens.GameScreen;

public class Panel extends Entity {

    public static final Texture TEXTURE = new Texture(Gdx.files.internal("core/assets/panel.png"));
    static final float radius = 30 * 0.0254f; //TODO: isn't this diameter?
    static final float density = .05f;

    LoadingStation loadingStation = null;
    long creation;

    public Panel(float x, float y, float angle) {
        this(x, y, angle, null);
    }

    public Panel(float x, float y, float angle, LoadingStation loading) {
        super(radius, radius, new BodyFactory(x,y).setTypeDynamic().setDensity(density).setShapeCircle(radius).create());
        setAngle(angle);
        setName("panel");
        setSprite(TEXTURE);
        loadingStation = loading;
        creation = GameScreen.getTime();
    }

    @Override
    public void onCollide(Entity e, Body self, Body other, Contact contact) {
        if (loadingStation != null && e instanceof Robot && GameScreen.getTime() - creation <= 150) {
            float diff = Math.abs(e.getAngle() - loadingStation.getAngle());
            if (diff <= 9.5) {
                Robot r = (Robot) e;
                DestinationMetadata meta = (DestinationMetadata) r.metadata;
                DestinationRobotStats stats = (DestinationRobotStats) r.stats;
                if (r.isPart("intake", other) && !meta.hasPanel && stats.gearHPStation) {
                    meta.hasPanel = true;
                    Main.removeEntity(this);
                    //Main.getInstance().removeEntity(this);
                }
            }
        }
    }
}
