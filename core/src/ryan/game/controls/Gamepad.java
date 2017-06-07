package ryan.game.controls;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import ryan.game.Utils;

import java.util.ArrayList;
import java.util.List;

public class Gamepad {

    private int id;
    private Controller c;
    private Component xAxis = null, yAxis = null, xAxis2 = null, yAxis2 = null;
    private List<Button> buttons = new ArrayList<Button>();

    private static int gamepads = 0;

    protected Gamepad(Controller c) {
        this.c = c;
        if (c.getType() == Controller.Type.KEYBOARD) {
            for (Component comp : c.getComponents()) {
                //TODO
            }
        } else {
            boolean zRot = false;
            for (Component comp : c.getComponents()) {
                //Utils.log(comp.getName());
                if (comp.getName().equalsIgnoreCase("X Axis")) xAxis = comp;
                if (comp.getName().equalsIgnoreCase("Y Axis")) yAxis = comp;
                if (comp.getName().equalsIgnoreCase("X Rotation")) xAxis2 = comp;
                if (comp.getName().equalsIgnoreCase("Y Rotation")) yAxis2 = comp;
                if (comp.getName().equalsIgnoreCase("Z Rotation")) zRot = true;
                if (comp.getName().toLowerCase().contains("button")) buttons.add(new Button(comp));

            }
            if (!hasSecondJoystick() && zRot) {
                for (Component comp : c.getComponents()) {
                    //Utils.log(comp.getName());
                    if (comp.getName().equalsIgnoreCase("Z Axis")) xAxis2 = comp;
                    if (comp.getName().equalsIgnoreCase("Z Rotation")) yAxis2 = comp;
                }
            }
        }
        id = gamepads++;
    }

    public boolean poll() {
        return c.poll();
    }

    public float getX() {
        return xAxis.getPollData();
    }

    public float getY() {
        return -yAxis.getPollData();
    }

    public float getX2() {
        if (xAxis2 != null) return xAxis2.getPollData();
        return 0;
    }

    public float getY2() {
        if (yAxis2 != null) return -yAxis2.getPollData();
        return 0;
    }

    public boolean hasSecondJoystick() {
        return xAxis2 != null && yAxis2 != null;
    }

    public List<Button> getButtons() {
        return new ArrayList<Button>(buttons);
    }

    public Button getButton(int id) {
        return buttons.get(id);
    }

    public String getName() {
        return c.getName();
    }
}
