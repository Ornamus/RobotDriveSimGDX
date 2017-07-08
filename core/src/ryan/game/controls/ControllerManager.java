package ryan.game.controls;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import ryan.game.Utils;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ControllerManager {

    private static List<Gamepad> gamepads = new ArrayList<>();
    private static Timer update;

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

    public static List<Gamepad> getGamepads() {
        return new ArrayList<>(gamepads);
    }

    public static Gamepad getGamepad(int id) {
        //Gamepads are added in order of ID, so this SHOULD be fine
        return gamepads.get(id);
    }

    private static ControllerEnvironment createDefaultEnvironment()  {
        try {
            Constructor<ControllerEnvironment> constructor = (Constructor<ControllerEnvironment>)
                    Class.forName("net.java.games.input.DefaultControllerEnvironment").getDeclaredConstructors()[0];

            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
