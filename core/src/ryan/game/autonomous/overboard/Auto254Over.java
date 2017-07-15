package ryan.game.autonomous.overboard;


import ryan.game.Utils;
import ryan.game.autonomous.pathmagic.PursuitControl;
import ryan.game.autonomous.pathmagic.RobotStateGenerator;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.drive.DriveOrder;
import ryan.game.entity.Robot;
import ryan.game.games.Game;
import ryan.game.games.overboard.PirateMetadata;
import ryan.game.games.steamworks.SteamworksMetadata;
import ryan.game.team254.utils.Path;
import ryan.game.team254.utils.RigidTransform2d;
import ryan.game.team254.utils.Translation2d;

import java.util.ArrayList;
import java.util.List;

public class Auto254Over extends Command {

    public Auto254Over(Robot r) {
        super(r);
    }

    @Override
    public void onInit() {
        Utils.log("254over init");
        try {
            PirateMetadata meta = (PirateMetadata) robot.metadata;
            robot.getGyro().reset();
            robot.getLeftEncoder().reset();
            robot.getRightEncoder().reset();
            if (robot.generator != null) robot.generator.actuallyStop();
            robot.generator = null;
            robot.state.reset(System.currentTimeMillis(), new RigidTransform2d());
            robot.generator = new RobotStateGenerator(robot.state, robot);
            robot.generator.start();

            //Getting the gear
            List<Path.Waypoint> path = new ArrayList<>();
            //path.add(new Path.Waypoint(new Translation2d(0, 0), 120));
            path.add(new Path.Waypoint(new Translation2d(500, 0), 120));
            path.add(new Path.Waypoint(new Translation2d(730, 50), 60));
            Path toChest = new Path(path);

            //Coming back
            path = new ArrayList<>();
            path.add(new Path.Waypoint(new Translation2d(730, 50), 120));
            path.add(new Path.Waypoint(new Translation2d(80, -40), 120));
            path.add(new Path.Waypoint(new Translation2d(80, -70), 120));
            Path toShipEntrance = new Path(path);

            //Entering the ship
            path = new ArrayList<>();
            //path.add(new Path.Waypoint(new Translation2d(80, -70), 120));
            path.add(new Path.Waypoint(new Translation2d(40, 80), 120));
            path.add(new Path.Waypoint(new Translation2d(80, 120), 120));
            Path toShipInside = new Path(path);

            //Exiting the ship
            path = new ArrayList<>();
            //path.add(new Path.Waypoint(new Translation2d(80, 120), 120));
            path.add(new Path.Waypoint(new Translation2d(40, 80), 120));
            path.add(new Path.Waypoint(new Translation2d(40, -10), 120));
            path.add(new Path.Waypoint(new Translation2d(200, -10), 120));
            Path toEscape = new Path(path);

            PursuitControl c = new PursuitControl(robot);

            meta.setIntaking(true);

            followPath(c, toChest, false);
            robot.setMotors(0,0);
            Thread.sleep(300);

            followPath(c, toShipEntrance, true);
            robot.setMotors(0,0);
            meta.setIntaking(false);

            followPath(c, toShipInside, false);
            robot.setMotors(0,0);
            meta.ejectChest(robot);
            Thread.sleep(300);
            meta.ejectChest(robot);
            Thread.sleep(300);

            followPath(c, toEscape, true);
            robot.setMotors(-1,-1);


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
