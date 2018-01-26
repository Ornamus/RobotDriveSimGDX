package ryan.game.games.power.robots;

public class Mikal6 extends MikalRobotBase {

    public Mikal6() {
        maxMPS = 6 / 3.28084f;
        //maxMPSLow = 8 / 3.28084f;

        robotHeight = 28 / 39.3701f;
        robotWidth = 32 / 39.3701f;

        pixelIntake = false;
        maxPixels = 0;
        outtakeBack = false;
        pixelIntakeStrength = 10;
        pixelIntakeTime = 300;

        arm = false;

        tallPixelScore = false;
        canClimb = true;
        climbTime = 5;

        //maxAccel = (15.5448f) * (robotWidth / 0.9144f); //recalculate when width changes

        texture = "core/assets/mikal6.png";
        recolorIndex = 1;
    }
}
