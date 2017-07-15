package ryan.game.games.overboard.robots;

import ryan.game.bcnlib_pieces.Command;
import ryan.game.competition.RobotStats;
import ryan.game.entity.Robot;
import ryan.game.entity.overboard.Chest;
import ryan.game.games.Game;

public class OverRobotStats extends RobotStats {

    public boolean chestIntake = true;
    public int maxChestIntakeAtOnce = 1;
    public int maxChests = 2;
    public float chestIntakeStrength = 5.75f;
    public float chestIntakeTime = 500;

    public boolean cannonballIntake = false;

    public boolean detectWeightOnIntake = false;
    public float detectWeightIntakeTime = 750;

    public boolean detectWeightWithMechanism = false;
    public float  detectWeightMechanismTime = 750;

    public OverRobotStats() {
        super(Game.OVERBOARD);
    }

    @Override
    public Command getAutonomous(Robot r) {
        return null;
    }
}
