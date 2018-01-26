package ryan.game.games.power.robots;

import com.badlogic.gdx.physics.box2d.Body;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Robot;
import ryan.game.entity.parts.Intake;

public class MikalRobotBase extends PowerRobotBase {

    public MikalRobotBase() {
        robotHeight = 30 / 39.3701f;
        robotWidth = 28 / 39.3701f;
    }

    @Override
    public void addParts(float x, float y, Robot r) {
        float width = robotWidth*.9f, height = robotHeight / 4;
        Body b = BodyFactory.getRectangleDynamic(x - (robotWidth/2), y + robotHeight * 1.25f, width, height, width*height);
        Intake in = new Intake(width*2, height*2, b);
        in.addTags("chest");
        r.addPart(in);
    }
}
