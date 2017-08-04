package ryan.game.controls;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.lwjgl3.Lwjgl3ControllerManager;
import ryan.game.entity.Robot;
import ryan.game.games.Game;

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

    public static List<Gamepad> getGamepads() {
        return new ArrayList<>(gamepads);
    }

    public static Gamepad getGamepad(Robot r) {
        for (Gamepad g : gamepads) {
            if (g.r == r) {
                return g;
            }
        }
        return null;
    }

    public static List<Gamepad> getGamepads(Robot r) {
        List<Gamepad> claimed = new ArrayList<>();
        for (Gamepad g : gamepads) {
            if (g.r == r) {
                claimed.add(g);
            }
        }
        return claimed;
    }

    public static Gamepad getGamepad(int id) {
        //Gamepads are added in order of ID, so this SHOULD be fine
        return gamepads.get(id);
    }

    public static Gamepad getGamepad(Controller c) {
        for (Gamepad g : gamepads) {
            if (g.c != null && g.c.equals(c)) {
                return g;
            }
        }
        //TODO do we make a new gamepad and return it?
        return null;
    }
}
