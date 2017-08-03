package ryan.game.entity.parts;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import ryan.game.Main;
import ryan.game.Utils;

public class Intake extends Part {

    Sprite s;
    Body body;
    float width, height;

    public Intake(float width, float height, Body b) {
        this("Intake", width, height, b);
        onRobotColorChange(Main.BLUE);
    }

    public Intake(String name, float width, float height, Body b) {
        super(name, b);
        this.width = width;
        this.height = height;
        body = b;
    }

    @Override
    public void onRobotColorChange(Color c) {
        s = new Sprite(Utils.colorImage("core/assets/robot_intake.png", c));
    }

    @Override
    public void draw(SpriteBatch batch) {
        Vector2 pos = body.getPosition();

        s.setBounds(pos.x - s.getWidth()/2, pos.y - s.getHeight()/2, width, height);
        s.setOriginCenter();
        s.setRotation((float)Math.toDegrees(body.getAngle()));
        s.draw(batch);
    }
}
