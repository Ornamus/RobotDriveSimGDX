package ryan.game.games.steamworks.robots;

import ryan.game.autonomous.steamworks.Auto1902;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.entity.Robot;

public class Steam1902 extends SteamRobotStats {

    public Steam1902() {
        super();
        gearIntake = false;
        //intakeWidth = robotWidth * .65f; //TODO: reeneable when this is redone when switched to
        fuelIntake = false;
        shooter = true;
        maxFuel = 20;
        timePerShoot = 300;
        fieldCentric = true;
        texture = "core/assets/1902.png";
    }

    @Override
    public Command getAutonomous(Robot r) {
        return new Auto1902(r);
    }
}
