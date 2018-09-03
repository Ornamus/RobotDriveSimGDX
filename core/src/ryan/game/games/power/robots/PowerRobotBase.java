package ryan.game.games.power.robots;

import com.badlogic.gdx.physics.box2d.Body;
import ryan.game.autonomous.BlankAuto;
import ryan.game.autonomous.overboard.Auto254Over;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.competition.RobotStats;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Robot;
import ryan.game.entity.parts.Intake;
import ryan.game.games.Game;

public class PowerRobotBase extends RobotStats { //28 by 33

    public boolean pixelIntake = true;
    public boolean outtakeBack = false;
    public int maxPixelIntakeAtOnce = 1;
    public int maxPixels = 1;
    public float pixelIntakeStrength = 8.75f;
    public float pixelIntakeTime = 600;

    public boolean arm = false;

    public boolean tallPixelScore = true;

    public boolean canClimb = true;
    public float climbTime = 2;

    public PowerRobotBase() {
        robotWidth = 0.7112f; //28 inches
        robotHeight = 0.8382f; //33 inches

        intakeWidth = robotWidth*.75f;
        needsStateGenerator = false;

        texture="core/assets/16.png";
    }

    @Override
    public Command getAutonomous(Robot r) {
        return new BlankAuto(r);
    }

    @Override
    public void addParts(float x, float y, Robot r) {
        float width = intakeWidth, height = robotHeight / 4;
        Body b = BodyFactory.getRectangleDynamic(x - (robotWidth/2), y + robotHeight * 1.25f, width, height, width*height);
        Intake in = new Intake(width*2, height*2, b);
        in.addTags("chest");
        r.addPart(in);
    }
}
