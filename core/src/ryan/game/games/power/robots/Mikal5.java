package ryan.game.games.power.robots;

public class Mikal5 extends MikalRobotBase {

    public Mikal5() {
        maxMPS = 14 / 3.28084f;
        maxMPSLow = 8 / 3.28084f;

        robotHeight = 28 / 39.3701f;
        robotWidth = 32 / 39.3701f;

        pixelIntake = true;
        outtakeBack = false;
        pixelIntakeStrength = 10;
        pixelIntakeTime = 300;

        arm = true;

        tallPixelScore = true;
        canClimb = true;
        climbTime = 8;

        //maxAccel = (15.5448f) * (robotWidth / 0.9144f); //recalculate when width changes

        texture = "core/assets/mikal5.png";
        recolorIndex = 1;
    }
}
