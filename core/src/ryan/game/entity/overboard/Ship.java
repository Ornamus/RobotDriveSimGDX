package ryan.game.entity.overboard;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;
import ryan.game.games.Game;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Ship extends Entity {

    public boolean blue;
    Body wall1, wall2;
    Body crowsNest, chestScore, topRamp, bottomRamp;

    public HashMap<Chest, Integer> scoredChests = new HashMap<>();

    public List<Robot> topRampRobots = new ArrayList<>(), bottomRampRobots = new ArrayList<>();

    public Ship(float x, float y, boolean blue) {
        super(new Body[]{});
        this.blue = blue;
        float flip = blue ? 1 : -1;
        PolygonShape s = new PolygonShape();
        Vector2[] vertices = new Vector2[] {
                new Vector2(0, 0),
                new Vector2(1.875f * flip, 2.75f),
                new Vector2(3.3f * flip, 3.6f),
                new Vector2(5.2f * flip, 3.9f),
        };
        s.set(vertices);
        wall1 = new BodyFactory(x, y).setShape(s).setTypeStatic().create();
        addBody(wall1);

        s = new PolygonShape();
        vertices = new Vector2[] {
                new Vector2(0, 0),
                new Vector2(1.875f * flip, -2.75f),
                new Vector2(3.3f * flip, -3.6f),
                new Vector2(5.2f * flip, -3.9f),
        };
        s.set(vertices);
        wall2 = new BodyFactory(x, y).setShape(s).setTypeStatic().create();
        addBody(wall2);

        s = new PolygonShape();
        vertices = new Vector2[] {
                new Vector2(0, 0),
                new Vector2(5.2f * flip, 3.9f),
                new Vector2(5.2f * flip, -3.9f),
        };
        s.set(vertices);
        chestScore = new BodyFactory(x, y).setShape(s).setTypeStatic().create();
        addBody(chestScore);

        crowsNest = new BodyFactory(x+(7.35f*flip),y).setShapeCircle(.9f).setTypeStatic().create();
        addBody(crowsNest);

        topRamp = new BodyFactory(x+(6.175f*flip), y+2.3f).setShapeRectangle(1.05f, .25f).setTypeStatic().create();
        addBody(topRamp);

        bottomRamp = new BodyFactory(x+(6.175f*flip), y-2.3f).setShapeRectangle(1.05f, .25f).setTypeStatic().create();
        addBody(bottomRamp);

        setPrimary(wall1);
    }

    @Override
    public void collideStart(Entity e, Body self, Body other, Contact contact) {
        if (self == topRamp || self == bottomRamp || self == chestScore || self == crowsNest) {
            contact.setEnabled(false);
            if (self == chestScore && e instanceof Chest && scoredChests.get(e) == null) {
                Chest c = (Chest) e;
                if ((blue && c.alliance != Game.ALLIANCE.RED) || (!blue && c.alliance != Game.ALLIANCE.BLUE))
                    scoredChests.put(c, Game.getMatchTime());
            }
            Robot r = null;
            if (e instanceof Robot) r = (Robot) e;
            if (r != null && r.blue == blue && !r.isPart("intake", other)) {
                if (self == bottomRamp) {
                    if (!bottomRampRobots.contains(r)) bottomRampRobots.add(r);
                } else if (self == topRamp) {
                    if (!topRampRobots.contains(r)) topRampRobots.add(r);
                }
            }
        }
    }

    @Override
    public void collideEnd(Entity e, Body self, Body other, Contact contact) {
        if (self == chestScore && scoredChests.get(e) != null) {
            scoredChests.remove(e);
        }
        Robot r = null;
        if (e instanceof Robot) r = (Robot) e;
        if (r != null && r.blue == blue && !r.isPart("intake", other)) {
            if (self == bottomRamp) {
                bottomRampRobots.remove(r);
            } else if (self == topRamp) {
                topRampRobots.remove(r);
            }
        }
    }
}
