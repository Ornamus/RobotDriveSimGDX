package ryan.game.games.steamworks.robots;

import ryan.game.autonomous.steamworks.AutoHopper;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.entity.Robot;

public class SteamSomething extends SteamRobotStats {

    public SteamSomething() {
        maxMPS = 13 / 3.28084f;

        intakeWidth = .55f;

        gearHPStation = true;
        gearIntake = true;
        gearIntakeRate = 750;
        gearIntakeStrength = 10f;

        fuelIntake = true;
        fuelIntakeRate = 220;
        fuelIntakeStrength = 1f;

        shooter = true;
        timePerShoot = 170;

        shootHeight = 1.1f;
        shootHeightVariance = .09f;
        shootPower = 24;
        shootPowerVariance = 2.5f;
        shootAngleVariance = 1;
        maxFuel = 50;

        climber = true;
        climbSpeed = 5f;

        texture = "core/assets/something.png";
        recolorIndex = 1;
    }


    @Override
    public Command getAutonomous(Robot r) {
        AutoHopper a = new AutoHopper(r);
        a.driveDistance = 7500;
        return a;
    }
}
