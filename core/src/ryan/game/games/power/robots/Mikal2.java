package ryan.game.games.power.robots;

public class Mikal2 extends MikalRobotBase {

    public Mikal2() {
        //maxMPSLow = 7 / 3.28084f;
        maxMPS = 6 / 3.28084f;

        robotHeight = 25 / 39.3701f;
        robotWidth = 29 / 39.3701f;

        pixelIntake = true;
        outtakeBack = false;
        pixelIntakeStrength = 5;
        pixelIntakeTime = 1000;

        arm = true;

        tallPixelScore = true;
        canClimb = false;
        climbTime = 12;

        //maxAccel = (15.5448f) * (robotWidth / 0.9144f); //recalculate when width changes

        texture = "core/assets/mikal2.png";
        recolorIndex = 1;
    }
}
