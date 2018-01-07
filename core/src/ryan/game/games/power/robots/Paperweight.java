package ryan.game.games.power.robots;

import ryan.game.entity.Robot;

public class Paperweight extends PowerRobotBase {

    public Paperweight() {
        super();
        canClimb = false;
        maxPixels = 0;

        texture="core/assets/rookie.png";
        recolorIndex = 1;
    }

    @Override
    public void addParts(float x, float y, Robot r) {}
}