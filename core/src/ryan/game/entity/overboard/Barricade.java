package ryan.game.entity.overboard;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class Barricade extends Entity {

    public static final float WIDTH = 0.125f/2;
    public static final float HEIGHT = 26;
    public static final float SPEED_REQUIREMENT = 1.75f;
    public static final float SLOWDOWN = 500;

    public List<Body> allowingEntry = new ArrayList<>();

    public Barricade(float x, float y) {
        super(BodyFactory.getRectangleStatic(x, y, WIDTH, HEIGHT, 0));
    }

    @Override
    public void collideStart(Entity e, Body self, Body other, Contact contact) {
        Vector2 speed = e.getSpeed();
        boolean allowingOtherPart = false;
        for (Body b : allowingEntry) {
            if (b.getUserData() == e) {
                allowingOtherPart = true;
                break;
            }
        }
        if (Math.abs(speed.x) >= SPEED_REQUIREMENT || allowingOtherPart) {
            contact.setEnabled(false);
            if (!allowingOtherPart) {
                synchronized (Main.WORLD_USE) {
                    for (Body b : e.getBodies()) {
                        b.setLinearVelocity(speed.x/3, speed.y);
                    }
                }
            }
            if (!allowingEntry.contains(other)) allowingEntry.add(other);
        }
    }

    @Override
    public void collideEnd(Entity e, Body self, Body other, Contact contact) {
        if (allowingEntry.contains(other)) {
            allowingEntry.remove(other);
        }
    }
}
