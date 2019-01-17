package ryan.game.games.destination;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import ryan.game.autonomous.steamworks.AutoBaseline;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.competition.RobotStats;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Robot;
import ryan.game.entity.parts.Intake;

public class DestinationRobotStats extends RobotStats {

    public boolean gearHPStation = true;
    public boolean panelIntake = true;
    public float panelIntakeRate = 650;
    public float panelIntakeStrength = 10f;

    public boolean cargoIntake = true;
    public float cargoIntakeRate = 250;
    public float cargoIntakeStrength = 1f;

    public boolean differentiateBetweenIntakes = false;

    public boolean shooter = true;
    public float timePerShoot = 166f;

    public DestinationRobotStats() {
        maxMPS = 18 / 3.28084f;
        robotWidth = (29 * 1.2f) * 0.0254f;
        robotHeight = (31 * 1.2f) * 0.0254f;
        intakeWidth = robotWidth * .5f;
    }

    @Override
    public Command getAutonomous(Robot r) {
        return new AutoBaseline(r);
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
