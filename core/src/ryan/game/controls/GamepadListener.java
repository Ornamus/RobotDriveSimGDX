package ryan.game.controls;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import ryan.game.Utils;

import java.util.HashMap;

public class GamepadListener implements ControllerListener {

    @Override
    public void connected(Controller controller) {
        Gamepad g = Gamepads.initGamepad(controller);
        Utils.log(g.getName() + " connected!");
    }

    @Override
    public void disconnected(Controller controller) {
        Gamepad g = Gamepads.getGamepad(controller);
        Utils.log(g.getName() + " disconnected!");
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        Gamepad g = Gamepads.getGamepad(controller);
        if (g.mapping) g.updateMap(buttonCode);
        return false;
    }

    HashMap<Controller, Integer> movingAxises = new HashMap<>();
    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        Gamepad g = Gamepads.getGamepad(controller);
        if (Math.abs(value) == 1) {
            if (movingAxises.get(controller) != null) {
                if (g.mapping) g.updateMap(axisCode);
                movingAxises.remove(controller);
            }
        } else {
            movingAxises.put(controller, axisCode);
        }
        return false;
    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        //Gamepad g = Gamepads.getGamepad(controller);
        //if (g.mapping) g.updateMap(povCode);
        return false;
    }

    @Override
    public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
        return false;
    }
}
