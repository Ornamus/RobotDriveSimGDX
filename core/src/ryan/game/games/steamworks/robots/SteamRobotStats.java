package ryan.game.games.steamworks.robots;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import ryan.game.autonomous.steamworks.AutoBaseline;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.competition.RobotStats;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Robot;
import ryan.game.entity.parts.Intake;
import ryan.game.games.Game;

public class SteamRobotStats extends RobotStats {

    public boolean gearHPStation = true;
    public boolean gearIntake = true;
    public float gearIntakeRate = 650;
    public float gearIntakeStrength = 10f;

    public boolean fuelIntake = true;
    public float fuelIntakeRate = 250;
    public float fuelIntakeStrength = 1f;

    public boolean differentiateBetweenIntakes = false;

    public boolean shooter = true;
    public float timePerShoot = 166f;
    public boolean shooterIsTurret = false;
    public float turretWidth = .275f;
    public float turretHeight = .275f;
    public boolean shooterVisible = false;
    public Vector2 shooterPosition = new Vector2(0, 0);
    public String turretTexture = "core/assets/118_turret.png";

    public float shootHeight = 1f;
    public float shootHeightVariance = .09f;
    public float shootPower = 24;
    public float shootPowerVariance = 3;
    public float shootAngleVariance = 2;
    public int maxFuel = 45;

    public boolean climber = true;
    public float climbSpeed = 2f;
    public float climbSuccess = 1f;

    public float gearScoreSuccess = 1f;
    public float gearDropOnCollide = 0f;

    public SteamRobotStats() {
        intakeWidth = robotWidth * .9f;
    }

    @Override
    public Command getAutonomous(Robot r) {
        return new AutoBaseline(r);
    }

    @Override
    public void addParts(float x, float y, Robot r) {
        if (hasIntake) {
            float width = intakeWidth, height = robotHeight / 4;
            Body in = BodyFactory.getRectangleDynamic(x - (robotWidth / 2), y + robotHeight + height, width, height, width * height);
            r.addPart(new Intake(width * 2, height * 2, in));
        }
    }

}
