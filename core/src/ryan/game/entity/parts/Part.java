package ryan.game.entity.parts;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import ryan.game.Utils;
import ryan.game.render.Drawable;
import java.util.ArrayList;
import java.util.List;

public class Part extends Drawable {

    public List<Body> bodies = new ArrayList<>();
    public String name = "Unnamed Part";
    public Object metadata = null;

    public Part(String name, Body...b) {
        this.name = name;
        for (Body bod : b) {
            bodies.add(bod);
        }
        Utils.log("Part created with " + bodies.size() + " bodies");
    }

    public boolean belongsTo(Body b) {
        return bodies.contains(b);
    }

    @Override
    public void draw(SpriteBatch batch) {}
}
