package ryan.game.games.power.robots;

public class Mikal1 extends MikalRobotBase {

    public Mikal1() {
        //maxMPSLow = 7 / 3.28084f;
        maxMPS = 10 / 3.28084f;

        robotHeight = 28 / 39.3701f;
        robotWidth = 33 / 39.3701f;

        pixelIntake = true;
        outtakeBack = false;
        pixelIntakeStrength = 9;
        pixelIntakeTime = 50;

        arm = false;

        tallPixelScore = false;
        canClimb = true;
        climbTime = 12;

        //maxAccel = (15.5448f) * (robotWidth / 0.9144f); //recalculate when width changes

        texture = "core/assets/mikal1.png";
        recolorIndex = 1;
    }
}
