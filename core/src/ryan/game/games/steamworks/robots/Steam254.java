package ryan.game.games.steamworks.robots;

import com.badlogic.gdx.physics.box2d.Body;
import ryan.game.autonomous.steamworks.Auto254;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Robot;
import ryan.game.entity.parts.Intake;

public class Steam254 extends SteamRobotStats {

    public Steam254() {
        super();
        intakeWidth = robotWidth*.6f;

        gearIntake = true;

        fuelIntake = true;
        fuelIntakeRate = 220;

        timePerShoot = 143f;
        shootHeight = 1.15f;
        shootPower = 24;
        shootPowerVariance = 2.5f;
        shootAngleVariance = 1;

        needsStateGenerator = true;

        texture = "core/assets/254.png";
        recolorIndex = 1;
    }

    //TODO: fix buggy physics with this extra intake
    @Override
    public void addParts(float x, float y, Robot r) {
        super.addParts(x, y, r);

        float width = robotWidth, height = robotHeight / 5;
        Body in = BodyFactory.getRectangleDynamic(x - (robotWidth / 2), y - robotHeight - height - .01f, width, height, 0.0000000001f);
        r.addPart(new Intake("fuel intake", width * 2, height * 2, in));
    }

    @Override
    public Command getAutonomous(Robot r) {
        return new Auto254(r);
    }
}
