package ryan.game.entity.steamworks;

import com.badlogic.gdx.physics.box2d.*;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.entity.Entity;
import ryan.game.games.Game;
import ryan.game.games.ScoreDisplay;
import ryan.game.games.steamworks.SteamworksField;

public class Boiler extends Entity {

    static float radius = .75f;

    public final boolean blue;

    public Boiler(float x, float y, boolean blue) {
        super(new Body[]{});
        this.blue = blue;
        BodyDef rightDef = new BodyDef();
        rightDef.type = BodyDef.BodyType.StaticBody;
        rightDef.position.set(x, y);

        Body right = Main.getInstance().world.createBody(rightDef);
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef rightFix = new FixtureDef();
        rightFix.shape = shape;
        rightFix.density = 0;
        rightFix.restitution = 0f;

        Fixture fixture = right.createFixture(rightFix);
        shape.dispose();

        right.setUserData(this);
        addBody(right);
        setPrimary(right);
    }

    @Override
    public void onCollide(Entity e, Body self, Body other, Contact c) {
        if (e.getName().equalsIgnoreCase("fuel") && e.getAirDistance() <= 5.5) {
            Main.getInstance().removeEntity(e);
            if (Main.matchPlay) {
                if (Game.getMatchTime() > 135) {
                    if (blue) SteamworksField.blueFuelInAuto++;
                    else SteamworksField.redFuelInAuto++;
                } else {
                    if (blue) SteamworksField.blueFuel++;
                    else SteamworksField.redFuel++;
                }
            }
        }
    }
}
