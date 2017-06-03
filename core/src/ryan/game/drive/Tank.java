package ryan.game.drive;

import ryan.game.Utils;
import ryan.game.controls.Gamepad;

public class Tank implements DriveController {

    @Override
    public DriveOrder calculate(Gamepad g) {
        float y = g.getY();
        float y2 = g.getY2();
        y = Utils.deadzone(y, 0.1f);
        y2 = Utils.deadzone(y2, 0.f);
        //x = Math.pow(x, 2) * Utils.sign(x);
        //y = Math.pow(y, 2) * Utils.sign(y);
        return new DriveOrder(y, y2);
    }
}
