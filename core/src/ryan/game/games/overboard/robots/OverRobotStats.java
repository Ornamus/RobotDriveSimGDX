package ryan.game.games.overboard.robots;

import com.badlogic.gdx.physics.box2d.Body;
import ryan.game.autonomous.overboard.Auto254Over;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.competition.RobotStats;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Robot;
import ryan.game.entity.parts.Intake;
import ryan.game.games.Game;

public class OverRobotStats extends RobotStats {

    public boolean chestIntake = true;
    public int maxChestIntakeAtOnce = 1;
    public final int maxChests = 1;
    public float chestIntakeStrength = 5.75f;
    public float chestIntakeTime = 500;

    public boolean cannonballIntake = false;
    public int maxCannonballs = 2;

    public boolean detectWeightOnIntake = false;
    public float detectWeightIntakeTime = 750;

    public boolean detectWeightWithMechanism = false;
    public float  detectWeightMechanismTime = 750;

    public OverRobotStats() {
        robotWidth = 0.8128f;
        intakeWidth = robotWidth*.75f;
        needsStateGenerator = true; //TODO: remove this once this class is properly treated as a stats default

        texture="core/assets/robot_multicolor.png";
        //texture="core/assets/1114.png";
        //recolorIndex=2;
    }

    @Override
    public Command getAutonomous(Robot r) {
        return new Auto254Over(r);
    }

    @Override
    public void addParts(float x, float y, Robot r) {
        float width = intakeWidth, height = robotHeight / 4;
        Body b = BodyFactory.getRectangleDynamic(x - (robotWidth/2), y + robotHeight * 1.25f, width, height, width*height);
        Intake in = new Intake(width*2, height*2, b);
        in.addTags("chest");
        r.addPart(in);

        //TODO: fix this intake floating behind the robot
        width = robotWidth;
        height = robotHeight / 8;
        b = BodyFactory.getRectangleDynamic(x - (robotWidth/2), y - (robotHeight * 1.25f), width, height, width*height);
        in = new Intake(width*2, height*2, b);
        in.addTags("ball");
        r.addPart(in);
    }
}
