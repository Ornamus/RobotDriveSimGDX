package ryan.game.games.power.robots;

import ryan.game.autonomous.powerup.Bacon_Basic;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.entity.Robot;

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


        climbTime = 4;

        tallPixelScore = false;

        outtakeBack = true;

        texture="core/assets/1902.png";
        recolorIndex = 1;
    }

    @Override
    public Command getAutonomous(Robot r) {
        return new Bacon_Basic(r);
    }
}