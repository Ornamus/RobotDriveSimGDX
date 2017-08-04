package ryan.game.autonomous.pathmagic;

import ryan.game.Main;
import ryan.game.entity.Robot;
import ryan.game.team254.utils.RigidTransform2d;
import ryan.game.team254.utils.Rotation2d;

/**
 * Periodically estimates the state of the robot using the robot's distance
 * traveled (compares two waypoints), gyroscope orientation, and velocity, among
 * various other factors. Similar to a car's odometer.
 */

public class RobotStateGenerator extends Thread  {

    static RobotStateGenerator instance_ = new RobotStateGenerator();
    boolean stop = false;

    public static RobotStateGenerator getInstance() {
        return instance_;
    }

    RobotStateGenerator() {}

    RobotState robot_state_;
    Robot robot;
    double left_encoder_prev_distance_ = 0;
    double right_encoder_prev_distance_ = 0;

    long lastLoop = 0;

    public RobotStateGenerator(RobotState state, Robot r) {
        robot = r;
        robot_state_ = state;
    }

    @Override
    public void start() {
        super.start();
        left_encoder_prev_distance_ = getLeftDistanceInches();
        right_encoder_prev_distance_ = getRightDistanceInches();
    }

    @Override
    public void run() {
        while (!stop) {
            double time = Main.getTime(); //TODO: Was Timer.getFPGA
            double left_distance = getLeftDistanceInches();
            double right_distance = getRightDistanceInches();
            Rotation2d gyro_angle = Rotation2d.fromDegrees(-robot.getGyro().getForPID());

            RigidTransform2d odometry = robot_state_.generateOdometryFromSensors(left_distance - left_encoder_prev_distance_,
                    right_distance - right_encoder_prev_distance_, gyro_angle);

            RigidTransform2d.Delta velocity = Kinematics.forwardKinematics(getLeftVelocityInchesPerSec(),
                    getRightVelocityInchesPerSec());

            robot_state_.addObservations(time, odometry, velocity);
            left_encoder_prev_distance_ = left_distance;
            right_encoder_prev_distance_ = right_distance;

            lastLoop = Main.getTime();
        }
    }

    public void actuallyStop() {
        stop = true;
    }

    public float getLeftDistanceInches() {
        return (float) (robot.getLeftEncoder().getForPID() / 1000) * 39.3701f;
    }

    public float getRightDistanceInches() {
        return (float) (robot.getRightEncoder().getForPID() / 1000) * 39.3701f;
    }

    public float getLeftVelocityInchesPerSec() {
        return (float) (getLeftDistanceInches() - left_encoder_prev_distance_) * (1000f / (Main.getTime() - lastLoop));
    }

    public float getRightVelocityInchesPerSec() {
        return (float) (getRightDistanceInches() - right_encoder_prev_distance_) * (1000f / (Main.getTime() - lastLoop));
    }
}
