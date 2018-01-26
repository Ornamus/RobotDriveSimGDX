package ryan.game.games.power.robots;

public class Mikal10 extends MikalRobotBase {

    public Mikal10() {
        maxMPS = 12 / 3.28084f;
        //maxMPSLow = 6 / 3.28084f;

        robotHeight = 27 / 39.3701f;
        robotWidth = 27 / 39.3701f;

        pixelIntake = true;
        outtakeBack = false;
        pixelIntakeStrength = 9.2f;
        pixelIntakeTime = 300;

        arm = true;

        tallPixelScore = true;
        canClimb = true;
        climbTime = 6;

        //maxAccel = (15.5448f) * (robotWidth / 0.9144f); //recalculate when width changes

        texture = "core/assets/mikal10.png";
        recolorIndex = 1;
    }
}
