package ryan.game.games.power.robots;

public class Mikal16 extends MikalRobotBase {

    public Mikal16() {
        maxMPS = 16 / 3.28084f;
        maxMPSLow = 8 / 3.28084f;

        robotHeight = 24 / 39.3701f;
        robotWidth = 27 / 39.3701f;

        pixelIntake = true;
        outtakeBack = false;
        pixelIntakeStrength = 9;
        pixelIntakeTime = 300;

        arm = false;

        tallPixelScore = false;
        canClimb = true;
        climbTime = 7;

        //maxAccel = (15.5448f) * (robotWidth / 0.9144f); //recalculate when width changes

        texture = "core/assets/mikal16.png";
        recolorIndex = 1;
    }
}
