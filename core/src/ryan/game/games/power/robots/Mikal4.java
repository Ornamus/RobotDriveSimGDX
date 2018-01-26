package ryan.game.games.power.robots;

public class Mikal4 extends MikalRobotBase {

    public Mikal4() {
        maxMPS = 15 / 3.28084f;
        //maxMPSLow = 14 / 3.28084f;

        robotHeight = 33 / 39.3701f;
        robotWidth = 28 / 39.3701f;

        pixelIntake = true;
        outtakeBack = true;
        pixelIntakeStrength = 7;
        pixelIntakeTime = 1000;

        arm = false;

        tallPixelScore = false;
        canClimb = true;
        climbTime = 4;

        //maxAccel = (15.5448f) * (robotWidth / 0.9144f); //recalculate when width changes

        texture = "core/assets/mikal4.png";
        recolorIndex = 1;
    }
}
