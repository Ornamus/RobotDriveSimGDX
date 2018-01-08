package ryan.game.games.power.robots;

public class Bacon extends PowerRobotBase {

    public Bacon() {
        super();
        robotHeight = 0.7112f; //28 inches
        robotWidth = 0.8382f; //33 inches

        intakeWidth = robotWidth*.9f;

        maxAccel = (15.5448f) * (robotWidth / 0.9144f); //recalculate when width changes

        pixelIntakeTime = 300;

        maxMPSLow = 8 / 3.28084f;
        maxMPS = 19 / 3.28084f;


        climbTime = 4;

        tallPixelScore = false;

        outtakeBack = true;

        texture="core/assets/1902.png";
        recolorIndex = 1;
    }
}