package ryan.game.drive;

import ryan.game.controls.Gamepad;

public interface DriveController {

    DriveOrder calculate(Gamepad g);
}
