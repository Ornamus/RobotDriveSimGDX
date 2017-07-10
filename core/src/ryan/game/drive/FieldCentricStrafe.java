package ryan.game.drive;

import ryan.game.Utils;
import ryan.game.bcnlib_pieces.Motor;
import ryan.game.bcnlib_pieces.PIDController;
import ryan.game.controls.Gamepad;
import ryan.game.entity.Robot;
import ryan.game.games.Game;
import ryan.game.sensors.Gyro;

public class FieldCentricStrafe implements DriveController {

    final float feedAngle = 60.56f;

    PIDController rotatePID;
    Motor pidOutput;
    Gyro gyro;
    Robot robot;
    float targetAngle;

    public FieldCentricStrafe(Robot r) {
        this.robot = r;
        pidOutput = new Motor();
        gyro = r.getGyro();
        rotatePID = new PIDController(pidOutput, gyro, 0.01, 0.0008 * 2, 0.09 * 1.5, 0.15, 1).setRotational(true);
        targetAngle = r.blue ? 270 : 90;
    }

    public void setTargetAngle(float angle) {
        targetAngle = angle;
    }

    //2 3 0 1
    @Override
    public DriveOrder calculate(Gamepad g) {
        if (gyro.getZeroOffset() != 0) gyro.rezero(0);
        //Utils.log(g.getDPad() + "");
        //double x, y, target
        float adjust = robot.blue ? 270 : 90;

        if (g.getButton(robot.blue? 0 : 3).get()) targetAngle = adjust - feedAngle;
        if (g.getButton(robot.blue ? 2 : 1).get()) targetAngle = adjust;
        if (g.getButton(robot.blue ? 1 : 2).get()) targetAngle = adjust + 180;
        if (g.getButton(robot.blue ? 3 : 0).get()) targetAngle = feedAngle + adjust;

        if (g.getButton(2).get() && g.getButton(1).get()) targetAngle = adjust + (robot.blue ? -90 : 90);

        if (Game.isAutonomous()) targetAngle = adjust;

        while (targetAngle > 360) targetAngle -= 360;
        while (targetAngle < 0) targetAngle = 360 + targetAngle;

        float x = g.getX();
        float y = g.getY();
        float target = targetAngle;

        //Utils.log("x: " + Utils.roundToPlace(x, 2) + ", y: "+ Utils.roundToPlace(y, 2));

        //y = -y;
        if (!rotatePID.isEnabled()) rotatePID.enable();
        rotatePID.setTarget(target);

        //Utils.log("Target angle: " + target);
        //Utils.log("PID output: " + pidOutput.getPower());

        float z = pidOutput.getPower();
        float angle = (float) Math.toRadians(-gyro.getForPID());
        float xSet, ySet;

        xSet = (float)(y * Math.sin(angle) + x * Math.cos(angle));
        ySet = (float)(y * Math.cos(angle) - x * Math.sin(angle));

        //Utils.log("Motors: " + Utils.roundToPlace(o.left, 2) + " / " + Utils.roundToPlace(o.right, 2) + " / " + Utils.roundToPlace(o.middle, 2));
        return setFiltered((ySet + z), -(z - ySet), xSet);
    }

    private DriveOrder setFiltered(float leftPower, float rightPower, float strafePower) {
        float max = Utils.maxFloat(leftPower, rightPower, strafePower);

        if (strafePower < 100 && max < 100) {
            return new DriveOrder(leftPower, rightPower, strafePower);
        }

        return new DriveOrder(leftPower / max, rightPower / max, strafePower / max);
    }
}
