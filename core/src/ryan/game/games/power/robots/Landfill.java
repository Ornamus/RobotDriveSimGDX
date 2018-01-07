package ryan.game.games.power.robots;

public class Landfill extends PowerRobotBase {

    public Landfill() {
        super();
        intakeWidth = robotWidth*.9f;
        pixelIntakeStrength = 7f;
        pixelIntakeTime = 500;

        tallPixelScore = false;

        texture="core/assets/1902.png";
        recolorIndex = 1;
    }
}
