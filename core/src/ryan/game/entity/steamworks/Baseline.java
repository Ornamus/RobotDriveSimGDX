package ryan.game.entity.steamworks;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.Main;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;
import ryan.game.games.Game;
import ryan.game.games.ScoreDisplay;
import ryan.game.games.steamworks.SteamworksMetadata;

public class Baseline extends Entity {

    public final boolean blue;

    public Baseline(float x, float y, boolean blue) {
        super(Entity.rectangleStaticBody(x, y, .2f, 30));
        this.blue = blue;
    }

    @Override
    public void collideStart(Entity e, Body self, Body other, Contact c) {
        c.setEnabled(false);
        if (e instanceof Robot && ((Robot)e).blue == blue) {
            if (Game.isPlaying() && Game.getMatchTime() > 135) {
                ((SteamworksMetadata) ((Robot) e).metadata).crossedBaseline = true;
            }
        }
    }
}
