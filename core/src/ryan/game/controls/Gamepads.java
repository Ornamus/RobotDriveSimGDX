package ryan.game.controls;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.lwjgl3.Lwjgl3ControllerManager;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Gamepads {

    private static List<Gamepad> gamepads = new ArrayList<>();
    private static Timer update;

    public static void init() {
        Lwjgl3ControllerManager manager = new Lwjgl3ControllerManager();
        manager.addListener(new GamepadListener());
        for (Controller controller : manager.getControllers()) {
            initGamepad(controller);
        }
    }

    public static Gamepad initGamepad(Controller c) {
        Gamepad g = new Gamepad(c);
        gamepads.add(g);
        return g;
    }

    /*
    public static void init() {
        gamepads.clear();
        ControllerEnvironment environment = createDefaultEnvironment();
        Controller[] controllers = environment.getControllers();
        for (Controller c : controllers) {
            //Utils.log(c.getName() + ": " + c.getType().toString());
            if (c.getType() == Controller.Type.GAMEPAD || c.getType() == Controller.Type.STICK) {
                gamepads.add(new Gamepad(c));
                Utils.log("Detected Gamepad: " + c.getName());
            }
        }
        if (gamepads.isEmpty()) {
            Utils.log("No controllers!");
        }
        update = new Timer(25, e -> {
            for (Gamepad g : gamepads) {
                g.poll();
            }
        });
        update.start();
    }
    */

    public static List<Gamepad> getGamepads() {
        return new ArrayList<>(gamepads);
    }

    public static Gamepad getGamepad(int id) {
        //Gamepads are added in order of ID, so this SHOULD be fine
        return gamepads.get(id);
    }

    public static Gamepad getGamepad(Controller c) {
        for (Gamepad g : gamepads) {
            if (g.c.equals(c)) {
                return g;
            }
        }
        //TODO do we make a new gamepad and return it?
        return null;
    }

    /*
    private static ControllerEnvironment createDefaultEnvironment()  {
        try {
            //noinspection unchecked
            Constructor<ControllerEnvironment> constructor = (Constructor<ControllerEnvironment>)
                    Class.forName("net.java.games.input.DefaultControllerEnvironment").getDeclaredConstructors()[0];

            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/
}
