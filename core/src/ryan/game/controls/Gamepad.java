package ryan.game.controls;

import net.java.games.input.Component;
import net.java.games.input.Controller;

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
        if (c.getName().contains("keyboard")) {
            for (Component comp : c.getComponents()) {
                //TODO
            }
        } else {
            for (Component comp : c.getComponents()) {
                if (comp.getName().equalsIgnoreCase("X Axis")) xAxis = comp;
                if (comp.getName().equalsIgnoreCase("Y Axis")) yAxis = comp;
                if (comp.getName().equalsIgnoreCase("X Rotation")) xAxis2 = comp;
                if (comp.getName().equalsIgnoreCase("Y Rotation")) yAxis2 = comp;
                if (comp.getName().toLowerCase().contains("button")) buttons.add(new Button(comp));
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
        return xAxis2.getPollData();
    }

    public float getY2() {
        return -yAxis2.getPollData();
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
