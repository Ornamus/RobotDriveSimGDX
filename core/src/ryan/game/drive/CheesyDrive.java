package ryan.game.drive;

import ryan.game.Utils;
import ryan.game.controls.Gamepad;

public class CheesyDrive implements DriveController {

    float mQuickStopAccumulator;

    //Tuning
    public static final float kThrottleDeadband = 0.02f;
    private static final float kWheelDeadband = 0.02f;
    private static final float kTurnSensitivity = 1.0f;

    public DriveOrder calculate(Gamepad g) {
        float throttle = g.getY();
        float wheel = g.getX2();

        throttle = (float) Math.pow(throttle, 2) * Utils.sign(throttle);
        wheel = (float) Math.pow(wheel, 2) * Utils.sign(wheel);

        boolean isQuickTurn = throttle < 0.1;

        wheel = handleDeadband(wheel, kWheelDeadband);
        throttle = handleDeadband(throttle, kThrottleDeadband);

        float overPower;

        float angularPower;

        if (isQuickTurn) {
            if (Math.abs(throttle) < 0.2) {
                float alpha = 0.1f;
                mQuickStopAccumulator = (1f - alpha) * mQuickStopAccumulator + alpha * Utils.cap(wheel, 1.0f) * 2;
            }
            overPower = 1.0f;
            angularPower = wheel;
        } else {
            overPower = 0.0f;
            angularPower = Math.abs(throttle) * wheel * kTurnSensitivity - mQuickStopAccumulator;
            if (mQuickStopAccumulator > 1) {
                mQuickStopAccumulator -= 1;
            } else if (mQuickStopAccumulator < -1) {
                mQuickStopAccumulator += 1;
            } else {
                mQuickStopAccumulator = 0.0f;
            }
        }

        float rightPwm = throttle - angularPower;
        float leftPwm = throttle + angularPower;
        if (leftPwm > 1.0) {
            rightPwm -= overPower * (leftPwm - 1.0);
            leftPwm = 1.0f;
        } else if (rightPwm > 1.0) {
            leftPwm -= overPower * (rightPwm - 1.0);
            rightPwm = 1.0f;
        } else if (leftPwm < -1.0) {
            rightPwm += overPower * (-1.0 - leftPwm);
            leftPwm = -1.0f;
        } else if (rightPwm < -1.0) {
            leftPwm += overPower * (-1.0 - rightPwm);
            rightPwm = -1.0f;
        }
        return new DriveOrder(leftPwm, rightPwm);
    }

    public float handleDeadband(float val, float deadband) {
        return (Math.abs(val) > Math.abs(deadband)) ? val : 0.0f;
    }
}