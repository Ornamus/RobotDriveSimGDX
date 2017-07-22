package ryan.game.autonomous.pathmagic;

import ryan.game.Main;
import ryan.game.bcnlib_pieces.Motor;
import ryan.game.bcnlib_pieces.PIDController;
import ryan.game.drive.DriveOrder;
import ryan.game.entity.Robot;
import ryan.game.team254.utils.AdaptivePurePursuitController;
import ryan.game.team254.utils.Path;
import ryan.game.team254.utils.RigidTransform2d;

public class PursuitControl {

    Motor leftOutput, rightOutput;
    public AdaptivePurePursuitController pathFollower;
    Robot r;
    PIDController left, right;

    final double kP = 0.01, kI = 0, kD = 0;

    public PursuitControl(Robot r) {
        this.r = r;
        leftOutput = new Motor();
        rightOutput = new Motor();
        left = new PIDController(leftOutput, r.getLeftEncoder(), kP, kI, kD);
        right = new PIDController(rightOutput, r.getRightEncoder(), kP, kI, kD);
    }

    public void followPath(Path path, boolean reversed) {
        pathFollower = new AdaptivePurePursuitController(Constants.pathFollowLookahead,
                Constants.pathFollowMaxAccel, Constants.kLooperDt, path, reversed, 0.1); //.25
    }

    public boolean isDone() {
        return pathFollower.isDone();
    }

    public DriveOrder tick() {
        if (pathFollower != null && !pathFollower.isDone()) {
            if (!left.isEnabled()) left.enable();
            if (!right.isEnabled()) right.enable();
            RigidTransform2d robot_pose = r.state.getLatestFieldToVehicle().getValue();
            RigidTransform2d.Delta command = pathFollower.update(robot_pose, Main.getTime()); //TODO: was Timer.getFPGA
            Kinematics.DriveVelocity setpoint = Kinematics.inverseKinematics(command);

            // Scale the command to respect the max velocity limits
            double max_vel = 0;
            max_vel = Math.max(max_vel, Math.abs(setpoint.left));
            max_vel = Math.max(max_vel, Math.abs(setpoint.right));
            if (max_vel > Constants.pathFollowMaxVel) {
                double scaling = Constants.pathFollowMaxVel / max_vel;
                setpoint = new Kinematics.DriveVelocity(setpoint.left * scaling, setpoint.right * scaling);
            }

            //Utils.log(setpoint.left + ", " + setpoint.right);
            //Utils.log(Utils.roundToPlace((float)setpoint.left, 5) + " / " + Utils.roundToPlace((float)setpoint.right, 2));

            //TODO: velocity PID for left and right wheels receive the setpoint.left and setpoint.right values

            left.setTarget(left.getCurrentSourceValue() + setpoint.left);
            right.setTarget(right.getCurrentSourceValue() + setpoint.right);
        } else {
            left.disable();
            right.disable();
            leftOutput.setPower(0);
            rightOutput.setPower(0);
        }
        return new DriveOrder(leftOutput.getPower(), rightOutput.getPower());
    }
}
