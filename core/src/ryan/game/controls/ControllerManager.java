package ryan.game.controls;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import ryan.game.Utils;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ControllerManager {

    private static List<Gamepad> gamepads = new ArrayList<Gamepad>();
    private static Timer update;

    public static void init() {
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        for (Controller c : controllers) {
            //Utils.log(c.getName() + ": " + c.getType().toString());
            if (c.getType() == Controller.Type.GAMEPAD || c.getType() == Controller.Type.STICK) {
                gamepads.add(new Gamepad(c));
                Utils.log("Detected Gamepad: " + c.getName());
            }
        }
        if (gamepads.isEmpty()) {
            Utils.log("No controllers!");
            //TODO: Keyboard support
            /*
            Utils.log("No controllers detected, rolling back to keyboard control (not suggested)");
            for (Controller c : controllers) {
                if (c.getName().toLowerCase().contains("keyboard")) {
                    gamepads.add(new Gamepad(c));
                    Utils.log("Detected Keyboard Controller: " + c.getName());
                }
            }
            */
        }
        //update = new Timer(25, e -> gamepads.forEach(Gamepad::poll));
        update = new Timer(25, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Gamepad g : gamepads) {
                    g.poll();
                }
            }
        });
        update.start();
    }

    public static List<Gamepad> getGamepads() {
        return new ArrayList<Gamepad>(gamepads);
    }

    public static Gamepad getGamepad(int id) {
        //Gamepads are added in order of ID, so this SHOULD be fine
        return gamepads.get(id);
    }
}
