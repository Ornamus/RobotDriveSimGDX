package ryan.game.games.power.robots;

public class Mikal8 extends MikalRobotBase {

    public Mikal8() {
        maxMPS = 29 / 3.28084f;
        //maxMPSLow = 6 / 3.28084f;

        robotHeight = 17 / 39.3701f;
        robotWidth = 12 / 39.3701f;

        pixelIntake = true;
        outtakeBack = true;
        pixelIntakeStrength = 12;
        pixelIntakeTime = 100;

        arm = false;

        tallPixelScore = true;
        canClimb = true;
        climbTime = 1;

        //maxAccel = (15.5448f) * (robotWidth / 0.9144f); //recalculate when width changes

        texture = "core/assets/mikal8.png";
        recolorIndex = 1;
    }
}
