package ryan.game.games.destination;

import com.badlogic.gdx.physics.box2d.Body;
import ryan.game.autonomous.BlankAuto;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.competition.RobotStats;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Robot;
import ryan.game.entity.parts.Intake;
import ryan.game.entity.parts.Part;

public class DestinationRobotStats extends RobotStats {

    public boolean panelIntake = true;
    public boolean panelFloor = true;
    public float panelIntakeRate = 650;
    public float panelIntakeStrength = 10f;

    public boolean cargoIntake = true;
    public float cargoIntakeRate = 650;
    public float cargoIntakeStrength = 10f;

    public boolean differentiateBetweenIntakes = false;
    public boolean elevator = true;

    public int hab_level = 3;
    public float habLevel1Speed = 0.5f;
    public float habLevel2Speed = 2;
    public float habLevel3Speed = 4;

    public DestinationRobotStats() {
        robotWidth = (29 * 1.5f) * 0.0254f;
        robotHeight = (31 * 1.5f) * 0.0254f;
        intakeWidth = robotWidth * .5f;

        maxMPS = (25f) / 3.28084f;
        maxAccel = (18.5448f) * (robotWidth / 0.9144f);

    }

    @Override
    public Command getAutonomous(Robot r) {
        return new BlankAuto(r);
    }

    @Override
    public void addParts(float x, float y, Robot r) {
        if (hasIntake) {
            float width = intakeWidth, height = robotHeight / 8;
            Body in = BodyFactory.getRectangleDynamic(x - (robotWidth / 2), y + robotHeight + height, width, height, width * height);
            Part p = new Intake(width * 2, height * 2, in);
            p.tags.add("cargo_eject");
            p.tags.add("panel");
            if (!differentiateBetweenIntakes) p.tags.add("cargo");
            r.addPart(p);


            if (differentiateBetweenIntakes) {
                width = robotWidth * 0.75f;
                height = robotHeight / 8;
                in = BodyFactory.getRectangleDynamic(x - (robotWidth / 2), y - robotHeight - height - .01f, width, height, 0.05f);
                p = new Intake(width * 2, height * 2, in);
                p.tags.add("cargo");
                r.addPart(p);
            }
        }
    }
}
