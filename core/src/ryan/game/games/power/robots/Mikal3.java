package ryan.game.games.power.robots;

public class Mikal3 extends MikalRobotBase {

    public Mikal3() {
        maxMPS = 14 / 3.28084f;
        maxMPSLow = 7 / 3.28084f;

        robotHeight = 33 / 39.3701f;
        robotWidth = 28 / 39.3701f;

        pixelIntake = true;
        outtakeBack = false;
        pixelIntakeStrength = 8;
        pixelIntakeTime = 250;

        arm = false;

        tallPixelScore = true;
        canClimb = true;
        climbTime = 7;

        //maxAccel = (15.5448f) * (robotWidth / 0.9144f); //recalculate when width changes

        texture = "core/assets/mikal3.png";
        recolorIndex = 1;
    }
}
