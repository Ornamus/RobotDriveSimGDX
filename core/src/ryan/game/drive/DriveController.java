package ryan.game.drive;

import ryan.game.controls.Gamepad;

public interface DriveController {

    public DriveOrder calculate(Gamepad g);
}
