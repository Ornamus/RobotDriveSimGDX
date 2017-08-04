package ryan.game.controls;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.entity.Robot;
import ryan.game.games.steamworks.robots.SteamDefault;
import java.util.HashMap;

public class GamepadListener implements ControllerListener {

    @Override
    public void connected(Controller controller) {
        boolean reconnect = false;
        for (Gamepad g : Gamepads.getGamepads()) {
            if (g.c == null && g.getName().equalsIgnoreCase(controller.getName()) && g.r != null) {
                g.c = controller;
                if (g.r != null) g.r.onGamepadReconnect();
                reconnect = true;
                break;
            }
        }
        if (reconnect) {
            Utils.log(controller.getName() + " gamepad reconnected!");
        } else {
            Gamepad g = Gamepads.initGamepad(controller);
            Utils.log(g.getName() + " gamepad connected!");

            //TODO: make this game generic
            Robot r = Robot.create(new SteamDefault(), 2, -11);

            r.claimGamepad(g);
            Main.robots.add(r);
            Main.getInstance().spawnEntity(r);
            Main.getInstance().popSound.play(0.75f);
        }
    }

    @Override
    public void disconnected(Controller controller) {
        Gamepad g = Gamepads.getGamepad(controller);
        Utils.log(g.getName() + " gamepad disconnected!");
        g.c = null;
        if (g.r != null) {
            g.r.onGamepadDisconnect();
        }
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
