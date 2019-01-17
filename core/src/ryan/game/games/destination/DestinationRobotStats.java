package ryan.game.games.destination;

import com.badlogic.gdx.physics.box2d.Body;
import ryan.game.autonomous.BlankAuto;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.competition.RobotStats;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Robot;
import ryan.game.entity.parts.Intake;

public class DestinationRobotStats extends RobotStats {

    public boolean panelHPStation = true;
    public boolean panelIntake = true;
    public float panelIntakeRate = 650;
    public float panelIntakeStrength = 10f;

    public boolean cargoIntake = true;
    public float cargoIntakeRate = 650;
    public float cargoIntakeStrength = 10f;

    public boolean differentiateBetweenIntakes = false;

    public DestinationRobotStats() {
        maxMPS = 18 / 3.28084f;
        robotWidth = (29 * 1.2f) * 0.0254f;
        robotHeight = (31 * 1.2f) * 0.0254f;
        intakeWidth = robotWidth * .5f;
    }

    @Override
    public Command getAutonomous(Robot r) {
        return new BlankAuto(r);
    }

    @Override
    public void addParts(float x, float y, Robot r) {
        if (hasIntake) {
            float width = intakeWidth, height = robotHeight / 4;
            Body in = BodyFactory.getRectangleDynamic(x - (robotWidth / 2), y + robotHeight + height, width, height, width * height);
            r.addPart(new Intake(width * 2, height * 2, in));
        }
    }
}
