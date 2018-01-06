package ryan.game.entity.powerup;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.Main;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class DrivableWall extends Entity {

    public static final float WIDTH = 0.125f/2;
    public static final float HEIGHT = 26;
    public static final float SPEED_REQUIREMENT = 1.85f; //1.75f
    public static final float SLOWDOWN = 500;

    public boolean onX = true;

    public List<Body> allowingEntry = new ArrayList<>();

    public DrivableWall(float x, float y, float width, float height, boolean onX) {
        super(BodyFactory.getRectangleStatic(x, y, width, height, 0));
        this.onX = onX;
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
        float compSpeed = onX ? speed.x : speed.y;
        if (Math.abs(compSpeed) >= SPEED_REQUIREMENT || allowingOtherPart) {
            contact.setEnabled(false);
            if (!allowingOtherPart) {
                synchronized (Main.WORLD_USE) {
                    for (Body b : e.getBodies()) {
                        if (onX) {
                            b.setLinearVelocity(speed.x / 3, speed.y);
                        } else {
                            b.setLinearVelocity(speed.x, speed.y / 3);
                        }
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
