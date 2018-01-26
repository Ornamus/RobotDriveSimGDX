package ryan.game.games.power.robots;

public class Mikal15 extends MikalRobotBase {

    public Mikal15() {
        maxMPS = 8 / 3.28084f;
        //maxMPSLow = 22 / 3.28084f;

        robotHeight = 10 / 39.3701f;
        robotWidth = 10 / 39.3701f;

        pixelIntake = false;
        maxPixels = 0;
        outtakeBack = false;
        pixelIntakeStrength = 10;
        pixelIntakeTime = 400;

        arm = false;

        tallPixelScore = false;
        canClimb = true;
        climbTime = 18;

        //maxAccel = (15.5448f) * (robotWidth / 0.9144f); //recalculate when width changes

        texture = "core/assets/mikal15.png";
        recolorIndex = 1;
    }
}
