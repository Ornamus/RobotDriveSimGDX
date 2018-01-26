package ryan.game.games.power.robots;

public class Mikal14 extends MikalRobotBase {

    public Mikal14() {
        maxMPS = 14 / 3.28084f;
        //maxMPSLow = 22 / 3.28084f;

        robotHeight = 27 / 39.3701f;
        robotWidth = 27 / 39.3701f;

        pixelIntake = true;
        outtakeBack = false;
        pixelIntakeStrength = 10;
        pixelIntakeTime = 400;

        arm = false;

        tallPixelScore = true;
        canClimb = true;
        climbTime = 16;

        //maxAccel = (15.5448f) * (robotWidth / 0.9144f); //recalculate when width changes

        texture = "core/assets/mikal14.png";
        recolorIndex = 1;
    }
}
