package ryan.game.entity.steamworks;

import com.badlogic.gdx.physics.box2d.*;
import ryan.game.Main;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;
import ryan.game.games.Game;
import ryan.game.games.steamworks.SteamworksField;

import java.util.ArrayList;
import java.util.List;

public class Boiler extends Entity {

    static float radius = .75f;

    public final boolean blue;
    List<Long> queue = new ArrayList<>();
    int fuelToCount = 0;

    public Boiler(float x, float y, boolean blue) {
        super(new BodyFactory(x,y).setTypeStatic().setShapeCircle(radius).create());
        this.blue = blue;
    }

    long timeSinceLast = 0;

    @Override
    public void tick() {
        super.tick();

        for (Long l : new ArrayList<>(queue)) {
            if (System.currentTimeMillis() - l >= 1000) {
                queue.remove(l);
                fuelToCount++;
            }
        }

        if (fuelToCount > 0) {
            if (System.currentTimeMillis() - timeSinceLast >= 200) {
                fuelToCount--;
                if (Game.isAutonomous()) {
                    if (blue) SteamworksField.blueFuelInAuto++;
                    else SteamworksField.redFuelInAuto++;
                } else {
                    if (blue) SteamworksField.blueFuel++;
                    else SteamworksField.redFuel++;
                }
                timeSinceLast = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void onCollide(Entity e, Body self, Body other, Contact c) {
        if (e.getName().equalsIgnoreCase("fuel") && e.getAirDistance() <= 5.5) {
            Main.getInstance().removeEntity(e);
            if (Main.matchPlay) {
                queue.add(System.currentTimeMillis());
            }
        }
    }
}
