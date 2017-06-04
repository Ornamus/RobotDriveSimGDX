package ryan.game.drive;

import ryan.game.Utils;
import ryan.game.bcnlib_pieces.Motor;
import ryan.game.bcnlib_pieces.PIDController;
import ryan.game.bcnlib_pieces.PIDSource;
import ryan.game.controls.Gamepad;
import ryan.game.entity.Robot;

public class FieldCentricStrafe implements DriveController {

    PIDController rotatePID;
    Motor pidOutput;
    PIDSource gyro;
    Robot robot;
    float targetAngle = 0;

    public FieldCentricStrafe(Robot r) {
        this.robot = r;
        pidOutput = new Motor();
        gyro = new PIDSource() {
            double fakeReset = 0;
            @Override
            public double getForPID() {
                double adjustAngle = -robot.getAngle() + fakeReset;

                if (adjustAngle < 0) {
                    adjustAngle = 360 + adjustAngle;
                }
                while (adjustAngle > 360) {
                    adjustAngle -= 360;
                }
                //Utils.log("ANGLE: " + adjustAngle);
                return adjustAngle;
            }

            @Override
            public void reset() {
                fakeReset = (double) robot.getAngle();
            }
        };
        rotatePID = new PIDController(pidOutput, gyro, 0.01, 0.0008 * 2, 0.09 * 1.5, 0.15, 1).setRotational(true);
    }

    public void setTargetAngle(float angle) {
        targetAngle = angle;
    }

    @Override
    public DriveOrder calculate(Gamepad g) {
        //double x, y, target
        float x = g.getX();
        float y = g.getY();
        float target = targetAngle;

        Utils.log("x: " + Utils.roundToPlace(x, 2) + ", y: "+ Utils.roundToPlace(y, 2));

        //y = -y;
        if (!rotatePID.isEnabled()) rotatePID.enable();
        rotatePID.setTarget(target);

        //Utils.log("Target angle: " + target);
        //Utils.log("PID output: " + pidOutput.getPower());

        float z = (float) pidOutput.getPower();
        float angle = (float) Math.toRadians(gyro.getForPID());
        float xSet, ySet;

        xSet = (float)(y * Math.sin(angle) + x * Math.cos(angle));
        ySet = (float)(y * Math.cos(angle) - x * Math.sin(angle));

        DriveOrder o = setFiltered(ySet + z, -(z - ySet), xSet);
        //Utils.log("Motors: " + Utils.roundToPlace(o.left, 2) + " / " + Utils.roundToPlace(o.right, 2) + " / " + Utils.roundToPlace(o.middle, 2));
        return o;
    }

    private DriveOrder setFiltered(float leftPower, float rightPower, float strafePower) {
        float max = Utils.maxFloat(leftPower, rightPower, strafePower);

        if (strafePower < 100 && max < 100) {
            return new DriveOrder(leftPower, rightPower, strafePower);
        }

        return new DriveOrder(leftPower / max, rightPower / max, strafePower / max);
    }
}
