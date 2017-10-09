package ryan.game.games.steamworks.robots;

import ryan.game.autonomous.steamworks.Auto254;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.entity.Robot;

public class Steam254 extends SteamRobotStats {

    public Steam254() {
        super();
        intakeWidth = robotWidth;

        gearIntake = true;

        fuelIntake = true;
        fuelIntakeRate = 220;

      //  differentiateBetweenIntakes = true;

        maxFuel = 70;
        timePerShoot = 143f;
        shootHeight = 1.15f;
        shootPower = 24;
        shootPowerVariance = 2.5f;
        shootAngleVariance = 1;

        needsStateGenerator = true;

        texture = "core/assets/254.png";
        recolorIndex = 1;
    }
    /*@Override
    public void addParts(float x, float y, Robot r) {
        float width = intakeWidth, height = robotHeight / 4;
        Body in = BodyFactory.getRectangleDynamic(x - (robotWidth / 2), y + robotHeight + height, width, height, width * height);
        Part p = new Intake(width * 2, height * 2, in);
        p.addTags("gear");
        r.addPart(p);

        width = robotWidth;
        height = robotHeight / 5;
        in = BodyFactory.getRectangleDynamic(x - (robotWidth / 2), y - robotHeight - height - .01f, width, height, 0.05f);
        p = new Intake(width * 2, height * 2, in);
        p.addTags("fuel");
        r.addPart(p);
    }
    */
    @Override
    public Command getAutonomous(Robot r) {
        return new Auto254(r);
    }
}
