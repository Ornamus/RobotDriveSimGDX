package ryan.game.autonomous;


import ryan.game.Utils;
import ryan.game.autonomous.pathmagic.PursuitControl;
import ryan.game.autonomous.pathmagic.RobotState;
import ryan.game.autonomous.pathmagic.RobotStateGenerator;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.drive.DriveOrder;
import ryan.game.entity.Robot;
import ryan.game.games.Game;
import ryan.game.games.steamworks.SteamworksMetadata;
import ryan.game.team254.utils.Path;
import ryan.game.team254.utils.RigidTransform2d;
import ryan.game.team254.utils.Translation2d;

import java.util.ArrayList;
import java.util.List;

public class Auto254 extends Command {

    public Auto254(Robot r) {
        super(r);
    }

    @Override
    public void onInit() {
        try {
            SteamworksMetadata meta = (SteamworksMetadata) robot.metadata;
            robot.getGyro().reset();
            robot.getLeftEncoder().reset();
            robot.getRightEncoder().reset();
            if (robot.generator != null) robot.generator.actuallyStop();
            robot.generator = null;
            robot.state.reset(System.currentTimeMillis(), new RigidTransform2d());
            robot.generator = new RobotStateGenerator(robot.state, robot);
            robot.generator.start();

            Utils.log("Gyro: " + robot.getGyro().getForPID() + ", left: " + robot.getLeftEncoder().getForPID() + ", right: " + robot.getRightEncoder().getForPID());

            PursuitControl c = new PursuitControl(robot);

            List<Path.Waypoint> path = new ArrayList<>();
            path.add(new Path.Waypoint(new Translation2d(0, 0), 120));
            path.add(new Path.Waypoint(new Translation2d(150, 0), 80));
            path.add(new Path.Waypoint(new Translation2d(270, -120), 120));

            followPath(c, new Path(path), false);
            meta.ejectGear(robot);

            path.clear();
            path.add(new Path.Waypoint(new Translation2d(270, -120), 120.0));
            path.add(new Path.Waypoint(new Translation2d(240, -90), 120.0));
            path.add(new Path.Waypoint(new Translation2d(435, 115), 120.0));
            path.add(new Path.Waypoint(new Translation2d(465, 125), 120.0));
            followPath(c, new Path(path), true);

            path.clear();
            path.add(new Path.Waypoint(new Translation2d(465, 125), 70));
            path.add(new Path.Waypoint(new Translation2d(380, 129), 0)); //370, 130

            followPath(c, new Path(path), false);
            robot.setMotors(0, 0);
            while (Game.isAutonomous()) {
                meta.shootFuel(robot);
            }
            Utils.log("254 done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void followPath(PursuitControl c, Path p, boolean reverse) {
        c.followPath(p, reverse);
        while (!c.isDone() && Game.isAutonomous()) {
            DriveOrder o = c.tick();
            robot.setMotors(o.left, o.right);
        }
    }

    @Override
    public void onLoop() {}

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return true;
    }
}
