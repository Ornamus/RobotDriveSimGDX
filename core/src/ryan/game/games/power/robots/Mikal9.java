package ryan.game.games.power.robots;

public class Mikal9 extends MikalRobotBase {

    public Mikal9() {
        maxMPS = 8 / 3.28084f;
        //maxMPSLow = 6 / 3.28084f;

        robotHeight = 28 / 39.3701f;
        robotWidth = 33 / 39.3701f;

        pixelIntake = true;
        outtakeBack = true;
        pixelIntakeStrength = 6;
        pixelIntakeTime = 500;

        arm = false;

        tallPixelScore = false;
        canClimb = true;
        climbTime = 8;

        //maxAccel = (15.5448f) * (robotWidth / 0.9144f); //recalculate when width changes

        texture = "core/assets/mikal9.png";
        recolorIndex = 1;
    }
}
