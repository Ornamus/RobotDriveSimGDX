package ryan.game.games.destination;

import com.badlogic.gdx.physics.box2d.Body;
import ryan.game.autonomous.BlankAuto;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.competition.RobotStats;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Robot;
import ryan.game.entity.parts.Intake;

public class DestinationRobotDefense extends DestinationRobotStats {


    public DestinationRobotDefense() {
        super();
        hasIntake = false;
        texture = "core/assets/dozer_recolor.png";

    }

    @Override
    public Command getAutonomous(Robot r) {
        return new BlankAuto(r);
    }

    @Override
    public void addParts(float x, float y, Robot r) {}
}
