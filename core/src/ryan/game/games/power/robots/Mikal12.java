package ryan.game.games.power.robots;

public class Mikal12 extends MikalRobotBase {

    public Mikal12() {
        maxMPS = 16 / 3.28084f;
        maxMPSLow = 8 / 3.28084f;

        robotHeight = 26 / 39.3701f;
        robotWidth = 29 / 39.3701f;

        pixelIntake = true;
        outtakeBack = false;
        pixelIntakeStrength = 5;
        pixelIntakeTime = 700;

        arm = true;

        tallPixelScore = true;
        canClimb = true;
        climbTime = 6;

        //maxAccel = (15.5448f) * (robotWidth / 0.9144f); //recalculate when width changes

        texture = "core/assets/mikal12.png";
        recolorIndex = 1;
    }
}
