package ryan.game.controls;

import com.badlogic.gdx.controllers.Controller;
import ryan.game.Utils;

public class Gamepad {

    public static final int ONE = 0;
    public static final int TWO = 1;
    public static final int THREE = 2;
    public static final int FOUR = 3;

    public static final int BUMPER_LEFT = 4, BUMPER_RIGHT = 5;

    public static final int START = 6, SELECT = 7;

    public static final int JOY_LEFT = 8, JOY_RIGHT = 9;

    public static final int TRIGGER_LEFT = 10, TRIGGER_RIGHT = 11;



    private static final int[] mapOrder = {ONE, TWO, THREE, FOUR, BUMPER_LEFT, BUMPER_RIGHT, START, SELECT, JOY_LEFT, JOY_RIGHT,
    TRIGGER_LEFT, TRIGGER_RIGHT, 94, 95, 96, 97, 98, 99};


    public final int id;
    protected Controller c;

    private GamepadConfig config = null;

    private boolean reverseSticks = false;

    protected boolean mapping = false;
    protected int mapIndex = 0;
    protected int currentKey = -1;

    private static int gamepads = 0;

    protected Gamepad(Controller c) {
        this.c = c;

        config = Utils.fromJSON("core/assets/controller_configs/" + getSimpleName() + ".json", GamepadConfig.class);
        if (config == null) {
            mapping = true;
            config = new GamepadConfig();
            currentKey = mapOrder[0];
            Utils.log("Starting controller mapping");
        }

        id = gamepads++;
    }

    public void updateMap(int index) {
        Utils.log("Mapping " + currentKey + " to " + index);
        config.setMapping(currentKey, index);
        mapIndex++;
        if (mapIndex < mapOrder.length) {
            currentKey = mapOrder[mapIndex];
        } else {
            mapping = false;
            config.save(getSimpleName());
            Utils.log("Mapping complete!");
        }
    }

    /*
    public boolean poll() {
        return c.poll();
    }*/

    public float getX() {
        if (hasSecondJoystick() && reverseSticks) return c.getAxis(config.xAxis2);
        return c.getAxis(config.xAxis);
    }

    public float getY() {
        if (hasSecondJoystick() && reverseSticks) return -c.getAxis(config.yAxis2);
        return -c.getAxis(config.yAxis);
    }

    public float getX2() {
        if (config.xAxis2 != -1) {
            if (reverseSticks) return c.getAxis(config.xAxis);
            return c.getAxis(config.xAxis2);
        }
        return 0;
    }

    public float getY2() {
        if (config.yAxis2 != -1) {
            if (reverseSticks) return -c.getAxis(config.yAxis);
            return -c.getAxis(config.yAxis2);
        }
        return 0;
    }

    public float getZ() {
        if (config.zAxis != -1) c.getAxis(config.zAxis);
        return 0;
    }

    public float getDPad() {
        //Utils.log("Gamepad.getDPad() not implemented yet");
        return 0;
    }

    public boolean isLeftTriggerPressed() {
        if (hasZAxis()) return getZ() > 0.1;
        else return getButton(TRIGGER_LEFT);
    }

    public boolean isRightTriggerPressed() {
        if (hasZAxis()) return getZ() < -0.1;
        else return getButton(TRIGGER_RIGHT);
    }

    public boolean hasZAxis() {
        return config.zAxis != -1;
    }

    public boolean hasSecondJoystick() {
        return config.xAxis2 != -1 && config.yAxis2 != -1;
    }

    public boolean isSticksReversed() {
        return reverseSticks;
    }

    public GamepadConfig getConfig() {
        return config;
    }

    public void setReverseSticks(boolean rev) {
        if (hasSecondJoystick()) reverseSticks = rev;
    }

    public boolean getButton(int id) {
        if (id > -1) return c.getButton(config.getMapping(id));
        return false;
    }

    public String getName() {
        return c.getName();
    }

    public String getSimpleName() {
        String s = getName();
        s = s.replaceAll("[^A-Za-z0-9]", "");
        return s;
    }
}
