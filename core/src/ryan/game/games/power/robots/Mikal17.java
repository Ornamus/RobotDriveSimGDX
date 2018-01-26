package ryan.game.games.power.robots;

public class Mikal17 extends MikalRobotBase {

    public Mikal17() {
        maxMPS = 5 / 3.28084f;
        //maxMPSLow = 6 / 3.28084f;

        robotHeight = 25 / 39.3701f;
        robotWidth = 27 / 39.3701f;

        pixelIntake = true;
        outtakeBack = false;
        pixelIntakeStrength = 301;
        pixelIntakeTime = 100;

        arm = false;

        tallPixelScore = false;
        canClimb = true;
        climbTime = 4;

        //maxAccel = (15.5448f) * (robotWidth / 0.9144f); //recalculate when width changes

        texture = "core/assets/mikal17.png";
        recolorIndex = 1;
    }
}
