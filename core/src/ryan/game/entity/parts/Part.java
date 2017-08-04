package ryan.game.entity.parts;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import ryan.game.entity.Robot;
import ryan.game.render.Drawable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Part extends Drawable {

    public List<Body> bodies = new ArrayList<>();
    public List<String> tags = new ArrayList<>();
    public Object metadata = null;
    public boolean collideWithSelf = false;

    public Part(String tag, Body...b) {
        tags.add(tag);
        Collections.addAll(bodies, b);
    }

    public void addTags(String... newTags) {
        Collections.addAll(tags, newTags);
    }

    public boolean hasTag(String tag) {
        for (String s : tags) {
            if (s.equalsIgnoreCase(tag)) {
                return true;
            }
        }
        return false;
    }

    public void onRobotColorChange(Color c) {}

    public Robot getRobot() {
        return (Robot) bodies.get(0).getUserData();
    }

    public boolean belongsTo(Body b) {
        return bodies.contains(b);
    }

    @Override
    public void draw(SpriteBatch batch) {}
}
