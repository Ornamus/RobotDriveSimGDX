package ryan.game.sensors;

import ryan.game.bcnlib_pieces.PIDSource;
import ryan.game.entity.Robot;

public class Gyro implements PIDSource {

    double fakeReset = 0;
    Robot r;

    public Gyro(Robot r) {
        this.r = r;
    }

    @Override
    public double getForPID() {
        double adjustAngle = -r.getAngle() + fakeReset;

        if (adjustAngle < 0) {
            adjustAngle = 360 + adjustAngle;
        }
        while (adjustAngle > 360) {
            adjustAngle -= 360;
        }
        return adjustAngle;
    }

    @Override
    public void reset() {
        rezero();
    }

    public void rezero() {
        rezero((double) r.getAngle());
    }

    public void rezero(double newZero) {
        fakeReset = newZero;
    }

    public double getZeroOffset() {
        return fakeReset;
    }
}
