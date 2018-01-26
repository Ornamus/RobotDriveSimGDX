package ryan.game.games.power.robots;

public class Mikal13 extends MikalRobotBase {

    public Mikal13() {
        maxMPS = 44 / 3.28084f;
        maxMPSLow = 22 / 3.28084f;

        robotHeight = 30 / 39.3701f;
        robotWidth = 26 / 39.3701f;

        pixelIntake = true;
        outtakeBack = false;
        pixelIntakeStrength = 11;
        pixelIntakeTime = 100;

        arm = false;

        tallPixelScore = true;
        canClimb = true;
        climbTime = 5;

        //maxAccel = (15.5448f) * (robotWidth / 0.9144f); //recalculate when width changes

        texture = "core/assets/mikal13.png";
        recolorIndex = 1;
    }
}
