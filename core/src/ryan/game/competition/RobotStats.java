package ryan.game.competition;

import ryan.game.bcnlib_pieces.Command;
import ryan.game.entity.Robot;
import ryan.game.games.Game;

public abstract class RobotStats {

    public final Game game;
    public float robotWidth = 0.9144f;
    public float robotHeight = robotWidth;
    public float maxMPS = 16 / 3.28084f;
    public float maxAccel = (15.5448f) * (robotWidth / 0.9144f); //TODO: recalculate when width changes
    public boolean hasIntake = true;
    public float intakeWidth = robotWidth;//*.95f;
    public boolean fieldCentric = false;
    public boolean needsStateGenerator = false;
    public float fieldCentricStrafeMult = .7f;
    public int recolorIndex = 0;

    public String texture = "core/assets/robot_recolor.png";

    public RobotStats(Game g) {
        game = g;
    }

    public abstract Command getAutonomous(Robot r);

    public void addParts(float x, float y, Robot r) {}
}
