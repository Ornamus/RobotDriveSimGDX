package ryan.game.drive;

import ryan.game.Utils;
import ryan.game.controls.Gamepad;

public class Arcade implements DriveController {

    boolean splitStick;

    public Arcade(boolean splitStick) {
        this.splitStick = splitStick;
    }

    @Override
    public DriveOrder calculate(Gamepad g) {
        float x = splitStick ? g.getX2() : g.getX();
        float y = g.getY();
        x = Utils.deadzone(x, 0.1f);
        y = Utils.deadzone(y, 0.1f);
        x = (float) Math.pow(x, 2) * Utils.sign(x);
        y = (float) Math.pow(y, 2) * Utils.sign(y);
        return new DriveOrder(y + x, y - x);
    }
}
