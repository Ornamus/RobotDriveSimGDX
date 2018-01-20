package ryan.game.games.power.robots;

import com.badlogic.gdx.physics.box2d.Body;
import ryan.game.autonomous.powerup.Bacon_Basic;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Robot;
import ryan.game.entity.parts.Intake;
import ryan.game.entity.parts.Part;

public class Bacon extends PowerRobotBase {

    public Bacon() {
        super();
        robotHeight = 0.7112f; //28 inches
        robotWidth = 0.8382f; //33 inches

        intakeWidth = robotWidth*.6f;

        maxAccel = (15.5448f) * (robotWidth / 0.9144f); //recalculate when width changes

        pixelIntakeTime = 300;

        maxMPSLow = 6.9f / 3.28084f;
        maxMPS = 23.7f / 3.28084f;

        arm = true;

        climbTime = 4;

        tallPixelScore = false;

        texture="core/assets/1902.png";
        recolorIndex = 1;
    }

    @Override
    public void addParts(float x, float y, Robot r) {
        super.addParts(x, y, r);

        r.getPart("intake").get(0).addTags("arm_front");

        float width = intakeWidth, height = robotHeight / 4;
        Body in = BodyFactory.getRectangleDynamic(x - (robotWidth / 2), y - robotHeight - height - .01f, width, height, 0.05f);
        Part p = new Intake(width * 2, height * 2, in);
        p.addTags("arm_back");
        r.addPart(p);
    }

    @Override
    public Command getAutonomous(Robot r) {
        return new Bacon_Basic(r);
    }
}