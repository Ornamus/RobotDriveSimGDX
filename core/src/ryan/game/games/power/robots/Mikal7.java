package ryan.game.games.power.robots;

public class Mikal7 extends MikalRobotBase {

    public Mikal7() {
        maxMPS = 13 / 3.28084f;
        maxMPSLow = 6 / 3.28084f;

        robotHeight = 24 / 39.3701f;
        robotWidth = 22 / 39.3701f;

        pixelIntake = true;
        outtakeBack = false;
        pixelIntakeStrength = 11;
        pixelIntakeTime = 150;

        arm = false;

        tallPixelScore = true;
        canClimb = true;
        climbTime = 5;

        //maxAccel = (15.5448f) * (robotWidth / 0.9144f); //recalculate when width changes

        texture = "core/assets/mikal7.png";
        recolorIndex = 1;
    }
}
